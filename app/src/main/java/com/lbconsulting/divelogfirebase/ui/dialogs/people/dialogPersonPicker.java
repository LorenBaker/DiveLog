package com.lbconsulting.divelogfirebase.ui.dialogs.people;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.Person;
import com.lbconsulting.divelogfirebase.ui.activities.DiveLogPagerActivity;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * A dialog where the user selects a dive's Buddy, Master, or Company
 */
public class dialogPersonPicker extends DialogFragment implements View.OnClickListener {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_SELECTED_PERSON_JSON = "argSelectedPersonJson";
    private static final String ARG_BUTTON_ID = "argButtonId";

    private String mUserUid;
    private DiveLog mActiveDiveLog;
    private Person mSelectedPerson;
    private int mButtonId;
    private ArrayList<Person> mPersons;

    private PersonAdapter mPersonPickerAdapter;

    public dialogPersonPicker() {
        // Empty constructor required for DialogFragment
    }

    public static dialogPersonPicker newInstance(@NonNull String userUid,
                                                 @Nullable Person selectedPerson,
                                                 int buttonId) {
        dialogPersonPicker frag = new dialogPersonPicker();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);

        if (selectedPerson != null) {
            Gson gson = new Gson();
            String personJson = gson.toJson(selectedPerson);
            args.putString(ARG_SELECTED_PERSON_JSON, personJson);
        }

