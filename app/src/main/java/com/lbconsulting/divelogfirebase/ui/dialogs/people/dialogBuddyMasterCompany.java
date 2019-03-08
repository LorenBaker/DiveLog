package com.lbconsulting.divelogfirebase.ui.dialogs.people;

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
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.Person;
import com.lbconsulting.divelogfirebase.ui.activities.DiveLogPagerActivity;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;

/**
 * This dialog allows a user to update a Buddy, Master, or Company for the current DiveLog
 */

public class dialogBuddyMasterCompany extends DialogFragment implements View.OnClickListener {

    public static final String ARG_USER_UID = "argUserUid";
    public static final String ARG_DIVE_LOG_JSON = "argDiveLogJson";

    private AlertDialog mBuddyMasterCompanyDialog;
    private String mUserUid;
    private DiveLog mDiveLog;
    private String mTitle;

    private Person mSelectedBuddy;
    private Person mSelectedMaster;
    private Person mSelectedCompany;

    private String mOriginalBuddyUid;
    private String mOriginalMasterUid;
    private String mOriginalCompanyUid;

    private Button btnDiveBuddy;
    private Button btnDiveMaster;
    private Button btnCompany;

    private boolean isPersonNameEdited;

    public dialogBuddyMasterCompany() {
    }

    public static dialogBuddyMasterCompany newInstance(@NonNull String userUid,
                                                       @NonNull DiveLog diveLog) {
        dialogBuddyMasterCompany frag = new dialogBuddyMasterCompany();
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
            mDiveLog = gson.fromJson(diveLogJson, DiveLog.class);
            mOriginalBuddyUid = mDiveLog.getDiveBuddyPersonUid();
            mOriginalMasterUid = mDiveLog.getDiveMasterPersonUid();
            mOriginalCompanyUid = mDiveLog.getDiveCompanyPersonUid();

            if (mOriginalBuddyUid == null || mOriginalBuddyUid.isEmpty()) {
                mOriginalBuddyUid = MySettings.NOT_AVAILABLE;
            }

            if (mOriginalMasterUid == null || mOriginalMasterUid.isEmpty()) {
                mOriginalMasterUid = MySettings.NOT_AVAILABLE;
            }

            if (mOriginalCompanyUid == null || mOriginalCompanyUid.isEmpty()) {
                mOriginalCompanyUid = MySettings.NOT_AVAILABLE;
            }
        }

        mSelectedBuddy = null;
        mSelectedMaster = null;
        mSelectedCompany = null;

        isPersonNameEdited = false;

        mTitle = "Select Person or Company";

        EventBus.getDefault().register(this);

        Timber.i("onCreate(): %s", mDiveLog.getShortTitle());
    }

    @Subscribe
    public void onEvent(MyEvents.personSelected event) {
        switch (event.getButtonId()) {
            case R.id.btnDiveBuddy:
                mSelectedBuddy = event.getSelectedPerson();
                mDiveLog.setDiveBuddyPersonUid(mSelectedBuddy.getPersonUid());
                populateDiveBuddy(btnDiveBuddy);
                break;

            case R.id.btnDiveMaster:
                mSelectedMaster = event.getSelectedPerson();
                mDiveLog.setDiveMasterPersonUid(mSelectedMaster.getPersonUid());
                populateDiveMaster(btnDiveMaster);
                break;

            case R.id.btnCompany:
                mSelectedCompany = event.getSelectedPerson();
                mDiveLog.setDiveCompanyPersonUid(mSelectedCompany.getPersonUid());
                populateDiveCompany(btnCompany);
                break;
        }
    }

    @Subscribe
    public void onEvent(MyEvents.newPersonSelected event) {
        switch (event.getButtonId()) {
            case R.id.btnDiveBuddy:
                mSelectedBuddy = event.getSelectedPerson();
                mDiveLog.setDiveBuddyPersonUid(mSelectedBuddy.getPersonUid());
                populateDiveBuddy(btnDiveBuddy);
                break;

            case R.id.btnDiveMaster:
                mSelectedMaster = event.getSelectedPerson();
                mDiveLog.setDiveMasterPersonUid(mSelectedMaster.getPersonUid());
                populateDiveMaster(btnDiveMaster);
                break;

            case R.id.btnCompany:
                mSelectedCompany = event.getSelectedPerson();
                mDiveLog.setDiveCompanyPersonUid(mSelectedCompany.getPersonUid());
                populateDiveCompany(btnCompany);
                break;
        }
        isPersonNameEdited = !event.isNewPerson();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated(): %s", mDiveLog.getShortTitle());

        mBuddyMasterCompanyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = mBuddyMasterCompanyDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DiveLog.save(mUserUid, mDiveLog);
                        if (mSelectedBuddy != null) {
                            Person.changeDiveBuddy(mUserUid, mOriginalBuddyUid,
                                    mSelectedBuddy.getPersonUid(), mDiveLog.getDiveLogUid());
                        }

                        if (mSelectedMaster != null) {
                            Person.changeDiveMaster(mUserUid, mOriginalMasterUid,
                                    mSelectedMaster.getPersonUid(), mDiveLog.getDiveLogUid());
                        }

                        if (mSelectedCompany != null) {
                            Person.changeDiveCompany(mUserUid, mOriginalCompanyUid,
                                    mSelectedCompany.getPersonUid(), mDiveLog.getDiveLogUid());
                        }

                        if (mSelectedBuddy != null || mSelectedMaster != null || mSelectedCompany != null) {
                            DiveLog.save(mUserUid, mDiveLog);
                        }
                        if (isPersonNameEdited) {
                            EventBus.getDefault().post(new MyEvents.populateDiveLogDetailFragmentUI(null));
                        }

                        dismiss();
//                        Toast.makeText(getActivity(), "Save Buddy, Master, Company Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                Button cancelButton = mBuddyMasterCompanyDialog.getButton(Dialog.BUTTON_NEGATIVE);
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
        Timber.i("onResume(): %s", mDiveLog.getShortTitle());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_buddy_master_company, null, false);

        // find the dialog's views
        btnDiveBuddy = (Button) view.findViewById(R.id.btnDiveBuddy);
        btnDiveMaster = (Button) view.findViewById(R.id.btnDiveMaster);
        btnCompany = (Button) view.findViewById(R.id.btnCompany);

        populateDiveBuddy(btnDiveBuddy);
        populateDiveMaster(btnDiveMaster);
        populateDiveCompany(btnCompany);

        btnDiveBuddy.setOnClickListener(this);
        btnDiveMaster.setOnClickListener(this);
        btnCompany.setOnClickListener(this);

        // build the dialog
        mBuddyMasterCompanyDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mBuddyMasterCompanyDialog;
