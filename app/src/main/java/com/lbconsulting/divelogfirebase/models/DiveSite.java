package com.lbconsulting.divelogfirebase.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * A class that holds a DiveLog DiveSite
 */
public class DiveSite {

    public static final String DEFAULT_DIVE_SITE_NAME = "[No Dive Site]";
    public static final String FIELD_DIVE_SITE_NAME = "diveSiteName";

    public static final String DEFAULT_AREA = "[Any Area]";
    public static final String DEFAULT_STATE = "[Any State]";
    public static final String DEFAULT_COUNTRY = "[Any Country]";

    private static final String NODE_DIVE_SITES = "diveSites";
    private static final String NODE_DIVE_LOGS = "diveLogs";
    private static final DatabaseReference dbReference = FirebaseDatabase
            .getInstance().getReference();

    private String diveSiteName;
    private String area;
    private String state;
    private String country;
    private String diveSiteUid;
    private Map<String, Boolean> diveLogs;

    public DiveSite() {
        // Default constructor.
    }

    //<editor-fold desc="Class Constructors">
    public DiveSite(@NonNull String diveSiteName, @NonNull String area,
                    @NonNull String state, @NonNull String country,
                    String diveLogUid) {
        this.diveSiteName = diveSiteName;
        this.area = area;
        this.state = state;
        this.country = country;
        this.diveSiteUid = MySettings.NOT_AVAILABLE;
        this.diveLogs = new HashMap<>();
        if (diveLogUid != null && !diveLogUid.equals(MySettings.NOT_AVAILABLE)) {
            this.diveLogs.put(diveLogUid, true);
        }
    }

    public DiveSite(@NonNull ArrayList<String> diveSiteRecord) {
        this.diveSiteName = diveSiteRecord.get(0);
        this.area = diveSiteRecord.get(1);
        this.state = diveSiteRecord.get(2);
        this.country = diveSiteRecord.get(3);
        this.diveSiteUid = MySettings.NOT_AVAILABLE;
        this.diveLogs = new HashMap<>();
    }
    //</editor-fold> Class Constructors

    //<editor-fold desc="Firebase Helper Methods">
    @Exclude
    public static DatabaseReference nodeUserDiveSites(@NonNull String userUid) {
        return dbReference.child(NODE_DIVE_SITES).child(userUid);
    }

    @Exclude
    private static DatabaseReference nodeUserDiveSite(@NonNull String userUid, @NonNull String
            diveSiteUid) {
        return dbReference.child(NODE_DIVE_SITES).child(userUid).child(diveSiteUid);
    }

    public static DatabaseReference nodeUserDiveSiteDiveLogs(@NonNull String userUid, @NonNull
            String diveSiteUid) {
        return nodeUserDiveSites(userUid).child(diveSiteUid).child(NODE_DIVE_LOGS);
    }

    @Exclude
    public static String save(@NonNull String userUid, @NonNull DiveSite diveSite) {
        if (diveSite.getDiveSiteUid() == null
                || diveSite.getDiveSiteUid().isEmpty()
                || diveSite.getDiveSiteUid().equals(MySettings.NOT_AVAILABLE)) {
            String newDiveLogUid = nodeUserDiveSites(userUid).push().getKey();
            diveSite.setDiveSiteUid(newDiveLogUid);
        }
        nodeUserDiveSites(userUid).child(diveSite.getDiveSiteUid()).setValue(diveSite);
        // update the diveSite's diveLogs
        updateDiveSiteDiveLogsSelectionValues(userUid, diveSite);
        Timber.i("save() DiveSite: \"%s\".", diveSite.getDiveSiteName());

        return diveSite.getDiveSiteUid();
    }

    @Exclude
    public static void remove(String userUid, DiveSite diveSite) {
        nodeUserDiveSites(userUid).child(diveSite.getDiveSiteUid()).removeValue();
    }

    @Exclude
    public static DiveSite getDefaultDiveSite() {
        return new DiveSite(DEFAULT_DIVE_SITE_NAME, DEFAULT_AREA, DEFAULT_STATE, DEFAULT_COUNTRY,
                MySettings.NOT_AVAILABLE);
    }

