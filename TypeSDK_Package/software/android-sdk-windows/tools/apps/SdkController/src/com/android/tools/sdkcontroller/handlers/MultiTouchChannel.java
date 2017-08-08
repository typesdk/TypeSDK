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

package com.android.tools.sdkcontroller.handlers;

import android.graphics.Point;
import android.os.Message;
import android.util.Log;

import com.android.tools.sdkcontroller.lib.Channel;
import com.android.tools.sdkcontroller.lib.ProtocolConstants;
import com.android.tools.sdkcontroller.service.ControllerService;

import java.nio.ByteBuffer;

/**
 * Implements multi-touch emulation.
 */
public class MultiTouchChannel extends Channel {

    @SuppressWarnings("hiding")
    private static final String TAG = MultiTouchChannel.class.getSimpleName();
    /**
     * A new frame buffer has been received from the emulator.
     * Parameter {@code obj} is a {@code byte[] array} containing the screen data.
     */
    public static final int EVENT_FRAME_BUFFER = 1;
    /**
     * A multi-touch "start" command has been received from the emulator.
     * Parameter {@code obj} is the string parameter from the start command.
     */
    public static final int EVENT_MT_START = 2;
    /**
     * A multi-touch "stop" command has been received from the emulator. There
     * is no {@code obj} parameter associated.
     */
    public static final int EVENT_MT_STOP = 3;

    private static final Point mViewSize = new Point(0, 0);

    /**
     * Constructs MultiTouchChannel instance.
     */
    public MultiTouchChannel(ControllerService service) {
        super(service, Channel.MULTITOUCH_CHANNEL);
    }

    /**
     * Sets size of the display view for emulated screen updates.
     *
     * @param width View width in pixels.
     * @param height View height in pixels.
     */
    public void setViewSize(int width, int height) {
        mViewSize.set(width, height);
    }

    /*
     * Channel abstract implementation.
     */

    /**
     * This method is invoked when this channel is fully connected with its
     * counterpart in the emulator.
     */
    @Override
    public void onEmulatorConnected() {
        if (hasUiHandler()) {
            enable();
            notifyUiHandlers(EVENT_MT_START);
        }
    }

    /**
     * This method is invoked when this channel loses connection with its
     * counterpart in the emulator.
     */
    @Override
    public void onEmulatorDisconnected() {
        if (hasUiHandler()) {
            disable();
            notifyUiHandlers(EVENT_MT_STOP);
        }
    }

    /**
     * A message has been received from the emulator.
     *
     * @param msg_type Message type.
     * @param msg_data Packet received from the emulator.
     */
    @Override
    public void onEmulatorMessage(int msg_type, ByteBuffer msg_data) {
        switch (msg_type) {
            case ProtocolConstants.MT_FB_UPDATE:
                Message msg = Message.obtain();
                msg.what = EVENT_FRAME_BUFFER;
                msg.obj = msg_data;
                postMessage(ProtocolConstants.MT_FB_ACK, (byte[]) null);
                notifyUiHandlers(msg);
                break;

            default:
                Log.e(TAG, "Unknown message type " + msg_type);
        }
    }

    /**
     * A query has been received from the emulator.
     *
     * @param query_id Identifies the query. This ID must be used when replying
     *            to the query.
     * @param query_type Query type.
     * @param query_data Query data.
     */
    @Override
    public void onEmulatorQuery(int query_id, int query_type, ByteBuffer query_data) {
        Loge("Unexpected query " + query_type + " in multi-touch");
        sendQueryResponse(query_id, (byte[]) null);
    }

    /**
     * Registers a new UI handler.
     *
     * @param uiHandler A non-null UI handler to register. Ignored if the UI
     *            handler is null or already registered.
     */
    @Override
    public void addUiHandler(android.os.Handler uiHandler) {
        final boolean first_handler = !hasUiHandler();
        super.addUiHandler(uiHandler);
        if (first_handler && isConnected()) {
            enable();
            notifyUiHandlers(EVENT_MT_START);
        }
    }

    /**
     * Unregisters an UI handler.
     *
     * @param uiHandler A non-null UI listener to unregister. Ignored if the
     *            listener is null or already registered.
     */
    @Override
    public void removeUiHandler(android.os.Handler uiHandler) {
        super.removeUiHandler(uiHandler);
        if (isConnected() && !hasUiHandler()) {
            disable();
        }
    }

    /***************************************************************************
     * Logging wrappers
     **************************************************************************/

    private void Loge(String log) {
        mService.addError(log);
        Log.e(TAG, log);
    }
}
