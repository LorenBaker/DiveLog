package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveEquipment;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.ui.adapters.DiveEquipmentFirebaseRecyclerAdapter;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Sets the dive's equipment list
 */
public class dialogDiveLogEquipmentPicker extends DialogFragment implements View.OnClickListener {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    private static final String ARG_DIVE_LOG_EQUIPMENT_LIST = "argDiveLogEquipmentList";

    private static Context mContext;
    private String mUserUid;
    private String mDiveLogUid;
    private String mEquipmentList;
    private String mTitle;

    private AlertDialog mDiveEquipmentDialog;
    private RecyclerView rvDiveEquipment;
    private DiveEquipment mDiveEquipment;
    private DiveEquipmentFirebaseRecyclerAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    public static dialogDiveLogEquipmentPicker newInstance(@NonNull Context context,
                                                           @NonNull String userUid,
                                                           @NonNull String diveLogUid,
                                                           @NonNull String equipmentList) {
        mContext = context;
        dialogDiveLogEquipmentPicker fragment = new dialogDiveLogEquipmentPicker();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putString(ARG_DIVE_LOG_EQUIPMENT_LIST, equipmentList);
        fragment.setArguments(args);
        return fragment;
    }

    public dialogDiveLogEquipmentPicker() {
        // Default constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate()");

        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
            mEquipmentList = args.getString(ARG_DIVE_LOG_EQUIPMENT_LIST);
            mTitle = getString(R.string.dialogDiveLogEquipmentPicker_title);
        }
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Subscribe
    public void onEvent(MyEvents.addNewEquipmentItem event) {
        int position = mAdapter.insertEquipmentItem(event.getNewEquipmentItem());
//        mLinearLayoutManager.scrollToPosition(position);
    }

    @Subscribe
    public void onEvent(MyEvents.updateEquipmentItem event) {
        int position = mAdapter.updateEquipmentItem(mUserUid,
                event.getOriginalNewEquipmentItem(),
                event.getProposedEquipmentItem());
//        mLinearLayoutManager.scrollToPosition(position);
    }

    @Subscribe
    public void onEvent(MyEvents.onEquipmentNameEditClick event) {
        dialogDiveLogEquipmentEditNewItem diveLogEquipmentPicker = dialogDiveLogEquipmentEditNewItem
                .newInstance(mUserUid, mDiveEquipment.getDiveEquipmentList(), event.getEquipmentItem());
        diveLogEquipmentPicker.show(getActivity().getSupportFragmentManager(),
                "dialogDiveLogEquipmentEditNewItem");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mDiveEquipmentDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button saveButton = mDiveEquipmentDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Save
                        String equipmentListString = mDiveEquipment.getEquipmentListString();
                        DiveLog.nodeUserDiveEquipmentList(mUserUid, mDiveLogUid).setValue(equipmentListString);
                        DiveEquipment.save(mUserUid, mDiveEquipment.getDiveEquipmentList());
                        dismiss();
                    }
                });

                Button cancelButton = mDiveEquipmentDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(16);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        dismiss();
                    }
                });

                Button newButton = mDiveEquipmentDialog.getButton(Dialog.BUTTON_NEUTRAL);
                newButton.setTextSize(16);
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create new equipment item
                        dialogDiveLogEquipmentEditNewItem diveLogEquipmentNewItemDialog = dialogDiveLogEquipmentEditNewItem
                                .newInstance(mUserUid, mDiveEquipment.getDiveEquipmentList(), null);
                        diveLogEquipmentNewItemDialog.show(getActivity().getSupportFragmentManager(),
                                "dialogDiveLogEquipmentEditNewItem");
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
        @SuppressLint("InflateParams") View view = inflater
                .inflate(R.layout.dialog_dive_log_equipment, null, false);

        // find the dialog's views
        Button btnDeleteCheckedItems = (Button) view.findViewById(R.id.btnDeleteCheckedItems);
        btnDeleteCheckedItems.setOnClickListener(this);
        rvDiveEquipment = (RecyclerView) view.findViewById(R.id.rvDiveEquipment);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        rvDiveEquipment.setLayoutManager(mLinearLayoutManager);

        // Retrieve user equipment list
        DiveEquipment.nodeDiveEquipment(mUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mDiveEquipment = dataSnapshot.getValue(DiveEquipment.class);
                    if (!mEquipmentList.equals(MySettings.NOT_AVAILABLE)
                            && !mEquipmentList.isEmpty()) {
                        mDiveEquipment.setDiveEquipmentListValues(mEquipmentList);
                    }
                } else {
                    mDiveEquipment = new DiveEquipment();
                }

                mAdapter = new DiveEquipmentFirebaseRecyclerAdapter(mContext, mDiveEquipment);
                rvDiveEquipment.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });

        // build the dialog
        mDiveEquipmentDialog = new AlertDialog.Builder(
                getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setNegativeButton(R.string.btnCancel_title, null)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNeutralButton(R.string.btnNew_title, null)
                .create();
        return mDiveEquipmentDialog;

    }


    @Override
    public void onClick(View view) {
        //btnDeleteCheckedItems Clicked
        if (mDiveEquipment.getDiveEquipmentList().size() > 0) {
            List<String> itemsToDelete = new ArrayList<>();
            for (Object o : mDiveEquipment.getDiveEquipmentList().entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                if ((boolean) pair.getValue()) {
                    itemsToDelete.add((String) pair.getKey());
                }
            }
            if (itemsToDelete.size() > 0) {
                String title = getResources().getQuantityString(R.plurals.okToDeleteDiveLogEquipmentTitle,
                        itemsToDelete.size(), itemsToDelete.size());
                deleteDiveLogYesNoDialog(title, itemsToDelete);

            } else {
                String msg = getString(R.string.no_equipment_items_checked_message);
                MyMethods.showOkDialog(getActivity(), "", msg);
            }
        }
    }

    private void deleteDiveLogYesNoDialog(@NonNull String title,
                                          @NonNull final List<String> itemsToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage("");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                for (String item : itemsToDelete) {
                    mDiveEquipment.getDiveEquipmentList().remove(item);
                }
                DiveEquipment.save(mUserUid, mDiveEquipment.getDiveEquipmentList());
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

    @Override
    public void onResume() {
        Timber.i("onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.i("onPause()");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy()");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}