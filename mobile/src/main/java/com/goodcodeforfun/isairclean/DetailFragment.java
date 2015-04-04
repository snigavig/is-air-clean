package com.goodcodeforfun.isairclean;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goodcodeforfun.isairclean.data.AirContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 1;
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    private static final String[] OBJECT_COLUMNS = {
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry._ID,
            AirContract.ObjectEntry.COLUMN_NAME,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_CARBON_CURRENT,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_ENERGY_CURRENT,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_INTENSITY_CURRENT,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_CARBON_FUTURE,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_ENERGY_FUTURE,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_INTENSITY_FUTURE
    };

    private static final int COL_OBJECT_CARBON_CURRENT = 2;
    private static final int COL_OBJECT_ENERGY_CURRENT = 3;
    private static final int COL_OBJECT_INTENSITY_CURRENT = 4;
    private static final int COL_OBJECT_CARBON_FUTURE = 5;
    private static final int COL_OBJECT_ENERGY_FUTURE = 6;
    private static final int COL_OBJECT_INTENSITY_FUTURE = 7;

    public TextView carbonCurrentView;
    public TextView energyCurrentView;
    public TextView intensityCurrentView;
    public TextView carbonFutureView;
    public TextView energyFutureView;
    public TextView intensityFutureView;


    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        //nameView = (TextView) rootView.findViewById(R.id.deta);//TODO: implement name
        carbonCurrentView = (TextView) rootView.findViewById(R.id.detailCurrentCarbonTextView);
        energyCurrentView = (TextView) rootView.findViewById(R.id.detailCurrentEnergyTextView);
        intensityCurrentView = (TextView) rootView.findViewById(R.id.detailCurrentIntensityTextView);
        carbonFutureView = (TextView) rootView.findViewById(R.id.detailFutureCarbonTextView);
        energyFutureView = (TextView) rootView.findViewById(R.id.detailFutureEnergyTextView);
        intensityFutureView = (TextView) rootView.findViewById(R.id.detailFutureIntensityTextView);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( mUri != null ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    OBJECT_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }

        String carbonCurrentString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_OBJECT_CARBON_CURRENT))));
        String energyCurrentString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_OBJECT_ENERGY_CURRENT))));
        String intensityCurrentString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_OBJECT_INTENSITY_CURRENT))));
        String carbonFutureString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_OBJECT_CARBON_FUTURE))));
        String energyFutureString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_OBJECT_ENERGY_FUTURE))));
        String intensityFutureString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_OBJECT_INTENSITY_FUTURE))));
        carbonCurrentView.setText(carbonCurrentString);
        energyCurrentView.setText(energyCurrentString);
        intensityCurrentView.setText(intensityCurrentString);
        carbonFutureView.setText(carbonFutureString);
        energyFutureView.setText(energyFutureString);
        intensityFutureView.setText(intensityFutureString);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
