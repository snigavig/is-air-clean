package com.goodcodeforfun.isairclean;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String name = cursor.getString(ObjectListFragment.COL_OBJECT_NAME);
        viewHolder.nameView.setText(name);
        float intensity = cursor.getFloat(ObjectListFragment.COL_OBJECT_INTENSITY_CURRENT);
        viewHolder.intensityView.setText(String.valueOf(intensity));

    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView intensityView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.listItemNameTextview);
            intensityView = (TextView) view.findViewById(R.id.listItemIntensityTextView);
        }
    }


}
