package com.lbconsulting.divelogfirebase.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.AppMetrics;
import com.lbconsulting.divelogfirebase.models.AppSettings;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.DiveSite;
import com.lbconsulting.divelogfirebase.models.Person;
import com.lbconsulting.divelogfirebase.models.ReefGuide;
import com.lbconsulting.divelogfirebase.reefGuide.ReefGuideWebPageReader;
import com.lbconsulting.divelogfirebase.ui.adapters.DiveLogPagerAdapter;
import com.lbconsulting.divelogfirebase.ui.adapters.firebase.MyFirebaseArray;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogDiveLogFilter;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogReefGuideSelection;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * This Activity shows dive log details using a ViewPager that is
 * backed by a Firebase database.
 */
public class DiveLogPagerActivity extends AppCompatActivity {

    //<editor-fold desc="activity Variables">
    private static final String EXTRA_USER_UID = "extraUserUid";
    private static final String EXTRA_CREATE_NEW_DIVE_LOG = "extraCreateNewDiveLog";

    private static AppSettings mUserAppSettings;
    private static DiveLog mActiveDiveLog;
    private static DiveLogPagerAdapter mDiveLogsPagerAdapter;
    private static MyFirebaseArray mDiveSitesArray;
    private static MyFirebaseArray mPersonsArray;

    private boolean mCreateNewDiveLog;
    private boolean mDiveSitesDownloaded;
    private boolean mPersonsDownloaded;
    private int mActivePosition;
    private LinearLayout llProgressBar;
    private String mUserUid;
    private TabLayout mTabLayout;
    private TextView tvProgressBarMessage;
    private UserAppSettingsValueEventListener mUserAppSettingsValueEventListener;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerOnPageChangeListener;
    //</editor-fold> activity Variables