    @Exclude
    public static boolean okToSaveDiveSite(@NonNull List<DiveSite> diveSites,
                                           @NonNull DiveSite proposedDiveSite) {
        boolean result = true;

        for (DiveSite diveSite : diveSites) {
            if (diveSite.getDiveSiteName() != null) {
                int comparison = diveSite.getDiveSiteName().compareToIgnoreCase(proposedDiveSite
                        .getDiveSiteName());
                if (comparison == 0) {
                    // we've found a dive site with the same name as the proposedDiveSite
                    // check the area, state, and country
                    if (diveSite.getArea().equals(proposedDiveSite.getArea())) {
                        if (diveSite.getState().equals(proposedDiveSite.getState())) {
                            if (diveSite.getCountry().equals(proposedDiveSite.getCountry())) {
                                // The dive sites' name, area, and country are all the same
                                if (!diveSite.getDiveSiteUid().equals(proposedDiveSite
                                        .getDiveSiteUid())) {
                                    // The dive sites Uids are NOT the same ...
                                    // Therefore, the dive site is already in the database
                                    result = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    //</editor-fold> Firebase Helper Methods

    //<editor-fold desc="Getters and Setters">
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDiveSiteName() {
        return diveSiteName;
    }

    @Exclude
    public String getDiveSiteDisplayName() {
        String displayName;
        if (diveSiteName != null) {
            if (diveLogs != null) {
                displayName = String.format(Locale.getDefault(), diveSiteName + " [%d]", diveLogs
                        .size());
            } else {
                displayName = diveSiteName;
            }
        } else {
            if (diveLogs != null) {
                displayName = String.format(Locale.getDefault(), "[%d]", diveLogs.size());
            } else {
                displayName = MySettings.NOT_AVAILABLE;
            }
        }
        return displayName;
    }

    public void setDiveSiteName(String diveSiteName) {
        this.diveSiteName = diveSiteName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDiveSiteUid() {
        return diveSiteUid;
    }

    public void setDiveSiteUid(String diveSiteUid) {
        this.diveSiteUid = diveSiteUid;
    }

    public Map<String, Boolean> getDiveLogs() {
        return diveLogs;
    }

    public void setDiveLogs(Map<String, Boolean> diveLogs) {
        this.diveLogs = diveLogs;
    }

    @Exclude
    public String getLocationDescription() {
        String locationDescription = "";

        if (area != null && !area.equals(DEFAULT_AREA)) {
            locationDescription = getArea();
        }

        if (state != null && !state.equals(DEFAULT_STATE)) {
            if (locationDescription.isEmpty()) {
                locationDescription = getState();
            } else {
                locationDescription = locationDescription + ", " + getState();
            }
        }
        if (country != null && !country.equals(DEFAULT_COUNTRY)) {
            if (locationDescription.isEmpty()) {
                locationDescription = getCountry();
            } else {
                locationDescription = locationDescription + ", " + getCountry();
            }
        }
        return locationDescription;
    }

    @Override
    public String toString() {
        return diveSiteName + ": " + getLocationDescription();
    }
    //</editor-fold> Getters and Setters


    public static void selectDiveSiteForDiveLog(final @NonNull String userUid,
                                                final @NonNull DiveLog activeDiveLog,
                                                final @NonNull DiveSite selectedDiveSite) {
//        Select diveSite for a diveLog:
//        1.	Remove the active diveLog from the previous diveSite's record
//        2.	Add the active diveLog to the isChecked diveSite's record
//        3.	Update the active diveLog with the isChecked diveSite's Uid, name, area, state, and country
//        4.	Save the active diveLog to the database

        removeDiveLogFromDiveSite(userUid, activeDiveLog.getDiveSiteUid(), activeDiveLog
                .getDiveLogUid());
        addDiveLogToDiveSite(userUid, selectedDiveSite.getDiveSiteUid(), activeDiveLog
                .getDiveLogUid());

        activeDiveLog.setDiveSiteUid(selectedDiveSite.getDiveSiteUid());
        activeDiveLog.setDiveSiteName(selectedDiveSite.getDiveSiteName());
        activeDiveLog.setArea(selectedDiveSite.getArea());
        activeDiveLog.setState(selectedDiveSite.getState());
        activeDiveLog.setCountry(selectedDiveSite.getCountry());
        DiveLog.save(userUid, activeDiveLog);
    }

    private static void addDiveLogToDiveSite(@NonNull String userUid, @NonNull String diveSiteUid,
                                             @NonNull String diveLogUid) {
        if (!diveSiteUid.equals(MySettings.NOT_AVAILABLE)) {
            nodeUserDiveSiteDiveLogs(userUid, diveSiteUid).child(diveLogUid).setValue(true);
        }
    }


    public static void removeDiveLogFromDiveSite(@NonNull String userUid, @NonNull String
            diveSiteUid,
                                                 @NonNull String diveLogUid) {
        nodeUserDiveSiteDiveLogs(userUid, diveSiteUid).child(diveLogUid).removeValue();
    }

    public static void updateDiveSitesWithSelectionValues(String userUid, SelectionValue
            selectionValue) {
        if (selectionValue.getDiveSites() != null && selectionValue.getDiveSites().size() > 0) {
            for (String diveSiteUid : selectionValue.getDiveSites().keySet()) {
                updateDiveSiteWithSelectionValue(userUid, diveSiteUid, selectionValue);
            }
        }
    }

    public static void updateDiveSitesWithDefaultSelectionValue(@NonNull String userUid,
                                                                @NonNull SelectionValue
                                                                        selectionValueForDeletion) {

        if (selectionValueForDeletion.getDiveSites() != null && selectionValueForDeletion
                .getDiveSites().size() > 0) {
            SelectionValue defaultSelectionValue = null;
            switch (selectionValueForDeletion.getNodeName()) {
                case SelectionValue.NODE_AREA_VALUES:
                    defaultSelectionValue = SelectionValue.getDefault(SelectionValue
                            .NODE_AREA_VALUES);
                    break;

                case SelectionValue.NODE_STATE_VALUES:
                    defaultSelectionValue = SelectionValue.getDefault(SelectionValue
                            .NODE_STATE_VALUES);
                    break;

                case SelectionValue.NODE_COUNTRY_VALUES:
                    defaultSelectionValue = SelectionValue.getDefault(SelectionValue
                            .NODE_COUNTRY_VALUES);
                    break;
                default:
                    Timber.e("updateDiveSitesWithDefaultSelectionValue(): Unknown SelectionValue " +
                            "node!");
            }

            if (defaultSelectionValue != null) {
                for (String diveSiteUid : selectionValueForDeletion.getDiveSites().keySet()) {
                    updateDiveSiteWithSelectionValue(userUid, diveSiteUid, defaultSelectionValue);
                }
            }
        }
    }

    private static void updateDiveSiteWithSelectionValue(final @NonNull String userUid,
                                                         final @NonNull String diveSiteUid,
                                                         final @NonNull SelectionValue
                                                                 selectionValue) {

        nodeUserDiveSite(userUid, diveSiteUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    DiveSite diveSite = dataSnapshot.getValue(DiveSite.class);
                    if (diveSite != null) {
                        switch (selectionValue.getNodeName()) {
                            case SelectionValue.NODE_AREA_VALUES:
                                diveSite.setArea(selectionValue.getValue());
                                break;
                            case SelectionValue.NODE_STATE_VALUES:
                                diveSite.setState(selectionValue.getValue());
                                break;
                            case SelectionValue.NODE_COUNTRY_VALUES:
                                diveSite.setCountry(selectionValue.getValue());
                                break;
                        }
                        save(userUid, diveSite);
                        updateDiveSiteDiveLogsSelectionValues(userUid, diveSite);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
        nodeUserDiveSite(userUid, diveSiteUid).child(selectionValue.getNodeName())
                .setValue(selectionValue.getValue());
    }

    private static void updateDiveSiteDiveLogsSelectionValues(@NonNull String userUid,
                                                              @NonNull DiveSite diveSite) {

        if (diveSite.getDiveLogs() != null) {
            // update the diveSite's diveLogs
            for (String diveLogUid : diveSite.diveLogs.keySet()) {
                updateDiveSiteDiveLogSelectionValues(userUid, diveLogUid, diveSite);
            }
        }
    }

    private static void updateDiveSiteDiveLogSelectionValues(final String userUid,
                                                             final String diveLogUid,
                                                             final DiveSite diveSite) {
        DiveLog.nodeUserDiveLog(userUid, diveLogUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            DiveLog diveLog = dataSnapshot.getValue(DiveLog.class);
                            if (diveLog != null) {
                                diveLog.setDiveSiteUid(diveSite.getDiveSiteUid());
                                diveLog.setDiveSiteName(diveSite.getDiveSiteName());
                                diveLog.setArea(diveSite.getArea());
                                diveLog.setState(diveSite.getState());
                                diveLog.setCountry(diveSite.getCountry());
                                DiveLog.save(userUid, diveLog);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                    }
                });
    }
//    public static DiveSite findDiveSite(String diveSiteName, List<DiveSite> diveSites,
//                                        String area, String state, String country) {
//        DiveSite foundDiveSite = null;
//
//        for (DiveSite diveSite : diveSites) {
//            if (diveSite.getDiveSiteName().equalsIgnoreCase(diveSiteName)) {
//                if (diveSite.getArea().equals(area)) {
//                    if (diveSite.getState().equals(state)) {
//                        if (diveSite.getCountry().equals(country)) {
//                            foundDiveSite = diveSite;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        return foundDiveSite;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiveSite diveSite = (DiveSite) o;

        return diveSiteUid.equals(diveSite.diveSiteUid);
    }

    @Override
    public int hashCode() {
        return diveSiteUid.hashCode();
    }
}
