package com.lbconsulting.divelogfirebase.ui.dialogs.people;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.Person;
import com.lbconsulting.divelogfirebase.utils.MyMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


/**
 * A dialog where the user selects, edits, or deletes a dive's buddy, dive master, or company
 */
public class dialogPersonPicker1 extends DialogFragment {
    public static final String ARG_USER_UID = "argUserUid";
    public static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    public static final String ARG_BUTTON_ID = "argButtonId";
    private static final String ARG_PERSON_UID = "argPersonUid";

    private AlertDialog mPersonPickerDialog;
    private String mUserUid;
    private String mDiveLogUid;
    private int mButtonID;
    private String mTitle;
    private Person mPerson;
    //    private String mPersonName;
    private List<Person> mPeople;
    private EditText txtPeopleFilter;
    private ListView lvPeople;

    private RadioButton rbSelect;
    private RadioButton rbEdit;
    private RadioButton rbDelete;

    private PeopleAdapter mPeoplePickerAdapter;

    public dialogPersonPicker1() {
        // Empty constructor required for DialogFragment
    }

    public static dialogPersonPicker1 newInstance(@NonNull String userUid,
                                                  @NonNull String diveLogUid, int buttonId,
                                                  @NonNull String personUid) {
        dialogPersonPicker1 frag = new dialogPersonPicker1();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putInt(ARG_BUTTON_ID, buttonId);
        args.putString(ARG_PERSON_UID, personUid);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
//        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
            mButtonID = args.getInt(ARG_BUTTON_ID);
            String personUid = args.getString(ARG_PERSON_UID);
            if (personUid != null) {
                Person.nodeUserPerson(mUserUid, personUid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            mPerson = dataSnapshot.getValue(Person.class);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                    }
                });
            }
        }
        switch (mButtonID) {
            // TODO: Implement dialogPeoplePicker
//            case R.id.btnDiveBuddy:
//                mTitle = "Select Buddy";
//                break;
//            case R.id.btnDiveMaster:
//                mTitle = "Select Dive Master";
//                break;
//            case R.id.btnCompany:
//                mTitle = "Select Company";
//                break;
        }
    }

    //region Delete Person
