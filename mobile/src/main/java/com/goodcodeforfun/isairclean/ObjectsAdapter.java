package com.goodcodeforfun.isairclean;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goodcodeforfun.isairclean.data.AirContract;

import at.markushi.ui.CircleButton;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

/**
 * Created by snigavig on 24.03.15.
 */
public class ObjectsAdapter extends CursorAdapter {

    public ObjectsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId;
        layoutId = R.layout.object_list_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        final String name = cursor.getString(ObjectListFragment.COL_OBJECT_NAME);
        viewHolder.nameView.setText(name);
        float intensity = cursor.getFloat(ObjectListFragment.COL_OBJECT_INTENSITY_CURRENT);
        viewHolder.intensityView.setText(String.valueOf(intensity));
        viewHolder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver mContentResolver = context.getContentResolver();
                Cursor objectCursor = mContentResolver.query(
                        AirContract.ObjectEntry.CONTENT_URI,
                        new String[]{AirContract.ObjectEntry.COLUMN_COORD_LAT, AirContract.ObjectEntry.COLUMN_COORD_LONG},
                        AirContract.ObjectEntry.COLUMN_NAME + " = ?",
                        new String[]{name},
                        null);

                if (objectCursor.moveToFirst()) {
                    String coordLat = objectCursor.getString(objectCursor.getColumnIndex(AirContract.ObjectEntry.COLUMN_COORD_LAT));
                    String coordLong = objectCursor.getString(objectCursor.getColumnIndex(AirContract.ObjectEntry.COLUMN_COORD_LONG));
                    if (!coordLat.equals("0") && !coordLong.equals("0")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri geoLocation = Uri.parse("geo:" + String.valueOf(coordLat) + "," + String.valueOf(coordLong));
                        //String uri = Uri.Builder().scheme("geo").appendPath(lat +","+ lng).appendQueryParameter("q", name).build();
                        intent.setData(geoLocation);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        } else {
                            makeText(context, "Sorry, no application to show a map", LENGTH_SHORT).show();
                        }
                    } else {
                        makeText(context, "Sorry, no location for this object", LENGTH_SHORT).show();
                    }

                }
                objectCursor.close();
            }
        });

    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView intensityView;
        public final CircleButton mapButton;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.listItemNameTextview);
            intensityView = (TextView) view.findViewById(R.id.listItemIntensityTextView);
            mapButton = (CircleButton) view.findViewById(R.id.mapButton);
        }
    }


}
