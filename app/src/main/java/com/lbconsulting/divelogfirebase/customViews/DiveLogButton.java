package com.lbconsulting.divelogfirebase.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lbconsulting.divelogfirebase.R;

/**
 * A button with four textViews
 * tvTitle on top
 * tvColumn1 and tvColumn2 below
 * tvColumn3
 */

public class DiveLogButton extends LinearLayout implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvColumn1;
    private TextView tvColumn2;
    private TextView tvColumn3;

    public DiveLogButton(Context context) {
        super(context);
        initializeViews(context);
    }

    public DiveLogButton(Context context, AttributeSet attrs) {
        super(context, attrs);

//        TypedArray a = context.getTheme().obtainStyledAttributes(
//                attrs,
//                R.styleable.DiveLogButton,
//                0, 0);
//
//        try {
//            tvTitle.setText(a.getString(R.styleable.DiveLogButton_titleText));
//            tvTitle.setHint(a.getString(R.styleable.DiveLogButton_titleHint));
//        } finally {
//            a.recycle();
//        }

        initializeViews(context);
    }

    public DiveLogButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

//        TypedArray a = context.getTheme().obtainStyledAttributes(
//                attrs,
//                R.styleable.DiveLogButton,
//                0, 0);
//
//        try {
//            tvTitle.setText(a.getString(R.styleable.DiveLogButton_titleText));
//            tvTitle.setHint(a.getString(R.styleable.DiveLogButton_titleHint));
//        } finally {
//            a.recycle();
//        }

        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.dive_log_button, this, true);
    }

    public void setTitleText(String title) {
        tvTitle.setText(title);
    }

    public void setColumn1Text(String column1Text) {
        tvColumn1.setText(column1Text);
    }

    public void setColumn2Text(String column2Text) {
        tvColumn2.setText(column2Text);
    }

    public void setColumn3Text(String column3Text) {
        tvColumn3.setText(column3Text);
    }

    public void setTitleHint(String hint) {
        tvTitle.setHint(hint);
    }

    public void setColumn1Hint(String column1Hint) {
        tvColumn1.setHint(column1Hint);
    }

    public void setColumn2Hint(String column2Hint) {
        tvColumn2.setHint(column2Hint);
    }

    public void setColumn3Hint(String column3Hint) {
        tvColumn3.setHint(column3Hint);
    }

    public void hideTitle() {
        tvTitle.setVisibility(GONE);
    }

    public void hideColumn1() {
        tvColumn1.setVisibility(GONE);
    }

    public void hideColumn2() {
        tvColumn2.setVisibility(GONE);
    }

    public void hideColumn3() {
        tvColumn3.setVisibility(GONE);
    }

    public void showTitle() {
        tvTitle.setVisibility(VISIBLE);
    }

    public void showColumn1() {
        tvColumn1.setVisibility(VISIBLE);
    }

    public void showColumn2() {
        tvColumn2.setVisibility(VISIBLE);
    }

    public void showColumn3() {
        tvColumn3.setVisibility(VISIBLE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvColumn1 = (TextView) findViewById(R.id.tvColumn1);
        tvColumn2 = (TextView) findViewById(R.id.tvColumn2);
        tvColumn3 = (TextView) findViewById(R.id.tvColumn3);
        setOrientation(VERTICAL);
        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onClick(View v) {
//        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }
}
