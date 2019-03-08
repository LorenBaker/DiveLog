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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.DiveSite;
import com.lbconsulting.divelogfirebase.ui.activities.DiveLogPagerActivity;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * A dialog where the user selects a dive's site
 */
public class dialogDiveSitePicker extends DialogFragment implements View.OnClickListener {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_DIVE_SITE_UID = "argDiveLogDiveSiteUid";

    private String mUserUid;
    private DiveLog mActiveDiveLog;
    private DiveSite mDiveSite;

    private DiveSiteAdapter mDiveSitePickerAdapter;

    public dialogDiveSitePicker() {
        // Empty constructor required for DialogFragment
    }

    public static dialogDiveSitePicker newInstance(@NonNull String userUid,
                                                   @NonNull String diveLogDiveSiteUid) {
        dialogDiveSitePicker frag = new dialogDiveSitePicker();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_DIVE_LOG_DIVE_SITE_UID, diveLogDiveSiteUid);

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

            String diveLogDiveSiteUid = args.getString(ARG_DIVE_LOG_DIVE_SITE_UID);
            mDiveSite = null;
            if (diveLogDiveSiteUid != null
                    && !diveLogDiveSiteUid.isEmpty()
                    && !diveLogDiveSiteUid.equals(MySettings.NOT_AVAILABLE)) {
                mDiveSite = DiveLogPagerActivity.getDiveSite(diveLogDiveSiteUid);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");
    }

    private void showDialogDiveSiteEditNew(DiveSite originalDiveSite, boolean isNewDiveSite) {

        DiveSite newDiveSite = null;
        if (isNewDiveSite) {
            // creating a new DiveSite with area, state, and country from the current active DiveLog
            newDiveSite = new DiveSite("",
                                       DiveLogPagerActivity.getActiveDiveLog().getArea(),
                                       DiveLogPagerActivity.getActiveDiveLog().getState(),
                                       DiveLogPagerActivity.getActiveDiveLog().getCountry(),
                                       mActiveDiveLog.getDiveLogUid());
        }


        dialogDiveSiteEditNew dialog = dialogDiveSiteEditNew.newInstance(mUserUid,
                                                                         originalDiveSite,
                                                                         newDiveSite);
        dialog.show(getActivity().getSupportFragmentManager(), "dialogDiveSiteEditNew");
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_dive_site_picker, null, false);

        // find the dialog's views
        TextView tvFilterDescription = (TextView) view.findViewById(R.id.tvFilterDescription);
        EditText txtDiveSiteFilter = (EditText) view.findViewById(R.id.txtDiveSiteFilter);
        final ListView lvDiveSites = (ListView) view.findViewById(R.id.lvDiveSites);

        Button btnEditDiveSite = (Button) view.findViewById(R.id.btnEditDiveSite);
        Button btnDeleteDiveSite = (Button) view.findViewById(R.id.btnDeleteDiveSite);
        Button btnNewDiveSite = (Button) view.findViewById(R.id.btnNewDiveSite);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnEditDiveSite.setOnClickListener(this);
        btnDeleteDiveSite.setOnClickListener(this);
        btnNewDiveSite.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        txtDiveSiteFilter.setText("");

        String title;
        if (mDiveSite != null) {
            btnEditDiveSite.setEnabled(true);
            btnDeleteDiveSite.setEnabled(true);
            title = mDiveSite.getDiveSiteName();
        } else {
            btnEditDiveSite.setEnabled(false);
            btnDeleteDiveSite.setEnabled(false);
            title = String.format("Dive site: %s", MySettings.NOT_AVAILABLE);
        }

        tvFilterDescription.setText(DiveLogPagerActivity.getUserAppSettings()
                                            .getFilterMethodDescription(getActivity()));

        // Get filtered DiveSites
        List<DiveSite> filteredDiveSites = DiveLogPagerActivity.getFilteredDiveSites();
        filteredDiveSites.add(0, DiveSite.getDefaultDiveSite());
        mDiveSitePickerAdapter = new DiveSiteAdapter(getActivity(), filteredDiveSites);
        lvDiveSites.setAdapter(mDiveSitePickerAdapter);

        txtDiveSiteFilter.addTextChangedListener(new TextWatcher() {
            //region onTextChanged
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDiveSitePickerAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

        });

