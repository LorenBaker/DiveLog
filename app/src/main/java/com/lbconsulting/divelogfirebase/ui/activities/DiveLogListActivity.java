package com.lbconsulting.divelogfirebase.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.AppSettings;
import com.lbconsulting.divelogfirebase.models.AppUser;
import com.lbconsulting.divelogfirebase.models.DiveEquipment;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.SelectionValue;
import com.lbconsulting.divelogfirebase.ui.activities.signIn.AuthUiActivity;
import com.lbconsulting.divelogfirebase.ui.adapters.DiveLogsListFirebaseRecyclerAdapter;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogDiveLogFilter;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import timber.log.Timber;

/**
 * This class is the App's launcher activity
 * This Activity shows dive log summaries in a Recycler Adapter
 * backed by a Firebase database.
 */
public class DiveLogListActivity extends AppCompatActivity {

    private AppSettings mUserAppSettings;
    private AppUser mAppUser;
    private boolean mReturningFromOnRestart;
    private DiveLogsListFirebaseRecyclerAdapter mDiveLogsListAdapter;
    private FirebaseAuth mFirebaseAuth;
    private CoordinatorLayout mRootView;
    private LinearLayout llProgressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView diveLogRecyclerView;
    private RelativeLayout rlDiveLogRecyclerView;
    private String mUserUid;
    private TextView tvProgressBarMessage;
    private UserAppSettingsValueEventListener mUserAppSettingsValueEventListener;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DiveLogListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate()");
        super.onCreate(savedInstanceState);

        // prohibit orientation change
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        EventBus.getDefault().register(this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(AuthUiActivity.createIntent(DiveLogListActivity.this));
            finish();
            return;
        } else {
            Timber.i("onCreate(): User \"%s\" signed in.", mFirebaseAuth.getCurrentUser()
                    .getDisplayName());
        }

        setContentView(R.layout.activity_dive_log_list);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRootView =  findViewById(R.id.rootView);
        diveLogRecyclerView =  findViewById(R.id.diveLogRecyclerView);

