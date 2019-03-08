package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.DiveSite;
import com.lbconsulting.divelogfirebase.models.SelectionValue;
import com.lbconsulting.divelogfirebase.ui.adapters.SelectionValueAdapter;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A dialog where the user selects diveLog field value
 */
public class dialogSelectionValuesPicker extends DialogFragment implements View.OnClickListener {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    private static final String ARG_DIVE_SITE_UID = "argDiveSiteUid";
    private static final String ARG_NODE_NAME = "argNodeName";
    private static final String ARG_ORIGINAL_SELECTION_VALUE_JSON = "argOriginalSelectionValueString";

    private String mUserUid;
    private String mDiveLogUid;
    private String mDiveSiteUid;
    private String mSelectionValueField;
    private SelectionValue mOriginalSelectionValue;
    private String mTitle;

    private List<SelectionValue> mSelectionValues;
    private EditText txtValueFilter;
    private ListView lvValues;

    private SelectionValueAdapter mSelectionValueAdapter;

    private TextWatcher mFilterTextWatcher;
    private AdapterView.OnItemClickListener mListViewOnItemClickListener;

    private int mAffectedDiveSites;
    private int mFoundDiveSites;
    private int mAffectedDiveLogs;

    public dialogSelectionValuesPicker() {
        // Empty constructor required for DialogFragment
    }

    public static dialogSelectionValuesPicker newInstance(@NonNull String userUid,
                                                          @Nullable String diveLogUid,
                                                          @Nullable String diveSiteUid,
                                                          @NonNull String nodeName,
                                                          @Nullable SelectionValue originalSelectionValue) {
        dialogSelectionValuesPicker frag = new dialogSelectionValuesPicker();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_NODE_NAME, nodeName);

        if (diveLogUid != null) {
            args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        }

        if (diveSiteUid != null) {
            args.putString(ARG_DIVE_SITE_UID, diveLogUid);
        }