    public static Intent createIntent(Context context, @NonNull String userUid, boolean
            createNewDiveLog) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_UID, userUid);
        intent.putExtra(EXTRA_CREATE_NEW_DIVE_LOG, createNewDiveLog);
        intent.setClass(context, DiveLogPagerActivity.class);
        return intent;
    }

    //<editor-fold desc="public static getters">
    public static AppSettings getUserAppSettings() {
        return mUserAppSettings;
    }

    public static long getAccumulatedBottomTimeToDate() {
        if (mDiveLogsPagerAdapter != null && mDiveLogsPagerAdapter.getTheLastDiveLog() != null) {
            return mDiveLogsPagerAdapter.getTheLastDiveLog().getAccumulatedBottomTimeToDate();
        } else {
            return 0;
        }
    }

    public static int getTotalNumberOfDives() {
        if (mDiveLogsPagerAdapter != null && mDiveLogsPagerAdapter.getTheLastDiveLog() != null) {
            return mDiveLogsPagerAdapter.getTheLastDiveLog().getDiveNumber();
        } else {
            return 0;
        }
    }

    public static DiveSite getDiveSite(String diveSiteUid) {
        int index = mDiveSitesArray.getIndexForKey(diveSiteUid);
        return mDiveSitesArray.getItem(index).getValue(DiveSite.class);
    }

    public static DiveLog getDiveLog(String diveLogUid) {
        if (mDiveLogsPagerAdapter != null) {
            return mDiveLogsPagerAdapter.getDiveLog(diveLogUid);
        } else {
            return null;
        }
    }

    public static DataSnapshot getPersonSnapshot(@NonNull String personUid) {
        DataSnapshot dataSnapshot = null;
        try {
            dataSnapshot = mPersonsArray.getItem(mPersonsArray.getIndexForKey(personUid));
        } catch (Exception e) {
            Timber.e("getPersonSnapshot(): Exception: %s.", e.getMessage());
        }
        return dataSnapshot;
    }

    public static DataSnapshot getDiveSiteSnapshot(@NonNull String diveSiteUid) {
        DataSnapshot dataSnapshot = null;
        try {
            dataSnapshot = mDiveSitesArray.getItem(mDiveSitesArray.getIndexForKey(diveSiteUid));
        } catch (Exception e) {
            Timber.e("getDiveSiteSnapshot(): Exception: %s.", e.getMessage());
        }
        return dataSnapshot;
    }

    public static DiveLog getActiveDiveLog() {
        return mActiveDiveLog;
    }

    public static MyFirebaseArray getDiveSitesArray() {
        return mDiveSitesArray;
    }

    public static ArrayList<DiveSite> getFilteredDiveSites() {
        ArrayList<DiveSite> filteredDiveSites = new ArrayList<>();
        DiveSite diveSite;
        for (int i = 0; i < mDiveSitesArray.getCount(); i++) {
            diveSite = mDiveSitesArray.getItem(i).getValue(DiveSite.class);
            int filterMethod = mUserAppSettings.getFilterMethod();
            if (mUserAppSettings.includeDiveSite(diveSite, filterMethod)) {
                filteredDiveSites.add(diveSite);
            }
        }
        return filteredDiveSites;
    }

    public static MyFirebaseArray getPersonsArray() {
        return mPersonsArray;
    }
    //</editor-fold> public static getters

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

        setContentView(R.layout.activity_dive_log_pager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_USER_UID)) {
                mUserUid = extras.getString(EXTRA_USER_UID);
            } else {
                Timber.e("onCreate(): Unable to retrieve mUserUid.");
                finish();
            }

            if (extras.containsKey(EXTRA_CREATE_NEW_DIVE_LOG)) {
                mCreateNewDiveLog = extras.getBoolean(EXTRA_CREATE_NEW_DIVE_LOG);
            } else {
                Timber.e("onCreate(): Unable to retrieve mCreateNewDiveLog.");
                finish();
            }
        }

        EventBus.getDefault().register(this);

        mViewPager = findViewById(R.id.diveLogsViewPager);
        mTabLayout = findViewById(R.id.tabs);
        llProgressBar = findViewById(R.id.llProgressBar);
        tvProgressBarMessage = findViewById(R.id.tvProgressBarMessage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiveLog theLastDiveLog = mDiveLogsPagerAdapter.getTheLastDiveLog();
                DiveLog.createNextDiveLog(mUserUid, theLastDiveLog, mUserAppSettings);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //<editor-fold desc="EventBus onEvents">
    @Subscribe
    public void onEvent(MyEvents.resetViewPagerAdapter event) {
        Timber.i("onEvent(): resetting ViewPagerAdapter");
        AppSettings.saveLastDiveLogViewedUid(mUserUid, event.getReturningDiveLogUid());
        resetViewPagerAdapter(null);
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onEvent(MyEvents.destroyDiveLogsAdapter event) {
        mDiveLogsPagerAdapter.destroy();
        mDiveLogsPagerAdapter = null;
        removeUserAppSettingsValueEventListener();
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onEvent(MyEvents.removeUserAppSettingListener event) {
        removeUserAppSettingsValueEventListener();
    }

    @Subscribe
    public void onEvent(MyEvents.scrollToPosition event) {
        scrollViewPagerToPosition(event.getPosition());
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onEvent(MyEvents.onInitialDiveLogItemsLoaded event) {
        onResume_continued(mUserAppSettings.getLastDiveLogViewedUid());
        hideProgressBar();
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

    @Subscribe
    public void onEvent(MyEvents.setDiveLogsSequenced event) {
        setDiveLogsSequenced(event.getValue());
    }
    //</editor-fold> EventBus onEvent

    private void scrollViewPagerToPosition(int position) {
        mActivePosition = position;
        if (mActivePosition < 0) {
            mActivePosition = 0;
        }
        mActiveDiveLog = mDiveLogsPagerAdapter.getDiveLog(mActivePosition);
        mDiveLogsPagerAdapter.setActiveDiveLog(mActiveDiveLog);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mViewPager.setCurrentItem(mActivePosition);
                mTabLayout.setScrollPosition(mActivePosition, 0f, true);
            }
        }, 100);
    }

    private void showProgressBar(@NonNull String message) {
        tvProgressBarMessage.setText(message);
        llProgressBar.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        if (llProgressBar.getVisibility() == View.VISIBLE) {
            llProgressBar.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
        }
    }

    private void setDiveLogsSequenced(boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MySettings.SETTING_DIVE_LOGS_SEQUENCED, value);
        editor.apply();
    }

    private void createDiveSitesArray() {
        mDiveSitesDownloaded = false;
        Query diveSitesQuery = DiveSite.nodeUserDiveSites(mUserUid).orderByChild(DiveSite.FIELD_DIVE_SITE_NAME);
        mDiveSitesArray = new MyFirebaseArray(diveSitesQuery, AppMetrics.nodeDiveSiteArraySize(mUserUid));
        mDiveSitesArray.setOnChangedListener(new MyFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, DataSnapshot dataSnapshot, int index, int
                    oldIndex) {
                // nothing to do
                DiveSite diveSite = dataSnapshot.getValue(DiveSite.class);
                if (diveSite != null) {
                    Timber.i("DiveSitesArray: onChanged(): %s %s", diveSite.getDiveSiteName(), type.toString());
                }
            }

            @Override
            public void onInitialItemsLoaded() {
                mDiveSitesDownloaded = true;
                addUserAppSettingsListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }

    private void createPersonsArray() {
        mPersonsDownloaded = false;
        Query personsQuery = Person.nodeUserPersons(mUserUid);
        mPersonsArray = new MyFirebaseArray(personsQuery, AppMetrics.nodePeopleArraySize(mUserUid));
        mPersonsArray.setOnChangedListener(new MyFirebaseArray.OnChangedListener() {

            @Override
            public void onChanged(EventType type, DataSnapshot dataSnapshot, int index, int
                    oldIndex) {
                // nothing to do
                Person person = dataSnapshot.getValue(Person.class);
                if (person != null) {
                    Timber.i("PersonsArray: onChanged(): %s %s", person.getName(), type.toString());
                }
            }

            @Override
            public void onInitialItemsLoaded() {
                mPersonsDownloaded = true;
                addUserAppSettingsListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }

    private void addUserAppSettingsListener() {
        if (mPersonsDownloaded && mDiveSitesDownloaded) {
            addUserAppSettingsValueEventListener(mUserUid);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume()");

        showProgressBar("Loading dive logs ...");
        createDiveSitesArray();
        createPersonsArray();
    }

    private void onResume_continued(String diveLogUid) {
        Timber.i("onResume_continued(): setAdapter");

        mViewPager.setAdapter(mDiveLogsPagerAdapter);
        mViewPager.addOnPageChangeListener(mViewPagerOnPageChangeListener);
        mTabLayout.setupWithViewPager(mViewPager);

        if (mCreateNewDiveLog) {
            mCreateNewDiveLog = false;
            DiveLog theLastDiveLog = mDiveLogsPagerAdapter.getTheLastDiveLog();
            DiveLog.createNextDiveLog(mUserUid, theLastDiveLog, mUserAppSettings);

        } else {
            int position = mDiveLogsPagerAdapter.getPosition(diveLogUid);
            scrollViewPagerToPosition(position);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("onPause()");
        removeUserAppSettingsValueEventListener();
        if (mActiveDiveLog != null) {
            AppSettings.saveLastDiveLogViewedUid(mUserUid, mActiveDiveLog);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
        EventBus.getDefault().unregister(this);
        mDiveLogsPagerAdapter.destroy();
        mPersonsArray.cleanup();
        mDiveSitesArray.cleanup();
    }

    //<editor-fold desc="optionsMenu">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // userUid SGUtgr6kE4WHpkKTuHzpVVcYXuF2 -> Loren
        // userUid UPJenbSO15aaRmT4ZdukW7tPx813 -> Patty (temp)
        if (mUserUid != null) {
            if (mUserUid.equals("SGUtgr6kE4WHpkKTuHzpVVcYXuF2")) {
                // action_refreshReefGuide scrapes the Reef Guide website; only available to Loren
                menu.findItem(R.id.action_refreshReefGuide).setVisible(true);
            } else {
                menu.findItem(R.id.action_refreshReefGuide).setVisible(false);
            }
        }

        if (mUserAppSettings != null) {
            if (mUserAppSettings.isImperialUnits()) {
                menu.findItem(R.id.action_switchToImperial).setVisible(false);
                menu.findItem(R.id.action_switchToMetric).setVisible(true);
            } else {
                menu.findItem(R.id.action_switchToImperial).setVisible(true);
                menu.findItem(R.id.action_switchToMetric).setVisible(false);
            }

            if (mUserAppSettings.isSortDiveLogsDescending()) {
                menu.findItem(R.id.action_sortMostRecentDiveFirst).setVisible(false);
                menu.findItem(R.id.action_sortMostRecentDiveLast).setVisible(true);
            } else {
                menu.findItem(R.id.action_sortMostRecentDiveFirst).setVisible(true);
                menu.findItem(R.id.action_sortMostRecentDiveLast).setVisible(false);
            }

            if (mUserAppSettings.isCreateNewDiveFromToday()) {
                menu.findItem(R.id.action_use_today_for_new_dive_date).setVisible(false);
                menu.findItem(R.id.action_use_last_dive_for_new_dive_date).setVisible(true);
            } else {
                menu.findItem(R.id.action_use_today_for_new_dive_date).setVisible(true);
                menu.findItem(R.id.action_use_last_dive_for_new_dive_date).setVisible(false);
            }

            menu.findItem(R.id.action_signOut).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_deleteDiveLog:
                showDeleteDiveLogYesNoDialog();
                return true;

            case R.id.action_sortMostRecentDiveFirst:
                mUserAppSettings.setSortDiveLogsDescending(true);
                mUserAppSettings.setLastDiveLogViewedUid(mActiveDiveLog.getDiveLogUid());
                AppSettings.save(mUserUid, mUserAppSettings);
                return true;

            case R.id.action_sortMostRecentDiveLast:
                mUserAppSettings.setSortDiveLogsDescending(false);
                mUserAppSettings.setLastDiveLogViewedUid(mActiveDiveLog.getDiveLogUid());
                AppSettings.save(mUserUid, mUserAppSettings);
                return true;

            case R.id.action_switchToImperial:
                AppSettings.saveIsImperialUnits(mUserUid, true);
                return true;

            case R.id.action_switchToMetric:
                AppSettings.saveIsImperialUnits(mUserUid, false);
                return true;

            case R.id.action_use_today_for_new_dive_date:
                AppSettings.saveCreateNewDiveFromToday(mUserUid, true);
                return true;

            case R.id.action_use_last_dive_for_new_dive_date:
                AppSettings.saveCreateNewDiveFromToday(mUserUid, false);
                return true;

            case R.id.action_diveLogFilter:
                showDiveLogFilterDialog();
                return true;

            case R.id.action_selectReefGuide:
                selectReefGuide();
                return true;
            case R.id.action_refreshReefGuide:
                // action_refreshReefGuide scrapes the Reef Guide website; only available to Loren
                refreshReefGuide(this, mUserAppSettings.getReefGuideId());
                return true;

            case R.id.action_sequenceDiveLogs:
                DiveLog.sequenceDiveLogs(mUserUid, mActiveDiveLog.getDiveLogUid());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectReefGuide() {
        FragmentManager fm = getSupportFragmentManager();
        dialogReefGuideSelection selectReefGuideDialog = dialogReefGuideSelection
                .newInstance(mUserUid, mUserAppSettings.getReefGuideId());
        selectReefGuideDialog.show(fm, "dialogReefGuideSelection");
    }

    private void refreshReefGuide(final Context context, final int reefGuideId) {
        DatabaseReference nodeReefGuides = ReefGuide.reefGuides(reefGuideId);
        final String reefGuideTitle = ReefGuide.getTitle(this, reefGuideId);

        nodeReefGuides.orderByChild(ReefGuide.FIELD_SORT_ORDER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<ReefGuide> reefGuides = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ReefGuide reefGuide = snapshot.getValue(ReefGuide.class);
                        reefGuides.add(reefGuide);
                    }
                }

                Timber.i("onDataChange(): Retrieved %d ReefGuides for guide \"%s\".", reefGuides.size(), reefGuideTitle);
                if (reefGuides.size() > 0) {
                    new ReefGuideWebPageReader(context, reefGuideId, reefGuideTitle, reefGuides).execute();
                } else {
                    String msg = String.format("Unable to refresh reef guide \"%s\". No reef guides retrieved from the database!", reefGuideTitle);
                    Timber.e("onDataChange(): %s", msg);
                    MyMethods.showOkDialog(context, "", msg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }

    private void showDeleteDiveLogYesNoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title;
        if (mActiveDiveLog.getDiveSiteName() != null
                && !mActiveDiveLog.getDiveSiteName().isEmpty()
                && !mActiveDiveLog.getDiveSiteName().equals(MySettings.NOT_AVAILABLE)) {
            title = String.format("Permanently delete \"%s:\" at \"%s\"?",
                    mActiveDiveLog.toString(), mActiveDiveLog.getDiveSiteName());
        } else {
            title = String.format("Permanently delete \"%s\"?", mActiveDiveLog.toString());
        }
        builder.setTitle(title);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DiveLog.deleteDiveLog(mUserUid, mActiveDiveLog);
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

    private void showDiveLogFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        Gson gson = new Gson();
        String userAppSettingsJson = gson.toJson(mUserAppSettings);
        dialogDiveLogFilter diveLogFilterDialog = dialogDiveLogFilter
                .newInstance(mUserUid, userAppSettingsJson);
        diveLogFilterDialog.show(fm, "dialogDiveLogFilter");
    }
    //</editor-fold> optionsMenu

    //<editor-fold desc="UserAppSettingsValueEventListener">
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
                resetViewPagerAdapter(dataSnapshot);
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

    private void resetViewPagerAdapter(@Nullable DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            Timber.i("resetViewPagerAdapter(): UserAppSettingsValueEventListener");
            showProgressBar("Loading dive logs ...");
            mUserAppSettings = dataSnapshot.getValue(AppSettings.class);
            if (mUserAppSettings != null) {
                setTitle(mUserAppSettings.getFilterMethodDescription(this));
                setUpViewPager();
            }
        } else {
            addUserAppSettingsListener();
        }
    }

    private void setUpViewPager() {
        Timber.i("setUpViewPager()");
        if (mDiveLogsPagerAdapter != null) {
            mDiveLogsPagerAdapter.destroy();
        }

        if (mViewPagerOnPageChangeListener != null) {
            mViewPager.removeOnPageChangeListener(mViewPagerOnPageChangeListener);
        }

        mDiveLogsPagerAdapter = new DiveLogPagerAdapter(getSupportFragmentManager(),
                mUserUid, mUserAppSettings);
        mViewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
                // nothing to do
            }

            @Override
            public void onPageSelected(int position) {
                // Update Active DiveLog;
                mActivePosition = position;
                mActiveDiveLog = mDiveLogsPagerAdapter.getDiveLog(mActivePosition);
                mDiveLogsPagerAdapter.setActiveDiveLog(mActiveDiveLog);
                Timber.i("onPageSelected(): %s", mActiveDiveLog.toString());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    EventBus.getDefault().post(new MyEvents.saveDiveRating(mActiveDiveLog));
                }
            }
        };
    }
    //</editor-fold> UserAppSettingsValueEventListener
}
