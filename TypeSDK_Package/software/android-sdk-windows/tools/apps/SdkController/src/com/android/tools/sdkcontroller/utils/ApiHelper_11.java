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
import android.view.View;

/**
 * API 11: support View_setSystemUiVisibility
 */
@TargetApi(11)
class ApiHelper_11 extends ApiHelper {

    /**
     * Applies {@link View#setSystemUiVisibility(int)}, available only starting with API 11.
     * Does nothing for API < 11.
     */
    @Override
    public void View_setSystemUiVisibility(View view, int visibility) {
        view.setSystemUiVisibility(visibility);
    }
}
