package com.goodcodeforfun.isairclean;

import android.content.Context;
import android.content.res.Resources;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * Created by snigavig on 09.04.15.
 */
public class AutoCompletePreference extends EditTextPreference {
    private static AutoCompleteTextView mEditText = null;

    public AutoCompletePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new AutoCompleteTextView(context, attrs);
        mEditText.setThreshold(0);
        Resources res = context.getResources();
        String[] arrayCities = res.getStringArray(R.array.citiesDummyList);
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
}
