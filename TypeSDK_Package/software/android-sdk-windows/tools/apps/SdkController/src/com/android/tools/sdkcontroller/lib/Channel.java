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

import android.os.Message;
import android.util.Log;

import com.android.tools.sdkcontroller.service.ControllerService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Encapsulates basics of a connection with the emulator.
 * This class must be used as a base class for all the channelss that provide
 * particular type of emulation (such as sensors, multi-touch, etc.)
 * <p/>
 * Essentially, Channel is an implementation of a particular emulated functionality,
 * that defines logical format of the data transferred between the emulator and
 * SDK controller. For instance, "sensors" is a channel that emulates sensors,
 * and transfers sensor value changes from the device to the emulator. "Multi-touch"
 * is a channel that supports multi-touch emulation, and transfers multi-touch
 * events to the emulator, while receiving frame buffer updates from the emulator.
 * <p/>
 * Besides connection with the emulator, each channel may contain one or more UI
 * components associated with it. This class provides some basics for UI support,
 * including:
 * <p/>
 * - Providing a way to register / unregister a UI component with the channel.
 * <p/>
 * - Implementing posting of messages to emulator in opposite to direct message
 * sent. This is due to requirement that UI threads are prohibited from doing
 * network I/O.
 */
public abstract class Channel {

    /**
     * Encapsulates a message posted to be sent to the emulator from a worker
     * thread. This class is used to describe a message that is posted in UI
     * thread, and then picked up in the worker thread.
     */
    private class SdkControllerMessage {
        /** Message type. */
        private int mMessageType;
        /** Message data (can be null). */
        private byte[] mMessage;
        /** Message data size */
        private int mMessageSize;

        /**
         * Construct message from an array.
         *
         * @param type Message type.
         * @param message Message data. Message data size is defined by size of
         *            the array.
         */
        public SdkControllerMessage(int type, byte[] message) {
            mMessageType = type;
            mMessage = message;
            mMessageSize = (message != null) ? message.length : 0;
        }

        /**
         * Construct message from a ByteBuffer.
         *
         * @param type Message type.
         * @param message Message data. Message data size is defined by
         *            position() property of the ByteBuffer.
         */
        public SdkControllerMessage(int type, ByteBuffer message) {
            mMessageType = type;
            if (message != null) {
                mMessage = message.array();
                mMessageSize = message.position();
            } else {
                mMessage = null;
                mMessageSize = 0;
            }
        }

        /**
         * Gets message type.

         *
         * @return Message type.
         */
        public int getMessageType() {
            return mMessageType;
        }

        /**
         * Gets message buffer.
         *
         * @return Message buffer.
         */
        public byte[] getMessage() {
            return mMessage;
        }

        /**
         * Gets message buffer size.
         *
         * @return Message buffer size.
         */
        public int getMessageSize() {
            return mMessageSize;
        }
    } // SdkControllerMessage

    /*
     * Names for currently implemented SDK controller channels.
     */

    /** Name for a channel that handles sensors emulation */
    public static final String SENSOR_CHANNEL = "sensors";
    /** Name for a channel that handles multi-touch emulation */
    public static final String MULTITOUCH_CHANNEL = "multi-touch";

    /*
     * Types of messages internally used by Channel class.
     */

    /** Service-side emulator is connected. */
    private static final int MSG_CONNECTED = -1;
    /** Service-side emulator is disconnected. */
    private static final int MSG_DISCONNECTED = -2;
    /** Service-side emulator is enabled. */
    private static final int MSG_ENABLED = -3;
    /** Service-side emulator is disabled. */
    private static final int MSG_DISABLED = -4;

    /** Tag for logging messages. */
    private static final String TAG = "SdkControllerChannel";
    /** Controls debug log. */
    private static final boolean DEBUG = false;

    /** Service that has created this object. */
    protected ControllerService mService;

    /*
     * Socket stuff.
     */

