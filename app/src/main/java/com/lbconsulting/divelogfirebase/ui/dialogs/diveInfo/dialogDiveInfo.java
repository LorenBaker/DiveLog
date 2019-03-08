package com.lbconsulting.divelogfirebase.ui.dialogs.diveInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.SelectionValue;
import com.lbconsulting.divelogfirebase.ui.activities.DiveLogPagerActivity;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogSelectionValuesPicker;
import com.lbconsulting.divelogfirebase.ui.dialogs.diveStats.dialogNumberPicker;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MySettings;
import com.lbconsulting.divelogfirebase.utils.clsConvert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;

import timber.log.Timber;

/**
 * This dialog allows a user to update a tank, visibility, and temperatures
 */

public class dialogDiveInfo extends DialogFragment implements View.OnClickListener {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_JSON = "argDiveLogJson";

    private AlertDialog mDiveInfoDialog;
    private String mUserUid;
    private DiveLog mCurrentDiveLog;
    private String mTitle;
    private NumberFormat nf;

    private Button btnStartingTankPressure;
    private Button btnEndingTankPressure;
    private Button btnAirUsed;
    private Button btnDiveTankType;
    private Button btnVisibility;
    private Button btnAirTemperature;
    private Button btnWaterTemperature;


    public dialogDiveInfo() {
    }

    public static dialogDiveInfo newInstance(@NonNull String userUid,
                                             @NonNull DiveLog diveLog) {
        dialogDiveInfo frag = new dialogDiveInfo();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        Gson gson = new Gson();
        String diveLogJson = gson.toJson(diveLog);
        args.putString(ARG_DIVE_LOG_JSON, diveLogJson);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
            String diveLogJson = args.getString(ARG_DIVE_LOG_JSON);
            Gson gson = new Gson();
            mCurrentDiveLog = gson.fromJson(diveLogJson, DiveLog.class);

        }
        nf = NumberFormat.getInstance();
        mTitle = "Update More Dive Details";

        EventBus.getDefault().register(this);

        Timber.i("onCreate(): %s", mCurrentDiveLog.getShortTitle());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated(): %s", mCurrentDiveLog.getShortTitle());

        mDiveInfoDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = mDiveInfoDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DiveLog.save(mUserUid, mCurrentDiveLog);
                        dismiss();
                    }
                });

                Button cancelButton = mDiveInfoDialog.getButton(Dialog.BUTTON_NEGATIVE);
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

    @Subscribe
    public void onEvent(MyEvents.updateSelectionValue event) {
        SelectionValue proposedSelectionValue = event.getSelectionValue();
        String value = proposedSelectionValue.getValue();

        switch (proposedSelectionValue.getNodeName()) {

            case SelectionValue.NODE_DIVE_TANK_VALUES:
                mCurrentDiveLog.setTankType(value);
                btnDiveTankType.setText(getDiveTankType());
                break;

            default:
                Timber.e("onEvent(): Invalid SelectionValue. Node = %s", proposedSelectionValue
                        .getNodeName());
        }
    }

    @Subscribe
    public void onEvent(MyEvents.dismissDialog event) {
        dismiss();
    }

    @Subscribe
    public void onEvent(MyEvents.updateActiveDiveLogNumericValues event) {
        if (mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLogUid())) {

            switch (event.getButtonID()) {
                case R.id.btnStartingTankPressure:
                    mCurrentDiveLog.setStartingTankPressureDouble(event.getSelectedValue());
                    mCurrentDiveLog.setAirUsedDouble(event.getSelectedValue() - mCurrentDiveLog
                            .getEndingTankPressureDouble());
                    btnStartingTankPressure.setText(getStartingPressure());
                    btnAirUsed.setText(getAirUsed());
                    break;

                case R.id.btnEndingTankPressure:
                    mCurrentDiveLog.setEndingTankPressureDouble(event.getSelectedValue());
                    mCurrentDiveLog.setAirUsedDouble(mCurrentDiveLog
                                                             .getStartingTankPressureDouble() -
                                                             event.getSelectedValue());
                    btnEndingTankPressure.setText(getEndingPressure());
                    btnAirUsed.setText(getAirUsed());
                    break;

                case R.id.btnAirUsed:
                    mCurrentDiveLog.setAirUsedDouble(event.getSelectedValue());
                    mCurrentDiveLog.setEndingTankPressureDouble(mCurrentDiveLog
                                                                        .getStartingTankPressureDouble() - event.getSelectedValue());
                    mCurrentDiveLog.setAirUsedDouble(event.getSelectedValue());
                    btnEndingTankPressure.setText(getEndingPressure());
                    btnAirUsed.setText(getAirUsed());
                    break;

                case R.id.btnVisibility:
                    mCurrentDiveLog.setVisibilityDouble(event.getSelectedValue());
                    btnVisibility.setText(getVisibility());
                    break;

                case R.id.btnAirTemperature:
                    mCurrentDiveLog.setAirTemperatureDouble(event.getSelectedValue());
                    btnAirTemperature.setText(getAirTemperature());
                    break;

                case R.id.btnWaterTemperature:
                    mCurrentDiveLog.setWaterTemperatureDouble(event.getSelectedValue());
                    btnWaterTemperature.setText(getWaterTemperature());
                    break;

                default:
                    Timber.e("onEvent(): Unknown ButtonID=%d", event.getButtonID());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("onResume(): %s", mCurrentDiveLog.getShortTitle());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_dive_info,
                                                                    null, false);

        // find the dialog's views
        btnStartingTankPressure = (Button) view.findViewById(R.id.btnStartingTankPressure);
        btnEndingTankPressure = (Button) view.findViewById(R.id.btnEndingTankPressure);
        btnAirUsed = (Button) view.findViewById(R.id.btnAirUsed);
        btnDiveTankType = (Button) view.findViewById(R.id.btnDiveTankType);
        btnVisibility = (Button) view.findViewById(R.id.btnVisibility);
        btnAirTemperature = (Button) view.findViewById(R.id.btnAirTemperature);
        btnWaterTemperature = (Button) view.findViewById(R.id.btnWaterTemperature);

        btnStartingTankPressure.setText(getStartingPressure());
        btnEndingTankPressure.setText(getEndingPressure());
        btnAirUsed.setText(getAirUsed());
        btnDiveTankType.setText(getDiveTankType());
        btnVisibility.setText(getVisibility());
        btnAirTemperature.setText(getAirTemperature());
        btnWaterTemperature.setText(getWaterTemperature());

        btnStartingTankPressure.setOnClickListener(this);
        btnEndingTankPressure.setOnClickListener(this);
        btnAirUsed.setOnClickListener(this);
        btnDiveTankType.setOnClickListener(this);
        btnVisibility.setOnClickListener(this);
        btnAirTemperature.setOnClickListener(this);
        btnWaterTemperature.setOnClickListener(this);

        // build the dialog
        mDiveInfoDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mDiveInfoDialog;
    }

    private String getStartingPressure() {
        String startingPressureText = String.format(getString(R.string.prefix_startingTankPressure), "", "");
        String suffix = getString(R.string.suffix_psi);
        double startingPressure = mCurrentDiveLog.getStartingTankPressureDouble();
        if (startingPressure > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(4);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                startingPressure = clsConvert.psiToBars(startingPressure);
                suffix = getString(R.string.suffix_bars);
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(0);
            }
            startingPressureText = String.format(getString(R.string.prefix_startingTankPressure),
                                                 nf.format(startingPressure), suffix);
        }
        return startingPressureText;
    }

    private String getEndingPressure() {
        String endingPressureText = String.format(getString(R.string.prefix_endingTankPressure), "", "");
        String suffix = getString(R.string.suffix_psi);
        double endingPressure = mCurrentDiveLog.getEndingTankPressureDouble();
        if (endingPressure > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(4);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                endingPressure = clsConvert.psiToBars(endingPressure);
                suffix = getString(R.string.suffix_bars);
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(0);
            }
            endingPressureText = String.format(getString(R.string.prefix_endingTankPressure), nf.format(endingPressure), suffix);
        }
        return endingPressureText;
    }

    private String getAirUsed() {
        String airUsedText = String.format(getString(R.string.prefix_airUsed), "", "");
        String suffix = getString(R.string.suffix_psi);
        double airUsed = mCurrentDiveLog.getAirUsedDouble();
        if (airUsed > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(4);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                airUsed = clsConvert.psiToBars(airUsed);
                suffix = getString(R.string.suffix_bars);
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(0);
            }
            airUsedText = String.format(getString(R.string.prefix_airUsed), nf.format(airUsed), suffix);
        }
        return airUsedText;
    }


    private String getDiveTankType() {
        String tankTypeText = String.format(getString(R.string.prefix_tank), "");
        String value = mCurrentDiveLog.getTankType();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE) && !value.startsWith("[")) {
            tankTypeText = String.format(getString(R.string.prefix_tank), value);
        }
        return tankTypeText;
    }


    private String getVisibility() {
        String visibilityText = String.format(getString(R.string.prefix_visibility), "", "");
        String suffix = getString(R.string.suffix_ft);
        double value = mCurrentDiveLog.getVisibilityDouble();
        if (value > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(3);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                value = clsConvert.feetToMeters(value);
                suffix = getString(R.string.suffix_m);
                nf.setMinimumFractionDigits(1);
                nf.setMaximumFractionDigits(1);
            }
            visibilityText = String.format(getString(R.string.prefix_visibility), nf.format(value), suffix);
        }
        return visibilityText;
    }

    private String getAirTemperature() {
        String airTempText = null;
        String suffix = getString(R.string.degrees_F);
        double value = mCurrentDiveLog.getAirTemperatureDouble();
        if (value > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(3);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                value = clsConvert.fahrenheitToCelsius(value);
                suffix = getString(R.string.degrees_C);
                nf.setMinimumFractionDigits(1);
                nf.setMaximumFractionDigits(1);
            }
            airTempText = String.format(getString(R.string.prefix_airTemp), nf.format(value), suffix);
        }
        return airTempText;
    }


    private String getWaterTemperature() {
        String waterTempText = String.format(getString(R.string.prefix_waterTemp), "", "");
        String suffix = getString(R.string.degrees_F);
        double value = mCurrentDiveLog.getWaterTemperatureDouble();
        if (value > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(3);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                value = clsConvert.fahrenheitToCelsius(value);
                suffix = getString(R.string.degrees_C);
                nf.setMinimumFractionDigits(1);
                nf.setMaximumFractionDigits(1);
            }
            waterTempText = String.format(getString(R.string.prefix_waterTemp), nf.format(value), suffix);
        }
        return waterTempText;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.i("onDestroyView(): %s", mCurrentDiveLog.getShortTitle());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        dialogNumberPicker numberPickerDialog;

        switch (v.getId()) {

            case R.id.btnStartingTankPressure:
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                     R.id.btnStartingTankPressure,
                                     mCurrentDiveLog.getStartingTankPressureDouble(),
                                     DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnEndingTankPressure:
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                     R.id.btnEndingTankPressure,
                                     mCurrentDiveLog.getEndingTankPressureDouble(),
                                     DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnAirUsed:
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                     R.id.btnAirUsed,
                                     mCurrentDiveLog.getAirUsedDouble(),
                                     DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnDiveTankType:
                showSelectionValuePickerDialog(fm, SelectionValue.NODE_DIVE_TANK_VALUES,
                                               mCurrentDiveLog.getTankType());
                break;

            case R.id.btnVisibility:
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                     R.id.btnVisibility,
                                     mCurrentDiveLog.getVisibilityDouble(),
                                     DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnAirTemperature:
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                     R.id.btnAirTemperature,
                                     mCurrentDiveLog.getAirTemperatureDouble(),
                                     DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnWaterTemperature:
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                     R.id.btnWaterTemperature,
                                     mCurrentDiveLog.getWaterTemperatureDouble(),
                                     DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;
        }
    }

    private void showSelectionValuePickerDialog(final FragmentManager fm,
                                                final String selectionValueNode,
                                                String value) {
        if (!value.equals(MySettings.NOT_AVAILABLE) && !value.startsWith("[")) {
            SelectionValue.nodeUserSelectionValue(mUserUid,
                                                  selectionValueNode, value)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                SelectionValue selectionValue = dataSnapshot.getValue
                                        (SelectionValue.class);
                                dialogSelectionValuesPicker selectionValuePickerDialog =
                                        dialogSelectionValuesPicker
                                                .newInstance(mUserUid, mCurrentDiveLog
                                                        .getDiveLogUid(), null,
                                                             selectionValueNode, selectionValue);
                                selectionValuePickerDialog.show(fm, "selectionValuePickerDialog");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Timber.e("onCancelled(): DatabaseError: %s.", databaseError
                                    .getMessage());
                        }
                    });
        } else {
            dialogSelectionValuesPicker selectionValuePickerDialog = dialogSelectionValuesPicker
                    .newInstance(mUserUid, mCurrentDiveLog.getDiveLogUid(), null,
                                 selectionValueNode, null);
            selectionValuePickerDialog.show(fm, "selectionValuePickerDialog");
        }
    }
}
