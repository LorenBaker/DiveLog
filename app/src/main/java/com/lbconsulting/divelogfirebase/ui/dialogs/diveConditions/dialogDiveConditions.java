package com.lbconsulting.divelogfirebase.ui.dialogs.diveConditions;

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
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogSelectionValuesPicker;
import com.lbconsulting.divelogfirebase.ui.dialogs.diveStats.dialogNumberPicker;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;

import timber.log.Timber;

/**
 * This dialog allows a user to update diveStyle, diveType, diveEntry,
 * weatherConditions, currentConditions, and seaConditions
 */

public class dialogDiveConditions extends DialogFragment implements View.OnClickListener {

    public static final String ARG_USER_UID = "argUserUid";
    public static final String ARG_CURRENT_DIVE_LOG_JSON = "argCurrentDiveLogJson";

    private AlertDialog diveConditionsDialog;
    private String mUserUid;
    private DiveLog mCurrentDiveLog;
    private String mTitle;
    private NumberFormat nf;

    private Button btnDiveStyle;
    private Button btnDiveType;
    private Button btnDiveEntry;
    private Button btnWeatherCondition;
    private Button btnCurrentCondition;
    private Button btnSeaCondition;

    private String mOriginalDiveStyle;
    private String mOriginalDiveType;
    private String mOriginalDiveEntry;
    private String mOriginalWeatherCondition;
    private String mOriginalCurrentCondition;
    private String mOriginalSeaCondition;


    public dialogDiveConditions() {
    }

    public static dialogDiveConditions newInstance(@NonNull String userUid,
                                                   @NonNull DiveLog diveLog) {
        dialogDiveConditions frag = new dialogDiveConditions();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        Gson gson = new Gson();
        String diveLogJson = gson.toJson(diveLog);
        args.putString(ARG_CURRENT_DIVE_LOG_JSON, diveLogJson);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
            String diveLogJson = args.getString(ARG_CURRENT_DIVE_LOG_JSON);
            Gson gson = new Gson();
            mCurrentDiveLog = gson.fromJson(diveLogJson, DiveLog.class);
        }

        mOriginalDiveStyle = mCurrentDiveLog.getDiveStyle();
        mOriginalDiveType = mCurrentDiveLog.getDiveType();
        mOriginalDiveEntry = mCurrentDiveLog.getDiveEntry();
        mOriginalWeatherCondition = mCurrentDiveLog.getWeatherCondition();
        mOriginalCurrentCondition = mCurrentDiveLog.getCurrentCondition();
        mOriginalSeaCondition = mCurrentDiveLog.getSeaCondition();

        nf = NumberFormat.getInstance();
        mTitle = "Other Dive Information";

        EventBus.getDefault().register(this);

        Timber.i("onCreate(): %s", mCurrentDiveLog.getShortTitle());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated(): %s", mCurrentDiveLog.getShortTitle());

