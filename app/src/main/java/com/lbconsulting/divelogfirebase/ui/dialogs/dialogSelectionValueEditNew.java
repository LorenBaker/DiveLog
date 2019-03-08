package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.DiveSite;
import com.lbconsulting.divelogfirebase.models.SelectionValue;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * A dialog where the user edits or creates a new SelectionValue
 */
public class dialogSelectionValueEditNew extends DialogFragment {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    //    private static final String ARG_SELECTION_VALUE_FIELD = "argNodeName";
    private static final String ARG_SELECTION_VALUE_JSON = "argSelectionValueField";
    private static final String ARG_IS_NEW_SELECTION_VALUE = "argIsNewSelectionValue";

    private AlertDialog mSelectionValueEditNewDialog;

    private String mUserUid;
    private String mDiveLogUid;
    //    private String mSelectionValueField;
    private SelectionValue mOriginalSelectionValue;
    private SelectionValue mProposedSelectionValue;

    private String mTitle;

    private TextInputEditText txtName;
    private TextInputLayout txtName_input_layout;

    private boolean mIsNewSelectionValue;
    private Vibrator mVibrator;

    public dialogSelectionValueEditNew() {
        // Empty constructor required for DialogFragment
    }

    public static dialogSelectionValueEditNew newInstance(@NonNull String userUid,
                                                          @NonNull String diveLogUid,
                                                          @NonNull String selectionValueJson,
                                                          boolean isNewSelectionValue) {
        dialogSelectionValueEditNew frag = new dialogSelectionValueEditNew();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putString(ARG_SELECTION_VALUE_JSON, selectionValueJson);
        args.putBoolean(ARG_IS_NEW_SELECTION_VALUE, isNewSelectionValue);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
            String selectionValueJson = args.getString(ARG_SELECTION_VALUE_JSON);
            Gson gson = new Gson();
            mOriginalSelectionValue = gson.fromJson(selectionValueJson, SelectionValue.class);
            mProposedSelectionValue = gson.fromJson(selectionValueJson, SelectionValue.class);
            mIsNewSelectionValue = args.getBoolean(ARG_IS_NEW_SELECTION_VALUE);
        }

        if (mIsNewSelectionValue) {
            if (mOriginalSelectionValue != null) {
                switch (mOriginalSelectionValue.getNodeName()) {
                    case SelectionValue.NODE_AREA_VALUES:
                        mTitle = getString(R.string.createArea_title);
                        break;
                    case SelectionValue.NODE_STATE_VALUES:
                        mTitle = getString(R.string.createState_title);
                        break;
                    case SelectionValue.NODE_COUNTRY_VALUES:
                        mTitle = getString(R.string.createCountry_title);
                        break;
                    case SelectionValue.NODE_CURRENT_VALUES:
                        mTitle = getString(R.string.createCurrentCondition_title);
                        break;
                    case SelectionValue.NODE_DIVE_ENTRY_VALUES:
                        mTitle = getString(R.string.createDiveEntry_title);
                        break;
                    case SelectionValue.NODE_DIVE_STYLE_VALUES:
                        mTitle = getString(R.string.createDiveStyle_title);
                        break;
                    case SelectionValue.NODE_DIVE_TANK_VALUES:
                        mTitle = getString(R.string.createDiveTank_title);
                        break;
                    case SelectionValue.NODE_DIVE_TYPE_VALUES:
                        mTitle = getString(R.string.createDiveType_title);
                        break;
                    case SelectionValue.NODE_SEA_CONDITION_VALUES:
                        mTitle = getString(R.string.createSeaCondition_title);
                        break;
                    case SelectionValue.NODE_WEATHER_CONDITION_VALUES:
                        mTitle = getString(R.string.createWeatherCondition_title);
                        break;
                }
            }
        } else {
            switch (mOriginalSelectionValue.getNodeName()) {
                case SelectionValue.NODE_AREA_VALUES:
                    mTitle = getString(R.string.editArea_title);
                    break;
                case SelectionValue.NODE_STATE_VALUES:
                    mTitle = getString(R.string.editState_title);
                    break;
                case SelectionValue.NODE_COUNTRY_VALUES:
                    mTitle = getString(R.string.editCountry_title);
                    break;
                case SelectionValue.NODE_CURRENT_VALUES:
                    mTitle = getString(R.string.editCurrentCondition_title);
                    break;
                case SelectionValue.NODE_DIVE_ENTRY_VALUES:
                    mTitle = getString(R.string.editDiveEntry_title);
                    break;
                case SelectionValue.NODE_DIVE_STYLE_VALUES:
                    mTitle = getString(R.string.editDiveStyle_title);
                    break;
                case SelectionValue.NODE_DIVE_TANK_VALUES:
                    mTitle = getString(R.string.editDiveTank_title);
                    break;
                case SelectionValue.NODE_DIVE_TYPE_VALUES:
                    mTitle = getString(R.string.editDiveType_title);
                    break;
                case SelectionValue.NODE_SEA_CONDITION_VALUES:
                    mTitle = getString(R.string.editSeaCondition_title);
                    break;
                case SelectionValue.NODE_WEATHER_CONDITION_VALUES:
                    mTitle = getString(R.string.editWeatherCondition_title);
                    break;
            }
        }

