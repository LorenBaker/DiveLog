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

import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.AppSettings;
import com.lbconsulting.divelogfirebase.models.SelectionValue;
import com.lbconsulting.divelogfirebase.utils.MyEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;


/**
 * A dialog where the user selects the area, state, and country filters
 */
public class dialogDiveLogFilter extends DialogFragment implements View.OnClickListener {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_USER_APP_SETTINGS_JSON = "argUserAppSettingsJson";

    private AlertDialog mDiveLogFilterDialog;
    private String mUserUid;
    private AppSettings mUserAppSettings;

    private String mProposedArea;
    private String mProposedState;
    private String mProposedCountry;

    private String mTitle;

    private Button btnArea;
    private Button btnState;
    private Button btnCountry;

    private Button cancelButton;


    public dialogDiveLogFilter() {
        // Empty constructor required for DialogFragment
    }

    public static dialogDiveLogFilter newInstance(@NonNull String userUid,
                                                  @NonNull String userAppSettingsJason) {
        dialogDiveLogFilter frag = new dialogDiveLogFilter();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_USER_APP_SETTINGS_JSON, userAppSettingsJason);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        EventBus.getDefault().register(this);
        Bundle args = getArguments();

        if (args.containsKey(ARG_USER_UID) && args.containsKey(ARG_USER_APP_SETTINGS_JSON)) {
            mUserUid = args.getString(ARG_USER_UID);
            String userAppSettingsJson = args.getString(ARG_USER_APP_SETTINGS_JSON);
            Gson gson = new Gson();
            mUserAppSettings = gson.fromJson(userAppSettingsJson, AppSettings.class);
            mTitle = "Select Dive Log Filter";

            mProposedArea = mUserAppSettings.getAreaFilter();
            mProposedState = mUserAppSettings.getStateFilter();
            mProposedCountry = mUserAppSettings.getCountryFilter();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mDiveLogFilterDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button saveButton = mDiveLogFilterDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Save
                        mUserAppSettings.setAreaFilter(mProposedArea);
                        mUserAppSettings.setStateFilter(mProposedState);
                        mUserAppSettings.setCountryFilter(mProposedCountry);
                        AppSettings.save(mUserUid, mUserAppSettings);
                        dismiss();
                    }
                });

                cancelButton = mDiveLogFilterDialog.getButton(Dialog.BUTTON_NEGATIVE);
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
        switch (event.getSelectionValue().getNodeName()) {
            case SelectionValue.NODE_AREA_VALUES:
                mProposedArea = event.getSelectionValue().getValue();
                setBtnAreaText(mProposedArea);
                break;

            case SelectionValue.NODE_STATE_VALUES:
                mProposedState = event.getSelectionValue().getValue();
                setBtnStateText(mProposedState);
                break;

            case SelectionValue.NODE_COUNTRY_VALUES:
                mProposedCountry = event.getSelectionValue().getValue();
                setBtnCountryText(mProposedCountry);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Timber.i("onResume()");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_dive_log_filter, null, false);

        // find the dialog's views

        btnArea = (Button) view.findViewById(R.id.btnArea);
        btnArea.setOnClickListener(this);
        btnState = (Button) view.findViewById(R.id.btnState);
        btnState.setOnClickListener(this);
        btnCountry = (Button) view.findViewById(R.id.btnCountry);
        btnCountry.setOnClickListener(this);

        setBtnAreaText(mUserAppSettings.getAreaFilter());
        setBtnStateText(mUserAppSettings.getStateFilter());
        setBtnCountryText(mUserAppSettings.getCountryFilter());

        // build the dialog
        mDiveLogFilterDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mDiveLogFilterDialog;
    }

    private void setBtnAreaText(String area) {
        String btnAreaText = String.format("Dive Area: %s", area);
        btnArea.setText(btnAreaText);
    }

    private void setBtnStateText(String state) {
        String btnStateText = String.format("Dive State: %s", state);
        btnState.setText(btnStateText);
    }

    private void setBtnCountryText(String country) {
        String btnCountryText = String.format("Dive Country: %s", country);
        btnCountry.setText(btnCountryText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnArea:
                dialogAreaStateCountryPicker areaStateCountryPickerDialog = dialogAreaStateCountryPicker
                        .newInstance(mUserUid, SelectionValue.NODE_AREA_VALUES, mProposedArea);
                areaStateCountryPickerDialog.show(getActivity().getSupportFragmentManager(),
                        "dialogAreaStateCountryPicker");
                break;

            case R.id.btnState:
                areaStateCountryPickerDialog = dialogAreaStateCountryPicker
                        .newInstance(mUserUid, SelectionValue.NODE_STATE_VALUES, mProposedState);
                areaStateCountryPickerDialog.show(getActivity().getSupportFragmentManager(),
                        "dialogAreaStateCountryPicker");

                break;

            case R.id.btnCountry:
                areaStateCountryPickerDialog = dialogAreaStateCountryPicker
                        .newInstance(mUserUid, SelectionValue.NODE_COUNTRY_VALUES, mProposedCountry);
                areaStateCountryPickerDialog.show(getActivity().getSupportFragmentManager(),
                        "dialogAreaStateCountryPicker");

                break;
        }
    }
}
