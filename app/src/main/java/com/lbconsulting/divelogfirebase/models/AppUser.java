package com.lbconsulting.divelogfirebase.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Java object that holds Dive Log user data.
 */
public class AppUser {

    private static final String NODE_USERS = "users";

    private static final DatabaseReference dbReference = FirebaseDatabase
            .getInstance().getReference();

    private String displayName;
    private String email;
    private String photoUrl;
    private String userUid;

    public AppUser() {
    }

    public AppUser(@NonNull String userUid, @NonNull String displayName,
                   @NonNull String email, @NonNull String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.userUid = userUid;
    }


    //<editor-fold desc="Getters and Setters">

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }


    @Override
    public String toString() {
        return displayName;
    }
    //</editor-fold> Getters and Setters


    //<editor-fold desc="Firebase Helpers">
    public static DatabaseReference nodeUser(@NonNull String userUid) {
        return dbReference.child(NODE_USERS).child(userUid);
    }

    public static void saveAppUser(@NonNull AppUser appUser) {
        dbReference.child(NODE_USERS).child(appUser.getUserUid()).setValue(appUser);
    }

}
