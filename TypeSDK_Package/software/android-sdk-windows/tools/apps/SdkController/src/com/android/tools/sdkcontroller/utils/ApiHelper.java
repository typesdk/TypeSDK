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

package com.android.tools.sdkcontroller.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

/**
 * Helper to deal with methods only available at certain API levels.
 * Users should get use {@link ApiHelper#get()} to retrieve a singleton
 * and then call the methods they desire. If the method is not available
 * on the current API level, a stub or a nop will be used instead.
 */
@TargetApi(7)
public class ApiHelper {

    private static ApiHelper sApiHelper = null;

    /** Creates a new ApiHelper adapted to the current runtime API level. */
    public static ApiHelper get() {
        if (sApiHelper == null) {
            if (Build.VERSION.SDK_INT >= 11) {
                sApiHelper = new ApiHelper_11();
            } else {
                sApiHelper = new ApiHelper();
            }
        }

        return sApiHelper;
    }

    protected ApiHelper() {
    }

    /**
     * Applies {@link View#setSystemUiVisibility(int)}, available only starting with API 11.
     * Does nothing for API < 11.
     */
    public void View_setSystemUiVisibility(View view, int visibility) {
        // nop
    }
}
