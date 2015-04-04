package com.goodcodeforfun.isairclean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.goodcodeforfun.isairclean.sync.AirSyncAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;
import java.util.Vector;


public class MainActivity extends
        ActionBarActivity implements
        SummaryFragmentCurrent.OnFragmentInteractionListener, SummaryFragmentFuture.OnFragmentInteractionListener,
        ObjectListFragment.OnFragmentInteractionListener, ObjectListFragment.Callback, ShareActionProvider.OnShareTargetSelectedListener{

    private static final String OBJECTLISTFRAGMENT_TAG = "DFTAG";
    private SlidingUpPanelLayout mLayout;
    private PagerAdapter mPagerAdapter;
    private CirclePageIndicator mIndicator;
    public SharedPreferences prefs;
    public static ShareActionProvider mShareActionProvider;
    public static String mShareString;
    public static String mLocationString;

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Util.getPreferredLocation(this);
        if (location != null && !location.equals(mLocationString)) {
            ObjectListFragment olf = (ObjectListFragment)getSupportFragmentManager().findFragmentByTag(OBJECTLISTFRAGMENT_TAG);
            if ( null != olf ) {
                olf.onLocationChanged();
            }
            mPagerAdapter.notifyDataSetChanged();
            mLocationString = location;
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
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, SummaryFragmentCurrent.class.getName()));
        fragments.add(Fragment.instantiate(this, SummaryFragmentFuture.class.getName()));
        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager)super.findViewById(R.id.viewPager);
        pager.setAdapter(this.mPagerAdapter);
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
    }

}
