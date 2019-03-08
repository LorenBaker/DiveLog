package com.lbconsulting.divelogfirebase.customViews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Loren on 1/3/2017.
 * Source: http://stackoverflow.com/questions/6958460/android-can-i-increase-the-textsize-for-the-numberpicker-widget/12084420#12084420
 */
public class MyNumberPicker extends android.widget.NumberPicker {

    public MyNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    private void updateView(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextSize(25);
            ((EditText) view).setTextColor(Color.parseColor("#333333"));
        }
    }

}

