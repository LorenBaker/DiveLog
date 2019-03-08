package com.lbconsulting.divelogfirebase.models;

import android.support.annotation.NonNull;

import com.lbconsulting.divelogfirebase.utils.MySettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class holds an array of bullet marine notes
 */

public class MarineNote {

    private static final Comparator<MarineNote> mAscendingSortKey = new Comparator<MarineNote>() {
        public int compare(MarineNote marineNote1, MarineNote marineNote2) {
            Long sortKey1 = marineNote1.getSortKey();
            Long sortKey2 = marineNote2.getSortKey();
            return sortKey1.compareTo(sortKey2);
        }
    };

    public static final String FIELD_SORT_KEY = "sortKey";

    private String note;
    private String userUid;
    private String diveLogUid;
    private String marineNoteUid;
    private long sortKey;

    public MarineNote() {
    }

    public MarineNote(@NonNull String userUid,
                      @NonNull String diveLogUid,
                      @NonNull String note) {
        this.userUid = userUid;
        this.diveLogUid = diveLogUid;
        this.note = note;
        this.marineNoteUid = MySettings.NOT_AVAILABLE;
        this.sortKey = System.currentTimeMillis();
    }

    //<editor-fold desc="Getters and Setters">
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDiveLogUid() {
        return diveLogUid;
    }

    public void setDiveLogUid(String diveLogUid) {
        this.diveLogUid = diveLogUid;
    }

    public String getMarineNoteUid() {
        return marineNoteUid;
    }

    public void setMarineNoteUid(String marineNoteUid) {
        this.marineNoteUid = marineNoteUid;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    @Override
    public String toString() {
        return this.note;
    }
    //</editor-fold> Getters and Setters

    public static String save(@NonNull String userUid,
                              @NonNull String diveLogUid,
                              @NonNull MarineNote marineNote) {
        if (marineNote.getMarineNoteUid() == null
                || marineNote.getMarineNoteUid().isEmpty()
                || marineNote.getMarineNoteUid().equals(MySettings.NOT_AVAILABLE)) {
            String newDiveLogUid = DiveLog.nodeUserMarineNotes(userUid, diveLogUid).push().getKey();
            marineNote.setMarineNoteUid(newDiveLogUid);
        }
        DiveLog.nodeUserMarineNotes(userUid, diveLogUid).child(marineNote
                .getMarineNoteUid()).setValue(marineNote);

        return marineNote.getMarineNoteUid();
    }

    public static void remove(@NonNull String userUid,
                              @NonNull String diveLogUid,
                              @NonNull MarineNote marineNote) {
        DiveLog.nodeUserMarineNotes(userUid, diveLogUid).child(marineNote
                .getMarineNoteUid()).removeValue();
    }

    public static ArrayList<MarineNote> sort(ArrayList<MarineNote> marineNotes) {
        Collections.sort(marineNotes, mAscendingSortKey);
        return marineNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarineNote that = (MarineNote) o;

        return marineNoteUid.equals(that.marineNoteUid);

    }

    @Override
    public int hashCode() {
        return marineNoteUid.hashCode();
    }
}
