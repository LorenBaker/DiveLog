package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.MarineNote;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;


/**
 * This dialog updates the dive log's notes
 */
public class dialogMarineNoteEditNew extends DialogFragment {

    private static final String ARG_USER_UID = "argUserUid";
    private static final String ARG_DIVE_LOG_UID = "argDiveLogUid";
    private static final String ARG_DIVE_LOG_MARINE_NOTE_JSON = "argDiveLogMarineNoteJson";

    private AlertDialog mDiveLogMarineNoteDialog;
    private String mDiveLogUid;
    private MarineNote mMarineNote;
    private boolean isNewMarineNote;
    private String mTitle;
    private String mUserUid;

    private EditText txtNotes;

    public static dialogMarineNoteEditNew newInstance(@NonNull String userUid,
                                                      @NonNull String diveLogUid,
                                                      @NonNull MarineNote marineNote) {
        dialogMarineNoteEditNew fragment = new dialogMarineNoteEditNew();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        Gson gson = new Gson();
        String marineNoteJson = gson.toJson(marineNote);
        args.putString(ARG_DIVE_LOG_MARINE_NOTE_JSON, marineNoteJson);
        fragment.setArguments(args);
        return fragment;
    }

    public dialogMarineNoteEditNew() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_USER_UID)) {
                mUserUid = args.getString(ARG_USER_UID);
                mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
                String marineNoteJson = args.getString(ARG_DIVE_LOG_MARINE_NOTE_JSON);
                Gson gson = new Gson();
                mMarineNote = gson.fromJson(marineNoteJson, MarineNote.class);
                isNewMarineNote = mMarineNote.getMarineNoteUid().equals(MySettings.NOT_AVAILABLE);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated()");

        mDiveLogMarineNoteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button cancelButton = mDiveLogMarineNoteDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(16);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        hideKeyboard();
                        dismiss();
                    }
                });

                final Button saveButton = mDiveLogMarineNoteDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Save
                        saveNote();
                        hideKeyboard();
                        dismiss();
                    }
                });

                final Button deleteButton = mDiveLogMarineNoteDialog.getButton(Dialog.BUTTON_NEUTRAL);
                deleteButton.setTextSize(16);
                deleteButton.setEnabled(true);
                if (mMarineNote.getNote() == null || mMarineNote.getNote().isEmpty()) {
                    deleteButton.setEnabled(false);
                }
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        deleteNote();
                        hideKeyboard();
                        dismiss();
                    }
                });
            }
        });
    }

    private void deleteNote() {
        EventBus.getDefault().post(new MyEvents.removeMarineNote(mMarineNote));
    }

    private void saveNote() {
        String marineNoteText = txtNotes.getText().toString().trim();
        if (!marineNoteText.isEmpty()) {
            mMarineNote.setNote(marineNoteText);
            if (isNewMarineNote) {
                EventBus.getDefault().post(new MyEvents.addMarineNote(mMarineNote));
            } else {
                EventBus.getDefault().post(new MyEvents.updateMarineNote(mMarineNote));
            }
        } else {
            String title = "Unable Add or Update Note";
            String msg = "The marine note is empty!";
            MyMethods.showOkDialog(getActivity(), title, msg);
        }
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(txtNotes.getWindowToken(), 0);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // inflate the xml layout
        View view = null;
        if (getActivity() != null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            view = inflater.inflate(R.layout.dialog_dive_log_marine_note, null, false);
        }

        // find the dialog's views
        if (view != null) {
            mTitle = "Marine Note";
            txtNotes = view.findViewById(R.id.txtNotes);
            txtNotes.setText(mMarineNote.getNote());

            txtNotes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        saveNote();
                        mMarineNote = new MarineNote(mUserUid, mDiveLogUid, "");
                        isNewMarineNote = true;
                        txtNotes.setText("");
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        // build the dialog
        mDiveLogMarineNoteDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setNegativeButton(R.string.btnCancel_title, null)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNeutralButton(R.string.btnDelete_title, null)
                .create();

        return mDiveLogMarineNoteDialog;
    }


    @Override
    public void onResume() {
        Timber.i("onResume()");
        super.onResume();
        if (getActivity() != null) {
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
