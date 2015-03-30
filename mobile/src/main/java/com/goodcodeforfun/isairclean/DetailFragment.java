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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private static final int LOADER_ID = 1;
    static final String DETAIL_URI = "URI";
    static final String DETAIL_ID = "ID";
    private Uri mUri;

    private static final String[] OBJECT_COLUMNS = {
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry._ID,
            AirContract.ObjectEntry.COLUMN_NAME,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_CARBON_CURRENT,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_ENERGY_CURRENT,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_INTENSITY_CURRENT,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_CARBON_FUTURE,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_ENERGY_FUTURE,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_INTENSITY_FUTURE,
    };

    private static final int COL_OBJECT_NAME = 1;
    private static final int COL_OBJECT_CARBON_CURRENT = 2;
    private static final int COL_OBJECT_ENERGY_CURRENT = 3;
    private static final int COL_OBJECT_INTENSITY_CURRENT = 4;
    private static final int COL_OBJECT_CARBON_FUTURE = 5;
    private static final int COL_OBJECT_ENERGY_FUTURE = 6;
    private static final int COL_OBJECT_INTENSINTY_FUTURE = 7;

    public TextView carbonCurrentView;
    public TextView energyCurrentView;
    public TextView intensityCurrentView;
    public TextView carbonFutureView;
    public TextView energyFutureView;
    public TextView intensityFutureView;
    public TextView nameView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        String nameString = data.getString(COL_OBJECT_NAME);
        String carbonCurrentString = String.valueOf(data.getFloat(COL_OBJECT_CARBON_CURRENT));
        String energyCurrentString = String.valueOf(data.getFloat(COL_OBJECT_ENERGY_CURRENT));
        String intensityCurrentString = String.valueOf(data.getFloat(COL_OBJECT_INTENSITY_CURRENT));
        String carbonFutureString = String.valueOf(data.getFloat(COL_OBJECT_CARBON_FUTURE));
        String energyFutureString = String.valueOf(data.getFloat(COL_OBJECT_ENERGY_FUTURE));
        String intensityFutureString = String.valueOf(data.getFloat(COL_OBJECT_INTENSINTY_FUTURE));
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

}
