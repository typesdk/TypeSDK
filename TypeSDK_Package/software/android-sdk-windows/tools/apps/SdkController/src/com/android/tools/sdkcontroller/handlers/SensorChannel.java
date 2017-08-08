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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.android.tools.sdkcontroller.lib.Channel;
import com.android.tools.sdkcontroller.lib.ProtocolConstants;
import com.android.tools.sdkcontroller.service.ControllerService;

/**
 * Implements sensors emulation.
 */
public class SensorChannel extends Channel {

    @SuppressWarnings("hiding")
    private static String TAG = SensorChannel.class.getSimpleName();
    @SuppressWarnings("hiding")
    private static boolean DEBUG = false;
    /**
     * The target update time per sensor. Ignored if 0 or negative.
     * Sensor updates that arrive faster than this delay are ignored.
     * Ideally the emulator can be updated at up to 50 fps, however
     * for average power devices something like 20 fps is more
     * reasonable.
     * Default value should match res/values/strings.xml > sensors_default_sample_rate.
     */
    private long mUpdateTargetMs = 1000/20; // 20 fps in milliseconds
    /** Accumulates average update frequency. */
    private long mGlobalAvgUpdateMs = 0;

    /** Array containing monitored sensors. */
    private final List<MonitoredSensor> mSensors = new ArrayList<MonitoredSensor>();
    /** Sensor manager. */
    private SensorManager mSenMan;

    /*
     * Messages exchanged with the UI.
     */

    /**
     * Sensor "enabled by emulator" state has changed. Parameter {@code obj} is
     * the {@link MonitoredSensor}.
     */
    public static final int SENSOR_STATE_CHANGED = 1;
    /**
     * Sensor display value has changed. Parameter {@code obj} is the
     * {@link MonitoredSensor}.
     */
    public static final int SENSOR_DISPLAY_MODIFIED = 2;

