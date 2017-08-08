/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.tools.sdkcontroller.lib;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.net.LocalServerSocket;
import android.net.LocalSocket;

import com.android.tools.sdkcontroller.lib.Channel;
import com.android.tools.sdkcontroller.service.ControllerService;

/**
 * Encapsulates a connection between SdkController service and the emulator. On
 * the device side, the connection is bound to the UNIX-domain socket named
 * 'android.sdk.controller'. On the emulator side the connection is established
 * via TCP port that is used to forward I/O traffic on the host machine to
 * 'android.sdk.controller' socket on the device. Typically, the port forwarding
 * can be enabled using adb command:
 * <p/>
 * 'adb forward tcp:<TCP port number> localabstract:android.sdk.controller'
 * <p/>
 * The way communication between the emulator and SDK controller service works
 * is as follows:
 * <p/>
 * 1. Both sides, emulator and the service have components that implement a particular
 * type of emulation. For instance, AndroidSensorsPort in the emulator, and
 * SensorChannel in the application implement sensors emulation.
 * Emulation channels are identified by unique names. For instance, sensor emulation
 * is done via "sensors" channel, multi-touch emulation is done via "multi-touch"
 * channel, etc.
 * <p/>
 * 2. Channels are connected to emulator via separate socket instance (though all
 * of the connections share the same socket address).
 * <p/>
 * 3. Connection is initiated by the emulator side, while the service provides
 * its side (a channel) that implement functionality and exchange protocol required
 * by the requested type of emulation.
 * <p/>
 * Given that, the main responsibilities of this class are:
 * <p/>
 * 1. Bind to "android.sdk.controller" socket, listening to emulator connections.
 * <p/>
 * 2. Maintain a list of service-side channels registered by the application.
 * <p/>
 * 3. Bind emulator connection with service-side channel via port name, provided by
 * the emulator.
 * <p/>
 * 4. Monitor connection state with the emulator, and automatically restore the
 * connection once it is lost.
 */
public class Connection {
    /** UNIX-domain name reserved for SDK controller. */
    public static final String SDK_CONTROLLER_PORT = "android.sdk.controller";
    /** Tag for logging messages. */
    private static final String TAG = "SdkControllerConnection";
    /** Controls debug logging */
    private static final boolean DEBUG = false;

    /** Server socket used to listen to emulator connections. */
    private LocalServerSocket mServerSocket = null;
    /** Service that has created this object. */
    private ControllerService mService;
    /**
     * List of connected emulator sockets, pending for a channel to be registered.
     * <p/>
     * Emulator may connect to SDK controller before the app registers a channel
     * for that connection. In this case (when app-side channel is not registered
     * with this class) we will keep emulator connection in this list, pending
     * for the app-side channel to register.
     */
    private List<Socket> mPendingSockets = new ArrayList<Socket>();
    /**
     * List of registered app-side channels.
     * <p/>
     * Channels that are kept in this list may be disconnected from (or pending
     * connection with) the emulator, or they may be connected with the
     * emulator.
     */
    private List<Channel> mChannels = new ArrayList<Channel>();

    /**
     * Constructs Connection instance.
     */
    public Connection(ControllerService service) {
        mService = service;
        if (DEBUG) Log.d(TAG, "SdkControllerConnection is constructed.");
    }

    /**
     * Binds to the socket, and starts the listening thread.
     */
    public void connect() {
        if (DEBUG) Log.d(TAG, "SdkControllerConnection is connecting...");
        // Start connection listener.
        new Thread(new Runnable() {
                @Override
            public void run() {
                runIOLooper();
            }
        }, "SdkControllerConnectionIoLoop").start();
    }

    /**
     * Stops the listener, and closes the socket.
     *
     * @return true if connection has been stopped in this call, or false if it
     *         has been already stopped when this method has been called.
     */
    public boolean disconnect() {
        // This is the only place in this class where we will null the
        // socket object. Since this method can be called concurrently from
        // different threads, lets do this under the lock.
        LocalServerSocket socket;
        synchronized (this) {
            socket = mServerSocket;
            mServerSocket = null;
        }
        if (socket != null) {
            if (DEBUG) Log.d(TAG, "SdkControllerConnection is stopping I/O looper...");
            // Stop accepting new connections.
            wakeIOLooper(socket);
            try {
                socket.close();
            } catch (Exception e) {
            }

            // Close all the pending sockets, and clear pending socket list.
            if (DEBUG) Log.d(TAG, "SdkControllerConnection is closing pending sockets...");
            for (Socket pending_socket : mPendingSockets) {
                pending_socket.close();
            }
            mPendingSockets.clear();

            // Disconnect all the emualtors.
            if (DEBUG) Log.d(TAG, "SdkControllerConnection is disconnecting channels...");
            for (Channel channel : mChannels) {
                if (channel.disconnect()) {
                    channel.onEmulatorDisconnected();
                }
            }
            if (DEBUG) Log.d(TAG, "SdkControllerConnection is disconnected.");
        }
        return socket != null;
    }

