package com.goodcodeforfun.isairclean;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.goodcodeforfun.isairclean.sync.AirSyncAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;
import java.util.Vector;


public class MainActivity extends ActionBarActivity implements SummaryFragmentCurrent.OnFragmentInteractionListener, SummaryFragmentFuture.OnFragmentInteractionListener, ObjectListFragment.OnFragmentInteractionListener {


    private SlidingUpPanelLayout mLayout;

    private PagerAdapter mPagerAdapter;
    private CirclePageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initialisePaging();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    //.add(R.id.summary_fragment, new SummaryFragment())
                    .add(R.id.list_fragment, new ObjectListFragment())
                    .commit();
        }

        AirSyncAdapter.initializeSyncAdapter(this);
        AirSyncAdapter.syncImmediately(this);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
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
    private void initialisePaging() {

        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, SummaryFragmentCurrent.class.getName()));
        fragments.add(Fragment.instantiate(this, SummaryFragmentFuture.class.getName()));
        this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        //
        ViewPager pager = (ViewPager)super.findViewById(R.id.viewPager);
        pager.setAdapter(this.mPagerAdapter);
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
    }
}