//        return super.onCreateDialog(savedInstanceState);
    }

    private void populateDiveBuddy(Button btn) {
        if (mSelectedBuddy != null) {
            if (!mSelectedBuddy.getName().startsWith("[")) {
                btn.setText(String.format(getString(R.string.prefix_buddy), mSelectedBuddy.getName()));
                btn.setTag(mSelectedBuddy);
            } else {
                btn.setText(null);
            }
        } else {
            String personUid = mDiveLog.getDiveBuddyPersonUid();
            if (personUid != null && !personUid.isEmpty()
                    && !personUid.equals(MySettings.NOT_AVAILABLE)) {
                DataSnapshot diveBuddy = DiveLogPagerActivity.getPersonSnapshot(personUid);
                if (diveBuddy != null) {
                    Person person = diveBuddy.getValue(Person.class);
                    btn.setText(String.format(getString(R.string.prefix_buddy), person.getName()));
                    btn.setTag(person);
                } else {
                    btn.setText(null);
                }
            } else {
                btn.setText(null);
            }
        }
    }

    private void populateDiveMaster(Button btn) {
        if (mSelectedMaster != null) {
            if (!mSelectedMaster.getName().startsWith("[")) {
                btn.setText(String.format(getString(R.string.prefix_master), mSelectedMaster.getName()));
                btn.setTag(mSelectedMaster);
            } else {
                btn.setText(null);
            }
        } else {

            String personUid = mDiveLog.getDiveMasterPersonUid();
            if (personUid != null && !personUid.isEmpty()
                    && !personUid.equals(MySettings.NOT_AVAILABLE)) {
                DataSnapshot diveMaster = DiveLogPagerActivity.getPersonSnapshot(personUid);
                if (diveMaster != null) {
                    Person person = diveMaster.getValue(Person.class);
                    btn.setText(String.format(getString(R.string.prefix_master), person.getName()));
                    btn.setTag(person);
                } else {
                    btn.setText(null);
                }
            } else {
                btn.setText(null);
            }
        }
    }

    private void populateDiveCompany(Button btn) {
        if (mSelectedCompany != null) {
            if (!mSelectedCompany.getName().startsWith("[")) {
                btn.setText(String.format(getString(R.string.prefix_company), mSelectedCompany.getName()));
                btn.setTag(mSelectedCompany);
            } else {
                btn.setText(null);
            }
        } else {
            String personUid = mDiveLog.getDiveCompanyPersonUid();
            if (personUid != null && !personUid.isEmpty()
                    && !personUid.equals(MySettings.NOT_AVAILABLE)) {
                DataSnapshot diveCompany = DiveLogPagerActivity.getPersonSnapshot(personUid);
                if (diveCompany != null) {
                    Person person = diveCompany.getValue(Person.class);
                    btn.setText(String.format(getString(R.string.prefix_company), person.getName()));
                    btn.setTag(person);
                } else {
                    btn.setText(null);
                }
            } else {
                btn.setText(null);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.i("onDestroyView(): %s", mDiveLog.getShortTitle());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {

        dialogPersonPicker personPickerDialog = null;
        Person person = (Person) v.getTag();
        switch (v.getId()) {
            case R.id.btnDiveBuddy:
                personPickerDialog = dialogPersonPicker
                        .newInstance(mUserUid, person, R.id.btnDiveBuddy);
//                Toast.makeText(getActivity(), "btnDiveBuddy Clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnDiveMaster:
                personPickerDialog = dialogPersonPicker
                        .newInstance(mUserUid, person, R.id.btnDiveMaster);
//                Toast.makeText(getActivity(), "btnDiveMaster Clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCompany:
                personPickerDialog = dialogPersonPicker
                        .newInstance(mUserUid, person, R.id.btnCompany);
//                Toast.makeText(getActivity(), "btnCompany Clicked", Toast.LENGTH_SHORT).show();
                break;
        }


        if (personPickerDialog != null) {
            final FragmentManager fm = getActivity().getSupportFragmentManager();
            personPickerDialog.show(fm, "personPickerDialog");
        }

    }
}
