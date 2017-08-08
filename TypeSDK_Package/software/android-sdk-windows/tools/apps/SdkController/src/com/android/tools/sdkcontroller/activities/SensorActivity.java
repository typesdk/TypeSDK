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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.tools.sdkcontroller.R;
import com.android.tools.sdkcontroller.handlers.SensorChannel;
import com.android.tools.sdkcontroller.handlers.SensorChannel.MonitoredSensor;
import com.android.tools.sdkcontroller.lib.Channel;
import com.android.tools.sdkcontroller.service.ControllerService.ControllerBinder;
import com.android.tools.sdkcontroller.service.ControllerService.ControllerListener;

/**
 * Activity that displays and controls the sensors from {@link SensorChannel}.
 * For each sensor it displays a checkbox that is enabled if the sensor is supported
 * by the emulator. The user can select whether the sensor is active. It also displays
 * data from the sensor when available.
 */
public class SensorActivity extends BaseBindingActivity
        implements android.os.Handler.Callback {

    @SuppressWarnings("hiding")
    public static String TAG = SensorActivity.class.getSimpleName();
    private static boolean DEBUG = true;

    private static final int MSG_UPDATE_ACTUAL_HZ = 0x31415;

    private TableLayout mTableLayout;
    private TextView mTextError;
    private TextView mTextStatus;
    private TextView mTextTargetHz;
    private TextView mTextActualHz;
    private SensorChannel mSensorHandler;

    private final Map<MonitoredSensor, DisplayInfo> mDisplayedSensors =
        new HashMap<SensorChannel.MonitoredSensor, SensorActivity.DisplayInfo>();
    private final android.os.Handler mUiHandler = new android.os.Handler(this);
    private int mTargetSampleRate;
    private long mLastActualUpdateMs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors);
        mTableLayout = (TableLayout) findViewById(R.id.tableLayout);
        mTextError  = (TextView) findViewById(R.id.textError);
        mTextStatus = (TextView) findViewById(R.id.textStatus);
        mTextTargetHz = (TextView) findViewById(R.id.textSampleRate);
        mTextActualHz = (TextView) findViewById(R.id.textActualRate);
        updateStatus("Waiting for connection");

        mTextTargetHz.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                updateSampleRate();
                return false;
            }
        });
        mTextTargetHz.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateSampleRate();
            }
        });
    }

    @Override
    protected void onResume() {
        if (DEBUG) Log.d(TAG, "onResume");
        // BaseBindingActivity.onResume will bind to the service.
        super.onResume();
        updateError();
    }

    @Override
    protected void onPause() {
        if (DEBUG) Log.d(TAG, "onPause");
        // BaseBindingActivity.onResume will unbind from (but not stop) the service.
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy");
        super.onDestroy();
        removeSensorUi();
    }

    // ----------

    @Override
    protected void onServiceConnected() {
        if (DEBUG) Log.d(TAG, "onServiceConnected");
        createSensorUi();
    }

    @Override
    protected void onServiceDisconnected() {
        if (DEBUG) Log.d(TAG, "onServiceDisconnected");
        removeSensorUi();
    }

    @Override
    protected ControllerListener createControllerListener() {
        return new SensorsControllerListener();
    }

    // ----------

    private class SensorsControllerListener implements ControllerListener {
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
                        mTableLayout.setEnabled(connected);
                        updateStatus(connected ? "Emulated connected" : "Emulator disconnected");
                    }
                }
            });
        }
    }

    private void createSensorUi() {
        final LayoutInflater inflater = getLayoutInflater();

        if (!mDisplayedSensors.isEmpty()) {
            removeSensorUi();
        }

        mSensorHandler = (SensorChannel) getServiceBinder().getChannel(Channel.SENSOR_CHANNEL);
        if (mSensorHandler != null) {
            mSensorHandler.addUiHandler(mUiHandler);
            mUiHandler.sendEmptyMessage(MSG_UPDATE_ACTUAL_HZ);

            assert mDisplayedSensors.isEmpty();
            List<MonitoredSensor> sensors = mSensorHandler.getSensors();
            for (MonitoredSensor sensor : sensors) {
                final TableRow row = (TableRow) inflater.inflate(R.layout.sensor_row,
                                                                 mTableLayout,
                                                                 false);
                mTableLayout.addView(row);
                mDisplayedSensors.put(sensor, new DisplayInfo(sensor, row));
            }
        }
    }

    private void removeSensorUi() {
        if (mSensorHandler != null) {
            mSensorHandler.removeUiHandler(mUiHandler);
            mSensorHandler = null;
        }
        mTableLayout.removeAllViews();
        for (DisplayInfo info : mDisplayedSensors.values()) {
            info.release();
        }
        mDisplayedSensors.clear();
    }

    private class DisplayInfo implements CompoundButton.OnCheckedChangeListener {
        private MonitoredSensor mSensor;
        private CheckBox mChk;
        private TextView mVal;

        public DisplayInfo(MonitoredSensor sensor, TableRow row) {
            mSensor = sensor;

            // Initialize displayed checkbox for this sensor, and register
            // checked state listener for it.
            mChk = (CheckBox) row.findViewById(R.id.row_checkbox);
            mChk.setText(sensor.getUiName());
            mChk.setEnabled(sensor.isEnabledByEmulator());
            mChk.setChecked(sensor.isEnabledByUser());
            mChk.setOnCheckedChangeListener(this);

            // Initialize displayed text box for this sensor.
            mVal = (TextView) row.findViewById(R.id.row_textview);
            mVal.setText(sensor.getValue());
        }

        /**
         * Handles checked state change for the associated CheckBox. If check
         * box is checked we will register sensor change listener. If it is
         * unchecked, we will unregister sensor change listener.
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mSensor != null) {
                mSensor.onCheckedChanged(isChecked);
            }
        }

        public void release() {
            mChk = null;
            mVal = null;
            mSensor = null;

        }

        public void updateState() {
            if (mChk != null && mSensor != null) {
                mChk.setEnabled(mSensor.isEnabledByEmulator());
                mChk.setChecked(mSensor.isEnabledByUser());
            }
        }

        public void updateValue() {
            if (mVal != null && mSensor != null) {
                mVal.setText(mSensor.getValue());
            }
        }
    }

    /** Implementation of Handler.Callback */
    @Override
    public boolean handleMessage(Message msg) {
        DisplayInfo info = null;
        switch (msg.what) {
        case SensorChannel.SENSOR_STATE_CHANGED:
            info = mDisplayedSensors.get(msg.obj);
            if (info != null) {
                info.updateState();
            }
            break;
        case SensorChannel.SENSOR_DISPLAY_MODIFIED:
            info = mDisplayedSensors.get(msg.obj);
            if (info != null) {
                info.updateValue();
            }
            if (mSensorHandler != null) {
                updateStatus(Integer.toString(mSensorHandler.getMsgSentCount()) + " events sent");

                // Update the "actual rate" field if the value has changed
                long ms = mSensorHandler.getActualUpdateMs();
                if (ms != mLastActualUpdateMs) {
                    mLastActualUpdateMs = ms;
                    String hz = mLastActualUpdateMs <= 0 ? "--" :
                                    Integer.toString((int) Math.ceil(1000. / ms));
                    mTextActualHz.setText(hz);
                }
            }
            break;
        case MSG_UPDATE_ACTUAL_HZ:
            if (mSensorHandler != null) {
                // Update the "actual rate" field if the value has changed
                long ms = mSensorHandler.getActualUpdateMs();
                if (ms != mLastActualUpdateMs) {
                    mLastActualUpdateMs = ms;
                    String hz = mLastActualUpdateMs <= 0 ? "--" :
                                    Integer.toString((int) Math.ceil(1000. / ms));
                    mTextActualHz.setText(hz);
                }
                mUiHandler.sendEmptyMessageDelayed(MSG_UPDATE_ACTUAL_HZ, 1000 /*1s*/);
            }
        }
        return true; // we consumed this message
    }

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

    private void updateSampleRate() {
        String str = mTextTargetHz.getText().toString();
        try {
            int hz = Integer.parseInt(str.trim());

            // Cap the value. 50 Hz is a reasonable max value for the emulator.
            if (hz <= 0 || hz > 50) {
                hz = 50;
            }

            if (hz != mTargetSampleRate) {
                mTargetSampleRate = hz;
                if (mSensorHandler != null) {
                    mSensorHandler.setUpdateTargetMs(hz <= 0 ? 0 : (int)(1000.0f / hz));
                }
            }
        } catch (Exception ignore) {}
    }
}
