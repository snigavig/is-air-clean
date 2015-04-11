package com.goodcodeforfun.isairclean.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.goodcodeforfun.isairclean.IsAirCleanApplication;
import com.goodcodeforfun.isairclean.R;
import com.goodcodeforfun.isairclean.Util;


public class SplashActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Button button = (Button) findViewById(R.id.splashButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(v);
            }
        });
    }

    @Override
    protected void onResume() {
        if (!Util.getPreferredLocation(this).isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        super.onResume();
        IsAirCleanApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        IsAirCleanApplication.activityPaused();
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        intent.putExtra(SettingsActivity.IS_SPLASH_FLAG, true);
        startActivity(intent);
        finish();
    }
}
