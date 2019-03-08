package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MySettings;
import com.lbconsulting.divelogfirebase.utils.clsConvert;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * A number picker for various diveLog fields.
 * <p/>
 * This dialog presents several 0-9 digit wheels for the user to select a number.
 * The dialog receives a starting value in imperial units as stored in the Firebase database.
 * If the user as selected metric units the dialog translates the starting value to metric; and
 * translates time durations from milliseconds to minutes.
 * Upon saving the new number value, the dialog translates the selected value to imperial units or
 * milliseconds and returns the selected value back to the DiveLogDetailFragment for storage in the
 * Firebase database and display in the UI.
 */
public class dialogNumberPicker extends DialogFragment {

    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    private static final String ARG_BUTTON_ID = "argButtonID";
    private static final String ARG_NUMBER_PICKER_START_VALUE = "argNumberPickerStartValue";
    private static final String ARG_IS_IMPERIAL_UNITS = "argIsImperialUnits";

    private AlertDialog mNumberPikerDialog;

    private String mDiveLogID;
    private int mButtonID;
    private Double mStartValue;
    private boolean mIsImperialUnits;

    private NumberPicker np1000s;
    private NumberPicker np100s;
    private NumberPicker np10s;
    private NumberPicker np1s;
    private TextView decimalPoint;
    private NumberPicker npTenths;

    private String mTitle;

    public dialogNumberPicker() {
        // Empty constructor required for DialogFragment
    }

    public static dialogNumberPicker newInstance(String diveLogUid, int buttonID, Double startValue, boolean isImperialUnits) {
        dialogNumberPicker fragment = new dialogNumberPicker();
        Bundle args = new Bundle();
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putInt(ARG_BUTTON_ID, buttonID);
        args.putDouble(ARG_NUMBER_PICKER_START_VALUE, startValue);
        args.putBoolean(ARG_IS_IMPERIAL_UNITS, isImperialUnits);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();
        if (args.containsKey(ARG_BUTTON_ID)) {
            mDiveLogID = args.getString(ARG_DIVE_LOG_UID);
            mButtonID = args.getInt(ARG_BUTTON_ID);
            mIsImperialUnits = args.getBoolean(ARG_IS_IMPERIAL_UNITS);
            mStartValue = args.getDouble(ARG_NUMBER_PICKER_START_VALUE);
            mStartValue = translateStartValue(mStartValue);
        }
    }

    private Double translateStartValue(Double startValue) {

        switch (mButtonID) {
            case R.id.btnBottomTime:
            case R.id.btnSurfaceInterval:
                // startValue in milliseconds --> minutes
                startValue = clsConvert.millisToMinutes(startValue);
                break;

            case R.id.btnMaximumDepth:
            case R.id.btnVisibility:
                if (!mIsImperialUnits) {
                    // startValue in feet --> meters
                    startValue = clsConvert.feetToMeters(startValue);
                }
                break;

            case R.id.btnWeightUsed:
                if (!mIsImperialUnits) {
                    // startValue in pounds --> kilograms
                    startValue = clsConvert.poundsToKg(startValue);
                }
                break;

            case R.id.btnStartingTankPressure:
            case R.id.btnEndingTankPressure:
            case R.id.btnAirUsed:
                if (!mIsImperialUnits) {
                    // startValue in psi --> bars
                    startValue = clsConvert.psiToBars(startValue);
                }
                break;


            case R.id.btnAirTemperature:
            case R.id.btnWaterTemperature:
                if (!mIsImperialUnits) {
                    // startValue in degrees F --> degrees C
                    startValue = clsConvert.fahrenheitToCelsius(startValue);
                }
                break;
        }
        return startValue;
    }

