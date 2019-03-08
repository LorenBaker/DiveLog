package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.lbconsulting.divelogfirebase.models.DiveSite;
import com.lbconsulting.divelogfirebase.models.SelectionValue;
import com.lbconsulting.divelogfirebase.utils.MyEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A dialog where the user edits or creates a new dive site
 */
public class dialogDiveSiteEditNew extends DialogFragment implements View.OnClickListener {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_ORIGINAL_DIVE_SITE_JSON = "argOriginalDiveSiteJson";
    private static final String ARG_NEW_DIVE_SITE_JSON = "argNewDiveSiteJson";

    private AlertDialog mDiveSiteNewEditDialog;
    private String mUserUid;
    private DiveSite mOriginalDiveSite;
    private DiveSite mProposedDiveSite;
    private DiveSite mNewDiveSite;

    private boolean mIsNewDiveSite;
    private String mTitle;

    private EditText txtName;
    private TextInputLayout txtName_input_layout;
    private Button btnArea;
    private Button btnState;
    private Button btnCountry;


    public static dialogDiveSiteEditNew newInstance(@NonNull String userUid,
                                                    @NonNull DiveSite originalDiveSite,
                                                    @Nullable DiveSite newDiveSite) {
        Gson gson = new Gson();
        dialogDiveSiteEditNew frag = new dialogDiveSiteEditNew();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        String originalDiveSiteJson = gson.toJson(originalDiveSite);
        args.putString(ARG_ORIGINAL_DIVE_SITE_JSON, originalDiveSiteJson);
        if (newDiveSite != null) {
            String newDiveSiteJson = gson.toJson(newDiveSite);
            args.putString(ARG_NEW_DIVE_SITE_JSON, newDiveSiteJson);
        }
        frag.setArguments(args);
        return frag;
    }

