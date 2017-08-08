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

package com.android.tools.sdkcontroller.activities;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.android.tools.sdkcontroller.R;
import com.android.tools.sdkcontroller.handlers.MultiTouchChannel;
import com.android.tools.sdkcontroller.lib.Channel;
import com.android.tools.sdkcontroller.lib.ProtocolConstants;
import com.android.tools.sdkcontroller.service.ControllerService.ControllerBinder;
import com.android.tools.sdkcontroller.service.ControllerService.ControllerListener;
import com.android.tools.sdkcontroller.utils.ApiHelper;
import com.android.tools.sdkcontroller.views.MultiTouchView;

/**
 * Activity that controls and displays the {@link MultiTouchChannel}.
 */
public class MultiTouchActivity extends BaseBindingActivity
        implements android.os.Handler.Callback {

    @SuppressWarnings("hiding")
    private static String TAG = MultiTouchActivity.class.getSimpleName();
    private static boolean DEBUG = true;

    private volatile MultiTouchChannel mHandler;

    private TextView mTextError;
    private TextView mTextStatus;
    private MultiTouchView mImageView;
    /** Width of the emulator's display. */
    private int mEmulatorWidth = 0;
    /** Height of the emulator's display. */
    private int mEmulatorHeight = 0;
    /** Bitmap storage. */
    private int[] mColors;

    private final TouchListener mTouchListener = new TouchListener();
    private final android.os.Handler mUiHandler = new android.os.Handler(this);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multitouch);
        mImageView  = (MultiTouchView) findViewById(R.id.imageView);
        mTextError  = (TextView) findViewById(R.id.textError);
        mTextStatus = (TextView) findViewById(R.id.textStatus);
        updateStatus("Waiting for connection");

        ApiHelper ah = ApiHelper.get();
        ah.View_setSystemUiVisibility(mImageView, View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    @Override
    protected void onResume() {
        if (DEBUG) Log.d(TAG, "onResume");
        // BaseBindingActivity.onResume will bind to the service.
        // Note: any initialization related to the service or the handler should
        // go in onServiceConnected() since in this call the service may not be
        // bound yet.
        super.onResume();
        updateError();
    }

    @Override
    protected void onPause() {
        if (DEBUG) Log.d(TAG, "onPause");
        // BaseBindingActivity.onResume will unbind from (but not stop) the service.
        super.onPause();
        mImageView.setEnabled(false);
        updateStatus("Paused");
    }

    // ----------

    @Override
    protected void onServiceConnected() {
        if (DEBUG) Log.d(TAG, "onServiceConnected");
        mHandler = (MultiTouchChannel) getServiceBinder().getChannel(Channel.MULTITOUCH_CHANNEL);
        if (mHandler != null) {
            mHandler.setViewSize(mImageView.getWidth(), mImageView.getHeight());
            mHandler.addUiHandler(mUiHandler);
        }
    }

    @Override
    protected void onServiceDisconnected() {
        if (DEBUG) Log.d(TAG, "onServiceDisconnected");
        if (mHandler != null) {
            mHandler.removeUiHandler(mUiHandler);
            mHandler = null;
        }
    }

    @Override
    protected ControllerListener createControllerListener() {
        return new MultiTouchControllerListener();
    }

    // ----------

    private class MultiTouchControllerListener implements ControllerListener {
        @Override
        public void onErrorChanged() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateError();
                }
            });
        }

        @Override
        public void onStatusChanged() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ControllerBinder binder = getServiceBinder();
                    if (binder != null) {
                        boolean connected = binder.isEmuConnected();
                        mImageView.setEnabled(connected);
                        updateStatus(connected ? "Emulator connected" : "Emulator disconnected");
                    }
                }
            });
        }
    }

    // ----------

    /**
     * Implements OnTouchListener interface that receives touch screen events,
     * and reports them to the emulator application.
     */
    class TouchListener implements OnTouchListener {
        /**
         * Touch screen event handler.
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ByteBuffer bb = null;
            final int action = event.getAction();
            final int action_code = action & MotionEvent.ACTION_MASK;
            final int action_pid_index = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int msg_type = 0;
            MultiTouchChannel h = mHandler;

            // Build message for the emulator.
            switch (action_code) {
                case MotionEvent.ACTION_MOVE:
                    if (h != null) {
                        bb = ByteBuffer.allocate(
                                event.getPointerCount() * ProtocolConstants.MT_EVENT_ENTRY_SIZE);
                        bb.order(h.getEndian());
                        for (int n = 0; n < event.getPointerCount(); n++) {
                            mImageView.constructEventMessage(bb, event, n);
                        }
                        msg_type = ProtocolConstants.MT_MOVE;
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (h != null) {
                        bb = ByteBuffer.allocate(ProtocolConstants.MT_EVENT_ENTRY_SIZE);
                        bb.order(h.getEndian());
                        mImageView.constructEventMessage(bb, event, action_pid_index);
                        msg_type = ProtocolConstants.MT_FISRT_DOWN;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (h != null) {
                        bb = ByteBuffer.allocate(ProtocolConstants.MT_EVENT_ENTRY_SIZE);
                        bb.order(h.getEndian());
                        bb.putInt(event.getPointerId(action_pid_index));
                        msg_type = ProtocolConstants.MT_LAST_UP;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (h != null) {
                        bb = ByteBuffer.allocate(ProtocolConstants.MT_EVENT_ENTRY_SIZE);
                        bb.order(h.getEndian());
                        mImageView.constructEventMessage(bb, event, action_pid_index);
                        msg_type = ProtocolConstants.MT_POINTER_DOWN;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if (h != null) {
                        bb = ByteBuffer.allocate(ProtocolConstants.MT_EVENT_ENTRY_SIZE);
                        bb.order(h.getEndian());
                        bb.putInt(event.getPointerId(action_pid_index));
                        msg_type = ProtocolConstants.MT_POINTER_UP;
                    }
                    break;
                default:
                    Log.w(TAG, "Unknown action type: " + action_code);
                    return true;
            }

            if (DEBUG && bb != null) Log.d(TAG, bb.toString());

            if (h != null && bb != null) {
                h.postMessage(msg_type, bb);
            }
            return true;
        }
    } // TouchListener

    /** Implementation of Handler.Callback */
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case MultiTouchChannel.EVENT_MT_START:
            MultiTouchChannel h = mHandler;
            if (h != null) {
                mImageView.setEnabled(true);
                mImageView.setOnTouchListener(mTouchListener);
            }
            break;
        case MultiTouchChannel.EVENT_MT_STOP:
            mImageView.setOnTouchListener(null);
            break;
        case MultiTouchChannel.EVENT_FRAME_BUFFER:
            onFrameBuffer(((ByteBuffer) msg.obj).array());
            mHandler.postMessage(ProtocolConstants.MT_FB_HANDLED, (byte[]) null);
            break;
        }
        return true; // we consumed this message
    }

    /**
     * Called when a BLOB query is received from the emulator.
     * <p/>
     * This query is used to deliver framebuffer updates in the emulator. The
     * blob contains an update header, followed by the bitmap containing updated
     * rectangle. The header is defined as MTFrameHeader structure in
     * external/qemu/android/multitouch-port.h
     * <p/>
     * NOTE: This method is called from the I/O loop, so all communication with
     * the emulator will be "on hold" until this method returns.
     *
     * TODO ===> CHECK that we can consume that array from a different thread than the producer's.
     * E.g. does the produce reuse the same array or does it generate a new one each time?
     *
     * @param array contains BLOB data for the query.
     */
    private void onFrameBuffer(byte[] array) {
        final ByteBuffer bb = ByteBuffer.wrap(array);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        // Read frame header.
        final int header_size = bb.getInt();
        final int disp_width = bb.getInt();
        final int disp_height = bb.getInt();
        final int x = bb.getInt();
        final int y = bb.getInt();
        final int w = bb.getInt();
        final int h = bb.getInt();
        final int bpl = bb.getInt();
        final int bpp = bb.getInt();
        final int format = bb.getInt();

        // Update application display.
        updateDisplay(disp_width, disp_height);

        if (format == ProtocolConstants.MT_FRAME_JPEG) {
            /*
             * Framebuffer is in JPEG format.
             */

            final ByteArrayInputStream jpg = new ByteArrayInputStream(bb.array());
            // Advance input stream to JPEG image.
            jpg.skip(header_size);
            // Draw the image.
            mImageView.drawJpeg(x, y, w, h, jpg);
        } else {
            /*
             * Framebuffer is in a raw RGB format.
             */

            final int pixel_num = h * w;
            // Advance stream to the beginning of framebuffer data.
            bb.position(header_size);

            // Make sure that mColors is large enough to contain the
            // update bitmap.
            if (mColors == null || mColors.length < pixel_num) {
                mColors = new int[pixel_num];
            }

            // Convert the blob bitmap into bitmap that we will display.
            if (format == ProtocolConstants.MT_FRAME_RGB565) {
                for (int n = 0; n < pixel_num; n++) {
                    // Blob bitmap is in RGB565 format.
                    final int color = bb.getShort();
                    final int r = ((color & 0xf800) >> 8) | ((color & 0xf800) >> 14);
                    final int g = ((color & 0x7e0) >> 3) | ((color & 0x7e0) >> 9);
                    final int b = ((color & 0x1f) << 3) | ((color & 0x1f) >> 2);
                    mColors[n] = Color.rgb(r, g, b);
                }
            } else if (format == ProtocolConstants.MT_FRAME_RGB888) {
                for (int n = 0; n < pixel_num; n++) {
                    // Blob bitmap is in RGB565 format.
                    final int r = bb.getChar();
                    final int g = bb.getChar();
                    final int b = bb.getChar();
                    mColors[n] = Color.rgb(r, g, b);
                }
            } else {
                Log.w(TAG, "Invalid framebuffer format: " + format);
                return;
            }
            mImageView.drawBitmap(x, y, w, h, mColors);
        }
    }

    /**
     * Updates application's screen accordingly to the emulator screen.
     *
     * @param e_width Width of the emulator screen.
     * @param e_height Height of the emulator screen.
     */
    private void updateDisplay(int e_width, int e_height) {
        if (e_width != mEmulatorWidth || e_height != mEmulatorHeight) {
            mEmulatorWidth = e_width;
            mEmulatorHeight = e_height;

            boolean rotateDisplay = false;
            int w = mImageView.getWidth();
            int h = mImageView.getHeight();
            if (w > h != e_width > e_height) {
                rotateDisplay = true;
                int tmp = w;
                w = h;
                h = tmp;
            }

            float dx = (float) w / (float) e_width;
            float dy = (float) h / (float) e_height;
            mImageView.setDxDy(dx, dy, rotateDisplay);
            if (DEBUG) Log.d(TAG, "Dispay updated: " + e_width + " x " + e_height +
                    " -> " + w + " x " + h + " ratio: " +
                    dx + " x " + dy);
        }
    }

    // ----------

    private void updateStatus(String status) {
        mTextStatus.setVisibility(status == null ? View.GONE : View.VISIBLE);
        if (status != null) mTextStatus.setText(status);
    }

    private void updateError() {
        ControllerBinder binder = getServiceBinder();
        String error = binder == null ? "" : binder.getServiceError();
        if (error == null) {
            error = "";
        }

        mTextError.setVisibility(error.length() == 0 ? View.GONE : View.VISIBLE);
        mTextError.setText(error);
    }
}
