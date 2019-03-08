package com.lbconsulting.divelogfirebase.ui.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.AppSettings;

import timber.log.Timber;


/**
 * A dialog where the user selects the area, state, and country filters
 */
public class dialogReefGuideSelection extends DialogFragment {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_USER_SELECTED_REEF_GUIDE_ID = "argSelectedReefGuideId";

    private AlertDialog mReefGuideSelectionDialog;
    private String mUserUid;
    private int mSelectedReefGuideId;
    private String mTitle;

    public dialogReefGuideSelection() {
        // Empty constructor required for DialogFragment
    }

    public static dialogReefGuideSelection newInstance(@NonNull String userUid,
                                                       @NonNull int selectedReefGuideId) {
        dialogReefGuideSelection frag = new dialogReefGuideSelection();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putInt(ARG_USER_SELECTED_REEF_GUIDE_ID, selectedReefGuideId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();

        if (args.containsKey(ARG_USER_UID) && args.containsKey(ARG_USER_SELECTED_REEF_GUIDE_ID)) {
            mUserUid = args.getString(ARG_USER_UID);
            mSelectedReefGuideId = args.getInt(ARG_USER_SELECTED_REEF_GUIDE_ID);
            mTitle = "Select Reef Guide";
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mReefGuideSelectionDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button saveButton = mReefGuideSelectionDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Save
                        AppSettings.saveReefGuideId(mUserUid, mSelectedReefGuideId);
                        dismiss();
                    }
                });


                Button cancelButton = mReefGuideSelectionDialog.getButton(Dialog.BUTTON_NEGATIVE);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle)
                // Specify the list array, the items to be selected by default (null for none),
                .setSingleChoiceItems(R.array.reef_guide_titles, mSelectedReefGuideId,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int reefGuideId) {
                                mSelectedReefGuideId = reefGuideId;
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null);

        mReefGuideSelectionDialog = builder.create();

        return mReefGuideSelectionDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
    }

}
