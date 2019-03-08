package com.lbconsulting.divelogfirebase.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.MarineNote;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A Firebase adapter for viewing MarineNotes in a RecyclerView
 */

public class MarineNotesRecyclerAdapter extends RecyclerView.Adapter<MarineNotesRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView tvMarineNote;

        private final MarineNoteRowOnClick mMarineNoteRowOnClickListener;

        public ViewHolder(View view,
                          MarineNoteRowOnClick marineNoteRowOnClick) {
            super(view);

            mMarineNoteRowOnClickListener = marineNoteRowOnClick;

            tvMarineNote = (TextView) view.findViewById(R.id.tvMarineNote);
            tvMarineNote.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvMarineNote:
                    mMarineNoteRowOnClickListener.onMarineNoteRowClick(view);
                    break;
            }
        }

        public interface MarineNoteRowOnClick {
            void onMarineNoteRowClick(View caller);
        }
    }

    private final String mUserUid;
    private final String mDiveLogUid;
    private final ArrayList<MarineNote> mMarineNotes;

    public MarineNotesRecyclerAdapter(@NonNull String userUid,
                                      @NonNull String diveLogUid,
                                      @NonNull ArrayList<MarineNote> marineNotes) {
        this.mUserUid = userUid;
        this.mDiveLogUid = diveLogUid;
        this.mMarineNotes = marineNotes;
    }

    @Override
    public MarineNotesRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_marine_note, parent, false);

        return new ViewHolder(view,
                new ViewHolder.MarineNoteRowOnClick() {
                    @Override
                    public void onMarineNoteRowClick(View caller) {
                        MarineNote note = (MarineNote) caller.getTag();
                        if (note != null) {
                            EventBus.getDefault().post(new MyEvents.showMarineNoteEditNewDialog(note));
                        }
                    }
                }
        );
    }

    @Override
    public void onBindViewHolder(MarineNotesRecyclerAdapter.ViewHolder holder, int position) {
        MarineNote item = mMarineNotes.get(position);
        holder.tvMarineNote.setText(item.getNote());
        holder.tvMarineNote.setTag(item);
    }

    @Override
    public int getItemCount() {
        return mMarineNotes.size();
    }

    public void add(MarineNote marineNote) {
        if (marineNote.getMarineNoteUid().equals(MySettings.NOT_AVAILABLE)) {
            String uid = DiveLog.nodeUserMarineNotes(mUserUid, mDiveLogUid).push().getKey();
            marineNote.setMarineNoteUid(uid);
        }
        int position = mMarineNotes.size();
        mMarineNotes.add(position, marineNote);
        notifyItemInserted(position);
    }

    public void update(MarineNote marineNote) {
        int position = mMarineNotes.indexOf(marineNote);
        if (position > -1 && position < mMarineNotes.size()) {
            mMarineNotes.set(position, marineNote);
            notifyItemChanged(position);
        }
    }

    public void remove(MarineNote marineNote) {
        int position = mMarineNotes.indexOf(marineNote);
        if (position > -1 && position < mMarineNotes.size()) {
            remove(position);
        }
    }

    public void remove(int position) {
        mMarineNotes.remove(position);
        notifyItemRemoved(position);
    }

    public void swap(int firstPosition, int secondPosition) {
        MarineNote noteFirstPosition = mMarineNotes.get(firstPosition);
        MarineNote noteSecondPosition = mMarineNotes.get(secondPosition);
        long firstPositionSortKey = noteFirstPosition.getSortKey();
        long secondPositionSortKey = noteSecondPosition.getSortKey();
        noteFirstPosition.setSortKey(secondPositionSortKey);
        noteSecondPosition.setSortKey(firstPositionSortKey);

        Collections.swap(mMarineNotes, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }
} 