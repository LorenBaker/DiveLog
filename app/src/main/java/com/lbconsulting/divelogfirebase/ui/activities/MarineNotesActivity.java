package com.lbconsulting.divelogfirebase.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.MarineNote;
import com.lbconsulting.divelogfirebase.ui.adapters.MarineNoteTouchHelper;
import com.lbconsulting.divelogfirebase.ui.adapters.MarineNotesRecyclerAdapter;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogMarineNoteEditNew;
import com.lbconsulting.divelogfirebase.utils.MyEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import timber.log.Timber;

public class MarineNotesActivity extends AppCompatActivity {

    private static final String EXTRA_USER_UID = "extraUserUid";
    private static final String EXTRA_DIVE_LOG_JSON = "extraDiveLogJson";

    private String mUserUid;
    private DiveLog mDiveLog;
    private final ArrayList<MarineNote> mMarineNotes = new ArrayList<>();

    private MarineNotesRecyclerAdapter mAdapter;

    public static Intent createIntent(Context context,
                                      @NonNull String userUid,
                                      @NonNull DiveLog diveLog) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_UID, userUid);
        Gson gson = new Gson();
        String diveLogJson = gson.toJson(diveLog);
        intent.putExtra(EXTRA_DIVE_LOG_JSON, diveLogJson);
        intent.setClass(context, MarineNotesActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");

        // prohibit orientation change
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_USER_UID)) {
                mUserUid = extras.getString(EXTRA_USER_UID);
            } else {
                Timber.e("onCreate(): Unable to retrieve mUserUid.");
                finish();
            }

            if (extras.containsKey(EXTRA_DIVE_LOG_JSON)) {
                String diveLogJson = extras.getString(EXTRA_DIVE_LOG_JSON);
                Gson gson = new Gson();
                mDiveLog = gson.fromJson(diveLogJson, DiveLog.class);
            } else {
                Timber.e("onCreate(): Unable to retrieve DiveLog.");
                finish();
            }
        }

        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_marine_notes);

        Toolbar mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(mDiveLog.getShortTitle());

        FloatingActionButton fab =  findViewById(R.id.fabAddMarineNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMarineNote();
            }

            private void addNewMarineNote() {
                MarineNote marineNote = new MarineNote(mUserUid, mDiveLog.getDiveLogUid(), "");
                FragmentManager fm = getSupportFragmentManager();

                dialogMarineNoteEditNew marineNoteDialog =
                        dialogMarineNoteEditNew.newInstance(mUserUid, mDiveLog.getDiveLogUid(),
                                marineNote);
                marineNoteDialog.show(fm, "dialogMarineNotes");
            }
        });

        setupRecyclerView();
    }

    @Subscribe
    public void onEvent(MyEvents.showMarineNoteEditNewDialog event) {
        FragmentManager fm = getSupportFragmentManager();

        dialogMarineNoteEditNew marineNoteDialog =
                dialogMarineNoteEditNew.newInstance(mUserUid, mDiveLog.getDiveLogUid(), event
                        .getMarineNote());
        marineNoteDialog.show(fm, "dialogMarineNotes");
    }

    @Subscribe
    public void onEvent(MyEvents.addMarineNote event) {
        mAdapter.add(event.getMarineNote());
    }

    @Subscribe
    public void onEvent(MyEvents.removeMarineNote event) {
        mAdapter.remove(event.getMarineNote());
    }

    @Subscribe
    public void onEvent(MyEvents.updateMarineNote event) {
        mAdapter.update(event.getMarineNote());
    }

    private void setupRecyclerView() {

        final RecyclerView recyclerView =  findViewById(R.id.rvMarineNotes);
        Query query = DiveLog.nodeUserMarineNotes(mUserUid, mDiveLog.getDiveLogUid())
                .orderByChild(MarineNote.FIELD_SORT_KEY);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MarineNote marineNote = snapshot.getValue(MarineNote.class);
                        if (marineNote != null) {
                            mMarineNotes.add(marineNote);
                        }
                    }
                }

                mAdapter = new MarineNotesRecyclerAdapter(mUserUid, mDiveLog.getDiveLogUid(),
                        mMarineNotes);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                        (MarineNotesActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

                ItemTouchHelper.Callback callback = new MarineNoteTouchHelper(mAdapter);
                ItemTouchHelper helper = new ItemTouchHelper(callback);
                helper.attachToRecyclerView(recyclerView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("onPause()");
        DiveLog.saveMarineNotes(mUserUid, mDiveLog.getDiveLogUid(), mMarineNotes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    private void setTitle(@NonNull String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
