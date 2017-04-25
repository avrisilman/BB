package com.domikado.itaxi.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.domikado.itaxi.R;
import com.domikado.itaxi.ui.ads.SplashActivity;
import com.domikado.itaxi.ui.ads.VacantActivity;
import com.domikado.itaxi.ui.settings.ContentManagerActivity;
import com.domikado.itaxi.ui.settings.SettingPanelActivity;

public class BaseSettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isAppInLockTaskMode())
            stopLockTask();
    }

    protected void setupTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                VacantActivity.start(this);
                return true;
            case R.id.opt_refresh:
                super.finish();
                startActivity(getIntent());
                return true;
            case R.id.opt_content_manager:
                ContentManagerActivity.start(this);
                return true;
            case R.id.opt_settings:
                SettingPanelActivity.start(this);
                return true;
            case R.id.opt_start:
                VacantActivity.start(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