    /**
     * Registers SDK controller channel.
     *
     * @param channel SDK controller emulator to register.
     * @return true if channel has been registered successfully, or false if channel
     *         with the same name is already registered.
     */
    public boolean registerChannel(Channel channel) {
        for (Channel check_channel : mChannels) {
            if (check_channel.getChannelName().equals(channel.getChannelName())) {
                Loge("Registering a duplicate Channel " + channel.getChannelName());
                return false;
            }
        }
        if (DEBUG) Log.d(TAG, "Registering Channel " + channel.getChannelName());
        mChannels.add(channel);

        // Lets see if there is a pending socket for this channel.
        for (Socket pending_socket : mPendingSockets) {
            if (pending_socket.getChannelName().equals(channel.getChannelName())) {
                // Remove the socket from the pending list, and connect the registered channel with it.
                if (DEBUG) Log.d(TAG, "Found pending Socket for registering Channel "
                        + channel.getChannelName());
                mPendingSockets.remove(pending_socket);
                channel.connect(pending_socket);
            }
        }
        return true;
    }

    /**
     * Checks if at least one socket connection exists with channel.
     *
     * @return true if at least one socket connection exists with channel.
     */
    public boolean isEmulatorConnected() {
        for (Channel channel : mChannels) {
            if (channel.isConnected()) {
                return true;
            }
        }
        return !mPendingSockets.isEmpty();
    }

    /**
     * Gets Channel instance for the given channel name.
     *
     * @param name Channel name to get Channel instance for.
     * @return Channel instance for the given channel name, or NULL if no
     *         channel has been registered for that name.
     */
    public Channel getChannel(String name) {
        for (Channel channel : mChannels) {
            if (channel.getChannelName().equals(name)) {
                return channel;
            }
        }
        return null;
    }

    /**
     * Gets connected emulator socket that is pending for service-side channel
     * registration.
     *
     * @param name Channel name to lookup Socket for.
     * @return Connected emulator socket that is pending for service-side channel
     *         registration, or null if no socket is pending for service-size
     *         channel registration.
     */
    private Socket getPendingSocket(String name) {
        for (Socket socket : mPendingSockets) {
            if (socket.getChannelName().equals(name)) {
                return socket;
            }
        }
        return null;
    }

    /**
     * Wakes I/O looper waiting on connection with the emulator.
     *
     * @param socket Server socket waiting on connection.
     */
    private void wakeIOLooper(LocalServerSocket socket) {
        // We wake the looper by connecting to the socket.
        LocalSocket waker = new LocalSocket();
        try {
            waker.connect(socket.getLocalSocketAddress());
        } catch (IOException e) {
            Loge("Exception " + e + " in SdkControllerConnection while waking up the I/O looper.");
        }
    }

    /**
     * Loops on the local socket, handling emulator connection attempts.
     */
    private void runIOLooper() {
        if (DEBUG) Log.d(TAG, "In SdkControllerConnection I/O looper.");
        do {
            try {
                // Create non-blocking server socket that would listen for connections,
                // and bind it to the given port on the local host.
                mServerSocket = new LocalServerSocket(SDK_CONTROLLER_PORT);
                LocalServerSocket socket = mServerSocket;
                while (socket != null) {
                    final LocalSocket sk = socket.accept();
                    if (mServerSocket != null) {
                        onAccept(sk);
                    } else {
                        break;
                    }
                    socket = mServerSocket;
                }
            } catch (IOException e) {
                Loge("Exception " + e + "SdkControllerConnection I/O looper.");
            }
            if (DEBUG) Log.d(TAG, "Exiting SdkControllerConnection I/O looper.");

          // If we're exiting the internal loop for reasons other than an explicit
          // disconnect request, we should reconnect again.
        } while (disconnect());
    }