    /** Socket to use to to communicate with the emulator. */
    private Socket mSocket = null;
    /** Channel name ("sensors", "multi-touch", etc.) */
    private String mChannelName;
    /** Endianness of data transferred in this channel. */
    private ByteOrder mEndian;

    /*
     * Message posting support.
     */

    /** Total number of messages posted in this channel */
    private final AtomicInteger mMsgCount = new AtomicInteger(0);
    /** Flags whether or not message thread is running. */
    private volatile boolean mRunMsgQueue = true;
    /** Queue of messages pending transmission. */
    private final BlockingQueue<SdkControllerMessage>
            mMsgQueue = new LinkedBlockingQueue<SdkControllerMessage>();
    /** Message thread */
    private final Thread mMsgThread;

    /*
     * UI support.
     */

    /** Lists UI handlers attached to this channel. */
    private final List<android.os.Handler> mUiHandlers = new ArrayList<android.os.Handler>();

    /*
     * Abstract methods.
     */

    /**
     * This method is invoked when this channel is fully connected with its
     * counterpart in the emulator.
     */
    public abstract void onEmulatorConnected();

    /**
     * This method is invoked when this channel loses connection with its
     * counterpart in the emulator.
     */
    public abstract void onEmulatorDisconnected();

    /**
     * A message has been received from the emulator.
     *
     * @param msg_type Message type.
     * @param msg_data Message data. Message data size is defined by the length
     *            of the array wrapped by the ByteBuffer.
     */
    public abstract void onEmulatorMessage(int msg_type, ByteBuffer msg_data);

    /**
     * A query has been received from the emulator.
     *
     * @param query_id Identifies the query. This ID must be used when replying
     *            to the query.
     * @param query_type Query type.
     * @param query_data Query data. Query data size is defined by the length of
     *            the array wrapped by the ByteBuffer.
     */
    public abstract void onEmulatorQuery(int query_id, int query_type, ByteBuffer query_data);

    /*
     * Channel implementation.
     */

    /**
     * Constructs Channel instance.
     *
     * @param name Channel name.
     */
    public Channel(ControllerService service, String name) {
        mService = service;
        mChannelName = name;
        // Start the worker thread for posted messages.
        mMsgThread = new Thread(new Runnable() {
                @Override
            public void run() {
                if (DEBUG) Log.d(TAG, "MsgThread.started-" + mChannelName);
                while (mRunMsgQueue) {
                    try {
                        SdkControllerMessage msg = mMsgQueue.take();
                        if (msg != null) {
                            sendMessage(
                                    msg.getMessageType(), msg.getMessage(), msg.getMessageSize());
                            mMsgCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, "MsgThread-" + mChannelName, e);
                    }
                }
                if (DEBUG) Log.d(TAG, "MsgThread.terminate-" + mChannelName);
            }
        }, "MsgThread-" + name);
        mMsgThread.start();
        if (DEBUG) Log.d(TAG, "Channel is constructed for " + mChannelName);
    }

    /**
     * Gets name for this channel.
     *
     * @return Emulator name.
     */
    public String getChannelName() {
        return mChannelName;
    }

    /**
     * Gets endianness for this channel.
     *
     * @return Channel endianness.
     */
    public ByteOrder getEndian() {
        return mEndian;
    }

    /**
     * Gets number of messages sent via postMessage method.
     *
     * @return Number of messages sent via postMessage method.
     */
    public int getMsgSentCount() {
        return mMsgCount.get();
    }

    /**
     * Checks if this channel is connected with the emulator.
     *
     * @return true if this channel is connected with the emulator, or false if it is
     *         not connected.
     */
    public boolean isConnected() {
        // Use local copy of the socket, ensuring it's not going to NULL while
        // we're working with it. If it gets closed, while we're in the middle
        // of data transfer - it's OK, since it will produce an exception, and
        // the caller will gracefully handle it.
        //
        // Same technique is used everywhere in this class where mSocket member
        // is touched.
        Socket socket = mSocket;
        return socket != null && socket.isConnected();
    }

