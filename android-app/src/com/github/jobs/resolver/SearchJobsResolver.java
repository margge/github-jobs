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
import com.github.jobs.api.GithubJobsApi;
import com.github.bean.Job;
import com.github.bean.Search;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.bean.SearchesAndJobs;

import java.util.ArrayList;
import java.util.List;

public class SearchJobsResolver extends CallResolver {

    private ArrayList<Job> mJobs;
    public static final String EXTRA_SEARCH_PACK = "com.github.jobs.extra_search_pack";
    public static final String DATA_JOBS = "com.github.jobs.data.jobs";
    public static final String DATA_SEARCH_PACK = "com.github.jobs.data.search_pack";

    @Override
    protected void updateData() {
        // get parameters
        Bundle params = getParameters();
        SearchPack searchPack = (SearchPack) params.getParcelable(EXTRA_SEARCH_PACK);

        // configure search
        Search.Builder builder = new Search.Builder();
        builder.setSearch(searchPack.getSearch());
        builder.setLocation(searchPack.getLocation());
        builder.setFullTime(searchPack.isFullTime());
        if (searchPack.getPage() > 0) {
            builder.setPage(searchPack.getPage());
        }

        // execute search
        Search search = builder.createSearch();
        List<Job> jobsList = GithubJobsApi.search(search);
        if (jobsList == null) {
            return;
        }

        mJobs = new ArrayList<Job>(jobsList);
        SqlAdapter sqlAdapter = Persistence.getAdapter(getContext());
        // delete old content
        if (searchPack.getPage() == 0 && mJobs.size() > 0) {
            if (searchPack.isDefault()) {
                sqlAdapter.delete(Job.class, null, null);
            } else {
                SearchesAndJobs sample = new SearchesAndJobs();
                sample.setSearchHashCode(searchPack.hashCode());
                sqlAdapter.delete(sample);
            }
        }
        // if the search is not the default one, references to cache table too
        if (!searchPack.isDefault()) {
            List<SearchesAndJobs> searchCaches = new ArrayList<SearchesAndJobs>();
            for (Job job : mJobs) {
                SearchesAndJobs searchesAndJobs = new SearchesAndJobs();
                searchesAndJobs.setSearchHashCode(searchPack.hashCode());
                searchesAndJobs.setJobId(job.getId());
                searchCaches.add(searchesAndJobs);
            }
            sqlAdapter.storeCollection(searchCaches, null);
        }
        // persist all data to be able to use it later
        sqlAdapter.storeCollection(mJobs, null);
    }

    @Override
    protected void prepareResult() {
        SearchPack searchPack = (SearchPack) getParameters().getParcelable(EXTRA_SEARCH_PACK);
        Bundle resultData = getResultData();
        resultData.putParcelable(DATA_SEARCH_PACK, searchPack);
        if (mJobs == null) {
            return;
        }

        resultData.putParcelableArrayList(DATA_JOBS, mJobs);
        setResultCode(Groundy.STATUS_FINISHED);
    }

    @Override
    protected boolean keepWifiOn() {
        return true;
    }
}
