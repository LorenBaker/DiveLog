package com.lbconsulting.divelogfirebase.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.AppSettings;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.ReefGuide;
import com.lbconsulting.divelogfirebase.models.ReefGuideItem;
import com.lbconsulting.divelogfirebase.reefGuide.ReefGuideAdapter;
import com.lbconsulting.divelogfirebase.reefGuide.ReefGuideTouchHelper;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class ReefGuideActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static Intent createIntent(@NonNull Context context,
                                      @NonNull String userUid,
                                      @NonNull String userAppSettingsJason,
                                      @NonNull String diveLogJason) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_UID, userUid);
        intent.putExtra(EXTRA_USER_APP_SETTINGS_JASON, userAppSettingsJason);
        intent.putExtra(EXTRA_DIVE_LOG_JASON, diveLogJason);

        intent.setClass(context, ReefGuideActivity.class);
        return intent;
    }

    private static final String EXTRA_USER_UID = "extraUserUid";
    private static final String EXTRA_USER_APP_SETTINGS_JASON = "extraUserAppSettingsJson";
    private static final String EXTRA_DIVE_LOG_JASON = "extraDiveLogJson";

    private String mUserUid;
    private AppSettings mUserAppSettings;
    private DiveLog mDiveLog;
    private HashMap<String, ReefGuide> mReefGuideMap;
    private HashMap<String, ArrayList<ReefGuideItem>> mReefGuideItemsMap;
    private RecyclerView mRecyclerView;
    private TextView tvSelectedReefGuideTitle;
    private WebView mWebView;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private boolean mIsSelectableReefGuide;
    private ItemTouchHelper mItemTouchHelper;
    private String mActiveReefGuidesTitle;

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

        // retrieve extras
        Intent intent = getIntent();
        mUserUid = intent.getStringExtra(EXTRA_USER_UID);
        String userAppSettingsJson = intent.getStringExtra(EXTRA_USER_APP_SETTINGS_JASON);
        String diveLogJson = intent.getStringExtra(EXTRA_DIVE_LOG_JASON);
        Gson gson = new Gson();
        mUserAppSettings = gson.fromJson(userAppSettingsJson, AppSettings.class);
        String[] reefGuidesTitles = getResources().getStringArray(R.array.reef_guide_titles);
        mActiveReefGuidesTitle = reefGuidesTitles[mUserAppSettings.getReefGuideId()].toUpperCase();
        mDiveLog = gson.fromJson(diveLogJson, DiveLog.class);

        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_reef_guide);
        mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout mDrawerLayout =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView =  findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        tvSelectedReefGuideTitle =findViewById(R.id.tvSelectedReefGuideTitle);
        mRecyclerView =  findViewById(R.id.card_recycler_view);
        mWebView =  findViewById(R.id.webView);
    }


    @Subscribe
    public void onEvent(MyEvents.addReefGuideItemToDiveLog event) {
        ReefGuideItem reefGuideItem = event.getReefGuideItem();
        if (mDiveLog.getReefGuideItems() != null) {
            int diveLogReefGuideItemIndex = mDiveLog.getReefGuideItems().indexOf(reefGuideItem);
            if (diveLogReefGuideItemIndex < 0) {
                mDiveLog.getReefGuideItems().add(reefGuideItem);
            } else {
                mDiveLog.getReefGuideItems().set(diveLogReefGuideItemIndex, reefGuideItem);
            }
        } else {
            mDiveLog.setReefGuideItems(new ArrayList<ReefGuideItem>());
            mDiveLog.getReefGuideItems().add(reefGuideItem);
        }
        setReefGuideItemCheckBox(reefGuideItem, true);
    }

    @Subscribe
    public void onEvent(MyEvents.removeReefGuideItemFromDiveLog event) {
        ReefGuideItem reefGuideItem = event.getReefGuideItem();
        if (mDiveLog.getReefGuideItems() != null) {
            int index = mDiveLog.getReefGuideItems().indexOf(reefGuideItem);
            if (index > -1) {
                mDiveLog.getReefGuideItems().remove(index);
            }
        }
        setReefGuideItemCheckBox(reefGuideItem, false);
    }

    @Subscribe
    public void onEvent(MyEvents.showWebsiteDetail event) {
        ReefGuideItem reefGuideItem = event.getReefGuideItem();
        mWebView.loadUrl(reefGuideItem.getReefGuideDetailUrl());
        showWebView();
    }

    private void showWebView() {
        mWebView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
    }


    private void setReefGuideItemCheckBox(ReefGuideItem reefGuideItem, boolean isChecked) {
        ArrayList<ReefGuideItem> guideItems = mReefGuideItemsMap.get(reefGuideItem.getSummaryPageUid());
        int reefGuideItemIndex = guideItems.indexOf(reefGuideItem);
        if (reefGuideItemIndex > -1) {
            ReefGuideItem selectedItem = guideItems.get(reefGuideItemIndex);
            selectedItem.setChecked(isChecked);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume()");
        mToolbar.setTitle(mDiveLog.getShortTitle());
        mReefGuideMap = new HashMap<>();
        mReefGuideItemsMap = new HashMap<>();

        retrieveReefGuides(mUserAppSettings.getReefGuideId());
    }

    private void retrieveReefGuides(final int reefGuideId) {
        ReefGuide.reefGuides(reefGuideId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ReefGuide reefGuide = snapshot.getValue(ReefGuide.class);
                                if(reefGuide!=null) {
                                    mReefGuideMap.put(reefGuide.getSummaryPageUid(), reefGuide);
                                }
                            }
                        }
                        retrieveReefGuideItems(reefGuideId);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                    }
                }
        );
    }

    private void retrieveReefGuideItems(int reefGuideId) {
        for (ReefGuide reefGuide : mReefGuideMap.values()) {
            retrieveItems(reefGuideId, reefGuide);
        }
    }

    private void retrieveItems(int reefGuideId, final ReefGuide reefGuide) {
        ReefGuideItem.nodeReefGuideItems(reefGuideId, reefGuide.getSummaryPageUid())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    ArrayList<ReefGuideItem> items = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ReefGuideItem item = snapshot.getValue(ReefGuideItem.class);
                                        items.add(item);
                                    }
                                    items = ReefGuideItem.sortReefGuideItemsBySortOrderKey(items);
                                    mReefGuideItemsMap.put(reefGuide.getSummaryPageUid(), items);
                                    if (mReefGuideItemsMap.size() == mReefGuideMap.size()) {
                                        continueOnResume();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                            }
                        }
                );
    }

    private void continueOnResume() {
        Timber.i("continueOnResume()");
        Menu navMenu = mNavigationView.getMenu();
        setupNavigationViewMenu(mNavigationView);

        // select either the the
        //  first navMenuItem(0) - DiveLogShortTitle; or
        //  second navMenuItem(1) - first reefGuide
        MenuItem navMenuItem = navMenu.getItem(1);
        mIsSelectableReefGuide = true;

        if (mDiveLog.getReefGuideItems() != null
                && mDiveLog.getReefGuideItems().size() > 0) {

            navMenuItem = mNavigationView.getMenu().getItem(0);
            mIsSelectableReefGuide = false;
        }

        navMenuItem.setChecked(true);
        onNavigationItemSelected(navMenuItem);
    }

    private void setupNavigationViewMenu(NavigationView navigationView) {
        Menu navMenu = mNavigationView.getMenu();
        TextView tvSummaryPageTitle =  navigationView.findViewById(R.id.tvSummaryPageTitle);
        tvSummaryPageTitle.setText(mActiveReefGuidesTitle);

        // set the first nav menu title to the dive log's short title
        navMenu.add(R.id.menu_reef_guide_nav_view_group, 0, 0, mDiveLog.getShortTitle());

        // set remaining nav menu titles
        ArrayList<ReefGuide> reefGuides = new ArrayList<>(mReefGuideMap.values());
        reefGuides = ReefGuide.sortReefGuideBySortOrderKey(reefGuides);
        for (int i = 0; i < reefGuides.size(); i++) {
            String menuTitle = reefGuides.get(i).getSummaryPageTitle();
            MenuItem navMenuItem = navMenu.add(R.id.menu_reef_guide_nav_view_group, i + 1, i + 1, menuTitle);
            View actionView = new View(this);
            actionView.setTag(reefGuides.get(i));
            navMenuItem.setActionView(actionView);
        }
        // Note: must be called after adding all menu items
        navMenu.setGroupCheckable(R.id.menu_reef_guide_nav_view_group, true, true);

        // set check marks for diveLogReefGuideItems
        if (mDiveLog.getReefGuideItems() != null
                && mDiveLog.getReefGuideItems().size() > 0) {

            for (ReefGuideItem reefGuideItem : mDiveLog.getReefGuideItems()) {
                ArrayList<ReefGuideItem> diveLogReefGuideItems = mReefGuideItemsMap.get(reefGuideItem.getSummaryPageUid());
                int index = diveLogReefGuideItems.indexOf(reefGuideItem);
                if (index > -1) {
                    ReefGuideItem selectedItem = diveLogReefGuideItems.get(index);
                    selectedItem.setChecked(true);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("onPause()");
        saveReefGuideSelectedItems();
    }

    private void saveReefGuideSelectedItems() {
        if (mDiveLog.getReefGuideItems() != null
                && mDiveLog.getReefGuideItems().size() > 0) {
            DiveLog.saveUserDiveReefGuideItems(this, mUserUid, mDiveLog.getDiveLogUid(),
                    mDiveLog.getReefGuideItems());

        } else {
            DiveLog.removeReefGuideItems(mUserUid, mDiveLog.getDiveLogUid());
        }
    }

    //<editor-fold desc="Balance of Lifecycle">
    @Override
    protected void onStart() {
        super.onStart();
        Timber.i("onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.i("onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy()");
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Timber.i("onRestart()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.i("onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Timber.i("onRestoreInstanceState()");
    }
    //</editor-fold> Balance of Lifecycle

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mWebView.getVisibility() == View.VISIBLE) {
            showRecyclerView();
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem navMenuItem) {
        // Handle navigation view item clicks here.
        ArrayList<ReefGuideItem> reefGuideItems = null;
        String summaryPageUid;
        final int DIVE_LOG = 0;
        navMenuItem.setChecked(true);

        if (navMenuItem.getItemId() == DIVE_LOG) {
            if(mDiveLog.getReefGuideItems()==null || mDiveLog.getReefGuideItems().size()==0){
                String msg = String.format("\"%s\" has no entries!\nPlease select a reef guide.",
                        mDiveLog.getShortTitle());
                MyMethods.showOkDialog(this, "", msg);
                return true;
            }else{
                mIsSelectableReefGuide = false;
                reefGuideItems = ReefGuideItem.sortReefGuideItemsByTitle(mDiveLog.getReefGuideItems());
                tvSelectedReefGuideTitle.setVisibility(View.GONE);
            }

        } else {
            mIsSelectableReefGuide = true;
            View actionView = navMenuItem.getActionView();
            if (actionView != null) {
                ReefGuide reefGuide = (ReefGuide) actionView.getTag();
                if (reefGuide != null) {
                    summaryPageUid = reefGuide.getSummaryPageUid();
                    reefGuideItems = mReefGuideItemsMap.get(summaryPageUid);
                    tvSelectedReefGuideTitle.setText(reefGuide.getSummaryPageTitle());
                    tvSelectedReefGuideTitle.setVisibility(View.VISIBLE);
                }
            }
        }

        if (reefGuideItems != null) {
            if (mIsSelectableReefGuide) {
                initSelectableReefGuides(reefGuideItems);
            } else {
                initDiveLogReefGuideEntries(reefGuideItems);
            }
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initSelectableReefGuides(ArrayList<ReefGuideItem> reefGuideItems) {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        ReefGuideAdapter adapter = new ReefGuideAdapter(this, reefGuideItems, mIsSelectableReefGuide);
        mRecyclerView.setAdapter(adapter);

        // Remove ItemTouchHelper for swipe to delete
        if (mItemTouchHelper != null) {
            mItemTouchHelper.attachToRecyclerView(null);
            mItemTouchHelper = null;
        }

    }

    private void initDiveLogReefGuideEntries(ArrayList<ReefGuideItem> reefGuideItems) {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        ReefGuideAdapter adapter = new ReefGuideAdapter(this, reefGuideItems, mIsSelectableReefGuide);
        mRecyclerView.setAdapter(adapter);

        // Setup ItemTouchHelper for swipe to delete
        ItemTouchHelper.Callback callback = new ReefGuideTouchHelper(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