    public dialogDiveSiteEditNew() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
        }

        Gson gson = new Gson();
        if (args.containsKey(ARG_ORIGINAL_DIVE_SITE_JSON)) {
            String originalDiveSiteJson = args.getString(ARG_ORIGINAL_DIVE_SITE_JSON);
            mOriginalDiveSite = gson.fromJson(originalDiveSiteJson, DiveSite.class);
            mProposedDiveSite = gson.fromJson(originalDiveSiteJson, DiveSite.class);
            mIsNewDiveSite = false;
        }

        if (args.containsKey(ARG_NEW_DIVE_SITE_JSON)) {
            String newDiveSiteJson = args.getString(ARG_NEW_DIVE_SITE_JSON);
            mNewDiveSite = gson.fromJson(newDiveSiteJson, DiveSite.class);
            mProposedDiveSite = gson.fromJson(newDiveSiteJson, DiveSite.class);
            mIsNewDiveSite = true;
        }

        if (mIsNewDiveSite) {
            mTitle = getString(R.string.createNewDiveSite_title);
        } else {
            mTitle = mOriginalDiveSite.getDiveSiteName();
        }

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(MyEvents.updateSelectionValue event) {
        setBtnText(event.getSelectionValue());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mDiveSiteNewEditDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = mDiveSiteNewEditDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyNameNotEmpty();
                    }
                });

                Button cancelButton = mDiveSiteNewEditDialog.getButton(Dialog.BUTTON_NEGATIVE);
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
        View view = inflater.inflate(R.layout.dialog_dive_site_edit_new, null, false);

        // find the dialog's views
        txtName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        btnArea = (Button) view.findViewById(R.id.btnArea);
        btnArea.setOnClickListener(this);
        btnState = (Button) view.findViewById(R.id.btnState);
        btnState.setOnClickListener(this);
        btnCountry = (Button) view.findViewById(R.id.btnCountry);
        btnCountry.setOnClickListener(this);

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
                    verifyNameNotEmpty();
                    return true;
                }
                return false;
            }
        });

        txtName.setText(mProposedDiveSite.getDiveSiteName());
        setBtnText(SelectionValue.NODE_AREA_VALUES, mProposedDiveSite.getArea());
        setBtnText(SelectionValue.NODE_STATE_VALUES, mProposedDiveSite.getState());
        setBtnText(SelectionValue.NODE_COUNTRY_VALUES, mProposedDiveSite.getCountry());

        // build the dialog
        mDiveSiteNewEditDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mDiveSiteNewEditDialog;
    }

    private void setBtnText(SelectionValue proposedSelectionValue) {
        switch (proposedSelectionValue.getNodeName()) {
            case SelectionValue.NODE_AREA_VALUES:
                btnArea.setTag(proposedSelectionValue);
                String btnAreaText = String.format(getString(R.string.diveArea_btnText),
                                                   proposedSelectionValue.getValue());
                btnArea.setText(btnAreaText);
                break;

            case SelectionValue.NODE_STATE_VALUES:
                btnState.setTag(proposedSelectionValue);
                String btnStateText = String.format(getString(R.string.diveState_btnText),
                                                    proposedSelectionValue.getValue());
                btnState.setText(btnStateText);
                break;

            case SelectionValue.NODE_COUNTRY_VALUES:
                btnCountry.setTag(proposedSelectionValue);
                String btnCountryText = String.format(getString(R.string.diveCountry_btnText),
                                                      proposedSelectionValue.getValue());
                btnCountry.setText(btnCountryText);
                break;

            default:
                Timber.e("onDataChange(): Unknown SelectionValue node!");
        }
    }

    private void setBtnText(final String node, final String value) {
        if (value.startsWith("[")) {
            switch (node) {
                case SelectionValue.NODE_AREA_VALUES:
                    btnArea.setTag(SelectionValue.getDefault(SelectionValue.NODE_AREA_VALUES));
                    String btnAreaText = String.format(getString(R.string.diveArea_btnText), value);
                    btnArea.setText(btnAreaText);
                    break;

                case SelectionValue.NODE_STATE_VALUES:
                    btnState.setTag(SelectionValue.getDefault(SelectionValue.NODE_STATE_VALUES));
                    String btnStateText = String.format(getString(R.string.diveState_btnText),
                                                        value);
                    btnState.setText(btnStateText);
                    break;

                case SelectionValue.NODE_COUNTRY_VALUES:
                    btnCountry.setTag(SelectionValue.getDefault(SelectionValue
                                                                        .NODE_COUNTRY_VALUES));
                    String btnCountryText = String.format(getString(R.string.diveCountry_btnText)
                            , value);
                    btnCountry.setText(btnCountryText);
                    break;

                default:
                    Timber.e("onDataChange(): Unknown SelectionValue node!");
            }
        } else {
            SelectionValue.nodeUserSelectionValue(mUserUid, node, value)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                SelectionValue proposedSelectionValue = dataSnapshot.getValue
                                        (SelectionValue.class);
                                setBtnText(proposedSelectionValue);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Timber.e("onCancelled(): DatabaseError: %s.", databaseError
                                    .getMessage());
                        }
                    });
        }
    }


    private void verifyNameNotEmpty() {
        String proposedDiveSiteName = txtName.getText().toString().trim();
        if (proposedDiveSiteName.isEmpty()) {
            String errorMsg = getActivity().getString(R.string.personName_isEmpty_error);
            txtName_input_layout.setError(errorMsg);
        } else {
            mProposedDiveSite.setDiveSiteName(proposedDiveSiteName);

            SelectionValue areaSelectionValue = (SelectionValue) btnArea.getTag();
            if (areaSelectionValue != null) {
                mProposedDiveSite.setArea(areaSelectionValue.getValue());
            } else {
                areaSelectionValue = SelectionValue.getDefault(SelectionValue.NODE_AREA_VALUES);
                if (areaSelectionValue != null) {
                    mProposedDiveSite.setArea(areaSelectionValue.getValue());
                }
            }

            SelectionValue stateSelectionValue = (SelectionValue) btnState.getTag();
            if (stateSelectionValue != null) {
                mProposedDiveSite.setState(stateSelectionValue.getValue());
            } else {
                stateSelectionValue = SelectionValue.getDefault(SelectionValue.NODE_STATE_VALUES);
                if (stateSelectionValue != null) {
                    mProposedDiveSite.setState(stateSelectionValue.getValue());
                }
            }

            SelectionValue countrySelectionValue = (SelectionValue) btnCountry.getTag();
            if (countrySelectionValue != null) {
                mProposedDiveSite.setCountry(countrySelectionValue.getValue());
            } else {
                countrySelectionValue = SelectionValue.getDefault(SelectionValue
                                                                          .NODE_COUNTRY_VALUES);
                if (countrySelectionValue != null) {
                    mProposedDiveSite.setCountry(countrySelectionValue.getValue());
                }
            }

            trySavingDiveSite(mProposedDiveSite);
        }
    }

    private void trySavingDiveSite(final DiveSite proposedDiveSite) {
        // get all DiveSites
        final List<DiveSite> diveSiteList = new ArrayList<>();
        DiveSite.nodeUserDiveSites(mUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DiveSite diveSite = snapshot.getValue(DiveSite.class);
                        diveSiteList.add(diveSite);
                    }
                }
                continueTryingToSaveDiveSite();
            }

            private void continueTryingToSaveDiveSite() {
                // To save a new or updated DiveSite:
                //  1.  Verify that is okToSaveDiveSite,
                //  If ok to save, then:
                //      2.  Save the diveSite the database
                //          Note: Saving the diveSite will also update the diveSite name, area,
                //          state, and country fields of each diveLog associated with the diveSite.
                //      3.  Remove the original diveSite from the area, state, and country
                //          SelectionValues
                //      4.  Add the proposed diveSite to the area, state and country
                //          SelectionValues
                //  If not ok to save, then
                //      5.  Show an error message

                if (DiveSite.okToSaveDiveSite(diveSiteList, proposedDiveSite)) {
                    String proposedDiveSiteUid;
                    // Save proposed diveSite to the database
                    if (mIsNewDiveSite) {
                        String originalDiveLogUid = (String) mNewDiveSite.getDiveLogs().keySet()
                                .toArray()[0];
                        String originalDiveSiteUid;
                        if (mOriginalDiveSite != null) {
                            originalDiveSiteUid = mOriginalDiveSite.getDiveSiteUid();
                            if (originalDiveLogUid != null && originalDiveSiteUid != null) {
                                DiveSite.removeDiveLogFromDiveSite(mUserUid,
                                                                   originalDiveSiteUid,
                                                                   originalDiveLogUid);
                            }
                        }
                        proposedDiveSiteUid = DiveSite.save(mUserUid, proposedDiveSite);
                    } else {
                        proposedDiveSiteUid = DiveSite.save(mUserUid, proposedDiveSite);
                    }

                    // Remove the original diveSite from the area, state, and country
                    // SelectionValues
                    if (mOriginalDiveSite != null) {

                        SelectionValue.removeDiveSiteFromSelectionValue(mUserUid,
                                                                        SelectionValue
                                                                                .NODE_AREA_VALUES,
                                                                        mOriginalDiveSite
                                                                                .getArea(),
                                                                        mOriginalDiveSite
                                                                                .getDiveSiteUid());

                        SelectionValue.removeDiveSiteFromSelectionValue(mUserUid,
                                                                        SelectionValue
                                                                                .NODE_STATE_VALUES,
                                                                        mOriginalDiveSite
                                                                                .getState(),
                                                                        mOriginalDiveSite
                                                                                .getDiveSiteUid());

                        SelectionValue.removeDiveSiteFromSelectionValue(mUserUid,
                                                                        SelectionValue
                                                                                .NODE_COUNTRY_VALUES,

                                                                        mOriginalDiveSite
                                                                                .getCountry(),
                                                                        mOriginalDiveSite
                                                                                .getDiveSiteUid());

                    }
                    //  Add the proposed diveSite to the area, state and country SelectionValues
                    SelectionValue.addDiveSiteToSelectionValue(mUserUid,
                                                               SelectionValue.NODE_AREA_VALUES,
                                                               proposedDiveSite.getArea(),
                                                               proposedDiveSiteUid);

                    SelectionValue.addDiveSiteToSelectionValue(mUserUid,
                                                               SelectionValue.NODE_STATE_VALUES,
                                                               proposedDiveSite.getState(),
                                                               proposedDiveSiteUid);

                    SelectionValue.addDiveSiteToSelectionValue(mUserUid,
                                                               SelectionValue
                                                                       .NODE_COUNTRY_VALUES,
                                                               proposedDiveSite.getCountry(),
                                                               proposedDiveSiteUid);
                    dismiss();

                } else {
                    // the proposed name already nameExists in the db
                    // set proposed dive site name back to its original value

                    String errorMsg = String.format(getActivity()
                                                            .getString(R.string.diveSiteName_nameExists_error),
                                                    proposedDiveSite.getDiveSiteName());
                    if (mIsNewDiveSite) {
                        mProposedDiveSite.setDiveSiteName(mNewDiveSite.getDiveSiteName());
                        txtName.setText(mNewDiveSite.getDiveSiteName());
                    } else {
                        mProposedDiveSite.setDiveSiteName(mOriginalDiveSite.getDiveSiteName());
                        txtName.setText(mOriginalDiveSite.getDiveSiteName());
                    }
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnArea:
                SelectionValue selectionValue = (SelectionValue) btnArea.getTag();
                dialogSelectionValuesPicker selectionValuePickerDialog = dialogSelectionValuesPicker
                        .newInstance(mUserUid, null, mProposedDiveSite.getDiveSiteUid(),
                                     SelectionValue.NODE_AREA_VALUES, selectionValue);
                selectionValuePickerDialog.show(getActivity().getSupportFragmentManager(),
                                                "dialogSelectionValuesPicker");
                break;

            case R.id.btnState:
                selectionValue = (SelectionValue) btnState.getTag();
                selectionValuePickerDialog = dialogSelectionValuesPicker
                        .newInstance(mUserUid, null, mProposedDiveSite.getDiveSiteUid(),
                                     SelectionValue.NODE_STATE_VALUES, selectionValue);
                selectionValuePickerDialog.show(getActivity().getSupportFragmentManager(),
                                                "dialogSelectionValuesPicker");
                break;

            case R.id.btnCountry:
                selectionValue = (SelectionValue) btnCountry.getTag();
                selectionValuePickerDialog = dialogSelectionValuesPicker
                        .newInstance(mUserUid, null, mProposedDiveSite.getDiveSiteUid(),
                                     SelectionValue.NODE_COUNTRY_VALUES, selectionValue);
                selectionValuePickerDialog.show(getActivity().getSupportFragmentManager(),
                                                "dialogSelectionValuesPicker");
                break;
        }
    }
}
