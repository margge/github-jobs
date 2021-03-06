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

package com.github.jobs.resolver;

import android.os.Bundle;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;
import com.github.jobs.bean.AboutMeUser;
import com.github.jobs.templates.fetcher.AboutMeFetcher;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutMeResolver extends CallResolver {
    public static final String PARAM_USERNAME = "com.github.jobs.param.username";
    public static final String RESULT_USER = "com.github.jobs.result.user";

    private AboutMeUser mAboutMeUser;

    @Override
    protected void updateData() {
        Bundle parameters = getParameters();
        String username = parameters.getString(PARAM_USERNAME);

        AboutMeFetcher aboutMeFetcher = new AboutMeFetcher();
        mAboutMeUser = aboutMeFetcher.getAboutMeUser(username);
    }

    @Override
    protected void prepareResult() {
        if (mAboutMeUser == null || mAboutMeUser.getServices() == null || mAboutMeUser.getServices().length == 0) {
            setResultCode(Groundy.STATUS_ERROR);
            return;
        }
        // pack the result in an parcelable
        Bundle resultData = getResultData();
        resultData.putParcelable(RESULT_USER, mAboutMeUser);
        setResultCode(Groundy.STATUS_FINISHED);
    }
}
