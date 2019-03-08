package com.lbconsulting.divelogfirebase.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.lbconsulting.divelogfirebase.utils.csvParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * This class holds the user's diveEquipment
 */
public class DiveEquipment {

    public DiveEquipment() {
    }

    private static final String NODE_DIVE_EQUIPMENT = "diveEquipment";
    private static final String NODE_DIVE_EQUIPMENT_LIST = "diveEquipmentList";
    private static final DatabaseReference dbReference = FirebaseDatabase.getInstance()
            .getReference();

    private HashMap<String, Boolean> diveEquipmentList = new HashMap<>();

    public HashMap<String, Boolean> getDiveEquipmentList() {
        return diveEquipmentList;
    }

    public void setDiveEquipmentList(HashMap<String, Boolean> diveEquipmentMap) {
        this.diveEquipmentList = diveEquipmentMap;
    }

    public static DatabaseReference nodeDiveEquipment(@NonNull String userUid) {
        return dbReference.child(NODE_DIVE_EQUIPMENT).child(userUid);
    }

    private static DatabaseReference nodeUserDiveEquipmentList(@NonNull String userUid) {
        return dbReference
                .child(NODE_DIVE_EQUIPMENT).child(userUid)
                .child(NODE_DIVE_EQUIPMENT_LIST);
    }

    public static void save(@NonNull String userUid, @NonNull Map<String, Boolean> diveEquipment) {
        nodeUserDiveEquipmentList(userUid).setValue(diveEquipment);
        Timber.i("Saved diveEquipmentList with %d items.", diveEquipment.size());
    }

    public static void saveEquipmentItem(@NonNull String userUid,
                                         @NonNull String equipmentItem) {
        nodeUserDiveEquipmentList(userUid).child(equipmentItem).setValue(true);
        Timber.i("Saved equipment item\"%s\".", equipmentItem);
    }

    public static void removeEquipmentItem(@NonNull String userUid,
                                         @NonNull String equipmentItem) {
        nodeUserDiveEquipmentList(userUid).child(equipmentItem).removeValue();
        Timber.i("Removed equipment item\"%s\".", equipmentItem);
    }

    @Exclude
    public ArrayList<String> getEquipmentArray() {
        Set<String> keys = diveEquipmentList.keySet();
        ArrayList<String> valuesArray = new ArrayList<>(keys);
        if (valuesArray.size() > 0) {
            Collections.sort(valuesArray, String.CASE_INSENSITIVE_ORDER);
        }
        return valuesArray;
    }

    @Exclude
    public String getEquipmentListString() {
        String valuesString = "";
        ArrayList<String> valuesArray = new ArrayList<>();
        if (diveEquipmentList.size() > 0) {
            for (Object o : diveEquipmentList.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                if ((boolean) pair.getValue()) {
                    valuesArray.add(pair.getKey().toString());
                }
            }
            if (valuesArray.size() > 0) {
                Collections.sort(valuesArray, String.CASE_INSENSITIVE_ORDER);
                valuesString = csvParser.toCSVString(valuesArray);
            }
        }
        return valuesString;
    }

    @Exclude
    public void setDiveEquipmentListValues(@NonNull String equipmentListString) {
        ArrayList<ArrayList<String>> equipmentListRecords = csvParser
                .CreateRecordAndFieldLists(equipmentListString);
        if (equipmentListRecords.size() > 0) {
            setAllValues(false);
            ArrayList<String> equipmentList = equipmentListRecords.get(0);
            for (String item : equipmentList) {
                if (diveEquipmentList.containsKey(item)) {
                    diveEquipmentList.put(item, true);
                }
            }
        }
    }

    public void add(@NonNull String item) {
        diveEquipmentList.put(item, true);
    }

    public void remove(@NonNull String item) {
        diveEquipmentList.remove(item);
    }

    @Exclude
    private void setAllValues(Boolean value) {
        if (diveEquipmentList.size() > 0) {
            for (Object o : diveEquipmentList.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                diveEquipmentList.put(pair.getKey().toString(), value);
            }
        }
    }
}