        if (originalSelectionValue != null) {
            Gson gson = new Gson();
            String originalSelectionValueJson = gson.toJson(originalSelectionValue);
            args.putString(ARG_ORIGINAL_SELECTION_VALUE_JSON, originalSelectionValueJson);
        }
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID) && args.containsKey(ARG_NODE_NAME)) {
            mUserUid = args.getString(ARG_USER_UID);
            mSelectionValueField = args.getString(ARG_NODE_NAME);
        } else {
            Timber.e("onCreate(): Failed to retrieve mUserUid or mSelectionValueField");
        }

        mDiveLogUid = null;
        if (args.containsKey(ARG_DIVE_LOG_UID)) {
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
        }

        mDiveSiteUid = null;
        if (args.containsKey(ARG_DIVE_SITE_UID)) {
            mDiveSiteUid = args.getString(ARG_DIVE_SITE_UID);
        }

        mOriginalSelectionValue = null;
        if (args.containsKey(ARG_ORIGINAL_SELECTION_VALUE_JSON)) {
            Gson gson = new Gson();
            String mOriginalSelectionValueJson = args.getString(ARG_ORIGINAL_SELECTION_VALUE_JSON);
            mOriginalSelectionValue = gson.fromJson(mOriginalSelectionValueJson,
                    SelectionValue.class);
            mTitle = mOriginalSelectionValue.getValue();
        } else {
            mTitle = "";
        }

        mFilterTextWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSelectionValueAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }
        };

        mListViewOnItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final SelectionValue proposedSelectionValue = mSelectionValueAdapter.getItem(position);
                if (proposedSelectionValue != null) {
                    switch (mSelectionValueField) {
                        case SelectionValue.NODE_AREA_VALUES:
                        case SelectionValue.NODE_STATE_VALUES:
                        case SelectionValue.NODE_COUNTRY_VALUES:
                            EventBus.getDefault().post(new MyEvents.updateSelectionValue(proposedSelectionValue));
                            dismiss();
                            break;

                        case SelectionValue.NODE_CURRENT_VALUES:
                        case SelectionValue.NODE_DIVE_ENTRY_VALUES:
                        case SelectionValue.NODE_DIVE_STYLE_VALUES:
                        case SelectionValue.NODE_DIVE_TANK_VALUES:
                        case SelectionValue.NODE_DIVE_TYPE_VALUES:
                        case SelectionValue.NODE_SEA_CONDITION_VALUES:
                        case SelectionValue.NODE_WEATHER_CONDITION_VALUES:
                            EventBus.getDefault().post(new MyEvents.updateSelectionValue(proposedSelectionValue));
//
//                            if (mOriginalSelectionValue != null) {
//                                SelectionValue
//                                        .removeDiveLogFromSelectionValue(mUserUid,
//                                                mSelectionValueField,
//                                                mOriginalSelectionValue.getValue(),
//                                                mDiveLogUid);
//                            }
//                            SelectionValue
//                                    .addDiveLogToSelectionValue(mUserUid,
//                                            mSelectionValueField,
//                                            proposedSelectionValue.getValue(),
//                                            mDiveLogUid);
//
//                            DiveLog
//                                    .updateDiveLogWithSelectionValue(mUserUid,
//                                            mDiveLogUid,
//                                            mSelectionValueField,
//                                            proposedSelectionValue.getValue());
                            dismiss();
                            break;
                    }
                }
            }
        };
    }

    @Subscribe
    public void onEvent(MyEvents.foundDiveSite event) {
        Timber.i("onEvent(): found diveSiteUid = %s", event.getDiveSiteUid());
        mFoundDiveSites++;
        if (mFoundDiveSites == mAffectedDiveSites) {
            String msg = getResources().getQuantityString(R.plurals.affectedDiveSites,
                    mAffectedDiveSites, mAffectedDiveSites);
            msg = msg + getResources().getQuantityString(R.plurals.affectedDiveLogs,
                    mAffectedDiveLogs, mAffectedDiveLogs);

            String title = String.format(getString(R.string.okToDeleteSelectionValue_dialogTitle),
                    mOriginalSelectionValue.getValue());

            showDeleteSelectionValueYesNoDialog(title, msg, mOriginalSelectionValue, true);
            dismiss();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void showDialogSelectionValueEditNew(@Nullable SelectionValue selectionValue) {
        boolean isNewSelectionValue = false;
        if (selectionValue == null) {
            // creating a new selectionValue
            selectionValue = new SelectionValue("", mSelectionValueField, mDiveLogUid, mDiveSiteUid);
            isNewSelectionValue = true;
        }
        Gson gson = new Gson();
        String selectedSelectionValueJson = gson.toJson(selectionValue);
        dialogSelectionValueEditNew dialog = dialogSelectionValueEditNew
                .newInstance(mUserUid, mDiveLogUid, selectedSelectionValueJson, isNewSelectionValue);
        dialog.show(getActivity().getSupportFragmentManager(), "dialogSelectionValueEditNew");
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_selection_values_picker, null, false);

        // find the dialog's views
        txtValueFilter = (EditText) view.findViewById(R.id.txtValueFilter);
        lvValues = (ListView) view.findViewById(R.id.lvValues);

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnDeleteSelectionValue = (Button) view.findViewById(R.id.btnDeleteSelectionValue);
        Button btnEditSelectionValue = (Button) view.findViewById(R.id.btnEditSelectionValue);
        Button btnNewSelectionValue = (Button) view.findViewById(R.id.btnNewSelectionValue);

        btnCancel.setOnClickListener(this);
        btnDeleteSelectionValue.setOnClickListener(this);
        btnEditSelectionValue.setOnClickListener(this);
        btnNewSelectionValue.setOnClickListener(this);

        if (mOriginalSelectionValue == null
                || MyMethods.containsInvalidCharacters(mOriginalSelectionValue.getValue())) {
            btnDeleteSelectionValue.setEnabled(false);
            btnEditSelectionValue.setEnabled(false);
        }

        // Fill the listView with selectionValues
        mSelectionValues = new ArrayList<>();

        SelectionValue.nodeSelectionValues(mUserUid, mSelectionValueField)
                .orderByChild(SelectionValue.FIELD_VALUE)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                SelectionValue selectionValue = snapshot.getValue(SelectionValue.class);
                                mSelectionValues.add(selectionValue);
                            }
                            SelectionValue defaultSelectionValue = SelectionValue.getDefault(mSelectionValueField);
                            mSelectionValues.add(0, defaultSelectionValue);

                            mSelectionValueAdapter = new SelectionValueAdapter(
                                    getActivity(), android.R.layout.simple_list_item_1, mSelectionValues);
                            lvValues.setAdapter(mSelectionValueAdapter);


                            txtValueFilter.addTextChangedListener(mFilterTextWatcher);

                            lvValues.setOnItemClickListener(mListViewOnItemClickListener);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                    }
                });


        // build the dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .create();
    }

    private void showDeleteSelectionValueYesNoDialog(@NonNull String title, @NonNull String message,
                                                     @NonNull final SelectionValue selectionValue,
                                                     final boolean isAreaStateCountry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // To delete a SelectionValue:
                //  1.  Update and save each diveLog/diveSite that uses the to be deleted
                //      SelectionValue with the default SelectionValue's value (e.g.: [None] )
                //  2.  Remove the SelectionValue from the database
                //  3.  If SelectionValue is an area, state, or country: update dialogSelectionValueEditNew

                if (isAreaStateCountry) {
                    DiveSite.updateDiveSitesWithDefaultSelectionValue(mUserUid, selectionValue);
                    SelectionValue.remove(mUserUid, selectionValue);
                    SelectionValue defaultSelectionValue = SelectionValue.getDefault(mSelectionValueField);
                    EventBus.getDefault().post(new MyEvents.updateSelectionValue(defaultSelectionValue));

                } else {
                    DiveLog.updateDiveLogsWithDefaultSelectionValue(mUserUid, mSelectionValueField, selectionValue);
                    SelectionValue.remove(mUserUid, selectionValue);
                    EventBus.getDefault().post(new MyEvents.dismissDialog());
                }
                dialog.dismiss();

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

    @Override
    public void onClick(View view) {
        String msg;
        switch (view.getId()) {
            case R.id.btnCancel:
                dismiss();
                break;

            case R.id.btnDeleteSelectionValue:
                String title = String.format(getString(R.string.okToDeleteSelectionValue_dialogTitle),
                        mOriginalSelectionValue.getValue());

                switch (mSelectionValueField) {
                    case SelectionValue.NODE_AREA_VALUES:
                    case SelectionValue.NODE_STATE_VALUES:
                    case SelectionValue.NODE_COUNTRY_VALUES:
                        if (mOriginalSelectionValue.getDiveSites() == null
                                || mOriginalSelectionValue.getDiveSites().size() == 0) {
                            msg = getResources().getString(R.string.safeToDeleteSelectionValue_diveSites);
                            showDeleteSelectionValueYesNoDialog(title, msg, mOriginalSelectionValue, true);
                            dismiss();

                        } else {
                            mAffectedDiveSites = 0;
                            mFoundDiveSites = 0;
                            mAffectedDiveLogs = 0;
                            if (mOriginalSelectionValue.getDiveSites() != null) {
                                mAffectedDiveSites = mOriginalSelectionValue.getDiveSites().size();
                                for (String diveSiteUid : mOriginalSelectionValue.getDiveSites().keySet()) {
                                    getDiveSiteDiveLogCount(mUserUid, diveSiteUid);
                                }
                            }
                        }
                        break;

                    case SelectionValue.NODE_CURRENT_VALUES:
                    case SelectionValue.NODE_DIVE_ENTRY_VALUES:
                    case SelectionValue.NODE_DIVE_STYLE_VALUES:
                    case SelectionValue.NODE_DIVE_TANK_VALUES:
                    case SelectionValue.NODE_DIVE_TYPE_VALUES:
                    case SelectionValue.NODE_SEA_CONDITION_VALUES:
                    case SelectionValue.NODE_WEATHER_CONDITION_VALUES:
                        if (mOriginalSelectionValue.getDiveLogs() == null
                                || mOriginalSelectionValue.getDiveLogs().size() == 0) {
                            msg = getResources().getString(R.string.safeToDeleteSelectionValue_diveLogs);
                        } else {
                            msg = getResources().getQuantityString(R.plurals.okToDeleteSelectionValueAffectedByDiveLogs,
                                    mOriginalSelectionValue.getDiveLogs().size(),
                                    mOriginalSelectionValue.getDiveLogs().size());
                        }
                        showDeleteSelectionValueYesNoDialog(title, msg, mOriginalSelectionValue, false);
                        dismiss();
                        break;
                }
                break;

            case R.id.btnEditSelectionValue:
                if (!MyMethods.containsInvalidCharacters(mOriginalSelectionValue.getValue())) {
                    showDialogSelectionValueEditNew(mOriginalSelectionValue);
                } else {
                    MyMethods.showOkDialog(getActivity(), "", getString(R.string.cannotEditDefault_message));
                }
                break;

            case R.id.btnNewSelectionValue:
                showDialogSelectionValueEditNew(null);
                break;
        }
    }

    private void getDiveSiteDiveLogCount(@NonNull String userUid, final @NonNull String diveSiteUid) {
        DiveSite.nodeUserDiveSiteDiveLogs(userUid, diveSiteUid).addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            mAffectedDiveLogs += dataSnapshot.getChildrenCount();
                        }
                        EventBus.getDefault().post(new MyEvents.foundDiveSite(diveSiteUid));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                    }
                });
    }
}