        args.putInt(ARG_BUTTON_ID, buttonId);

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
            mActiveDiveLog = DiveLogPagerActivity.getActiveDiveLog();
            mButtonId = args.getInt(ARG_BUTTON_ID);
        }

        mSelectedPerson = null;
        if (args.containsKey(ARG_SELECTED_PERSON_JSON)) {
            Gson gson = new Gson();
            String selectedPersonJson = args.getString(ARG_SELECTED_PERSON_JSON);
            mSelectedPerson = gson.fromJson(selectedPersonJson, Person.class);
        }

        mPersons = new ArrayList<>();
        for (int i = 0; i < DiveLogPagerActivity.getPersonsArray().getCount(); i++) {
            DataSnapshot personSnapshot = DiveLogPagerActivity.getPersonsArray().getItem(i);
            Person person = personSnapshot.getValue(Person.class);
            switch (mButtonId) {
                case R.id.btnDiveBuddy:
                    if (person.isBuddy()) {
                        mPersons.add(person);
                    }
                    break;

                case R.id.btnDiveMaster:
                    if (person.isDiveMaster()) {
                        mPersons.add(person);
                    }
                    break;

                case R.id.btnCompany:
                    if (person.isCompany()) {
                        mPersons.add(person);
                    }
                    break;
            }
        }
        Collections.sort(mPersons, Person.sortOrderAscending);
        Person defaultPerson = Person.getDefaultPerson(mButtonId);
        mPersons.add(0, defaultPerson);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");
    }

    private void showDialogPersonEditNew(@Nullable Person person) {
        Timber.i("showDialogPersonEditNew()");

        if (person == null) {
            // creating a new Person
            person = Person.getDefaultPerson(mButtonId);
            person.setName("");
            switch (mButtonId) {
                case R.id.btnDiveBuddy:
                    person.getDiveLogsAsBuddy().put(mActiveDiveLog.getDiveLogUid(), true);
                    break;

                case R.id.btnDiveMaster:
                    person.getDiveLogsAsMaster().put(mActiveDiveLog.getDiveLogUid(), true);
                    break;

                case R.id.btnCompany:
                    person.getDiveLogsAsCompany().put(mActiveDiveLog.getDiveLogUid(), true);
                    break;
            }
        }


        dialogPersonEditNew dialog = dialogPersonEditNew.newInstance(mUserUid,
                mActiveDiveLog.getDiveLogUid(), mButtonId, person);
        dialog.show(getActivity().getSupportFragmentManager(), "dialogPersonEditNew");
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_persons_picker, null, false);

        // find the dialog's views
        EditText txtPersonFilter = (EditText) view.findViewById(R.id.txtPersonFilter);
        final ListView lvPersons = (ListView) view.findViewById(R.id.lvPersons);

        Button btnEditPerson = (Button) view.findViewById(R.id.btnEditPerson);
        Button btnDeletePerson = (Button) view.findViewById(R.id.btnDeletePerson);
        Button btnNewPerson = (Button) view.findViewById(R.id.btnNewPerson);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnEditPerson.setOnClickListener(this);
        btnDeletePerson.setOnClickListener(this);
        btnNewPerson.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        txtPersonFilter.setText("");

        String title = "";
        if (mSelectedPerson != null) {
            btnEditPerson.setEnabled(true);
            btnDeletePerson.setEnabled(true);
            switch (mButtonId) {
                case R.id.btnDiveBuddy:
                    title = String.format(getString(R.string.prefix_buddy), mSelectedPerson.getName());
                    break;

                case R.id.btnDiveMaster:
                    title = String.format(getString(R.string.prefix_master), mSelectedPerson.getName());
                    break;

                case R.id.btnCompany:
                    title = String.format(getString(R.string.prefix_company), mSelectedPerson.getName());
                    break;
            }

        } else {
            btnEditPerson.setEnabled(false);
            btnDeletePerson.setEnabled(false);
            switch (mButtonId) {
                case R.id.btnDiveBuddy:
                    title = String.format(getString(R.string.prefix_buddy), MySettings.NOT_AVAILABLE);
                    break;

                case R.id.btnDiveMaster:
                    title = String.format(getString(R.string.prefix_master), MySettings.NOT_AVAILABLE);
                    break;

                case R.id.btnCompany:
                    title = String.format(getString(R.string.prefix_company), MySettings.NOT_AVAILABLE);
                    break;
            }
        }

        mPersonPickerAdapter = new PersonAdapter(getActivity(), mPersons);
        lvPersons.setAdapter(mPersonPickerAdapter);

        txtPersonFilter.addTextChangedListener(new TextWatcher() {
            //region onTextChanged
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPersonPickerAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

        });

        lvPersons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                Person selectedPerson = mPersonPickerAdapter.getItem(position);
                if (selectedPerson != null) {
                    EventBus.getDefault().post(new MyEvents.personSelected(mButtonId, selectedPerson));
//                            Person.selectPersonForDiveLog(mButtonId, selectedPerson);
                    dismiss();
                }
            }
        });

        // build the dialog
        return new AlertDialog.Builder(
                getActivity())
                .setTitle(title)
                .setView(view)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEditPerson:
                if (mSelectedPerson != null
                        && mSelectedPerson.getName() != null
                        && !mSelectedPerson.getName().startsWith("[")) {
                    showDialogPersonEditNew(mSelectedPerson);
                }
                break;

            case R.id.btnDeletePerson:
                if (mSelectedPerson != null
                        && mSelectedPerson.getName() != null
                        && !mSelectedPerson.getName().startsWith("[")) {
                    // Get the number of affected diveLogs
                    String title = String.format(getString(R.string.okToDeletePerson_dialogTitle),
                            mSelectedPerson.getName());
                    String msg = "";
                    switch (mButtonId) {
                        case R.id.btnDiveBuddy:
                            if (mSelectedPerson.getDiveLogsAsBuddy() == null || mSelectedPerson.getDiveLogsAsBuddy().size() == 0) {
                                msg = getResources().getString(R.string.safeToDeletePerson);
                            } else {
                                msg = getResources().getQuantityString(R.plurals.okToDeleteDiveBuddyPerson,
                                        mSelectedPerson.getDiveLogsAsBuddy().size(),
                                        mSelectedPerson.getDiveLogsAsBuddy().size());
                            }
                            break;

                        case R.id.btnDiveMaster:
                            if (mSelectedPerson.getDiveLogsAsMaster() == null || mSelectedPerson.getDiveLogsAsMaster().size() == 0) {
                                msg = getResources().getString(R.string.safeToDeletePerson);
                            } else {
                                msg = getResources().getQuantityString(R.plurals.okToDeleteDiveMasterPerson,
                                        mSelectedPerson.getDiveLogsAsMaster().size(),
                                        mSelectedPerson.getDiveLogsAsMaster().size());
                            }
                            break;

                        case R.id.btnCompany:
                            if (mSelectedPerson.getDiveLogsAsCompany() == null || mSelectedPerson.getDiveLogsAsCompany().size() == 0) {
                                msg = getResources().getString(R.string.safeToDeletePerson);
                            } else {
                                msg = getResources().getQuantityString(R.plurals.okToDeleteDiveCompanyPerson,
                                        mSelectedPerson.getDiveLogsAsCompany().size(),
                                        mSelectedPerson.getDiveLogsAsCompany().size());
                            }
                            break;

                        default:
                            Timber.e("onClick(): Unknown mButtonId = %d", mButtonId);
                    }
                    showDeletePersonYesNoDialog(title, msg, mSelectedPerson);
                    dismiss();
                }
                break;

            case R.id.btnNewPerson:
                showDialogPersonEditNew(null);
                break;

            case R.id.btnCancel:
                dismiss();
                break;
        }
    }

    private void showDeletePersonYesNoDialog(@NonNull String title, @NonNull String message,
                                             @NonNull final Person person) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO: Verify that delete Person is properly implemented

                // To delete a dive site:
                //  1.  Update and save each diveLog that uses the to be deleted Person
                //      with the default person Uid
                //  2.  Remove the Person from the database

                DiveLog.updateDiveLogsWithDefaultPerson(mUserUid, person);
                Person.remove(mUserUid, person);
                int index = mPersons.indexOf(person);
                if (index > -1) {
                    mPersons.remove(index);
                    mPersonPickerAdapter.notifyDataSetChanged();
                }

                switch (mButtonId) {
                    case R.id.btnDiveBuddy:
                        Person defaultBuddy = Person.getDefaultPerson(R.id.btnDiveBuddy);
                        mActiveDiveLog.setDiveBuddyPersonUid(defaultBuddy.getPersonUid());
                        EventBus.getDefault().post(new MyEvents.personSelected(R.id.btnDiveBuddy, defaultBuddy));
                        break;

                    case R.id.btnDiveMaster:
                        Person defaultMaster = Person.getDefaultPerson(R.id.btnDiveMaster);
                        mActiveDiveLog.setDiveMasterPersonUid(defaultMaster.getPersonUid());
                        EventBus.getDefault().post(new MyEvents.personSelected(R.id.btnDiveMaster, defaultMaster));
                        break;

                    case R.id.btnCompany:
                        Person defaultCompany = Person.getDefaultPerson(R.id.btnCompany);
                        mActiveDiveLog.setDiveCompanyPersonUid(defaultCompany.getPersonUid());
                        EventBus.getDefault().post(new MyEvents.personSelected(R.id.btnDiveBuddy, defaultCompany));

                        break;
                }
                dismiss();
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

    private class PersonAdapter extends ArrayAdapter<Person> implements Filterable {

        private final List<Person> mOriginalPersonList;
        private final List<Person> mSubFilteredList;
        private final HashMap<String, Integer> mIdMap;

        // View lookup cache
        private class ViewHolder {
            TextView tvPersonName;
        }

        PersonAdapter(Context context, List<Person> persons) {
            super(context, android.R.layout.simple_list_item_1, persons);
            mOriginalPersonList = new ArrayList<>();
            mOriginalPersonList.addAll(persons);
            mSubFilteredList = new ArrayList<>();
            mIdMap = new HashMap<>();
            for (int i = 0; i < persons.size(); ++i) {
                mIdMap.put(persons.get(i).toString(), i);
            }
        }

        public void filter(String filter) {
            clear();
            mIdMap.clear();

            if (!filter.isEmpty()) {
                mSubFilteredList.clear();
                int index = -1;
                for (Person person : mOriginalPersonList) {
                    if (person != null) {
                        String personName = MyMethods.removePunctuation(person.toString());
                        String filterString = MyMethods.removePunctuation(filter);
                        if (personName.toLowerCase().contains(filterString.toLowerCase())) {
                            mSubFilteredList.add(person);
                            index++;
                            mIdMap.put(person.toString(), index);
                        }
                    } else {
                        Timber.w("filter(): Null person!");
                    }
                }
                addAll(mSubFilteredList);

            } else {
                for (int i = 0; i < mOriginalPersonList.size(); ++i) {
                    mIdMap.put(mOriginalPersonList.get(i).toString(), i);
                }
                addAll(mOriginalPersonList);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            Person person = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.row_person, parent, false);
                viewHolder.tvPersonName = (TextView) convertView.findViewById(R.id.tvPersonName);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            if (person != null) {
                viewHolder.tvPersonName.setText(person.getName());
            }
            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            long itemId = -1;
            Person item = getItem(position);
            if (item != null) {
                itemId = mIdMap.get(item.toString());
            }
            return itemId;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
