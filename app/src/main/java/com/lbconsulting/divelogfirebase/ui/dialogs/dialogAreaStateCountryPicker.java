package com.lbconsulting.divelogfirebase.ui.dialogs;


import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.SelectionValue;
import com.lbconsulting.divelogfirebase.ui.adapters.SelectionValueAdapter;
import com.lbconsulting.divelogfirebase.utils.MyEvents;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * A dialog where the user selects diveLog field value
 */
public class dialogAreaStateCountryPicker extends DialogFragment {


    public static final String ARG_USER_UID = "argUserUid";
    public static final String ARG_NODE_NAME = "argNodeName";
    private static final String ARG_PREVIOUS_SELECTION_VALUE_STRING = "argPreviousSelectionValueString";

    private AlertDialog mAreaStateCountryPickerDialog;
    private String mUserUid;
    private String mNodeName;
    private String mPreviousSelectionValueString;
    private String mTitle;
    private List<SelectionValue> mOriginalSelectionValues;
    private EditText txtValueFilter;
    private ListView lvValues;

    private SelectionValueAdapter mSelectionValueAdapter;

    public dialogAreaStateCountryPicker() {
        // Empty constructor required for DialogFragment
    }

    public static dialogAreaStateCountryPicker newInstance(@NonNull String userUid,
                                                           @NonNull String nodeName,
                                                           @NonNull String selectionValueString) {
        dialogAreaStateCountryPicker frag = new dialogAreaStateCountryPicker();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_NODE_NAME, nodeName);
        args.putString(ARG_PREVIOUS_SELECTION_VALUE_STRING, selectionValueString);
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
            mPreviousSelectionValueString = args.getString(ARG_PREVIOUS_SELECTION_VALUE_STRING);

            if (mNodeName != null) {
                switch (mNodeName) {
                    case SelectionValue.NODE_AREA_VALUES:
                        mTitle = "Select Area";
                        break;
                    case SelectionValue.NODE_STATE_VALUES:
                        mTitle = "Select State";
                        break;
                    case SelectionValue.NODE_COUNTRY_VALUES:
                        mTitle = "Select Country";
                        break;
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mAreaStateCountryPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button cancelButton = mAreaStateCountryPickerDialog.getButton(Dialog.BUTTON_NEGATIVE);
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


//    private void showDialogAreaStateCountryEditNew(SelectionValue selectedSelectionValue) {
//        if (selectedSelectionValue == null) {
//            // creating a new selectionValue
//            selectedSelectionValue = new SelectionValue("", mNodeName, null, null);
//        }
//
//        Gson gson = new Gson();
//        String selectedSelectionValueJson = gson.toJson(selectedSelectionValue);
//        dialogAreaStateCountryEditNew dialog = dialogAreaStateCountryEditNew
//                .newInstance(mUserUid, mNodeName, selectedSelectionValueJson);
//        dialog.show(getActivity().getSupportFragmentManager(), "dialogAreaStateCountryEditNew");
//        dismiss();
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_area_state_country_picker, null, false);

        // find the dialog's views
        txtValueFilter = (EditText) view.findViewById(R.id.txtValueFilter);
        lvValues = (ListView) view.findViewById(R.id.lvValues);

        // Fill the listView with selectionValues
        mOriginalSelectionValues = new ArrayList<>();

        SelectionValue.nodeSelectionValues(mUserUid, mNodeName).orderByChild(SelectionValue.FIELD_VALUE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SelectionValue selectionValue = snapshot.getValue(SelectionValue.class);
                        mOriginalSelectionValues.add(selectionValue);
                    }
                    SelectionValue defaultSelectionValue = SelectionValue.getDefault(mNodeName);
                    mOriginalSelectionValues.add(0, defaultSelectionValue);

                    mSelectionValueAdapter = new SelectionValueAdapter(getActivity(),
                            android.R.layout.simple_list_item_1, mOriginalSelectionValues);
                    lvValues.setAdapter(mSelectionValueAdapter);

                    txtValueFilter.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            mSelectionValueAdapter.filter(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {

                        }

                    });

                    lvValues.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            final SelectionValue proposedSelectionValue = mSelectionValueAdapter.getItem(position);
                            EventBus.getDefault().post(new MyEvents.updateSelectionValue(proposedSelectionValue));
                            dismiss();
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
        mAreaStateCountryPickerDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setNegativeButton(R.string.btnCancel_title, null)
//                .setNeutralButton(R.string.btnNew_title, null)
                .create();

        return mAreaStateCountryPickerDialog;
    }

}