        rlDiveLogRecyclerView =  findViewById(R.id.rlDiveLogRecyclerView);
        llProgressBar =  findViewById(R.id.llProgressBar);
        tvProgressBarMessage =  findViewById(R.id.tvProgressBarMessage);
        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserAppSettingsValueEventListener();
                Intent intent = DiveLogPagerActivity
                        .createIntent(DiveLogListActivity.this, mUserUid, true);
                startActivity(intent);
            }
        });

        mReturningFromOnRestart = false;
        setDiveLogsSequencedFalse();
    }

    //<editor-fold desc="EventBus onEvents">
    @Subscribe
    public void onEvent(MyEvents.startDiveLogPagerActivity event) {
        if (event.getDiveLog() != null) {
            removeUserAppSettingsValueEventListener();
            AppSettings.saveLastDiveLogViewedUid(mUserUid, event.getDiveLog());
        }
        Intent intent = DiveLogPagerActivity.createIntent(this, mUserUid, false);
        startActivity(intent);
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onEvent(MyEvents.removeUserAppSettingListener event) {
        removeUserAppSettingsValueEventListener();
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onEvent(MyEvents.onInitialDiveLogItemsLoaded event) {
        diveLogRecyclerView.setAdapter(mDiveLogsListAdapter);
        hideProgressBar();
        setRecyclerViewPosition();
    }

    @Subscribe
    public void onEvent(MyEvents.showProgressBar event) {
        showProgressBar(event.getMessage());
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onEvent(MyEvents.hideProgressBar event) {
        hideProgressBar();
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onEvent(MyEvents.destroyDiveLogsAdapter event) {
        mDiveLogsListAdapter.destroy();
        removeUserAppSettingsValueEventListener();
    }

    @Subscribe
    public void onEvent(MyEvents.scrollToPosition event) {
        mLinearLayoutManager.scrollToPositionWithOffset(event.getPosition(), 0);
    }
    //</editor-fold> EventBus onEvents

    private boolean getDiveLogsSequenced() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(MySettings.SETTING_DIVE_LOGS_SEQUENCED, false);
    }

    private void setDiveLogsSequencedFalse() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MySettings.SETTING_DIVE_LOGS_SEQUENCED, false);
        editor.apply();
    }

    private void setRecyclerViewPosition() {
        int position = 0;
        if (!mUserAppSettings.getLastDiveLogViewedUid().equals(MySettings.NOT_AVAILABLE)) {
            position = mDiveLogsListAdapter.findPosition(mUserAppSettings.getLastDiveLogViewedUid
                    ());
            position = position - 2;
            if (position < 0) {
                position = 0;
            }
        }
        mLinearLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    private void showProgressBar(@NonNull String message) {
        tvProgressBarMessage.setText(message);
        llProgressBar.setVisibility(View.VISIBLE);
        rlDiveLogRecyclerView.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        if (llProgressBar.getVisibility() == View.VISIBLE) {
            llProgressBar.setVisibility(View.GONE);
            rlDiveLogRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    //<editor-fold desc="Lifecycle Overrides">
    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume()");
        try {
            if (mFirebaseAuth != null
                    && mFirebaseAuth.getCurrentUser() != null) {
                AppUser.nodeUser(mFirebaseAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    //The appUser exists, so the App has already been initialized.
                                    mAppUser = dataSnapshot.getValue(AppUser.class);
                                    if (mAppUser != null) {
                                        mUserUid = mAppUser.getUserUid();
                                        addUserAppSettingsValueEventListener(mUserUid);
                                    }
                                } else {
                                    initializeApp();
                                }
                            }

                            //<editor-fold desc="App Initialization">
                            private void initializeApp() {
                                Timber.i("initializeApp()");
                                initializeUser();
                                initializeUserAppSettings();
                                addUserAppSettingsValueEventListener(mUserUid);
                            }

                            private void initializeUser() {
                                // Create a user in the Firebase db
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                if (user != null) {
                                    String displayName = MySettings.NOT_AVAILABLE;
                                    if (user.getDisplayName() != null) {
                                        displayName = user.getDisplayName();
                                    }

                                    String email = MySettings.NOT_AVAILABLE;
                                    if (user.getEmail() != null) {
                                        email = user.getEmail();
                                    }

                                    String photoUrl = MySettings.NOT_AVAILABLE;
                                    if (user.getPhotoUrl() != null) {
                                        photoUrl = user.getPhotoUrl().toString();
                                    }
                                    mAppUser = new AppUser(user.getUid(), displayName, email,
                                            photoUrl);

                                    AppUser.saveAppUser(mAppUser);
                                    mUserUid = mAppUser.getUserUid();

                                    Timber.i("initializeUser() complete: Created user \"%s\".",
                                            mAppUser.getDisplayName());
                                } else {
                                    Timber.e("initializeUser(): Unable to initializeUser. Failed " +
                                            "to retrieve user!");
                                }
                            }

                            private void initializeUserAppSettings() {
                                // Create the user's appSettings
                                if (mAppUser != null && !mUserUid.isEmpty()) {
                                    mUserAppSettings = AppSettings.getDefaultAppSettings(mAppUser);
                                    AppSettings.save(mUserUid, mUserAppSettings);
                                    Timber.i("initializeUserAppSettings() complete: Created " +
                                                    "userAppSettings for user \"%s\".",
                                            mAppUser.getDisplayName());
                                    initializeSelectionValues();
                                } else {
                                    Timber.e("initializeUserAppSettings(): FAILED to " +
                                            "initializeUserAppSettings. mAppUser is " +
                                            "null!");
                                }
                            }

                            private void initializeSelectionValues() {
                                SelectionValue.nodeInitialSelectionValues()
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() != null) {
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        switch (snapshot.getKey()) {
                                                            case SelectionValue.KEY_AREA_INITIAL_VALUES:
                                                                initializeAreaValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_STATE_INITIAL_VALUES:
                                                                initializeStateValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_COUNTRY_INITIAL_VALUES:
                                                                initializeCountryValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_CURRENT_INITIAL_VALUES:
                                                                initializeCurrentValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_DIVE_ENTRY_INITIAL_VALUES:
                                                                initializeDiveEntryValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_DIVE_DIVE_EQUIPMENT_INITIAL_VALUES:
                                                                initializeDiveEquipmentValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_DIVE_STYLE_INITIAL_VALUES:
                                                                initializeDiveStyleValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_DIVE_TANK_INITIAL_VALUES:
                                                                initializeDiveTankValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_DIVE_TYPE_INITIAL_VALUES:
                                                                initializeDiveTypeValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_SEA_CONDITION_INITIAL_VALUES:
                                                                initializeSeaConditionValues(snapshot);
                                                                break;
                                                            case SelectionValue.KEY_WEATHER_CONDITION_INITIAL_VALUES:
                                                                initializeWeatherConditionValues(snapshot);
                                                                break;
                                                        }
                                                    }
                                                }
                                            }

                                            private void initializeAreaValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_AREA_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeStateValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_STATE_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeCountryValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_COUNTRY_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeCurrentValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_CURRENT_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeDiveEntryValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_DIVE_ENTRY_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeDiveEquipmentValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> diveEquipment =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (diveEquipment != null) {
                                                    DiveEquipment.save(mUserUid, diveEquipment);
                                                }
                                            }

                                            private void initializeDiveStyleValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_DIVE_STYLE_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeDiveTankValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_DIVE_TANK_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeDiveTypeValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_DIVE_TYPE_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeSeaConditionValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_SEA_CONDITION_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            private void initializeWeatherConditionValues(DataSnapshot snapshot) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String, Boolean> hashMap =
                                                        (HashMap<String, Boolean>) snapshot.getValue();
                                                if (hashMap != null && snapshot.getValue() != null) {
                                                    for (String value : hashMap.keySet()) {
                                                        SelectionValue selectionValue =
                                                                new SelectionValue(value, SelectionValue.NODE_WEATHER_CONDITION_VALUES);
                                                        SelectionValue.save(mUserUid, selectionValue);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                                            }
                                        });
                            }

                            //</editor-fold> App Initialization

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                            }
                        });
            }
        } catch (Exception e) {
            Timber.e("onResume(): Exception: %s.", e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        Timber.i("onPause()");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Timber.i("onDestroy()");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        removeUserAppSettingsValueEventListener();
        if (mDiveLogsListAdapter != null) {
            mDiveLogsListAdapter.destroy();
        }
    }

    @Override
    protected void onRestart() {
        Timber.i("onRestart()");
        super.onRestart();
        mReturningFromOnRestart = true;
    }
    //</editor-fold> Lifecycle Overrides

    //<editor-fold desc="Options Menu">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_selectReefGuide).setVisible(false);
        menu.findItem(R.id.action_refreshReefGuide).setVisible(false);
        menu.findItem(R.id.action_sequenceDiveLogs).setVisible(false);
        menu.findItem(R.id.action_deleteDiveLog).setVisible(false);
        menu.findItem(R.id.action_switchToImperial).setVisible(false);
        menu.findItem(R.id.action_switchToMetric).setVisible(false);
        menu.findItem(R.id.action_use_today_for_new_dive_date).setVisible(false);
        menu.findItem(R.id.action_use_last_dive_for_new_dive_date).setVisible(false);

        if (mUserAppSettings != null && mUserAppSettings.isSortDiveLogsDescending()) {
            menu.findItem(R.id.action_sortMostRecentDiveFirst).setVisible(false);
            menu.findItem(R.id.action_sortMostRecentDiveLast).setVisible(true);
        } else {
            menu.findItem(R.id.action_sortMostRecentDiveFirst).setVisible(true);
            menu.findItem(R.id.action_sortMostRecentDiveLast).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sortMostRecentDiveFirst:
                AppSettings.saveSortDiveLogsDescending(mUserUid, true);
                return true;
            case R.id.action_sortMostRecentDiveLast:
                AppSettings.saveSortDiveLogsDescending(mUserUid, false);
                return true;
            case R.id.action_diveLogFilter:
                showDiveLogFilterDialog();
                return true;
            case R.id.action_signOut:
                signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(AuthUiActivity.createIntent(DiveLogListActivity.this));
                            finish();
                        } else {
                            MyMethods.showSnackbar(mRootView, getResources()
                                    .getString(R.string.sign_out_failed), Snackbar.LENGTH_LONG);
                        }
                    }
                });
    }

    private void showDiveLogFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        Gson gson = new Gson();
        String userAppSettingsJson = gson.toJson(mUserAppSettings);
        dialogDiveLogFilter diveLogFilterDialog = dialogDiveLogFilter
                .newInstance(mUserUid, userAppSettingsJson);
        diveLogFilterDialog.show(fm, "dialogDiveLogFilter");
    }
    //</editor-fold> Options Menu

    //<editor-fold desc="UserAppSettingsValueEventListener">
    private void setupDiveLogsListRecyclerView() {
        showProgressBar("Loading dive logs ...");
        // This query returns DiveLogs in ascending sort order
        Query diveLogsQuery = DiveLog.nodeUserDiveLogs(mUserUid).orderByChild(DiveLog.FIELD_DIVE_START);

        if (mDiveLogsListAdapter != null) {
            mDiveLogsListAdapter.destroy();

        } else {
            mLinearLayoutManager = new LinearLayoutManager(this);
            diveLogRecyclerView.setLayoutManager(mLinearLayoutManager);
        }

        mDiveLogsListAdapter = new DiveLogsListFirebaseRecyclerAdapter(mUserUid, diveLogsQuery,
                DiveLog.class,
                mUserAppSettings);
    }

    private void addUserAppSettingsValueEventListener(String userUid) {
        mUserAppSettingsValueEventListener = new UserAppSettingsValueEventListener();
        AppSettings.nodeUserAppSettings(userUid)
                .addValueEventListener(mUserAppSettingsValueEventListener);
    }

    private void removeUserAppSettingsValueEventListener() {
        if (mUserAppSettingsValueEventListener != null) {
            AppSettings.nodeUserAppSettings(mUserUid)
                    .removeEventListener(mUserAppSettingsValueEventListener);
        }
    }

    private class UserAppSettingsValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                mUserAppSettings = dataSnapshot.getValue(AppSettings.class);
                if (mUserAppSettings != null) {
                    if (mDiveLogsListAdapter != null) {
                        mDiveLogsListAdapter.setUserAppSettings(mUserAppSettings);
                    }
                    setTitle(mUserAppSettings.getFilterMethodDescription(DiveLogListActivity.this));
                    boolean isDiveLogsSequenced = getDiveLogsSequenced();
                    if (mReturningFromOnRestart && !isDiveLogsSequenced) {
                        mReturningFromOnRestart = false;
                        setDiveLogsSequencedFalse();
                        setRecyclerViewPosition();
                    } else {
                        setupDiveLogsListRecyclerView();
                    }
                }

            } else {
                Timber.e("UserAppSettingsValueEventListener: onDataChange(): FAILED to retrieve " +
                        "mUserAppSettings");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
        }
    }
    //</editor-fold>

}
