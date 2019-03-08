package com.lbconsulting.divelogfirebase.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * This Class holds a SelectionValue.
 */
public class SelectionValue {

    private static final String NODE_INITIAL_SELECTION_VALUES = "initialSelectionValues";
    public static final String KEY_AREA_INITIAL_VALUES = "areaInitialValues";
    public static final String KEY_STATE_INITIAL_VALUES = "stateInitialValues";
    public static final String KEY_COUNTRY_INITIAL_VALUES = "countryInitialValues";
    public static final String KEY_CURRENT_INITIAL_VALUES = "currentConditionInitialValues";
    public static final String KEY_DIVE_ENTRY_INITIAL_VALUES = "diveEntryInitialValues";
    public static final String KEY_DIVE_DIVE_EQUIPMENT_INITIAL_VALUES =
            "diveEquipmentInitialValues";
    public static final String KEY_DIVE_STYLE_INITIAL_VALUES = "diveStyleInitialValues";
    public static final String KEY_DIVE_TANK_INITIAL_VALUES = "diveTankInitialValues";
    public static final String KEY_DIVE_TYPE_INITIAL_VALUES = "diveTypeInitialValues";
    public static final String KEY_SEA_CONDITION_INITIAL_VALUES = "seaConditionInitialValues";
    public static final String KEY_WEATHER_CONDITION_INITIAL_VALUES =
            "weatherConditionInitialValues";

    public static final String NODE_AREA_VALUES = "areaValues";
    public static final String NODE_STATE_VALUES = "stateValues";
    public static final String NODE_COUNTRY_VALUES = "countryValues";
    public static final String NODE_CURRENT_VALUES = "currentConditionValues";
    public static final String NODE_DIVE_ENTRY_VALUES = "diveEntryValues";
    public static final String NODE_DIVE_STYLE_VALUES = "diveStyleValues";
    public static final String NODE_DIVE_TANK_VALUES = "diveTankValues";
    public static final String NODE_DIVE_TYPE_VALUES = "diveTypeValues";
    public static final String NODE_SEA_CONDITION_VALUES = "seaConditionValues";
    public static final String NODE_WEATHER_CONDITION_VALUES = "weatherConditionValues";

    public static final String DEFAULT_AREA = "[Any Area]";
    public static final String DEFAULT_STATE = "[Any State]";
    public static final String DEFAULT_COUNTRY = "[Any Country]";

    public static final String DEFAULT_CURRENT = "[None]";
    public static final String DEFAULT_DIVE_ENTRY = "[None]";
    public static final String DEFAULT_DIVE_STYLE = "[None]";
    public static final String DEFAULT_DIVE_TANK = "[None]";
    public static final String DEFAULT_DIVE_TYPE = "[None]";
    public static final String DEFAULT_SEA_CONDITION = "[None]";
    public static final String DEFAULT_WEATHER_CONDITION = "[None]";

    public static final String FIELD_VALUE = "value";

    private static final String NODE_DIVE_LOGS = "diveLogs";
    private static final String NODE_DIVE_SITES = "diveSites";

    private static final DatabaseReference dbReference = FirebaseDatabase.getInstance()
            .getReference();
    private static final String NODE_SELECTION_VALUES = "selectionValues";

    private String value;
    private String nodeName;
    private Map<String, Boolean> diveLogs;
    private Map<String, Boolean> diveSites;

    public SelectionValue() {
        // Default Constructor
    }

    public SelectionValue(@NonNull String value, @NonNull String nodeName,
                          @Nullable String diveLogUid, @Nullable String diveSiteUid) {
        this.value = value;
        this.nodeName = nodeName;
        this.diveLogs = new HashMap<>();
        if (diveLogUid != null && !diveLogUid.equals(MySettings.NOT_AVAILABLE)) {
            this.diveLogs.put(diveLogUid, true);
        }
        this.diveSites = new HashMap<>();
        if (diveSiteUid != null && !diveSiteUid.equals(MySettings.NOT_AVAILABLE)) {
            this.diveSites.put(diveSiteUid, true);
        }
    }

