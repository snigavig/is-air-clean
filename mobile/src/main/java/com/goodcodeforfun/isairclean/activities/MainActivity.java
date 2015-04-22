package com.goodcodeforfun.isairclean.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.goodcodeforfun.isairclean.IsAirCleanApplication;
import com.goodcodeforfun.isairclean.R;
import com.goodcodeforfun.isairclean.Util;
import com.goodcodeforfun.isairclean.adapters.PagerAdapter;
import com.goodcodeforfun.isairclean.fragments.ObjectListFragment;
import com.goodcodeforfun.isairclean.fragments.SummaryFragmentCurrent;
import com.goodcodeforfun.isairclean.fragments.SummaryFragmentFuture;
import com.goodcodeforfun.isairclean.sync.AirSyncAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;
import java.util.Vector;

import static android.widget.Toast.makeText;


public class MainActivity extends
        ActionBarActivity implements
        ObjectListFragment.OnFragmentInteractionListener, ObjectListFragment.Callback,
        ShareActionProvider.OnShareTargetSelectedListener {

    private static final String OBJECTLISTFRAGMENT_TAG = "DFTAG";
    public static ShareActionProvider mShareActionProvider;
    public static String mShareString;
    public static String mLocationString;
    public SharedPreferences prefs;
    private SlidingUpPanelLayout mLayout;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onPause() {
        super.onPause();
        IsAirCleanApplication.activityPaused();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            makeText(this, "Sorry, there is no internet connection", Toast.LENGTH_LONG).show();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initialisePaging();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.list_fragment, new ObjectListFragment(), OBJECTLISTFRAGMENT_TAG)
                    .commit();
        }

        AirSyncAdapter.initializeSyncAdapter(this);
        AirSyncAdapter.syncImmediately(this);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLocationString = Util.getPreferredLocation(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setOnShareTargetSelectedListener(this);
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

        if (id == R.id.action_about) {
            Intent startAboutActivityIntent = new Intent(this, AboutActivity.class);
            startActivity(startAboutActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IsAirCleanApplication.activityResumed();
        String location = Util.getPreferredLocation(this);
        if (location != null && !location.equals(mLocationString)) {
            ObjectListFragment olf = (ObjectListFragment) getSupportFragmentManager().findFragmentByTag(OBJECTLISTFRAGMENT_TAG);
            if (null != olf) {
                olf.onLocationChanged();
            }
            mPagerAdapter.notifyDataSetChanged();
            mLocationString = location;
        }
        if (Util.getPreferredLocation(this).isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(contentUri);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //fragment interaction stab,  not needed at this point
    }


    @Override
    public boolean onShareTargetSelected(ShareActionProvider source,
                                         Intent intent) {
        this.startActivity(intent);
        return true;
    }

    private void initialisePaging() {
        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, SummaryFragmentCurrent.class.getName()));
        fragments.add(Fragment.instantiate(this, SummaryFragmentFuture.class.getName()));
        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager) super.findViewById(R.id.viewPager);
        pager.setAdapter(this.mPagerAdapter);
        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
    }

}
