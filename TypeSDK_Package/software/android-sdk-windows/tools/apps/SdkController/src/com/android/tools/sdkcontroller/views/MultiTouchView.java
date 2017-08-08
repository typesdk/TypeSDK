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

package com.android.tools.sdkcontroller.views;

import java.io.InputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Implements a main view for the application providing multi-touch emulation.
 */
public class MultiTouchView extends View {
    /** Tag for logging messages. */
    private static final String TAG = MultiTouchView.class.getSimpleName();
    /**
     * Back-end bitmap. Initialized in onSizeChanged(), updated in
     * onTouchEvent() and drawn in onDraw().
     */
    private Bitmap mBitmap;
    /** Default Paint instance for drawing the bitmap. */
    private final Paint mPaint = new Paint();
    /** Canvas instance for this view. */
    private Canvas mCanvas;
    /** Emulator screen width to this view width ratio. */
    private float mDx = 1;
    /** Emulator screen height to this view height ratio. */
    private float mDy = 1;
    /**
     * Flags whether or not image received from the emulator should be rotated.
     * Rotation is required when display orientation state of the emulator and
     * the device doesn't match.
     */
    private boolean mRotateDisplay;
    /** Base matrix that keep emulator->device display scaling */
    private Matrix mBaseMatrix = new Matrix();
    /** Matrix that is used to draw emulator's screen on the device. */
    private Matrix mDrawMatrix = new Matrix();

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @see View#View(Context)
     */
    public MultiTouchView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @see View#View(Context, AttributeSet)
     */
    public MultiTouchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @see View#View(Context, AttributeSet, int)
     */
    public MultiTouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // TODO Add constructor-time code here.
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Just draw the back-end bitmap without zooming or scaling.
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    /**
     * Sets emulator screen width and height to this view width and height
     * ratio.
     *
     * @param dx Emulator screen width to this view width ratio.
     * @param dy Emulator screen height to this view height ratio.
     * @param rotateDisplay Flags whether image received from the emulator
     *            should be rotated when drawn on the device.
     */
    public void setDxDy(float dx, float dy, boolean rotateDisplay) {
        mDx = dx;
        mDy = dy;
        mRotateDisplay = rotateDisplay;

        mBaseMatrix.setScale(dx, dy);
        if (mRotateDisplay) {
            mBaseMatrix.postRotate(90);
            mBaseMatrix.postTranslate(getWidth(), 0);
        }
    }

    /**
     * Computes draw matrix for the emulator screen update.
     *
     * @param x Left screen coordinate of the bitmap on emulator screen.
     * @param y Top screen coordinate of the bitmap on emulator screen.
     */
    private void computeDrawMatrix(int x, int y) {
        mDrawMatrix.set(mBaseMatrix);
        if (mRotateDisplay) {
            mDrawMatrix.postTranslate(-y * mDy, x * mDx);
        } else {
            mDrawMatrix.postTranslate(x * mDx, y * mDy);
        }
    }

    /**
     * Draws a bitmap on the screen.
     *
     * @param x Left screen coordinate of the bitmap on emulator screen.
     * @param y Top screen coordinate of the bitmap on emulator screen.
     * @param w Width of the bitmap on the emulator screen.
     * @param h Height of the bitmap on the emulator screen.
     * @param colors Bitmap to draw.
     */
    public void drawBitmap(int x, int y, int w, int h, int[] colors) {
        if (mCanvas != null) {
            final Bitmap bmp = Bitmap.createBitmap(colors, 0, w, w, h, Bitmap.Config.ARGB_8888);

            computeDrawMatrix(x, y);

            /* Draw the bitmap and invalidate the updated region. */
            mCanvas.drawBitmap(bmp, mDrawMatrix, mPaint);
            invalidate();
        }
    }

    /**
     * Draws a JPEG bitmap on the screen.
     *
     * @param x Left screen coordinate of the bitmap on emulator screen.
     * @param y Top screen coordinate of the bitmap on emulator screen.
     * @param w Width of the bitmap on the emulator screen.
     * @param h Height of the bitmap on the emulator screen.
     * @param jpeg JPEG bitmap to draw.
     */
    public void drawJpeg(int x, int y, int w, int h, InputStream jpeg) {
        if (mCanvas != null) {
            final Bitmap bmp = BitmapFactory.decodeStream(jpeg);

            computeDrawMatrix(x, y);

            /* Draw the bitmap and invalidate the updated region. */
            mCanvas.drawBitmap(bmp, mDrawMatrix, mPaint);
            invalidate();
        }
    }

    /**
     * Constructs touch event message to be send to emulator.
     *
     * @param bb ByteBuffer where to construct the message.
     * @param event Event for which to construct the message.
     * @param ptr_index Index of the motion pointer for which to construct the
     *            message.
     */
    public void constructEventMessage(ByteBuffer bb, MotionEvent event, int ptr_index) {
        bb.putInt(event.getPointerId(ptr_index));
        if (mRotateDisplay == false) {
            bb.putInt((int) (event.getX(ptr_index) / mDx));
            bb.putInt((int) (event.getY(ptr_index) / mDy));
        } else {
            bb.putInt((int) (event.getY(ptr_index) / mDy));
            bb.putInt((int) (getWidth() - event.getX(ptr_index) / mDx));
        }
        // At the system level the input reader takes integers in the range
        // 0 - 100 for the pressure.
        int pressure = (int) (event.getPressure(ptr_index) * 100);
        // Make sure it doesn't exceed 100...
        if (pressure > 100) {
            pressure = 100;
        }
        bb.putInt(pressure);
    }

    /***************************************************************************
     * Logging wrappers
     **************************************************************************/

    @SuppressWarnings("unused")
    private void Loge(String log) {
        Log.e(TAG, log);
    }

    @SuppressWarnings("unused")
    private void Logw(String log) {
        Log.w(TAG, log);
    }

    @SuppressWarnings("unused")
    private void Logv(String log) {
        Log.v(TAG, log);
    }
}
