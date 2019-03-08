package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;

import timber.log.Timber;

/**
 * The dialog to select dive tissue loading
 */
public class dialogTissueLoading extends DialogFragment implements CompoundButton.OnCheckedChangeListener {
    public static final String TISSUE_LOADING_GREEN = "Green";
    private static final String TISSUE_LOADING_YELLOW = "Yellow";
    private static final String TISSUE_LOADING_RED = "Red";

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    private static final String ARG_TISSUE_LOADING_COLOR = "argTissueLoadingColor";
    private static final String ARG_TISSUE_LOADING_VALUE = "argTissueLoadingValue";

    private AlertDialog mTissueLoadingDialog;

    private String mUserUid;
    private String mDiveLogUid;
    private String mTissueLoadingColor;
    private int mTissueLoadingValue;
    private int mInitialTissueLoadingValue;
    private boolean mIsFirstTimeLoadingDialog;

    private NumberPicker npTissueLoading;
    private int[] mNumberPickerNumbers;

    public dialogTissueLoading() {
        // default constructor
    }

    public static dialogTissueLoading newInstance(String userUid, String diveLogUid,
                                                  String startColor, int startValue) {
        dialogTissueLoading fragment = new dialogTissueLoading();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putString(ARG_TISSUE_LOADING_COLOR, startColor);
        args.putInt(ARG_TISSUE_LOADING_VALUE, startValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
            mTissueLoadingColor = args.getString(ARG_TISSUE_LOADING_COLOR);
            mInitialTissueLoadingValue = args.getInt(ARG_TISSUE_LOADING_VALUE);
        }
        mIsFirstTimeLoadingDialog = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mTissueLoadingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button cancelButton = mTissueLoadingDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(16);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        dismiss();
                    }
                });

                Button saveButton = mTissueLoadingDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Save
                        mTissueLoadingValue = mNumberPickerNumbers[npTissueLoading.getValue()];
                        DiveLog.saveTissueLoading(mUserUid, mDiveLogUid, mTissueLoadingColor, mTissueLoadingValue);
                        dismiss();
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater
                .inflate(R.layout.dialog_dive_log_tissue_loading, null, false);

        // find the dialog's views
        if (view != null) {
            String mTitle = "Select Tissue Loading";
            RadioButton rbGreen = (RadioButton) view.findViewById(R.id.rbGreen);
            rbGreen.setOnCheckedChangeListener(this);
            RadioButton rbYellow = (RadioButton) view.findViewById(R.id.rbYellow);
            rbYellow.setOnCheckedChangeListener(this);
            RadioButton rbRed = (RadioButton) view.findViewById(R.id.rbRed);
            rbRed.setOnCheckedChangeListener(this);
            npTissueLoading = (NumberPicker) view.findViewById(R.id.npTissueLoading);

            switch (mTissueLoadingColor) {
                case TISSUE_LOADING_GREEN:
                    rbGreen.setChecked(true);
                    break;

                case TISSUE_LOADING_YELLOW:
                    rbYellow.setChecked(true);
                    break;

                case TISSUE_LOADING_RED:
                    rbRed.setChecked(true);
                    break;
            }
            // build the dialog
            mTissueLoadingDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(mTitle)
                    .setView(view)
                    .setNegativeButton(R.string.btnCancel_title, null)
                    .setPositiveButton(R.string.btnSave_title, null)
                    .create();
        }
        return mTissueLoadingDialog;
    }

    private void setupNumberPicker(String selectedColor) {

        int minValue = 1;
        int maxValue = 15;
        int increment = 1;

        switch (selectedColor) {
            case TISSUE_LOADING_GREEN:
                minValue = 1;
                maxValue = 15;
                increment = 1;
                break;

            case TISSUE_LOADING_YELLOW:
                minValue = 1;
                maxValue = 5;
                increment = 1;
                break;

            case TISSUE_LOADING_RED:
                minValue = 1;
                maxValue = 20;
                increment = 1;
                break;
        }

        // Create the array of numbers that will populate the number picker
        // make sure that the starting value is within number picker range
        if (mIsFirstTimeLoadingDialog) {
            mTissueLoadingValue = mInitialTissueLoadingValue;
            mIsFirstTimeLoadingDialog = false;
        }
        if (mTissueLoadingValue > maxValue) {
            mTissueLoadingValue = maxValue;
        }
        if (mTissueLoadingValue < minValue) {
            mTissueLoadingValue = minValue;
        }
        int dialogMaxValue = (maxValue - minValue) / increment;
        int displayNumberSize = dialogMaxValue + 1;

        int startingValueIndex = -1;
        int nextNumber;
        final String[] displayedNumbers = new String[displayNumberSize];
        mNumberPickerNumbers = new int[displayNumberSize];

        for (int i = 0; i < displayedNumbers.length; i++) {
            nextNumber = (i * increment) + minValue;
            displayedNumbers[i] = String.valueOf(nextNumber);
            mNumberPickerNumbers[i] = nextNumber;
            if (nextNumber >= mTissueLoadingValue && startingValueIndex == -1) {
                startingValueIndex = i;
            }
        }

        // Set the max and min values of the number picker, and give it the
        // array of numbers created above to be the displayed numbers/
        // Must set Display Values null or else you'll get an array out of bounds
        // error when going from a smaller list to a larger display list.
        npTissueLoading.setDisplayedValues(null);
        npTissueLoading.setMaxValue(displayedNumbers.length - 1); // the last array index
        npTissueLoading.setMinValue(0); // the fist array index
        npTissueLoading.setDisplayedValues(displayedNumbers);
        npTissueLoading.setWrapSelectorWheel(true);
        npTissueLoading.setValue(startingValueIndex);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (mNumberPickerNumbers != null) {
            mTissueLoadingValue = mNumberPickerNumbers[npTissueLoading.getValue()];
        } else {
            mTissueLoadingValue = 0;
        }

        switch (buttonView.getId()) {

            case R.id.rbGreen:
                if (isChecked) {
                    mTissueLoadingColor = TISSUE_LOADING_GREEN;
                    setupNumberPicker(TISSUE_LOADING_GREEN);
                }
                break;

            case R.id.rbYellow:
                if (isChecked) {
                    mTissueLoadingColor = TISSUE_LOADING_YELLOW;
                    setupNumberPicker(TISSUE_LOADING_YELLOW);
                }
                break;

            case R.id.rbRed:
                if (isChecked) {
                    mTissueLoadingColor = TISSUE_LOADING_RED;
                    setupNumberPicker(TISSUE_LOADING_RED);
                }
                break;
        }
    }
}
