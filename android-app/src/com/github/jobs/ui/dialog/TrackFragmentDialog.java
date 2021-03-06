/*
 * Copyright 2012 CodeSlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jobs.ui.dialog;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.github.jobs.utils.AnalyticsHelper;

/**
 * @author cristian
 * @version 1.0
 */
public class TrackFragmentDialog extends SherlockFragmentActivity {

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsHelper.getTracker(this).onActivityStarted(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        // This is deprecated on Honeycomb+ but Analytics implementation requires it
        Object obj = super.onRetainCustomNonConfigurationInstance();
        AnalyticsHelper.getTracker(this).onActivityRetainNonConfigurationInstance();
        return obj;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsHelper.getTracker(this).onActivityStopped(this);
    }
}