    /**
     * Accepts new connection from the emulator.
     *
     * @param sock Connecting socket.
     * @throws IOException
     */
    private void onAccept(LocalSocket sock) throws IOException {
        final ByteBuffer handshake = ByteBuffer.allocate(ProtocolConstants.QUERY_HEADER_SIZE);

        // By protocol, first byte received from newly connected emulator socket
        // indicates host endianness.
        Socket.receive(sock, handshake.array(), 1);
        final ByteOrder endian = (handshake.getChar() == 0) ? ByteOrder.LITTLE_ENDIAN :
                ByteOrder.BIG_ENDIAN;
        handshake.order(endian);

        // Right after that follows the handshake query header.
        handshake.position(0);
        Socket.receive(sock, handshake.array(), handshake.array().length);

        // First int - signature
        final int signature = handshake.getInt();
        assert signature == ProtocolConstants.PACKET_SIGNATURE;
        // Second int - total query size (including fixed query header)
        final int remains = handshake.getInt() - ProtocolConstants.QUERY_HEADER_SIZE;
        // After that - header type (which must be SDKCTL_PACKET_TYPE_QUERY)
        final int msg_type = handshake.getInt();
        assert msg_type == ProtocolConstants.PACKET_TYPE_QUERY;
        // After that - query ID.
        final int query_id = handshake.getInt();
        // And finally, query type (which must be ProtocolConstants.QUERY_HANDSHAKE for
        // handshake query)
        final int query_type = handshake.getInt();
        assert query_type == ProtocolConstants.QUERY_HANDSHAKE;
        // Verify that received is a query.
        if (msg_type != ProtocolConstants.PACKET_TYPE_QUERY) {
            // Message type is not a query. Lets read and discard the remainder
            // of the message.
            if (remains > 0) {
                Loge("Unexpected handshake message type: " + msg_type);
                byte[] discard = new byte[remains];
                Socket.receive(sock, discard, discard.length);
            }
            return;
        }

        // Receive query data.
        final byte[] name_array = new byte[remains];
        Socket.receive(sock, name_array, name_array.length);

        // Prepare response header.
        handshake.position(0);
        handshake.putInt(ProtocolConstants.PACKET_SIGNATURE);
        // Handshake reply is just one int.
        handshake.putInt(ProtocolConstants.QUERY_RESP_HEADER_SIZE + 4);
        handshake.putInt(ProtocolConstants.PACKET_TYPE_QUERY_RESPONSE);
        handshake.putInt(query_id);

        // Verify that received query is in deed a handshake query.
        if (query_type != ProtocolConstants.QUERY_HANDSHAKE) {
            // Query is not a handshake. Reply with failure.
            Loge("Unexpected handshake query type: " + query_type);
            handshake.putInt(ProtocolConstants.HANDSHAKE_RESP_QUERY_UNKNOWN);
            sock.getOutputStream().write(handshake.array());
            return;
        }

        // Handshake query data consist of SDK controller channel name.
        final String channel_name = new String(name_array);
        if (DEBUG) Log.d(TAG, "Handshake received for channel " + channel_name);

        // Respond to query depending on service-side channel availability
        final Channel channel = getChannel(channel_name);
        Socket sk = null;

        if (channel != null) {
            if (channel.isConnected()) {
                // This is a duplicate connection.
                Loge("Duplicate connection to a connected Channel " + channel_name);
                handshake.putInt(ProtocolConstants.HANDSHAKE_RESP_DUP);
            } else {
                // Connecting to a registered channel.
                if (DEBUG) Log.d(TAG, "Emulator is connected to a registered Channel " + channel_name);
                handshake.putInt(ProtocolConstants.HANDSHAKE_RESP_CONNECTED);
            }
        } else {
            // Make sure that there are no other channel connections for this
            // channel name.
            if (getPendingSocket(channel_name) != null) {
                // This is a duplicate.
                Loge("Duplicate connection to a pending Socket " + channel_name);
                handshake.putInt(ProtocolConstants.HANDSHAKE_RESP_DUP);
            } else {
                // Connecting to a channel that has not been registered yet.
                if (DEBUG) Log.d(TAG, "Emulator is connected to a pending Socket " + channel_name);
                handshake.putInt(ProtocolConstants.HANDSHAKE_RESP_NOPORT);
                sk = new Socket(sock, channel_name, endian);
                mPendingSockets.add(sk);
            }
        }

        // Send handshake reply.
        sock.getOutputStream().write(handshake.array());

        // If a disconnected channel for emulator connection has been found,
        // connect it.
        if (channel != null && !channel.isConnected()) {
            if (DEBUG) Log.d(TAG, "Connecting Channel " + channel_name + " with emulator.");
            sk = new Socket(sock, channel_name, endian);
            channel.connect(sk);
        }

        mService.notifyStatusChanged();
    }

    /***************************************************************************
     * Logging wrappers
     **************************************************************************/

    private void Loge(String log) {
        mService.addError(log);
        Log.e(TAG, log);
    }
}