    private Double translateSelectedValue(Double selectedValue) {

        switch (mButtonID) {
            case R.id.btnBottomTime:
            case R.id.btnSurfaceInterval:
                // startValue in minutes --> milliseconds
                selectedValue = clsConvert.minutesToMillis(selectedValue);
                break;

            case R.id.btnMaximumDepth:
            case R.id.btnVisibility:
                if (!mIsImperialUnits) {
                    // startValue in meters --> feet
                    selectedValue = clsConvert.metersToFeet(selectedValue);
                }
                break;

            case R.id.btnWeightUsed:
                if (!mIsImperialUnits) {
                    // startValue in kilograms --> pounds
                    selectedValue = clsConvert.kgToPounds(selectedValue);
                }
                break;

            case R.id.btnStartingTankPressure:
            case R.id.btnEndingTankPressure:
            case R.id.btnAirUsed:
                if (!mIsImperialUnits) {
                    // startValue in bars --> psi
                    selectedValue = clsConvert.barsToPsi(selectedValue);
                }
                break;


            case R.id.btnAirTemperature:
            case R.id.btnWaterTemperature:
                if (!mIsImperialUnits) {
                    // startValue in degrees C --> degrees F
                    selectedValue = clsConvert.celsiusToFahrenheit(selectedValue);
                }
                break;
        }
        return selectedValue;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mNumberPikerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = mNumberPikerDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double selectedValue = translateSelectedValue(getSelectedValue());
                        EventBus.getDefault().post(new MyEvents
                                .updateActiveDiveLogNumericValues(mDiveLogID, mButtonID, selectedValue));
                        dismiss();
                    }
                });

                Button cancelButton = mNumberPikerDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(16);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
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
        View view = inflater.inflate(R.layout.dialog_number_picker, null, false);

        // find the dialog's views
        if (view != null) {
            np1000s = (NumberPicker) view.findViewById(R.id.np1000s);
            np100s = (NumberPicker) view.findViewById(R.id.np100s);
            np10s = (NumberPicker) view.findViewById(R.id.np10s);
            np1s = (NumberPicker) view.findViewById(R.id.np1s);
            decimalPoint = (TextView) view.findViewById(R.id.decimalPoint);
            npTenths = (NumberPicker) view.findViewById(R.id.npTenths);

            List<NumberPicker> numberPickers = new ArrayList<>();
            numberPickers.add(np1000s);
            numberPickers.add(np100s);
            numberPickers.add(np10s);
            numberPickers.add(np1s);
            numberPickers.add(npTenths);
            setupNumberPickers(numberPickers);
        }

        // build the dialog
        mNumberPikerDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mNumberPikerDialog;

    }

    private Double getSelectedValue() {
        int thousands = np1000s.getValue();
        int hundreds = np100s.getValue();
        int tens = np10s.getValue();
        int ones = np1s.getValue();
        int tenths = npTenths.getValue();

        return (thousands * 1000) + (hundreds * 100) + (tens * 10) + ones + (tenths * 0.1);
    }

    private int[] getStartingNumberPickerValues(Double startValue) {

        Double thousandsDouble = (startValue / 1000);
        int thousands = thousandsDouble.intValue();
        Double remainder = startValue - (thousands * 1000);

        Double hundredsDouble = remainder / 100;
        int hundreds = hundredsDouble.intValue();
        remainder = remainder - (hundreds * 100);

        Double tensDouble = remainder / 10;
        int tens = tensDouble.intValue();
        remainder = remainder - (tens * 10);

        int ones = remainder.intValue();
        remainder = remainder - ones;

        Double tenthsDouble = remainder * 10;
        int tenths = tenthsDouble.intValue();

        int[] result = new int[5];
        result[0] = thousands;
        result[1] = hundreds;
        result[2] = tens;
        result[3] = ones;
        result[4] = tenths;

        return result;
    }

    private void setupNumberPickers(List<NumberPicker> numberPickers) {

        mTitle = MySettings.NOT_AVAILABLE;

        for (NumberPicker numberPicker : numberPickers) {
            numberPicker.setMaxValue(9);
            numberPicker.setMinValue(0);
            numberPicker.setWrapSelectorWheel(true);
        }


        int[] initialValues = getStartingNumberPickerValues(mStartValue);
        np1000s.setValue(initialValues[0]);
        np100s.setValue(initialValues[1]);
        np10s.setValue(initialValues[2]);
        np1s.setValue(initialValues[3]);
        npTenths.setValue(initialValues[4]);

        switch (mButtonID) {
            case R.id.btnBottomTime:
                mTitle = "Bottom Time (min)";
                show100s();
                break;

            case R.id.btnSurfaceInterval:
                mTitle = "Surface Interval (min)";
                show100s();
                break;

            case R.id.btnMaximumDepth:
                if (mIsImperialUnits) {
                    mTitle = "Max Depth (ft)";
                    show100s();
                } else {
                    mTitle = "Max Depth (m)";
                    mStartValue = clsConvert.feetToMeters(mStartValue);
                    show10sWithTenths();
                }
                break;

            case R.id.btnWeightUsed:
                if (mIsImperialUnits) {
                    mTitle = "Select Weight (lbs)";
                    show10s();
                } else {
                    mTitle = "Select Weight (kg)";
                    show10sWithTenths();
                }
                break;

            case R.id.btnStartingTankPressure:
                if (mIsImperialUnits) {
                    mTitle = "Starting Pressure (psi)";
                    show1000s();
                } else {
                    mTitle = "Starting Pressure (bar)";
                    show100s();
                }
                break;

            case R.id.btnEndingTankPressure:
                if (mIsImperialUnits) {
                    mTitle = "Ending Pressure (psi)";
                    show1000s();
                } else {
                    mTitle = "Ending Pressure (bar)";
                    show100s();
                }

            case R.id.btnAirUsed:
                if (mIsImperialUnits) {
                    mTitle = "Air Used (psi)";
                    show1000s();
                } else {
                    mTitle = "Air Used (bar)";
                    show100s();
                }
                break;

            case R.id.btnVisibility:
                if (mIsImperialUnits) {
                    mTitle = "Visibility (ft)";
                    show100s();
                } else {
                    mTitle = "Visibility (m)";
                    show10sWithTenths();
                }
                break;

            case R.id.btnAirTemperature:
                if (mIsImperialUnits) {
                    mTitle = "Air Temp " + getString(R.string.degrees_F);
                    show100s();
                } else {
                    mTitle = "Air Temp " + getString(R.string.degrees_C);
                    show10sWithTenths();
                }
                break;

            case R.id.btnWaterTemperature:
                if (mIsImperialUnits) {
                    mTitle = "Water Temp " + getString(R.string.degrees_F);
                    show100s();
                } else {
                    mTitle = "Water Temp " + getString(R.string.degrees_C);
                    show10sWithTenths();
                }
                break;
        }
    }

    private void show1000s() {
        roundTenths();
        np1000s.setVisibility(View.VISIBLE);
        np100s.setVisibility(View.VISIBLE);
        np10s.setVisibility(View.VISIBLE);
        np1s.setVisibility(View.VISIBLE);
        decimalPoint.setVisibility(View.GONE);
        npTenths.setVisibility(View.GONE);
    }

    private void show100s() {
        roundTenths();
        np1000s.setVisibility(View.GONE);
        np1000s.setValue(0);
        np100s.setVisibility(View.VISIBLE);
        np10s.setVisibility(View.VISIBLE);
        np1s.setVisibility(View.VISIBLE);
        decimalPoint.setVisibility(View.GONE);
        npTenths.setVisibility(View.GONE);
    }

    private void show10s() {
        roundTenths();
        np1000s.setVisibility(View.GONE);
        np1000s.setValue(0);
        np100s.setVisibility(View.INVISIBLE);
        np10s.setVisibility(View.VISIBLE);
        np1s.setVisibility(View.VISIBLE);
        decimalPoint.setVisibility(View.GONE);
        npTenths.setVisibility(View.GONE);
    }

    private void roundTenths() {
        if (npTenths.getValue() > 4) {
            if (np1s.getValue() == 9) {
                np1s.setValue(0);
                if (np10s.getValue() == 9) {
                    np10s.setValue(0);
                    if (np100s.getValue() == 9) {
                        np100s.setValue(0);
                        if (np1000s.getValue() == 9) {
                            np100s.setValue(0);
                        }
                    } else {
                        np100s.setValue(np100s.getValue() + 1);
                    }
                } else {
                    np10s.setValue(np10s.getValue() + 1);
                }
            } else {
                np1s.setValue(np1s.getValue() + 1);
            }
        }
        npTenths.setValue(0);

    }

    private void show10sWithTenths() {
        np1000s.setVisibility(View.GONE);
        np1000s.setValue(0);
        np100s.setVisibility(View.GONE);
        np10s.setVisibility(View.VISIBLE);
        np1s.setVisibility(View.VISIBLE);
        decimalPoint.setVisibility(View.VISIBLE);
        npTenths.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        Timber.i("onResume()");
        super.onResume();
    }


    @Override
    public void onPause() {
        Timber.i("onPause()");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy()");
        super.onDestroy();
    }
}
