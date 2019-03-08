package com.lbconsulting.divelogfirebase.ui.adapters;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.AppMetrics;
import com.lbconsulting.divelogfirebase.models.AppSettings;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.DiveSite;
import com.lbconsulting.divelogfirebase.ui.adapters.firebase.MyFirebaseArray;
import com.lbconsulting.divelogfirebase.ui.adapters.firebase.MyFirebaseRecyclerAdapter;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Loren on 11/29/2016.
 * This adapter shows DiveLog summaries in a RecyclerView
 */
public class DiveLogsListFirebaseRecyclerAdapter extends
                                                 MyFirebaseRecyclerAdapter<DiveLogsListFirebaseRecyclerAdapter.ViewHolder, DiveLog> {

    private final String mUserUid;
    private AppSettings mUserAppSettings;
    private MyFirebaseArray mDiveSitesArray;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final LinearLayout llDiveLogRow;
        private final TextView tvDiveNumber;
        private final TextView tvDiveSiteName;
        private final TextView tvDate;
        private final TextView tvLocationDescription;

        private final DiveLogRowOnClick mDiveLogRowOnClickListener;


        public ViewHolder(View view,
                          DiveLogRowOnClick diveLogRowOnClick) {
            super(view);

            mDiveLogRowOnClickListener = diveLogRowOnClick;

            llDiveLogRow = (LinearLayout) view.findViewById(R.id.llDiveLogRow);
            tvDiveNumber = (TextView) view.findViewById(R.id.tvDiveNumber);
            tvDiveSiteName = (TextView) view.findViewById(R.id.tvDiveSiteName);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvLocationDescription = (TextView) view.findViewById(R.id.tvLocationDescription);

            llDiveLogRow.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.llDiveLogRow:
                    mDiveLogRowOnClickListener.onDiveLogRowClick(view);
                    break;
            }
        }

        public interface DiveLogRowOnClick {
            void onDiveLogRowClick(View caller);
        }
    }

    public DiveLogsListFirebaseRecyclerAdapter(@NonNull String userUid,
                                               @NonNull Query query,
                                               @NonNull Class<DiveLog> itemClass,
                                               @NonNull AppSettings userAppSettings) {
        super(query, itemClass, AppMetrics.nodeDiveLogArraySize(userUid), userAppSettings
                .isSortDiveLogsDescending());
        this.mUserUid = userUid;
        this.mUserAppSettings = userAppSettings;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_dive_log, parent, false);

        return new ViewHolder(view,
                              new ViewHolder.DiveLogRowOnClick() {
                                  @Override
                                  public void onDiveLogRowClick(View caller) {
                                      // DiveLog row clicked
                                      DiveLog diveLog = (DiveLog) caller.getTag();
                                      EventBus.getDefault().post(new MyEvents
                                              .startDiveLogPagerActivity(diveLog));
                                  }
                              }
        );

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        DiveLog diveLog = getItem(position);
        viewHolder.llDiveLogRow.setTag(diveLog);

        viewHolder.tvDiveNumber.setText(String.valueOf(diveLog.getDiveNumber()));
        viewHolder.tvDate.setText(diveLog.getDiveStartDayDateTime());
        viewHolder.tvLocationDescription.setText(diveLog.getLocationDescription());

        if (diveLog.getDiveSiteUid() == null
                || diveLog.getDiveSiteUid().equals(MySettings.NOT_AVAILABLE)
                || diveLog.getDiveSiteUid().isEmpty()) {
            viewHolder.tvDiveSiteName.setText(DiveSite.DEFAULT_DIVE_SITE_NAME);

        } else {
            viewHolder.tvDiveSiteName.setText(diveLog.getDiveSiteName());
            String[] params = {diveLog.getDiveSiteName(), diveLog.getDiveSiteUid()};
            new retrieveDiveSiteDisplayName(viewHolder.tvDiveSiteName).execute(params);
        }
    }

    @Override
    protected boolean okToAddItem(DiveLog item) {
        return mUserAppSettings.includeDiveLog(item);
    }

    @Override
    protected void onInitialItemsLoaded() {
        Timber.i("onInitialItemsLoaded(). Loaded %d filtered DiveLogs.", getItemCount());

        // All diveLogs loaded ... now retrieve diveSites
        retrieveDiveSites();
    }

    private void retrieveDiveSites() {
        Query diveSitesQuery = DiveSite.nodeUserDiveSites(mUserUid).orderByChild(DiveSite.FIELD_DIVE_SITE_NAME);
        mDiveSitesArray = new MyFirebaseArray(diveSitesQuery, AppMetrics.nodeDiveSiteArraySize
                (mUserUid));
        mDiveSitesArray.setOnChangedListener(new MyFirebaseArray.OnChangedListener() {

            @Override
            public void onChanged(EventType type, DataSnapshot dataSnapshot, int index, int
                    oldIndex) {

            }

            @Override
            public void onInitialItemsLoaded() {
                EventBus.getDefault().post(new MyEvents.onInitialDiveLogItemsLoaded());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }

    @Override
    protected void itemAdded(DiveLog item, String key, int position) {
        Timber.i("itemAdded(): %s at position %d", item.toString(), position);
    }

    @Override
    protected void itemChanged(DiveLog oldItem, DiveLog newItem, String key, int newPosition) {
        Timber.i("itemChanged(): %s at position %d", newItem.toString(), newPosition);
    }

    @Override
    protected void itemRemoved(DiveLog item, String key, int position) {
        Timber.i("itemRemoved(): %s at position %d", item.toString(), position);
    }

    @Override
    protected void itemMoved(DiveLog item, String key, int oldPosition, int newPosition) {
        Timber.i("itemMoved(): %s at position %d move to position %d",
                 item.toString(), oldPosition, newPosition);
    }

    public int findPosition(String soughtDiveLogUid) {
        ArrayList<DiveLog> diveLogs = getItems();
        int position = 0;
        boolean found = false;
        for (DiveLog diveLog : diveLogs) {
            if (diveLog.getDiveLogUid().equals(soughtDiveLogUid)) {
                found = true;
                break;
            } else {
                position++;
            }
        }

        if (!found) {
            position = -1;
        }

        return position;
    }

    public void setUserAppSettings(AppSettings userAppSettings) {
        mUserAppSettings = userAppSettings;
    }

    /**
     * Clean the adapter.
     * ALWAYS call this method before destroying the adapter to remove the listener.
     */
    @Override
    public void destroy() {
        super.destroy();
        if (mDiveSitesArray != null) {
            mDiveSitesArray.cleanup();
        }
    }

    private class retrieveDiveSiteDisplayName extends AsyncTask<String, Void, String> {
        private final TextView textView;

        public retrieveDiveSiteDisplayName(TextView textView) {
            this.textView = textView;
        }


        @Override
        protected String doInBackground(String... strings) {
            String diveSiteDisplayName = strings[0];
            String diveSiteUid = strings[1];
            try {
                int index = mDiveSitesArray.getIndexForKey(diveSiteUid);
                DataSnapshot dataSnapshot = mDiveSitesArray.getItem(index);
                if (dataSnapshot.getValue() != null) {
                    DiveSite diveSite = dataSnapshot.getValue(DiveSite.class);
                    if (diveSite != null) {
                        diveSiteDisplayName = diveSite.getDiveSiteDisplayName();
                    }
                }
            } catch (Exception e) {
                Timber.e("retrieveDiveSiteDisplayName: doInBackground(): Exception: %s.", e
                        .getMessage());
            }
            return diveSiteDisplayName;
        }

        @Override
        protected void onPostExecute(String diveSiteDisplayName) {
            textView.setText(diveSiteDisplayName);
        }
    }
}
