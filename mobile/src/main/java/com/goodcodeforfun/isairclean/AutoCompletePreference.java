package com.goodcodeforfun.isairclean;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.goodcodeforfun.isairclean.data.AirContract;

import java.util.ArrayList;

/**
 * Created by snigavig on 09.04.15.
 */
public class AutoCompletePreference extends EditTextPreference {
    private static AutoCompleteTextView mEditText = null;
    private static Context mContext;

    public AutoCompletePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mEditText = new AutoCompleteTextView(context, attrs);
        mEditText.setThreshold(0);
        ArrayList<String> arrayCities = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                AirContract.CityEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            arrayCities.add(cursor.getString(1));
        }

        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, arrayCities);
        mEditText.setAdapter(adapter);
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        AutoCompleteTextView editText = mEditText;
        editText.setText(getText());

        ViewParent oldParent = editText.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(editText);
            }
            onAddEditTextToDialogView(view, editText);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        final boolean wasBlocking = shouldDisableDependents();
        persistString(String.valueOf(mEditText.getText()));
        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
        if (callChangeListener(String.valueOf(mEditText.getText()))) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(mContext.getString(R.string.pref_location_key), String.valueOf(mEditText.getText()));
            editor.commit();
        }
    }
}