        diveConditionsDialog.setOnShowListener(
                new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button saveButton = diveConditionsDialog.getButton(Dialog.BUTTON_POSITIVE);
                        saveButton.setTextSize(16);
                        saveButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        DiveLog.save(mUserUid, mCurrentDiveLog);

                                        //<editor-fold desc="DiveStyle">
                                        if (okToRemoveDiveLogFromSelectionValue(mOriginalDiveStyle, mCurrentDiveLog.getDiveStyle())) {
                                            removeDiveLogFromSelectionValue(mUserUid,
                                                    SelectionValue.NODE_DIVE_STYLE_VALUES,
                                                    mOriginalDiveStyle,
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }

                                        if (okToAddDiveLogToSelectionValue(mOriginalDiveStyle, mCurrentDiveLog.getDiveStyle())) {
                                            addDiveLogToSelectionValue(mUserUid,
                                                    SelectionValue.NODE_DIVE_STYLE_VALUES,
                                                    mCurrentDiveLog.getDiveStyle(),
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }
                                        //</editor-fold> DiveStyle

                                        //<editor-fold desc="DiveType">
                                        if (okToRemoveDiveLogFromSelectionValue(mOriginalDiveType, mCurrentDiveLog.getDiveType())) {
                                            removeDiveLogFromSelectionValue(mUserUid,
                                                    SelectionValue.NODE_DIVE_TYPE_VALUES,
                                                    mOriginalDiveType,
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }

                                        if (okToAddDiveLogToSelectionValue(mOriginalDiveType, mCurrentDiveLog.getDiveType())) {
                                            addDiveLogToSelectionValue(mUserUid,
                                                    SelectionValue.NODE_DIVE_TYPE_VALUES,
                                                    mCurrentDiveLog.getDiveType(),
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }
                                        //</editor-fold> DiveType

                                        //<editor-fold desc="DiveEntry">
                                        if (okToRemoveDiveLogFromSelectionValue(mOriginalDiveEntry, mCurrentDiveLog.getDiveEntry())) {
                                            removeDiveLogFromSelectionValue(mUserUid,
                                                    SelectionValue.NODE_DIVE_ENTRY_VALUES,
                                                    mOriginalDiveEntry,
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }

                                        if (okToAddDiveLogToSelectionValue(mOriginalDiveEntry, mCurrentDiveLog.getDiveEntry())) {
                                            addDiveLogToSelectionValue(mUserUid,
                                                    SelectionValue.NODE_DIVE_ENTRY_VALUES,
                                                    mCurrentDiveLog.getDiveEntry(),
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }
                                        //</editor-fold> DiveEntry

                                        //<editor-fold desc="WeatherCondition">
                                        if (okToRemoveDiveLogFromSelectionValue(mOriginalWeatherCondition, mCurrentDiveLog.getWeatherCondition())) {
                                            removeDiveLogFromSelectionValue(mUserUid,
                                                    SelectionValue.NODE_WEATHER_CONDITION_VALUES,
                                                    mOriginalWeatherCondition,
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }

                                        if (okToAddDiveLogToSelectionValue(mOriginalWeatherCondition, mCurrentDiveLog.getWeatherCondition())) {
                                            addDiveLogToSelectionValue(mUserUid,
                                                    SelectionValue.NODE_WEATHER_CONDITION_VALUES,
                                                    mCurrentDiveLog.getWeatherCondition(),
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }
                                        //</editor-fold> WeatherCondition

                                        //<editor-fold desc="CurrentCondition">
                                        if (okToRemoveDiveLogFromSelectionValue(mOriginalCurrentCondition, mCurrentDiveLog.getCurrentCondition())) {
                                            removeDiveLogFromSelectionValue(mUserUid,
                                                    SelectionValue.NODE_CURRENT_VALUES,
                                                    mOriginalCurrentCondition,
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }

                                        if (okToAddDiveLogToSelectionValue(mOriginalCurrentCondition, mCurrentDiveLog.getCurrentCondition())) {
                                            addDiveLogToSelectionValue(mUserUid,
                                                    SelectionValue.NODE_CURRENT_VALUES,
                                                    mCurrentDiveLog.getCurrentCondition(),
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }
                                        //</editor-fold> CurrentCondition

                                        //<editor-fold desc="SeaCondition">
                                        if (okToRemoveDiveLogFromSelectionValue(mOriginalSeaCondition, mCurrentDiveLog.getSeaCondition())) {
                                            removeDiveLogFromSelectionValue(mUserUid,
                                                    SelectionValue.NODE_SEA_CONDITION_VALUES,
                                                    mOriginalSeaCondition,
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }

                                        if (okToAddDiveLogToSelectionValue(mOriginalSeaCondition, mCurrentDiveLog.getSeaCondition())) {
                                            addDiveLogToSelectionValue(mUserUid,
                                                    SelectionValue.NODE_SEA_CONDITION_VALUES,
                                                    mCurrentDiveLog.getSeaCondition(),
                                                    mCurrentDiveLog.getDiveLogUid());
                                        }
                                        //</editor-fold> SeaCondition

                                        dismiss();
                                    }
                                }

                        );

                        Button cancelButton = diveConditionsDialog.getButton(Dialog.BUTTON_NEGATIVE);
                        cancelButton.setTextSize(16);
                        cancelButton.setOnClickListener(new View.OnClickListener()

                                                        {
                                                            @Override
                                                            public void onClick(View v) {
                                                                // Cancel
                                                                dismiss();
                                                            }
                                                        }
                        );
                    }
                }
        );
    }

    private boolean okToRemoveDiveLogFromSelectionValue(String originalSelectionValue, String proposedSelectionValue) {
        boolean okToRemove = false;

        if (originalSelectionValue != null
                && !originalSelectionValue.equals(proposedSelectionValue)
                && !originalSelectionValue.isEmpty()
                && !MyMethods.containsInvalidCharacters(originalSelectionValue)) {
            okToRemove = true;
        }

        return okToRemove;
    }

    private boolean okToAddDiveLogToSelectionValue(String originalSelectionValue, String proposedSelectionValue) {
        boolean okToAdd = false;

        if (originalSelectionValue != null) {

            if (proposedSelectionValue != null
                    && !originalSelectionValue.equals(proposedSelectionValue)
                    && !proposedSelectionValue.isEmpty()
                    && !MyMethods.containsInvalidCharacters(proposedSelectionValue)) {
                okToAdd = true;
            }
        } else {
            if (proposedSelectionValue != null
                    && !proposedSelectionValue.isEmpty()
                    && !MyMethods.containsInvalidCharacters(proposedSelectionValue)) {
                okToAdd = true;
            }
        }

        return okToAdd;
    }

    private void addDiveLogToSelectionValue(String userUid, String selectionValueField, String selectedValueKey, String diveLogUid) {
        SelectionValue.addDiveLogToSelectionValue(userUid, selectionValueField, selectedValueKey, diveLogUid);
    }

    private void removeDiveLogFromSelectionValue(String userUid, String selectionValueField, String selectedValueKey, String diveLogUid) {
        SelectionValue.removeDiveLogFromSelectionValue(userUid, selectionValueField, selectedValueKey, diveLogUid);
    }

    @Subscribe
    public void onEvent(MyEvents.dismissDialog event) {
        dismiss();
    }


    @Subscribe
    public void onEvent(MyEvents.updateSelectionValue event) {
        SelectionValue proposedSelectionValue = event.getSelectionValue();
        String value = proposedSelectionValue.getValue();

        switch (proposedSelectionValue.getNodeName()) {

            case SelectionValue.NODE_DIVE_STYLE_VALUES:
                mCurrentDiveLog.setDiveStyle(value);
                btnDiveStyle.setText(getDiveStyle());
                break;

            case SelectionValue.NODE_DIVE_TYPE_VALUES:
                mCurrentDiveLog.setDiveType(value);
                btnDiveType.setText(getDiveType());
                break;

            case SelectionValue.NODE_DIVE_ENTRY_VALUES:
                mCurrentDiveLog.setDiveEntry(value);
                btnDiveEntry.setText(getDiveEntry());
                break;

            case SelectionValue.NODE_WEATHER_CONDITION_VALUES:
                mCurrentDiveLog.setWeatherCondition(value);
                btnWeatherCondition.setText(getWeatherCondition());
                break;

            case SelectionValue.NODE_CURRENT_VALUES:
                mCurrentDiveLog.setCurrentCondition(value);
                btnCurrentCondition.setText(getCurrentCondition());
                break;

            case SelectionValue.NODE_SEA_CONDITION_VALUES:
                mCurrentDiveLog.setSeaCondition(value);
                btnSeaCondition.setText(getSeaCondition());
                break;

            default:
                Timber.e("onEvent(): Invalid SelectionValue. Node = %s", proposedSelectionValue.getNodeName());
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_dive_conditions, null, false);

        // find the dialog's views
        btnDiveStyle = (Button) view.findViewById(R.id.btnDiveStyle);
        btnDiveType = (Button) view.findViewById(R.id.btnDiveType);
        btnDiveEntry = (Button) view.findViewById(R.id.btnDiveEntry);
        btnWeatherCondition = (Button) view.findViewById(R.id.btnWeatherCondition);
        btnCurrentCondition = (Button) view.findViewById(R.id.btnCurrentCondition);
        btnSeaCondition = (Button) view.findViewById(R.id.btnSeaCondition);

        btnDiveStyle.setText(getDiveStyle());
        btnDiveType.setText(getDiveType());
        btnDiveEntry.setText(getDiveEntry());
        btnWeatherCondition.setText(getWeatherCondition());
        btnCurrentCondition.setText(getCurrentCondition());
        btnSeaCondition.setText(getSeaCondition());

        btnDiveStyle.setOnClickListener(this);
        btnDiveType.setOnClickListener(this);
        btnDiveEntry.setOnClickListener(this);
        btnWeatherCondition.setOnClickListener(this);
        btnCurrentCondition.setOnClickListener(this);
        btnSeaCondition.setOnClickListener(this);

        // build the dialog
        diveConditionsDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return diveConditionsDialog;
    }

    private String getDiveStyle() {
        String diveStyleText = String.format(getString(R.string.prefix_diveStyle), "");
        String value = mCurrentDiveLog.getDiveStyle();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !MyMethods.containsInvalidCharacters(value)) {
//            diveStyleText = String.format(getString(R.string.prefix_diveStyle), value);
            diveStyleText = value;

        }
        return diveStyleText;
    }

    private String getDiveType() {
        String diveTypeText = String.format(getString(R.string.prefix_diveType), "");
        String value = mCurrentDiveLog.getDiveType();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !MyMethods.containsInvalidCharacters(value)) {
//            diveTypeText = String.format(getString(R.string.prefix_diveType), value);
            diveTypeText = value;

        }
        return diveTypeText;
    }

    private String getDiveEntry() {
        String diveEntryText = String.format(getString(R.string.prefix_diveEntry), "");
        String value = mCurrentDiveLog.getDiveEntry();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !MyMethods.containsInvalidCharacters(value)) {
//            diveEntryText = String.format(getString(R.string.prefix_diveEntry), value);
            diveEntryText = value;

        }
        return diveEntryText;
    }

    private String getWeatherCondition() {
        String weatherConditionText = String.format(getString(R.string.prefix_weather), "");
        String value = mCurrentDiveLog.getWeatherCondition();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE) && !MyMethods.containsInvalidCharacters(value)) {
//            weatherConditionText = String.format(getString(R.string.prefix_weather), value);
            weatherConditionText = value;
        }
        return weatherConditionText;
    }

