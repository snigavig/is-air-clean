package com.goodcodeforfun.isairclean;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.goodcodeforfun.isairclean.sync.AirSyncAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


public class MainActivity extends
        ActionBarActivity implements
        SummaryFragmentCurrent.OnFragmentInteractionListener, SummaryFragmentFuture.OnFragmentInteractionListener,
        ObjectListFragment.OnFragmentInteractionListener, ObjectListFragment.Callback {

    private static final int TIMEOUT_HOURS = 6;
    private static Criteria searchProviderCriteria = new Criteria();

    // Location Criteria
    static {
        searchProviderCriteria.setPowerRequirement(Criteria.POWER_LOW);
        searchProviderCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        searchProviderCriteria.setCostAllowed(false);
    }

    private static final String OBJECTLISTFRAGMENT_TAG = "DFTAG";
    private SlidingUpPanelLayout mLayout;
    private String mLocationString;
    private PagerAdapter mPagerAdapter;
    private CirclePageIndicator mIndicator;
    private Geocoder gcd;
    public SharedPreferences prefs;

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
            double sLat = location.getLatitude();
            double sLon = location.getLongitude();

            String cityName = getCityName(sLat, sLon);
            if (cityName != null){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getResources().getString(R.string.pref_location_key), cityName);
                editor.apply();
            }

            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locManager.removeUpdates(mLocationListener);
        }
    };


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


        gcd = new Geocoder(this, Locale.getDefault());

        AirSyncAdapter.initializeSyncAdapter(this);
        AirSyncAdapter.syncImmediately(this);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLocationString = Util.getPreferredLocation(this);
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(searchProviderCriteria, true);
        Location mLocation = mLocationManager.getLastKnownLocation(provider);
        if (mLocation == null || (SystemClock.elapsedRealtime() - mLocation.getTime()) > TimeUnit.HOURS.toMillis(TIMEOUT_HOURS)) {
            mLocationManager.requestSingleUpdate(provider, mLocationListener, null);
        }
        else {
            mLocationListener.onLocationChanged(mLocation);
        }
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

    //from stackoverflow
    private String getCityName(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }
}
