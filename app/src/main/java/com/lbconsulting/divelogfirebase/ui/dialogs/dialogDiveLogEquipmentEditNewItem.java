package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveEquipment;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import timber.log.Timber;


/**
 * This dialog updates the dive log's notes
 */
public class dialogDiveLogEquipmentEditNewItem extends DialogFragment {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_EQUIPMENT_MAP = "argEquipmentMap";
    private static final String ARG_ORIGINAL_EQUIPMENT_ITEM = "argOriginalEquipmentItem";

    private AlertDialog mDiveLogEquipmentNewItemDialog;
    private String mTitle;
    private String mUserUid;
    private String mOriginalEquipmentItem;
    private HashMap<String, Boolean> mEquipmentMap;
    //    private String mEquipmentItem;
    private boolean isNewEquipmentItem;

    private EditText txtEquipmentItem;

    public static dialogDiveLogEquipmentEditNewItem newInstance(@NonNull String userUid,
                                                                @NonNull HashMap<String, Boolean> equipmentMap,
                                                                @Nullable String equipmentItem) {
        dialogDiveLogEquipmentEditNewItem fragment = new dialogDiveLogEquipmentEditNewItem();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putSerializable(ARG_EQUIPMENT_MAP, equipmentMap);
        if (equipmentItem != null) {
            args.putString(ARG_ORIGINAL_EQUIPMENT_ITEM, equipmentItem);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public dialogDiveLogEquipmentEditNewItem() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_USER_UID) && args.containsKey(ARG_EQUIPMENT_MAP)) {
            mUserUid = args.getString(ARG_USER_UID);
            //noinspection unchecked
            mEquipmentMap = (HashMap<String, Boolean>) args.getSerializable(ARG_EQUIPMENT_MAP);
        }

        mOriginalEquipmentItem = null;
        if (args != null && args.containsKey(ARG_ORIGINAL_EQUIPMENT_ITEM)) {
            mOriginalEquipmentItem = args.getString(ARG_ORIGINAL_EQUIPMENT_ITEM);
        }

        isNewEquipmentItem = mOriginalEquipmentItem == null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mDiveLogEquipmentNewItemDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button cancelButton = mDiveLogEquipmentNewItemDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(16);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        hideKeyboard();
                        dismiss();
                    }
                });

                Button saveButton = mDiveLogEquipmentNewItemDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Save
                        String proposedEquipmentItem = txtEquipmentItem.getText().toString().trim();
                        if (proposedEquipmentItem.isEmpty()) {
                            String title = getString(R.string.invalid_equipment_item_title);
                            String msg = getString(R.string.equipment_item_is_empty_message);
                            MyMethods.showOkDialog(getActivity(), title, msg);
                        } else {
                            int numberOfEquipmentItemsInMap = getNumberOfEquipmentItems(proposedEquipmentItem, mEquipmentMap);
                            if (isNewEquipmentItem) {
                                if (numberOfEquipmentItemsInMap == 0) {
                                    // ok to create new equipment item
                                    DiveEquipment.saveEquipmentItem(mUserUid, proposedEquipmentItem);
                                    EventBus.getDefault().post(new MyEvents.addNewEquipmentItem(proposedEquipmentItem));
                                    hideKeyboard();
                                    dismiss();

                                } else {
                                    // proposed new equipment item exists
                                    showEquipmentExistDialog(proposedEquipmentItem);
                                }

                            } else {

                                if (numberOfEquipmentItemsInMap == 0) {
                                    // editing an existing equipment item
                                    // ok to update the equipment item
                                    updateEquipmentItem(mUserUid, mOriginalEquipmentItem, proposedEquipmentItem);

                                } else if (numberOfEquipmentItemsInMap == 1) {
                                    int isSameItem = mOriginalEquipmentItem.compareToIgnoreCase(proposedEquipmentItem);
                                    if (isSameItem == 0) {
                                        updateEquipmentItem(mUserUid, mOriginalEquipmentItem, proposedEquipmentItem);
                                    } else {
                                        // proposed new equipment item exists
                                        showEquipmentExistDialog(proposedEquipmentItem);
                                    }

                                } else {
                                    // proposed new equipment item exists
                                    showEquipmentExistDialog(proposedEquipmentItem);
                                }

                            }
                        }

                    }

                    private void showEquipmentExistDialog(String proposedEquipmentItem) {
                        String title = getString(R.string.invalid_equipment_item_title);
                        String msg = String.format(getString(R.string.equipment_item_exists_message),
                                proposedEquipmentItem);
                        txtEquipmentItem.setText("");
                        MyMethods.showOkDialog(getActivity(), title, msg);
                    }

                    private void updateEquipmentItem(String userUid,
                                                     String originalEquipmentItem,
                                                     String proposedEquipmentItem) {
                        DiveEquipment.removeEquipmentItem(userUid, originalEquipmentItem);
                        DiveEquipment.saveEquipmentItem(userUid, proposedEquipmentItem);

                        EventBus.getDefault().post(new MyEvents
                                .updateEquipmentItem(originalEquipmentItem, proposedEquipmentItem));
                        hideKeyboard();
                        dismiss();
                    }
                });
            }
        });
    }

    private int getNumberOfEquipmentItems(String proposedEquipmentItem, HashMap<String, Boolean> equipmentMap) {
        int numberOfEquipmentItems = 0;
        for (String key : equipmentMap.keySet()) {
            if (key.equalsIgnoreCase(proposedEquipmentItem)) {
                numberOfEquipmentItems++;
            }
        }
        return numberOfEquipmentItems;
    }

    private void hideKeyboard() {
        if (getActivity() != null && getActivity().getSystemService(Context.INPUT_METHOD_SERVICE) != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(txtEquipmentItem.getWindowToken(), 0);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");
        View view = null;
        // inflate the xml layout
        if (getActivity() != null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            view = inflater.inflate(R.layout.dialog_dive_log_equipment_new_item, null, false);
        }

        // find the dialog's views
        if (view != null) {
            txtEquipmentItem =  view.findViewById(R.id.txtEquipmentItem);
            if (isNewEquipmentItem) {
                mTitle = getString(R.string.dialogDiveLogEquipmentEditNewItem_newTitle);
            } else {
                mTitle = getString(R.string.dialogDiveLogEquipmentEditNewItem_editTitle);
                txtEquipmentItem.setText(mOriginalEquipmentItem);
            }
        }
        // build the dialog
        if (getActivity() != null) {
            mDiveLogEquipmentNewItemDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(mTitle)
                    .setView(view)
                    .setNegativeButton(R.string.btnCancel_title, null)
                    .setPositiveButton(R.string.btnSave_title, null)
                    .create();
        }
        return mDiveLogEquipmentNewItemDialog;
    }

    @Override
    public void onResume() {
        Timber.i("onResume()");
        super.onResume();
        if (getActivity() != null && getActivity().getSystemService(Context.INPUT_METHOD_SERVICE) != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }

    @Override
    public void onPause() {
        Timber.i("onPause()");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy()");
        super.onDestroy();
    }
}