    /**
     * Establishes connection with the emulator. This method is called by Connection
     * object when emulator successfully connects to this channel, or this channel
     * gets registered, and there is a pending socket connection for it.
     *
     * @param socket Channel connection socket.
     */
    public void connect(Socket socket) {
        mSocket = socket;
        mEndian = socket.getEndian();
        Logv("Channel " + mChannelName + " is now connected with the emulator.");
        // Notify the emulator that connection is established.
        sendMessage(MSG_CONNECTED, (byte[]) null);

        // Let the derived class know that emulator is connected, and start the
        // I/O loop in which we will receive data from the emulator. Note that
        // we start the loop after onEmulatorConnected call, since we don't want
        // to start dispatching messages before the derived class could set
        // itself up for receiving them.
        onEmulatorConnected();
        new Thread(new Runnable() {
                @Override
            public void run() {
                runIOLooper();
            }
        }, "ChannelIoLoop").start();
        mService.notifyStatusChanged();
    }

    /**
     * Disconnects this channel from the emulator.
     *
     * @return true if this channel has been disconnected in this call, or false if
     *         channel has been already disconnected when this method has been called.
     */
    public boolean disconnect() {
        // This is the only place in this class where we will null the
        // socket object. Since this method can be called concurrently from
        // different threads, lets do this under the lock.
        Socket socket;
        synchronized (this) {
            socket = mSocket;
            mSocket = null;
        }
        if (socket != null) {
            // Notify the emulator about channel disconnection before we close
            // the communication socket.
            try {
                sendMessage(socket, MSG_DISCONNECTED, null, 0);
            } catch (IOException e) {
                // Ignore I/O exception at this point. We don't care about
                // it, since the socket is being closed anyways.
            }
            // This will eventually stop I/O looper thread.
            socket.close();
            mService.notifyStatusChanged();
        }
        return socket != null;
    }

    /**
     * Enables the emulation. Typically, this method is called for channels that are
     * dependent on UI to handle the emulation. For instance, multi-touch emulation is
     * disabled until at least one UI component is attached to the channel. So, for
     * multi-touch emulation this method is called when UI gets attached to the channel.
     */
    public void enable() {
        postMessage(MSG_ENABLED, (byte[]) null);
        mService.notifyStatusChanged();
    }

    /**
     * Disables the emulation. Just the opposite to enable(). For multi-touch this
     * method is called when UI detaches from the channel.
     */
    public void disable() {
        postMessage(MSG_DISABLED, (byte[]) null);
        mService.notifyStatusChanged();
    }

    /**
     * Sends message to the emulator.
     *
     * @param socket Socket to send the message to.
     * @param msg_type Message type.
     * @param msg Message data to send.
     * @param len Byte size of message data.
     * @throws IOException
     */
    private void sendMessage(Socket socket, int msg_type, byte[] msg, int len)
            throws IOException {
        // In async environment we must have message header and message data in
        // one block to prevent messages from other threads getting between the
        // header and the data. So, we can't sent header, and then the data. We
        // must combine them in one data block instead.
        ByteBuffer bb = ByteBuffer.allocate(ProtocolConstants.MESSAGE_HEADER_SIZE + len);
        bb.order(mEndian);

        // Initialize message header.
        bb.putInt(ProtocolConstants.PACKET_SIGNATURE);
        bb.putInt(ProtocolConstants.MESSAGE_HEADER_SIZE + len);
        bb.putInt(ProtocolConstants.PACKET_TYPE_MESSAGE);
        bb.putInt(msg_type);

        // Save message data (if there is any).
        if (len != 0) {
            bb.put(msg, 0, len);
        }

        socket.send(bb.array());
    }

