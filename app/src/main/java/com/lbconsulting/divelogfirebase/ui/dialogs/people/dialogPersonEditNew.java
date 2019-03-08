package com.lbconsulting.divelogfirebase.ui.dialogs.people;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.Person;
import com.lbconsulting.divelogfirebase.ui.activities.DiveLogPagerActivity;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * A dialog where the user edits or creates a new Person
 */
public class dialogPersonEditNew extends DialogFragment {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    private static final String ARG_BUTTON_ID = "argButtonId";
    private static final String ARG_PERSON_JSON = "argPersonJson";

    private AlertDialog mPersonEditNewDialog;

    private String mUserUid;
    private String mDiveLogUid;
    private int mButtonId;
    private Person mPerson;

    private String mTitle;

    private EditText txtName;
    private TextInputLayout txtName_input_layout;
    private CheckBox ckIsDiveBuddy;
    private CheckBox ckIsDiveMaster;
    private CheckBox ckIsDiveCompany;

    private boolean isNewPerson;


    public dialogPersonEditNew() {
        // Empty constructor required for DialogFragment
    }

    public static dialogPersonEditNew newInstance(@NonNull String userUid, @NonNull String diveLogUid,
                                                  int buttonID, @NonNull Person person) {
        dialogPersonEditNew frag = new dialogPersonEditNew();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putInt(ARG_BUTTON_ID, buttonID);
        Gson gson = new Gson();
        String personJson = gson.toJson(person);
        args.putString(ARG_PERSON_JSON, personJson);
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
            mButtonId = args.getInt(ARG_BUTTON_ID);
            String personJson = args.getString(ARG_PERSON_JSON);
            Gson gson = new Gson();
            mPerson = gson.fromJson(personJson, Person.class);
        }

        isNewPerson = mPerson.getPersonUid().equals(MySettings.NOT_AVAILABLE);

        if (isNewPerson) {
            switch (mButtonId) {
                case R.id.btnDiveBuddy:
                    mTitle = "Create New Dive Buddy";
                    break;

                case R.id.btnDiveMaster:
                    mTitle = "Create New Dive Master";
                    break;

                case R.id.btnCompany:
                    mTitle = "Create New Company";
                    break;
            }
        } else {
            switch (mButtonId) {
                case R.id.btnDiveBuddy:
                    mTitle = "Edit Dive Buddy";
                    break;

                case R.id.btnDiveMaster:
                    mTitle = "Edit Dive Master";
                    break;

                case R.id.btnCompany:
                    mTitle = "Edit Company";
                    break;
            }
        }
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mPersonEditNewDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = mPersonEditNewDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Save Person Clicked", Toast.LENGTH_SHORT).show();
                        verifyNameNotEmpty();
                    }
                });

                Button cancelButton = mPersonEditNewDialog.getButton(Dialog.BUTTON_NEGATIVE);
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_person_edit_new, null, false);

        // find the dialog's views
        txtName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        ckIsDiveBuddy = (CheckBox) view.findViewById(R.id.ckIsDiveBuddy);
        ckIsDiveMaster = (CheckBox) view.findViewById(R.id.ckIsDiveMaster);
        ckIsDiveCompany = (CheckBox) view.findViewById(R.id.ckIsDiveCompany);

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

        txtName.setText(mPerson.getName());
        ckIsDiveBuddy.setChecked(mPerson.isBuddy());
        ckIsDiveMaster.setChecked(mPerson.isDiveMaster());
        ckIsDiveCompany.setChecked(mPerson.isCompany());

        // build the dialog
        mPersonEditNewDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mPersonEditNewDialog;
    }

    private void verifyNameNotEmpty() {
        String proposedPersonName = txtName.getText().toString().trim();
        if (proposedPersonName.isEmpty()) {
            String errorMsg = getActivity().getString(R.string.personName_isEmpty_error);
            txtName_input_layout.setError(errorMsg);

        } else if (proposedPersonName.startsWith("[")) {
            String errorMsg = getActivity().getString(R.string.personName_isDefault_error);
            txtName_input_layout.setError(errorMsg);

        } else {
            mPerson.setName(proposedPersonName);
            mPerson.setBuddy(ckIsDiveBuddy.isChecked());
            mPerson.setDiveMaster(ckIsDiveMaster.isChecked());
            mPerson.setCompany(ckIsDiveCompany.isChecked());

            trySavingPerson(mPerson);
        }
    }

    private void trySavingPerson(final Person proposedPerson) {
        // get all people
        final ArrayList<Person> personList = new ArrayList<>();

        for (int i = 0; i < DiveLogPagerActivity.getPersonsArray().getCount(); i++) {
            Person person = DiveLogPagerActivity.getPersonsArray().getItem(i).getValue(Person.class);
            personList.add(person);
        }

        continueTryingToSavePerson(personList, proposedPerson);
    }

    private void continueTryingToSavePerson(ArrayList<Person> personList, Person proposedPerson) {
        // To save a new or updated Person:
        //  1. Verify that the Person's name does not exist in the people database,
        //     If ok to save person, then
        //      2. Save the person the database
        //      3. Update and save each diveLog that uses the Person with the Person's revised name and Uid

        if (Person.okToSavePerson(personList, proposedPerson)) {
            String proposedPersonUid = Person.save(mUserUid, proposedPerson);
            proposedPerson.setPersonUid(proposedPersonUid);
            EventBus.getDefault().post(new MyEvents.newPersonSelected(mButtonId, proposedPerson, isNewPerson));
            dismiss();

        } else {
            // the proposed name already nameExists in the db
            txtName.setText(mPerson.getName());
            String errorMsg = String.format(getActivity()
                    .getString(R.string.personName_nameExists_error), proposedPerson.getName());
            txtName_input_layout.setError(errorMsg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
//        EventBus.getDefault().unregister(this);
    }
}
