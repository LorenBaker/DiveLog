package com.lbconsulting.divelogfirebase.ui.adapters;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.models.AppMetrics;
import com.lbconsulting.divelogfirebase.models.AppSettings;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.ui.adapters.firebase.MyFirebaseArray;
import com.lbconsulting.divelogfirebase.ui.fragments.DiveLogDetailFragment;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * A FragmentPagerAdapter that displays DiveLogDetailFragments.
 */
//public class SectionsPagerAdapter extends FragmentPagerAdapter {
public class DiveLogPagerAdapter extends FragmentStatePagerAdapter {

    private final String mUserUid;
    private final AppSettings mUserAppSettings;
    private final MyFirebaseArray mDiveLogsArray;
    private ArrayList<DiveLog> mFilteredDiveLogs;
    private DiveLog mTheLastDiveLog;
    private final FragmentManager mFragmentManager;
    private DiveLog mActiveDiveLog;

    public DiveLogPagerAdapter(@NonNull FragmentManager fm,
                               @NonNull String userUid,
                               @NonNull AppSettings userAppsAppSettings) {
        super(fm);
        this.mFilteredDiveLogs = new ArrayList<>();
        this.mUserUid = userUid;
        this.mUserAppSettings = userAppsAppSettings;
        this.mFragmentManager = fm;

        // This query returns DiveLogs ascending sort order
        Query query = DiveLog.nodeUserDiveLogs(mUserUid).orderByChild(DiveLog.FIELD_DIVE_START);
        mDiveLogsArray = new MyFirebaseArray(query, AppMetrics.nodeDiveLogArraySize(mUserUid));

        mDiveLogsArray.setOnChangedListener(new MyFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, DataSnapshot dataSnapshot, int index, int
                    oldIndex) {
                switch (type) {
                    case ADDED:
                        DiveLog addedDiveLog = dataSnapshot.getValue(DiveLog.class);
                        if (addedDiveLog != null) {
                            if (mTheLastDiveLog != null) {
                                if (addedDiveLog.getDiveStart() > mTheLastDiveLog.getDiveStart()) {
                                    mTheLastDiveLog = addedDiveLog;
                                }
                            } else {
                                mTheLastDiveLog = addedDiveLog;
                            }

                            if (mUserAppSettings.includeDiveLog(addedDiveLog)) {
                                int position = mFilteredDiveLogs.size();
                                if (mUserAppSettings.isSortDiveLogsDescending()) {
                                    position = 0;
                                }
                                mFilteredDiveLogs.add(position, addedDiveLog);
                                Timber.i("onChanged(): ADDED %s at position %d.", addedDiveLog
                                        .toString(), position);
                                notifyDataSetChanged();
                                EventBus.getDefault().post(new MyEvents.scrollToPosition(position));
                            }
                        }
                        break;

                    case CHANGED:
                        DiveLog changedDiveLog = dataSnapshot.getValue(DiveLog.class);
                        if (changedDiveLog != null) {
                            if (changedDiveLog.getDiveLogUid().equals(mTheLastDiveLog.getDiveLogUid()
                            )) {
                                mTheLastDiveLog = changedDiveLog;
                            }
                            if (changedDiveLog.isSequencingRequired()) {
                                Timber.i("onChanged(): CHANGED sequenceDiveLogs started by %s; uid:%s",
                                        changedDiveLog.toString(), changedDiveLog.getDiveLogUid());
                                if (mActiveDiveLog != null) {
                                    DiveLog.sequenceDiveLogs(mUserUid, mActiveDiveLog.getDiveLogUid());
                                } else {
                                    Timber.e("onChanged(): isSequencingRequired: mActiveDiveLog==null! ");
                                }
                            } else {
                                Timber.i("onChanged(): CHANGED populateDiveLogDetailFragmentUI " +
                                                "started by %s; uid:%s",
                                        changedDiveLog.toString(), changedDiveLog.getDiveLogUid());
                                int position = getPosition(changedDiveLog.getDiveLogUid());
                                if (position > -1) {
                                    mFilteredDiveLogs.set(position, changedDiveLog);
                                    EventBus.getDefault().post(new MyEvents
                                            .populateDiveLogDetailFragmentUI(changedDiveLog));
                                }
                            }
                        }
                        break;

                    case REMOVED:
                        DiveLog removedDiveLog = dataSnapshot.getValue(DiveLog.class);
                        if (removedDiveLog != null) {
                            boolean isLastDiveLog = removedDiveLog.getDiveLogUid().equals
                                    (mTheLastDiveLog.getDiveLogUid());
                            if (isLastDiveLog) {
                                if (mDiveLogsArray.getCount() > 0) {
                                    mTheLastDiveLog = mDiveLogsArray
                                            .getItem(mDiveLogsArray.getCount() - 1)
                                            .getValue(DiveLog.class);
                                    mTheLastDiveLog.setNextDiveLogUid(MySettings.NOT_AVAILABLE);

                                } else {
                                    mTheLastDiveLog = null;
                                }
                            }

                            // remove the removed diveLog from the filtered diveLogs
                            int removedDiveLogPosition = getPosition(removedDiveLog.getDiveLogUid());
                            if (removedDiveLogPosition > -1
                                    && removedDiveLogPosition < mFilteredDiveLogs.size()) {
                                mFilteredDiveLogs.remove(removedDiveLogPosition);

                                Timber.i("onChanged() REMOVED %s at position %d", removedDiveLog
                                        .toString(), removedDiveLogPosition);
                                notifyDataSetChanged();
                                if (removedDiveLogPosition >= mFilteredDiveLogs.size()) {
                                    removedDiveLogPosition = mFilteredDiveLogs.size() - 1;
                                }
                                EventBus.getDefault().post(new MyEvents.scrollToPosition
                                        (removedDiveLogPosition));
                            }

                            if (isLastDiveLog) {
                                if (mTheLastDiveLog != null) {
                                    DiveLog.save(mUserUid, mTheLastDiveLog);
                                    notifyDataSetChanged();
                                }
                            } else {
                                DiveLog.sequenceDiveLogs(mUserUid, mActiveDiveLog.getDiveLogUid());
                            }
                        }
                        break;

                    case MOVED:
                        // nothing to do.
                        break;
                }
            }

            @Override
            public void onInitialItemsLoaded() {

                mFilteredDiveLogs.clear();
                mFilteredDiveLogs = retrieveFilteredDiveLogs(mDiveLogsArray);
                notifyDataSetChanged();
                EventBus.getDefault().post(new MyEvents.onInitialDiveLogItemsLoaded());
            }

            private ArrayList<DiveLog> retrieveFilteredDiveLogs(MyFirebaseArray diveLogsArray) {
                ArrayList<DiveLog> filteredDiveLogs = new ArrayList<>();

                if (mUserAppSettings.isSortDiveLogsDescending()) {
                    // Iterate through in reverse order to obtain descending sort
                    for (int i = diveLogsArray.getCount() - 1; i >= 0; i--) {
                        DiveLog diveLog = diveLogsArray.getItem(i).getValue(DiveLog.class);
                        if (mUserAppSettings.includeDiveLog(diveLog)) {
                            filteredDiveLogs.add(diveLog);
                        }
                    }
                } else {
                    // DiveLogs are retrieved from Firebase in ascending sort order
                    for (int i = 0; i < diveLogsArray.getCount(); i++) {
                        DiveLog diveLog = diveLogsArray.getItem(i).getValue(DiveLog.class);
                        if (mUserAppSettings.includeDiveLog(diveLog)) {
                            filteredDiveLogs.add(diveLog);
                        }
                    }
                }

                if (diveLogsArray.getCount() > 0) {
                    mTheLastDiveLog = diveLogsArray.getItem(diveLogsArray.getCount() - 1)
                            .getValue(DiveLog.class);
                }

                Timber.i("retrieveFilteredDiveLogs(): Retrieved %d unfiltered DiveLogs and %d " +
                                "filtered DiveLogs.",
                        diveLogsArray.getCount(), filteredDiveLogs.size());

                return filteredDiveLogs;
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });

    }


    public void setActiveDiveLog(DiveLog activeDiveLog) {
        mActiveDiveLog = activeDiveLog;
    }

    @Override
    public int getCount() {
        return mFilteredDiveLogs.size();
    }

    @Override
    public Fragment getItem(int position) {
        DiveLog diveLog = mFilteredDiveLogs.get(position);
        Timber.d("getItem(): %s at position=%d", diveLog.toString(), position);
        Gson gson = new Gson();
        String diveLogJson = gson.toJson(diveLog);
        return DiveLogDetailFragment.newInstance(mUserUid, diveLogJson);
    }

    @Override
    public int getItemPosition(Object object) {
        Timber.i("getItemPosition()");
        if (mFragmentManager.getFragments().contains(object)) {
            // Causes adapter to reload all Fragments when
            // notifyDataSetChanged is called
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        DiveLog diveLog = mFilteredDiveLogs.get(position);
        return diveLog.toString();
    }

    public int getPosition(String soughtDiveLogUid) {
        if (soughtDiveLogUid == null) {
            return -1;
        }
        return MyMethods.findDiveLogPosition(mFilteredDiveLogs, soughtDiveLogUid);
    }

    public DiveLog getDiveLog(@NonNull String soughtDiveLogUid) {
        DataSnapshot dataSnapshot = mDiveLogsArray.getDataSnapshotForKey(soughtDiveLogUid);
        if (dataSnapshot != null) {
            if (dataSnapshot.getValue() != null) {
                return dataSnapshot.getValue(DiveLog.class);
            }
        }
        return null;
    }

    public DiveLog getDiveLog(int position) {
        DiveLog diveLog = null;
        if (position < mFilteredDiveLogs.size() && position > -1) {
            diveLog = mFilteredDiveLogs.get(position);
        }
        return diveLog;
    }

    public DiveLog getTheLastDiveLog() {
        return mTheLastDiveLog;
    }

    public void destroy() {
        Timber.i("destroy()");
        mTheLastDiveLog = null;
        mFilteredDiveLogs.clear();
        mDiveLogsArray.cleanup();
    }
}