    /**
     * Sends message to the emulator.
     *
     * @param msg_type Message type.
     * @param msg Message data to send. Message size is defined by the size of
     *            the array.
     * @return true on success, or false if data transmission has failed.
     */
    public boolean sendMessage(int msg_type, byte[] msg, int msg_len) {
        try {
            Socket socket = mSocket;
            if (socket != null) {
                sendMessage(socket, msg_type, msg, msg_len);
                return true;
            } else {
                Logw("sendMessage is called on disconnected Channel " + mChannelName);
            }
        } catch (IOException e) {
            Loge("Exception " + e + " in sendMessage for Channel " + mChannelName);
            onIoFailure();
        }
        return false;
    }

    /**
     * Sends message to the emulator.
     *
     * @param msg_type Message type.
     * @param msg Message data to send. Message size is defined by the size of
     *            the array.
     * @return true on success, or false if data transmission has failed.
     */
    public boolean sendMessage(int msg_type, byte[] msg) {
        try {
            Socket socket = mSocket;
            if (socket != null) {
                if (msg != null) {
                    sendMessage(socket, msg_type, msg, msg.length);
                } else {
                    sendMessage(socket, msg_type, null, 0);
                }
                return true;
            } else {
                Logw("sendMessage is called on disconnected Channel " + mChannelName);
            }
        } catch (IOException e) {
            Loge("Exception " + e + " in sendMessage for Channel " + mChannelName);
            onIoFailure();
        }
        return false;
    }

    /**
     * Sends message to the emulator.
     *
     * @param msg_type Message type.
     * @param msg Message data to send. Message size is defined by the
     *            position() property of the ByteBuffer.
     * @return true on success, or false if data transmission has failed.
     */
    public boolean sendMessage(int msg_type, ByteBuffer msg) {
        try {
            Socket socket = mSocket;
            if (socket != null) {
                if (msg != null) {
                    sendMessage(socket, msg_type, msg.array(), msg.position());
                } else {
                    sendMessage(socket, msg_type, null, 0);
                }
                return true;
            } else {
                Logw("sendMessage is called on disconnected Channel " + mChannelName);
            }
        } catch (IOException e) {
            Loge("Exception " + e + " in sendMessage for Channel " + mChannelName);
            onIoFailure();
        }
        return false;
    }

    /**
     * Posts message to the emulator.
     *
     * @param msg_type Message type.
     * @param msg Message data to post. Message size is defined by the size of
     *            the array.
     */
    public void postMessage(int msg_type, byte[] msg) {
        try {
            mMsgQueue.put(new SdkControllerMessage(msg_type, msg));
        } catch (InterruptedException e) {
            Log.e(TAG, "mMessageQueue.put", e);
        }
    }

    /**
     * Posts message to the emulator.
     *
     * @param msg_type Message type.
     * @param msg Message data to post. Message size is defined by the
     *            position() property of the ByteBuffer.
     */
    public void postMessage(int msg_type, ByteBuffer msg) {
        try {
            mMsgQueue.put(new SdkControllerMessage(msg_type, msg));
        } catch (InterruptedException e) {
            Log.e(TAG, "mMessageQueue.put", e);
        }
    }

    /**
     * Sends query response to the emulator.
     *
     * @param query_id Query identifier.
     * @param qresp Response to the query.
     * @param len Byte size of query response data.
     * @return true on success, or false if data transmission has failed.
     */
    public boolean sendQueryResponse(int query_id, byte[] qresp, int len) {
        // Just like with messages, we must combine header and data in a single
        // transmitting block.
        ByteBuffer bb = ByteBuffer.allocate(ProtocolConstants.QUERY_RESP_HEADER_SIZE + len);
        bb.order(mEndian);

        // Initialize response header.
        bb.putInt(ProtocolConstants.PACKET_SIGNATURE);
        bb.putInt(ProtocolConstants.QUERY_RESP_HEADER_SIZE + len);
        bb.putInt(ProtocolConstants.PACKET_TYPE_QUERY_RESPONSE);
        bb.putInt(query_id);

        // Save response data (if there is any).
        if (qresp != null && len != 0) {
            bb.put(qresp, 0, len);
        }

        // Send the response.
        try {
            Socket socket = mSocket;
            if (socket != null) {
                socket.send(bb.array());
                return true;
            } else {
                Logw("sendQueryResponse is called on disconnected Channel "
                        + mChannelName);
            }
        } catch (IOException e) {
            Loge("Exception " + e + " in sendQueryResponse for Channel " + mChannelName);
            onIoFailure();
        }
        return false;
    }

