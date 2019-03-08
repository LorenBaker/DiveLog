package com.lbconsulting.divelogfirebase.ui.dialogs;

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

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.utils.MyEvents;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * A dialog where the user selects a time zone
 */
public class dialogSelectTimeZone extends DialogFragment {

    private AlertDialog mSelectTimeZoneDialog;

    private String mDiveLogUid;
    private String mTimeZoneId;
//    private TimeZone mTimeZone;

    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    private static final String ARG_LAST_TIME_ZONE_ID = "argLastTimeZoneID";
    private String mLastTimeZoneID;

    public dialogSelectTimeZone() {
        // Empty constructor required for DialogFragment
    }

    public static dialogSelectTimeZone newInstance(@NonNull String diveLogUid, @NonNull String
            lastTimeZoneID) {
        dialogSelectTimeZone frag = new dialogSelectTimeZone();
        Bundle args = new Bundle();
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putString(ARG_LAST_TIME_ZONE_ID, lastTimeZoneID);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();
        if (args.containsKey(ARG_LAST_TIME_ZONE_ID)) {
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
            mLastTimeZoneID = args.getString(ARG_LAST_TIME_ZONE_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mSelectTimeZoneDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button cancelButton = mSelectTimeZoneDialog.getButton(Dialog.BUTTON_NEGATIVE);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_select_time_zone, null, false);

        // find the dialog's views
        final EditText txtTimeZoneIdFilter = (EditText) view.findViewById(R.id.txtTimeZoneIdFilter);
        ListView lvTimeZoneIDs = (ListView) view.findViewById(R.id.lvTimeZoneIDs);

        // Fill the list view with available Time Zone IDs
        ArrayList<String> timeZoneIDs = new ArrayList<>();
        Collections.addAll(timeZoneIDs, TimeZone.getAvailableIDs());

        final TimeZoneAdapter timeZoneAdapter = new TimeZoneAdapter(getActivity(),
                                                                    android.R.layout
                                                                            .simple_list_item_1,
                                                                    timeZoneIDs);
        lvTimeZoneIDs.setAdapter(timeZoneAdapter);

        txtTimeZoneIdFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timeZoneAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

        });

        lvTimeZoneIDs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mTimeZoneId = timeZoneAdapter.getItem(position);
                EventBus.getDefault().post(new MyEvents.setTimeZone(mDiveLogUid, mTimeZoneId));
                dismiss();
            }
        });

        txtTimeZoneIdFilter.setText(mLastTimeZoneID);

        // build the dialog
        mSelectTimeZoneDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialogSelectTimeZone_title)
                .setView(view)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mSelectTimeZoneDialog;
    }

    private class TimeZoneAdapter extends ArrayAdapter<String> implements Filterable {

        private final List<String> mOriginalList;
        private final List<String> mFilteredList;
        private final HashMap<String, Integer> mIdMap;

        TimeZoneAdapter(Context context, int textViewResourceId,
                        List<String> timeZoneIDs) {
            super(context, textViewResourceId, timeZoneIDs);
            mOriginalList = new ArrayList<>();
            mOriginalList.addAll(timeZoneIDs);
            mFilteredList = new ArrayList<>();
            mIdMap = new HashMap<>();
            for (int i = 0; i < timeZoneIDs.size(); ++i) {
                mIdMap.put(timeZoneIDs.get(i), i);
            }
        }

        public void filter(String filter) {
            clear();
            mIdMap.clear();

            if (!filter.isEmpty()) {
                mFilteredList.clear();
                int index = -1;
                for (String timeZoneID : mOriginalList) {
                    if (timeZoneID.toLowerCase().contains(filter.toLowerCase())) {
                        mFilteredList.add(timeZoneID);
                        index++;
                        mIdMap.put(timeZoneID, index);
                    }
                }
                addAll(mFilteredList);

            } else {
                for (int i = 0; i < mOriginalList.size(); ++i) {
                    mIdMap.put(mOriginalList.get(i), i);
                }
                addAll(mOriginalList);
            }
            notifyDataSetChanged();
        }

        @Override
        public String getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
