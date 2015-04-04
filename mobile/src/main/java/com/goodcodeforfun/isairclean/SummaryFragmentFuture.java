package com.goodcodeforfun.isairclean;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.goodcodeforfun.isairclean.data.AirContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SummaryFragmentFuture.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SummaryFragmentFuture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragmentFuture extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WebView mWebView;

    public TextView carbonFutureView;
    public TextView energyFutureView;
    public TextView intensityFutureView;
    public static String initChartString;
    private OnFragmentInteractionListener mListener;

    private static final int LOADER_ID = 3;

    private Uri mUri;

    private static final String[] SUMMARY_COLUMNS = {
            AirContract.LocationEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_CARBON_FUTURE,
            AirContract.LocationEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_ENERGY_FUTURE,
            AirContract.LocationEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_INTENSITY_FUTURE,
            AirContract.LocationEntry.COLUMN_FOSSIL_FUTURE,
            AirContract.LocationEntry.COLUMN_NUCLEAR_FUTURE,
            AirContract.LocationEntry.COLUMN_HYDRO_FUTURE,
            AirContract.LocationEntry.COLUMN_RENEWABLE_FUTURE

    };

    private static final int COL_LOCATION_CARBON_FUTURE = 0;
    private static final int COL_LOCATION_ENERGY_FUTURE = 1;
    private static final int COL_LOCATION_INTENSITY_FUTURE = 2;
    private static final int COL_SUMMARY_FOSSIL_FUTURE = 3;
    private static final int COL_SUMMARY_NUCLEAR_FUTURE = 4;
    private static final int COL_SUMMARY_HYDRO_FUTURE = 5;
    private static final int COL_SUMMARY_RENEWABLE_FUTURE = 6;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SummaryFragmentFuture.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragmentFuture newInstance(String param1, String param2) {
        SummaryFragmentFuture fragment = new SummaryFragmentFuture();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SummaryFragmentFuture() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary_future, container, false);

        String locationSetting = Util.getPreferredLocation(getActivity());
        mUri = AirContract.LocationEntry.buildLocationBySetting(locationSetting);
        carbonFutureView = (TextView) rootView.findViewById(R.id.summaryFutureCarbonTextView);
        energyFutureView = (TextView) rootView.findViewById(R.id.summaryFutureEnergyTextView);
        intensityFutureView = (TextView) rootView.findViewById(R.id.summaryFutureIntensityTextView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mWebView = (WebView) getActivity().findViewById(R.id.pieChartFutureWebView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (!getResources().getBoolean(R.bool.is_tablet)) {
            if (Util.getOrientation(getActivity()) == Configuration.ORIENTATION_PORTRAIT) {
                mWebView.loadUrl("file:///android_asset/www/pie_chart_web_view.html");
            } else if (Util.getOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE) {
                mWebView.loadUrl("file:///android_asset/www/pie_chart_web_view_wide.html");
            }
        } else {
            mWebView.loadUrl("file:///android_asset/www/pie_chart_web_view_wide.html");
        }
        mWebView.setBackgroundColor(Color.TRANSPARENT);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( mUri != null ) {
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
        if (!data.moveToFirst()) { return; }

        String carbonFutureString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_LOCATION_CARBON_FUTURE))));
        String energyFutureString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_LOCATION_ENERGY_FUTURE))));
        String intensityFutureString = String.format("%,d", Long.parseLong(String.valueOf(data.getInt(COL_LOCATION_INTENSITY_FUTURE))));
        carbonFutureView.setText(carbonFutureString);
        energyFutureView.setText(energyFutureString);
        intensityFutureView.setText(intensityFutureString);
        initChartString = String.format("javascript: window.initChart(%f, %f, %f, %f);",
                data.getFloat(COL_SUMMARY_FOSSIL_FUTURE)*100,
                data.getFloat(COL_SUMMARY_NUCLEAR_FUTURE)*100,
                data.getFloat(COL_SUMMARY_HYDRO_FUTURE)*100,
                data.getFloat(COL_SUMMARY_RENEWABLE_FUTURE)*100);


        mWebView.loadUrl(initChartString);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl(initChartString); //
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    void onLocationChanged() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

}