    /**
     * Sends query response to the emulator.
     *
     * @param query_id Query identifier.
     * @param qresp Response to the query. Query response size is defined by the
     *            size of the array.
     * @return true on success, or false if data transmission has failed.
     */
    public boolean sendQueryResponse(int query_id, byte[] qresp) {
        return (qresp != null) ? sendQueryResponse(query_id, qresp, qresp.length) :
                sendQueryResponse(query_id, null, 0);
    }

    /**
     * Sends query response to the emulator.
     *
     * @param query_id Query identifier.
     * @param qresp Response to the query. Query response size is defined by the
     *            position() property of the ByteBuffer.
     * @return true on success, or false if data transmission has failed.
     */
    public boolean sendQueryResponse(int query_id, ByteBuffer qresp) {
        return (qresp != null) ? sendQueryResponse(query_id, qresp.array(), qresp.position()) :
                sendQueryResponse(query_id, null, 0);
    }

    /**
     * Handles an I/O failure occurred in the channel.
     */
    private void onIoFailure() {
        // All I/O failures cause disconnection.
        if (disconnect()) {
            // Success of disconnect() indicates that I/O failure is not the
            // result of a disconnection request, but is in deed an I/O
            // failure. Report lost connection to the derived class.
            Loge("Connection with the emulator has been lost in Channel " + mChannelName);
            onEmulatorDisconnected();
        }
    }

    /**
     * Loops on the local socket, handling connection attempts.
     */
    private void runIOLooper() {
        if (DEBUG) Log.d(TAG, "In I/O looper for Channel " + mChannelName);
        // Initialize byte buffer large enough to receive packet header.
        ByteBuffer header = ByteBuffer.allocate(ProtocolConstants.PACKET_HEADER_SIZE);
        header.order(mEndian);
        try {
            // Since disconnection (which will null the mSocket) can be
            // requested from outside of this thread, it's simpler just to make
            // a copy of mSocket here, and work with that copy. Otherwise we
            // will have to go through a complex synchronization algorithm that
            // would decrease performance on normal runs. If socket gets closed
            // while we're in the middle of transfer, an exception will occur,
            // which we will catch and handle properly.
            Socket socket = mSocket;
            while (socket != null) {
                // Reset header position.
                header.position(0);
                // This will receive total packet size + packet type.
                socket.receive(header.array());
                // First - signature.
                final int signature = header.getInt();
                assert signature == ProtocolConstants.PACKET_SIGNATURE;
                // Next - packet size (including header).
                int remains = header.getInt() - ProtocolConstants.PACKET_HEADER_SIZE;
                // After the size comes packet type.
                final int packet_type = header.getInt();

                // Get the remainder of the data, and dispatch the packet to
                // an appropriate handler.
                switch (packet_type) {
                    case ProtocolConstants.PACKET_TYPE_MESSAGE:
                        // Read message header (one int: message type).
                        final int ext = ProtocolConstants.MESSAGE_HEADER_SIZE - ProtocolConstants.PACKET_HEADER_SIZE;
                        header.position(0);
                        socket.receive(header.array(), ext);
                        final int msg_type = header.getInt();

                        // Read message data.
                        remains -= ext;
                        final ByteBuffer msg_data = ByteBuffer.allocate(remains);
                        msg_data.order(mEndian);
                        socket.receive(msg_data.array());

                        // Dispatch message for handling.
                        onEmulatorMessage(msg_type, msg_data);
                        break;

                    case ProtocolConstants.PACKET_TYPE_QUERY:
                        // Read query ID and query type.
                        final int extq = ProtocolConstants.QUERY_HEADER_SIZE - ProtocolConstants.PACKET_HEADER_SIZE;
                        header.position(0);
                        socket.receive(header.array(), extq);
                        final int query_id = header.getInt();
                        final int query_type = header.getInt();

                        // Read query data.
                        remains -= extq;
                        final ByteBuffer query_data = ByteBuffer.allocate(remains);
                        query_data.order(mEndian);
                        socket.receive(query_data.array());

                        // Dispatch query for handling.
                        onEmulatorQuery(query_id, query_type, query_data);
                        break;

                    default:
                        // Unknown packet type. Just discard the remainder
                        // of the packet
                        Loge("Unknown packet type " + packet_type + " in Channel "
                                + mChannelName);
                        final byte[] discard_data = new byte[remains];
                        socket.receive(discard_data);
                        break;
                }
                socket = mSocket;
            }
        } catch (IOException e) {
            Loge("Exception " + e + " in I/O looper for Channel " + mChannelName);
            onIoFailure();
        }
        if (DEBUG) Log.d(TAG, "Exiting I/O looper for Channel " + mChannelName);
    }