    /**
     * Constructs SensorChannel instance.
     *
     * @param service Service context.
     */
    public SensorChannel(ControllerService service) {
        super(service, Channel.SENSOR_CHANNEL);
        mSenMan = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);
        // Iterate through the available sensors, adding them to the array.
        List<Sensor> sensors = mSenMan.getSensorList(Sensor.TYPE_ALL);
        int cur_index = 0;
        for (int n = 0; n < sensors.size(); n++) {
            Sensor avail_sensor = sensors.get(n);

            // There can be multiple sensors of the same type. We need only one.
            if (!isSensorTypeAlreadyMonitored(avail_sensor.getType())) {
                // The first sensor we've got for the given type is not
                // necessarily the right one. So, use the default sensor
                // for the given type.
                Sensor def_sens = mSenMan.getDefaultSensor(avail_sensor.getType());
                MonitoredSensor to_add = new MonitoredSensor(def_sens);
                cur_index++;
                mSensors.add(to_add);
                if (DEBUG)
                    Log.d(TAG, String.format(
                            "Monitoring sensor #%02d: Name = '%s', Type = 0x%x",
                            cur_index, def_sens.getName(), def_sens.getType()));
            }
        }
    }

    /**
     * Returns the list of sensors found on the device.
     * The list is computed once by {@link #SensorChannel(ControllerService)}.
     *
     * @return A non-null possibly-empty list of sensors.
     */
    public List<MonitoredSensor> getSensors() {
        return mSensors;
    }

    /**
     * Set the target update delay throttling per-sensor, in milliseconds.
     * <p/>
     * For example setting it to 1000/50 means that updates for a <em>given</em> sensor
     * faster than 50 fps is discarded.
     *
     * @param updateTargetMs 0 to disable throttling, otherwise a > 0 millisecond minimum
     *   between sensor updates.
     */
    public void setUpdateTargetMs(long updateTargetMs) {
        mUpdateTargetMs = updateTargetMs;
    }

    /**
     * Returns the actual average time in milliseconds between same-sensor updates.
     *
     * @return The actual average time in milliseconds between same-sensor updates or 0.
     */
    public long getActualUpdateMs() {
        return mGlobalAvgUpdateMs;
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
        // Emulation is now possible. Note though that it will start only after
        // emulator tells us so with SENSORS_START command.
        enable();
    }

    /**
     * This method is invoked when this channel loses connection with its
     * counterpart in the emulator.
     */
    @Override
    public void onEmulatorDisconnected() {
        // Stop sensor event callbacks.
        stopSensors();
    }

    /**
     * A query has been received from the emulator.
     *
     * @param query_id Identifies the query. This ID should be used when
     *            replying to the query.
     * @param query_type Query type.
     * @param query_data Query data.
     */
    @Override
    public void onEmulatorQuery(int query_id, int query_type, ByteBuffer query_data) {
        switch (query_type) {
            case ProtocolConstants.SENSORS_QUERY_LIST:
                // Preallocate large response buffer.
                ByteBuffer resp = ByteBuffer.allocate(1024);
                resp.order(getEndian());
                // Iterate through the list of monitored sensors, dumping them
                // into the response buffer.
                for (MonitoredSensor sensor : mSensors) {
                    // Entry for each sensor must contain:
                    // - an integer for its ID
                    // - a zero-terminated emulator-friendly name.
                    final byte[] name = sensor.getEmulatorFriendlyName().getBytes();
                    final int required_size = 4 + name.length + 1;
                    resp = ExpandIf(resp, required_size);
                    resp.putInt(sensor.getType());
                    resp.put(name);
                    resp.put((byte) 0);
                }
                // Terminating entry contains single -1 integer.
                resp = ExpandIf(resp, 4);
                resp.putInt(-1);
                sendQueryResponse(query_id, resp);
                return;

            default:
                Loge("Unknown query " + query_type);
                return;
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
            case ProtocolConstants.SENSORS_START:
                Log.v(TAG, "Starting sensors emulation.");
                startSensors();
                break;
            case ProtocolConstants.SENSORS_STOP:
                Log.v(TAG, "Stopping sensors emulation.");
                stopSensors();
                break;
            case ProtocolConstants.SENSORS_ENABLE:
                String enable_name = new String(msg_data.array());
                Log.v(TAG, "Enabling sensor: " + enable_name);
                onEnableSensor(enable_name);
                break;
            case ProtocolConstants.SENSORS_DISABLE:
                String disable_name = new String(msg_data.array());
                Log.v(TAG, "Disabling sensor: " + disable_name);
                onDisableSensor(disable_name);
                break;
            default:
                Loge("Unknown message type " + msg_type);
                break;
        }
    }

    /**
     * Handles 'enable' message.
     *
     * @param name Emulator-friendly name of a sensor to enable, or "all" to
     *            enable all sensors.
     */
    private void onEnableSensor(String name) {
        if (name.contentEquals("all")) {
            // Enable all sensors.
            for (MonitoredSensor sensor : mSensors) {
                sensor.enableSensor();
            }
        } else {
            // Lookup sensor by emulator-friendly name.
            final MonitoredSensor sensor = getSensorByEFN(name);
            if (sensor != null) {
                sensor.enableSensor();
            }
        }
    }

    /**
     * Handles 'disable' message.
     *
     * @param name Emulator-friendly name of a sensor to disable, or "all" to
     *            disable all sensors.
     */
    private void onDisableSensor(String name) {
        if (name.contentEquals("all")) {
            // Disable all sensors.
            for (MonitoredSensor sensor : mSensors) {
                sensor.disableSensor();
            }
        } else {
            // Lookup sensor by emulator-friendly name.
            MonitoredSensor sensor = getSensorByEFN(name);
            if (sensor != null) {
                sensor.disableSensor();
            }
        }
    }

    /**
     * Start listening to all monitored sensors.
     */
    private void startSensors() {
        for (MonitoredSensor sensor : mSensors) {
            sensor.startListening();
        }
    }

    /**
     * Stop listening to all monitored sensors.
     */
    private void stopSensors() {
        for (MonitoredSensor sensor : mSensors) {
            sensor.stopListening();
        }
    }

    /***************************************************************************
     * Internals
     **************************************************************************/

    /**
     * Checks if a sensor for the given type is already monitored.
     *
     * @param type Sensor type (one of the Sensor.TYPE_XXX constants)
     * @return true if a sensor for the given type is already monitored, or
     *         false if the sensor is not monitored.
     */
    private boolean isSensorTypeAlreadyMonitored(int type) {
        for (MonitoredSensor sensor : mSensors) {
            if (sensor.getType() == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks up a monitored sensor by its emulator-friendly name.
     *
     * @param name Emulator-friendly name to look up the monitored sensor for.
     * @return Monitored sensor for the fiven name, or null if sensor was not
     *         found.
     */
    private MonitoredSensor getSensorByEFN(String name) {
        for (MonitoredSensor sensor : mSensors) {
            if (sensor.mEmulatorFriendlyName.contentEquals(name)) {
                return sensor;
            }
        }
        return null;
    }

    /**
     * Encapsulates a sensor that is being monitored. To monitor sensor changes
     * each monitored sensor registers with sensor manager as a sensor listener.
     * To control sensor monitoring from the UI, each monitored sensor has two
     * UI controls associated with it: - A check box (named after sensor) that
     * can be used to enable, or disable listening to the sensor changes. - A
     * text view where current sensor value is displayed.
     */
    public class MonitoredSensor {
        /** Sensor to monitor. */
        private final Sensor mSensor;
        /** The sensor name to display in the UI. */
        private String mUiName = "";
        /** Text view displaying the value of the sensor. */
        private String mValue = null;
        /** Emulator-friendly name for the sensor. */
        private String mEmulatorFriendlyName;
        /** Formats string to show in the TextView. */
        private String mTextFmt;
        /** Sensor values. */
        private float[] mValues = new float[3];
        /**
         * Enabled state. This state is controlled by the emulator, that
         * maintains its own list of sensors. So, if a sensor is missing, or is
         * disabled in the emulator, it should be disabled in this application.
         */
        private boolean mEnabledByEmulator = false;
        /** User-controlled enabled state. */
        private boolean mEnabledByUser = true;
        /** Sensor event listener for this sensor. */
        private final OurSensorEventListener mListener = new OurSensorEventListener();

        /**
         * Constructs MonitoredSensor instance, and register the listeners.
         *
         * @param sensor Sensor to monitor.
         */
        MonitoredSensor(Sensor sensor) {
            mSensor = sensor;
            mEnabledByUser = true;

            // Set appropriate sensor name depending on the type. Unfortunately,
            // we can't really use sensor.getName() here, since the value it
            // returns (although resembles the purpose) is a bit vaguer than it
            // should be. Also choose an appropriate format for the strings that
            // display sensor's value.
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    mUiName = "Accelerometer";
                    mTextFmt = "%+.2f %+.2f %+.2f";
                    mEmulatorFriendlyName = "acceleration";
                    break;
                case 9: // Sensor.TYPE_GRAVITY is missing in API 7
                    mUiName = "Gravity";
                    mTextFmt = "%+.2f %+.2f %+.2f";
                    mEmulatorFriendlyName = "gravity";
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    mUiName = "Gyroscope";
                    mTextFmt = "%+.2f %+.2f %+.2f";
                    mEmulatorFriendlyName = "gyroscope";
                    break;
                case Sensor.TYPE_LIGHT:
                    mUiName = "Light";
                    mTextFmt = "%.0f";
                    mEmulatorFriendlyName = "light";
                    break;
                case 10: // Sensor.TYPE_LINEAR_ACCELERATION is missing in API 7
                    mUiName = "Linear acceleration";
                    mTextFmt = "%+.2f %+.2f %+.2f";
                    mEmulatorFriendlyName = "linear-acceleration";
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mUiName = "Magnetic field";
                    mTextFmt = "%+.2f %+.2f %+.2f";
                    mEmulatorFriendlyName = "magnetic-field";
                    break;
                case Sensor.TYPE_ORIENTATION:
                    mUiName = "Orientation";
                    mTextFmt = "%+03.0f %+03.0f %+03.0f";
                    mEmulatorFriendlyName = "orientation";
                    break;
                case Sensor.TYPE_PRESSURE:
                    mUiName = "Pressure";
                    mTextFmt = "%.0f";
                    mEmulatorFriendlyName = "pressure";
                    break;
                case Sensor.TYPE_PROXIMITY:
                    mUiName = "Proximity";
                    mTextFmt = "%.0f";
                    mEmulatorFriendlyName = "proximity";
                    break;
                case 11: // Sensor.TYPE_ROTATION_VECTOR is missing in API 7
                    mUiName = "Rotation";
                    mTextFmt = "%+.2f %+.2f %+.2f";
                    mEmulatorFriendlyName = "rotation";
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    mUiName = "Temperature";
                    mTextFmt = "%.0f";
                    mEmulatorFriendlyName = "temperature";
                    break;
                default:
                    mUiName = "<Unknown>";
                    mTextFmt = "N/A";
                    mEmulatorFriendlyName = "unknown";
                    if (DEBUG) Loge("Unknown sensor type " + mSensor.getType() +
                            " for sensor " + mSensor.getName());
                    break;
            }
        }

        /**
         * Get name for this sensor to display.
         *
         * @return Name for this sensor to display.
         */
        public String getUiName() {
            return mUiName;
        }

        /**
         * Gets current sensor value to display.
         *
         * @return Current sensor value to display.
         */
        public String getValue() {
            if (mValue == null) {
                float[] values = mValues;
                mValue = String.format(mTextFmt, values[0], values[1], values[2]);
            }
            return mValue == null ? "??" : mValue;
        }

        /**
         * Checks if monitoring of this this sensor has been enabled by
         * emulator.
         *
         * @return true if monitoring of this this sensor has been enabled by
         *         emulator, or false if emulator didn't enable this sensor.
         */
        public boolean isEnabledByEmulator() {
            return mEnabledByEmulator;
        }

        /**
         * Checks if monitoring of this this sensor has been enabled by user.
         *
         * @return true if monitoring of this this sensor has been enabled by
         *         user, or false if user didn't enable this sensor.
         */
        public boolean isEnabledByUser() {
            return mEnabledByUser;
        }

        /**
         * Handles checked state change for the associated CheckBox. If check
         * box is checked we will register sensor change listener. If it is
         * unchecked, we will unregister sensor change listener.
         */
        public void onCheckedChanged(boolean isChecked) {
            mEnabledByUser = isChecked;
            if (isChecked) {
                startListening();
            } else {
                stopListening();
            }
        }

        /**
         * Gets sensor type.
         *
         * @return Sensor type as one of the Sensor.TYPE_XXX constants.
         */
        private int getType() {
            return mSensor.getType();
        }

        /**
         * Gets sensor's emulator-friendly name.
         *
         * @return Sensor's emulator-friendly name.
         */
        private String getEmulatorFriendlyName() {
            return mEmulatorFriendlyName;
        }

        /**
         * Starts monitoring the sensor.
         * NOTE: This method is called from outside of the UI thread.
         */
        private void startListening() {
            if (mEnabledByEmulator && mEnabledByUser) {
                if (DEBUG) Log.d(TAG, "+++ Sensor " + getEmulatorFriendlyName() + " is started.");
                mSenMan.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        /**
         * Stops monitoring the sensor.
         * NOTE: This method is called from outside of the UI thread.
         */
        private void stopListening() {
            if (DEBUG) Log.d(TAG, "--- Sensor " + getEmulatorFriendlyName() + " is stopped.");
            mSenMan.unregisterListener(mListener);
        }

        /**
         * Enables sensor events.
         * NOTE: This method is called from outside of the UI thread.
         */
        private void enableSensor() {
            if (DEBUG) Log.d(TAG, ">>> Sensor " + getEmulatorFriendlyName() + " is enabled.");
            mEnabledByEmulator = true;
            mValue = null;

            Message msg = Message.obtain();
            msg.what = SENSOR_STATE_CHANGED;
            msg.obj = MonitoredSensor.this;
            notifyUiHandlers(msg);
        }

        /**
         * Disables sensor events.
         * NOTE: This method is called from outside of the UI thread.
         */
        private void disableSensor() {
            if (DEBUG) Log.w(TAG, "<<< Sensor " + getEmulatorFriendlyName() + " is disabled.");
            mEnabledByEmulator = false;
            mValue = "Disabled by emulator";

            Message msg = Message.obtain();
            msg.what = SENSOR_STATE_CHANGED;
            msg.obj = MonitoredSensor.this;
            notifyUiHandlers(msg);
        }

        private class OurSensorEventListener implements SensorEventListener {
            /** Last update's time-stamp in local thread millisecond time. */
            private long mLastUpdateTS = 0;
            /** Last display update time-stamp. */
            private long mLastDisplayTS = 0;
            /** Preallocated buffer for change notification message. */
            private final ByteBuffer mChangeMsg = ByteBuffer.allocate(64);

            /**
             * Handles "sensor changed" event.
             * This is an implementation of the SensorEventListener interface.
             */
            @Override
            public void onSensorChanged(SensorEvent event) {
                long now = SystemClock.elapsedRealtime();

                long deltaMs = 0;
                if (mLastUpdateTS != 0) {
                    deltaMs = now - mLastUpdateTS;
                    if (mUpdateTargetMs > 0 && deltaMs < mUpdateTargetMs) {
                        // New sample is arriving too fast. Discard it.
                        return;
                    }
                }

                // Format and post message for the emulator.
                float[] values = event.values;
                final int len = values.length;

                mChangeMsg.order(getEndian());
                mChangeMsg.position(0);
                mChangeMsg.putInt(getType());
                mChangeMsg.putFloat(values[0]);
                if (len > 1) {
                    mChangeMsg.putFloat(values[1]);
                    if (len > 2) {
                        mChangeMsg.putFloat(values[2]);
                    }
                }
                postMessage(ProtocolConstants.SENSORS_SENSOR_EVENT, mChangeMsg);

                // Computes average update time for this sensor and average globally.
                if (mLastUpdateTS != 0) {
                    if (mGlobalAvgUpdateMs != 0) {
                        mGlobalAvgUpdateMs = (mGlobalAvgUpdateMs + deltaMs) / 2;
                    } else {
                        mGlobalAvgUpdateMs = deltaMs;
                    }
                }
                mLastUpdateTS = now;

                // Update the UI for the sensor, with a static throttling of 10 fps max.
                if (hasUiHandler()) {
                    if (mLastDisplayTS != 0) {
                        long uiDeltaMs = now - mLastDisplayTS;
                        if (uiDeltaMs < 1000 / 4 /* 4fps in ms */) {
                            // Skip this UI update
                            return;
                        }
                    }
                    mLastDisplayTS = now;

                    mValues[0] = values[0];
                    if (len > 1) {
                        mValues[1] = values[1];
                        if (len > 2) {
                            mValues[2] = values[2];
                        }
                    }
                    mValue = null;

                    Message msg = Message.obtain();
                    msg.what = SENSOR_DISPLAY_MODIFIED;
                    msg.obj = MonitoredSensor.this;
                    notifyUiHandlers(msg);
                }

                if (DEBUG) {
                    long now2 = SystemClock.elapsedRealtime();
                    long processingTimeMs = now2 - now;
                    Log.d(TAG, String.format("glob %d - local %d > target %d - processing %d -- %s",
                            mGlobalAvgUpdateMs, deltaMs, mUpdateTargetMs, processingTimeMs,
                            mSensor.getName()));
                }
            }

            /**
             * Handles "sensor accuracy changed" event.
             * This is an implementation of the SensorEventListener interface.
             */
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }
    } // MonitoredSensor

    /***************************************************************************
     * Logging wrappers
     **************************************************************************/

    private void Loge(String log) {
        mService.addError(log);
        Log.e(TAG, log);
    }
}