//    @Subscribe
//    public void onEvent(MyEvents.deletePerson event) {
//        // To delete a Person:
//        //  1.  Update and save each diveLog that uses the to be deleted Person
//        //      with the default Person's name and Uid (e.g.: [None] and N/A respectively)
//        //  2.  Remove the Person from the database
//        DiveLog.updateDiveLogsWithDefaultPerson(mUserUid, event.getPersonSnapshot());
//        Person.remove(mUserUid, event.getPersonSnapshot());
//        dismiss();
//    }
    //endregion Delete Person


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mPersonPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button cancelButton = mPersonPickerDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(16);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        dismiss();
                    }
                });

                Button newButton = mPersonPickerDialog.getButton(Dialog.BUTTON_NEUTRAL);
                newButton.setTextSize(16);
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogPersonEditNew(null);
                    }
                });
            }
        });
    }

    private void showDialogPersonEditNew(Person selectedPerson) {
        // TODO: Implement showDialogPersonEditNew

//        if (selectedPerson == null) {
//            // creating a new Person
//            switch (mButtonID) {
//                case R.id.btnDiveBuddy:
//                    selectedPerson = new Person("", MySettings.NOT_AVAILABLE,
//                            true, false, false, mDiveLogUid);
//                    break;
//                case R.id.btnDiveMaster:
//                    selectedPerson = new Person("", MySettings.NOT_AVAILABLE,
//                            false, true, false, mDiveLogUid);
//                    break;
//                case R.id.btnCompany:
//                    selectedPerson = new Person("", MySettings.NOT_AVAILABLE,
//                            false, false, true, mDiveLogUid);
//                    break;
//            }
//        }
//
//        Gson gson = new Gson();
//        String selectedPersonJson = gson.toJson(selectedPerson);
//        dialogPersonEditNew dialog = dialogPersonEditNew
//                .newInstance(mUserUid, mDiveLogUid, mButtonID, selectedPersonJson);
//        dialog.show(getActivity().getSupportFragmentManager(), "dialogPersonEditNew");
//        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_people_picker, null, false);

        // find the dialog's views
        txtPeopleFilter = (EditText) view.findViewById(R.id.txtPeopleFilter);
        lvPeople = (ListView) view.findViewById(R.id.lvPeople);
        rbSelect = (RadioButton) view.findViewById(R.id.rbSelect);
        rbEdit = (RadioButton) view.findViewById(R.id.rbEdit);
        rbDelete = (RadioButton) view.findViewById(R.id.rbDelete);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonId) {
                //region Radio Button switch statement
//                switch (radioButtonId) {
//                    case R.id.rbSelect:
//                        if (rbSelect.isChecked()) {
//                            switch (mButtonID) {
//                                case R.id.btnDiveBuddy:
//                                    mTitle = "Select Buddy";
//                                    break;
//                                case R.id.btnDiveMaster:
//                                    mTitle = "Select Dive Master";
//                                    break;
//                                case R.id.btnCompany:
//                                    mTitle = "Select Company";
//                                    break;
//                            }
//                            txtPeopleFilter.setText("");
//                        }
//                        break;
//
//                    case R.id.rbEdit:
//                        if (rbEdit.isChecked()) {
//                            switch (mButtonID) {
//                                case R.id.btnDiveBuddy:
//                                    mTitle = "Edit Buddy";
//                                    break;
//                                case R.id.btnDiveMaster:
//                                    mTitle = "Edit Dive Master";
//                                    break;
//                                case R.id.btnCompany:
//                                    mTitle = "Edit Company";
//                                    break;
//                            }
//                            txtPeopleFilter.setText(mPerson.getName());
//                            if (mPeoplePickerAdapter.getFilteredListSize() == 1) {
//                                int listViewPosition = 0;
//                                lvPeople.performItemClick(
//                                        lvPeople.getAdapter().getView(listViewPosition, null, null),
//                                        listViewPosition,
//                                        lvPeople.getAdapter().getItemId(listViewPosition));
//                            } else if (mPeoplePickerAdapter.getFilteredListSize() > 1) {
//                                String title = "";
//                                String msg = "";
//                                switch (mButtonID) {
//                                    case R.id.btnDiveBuddy:
//                                        msg = "Select the Buddy to edit.";
//                                        break;
//                                    case R.id.btnDiveMaster:
//                                        msg = "Select the Dive Master to edit.";
//                                        break;
//                                    case R.id.btnCompany:
//                                        msg = "Select the Company to edit.";
//                                        break;
//                                }
//                                MyMethods.showOkDialog(getActivity(), title, msg);
//                            } else {
//                                String title = "";
//                                String msg = "";
//                                switch (mButtonID) {
//                                    case R.id.btnDiveBuddy:
//                                        msg = "There are no Buddies to select for editing!";
//                                        break;
//                                    case R.id.btnDiveMaster:
//                                        msg = "There are no Dive Masters to select for editing!";
//                                        break;
//                                    case R.id.btnCompany:
//                                        msg = "There are no Companies to select for editing!";
//                                        break;
//                                }
//                                MyMethods.showOkDialog(getActivity(), title, msg);
//                            }
//                        }
//                        break;
//
//                    case R.id.rbDelete:
//                        if (rbDelete.isChecked()) {
//                            mTitle = "Delete Dive Site";
//                            switch (mButtonID) {
//                                case R.id.btnDiveBuddy:
//                                    mTitle = "Select Buddy to delete.";
//                                    break;
//                                case R.id.btnDiveMaster:
//                                    mTitle = "Select Dive Master to delete.";
//                                    break;
//                                case R.id.btnCompany:
//                                    mTitle = "Select Company to delete.";
//                                    break;
//                            }
//                            txtPeopleFilter.setText(mPerson.getName());
//                            if (mPeoplePickerAdapter.getFilteredListSize() == 1) {
//                                int listViewPosition = 0;
//                                lvPeople.performItemClick(
//                                        lvPeople.getAdapter().getView(listViewPosition, null, null),
//                                        listViewPosition,
//                                        lvPeople.getAdapter().getItemId(listViewPosition));
//                            } else if (mPeoplePickerAdapter.getFilteredListSize() > 1) {
//                                String title = "";
//                                String msg = "";
//                                switch (mButtonID) {
//                                    case R.id.btnDiveBuddy:
//                                        msg = "Select Buddy to delete.";
//                                        break;
//                                    case R.id.btnDiveMaster:
//                                        msg = "Select Dive Master to delete.";
//                                        break;
//                                    case R.id.btnCompany:
//                                        msg = "Select Company to delete.";
//                                        break;
//                                }
//                                MyMethods.showOkDialog(getActivity(), title, msg);
//                            } else {
//                                String title = "";
//                                String msg = "";
//                                switch (mButtonID) {
//                                    case R.id.btnDiveBuddy:
//                                        msg = "There are no Buddies to select for deletion!";
//                                        break;
//                                    case R.id.btnDiveMaster:
//                                        msg = "There are no Dive Masters to select for deletion!";
//                                        break;
//                                    case R.id.btnCompany:
//                                        msg = "There are no Companies to select for deletion!";
//                                        break;
//                                }
//                                MyMethods.showOkDialog(getActivity(), title, msg);
//                            }
//                        }
//                        break;
//                }
                mPersonPickerDialog.setTitle(mTitle);
                //endregion
            }
        });

        // Fill the listView with people
        mPeople = new ArrayList<>();
        Person.nodeUserPersons(mUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Person person = snapshot.getValue(Person.class);
//                        switch (mButtonID) {
//                            case R.id.btnDiveBuddy:
//                                if (person.isBuddy()) {
//                                    mPeople.add(person);
//                                }
//                                break;
//
//                            case R.id.btnDiveMaster:
//                                if (person.isDiveMaster()) {
//                                    mPeople.add(person);
//                                }
//                                break;
//
//                            case R.id.btnCompany:
//                                if (person.isCompany()) {
//                                    mPeople.add(person);
//                                }
//                                break;
//                        }
                    }
                    Collections.sort(mPeople, Person.sortOrderAscending);
                    Person defaultPerson = Person.getDefaultPerson(mButtonID);
                    mPeople.add(0, defaultPerson);

                    mPeoplePickerAdapter = new PeopleAdapter(getActivity(),
                            android.R.layout.simple_list_item_1, mPeople);
                    lvPeople.setAdapter(mPeoplePickerAdapter);

                    txtPeopleFilter.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            mPeoplePickerAdapter.filter(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {

                        }

                    });

                    lvPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            final Person selectedPerson = mPeoplePickerAdapter.getItem(position);
                            if (selectedPerson == null) {
                                Timber.e("onItemClick(): Unable to retrieve isChecked person! Selected person is null!");
                                return;
                            }
                            if (rbSelect.isChecked()) {
                                Person.selectPersonForDiveLog(mUserUid, mDiveLogUid, mButtonID, selectedPerson);
                                dismiss();
                            } else {
                                if (!selectedPerson.getName().startsWith("[")) {
                                    if (rbEdit.isChecked()) {
                                        showDialogPersonEditNew(selectedPerson);
                                    } else if (rbDelete.isChecked()) {
                                        // Get the number of affected diveLogs
                                        String title = String.format(getString(R.string.okToDeletePerson_dialogTitle), selectedPerson.getName());
                                        String msg;
                                        if (selectedPerson.getDiveLogsAsBuddy().size() == 0
                                                && selectedPerson.getDiveLogsAsMaster().size() == 0
                                                && selectedPerson.getDiveLogsAsCompany().size() == 0) {
                                            msg = getResources().getString(R.string.safeToDeletePerson);

                                        } else {
                                            String buddyMsg = getResources().getQuantityString(R.plurals.okToDeleteDiveBuddyPerson,
                                                    selectedPerson.getDiveLogsAsBuddy().size(), selectedPerson.getDiveLogsAsBuddy().size());
                                            String masterMsg = getResources().getQuantityString(R.plurals.okToDeleteDiveMasterPerson,
                                                    selectedPerson.getDiveLogsAsMaster().size(), selectedPerson.getDiveLogsAsMaster().size());
                                            String companyMsg = getResources().getQuantityString(R.plurals.okToDeleteDiveCompanyPerson,
                                                    selectedPerson.getDiveLogsAsCompany().size(), selectedPerson.getDiveLogsAsCompany().size());
                                            msg = buddyMsg + masterMsg + companyMsg;
                                        }
                                        deletePersonYesNoDialog(title, msg, selectedPerson);
                                    }
                                } else {
                                    // trying to edit or delete a default Person
                                    String title = "";
                                    String msg = getString(R.string.defaultValueChange_okMessage, selectedPerson.getName());
                                    MyMethods.showOkDialog(getActivity(), title, msg);
                                }
                            }
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });


        // build the dialog
        mPersonPickerDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setNegativeButton(R.string.btnCancel_title, null)
                .setNeutralButton(R.string.btnNew_title, null)
                .create();

        return mPersonPickerDialog;
    }

    private void deletePersonYesNoDialog(@NonNull String title, @NonNull String message,
                                         @NonNull final Person person) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // To delete a Person:
                //  1.  Update and save each diveLog that uses the to be deleted Person
                //      with the default Person's name and Uid (e.g.: [None] and N/A respectively)
                //  2.  Remove the Person from the database
                DiveLog.updateDiveLogsWithDefaultPerson(mUserUid, person);
                Person.remove(mUserUid, person);
                dialog.dismiss();