        mVibrator = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mSelectionValueEditNewDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = mSelectionValueEditNewDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyValueNotEmpty();
                    }
                });

                Button cancelButton = mSelectionValueEditNewDialog.getButton(Dialog.BUTTON_NEGATIVE);
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_selection_value_edit_new, null, false);

        // find the dialog's views
        txtName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        txtName = (TextInputEditText) view.findViewById(R.id.txtName);
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtName_input_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // empty
            }
        });

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (MyMethods.isInvalidCharacter(source.charAt(i))) {
                        vibratePhone();
                        return "";
                    }
                }
                return null;
            }

            public void vibratePhone() {
//                long pattern[] = {0, 100, 200, 300, 400};
                final int vibrationDuration = 200; // millis
                if (mVibrator == null) {
                    mVibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
                }
                if (mVibrator.hasVibrator()) {
                    mVibrator.vibrate(vibrationDuration);
                }
            }

            public void stopVibrate(View v) {
                mVibrator.cancel();
            }

        };

        txtName.setFilters(new InputFilter[]{filter});


        txtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE && keyEvent == null) {
                    verifyValueNotEmpty();
                    return true;
                }
                return false;
            }
        });

        txtName.setText(mOriginalSelectionValue.getValue());

        // build the dialog
        mSelectionValueEditNewDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mSelectionValueEditNewDialog;
    }

    private void verifyValueNotEmpty() {
        String proposedSelectionValueName = txtName.getText().toString().trim();
        if (proposedSelectionValueName.isEmpty()) {
            String errorMsg = getActivity().getString(R.string.selectionValue_isEmpty_error);
            txtName_input_layout.setError(errorMsg);

        } else if (MyMethods.containsInvalidCharacters(proposedSelectionValueName)) {
            String errorMsg = getActivity().getString(R.string.selectionValue_containsInvalidCharacters_error);
            txtName_input_layout.setError(errorMsg);

        } else {
            mProposedSelectionValue.setValue(proposedSelectionValueName);
            trySavingSelectionValue(mOriginalSelectionValue, mProposedSelectionValue);
        }
    }

    private void trySavingSelectionValue(final SelectionValue originalSelectionValue,
                                         final SelectionValue proposedSelectionValue) {
        // get all selectionValues
        final List<SelectionValue> selectionValueList = new ArrayList<>();
        SelectionValue.nodeSelectionValues(mUserUid, proposedSelectionValue.getNodeName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                SelectionValue selectionValue = snapshot
                                        .getValue(SelectionValue.class);
                                selectionValueList.add(selectionValue);
                            }
                        }
                        continueTryingToSaveSelectionValue();
                    }

                    private void continueTryingToSaveSelectionValue() {
                        // To save a new or updated SelectionValue:
                        //  1. Verify that the SelectionValue's name does not exist in the database,
                        //     If ok to save selectionValue, then
                        //      2. if not a new SelectionValue remove the original selectionValue
                        //      3. Save the proposed selectionValue the database
                        //      4. Update and save each diveSite or diveLog that uses the SelectionValue with
                        // the SelectionValue's revised name and Uid
                        if (SelectionValue.okToSaveSelectionValue(selectionValueList, proposedSelectionValue)) {

                            // replace the original SelectionValue with the proposed SelectionValue
                            if (!mIsNewSelectionValue) {
                                SelectionValue.remove(mUserUid, originalSelectionValue);
                            }
                            SelectionValue.save(mUserUid, proposedSelectionValue);

                            switch (proposedSelectionValue.getNodeName()) {
                                case SelectionValue.NODE_AREA_VALUES:
                                case SelectionValue.NODE_STATE_VALUES:
                                case SelectionValue.NODE_COUNTRY_VALUES:

                                    DiveSite.updateDiveSitesWithSelectionValues(
                                            mUserUid, proposedSelectionValue);
                                    EventBus.getDefault().post(new MyEvents
                                            .updateSelectionValue(proposedSelectionValue));
                                    dismiss();
                                    break;

                                case SelectionValue.NODE_CURRENT_VALUES:
                                case SelectionValue.NODE_DIVE_ENTRY_VALUES:
                                case SelectionValue.NODE_DIVE_STYLE_VALUES:
                                case SelectionValue.NODE_DIVE_TANK_VALUES:
                                case SelectionValue.NODE_DIVE_TYPE_VALUES:
                                case SelectionValue.NODE_SEA_CONDITION_VALUES:
                                case SelectionValue.NODE_WEATHER_CONDITION_VALUES:
                                    if (proposedSelectionValue.getDiveLogs() == null) {
                                        proposedSelectionValue.setDiveLogs(new HashMap<String, Boolean>());
                                    }
                                    proposedSelectionValue.getDiveLogs().put(mDiveLogUid, true);
                                    DiveLog.updateDiveLogsWithSelectionValue(
                                            mUserUid, proposedSelectionValue);
                                    EventBus.getDefault().post(new MyEvents.dismissDialog());
                                    dismiss();
                                    break;
                            }
                        } else {
                            // the proposed name already nameExists in the db
                            txtName.setText(mOriginalSelectionValue.getValue());
                            String errorMsg = String.format(getActivity()
                                    .getString(R.string.selectionValueName_nameExists_error), proposedSelectionValue.getValue());
                            txtName_input_layout.setError(errorMsg);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
    }
}
