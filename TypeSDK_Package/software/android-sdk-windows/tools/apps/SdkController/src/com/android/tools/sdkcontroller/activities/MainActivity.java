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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.tools.sdkcontroller.R;
import com.android.tools.sdkcontroller.service.ControllerService;
import com.android.tools.sdkcontroller.service.ControllerService.ControllerBinder;
import com.android.tools.sdkcontroller.service.ControllerService.ControllerListener;

/**
 * Main activity. It's the entry point for the application.
 * It allows the user to start/stop the service and see it's current state and errors.
 * It also has buttons to start either the sensor control activity or the multitouch activity.
 */
public class MainActivity extends BaseBindingActivity {

    @SuppressWarnings("hiding")
    public static String TAG = MainActivity.class.getSimpleName();
    private static boolean DEBUG = true;
    private Button mBtnOpenMultitouch;
    private Button mBtnOpenSensors;
    private ToggleButton mBtnToggleService;
    private TextView mTextError;
    private TextView mTextStatus;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTextError  = (TextView) findViewById(R.id.textError);
        mTextStatus = (TextView) findViewById(R.id.textStatus);

        WebView wv = (WebView) findViewById(R.id.webIntro);
        wv.loadUrl("file:///android_asset/intro_help.html");

        setupButtons();
    }

    @Override
    protected void onResume() {
        // BaseBindingActivity.onResume will bind to the service.
        super.onResume();
        updateError();
    }

    @Override
    protected void onPause() {
        // BaseBindingActivity.onResume will unbind from (but not stop) the service.
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (DEBUG) Log.d(TAG, "onBackPressed");
        // If back is pressed, we stop the service automatically.
        // It seems more intuitive that way.
        stopService();
        super.onBackPressed();
    }

    // ----------

    @Override
    protected void onServiceConnected() {
        updateButtons();
    }

    @Override
    protected void onServiceDisconnected() {
        updateButtons();
    }

    @Override
    protected ControllerListener createControllerListener() {
        return new MainControllerListener();
    }

    // ----------

    private void setupButtons() {
        mBtnOpenMultitouch = (Button) findViewById(R.id.btnOpenMultitouch);
        mBtnOpenSensors    = (Button) findViewById(R.id.btnOpenSensors);

        mBtnOpenMultitouch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the multi-touch activity.
                Intent i = new Intent(MainActivity.this, MultiTouchActivity.class);
                startActivity(i);
            }
        });

        mBtnOpenSensors.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sensor activity.
                Intent i = new Intent(MainActivity.this, SensorActivity.class);
                startActivity(i);
            }
        });

        mBtnToggleService = (ToggleButton) findViewById(R.id.toggleService);

        // set initial state
        updateButtons();

        mBtnToggleService.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bindToService();
                    updateButtons();
                } else {
                    stopService();
                    updateButtons();
                }
            }
        });

    }

    private void updateButtons() {
        boolean running = ControllerService.isServiceIsRunning();
        mBtnOpenMultitouch.setEnabled(running);
        mBtnOpenSensors.setEnabled(running);
        mBtnToggleService.setChecked(running);
    }

    /**
     * Unbind and then actually stops the service.
     */
    private void stopService() {
        Intent service = new Intent(this, ControllerService.class);
        unbindFromService();
        if (DEBUG) Log.d(TAG, "stop service requested");
        stopService(service);
    }

    private class MainControllerListener implements ControllerListener {
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
                    updateStatus();
                }
            });
        }
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

    private void updateStatus() {
        ControllerBinder binder = getServiceBinder();
        boolean connected = binder == null ? false : binder.isEmuConnected();
        mTextStatus.setText(
                getText(connected ? R.string.main_service_status_connected
                                  : R.string.main_service_status_disconnected));

    }
}