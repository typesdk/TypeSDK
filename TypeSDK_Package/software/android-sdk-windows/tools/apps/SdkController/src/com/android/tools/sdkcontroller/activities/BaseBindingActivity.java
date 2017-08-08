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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.android.tools.sdkcontroller.service.ControllerService;
import com.android.tools.sdkcontroller.service.ControllerService.ControllerBinder;
import com.android.tools.sdkcontroller.service.ControllerService.ControllerListener;

/**
 * Base activity class that knows how to bind and unbind from the
 * {@link ControllerService}.
 */
public abstract class BaseBindingActivity extends Activity {

    public static String TAG = BaseBindingActivity.class.getSimpleName();
    private static boolean DEBUG = true;
    private ServiceConnection mServiceConnection;
    private ControllerBinder mServiceBinder;

    /**
     * Returns the binder. Activities can use that to query the controller service.
     * @return An existing {@link ControllerBinder}.
     *   The binder is only valid between calls {@link #onServiceConnected()} and
     *   {@link #onServiceDisconnected()}. Returns null when not valid.
     */
    public ControllerBinder getServiceBinder() {
        return mServiceBinder;
    }

    /**
     * Called when the activity resumes.
     * This automatically binds to the service, starting it as needed.
     * <p/>
     * Since on resume we automatically bind to the service, the {@link ServiceConnection}
     * will is restored and {@link #onServiceConnected()} is called as necessary.
     * Derived classes that need to initialize anything that is related to the service
     * (e.g. getting their handler) should thus do so in {@link #onServiceConnected()} and
     * <em>not</em> in {@link #onResume()} -- since binding to the service is asynchronous
     * there is <em>no</em> guarantee that {@link #getServiceBinder()} returns non-null
     * when this call finishes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        bindToService();
    }

    /**
     * Called when the activity is paused.
     * This automatically unbinds from the service but does not stop it.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unbindFromService();
    }

    // ----------

    /**
     * Called when binding to the service to get the activity's {@link ControllerListener}.
     * @return A new non-null {@link ControllerListener}.
     */
    protected abstract ControllerListener createControllerListener();

    /**
     * Called by the service once the activity is connected (bound) to it.
     * <p/>
     * When this is called, {@link #getServiceBinder()} returns a non-null binder that
     * can be used by the activity to control the service.
     */
    protected abstract void onServiceConnected();

    /**
     * Called by the service when it is forcibly disconnected OR when we know
     * we're unbinding the service.
     * <p/>
     * When this is called, {@link #getServiceBinder()} returns a null binder and
     * the activity should stop using that binder and remove any reference to it.
     */
    protected abstract void onServiceDisconnected();

    /**
     * Starts the service and binds to it.
     */
    protected void bindToService() {
        if (mServiceConnection == null) {
            final ControllerListener listener = createControllerListener();

            mServiceConnection = new ServiceConnection() {
                /**
                 * Called when the service is connected.
                 * Allows us to retrieve the binder to talk to the service.
                 */
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (DEBUG) Log.d(TAG, "Activity connected to service");
                    mServiceBinder = (ControllerBinder) service;
                    mServiceBinder.addControllerListener(listener);
                    BaseBindingActivity.this.onServiceConnected();
                }

                /**
                 * Called when the service got disconnected, e.g. because it crashed.
                 * This is <em>not</em> called when we unbind from the service.
                 */
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    if (DEBUG) Log.d(TAG, "Activity disconnected from service");
                    mServiceBinder = null;
                    BaseBindingActivity.this.onServiceDisconnected();
                }
            };
        }

        // Start service so that it doesn't stop when we unbind
        if (DEBUG) Log.d(TAG, "start requested & bind service");
        Intent service = new Intent(this, ControllerService.class);
        startService(service);
        bindService(service,
                mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbinds from the service but does not actually stop the service.
     * This lets us have it run in the background even if this isn't the active activity.
     */
    protected void unbindFromService() {
        if (mServiceConnection != null) {
            if (DEBUG) Log.d(TAG, "unbind service");
            mServiceConnection.onServiceDisconnected(null /*name*/);
            unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }
}