//                EventBus.getDefault().post(new MyEvents.deletePerson(person));
//                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class PeopleAdapter extends ArrayAdapter<Person> implements Filterable {

        private final List<Person> mOriginalList;
        private final List<Person> mFilteredList;
        private final HashMap<String, Integer> mIdMap;

        PeopleAdapter(Context context, int textViewResourceId,
                      List<Person> people) {
            super(context, textViewResourceId, people);
            mOriginalList = new ArrayList<>();
            mOriginalList.addAll(people);
            mFilteredList = new ArrayList<>();
            mIdMap = new HashMap<>();
            for (int i = 0; i < people.size(); ++i) {
                mIdMap.put(people.get(i).toString(), i);
            }
        }

        public void filter(String filter) {
            clear();
            mIdMap.clear();

            if (!filter.isEmpty()) {
                mFilteredList.clear();
                int index = -1;
                for (Person person : mOriginalList) {
                    if (person.toString().toLowerCase().contains(filter.toLowerCase())) {
                        mFilteredList.add(person);
                        index++;
                        mIdMap.put(person.toString(), index);
                    }
                }
                addAll(mFilteredList);

            } else {
                for (int i = 0; i < mOriginalList.size(); ++i) {
                    mIdMap.put(mOriginalList.get(i).toString(), i);
                }
                addAll(mOriginalList);
            }
            notifyDataSetChanged();
        }

        @Override
        public Person getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            Person item = getItem(position);
            return mIdMap.get(item.toString());
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public int getFilteredListSize() {
            return mFilteredList.size();
        }
    }
}
