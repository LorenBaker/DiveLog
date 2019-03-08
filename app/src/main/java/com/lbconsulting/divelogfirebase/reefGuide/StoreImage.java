package com.lbconsulting.divelogfirebase.reefGuide;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lbconsulting.divelogfirebase.models.ReefGuideItem;

import timber.log.Timber;

/**
 * Created by Loren on 2/5/2017.
 */

public class StoreImage extends AsyncTask<Void, Void, Void> {
    private String mUserUid;
    private String mDiveLogUid;
    private ReefGuideItem mReefGuideItem;
    private boolean isVerbose;


    private static final String ROOT_STORAGE_REFERENCE = "gs://divelog-c7d91.appspot.com/";

    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference rootReference = storage.getReferenceFromUrl(ROOT_STORAGE_REFERENCE);

    public StoreImage(@NonNull String userUid,
                      @NonNull String diveLogUid,
                      @NonNull ReefGuideItem reefGuideItem,
                      boolean isVerbose) {

        this.mUserUid = userUid;
        this.mDiveLogUid = diveLogUid;
        this.mReefGuideItem = reefGuideItem;
        this.isVerbose = isVerbose;
    }

    @Override
    protected void onPreExecute() {
        Timber.i("onPreExecute()");
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}


