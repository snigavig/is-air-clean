package com.goodcodeforfun.isairclean.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.goodcodeforfun.isairclean.R;
import com.goodcodeforfun.isairclean.Util;
import com.goodcodeforfun.isairclean.activities.MainActivity;
import com.goodcodeforfun.isairclean.data.AirContract;

public class SummaryFragmentCurrent extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 2;
    private static final String[] SUMMARY_COLUMNS = {
            AirContract.LocationEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_CARBON_CURRENT,
            AirContract.LocationEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_ENERGY_CURRENT,
            AirContract.LocationEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_INTENSITY_CURRENT,
            AirContract.LocationEntry.COLUMN_FOSSIL_CURRENT,
            AirContract.LocationEntry.COLUMN_NUCLEAR_CURRENT,
            AirContract.LocationEntry.COLUMN_HYDRO_CURRENT,
            AirContract.LocationEntry.COLUMN_RENEWABLE_CURRENT

    };
    private static final int COL_LOCATION_CARBON_CURRENT = 0;
    private static final int COL_LOCATION_ENERGY_CURRENT = 1;
    private static final int COL_LOCATION_INTENSITY_CURRENT = 2;
    private static final int COL_SUMMARY_FOSSIL_CURRENT = 3;
    private static final int COL_SUMMARY_NUCLEAR_CURRENT = 4;
    private static final int COL_SUMMARY_HYDRO_CURRENT = 5;
    private static final int COL_SUMMARY_RENEWABLE_CURRENT = 6;
    public static String initChartString;
    public TextView carbonCurrentView;
    public TextView energyCurrentView;
    public TextView intensityCurrentView;
    private PieGraph mPieChart;
    private Uri mUri;

    public SummaryFragmentCurrent() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        if (!getResources().getBoolean(R.bool.is_tablet)) {
            if (Util.getOrientation(getActivity()) == Configuration.ORIENTATION_PORTRAIT) {
                //        mPieChart.loadUrl("file:///android_asset/www/view");
            } else if (Util.getOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE) {
                //        mPieChart.loadUrl("file:///android_asset/www/view_wide");
            }
        } else {
            //    mPieChart.loadUrl("file:///android_asset/www/view_wide");
        }
        //mPieChart.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary_current, container, false);

        String locationSetting = Util.getPreferredLocation(getActivity());
        mUri = AirContract.LocationEntry.buildLocationBySetting(locationSetting);
        carbonCurrentView = (TextView) rootView.findViewById(R.id.summaryCurrentCarbonTextView);
        energyCurrentView = (TextView) rootView.findViewById(R.id.summaryCurrentEnergyTextView);
        intensityCurrentView = (TextView) rootView.findViewById(R.id.summaryCurrentIntensityTextView);
        MainActivity activity = (MainActivity) getActivity();
        Util.setPanelHeight(getActivity(), rootView.findViewById(R.id.summaryWrapLinearLayout), activity.mLayout);
        Resources res = getResources();
        mPieChart = (PieGraph) rootView.findViewById(R.id.graph);
        mPieChart.setStrokeWidth((int) Util.toPx(getActivity(), 5));
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    SUMMARY_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            Toast.makeText(getActivity(), "Getting data...", Toast.LENGTH_SHORT).show();
            return;
        }

        String carbonCurrentString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_LOCATION_CARBON_CURRENT))));
        String energyCurrentString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_LOCATION_ENERGY_CURRENT))));
        String intensityCurrentString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_LOCATION_INTENSITY_CURRENT))));
        carbonCurrentView.setText(carbonCurrentString);
        energyCurrentView.setText(energyCurrentString);
        intensityCurrentView.setText(intensityCurrentString);
        MainActivity.mShareString = "Intensity: " + intensityCurrentString + " kg CO2 per MWh";
        MainActivity.mShareActionProvider.setShareIntent(getShareIntent());

        mPieChart.removeSlices();
        PieSlice slice = new PieSlice();
        slice.setColor(getResources().getColor(R.color.pie_fossil));
        slice.setValue(data.getFloat(COL_SUMMARY_FOSSIL_CURRENT) * 10);
        slice.setTitle(String.valueOf((int) data.getFloat(COL_SUMMARY_FOSSIL_CURRENT) * 100));
        mPieChart.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(getResources().getColor(R.color.pie_nuclear));
        slice.setValue(data.getFloat(COL_SUMMARY_NUCLEAR_CURRENT) * 10);
        slice.setTitle(String.valueOf((int) data.getFloat(COL_SUMMARY_NUCLEAR_CURRENT) * 100));
        mPieChart.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(getResources().getColor(R.color.pie_hydro));
        slice.setValue(data.getFloat(COL_SUMMARY_HYDRO_CURRENT) * 10);
        slice.setTitle(String.valueOf((int) data.getFloat(COL_SUMMARY_HYDRO_CURRENT) * 100));
        mPieChart.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(getResources().getColor(R.color.pie_renewable));
        slice.setValue(data.getFloat(COL_SUMMARY_RENEWABLE_CURRENT) * 10);
        slice.setTitle(String.valueOf((int) data.getFloat(COL_SUMMARY_RENEWABLE_CURRENT) * 100));
        mPieChart.addSlice(slice);
        for (PieSlice s : mPieChart.getSlices())
            s.setGoalValue(s.getValue() * 10);
        mPieChart.setDuration(1500);
        mPieChart.setInterpolator(new BounceInterpolator());
        mPieChart.animateToGoalValues();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        String ellipsis = "...";
        carbonCurrentView.setText(ellipsis);
        energyCurrentView.setText(ellipsis);
        intensityCurrentView.setText(ellipsis);
        MainActivity.mShareString = "Intensity: " + ellipsis + " kg CO2 per MWh";
        MainActivity.mShareActionProvider.setShareIntent(getShareIntent());
        mPieChart.removeSlices();
    }

    private Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareString = MainActivity.mShareString + " #IsAirClean in " + MainActivity.mLocationString + "?";
        intent.putExtra(Intent.EXTRA_TEXT, shareString);

        //noinspection deprecation
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent;
    }
}