    public SelectionValue(@NonNull String value, @NonNull String nodeName) {
        this.value = value;
        this.nodeName = nodeName;
        this.diveLogs = new HashMap<>();
        this.diveSites = new HashMap<>();
    }

    //region Getters and Setters
    public Map<String, Boolean> getDiveLogs() {
        return diveLogs;
    }

    public void setDiveLogs(Map<String, Boolean> diveLogs) {
        this.diveLogs = diveLogs;
    }

    public Map<String, Boolean> getDiveSites() {
        return diveSites;
    }

    public void setDiveSites(Map<String, Boolean> diveSites) {
        this.diveSites = diveSites;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    //endregion  Getters and Setters

    public static DatabaseReference nodeInitialSelectionValues() {
        return dbReference.child(NODE_INITIAL_SELECTION_VALUES);
    }

    public static DatabaseReference nodeSelectionValues(@NonNull String userUid, @NonNull String
            nodeName) {
        return dbReference.child(NODE_SELECTION_VALUES).child(userUid).child(nodeName);
    }


    @Exclude
    public static SelectionValue getDefault(@NonNull String nodeName) {
        switch (nodeName) {
            case NODE_AREA_VALUES:
                return new SelectionValue(DEFAULT_AREA, NODE_AREA_VALUES, null, null);
            case NODE_STATE_VALUES:
                return new SelectionValue(DEFAULT_STATE, NODE_STATE_VALUES, null, null);
            case NODE_COUNTRY_VALUES:
                return new SelectionValue(DEFAULT_COUNTRY, NODE_COUNTRY_VALUES, null, null);
            case NODE_CURRENT_VALUES:
                return new SelectionValue(DEFAULT_CURRENT, NODE_CURRENT_VALUES, null, null);
            case NODE_DIVE_ENTRY_VALUES:
                return new SelectionValue(DEFAULT_DIVE_ENTRY, NODE_DIVE_ENTRY_VALUES, null, null);
            case NODE_DIVE_STYLE_VALUES:
                return new SelectionValue(DEFAULT_DIVE_STYLE, NODE_DIVE_STYLE_VALUES, null, null);
            case NODE_DIVE_TANK_VALUES:
                return new SelectionValue(DEFAULT_DIVE_TANK, NODE_DIVE_TANK_VALUES, null, null);
            case NODE_DIVE_TYPE_VALUES:
                return new SelectionValue(DEFAULT_DIVE_TYPE, NODE_DIVE_TYPE_VALUES, null, null);
            case NODE_SEA_CONDITION_VALUES:
                return new SelectionValue(DEFAULT_SEA_CONDITION, NODE_SEA_CONDITION_VALUES, null,
                                          null);
            case NODE_WEATHER_CONDITION_VALUES:
                return new SelectionValue(DEFAULT_WEATHER_CONDITION,
                                          NODE_WEATHER_CONDITION_VALUES, null, null);
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return value;
    }

    private static DatabaseReference nodeUserSelectionValues(@NonNull String userUid, @NonNull
            String nodeName) {
        return dbReference.child(NODE_SELECTION_VALUES).child(userUid).child(nodeName);
    }

    public static DatabaseReference nodeUserSelectionValue(@NonNull String userUid,
                                                           @NonNull String nodeName,
                                                           @NonNull String selectedValueUid) {
        return nodeUserSelectionValues(userUid, nodeName).child(selectedValueUid);
    }

    private static DatabaseReference nodeUserSelectedValueDiveLogs(@NonNull String userUid,
                                                                   @NonNull String nodeName,
                                                                   @NonNull String
                                                                           selectionValueKey) {
        return nodeUserSelectionValues(userUid, nodeName).child(selectionValueKey)
                .child(NODE_DIVE_LOGS);
    }

    private static DatabaseReference nodeUserSelectedValueDiveSites(@NonNull String userUid,
                                                                    @NonNull String nodeName,
                                                                    @NonNull String
                                                                            selectionValueKey) {
        return nodeUserSelectionValues(userUid, nodeName).child(selectionValueKey).child
                (NODE_DIVE_SITES);
    }

    public static void save(@NonNull String userUid, @NonNull SelectionValue selectionValue) {
        if (selectionValue.getValue() != null
                && !selectionValue.getValue().equals(MySettings.NOT_AVAILABLE)
                && !MyMethods.containsInvalidCharacters(selectionValue.getValue())) {
            nodeSelectionValues(userUid, selectionValue.getNodeName())
                    .child(selectionValue.getValue()).setValue(selectionValue);
            Timber.i("save() DiveSite: \"%s\".", selectionValue.getValue());
        }
    }


    public static void remove(@NonNull String userUid,
                              @NonNull SelectionValue selectionValue) {
        nodeSelectionValues(userUid, selectionValue.getNodeName())
                .child(selectionValue.getValue()).removeValue();
    }

    public static boolean okToSaveSelectionValue(@NonNull List<SelectionValue> selectionValues,
                                                 @NonNull SelectionValue proposedSelectionValue) {
        boolean result = true;
        if (selectionValues.size() > 0) {
            for (SelectionValue selectionValue : selectionValues) {
                if (selectionValue.getValue() != null) {
                    int comparison = selectionValue.getValue().compareToIgnoreCase
                            (proposedSelectionValue.getValue());
                    if (comparison == 0) {
                        // we've found a selectionValue with the same value as the
                        // proposedSelectionValue
                        result = false;
                    }
                }
            }
        }

        return result;
    }

    public static void addDiveLogToSelectionValue(@NonNull String userUid,
                                                  @NonNull String selectionValueField,
                                                  @NonNull String selectedValueKey,
                                                  @NonNull String diveLogUid) {
        if (!selectedValueKey.equals(MySettings.NOT_AVAILABLE)
                && !MyMethods.containsInvalidCharacters(selectedValueKey)) {
            nodeUserSelectedValueDiveLogs(userUid, selectionValueField, selectedValueKey)
                    .child(diveLogUid).setValue(true);
        }
    }

    public static void removeDiveLogFromSelectionValues(@NonNull final String userUid,
                                                        @NonNull final String selectionValueField,
                                                        @NonNull final String diveLogUid) {
        nodeUserSelectionValues(userUid, selectionValueField)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                SelectionValue selectionValue = snapshot
                                        .getValue(SelectionValue.class);
                                if (selectionValue != null
                                        && selectionValue.getValue() != null
                                        && selectionValue.getDiveLogs() != null) {
                                    if (selectionValue.getDiveLogs().containsKey(diveLogUid)) {
                                        removeDiveLogFromSelectionValue(userUid,
                                                                        selectionValueField,
                                                                        selectionValue.getValue(),
                                                                        diveLogUid);
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

    }

    public static void removeDiveLogFromSelectionValue(@NonNull String userUid,
                                                       @NonNull String selectionValueField,
                                                       @NonNull String selectedValueKey,
                                                       @NonNull String diveLogUid) {
        nodeUserSelectedValueDiveLogs(userUid, selectionValueField, selectedValueKey)
                .child(diveLogUid).removeValue();
    }

    public static void addDiveSiteToSelectionValue(@NonNull String userUid,
                                                   @NonNull String selectionValueField,
                                                   @NonNull String selectionValueKey,
                                                   @NonNull String diveSiteUid) {
        if (!MyMethods.containsInvalidCharacters(selectionValueKey)
                && !selectionValueKey.equals(MySettings.NOT_AVAILABLE)) {
            nodeUserSelectedValueDiveSites(userUid, selectionValueField, selectionValueKey)
                    .child(diveSiteUid).setValue(true);
        }
    }

    public static void removeDiveSiteFromSelectionValue(@NonNull String userUid,
                                                        @NonNull String selectionValueField,
                                                        @NonNull String selectionValueKey,
                                                        @NonNull String diveSiteUid) {
        if (!MyMethods.containsInvalidCharacters(selectionValueKey)) {
            nodeUserSelectedValueDiveSites(userUid, selectionValueField, selectionValueKey).child
                    (diveSiteUid).removeValue();

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectionValue that = (SelectionValue) o;

        return value.equals(that.value) && nodeName.equals(that.nodeName);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + nodeName.hashCode();
        return result;
    }
}
