package com.lbconsulting.divelogfirebase.ui.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveEquipment;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MySettings;
import com.lbconsulting.divelogfirebase.utils.csvParser;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

/**
 * This class presents DiveEquipment in a RecyclerView.
 */
public class DiveEquipmentFirebaseRecyclerAdapter extends RecyclerView
        .Adapter<DiveEquipmentFirebaseRecyclerAdapter.DiveEquipmentViewHolder> {

    private final DiveEquipment mDiveEquipment;
    private List<String> mSortedEquipmentList;
    private final Context mContext;

    public DiveEquipmentFirebaseRecyclerAdapter(Context context, DiveEquipment diveEquipment) {
        this.mContext = context;
        this.mDiveEquipment = diveEquipment;
        if (diveEquipment != null) {
            this.mSortedEquipmentList = diveEquipment.getEquipmentArray();
        } else {
            this.mSortedEquipmentList = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public DiveEquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_dive_log_equipment, parent, false);

        return new DiveEquipmentViewHolder(rootView,
                new DiveEquipmentViewHolder.EquipmentNameClick() {

                    @Override
                    public void onEquipmentNameClick(View caller) {
                        boolean isChecked = ((CheckBox) caller).isChecked();
                        String equipmentItem = caller.getTag().toString();
                        mDiveEquipment.getDiveEquipmentList().put(equipmentItem, isChecked);
                    }
                },
                new DiveEquipmentViewHolder.EquipmentNameEditClick() {

                    @Override
                    public void onEquipmentNameEditClick(View caller) {
                        String equipmentItem = caller.getTag().toString();
                        EventBus.getDefault().post(new MyEvents.onEquipmentNameEditClick(equipmentItem));
                    }
                });
    }

    @Override
    public void onBindViewHolder(@NonNull final DiveEquipmentViewHolder viewHolder, int position) {

        String equipmentItemName = mSortedEquipmentList.get(position);
        viewHolder.cbEquipmentItem.setText(equipmentItemName);
        boolean value = mDiveEquipment.getDiveEquipmentList().get(equipmentItemName);
        viewHolder.cbEquipmentItem.setChecked(value);
        viewHolder.cbEquipmentItem.setTag(equipmentItemName);
        viewHolder.btnEditEquipmentItem.setTag(equipmentItemName);
    }

    @Override
    public int getItemCount() {
        return mSortedEquipmentList.size();
    }

    public int insertEquipmentItem(String newEquipmentItem) {
        mDiveEquipment.getDiveEquipmentList().put(newEquipmentItem, true);
        mSortedEquipmentList = mDiveEquipment.getEquipmentArray();
        int position = findPosition(newEquipmentItem, mSortedEquipmentList);
        notifyItemInserted(position);
        return position;
    }

    private int findPosition(String equipmentItem, List<String> sortedEquipmentList) {
        return sortedEquipmentList.indexOf(equipmentItem);
    }

    public int updateEquipmentItem(@NonNull String userUid,
                                   @NonNull String originalNewEquipmentItem,
                                   @NonNull String proposedEquipmentItem) {
        mDiveEquipment.getDiveEquipmentList().remove(originalNewEquipmentItem);
        mDiveEquipment.getDiveEquipmentList().put(proposedEquipmentItem, true);
        mSortedEquipmentList = mDiveEquipment.getEquipmentArray();
        int position = findPosition(proposedEquipmentItem, mSortedEquipmentList);
        notifyItemChanged(position);
        new updateEquipmentItemAsync(userUid, originalNewEquipmentItem, proposedEquipmentItem).execute();
        return position;
    }


    //region DiveEquipmentViewHolder
    public static class DiveEquipmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CheckBox cbEquipmentItem;
        private final ImageButton btnEditEquipmentItem;
        private final EquipmentNameClick mOnEquipmentNameClickListener;
        private final EquipmentNameEditClick mOnEquipmentNameEditClickListener;

        public DiveEquipmentViewHolder(View view,
                                       EquipmentNameClick equipmentNameClick,
                                       EquipmentNameEditClick equipmentNameEditClick) {
            super(view);

            mOnEquipmentNameClickListener = equipmentNameClick;
            mOnEquipmentNameEditClickListener = equipmentNameEditClick;
            cbEquipmentItem = view.findViewById(R.id.cbEquipmentItem);
            btnEditEquipmentItem = view.findViewById(R.id.btnEditEquipmentItem);

            cbEquipmentItem.setOnClickListener(this);
            btnEditEquipmentItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cbEquipmentItem:
                    mOnEquipmentNameClickListener.onEquipmentNameClick(view);
                    break;

                case R.id.btnEditEquipmentItem:
                    mOnEquipmentNameEditClickListener.onEquipmentNameEditClick(view);
                    break;
            }
        }

        public interface EquipmentNameClick {
            void onEquipmentNameClick(View caller);
        }

        public interface EquipmentNameEditClick {
            void onEquipmentNameEditClick(View caller);
        }
    }

    //endregion DiveEquipmentViewHolder

    private static class updateEquipmentItemAsync extends AsyncTask<Void, Void, Void> {
        private final String userUid;
        private final String originalNewEquipmentItem;
        private final String proposedEquipmentItem;

        public updateEquipmentItemAsync(
                @NonNull String userUid,
                @NonNull String originalNewEquipmentItem,
                @NonNull String proposedEquipmentItem) {
            this.userUid = userUid;
            this.originalNewEquipmentItem = originalNewEquipmentItem;
            this.proposedEquipmentItem = proposedEquipmentItem;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DiveLog.nodeUserDiveLogs(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DiveLog diveLog = snapshot.getValue(DiveLog.class);
                            if (diveLog != null) {
                                if (diveLog.getEquipmentList() != null
                                        && !diveLog.getEquipmentList().isEmpty()
                                        && !diveLog.getEquipmentList().equals(MySettings.NOT_AVAILABLE)
                                        && diveLog.getEquipmentList().contains(originalNewEquipmentItem)) {

                                    ArrayList<ArrayList<String>> equipmentListRecords = csvParser
                                            .CreateRecordAndFieldLists(diveLog.getEquipmentList());
                                    if (equipmentListRecords.size() > 0) {
                                        ArrayList<String> equipmentList = equipmentListRecords.get(0);
                                        if (equipmentList.contains(originalNewEquipmentItem)) {
                                            equipmentList.remove(originalNewEquipmentItem);
                                        }
                                        equipmentList.add(proposedEquipmentItem);
                                        if (equipmentList.size() > 1) {
                                            Collections.sort(equipmentList, new Comparator<String>() {
                                                @Override
                                                public int compare(String s1, String s2) {
                                                    return s1.compareToIgnoreCase(s2);
                                                }
                                            });
                                        }
                                        String equipmentListString = csvParser.toCSVString(equipmentList);
                                        DiveLog.saveEquipmentList(userUid, diveLog.getDiveLogUid(), equipmentListString);
                                    }

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                }
            });

            return null;
        }
    }
}
