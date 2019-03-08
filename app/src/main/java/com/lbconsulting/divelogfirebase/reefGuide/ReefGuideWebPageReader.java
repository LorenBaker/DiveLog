package com.lbconsulting.divelogfirebase.reefGuide;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.lbconsulting.divelogfirebase.models.ReefGuide;
import com.lbconsulting.divelogfirebase.models.ReefGuideItem;
import com.lbconsulting.divelogfirebase.utils.MyMethods;

import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Loren on 2/5/2017.
 */

public class ReefGuideWebPageReader extends AsyncTask<Void, String, Void> {
    private Context context;
    private ArrayList<ReefGuide> mReefGuides;
    private String mReefGuideTitle;
    private int mReefGuideId;

    private ProgressDialog progressDialog;
    private int mNumberOfImages;

    public ReefGuideWebPageReader(@NonNull Context context,
                                  int reefGuideId,
                                  @NonNull String reefGuideTitle,
                                  @NonNull ArrayList<ReefGuide> reefGuides) {
        this.context = context;
        this.mReefGuideId = reefGuideId;
        this.mReefGuideTitle = reefGuideTitle;
        this.mReefGuides = reefGuides;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading: ");
    }

    @Override
    protected void onPreExecute() {
        Timber.i("onPreExecute()");
        mNumberOfImages = 0;
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        String loadingMessage = "Loading: " + values[0];
        progressDialog.setMessage(loadingMessage);
    }

    @Override
    protected Void doInBackground(Void... params) {
        ReefGuideItem.removeAllReefGuideItems(mReefGuideId, mReefGuideTitle);
        retrieveReefGuideItems(mReefGuideId, mReefGuideTitle, mReefGuides);
        return null;
    }

    private void retrieveReefGuideItems(int reefGuideId, String reefGuideTitle, ArrayList<ReefGuide> reefGuides) {
        Timber.i("retrieveReefGuideItems() for guide: %s", reefGuideTitle);
        for (ReefGuide reefGuide : reefGuides) {

            publishProgress(reefGuide.getSummaryPageUid());

            RetrieveReefGuideDetailUrls retrieveReefGuideDetailUrls = new RetrieveReefGuideDetailUrls(reefGuideId,reefGuide.getSummaryPageUrl());
            ArrayList<ThumbnailAndUrl> reefGuideDetailUrls = retrieveReefGuideDetailUrls.fetch();

            for (ThumbnailAndUrl thumbnailAndUrl : reefGuideDetailUrls) {
                RetrieveReefGuideDetail retrieveReefGuideDetail = new RetrieveReefGuideDetail(
                        thumbnailAndUrl, reefGuide.getSummaryPageUid());
                ReefGuideItem reefGuideItem = retrieveReefGuideDetail.fetch();
                mNumberOfImages++;
                reefGuideItem.setSortOrder(mNumberOfImages);
                ReefGuideItem.save(reefGuideId, reefGuide.getSummaryPageUid(), reefGuideItem);
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        String title = "Refresh Reef Guide Complete";
        String msg = String.format(Locale.getDefault(), mReefGuideTitle+":\nRetrieved %d reef guides and %d images",
                mReefGuides.size(), mNumberOfImages);

        Timber.i("onPostExecute() " + msg);
        MyMethods.showOkDialog(context, title, msg);
        progressDialog.dismiss();
    }
}