    /**
     * Indicates any UI handler is currently registered with the channel. If no UI
     * is displaying the channel's state, maybe the channel can skip UI related tasks.
     *
     * @return True if there's at least one UI handler registered.
     */
    public boolean hasUiHandler() {
        return !mUiHandlers.isEmpty();
    }

    /**
     * Registers a new UI handler.
     *
     * @param uiHandler A non-null UI handler to register. Ignored if the UI
     *            handler is null or already registered.
     */
    public void addUiHandler(android.os.Handler uiHandler) {
        assert uiHandler != null;
        if (uiHandler != null) {
            if (!mUiHandlers.contains(uiHandler)) {
                mUiHandlers.add(uiHandler);
            }
        }
    }

    /**
     * Unregisters an UI handler.
     *
     * @param uiHandler A non-null UI listener to unregister. Ignored if the
     *            listener is null or already registered.
     */
    public void removeUiHandler(android.os.Handler uiHandler) {
        assert uiHandler != null;
        mUiHandlers.remove(uiHandler);
    }

    /**
     * Protected method to be used by handlers to send an event to all UI
     * handlers.
     *
     * @param event An integer event code with no specific parameters. To be
     *            defined by the handler itself.
     */
    protected void notifyUiHandlers(int event) {
        for (android.os.Handler uiHandler : mUiHandlers) {
            uiHandler.sendEmptyMessage(event);
        }
    }

    /**
     * Protected method to be used by handlers to send an event to all UI
     * handlers.
     *
     * @param msg An event with parameters. To be defined by the handler itself.
     */
    protected void notifyUiHandlers(Message msg) {
        for (android.os.Handler uiHandler : mUiHandlers) {
            uiHandler.sendMessage(msg);
        }
    }

    /**
     * A helper routine that expands ByteBuffer to contain given number of extra
     * bytes.
     *
     * @param buff Buffer to expand.
     * @param extra Number of bytes that are required to be available in the
     *            buffer after current position()
     * @return ByteBuffer, containing required number of available bytes.
     */
    public ByteBuffer ExpandIf(ByteBuffer buff, int extra) {
        if (extra <= buff.remaining()) {
            return buff;
        }
        ByteBuffer ret = ByteBuffer.allocate(buff.position() + extra);
        ret.order(buff.order());
        ret.put(buff.array(), 0, buff.position());
        return ret;
    }

    /***************************************************************************
     * Logging wrappers
     **************************************************************************/

    private void Loge(String log) {
        mService.addError(log);
        Log.e(TAG, log);
    }

    private void Logw(String log) {
        Log.w(TAG, log);
    }

    private void Logv(String log) {
        Log.v(TAG, log);
    }
}
