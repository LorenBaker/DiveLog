package com.lbconsulting.divelogfirebase.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class store various application metrics
 */
public class AppMetrics {

    private static final String NODE_APP_METRICS = "appMetrics";
    private static final String FIELD_DIVE_LOG_ARRAY_SIZE = "diveLogArraySize";
    private static final String FIELD_DIVE_SITE_ARRAY_SIZE = "diveSiteArraySize";
    private static final String FIELD_PEOPLE_ARRAY_SIZE = "peopleArraySize";
    private static final String FIELD_AREAS_ARRAY_SIZE = "areasArraySize";

    private static final DatabaseReference dbReference = FirebaseDatabase.getInstance()
            .getReference();

    private int diveLogArraySize;
    private int diveSiteArraySize;
    private int peopleArraySize;
    private int areasArraySize;

    public AppMetrics() {
    }

    //region Getters and Setters
    public int getDiveLogArraySize() {
        return diveLogArraySize;
    }

    public void setDiveLogArraySize(int diveLogArraySize) {
        this.diveLogArraySize = diveLogArraySize;
    }

    public int getDiveSiteArraySize() {
        return diveSiteArraySize;
    }

    public void setDiveSiteArraySize(int diveSiteArraySize) {
        this.diveSiteArraySize = diveSiteArraySize;
    }

    public int getPeopleArraySize() {
        return peopleArraySize;
    }

    public void setPeopleArraySize(int peopleArraySize) {
        this.peopleArraySize = peopleArraySize;
    }

    public int getAreasArraySize() {
        return areasArraySize;
    }

    public void setAreasArraySize(int areasArraySize) {
        this.areasArraySize = areasArraySize;
    }

    //endregion Getters and Setters

    public static DatabaseReference nodeDiveLogArraySize(@NonNull String userUid) {
        return dbReference.child(NODE_APP_METRICS).child(userUid).
                child(FIELD_DIVE_LOG_ARRAY_SIZE);
    }

    public static DatabaseReference nodeDiveSiteArraySize(@NonNull String userUid) {
        return dbReference.child(NODE_APP_METRICS).child(userUid)
                .child(FIELD_DIVE_SITE_ARRAY_SIZE);
    }

    public static DatabaseReference nodePeopleArraySize(@NonNull String userUid) {
        return dbReference.child(NODE_APP_METRICS).child(userUid)
                .child(FIELD_PEOPLE_ARRAY_SIZE);
    }

    private static DatabaseReference nodeAreasArraySize(@NonNull String userUid) {
        return dbReference.child(NODE_APP_METRICS).child(userUid)
                .child(FIELD_AREAS_ARRAY_SIZE);
    }

    public static void saveDiveLogArraySize(@NonNull String userUid, int diveLogArraySize) {
        nodeDiveLogArraySize(userUid).setValue(diveLogArraySize);
    }

    public static void saveDiveSiteArraySize(@NonNull String userUid, int diveSiteArraySize) {
        nodeDiveSiteArraySize(userUid).setValue(diveSiteArraySize);
    }

    public static void savePeopleArraySize(@NonNull String userUid, int peopleArraySize) {
        nodePeopleArraySize(userUid).setValue(peopleArraySize);
    }

    public static void saveAreasArraySize(@NonNull String userUid, int areasArraySize) {
        nodeAreasArraySize(userUid).setValue(areasArraySize);
    }
}
