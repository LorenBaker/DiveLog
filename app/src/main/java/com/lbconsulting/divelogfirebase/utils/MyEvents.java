package com.lbconsulting.divelogfirebase.utils;

import android.support.annotation.NonNull;

import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.MarineNote;
import com.lbconsulting.divelogfirebase.models.Person;
import com.lbconsulting.divelogfirebase.models.ReefGuideItem;
import com.lbconsulting.divelogfirebase.models.SelectionValue;

/**
 * EventBus events.
 */
public class MyEvents {

    public static class showProgressBar {
        private String mMessage;

        public showProgressBar(@NonNull String message) {
            this.mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class hideProgressBar {
        public hideProgressBar(boolean showDiveLogsList) {
        }
    }

    public static class addNewEquipmentItem {
        private String mNewEquipmentItem;

        public addNewEquipmentItem(String newEquipmentItem) {
            mNewEquipmentItem = newEquipmentItem;
        }

        public String getNewEquipmentItem() {
            return mNewEquipmentItem;
        }
    }

    public static class updateSelectionValue {
        private SelectionValue mSelectionValue;

        public updateSelectionValue(SelectionValue selectionValue) {
            mSelectionValue = selectionValue;
        }

        public SelectionValue getSelectionValue() {
            return mSelectionValue;
        }

    }

    public static class updateActiveDiveLogNumericValues {
        private String mDiveLogUid;
        private int mButtonID;
        private Double mSelectedValue;

        public updateActiveDiveLogNumericValues(@NonNull String diveLogUid, int buttonID, Double
                selectedValue) {
            mDiveLogUid = diveLogUid;
            mButtonID = buttonID;
            mSelectedValue = selectedValue;
        }

        public int getButtonID() {
            return mButtonID;
        }

        public String getDiveLogUid() {
            return mDiveLogUid;
        }

        public Double getSelectedValue() {
            return mSelectedValue;
        }
    }

    public static class setTimeZone {
        private String mDiveLogUid;
        private String mTimeZoneID;


        public setTimeZone(@NonNull String diveLogUid, @NonNull String timeZoneID) {
            mDiveLogUid = diveLogUid;
            mTimeZoneID = timeZoneID;
        }

        public String getDiveLogUid() {
            return mDiveLogUid;
        }

        public String getTimeZoneID() {
            return mTimeZoneID;
        }


    }

    public static class populateDiveLogDetailFragmentUI {
        private DiveLog mDiveLog;

        public populateDiveLogDetailFragmentUI(DiveLog diveLog) {
            mDiveLog = diveLog;
        }

        public DiveLog getDiveLog() {
            return mDiveLog;
        }

    }

    public static class saveDiveRating {
        private DiveLog mDiveLog;

        public saveDiveRating(DiveLog diveLog) {
            mDiveLog = diveLog;
        }

        public DiveLog getDiveLog() {
            return mDiveLog;
        }

    }

    public static class updateDiveDate {
        private String mDiveLogUid;
        private long mStartDate;

        public updateDiveDate(@NonNull String diveLogUid, long startDate) {
            mDiveLogUid = diveLogUid;
            mStartDate = startDate;
        }

        public String getDiveLogUid() {
            return mDiveLogUid;
        }

        public long getDiveStart() {
            return mStartDate;
        }
    }

    public static class updateDiveStart {
        private String mDiveLogUid;
        private long mStartDate;

        public updateDiveStart(@NonNull String diveLogUid, long startDate) {
            mDiveLogUid = diveLogUid;
            mStartDate = startDate;
        }

        public String getDiveLogUid() {
            return mDiveLogUid;
        }

        public long getDiveStart() {
            return mStartDate;
        }
    }

    public static class updateDiveEnd {
        private String mDiveLogUid;
        private long mEndDate;

        public updateDiveEnd(@NonNull String diveLogUid, long endDate) {
            mDiveLogUid = diveLogUid;
            mEndDate = endDate;
        }

        public String getDiveLogUid() {
            return mDiveLogUid;
        }

        public long getDiveEnd() {
            return mEndDate;
        }

    }

    public static class onInitialDiveLogItemsLoaded {
        public onInitialDiveLogItemsLoaded() {
        }
    }

    public static class startDiveLogPagerActivity {
        private DiveLog mDiveLog;

        public startDiveLogPagerActivity(DiveLog diveLog) {
            this.mDiveLog = diveLog;
        }

        public DiveLog getDiveLog() {
            return mDiveLog;
        }
    }

    public static class resetViewPagerAdapter {
        private String mReturningDiveLogUid;

        public resetViewPagerAdapter(String returningDiveLogUid) {
            mReturningDiveLogUid = returningDiveLogUid;
        }

        public String getReturningDiveLogUid() {
            return mReturningDiveLogUid;
        }
    }

    public static class destroyDiveLogsAdapter {
        public destroyDiveLogsAdapter() {

        }
    }

    public static class scrollToPosition {
        private int mPosition;

        public scrollToPosition(int position) {
            this.mPosition = position;
        }

        public int getPosition() {
            return mPosition;
        }
    }

    public static class setDiveLogsSequenced {
        private boolean mValue;

        public setDiveLogsSequenced(boolean value) {
            this.mValue = value;
        }

        public boolean getValue() {
            return mValue;
        }
    }

    public static class foundDiveSite {
        private String mDiveSiteUid;

        public foundDiveSite(String diveSiteUid) {
            this.mDiveSiteUid = diveSiteUid;
        }

        public String getDiveSiteUid() {
            return mDiveSiteUid;
        }
    }

    public static class removeUserAppSettingListener {
        public removeUserAppSettingListener() {

        }
    }

    public static class addReefGuideItemToDiveLog {
        private ReefGuideItem reefGuideItem;

        public addReefGuideItemToDiveLog(ReefGuideItem reefGuideItem) {
            this.reefGuideItem = reefGuideItem;
        }

        public ReefGuideItem getReefGuideItem() {
            return reefGuideItem;
        }
    }

    public static class removeReefGuideItemFromDiveLog {
        private ReefGuideItem reefGuideItem;

        public removeReefGuideItemFromDiveLog(ReefGuideItem reefGuideItem) {
            this.reefGuideItem = reefGuideItem;
        }

        public ReefGuideItem getReefGuideItem() {
            return reefGuideItem;
        }
    }

    public static class showWebsiteDetail {
        private ReefGuideItem reefGuideItem;

        public showWebsiteDetail(ReefGuideItem reefGuideItem) {
            this.reefGuideItem = reefGuideItem;
        }

        public ReefGuideItem getReefGuideItem() {
            return reefGuideItem;
        }
    }

    public static class personSelected {
        private int buttonId;
        private Person selectedPerson;

        public personSelected(int buttonId, Person selectedPerson) {
            this.buttonId = buttonId;
            this.selectedPerson = selectedPerson;
        }

        public int getButtonId() {
            return buttonId;
        }

        public Person getSelectedPerson() {
            return selectedPerson;
        }
    }

    public static class newPersonSelected {
        private int buttonId;
        private Person selectedPerson;
        private boolean isNewPerson;

        public newPersonSelected(int buttonId, Person selectedPerson, boolean isNewPerson) {
            this.buttonId = buttonId;
            this.selectedPerson = selectedPerson;
            this.isNewPerson = isNewPerson;
        }

        public int getButtonId() {
            return buttonId;
        }

        public Person getSelectedPerson() {
            return selectedPerson;
        }

        public boolean isNewPerson() {
            return isNewPerson;
        }
    }

    public static class dismissDialog {
        public dismissDialog() {
        }
    }

    public static class addMarineNote {
        private MarineNote marineNote;

        public addMarineNote(MarineNote marineNote) {
            this.marineNote = marineNote;
        }

        public MarineNote getMarineNote() {
            return marineNote;
        }
    }

    public static class removeMarineNote {
        private MarineNote marineNote;

        public removeMarineNote(MarineNote marineNote) {
            this.marineNote = marineNote;
        }

        public MarineNote getMarineNote() {
            return marineNote;
        }
    }

    public static class updateMarineNote {
        private MarineNote marineNote;

        public updateMarineNote(MarineNote marineNote) {
            this.marineNote = marineNote;
        }

        public MarineNote getMarineNote() {
            return marineNote;
        }
    }

    public static class showMarineNoteEditNewDialog {
        private MarineNote marineNote;

        public showMarineNoteEditNewDialog(MarineNote marineNote) {
            this.marineNote = marineNote;
        }

        public MarineNote getMarineNote() {
            return marineNote;
        }
    }

    public static class updateEquipmentItem {
        private String originalNewEquipmentItem;
        private String proposedEquipmentItem;

        public updateEquipmentItem(String originalNewEquipmentItem, String proposedEquipmentItem) {
            this.originalNewEquipmentItem = originalNewEquipmentItem;
            this.proposedEquipmentItem = proposedEquipmentItem;
        }

        public String getOriginalNewEquipmentItem() {
            return originalNewEquipmentItem;
        }

        public String getProposedEquipmentItem() {
            return proposedEquipmentItem;
        }
    }

    public static class onEquipmentNameEditClick {
        private String equipmentItem;

        public onEquipmentNameEditClick(String equipmentItem) {
            this.equipmentItem = equipmentItem;
        }

        public String getEquipmentItem() {
            return equipmentItem;
        }
    }
}

