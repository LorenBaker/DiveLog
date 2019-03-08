package com.lbconsulting.divelogfirebase.ui.dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.SelectionValue;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * A dialog where the user edits or creates a new area, state, or country SelectionValue
 */
public class dialogAreaStateCountryEditNew extends DialogFragment {

    public static final String ARG_USER_UID = "argUserUid";
    public static final String ARG_NODE_NAME = "argNodeName";
    private static final String ARG_SELECTION_VALUE_JSON = "argSelectionValueUid";

    private AlertDialog mSelectionValueEditNewDialog;

    private String mUserUid;
    private String mNodeName;
    private SelectionValue mSelectionValue;

    private String mTitle;

    private EditText txtName;
    private TextInputLayout txtName_input_layout;

    private boolean isNewSelectionValue;


    public dialogAreaStateCountryEditNew() {
        // Empty constructor required for DialogFragment
    }

    public static dialogAreaStateCountryEditNew newInstance(@NonNull String userUid,
                                                            @NonNull String nodeName,
                                                            @NonNull String selectionValueJson) {
        dialogAreaStateCountryEditNew frag = new dialogAreaStateCountryEditNew();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_NODE_NAME, nodeName);
        args.putString(ARG_SELECTION_VALUE_JSON, selectionValueJson);
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
            mNodeName = args.getString(ARG_NODE_NAME);
            String selectionValueJson = args.getString(ARG_SELECTION_VALUE_JSON);
            Gson gson = new Gson();
            mSelectionValue = gson.fromJson(selectionValueJson, SelectionValue.class);
        }

//        isNewSelectionValue = mSelectionValue.getKey().equals(MySettings.NOT_AVAILABLE);

        if (isNewSelectionValue) {
            if (mNodeName != null) {
                switch (mNodeName) {
                    case SelectionValue.NODE_AREA_VALUES:
                        mTitle = "Create Area";
                        break;
                    case SelectionValue.NODE_STATE_VALUES:
                        mTitle = "Create State";
                        break;
                    case SelectionValue.NODE_COUNTRY_VALUES:
                        mTitle = "Create Country";
                        break;
                }
            }
        } else {
            switch (mNodeName) {
                case SelectionValue.NODE_AREA_VALUES:
                    mTitle = "Edit Area";
                    break;
                case SelectionValue.NODE_STATE_VALUES:
                    mTitle = "Edit State";
                    break;
                case SelectionValue.NODE_COUNTRY_VALUES:
                    mTitle = "Edit Country";
                    break;
            }
        }
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_area_state_country_edit_new, null, false);

        // find the dialog's views
        txtName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        txtName = (EditText) view.findViewById(R.id.txtName);
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

        txtName.setText(mSelectionValue.getValue());

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
        } else {
            mSelectionValue.setValue(proposedSelectionValueName);
            trySavingSelectionValue(mSelectionValue);
        }
    }

    private void trySavingSelectionValue(final SelectionValue proposedSelectionValue) {
        // get all selectionValues
        final List<SelectionValue> selectionValueList = new ArrayList<>();
        SelectionValue.nodeSelectionValues(mUserUid, mNodeName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                SelectionValue selectionValue = snapshot.getValue(SelectionValue.class);
                                selectionValueList.add(selectionValue);
                            }
                            continueTryingToSaveSelectionValue();
                        }
                    }

                    private void continueTryingToSaveSelectionValue() {
                        // To save a new or updated SelectionValue:
                        //  1. Verify that the SelectionValue's name does not exist in the database,
                        //     If ok to save selectionValue, then
                        //      2. Save the selectionValue the database
                        //      3. Update and save each diveLog that uses the SelectionValue with the SelectionValue's revised name and Uid
 /*                       if (SelectionValue.okToSaveSelectionValue(selectionValueList, proposedSelectionValue)) {
                            SelectionValue.save(mUserUid, mNodeName, proposedSelectionValue);

                            int actionValue = MySettings.EDITED_SELECTION_VALUE;
                            if (isNewSelectionValue) {
                                actionValue = MySettings.NEW_SELECTION_VALUE;
                            } else {
                                DiveSite.updateDiveSitesWithSelectionValue(mUserUid, mNodeName, proposedSelectionValue);
                            }
                            EventBus.getDefault().post(new MyEvents
                                    .updateSelectionValue(mNodeName, proposedSelectionValue, actionValue));
                            dismiss();


                        } else {
                            // the proposed name already nameExists in the db
                            txtName.setText(mSelectionValue.getValue());
                            String errorMsg = String.format(getActivity()
                                    .getString(R.string.selectionValueName_nameExists_error), proposedSelectionValue.getValue());
                            txtName_input_layout.setError(errorMsg);
                        }*/
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
//        EventBus.getDefault().unregister(this);
    }
}