        lvDiveSites
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                            long id) {
                        DiveSite selectedDiveSite = mDiveSitePickerAdapter.getItem(position);
                        if (selectedDiveSite != null) {
                            DiveSite.selectDiveSiteForDiveLog(mUserUid, mActiveDiveLog,
                                                              selectedDiveSite);
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
            case R.id.btnEditDiveSite:
                if (mDiveSite != null
                        && mDiveSite.getDiveSiteName() != null
                        && !mDiveSite.getDiveSiteName().startsWith("[")) {
                    showDialogDiveSiteEditNew(mDiveSite, false);
                }
                break;

            case R.id.btnDeleteDiveSite:
                if (mDiveSite != null
                        && mDiveSite.getDiveSiteName() != null
                        && !mDiveSite.getDiveSiteName().startsWith("[")) {
                    // Get the number of affected diveLogs
                    String title = String.format(getString(R.string.okToDeleteDiveSite_dialogTitle),
                                                 mDiveSite.getDiveSiteName());
                    String msg;
                    if (mDiveSite.getDiveLogs() == null || mDiveSite.getDiveLogs().size() == 0) {
                        msg = getResources().getString(R.string.safeToDeleteDiveSite);
                    } else {
                        msg = getResources().getQuantityString(R.plurals.okToDeleteDiveSite,
                                                               mDiveSite.getDiveLogs().size(),
                                                               mDiveSite.getDiveLogs().size());
                    }
                    showDeleteDiveSiteYesNoDialog(title, msg, mDiveSite);
                    dismiss();
                }
                break;

            case R.id.btnNewDiveSite:
                showDialogDiveSiteEditNew(mDiveSite, true);
                break;

            case R.id.btnCancel:
                dismiss();
                break;
        }
    }

    private void showDeleteDiveSiteYesNoDialog(@NonNull String title, @NonNull String message,
                                               @NonNull final DiveSite diveSite) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // To delete a dive site:
                //  1.  Update and save each diveLog that uses the to be deleted DiveSite
                //      with the default diveSite Uid, name, area, state and country (e.g.: N/A)
                //  2.  Remove the DiveSite from the database

                DiveLog.updateDiveLogsWithDiveSiteDefaultValues(mUserUid, diveSite);
                DiveSite.remove(mUserUid, diveSite);
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

    private class DiveSiteAdapter extends ArrayAdapter<DiveSite> implements Filterable {

        private final List<DiveSite> mOriginalDiveSiteList;
        private final List<DiveSite> mSubFilteredList;
        private final HashMap<String, Integer> mIdMap;

        // View lookup cache
        private class ViewHolder {
            TextView tvDiveSiteName;
            TextView tvDiveSiteLocation;
        }

        DiveSiteAdapter(Context context, List<DiveSite> diveSites) {
            super(context, android.R.layout.simple_list_item_1, diveSites);
            mOriginalDiveSiteList = new ArrayList<>();
            mOriginalDiveSiteList.addAll(diveSites);
            mSubFilteredList = new ArrayList<>();
            mIdMap = new HashMap<>();
            for (int i = 0; i < diveSites.size(); ++i) {
                mIdMap.put(diveSites.get(i).toString(), i);
            }
        }

        public void filter(String filter) {
            clear();
            mIdMap.clear();

            if (!filter.isEmpty()) {
                mSubFilteredList.clear();
                int index = -1;
                for (DiveSite diveSite : mOriginalDiveSiteList) {
                    if (diveSite != null) {
                        String diveSiteName = MyMethods.removePunctuation(diveSite.toString());
                        String filterString = MyMethods.removePunctuation(filter);
                        if (diveSiteName.toLowerCase().contains(filterString.toLowerCase())) {
                            mSubFilteredList.add(diveSite);
                            index++;
                            mIdMap.put(diveSite.toString(), index);
                        }
                    } else {
                        Timber.w("filter(): Null diveSite!");
                    }
                }
                addAll(mSubFilteredList);

            } else {
                for (int i = 0; i < mOriginalDiveSiteList.size(); ++i) {
                    mIdMap.put(mOriginalDiveSiteList.get(i).toString(), i);
                }
                addAll(mOriginalDiveSiteList);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            DiveSite diveSite = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.row_dive_site, parent, false);
                viewHolder.tvDiveSiteName = (TextView) convertView.findViewById(R.id.tvDiveSiteName);
                viewHolder.tvDiveSiteLocation = (TextView) convertView.findViewById(R.id.tvDiveSiteLocation);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            if (diveSite != null) {
                viewHolder.tvDiveSiteName.setText(diveSite.getDiveSiteName());
                String locationDescription = diveSite.getLocationDescription();
                if (locationDescription.isEmpty()) {
                    viewHolder.tvDiveSiteLocation.setVisibility(View.GONE);
                } else {
                    viewHolder.tvDiveSiteLocation.setVisibility(View.VISIBLE);
                    viewHolder.tvDiveSiteLocation.setText(locationDescription);
                }
            }
            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            long itemId = -1;
            DiveSite item = getItem(position);
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
