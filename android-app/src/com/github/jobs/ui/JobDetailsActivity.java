package com.github.jobs.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.persistence.Persistence;
import com.codeslap.topy.BaseActivity;
import com.github.jobs.R;
import com.github.jobs.utils.ShareHelper;
import com.telly.wasp.BitmapHelper;
import com.telly.wasp.CallbackBitmapObserver;

/**
 * @author cristian
 */
public class JobDetailsActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_JOB_ID = "job_id";
    private static final int SHARE = 484;
    private static final int HOW_TO_APPLY = 4734;
    public static final String FULL_TIME = "Full Time";
    private Job mJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_details_activity);

        String jobId = getIntent().getStringExtra(EXTRA_JOB_ID);
        mJob = new Job();
        mJob.setId(jobId);
        mJob = Persistence.quick(this).findFirst(mJob);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(mJob.getTitle());

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(Html.fromHtml(mJob.getDescription()));
        description.setMovementMethod(LinkMovementMethod.getInstance());

        TextView company = (TextView) findViewById(R.id.company);
        company.setText(mJob.getCompany());

        TextView companyUrl = (TextView) findViewById(R.id.company_url);
        if (mJob.getCompanyUrl() == null) {
            companyUrl.setVisibility(View.GONE);
        } else {
            SpannableString content = new SpannableString(mJob.getCompanyUrl());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            companyUrl.setText(content);
            companyUrl.setOnClickListener(this);
            companyUrl.setVisibility(View.VISIBLE);
        }

        TextView companyLocation = (TextView) findViewById(R.id.company_location);
        if (mJob.getLocation() == null) {
            companyLocation.setVisibility(View.GONE);
        } else {
            companyLocation.setText(mJob.getLocation());
            companyLocation.setVisibility(View.VISIBLE);
        }

        if (!FULL_TIME.equalsIgnoreCase(mJob.getType())) {
            findViewById(R.id.full_time).setVisibility(View.INVISIBLE);
        }

        setLogoBackground();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, HOW_TO_APPLY, 0, getString(R.string.apply))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, SHARE, 0, R.string.share)
                .setActionProvider(new ShareActionProvider(getSupportActionBar().getThemedContext()))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // Set file with share history to the provider and set the share intent.
        MenuItem actionItem = menu.findItem(SHARE);
        ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        actionProvider.setShareIntent(ShareHelper.getShareIntent(mJob));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case HOW_TO_APPLY:
                startActivity(new Intent(this, HowToApplyDialog.class)
                        .putExtra(HowToApplyDialog.EXTRA_HOW_TO_APPLY, mJob.getHowToApply()));
                return true;
            case android.R.id.home:
                // app icon in Action Bar clicked; go home
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.company_url:
                Intent companyUrl = new Intent(Intent.ACTION_VIEW);
                companyUrl.addCategory(Intent.CATEGORY_BROWSABLE);
                companyUrl.setData(Uri.parse(mJob.getCompanyUrl()));
                startActivity(companyUrl);
                break;
        }
    }


    private void setLogoBackground() {
        if (mJob.getCompanyLogo() == null) {
            return;
        }
        CallbackBitmapObserver rawBitmapObserver = new CallbackBitmapObserver(new CallbackBitmapObserver.BitmapCallback() {
            @Override
            public boolean stillNeedsUrl(String uri) {
                return true;
            }

            @Override
            public void receiveBitmap(String uri, Bitmap bitmap) {
                if (bitmap == null) {
                    return;
                }
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                bitmapDrawable.setAlpha(50);
                bitmapDrawable.setGravity(Gravity.CENTER);
                ImageView background = (ImageView) findViewById(R.id.job_details_background);
                background.setImageDrawable(bitmapDrawable);
            }
        }, mJob.getCompanyLogo(), new Handler());
        BitmapHelper.getInstance().registerBitmapObserver(this, rawBitmapObserver);
    }
}
