package com.goodcodeforfun.isairclean;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;
import android.widget.Toast;

import com.goodcodeforfun.isairclean.sync.AirSyncAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MainActivity extends ActionBarActivity implements SummaryFragment.OnFragmentInteractionListener, ObjectListFragment.OnFragmentInteractionListener {


    private SlidingUpPanelLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.summary_fragment, new SummaryFragment())
                    .add(R.id.list_fragment, new ObjectListFragment())
                    .commit();
        }

        AirSyncAdapter.initializeSyncAdapter(this);
        AirSyncAdapter.syncImmediately(this);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
//        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
//            @Override
//            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i("PanelSlide", "onPanelSlide, offset " + slideOffset);
//            }
//
//
//            @Override
//            public void onPanelExpanded(View panel) {
//                Log.i("PanelSlide", "onPanelExpanded");
//
//
//            }
//
//
//            @Override
//            public void onPanelCollapsed(View panel) {
//                Log.i("PanelSlide", "onPanelCollapsed");
//
//
//            }
//
//
//            @Override
//            public void onPanelAnchored(View panel) {
//                Log.i("PanelSlide", "onPanelAnchored");
//            }
//
//
//            @Override
//            public void onPanelHidden(View panel) {
//                Log.i("PanelSlide", "onPanelHidden");
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //fragment interaction stab,  not needed at this point
    }

}
