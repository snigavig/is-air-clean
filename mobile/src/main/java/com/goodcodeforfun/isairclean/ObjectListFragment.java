package com.goodcodeforfun.isairclean;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.goodcodeforfun.isairclean.data.AirContract;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ObjectListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ObjectListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 0;

    private static final String[] OBJECT_COLUMNS = {
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry._ID,
            AirContract.ObjectEntry.COLUMN_NAME,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_INTENSITY_CURRENT,
            AirContract.ObjectEntry.COLUMN_COORD_LAT,
            AirContract.ObjectEntry.COLUMN_COORD_LONG
    };

    static final int COL_OBJECT_ID = 0;
    static final int COL_OBJECT_NAME = 1;
    static final int COL_OBJECT_INTENSITY_CURRENT = 2;
    static final int COL_OBJECT_COORD_LAT = 3;
    static final int COL_OBJECT_COORD_LONG = 4;

    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private SlidingUpPanelLayout mLayout;
    private OnFragmentInteractionListener mListener;
    private final SlidingUpPanelLayout.PanelSlideListener inactiveSlideListener = new SlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            if(slideOffset == 1){
                isClickable = true;
                mLayout.setDragView(mTextView);
            } else if (slideOffset == 0){
                isClickable = false;
                mLayout.setDragView(mLinearLayout);
            }
        }


        @Override
        public void onPanelExpanded(View panel) {
            isClickable = true;
            mLayout.setDragView(mTextView);
        }


        @Override
        public void onPanelCollapsed(View panel) {
            isClickable = false;
            mLayout.setDragView(mLinearLayout);
        }


        @Override
        public void onPanelAnchored(View panel) {
        }


        @Override
        public void onPanelHidden(View panel) {
        }
    };

    public Boolean isClickable = false;

    public ObjectsAdapter arrayAdapterObjects;
    public ArrayList<String> arrayListObjects;
    public SharedPreferences prefs;



    public ObjectListFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        arrayListObjects = new ArrayList<>();
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri objectUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_object_list, container, false);
        arrayAdapterObjects = new ObjectsAdapter(getActivity(), null, 0);

        ListView mListView = (ListView) rootView.findViewById(R.id.objectListView);
        mTextView = (TextView) rootView.findViewById(R.id.cityNameTextView);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.listViewWrapLinearLayout);
        mListView.setAdapter(arrayAdapterObjects);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Util.getPreferredLocation(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(AirContract.ObjectEntry.buildObjectLocationId(
                                    locationSetting, cursor.getInt(COL_OBJECT_ID)
                            ));
                }
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !isClickable;
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Auto-generated method stub
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getChildAt(0).getTop() == 0 && scrollState == 0) {
                    mLayout.setDragView(mLinearLayout);
                    isClickable = false;
                } else {
                    mLayout.setDragView(mTextView);
                    isClickable = true;
                }
            }
        });

        mLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(inactiveSlideListener);
        mLayout.setDragView(mLinearLayout);
        if (Util.getOrientation(getActivity()) == Configuration.ORIENTATION_PORTRAIT) {
            mLayout.setPanelHeight(Util.getPanelHeight(getActivity(), getActivity().findViewById(R.id.indicator)));
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
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
        String locationSetting = Util.getPreferredLocation(getActivity());
        String sortOrder = AirContract.ObjectEntry.COLUMN_NAME + " ASC";
        Uri objectForLocationUri = AirContract.ObjectEntry.buildObjectLocation(
                locationSetting);

        return new CursorLoader(getActivity(),
                objectForLocationUri,
                OBJECT_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        arrayAdapterObjects.swapCursor(data);
        mTextView.setText(Util.getPreferredLocation(getActivity()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        arrayAdapterObjects.swapCursor(null);
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
