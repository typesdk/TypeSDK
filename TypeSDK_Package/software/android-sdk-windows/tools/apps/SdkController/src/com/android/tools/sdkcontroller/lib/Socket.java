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

import android.net.LocalSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;

/**
 * Encapsulates a connection with the emulator over a UNIX-domain socket.
 */
public class Socket {
    /** UNIX-domain socket connected with the emulator. */
    private LocalSocket mSocket = null;
    /** Channel name for the connection established via this socket. */
    private String mChannelName;
    /** Endianness of data transferred in this connection. */
    private ByteOrder mEndian;

    /** Tag for message logging. */
    private static final String TAG = "SdkControllerSocket";
    /** Controls debug log. */
    private static boolean DEBUG = false;

    /**
     * Constructs Socket instance.
     *
     * @param socket Socket connection with the emulator.
     * @param name Channel port name for this connection.
     * @param endian Endianness of data transferred in this connection.
     */
    public Socket(LocalSocket socket, String name, ByteOrder endian) {
        mSocket = socket;
        mChannelName = name;
        mEndian = endian;
        if (DEBUG) Log.d(TAG, "Socket is constructed for " + mChannelName);
    }

    /**
     * Gets connection status of this socket.
     *
     * @return true if socket is connected, or false if socket is not connected.
     */
    public boolean isConnected() {
        return mSocket != null;
    }

    /**
     * Gets channel name for this socket.
     *
     * @return Channel name for this socket.
     */
    public String getChannelName() {
        return mChannelName;
    }

    /**
     * Gets endianness of data transferred via this socket.
     *
     * @return Endianness of data transferred via this socket.
     */
    public ByteOrder getEndian() {
        return mEndian;
    }

    /**
     * Sends data to the socket.
     *
     * @param data Data to send. Data size is defined by the length of the
     *            array.
     * @throws IOException
     */
    public void send(byte[] data) throws IOException {
        // Use local copy of the socket, ensuring it's not going to NULL while
        // we're working with it. If it gets closed, while we're in the middle
        // of data transfer - it's OK, since it will produce an exception, and
        // the caller will gracefully handle it.
        //
        // Same technique is used everywhere in this class where mSocket member
        // is touched.
        LocalSocket socket = mSocket;
        if (socket == null) {
            Logw("'send' request on closed Socket " + mChannelName);
            throw new ClosedChannelException();
        }
        socket.getOutputStream().write(data);
    }

    /**
     * Sends data to the socket.
     *
     * @param data Data to send.
     * @param offset The start position in data from where to get bytes.
     * @param len The number of bytes from data to write to this socket.
     * @throws IOException
     */
    public void send(byte[] data, int offset, int len) throws IOException {
        LocalSocket socket = mSocket;
        if (socket == null) {
            Logw("'send' request on closed Socket " + mChannelName);
            throw new ClosedChannelException();
        }
        socket.getOutputStream().write(data, offset, len);
    }

    /**
     * Receives data from the socket.
     *
     * @param socket Socket from where to receive data.
     * @param data Array where to save received data.
     * @param len Number of bytes to receive.
     * @throws IOException
     */
    public static void receive(LocalSocket socket, byte[] data, int len) throws IOException {
        final InputStream is = socket.getInputStream();
        int received = 0;
        while (received != len) {
            final int chunk = is.read(data, received, len - received);
            if (chunk < 0) {
                throw new IOException(
                        "I/O failure while receiving SDK controller data from socket.");
            }
            received += chunk;
        }
    }

    /**
     * Receives data from the socket.
     *
     * @param data Array where to save received data.
     * @param len Number of bytes to receive.
     * @throws IOException
     */
    public void receive(byte[] data, int len) throws IOException {
        LocalSocket socket = mSocket;
        if (socket == null) {
            Logw("'receive' request on closed Socket " + mChannelName);
            throw new ClosedChannelException();
        }
        receive(socket, data, len);
    }

    /**
     * Receives data from the socket.
     *
     * @param data Array where to save received data. Data size is defined by
     *            the size of the array.
     * @throws IOException
     */
    public void receive(byte[] data) throws IOException {
        receive(data, data.length);
    }

    /**
     * Closes the socket.
     *
     * @return true if socket has been closed in this call, or false if it had
     *         been already closed when this method has been called.
     */
    public boolean close() {
        // This is the only place in this class where we will null the socket
        // object. Since this method can be called concurrently from different
        // threads, lets do this under the lock.
        LocalSocket socket;
        synchronized (this) {
            socket = mSocket;
            mSocket = null;
        }
        if (socket != null) {
            try {
                // Force all I/O to stop before closing the socket.
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
                if (DEBUG) Log.d(TAG, "Socket is closed for " + mChannelName);
                return true;
            } catch (IOException e) {
                Loge("Exception " + e + " while closing Socket for " + mChannelName);
            }
        }
        return false;
    }

    /***************************************************************************
     * Logging wrappers
     **************************************************************************/

    private void Loge(String log) {
        Log.e(TAG, log);
    }

    private void Logw(String log) {
        Log.w(TAG, log);
    }
}
