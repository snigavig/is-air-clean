package com.goodcodeforfun.isairclean.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.goodcodeforfun.isairclean.R;
import com.goodcodeforfun.isairclean.Util;
import com.goodcodeforfun.isairclean.adapters.ObjectsAdapter;
import com.goodcodeforfun.isairclean.data.AirContract;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ObjectListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ObjectListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_OBJECT_NAME = 1;
    public static final int COL_OBJECT_INTENSITY_CURRENT = 2;
    static final int COL_OBJECT_ID = 0;
    static final int COL_OBJECT_COORD_LAT = 3;
    static final int COL_OBJECT_COORD_LONG = 4;
    static final int MIN_DISTANCE = 100;
    private static final int LOADER_ID = 0;
    private static final String[] OBJECT_COLUMNS = {
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry._ID,
            AirContract.ObjectEntry.COLUMN_NAME,
            AirContract.ObjectEntry.TABLE_NAME + "." + AirContract.ObjectEntry.COLUMN_INTENSITY_CURRENT,
            AirContract.ObjectEntry.COLUMN_COORD_LAT,
            AirContract.ObjectEntry.COLUMN_COORD_LONG
    };
    public Boolean isClickable = false;
    public ObjectsAdapter arrayAdapterObjects;
    public ArrayList<String> arrayListObjects;
    public SharedPreferences prefs;
    public DisplayMetrics displayMetrics;
    public SlidingUpPanelLayout mLayout;
    private float downX, downY, upX, upY;
    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private ActionBar mActionBar;
    private final SlidingUpPanelLayout.PanelSlideListener inactiveSlideListener = new SlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            mActionBar.setElevation(displayMetrics.density * (8 - (slideOffset * 8)));

            if (slideOffset == 1) {
                isClickable = true;
                mLayout.setDragView(mTextView);
            } else if (slideOffset == 0) {
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


    public ObjectListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        arrayListObjects = new ArrayList<>();
        displayMetrics = getActivity().getResources().getDisplayMetrics();
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
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
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downX = event.getX();
                        downY = event.getY();
                    }
                    case MotionEvent.ACTION_UP: {
                        upY = event.getY();

                        float deltaY = downY - upY;

                        if (Math.abs(deltaY) > MIN_DISTANCE) {
                            if (deltaY < 0) {
                                return !isClickable;
                            }
                            if (deltaY > 0) {
                                return !isClickable;
                            }
                        } else {
                            return false;
                        }
                    }
                }
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
//        if (!getResources().getBoolean(R.bool.is_tablet)) {
//            if (Util.getOrientation(getActivity()) == Configuration.ORIENTATION_PORTRAIT) {
//                mLayout.setPanelHeight(Util.setPanelHeight(getActivity(), getActivity().findViewById(R.id.graphWrap)));
//            }
//        } else {
//            mLayout.setPanelHeight(Util.setPanelHeight(getActivity(), getActivity().findViewById(R.id.graphWrap)));
//        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        arrayAdapterObjects.swapCursor(null);
    }

    public void onLocationChanged() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri objectUri);
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
    }
}