    private String getCurrentCondition() {
        String currentConditionText = String.format(getString(R.string.prefix_currents), "");
        String value = mCurrentDiveLog.getCurrentCondition();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE) && !MyMethods.containsInvalidCharacters(value)) {
//            currentConditionText = String.format(getString(R.string.prefix_currents), value);
            currentConditionText = value;
        }
        return currentConditionText;
    }

    private String getSeaCondition() {
        String seaConditionText = String.format(getString(R.string.prefix_seas), "");
        String value = mCurrentDiveLog.getSeaCondition();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !MyMethods.containsInvalidCharacters(value)) {
//            seaConditionText = String.format(getString(R.string.prefix_seas), value);
            seaConditionText = value;
        }
        return seaConditionText;
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

            case R.id.btnDiveStyle:
                showSelectionValuePickerDialog(fm, SelectionValue.NODE_DIVE_STYLE_VALUES,
                        mCurrentDiveLog.getDiveStyle());
                break;

            case R.id.btnDiveType:
                showSelectionValuePickerDialog(fm, SelectionValue.NODE_DIVE_TYPE_VALUES,
                        mCurrentDiveLog.getDiveType());
                break;

            case R.id.btnDiveEntry:
                showSelectionValuePickerDialog(fm, SelectionValue.NODE_DIVE_ENTRY_VALUES,
                        mCurrentDiveLog.getDiveEntry());
                break;

            case R.id.btnWeatherCondition:
                showSelectionValuePickerDialog(fm, SelectionValue.NODE_WEATHER_CONDITION_VALUES,
                        mCurrentDiveLog.getWeatherCondition());
                break;
            case R.id.btnCurrentCondition:
                showSelectionValuePickerDialog(fm, SelectionValue.NODE_CURRENT_VALUES,
                        mCurrentDiveLog.getCurrentCondition());
                break;

            case R.id.btnSeaCondition:
                showSelectionValuePickerDialog(fm, SelectionValue.NODE_SEA_CONDITION_VALUES,
                        mCurrentDiveLog.getSeaCondition());
                break;

            default:
                Timber.e("onClick(): Invalid button. buttonId = %d", v.getId());

        }
    }

    private void showSelectionValuePickerDialog(final FragmentManager fm,
                                                final String selectionValueNode,
                                                String value) {
        if (!value.equals(MySettings.NOT_AVAILABLE)
                && !value.isEmpty()
                && !MyMethods.containsInvalidCharacters(value)) {
            SelectionValue.nodeUserSelectionValue(mUserUid, selectionValueNode, value)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                SelectionValue selectionValue = dataSnapshot.getValue(SelectionValue.class);
                                dialogSelectionValuesPicker selectionValuePickerDialog =
                                        dialogSelectionValuesPicker
                                                .newInstance(mUserUid, mCurrentDiveLog.getDiveLogUid(), null,
                                                        selectionValueNode, selectionValue);
                                selectionValuePickerDialog.show(fm, "selectionValuePickerDialog");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
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
