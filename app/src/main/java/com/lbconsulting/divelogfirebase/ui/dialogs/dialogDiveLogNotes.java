package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import timber.log.Timber;


/**
 * This dialog updates the dive log's notes
 */
public class dialogDiveLogNotes extends DialogFragment {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    private static final String ARG_DIVE_LOG_NOTES = "argDiveLogNote";

    private AlertDialog mDiveLogNoteDialog;
    private String mDiveLogUid;
    private String mNotes;
    private String mTitle;
    private String mUserUid;

    private EditText txtNotes;

    public static dialogDiveLogNotes newInstance(@NonNull String userUid,
                                                 @NonNull String diveLogUid,
                                                 @NonNull String note) {
        dialogDiveLogNotes fragment = new dialogDiveLogNotes();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putString(ARG_DIVE_LOG_NOTES, note);
        fragment.setArguments(args);
        return fragment;
    }

    public dialogDiveLogNotes() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
            mNotes = args.getString(ARG_DIVE_LOG_NOTES);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mDiveLogNoteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button cancelButton = mDiveLogNoteDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(16);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        hideKeyboard();
                        dismiss();
                    }
                });

                Button saveButton = mDiveLogNoteDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Save
                        saveNote();
                    }
                });
            }
        });
    }

    private void saveNote() {
        mNotes = txtNotes.getText().toString().trim();
        DiveLog.nodeUserDiveNotes(mUserUid, mDiveLogUid).setValue(mNotes);
        hideKeyboard();
        dismiss();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtNotes.getWindowToken(), 0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_dive_log_note, null, false);

        // find the dialog's views
        if (view != null) {
            mTitle = "Dive Notes";
            txtNotes = (EditText) view.findViewById(R.id.txtNotes);
            if (mNotes == null || mNotes.equals(MySettings.NOT_AVAILABLE)) {
                mNotes = "";
            }
            txtNotes.setText(mNotes);
//            txtNotes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_DONE) {
//                        saveNote();
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            });
        }
        // build the dialog
        mDiveLogNoteDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setNegativeButton(R.string.btnCancel_title, null)
                .setPositiveButton(R.string.btnSave_title, null)
                .create();

        return mDiveLogNoteDialog;
    }

    @Override
    public void onResume() {
        Timber.i("onResume()");
        super.onResume();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
