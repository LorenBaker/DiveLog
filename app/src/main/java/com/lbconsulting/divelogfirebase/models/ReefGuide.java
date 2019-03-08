package com.lbconsulting.divelogfirebase.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.lbconsulting.divelogfirebase.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import timber.log.Timber;

/**
 * This class holds marine life data from
 * http://reefguide.org/
 */

public class ReefGuide {
    public static final int CARIBBEAN = 0;
    public static final int HAWAII = 1;
    public static final int SOUTH_FLORIDA = 2;
    public static final int TROPICAL_PACIFIC = 3;

    private static final String NODE_REEF_GUIDES = "reefGuides";
    public static final String NODE_CARIBBEAN = "caribbean";
    public static final String NODE_HAWAII = "hawaii";
    public static final String NODE_SOUTH_FLORIDA = "south_florida";
    public static final String NODE_TROPICAL_PACIFIC = "tropical_pacific";
    public static final String FIELD_SORT_ORDER = "sortOrder";

    private static final DatabaseReference dbReference = FirebaseDatabase
            .getInstance().getReference();

    private String summaryPageTitle;
    private String summaryPageUid;
    private String summaryPageUrl;
    private int sortOrder;
    private int reefGuideId;

//    private ArrayList<ReefGuideItem> reefGuideItems;

    public static ArrayList<ReefGuide> sortReefGuideBySortOrderKey(ArrayList<ReefGuide> reefGuides) {
        Collections.sort(reefGuides, sortAscendingBySortOrderKey);
        return reefGuides;
    }

    private static final Comparator<ReefGuide> sortAscendingBySortOrderKey = new Comparator<ReefGuide>() {
        public int compare(final ReefGuide item1, final ReefGuide item2) {
            return item1.sortOrder - item2.sortOrder;
        }
    };

    public ReefGuide() {
    }

    public ReefGuide(int reefGuideId, int sortOrder, String summaryPageTitle, String summaryPageUid, String summaryPageUrl) {
        this.sortOrder = sortOrder;
        this.summaryPageTitle = summaryPageTitle;
        this.summaryPageUid = summaryPageUid;
        this.summaryPageUrl = summaryPageUrl;
        this.reefGuideId = reefGuideId;
    }
    //    public ReefGuide(String csvReefGuideSummary) {
//        final int SUMMARY_PAGE_SORT_ORDER = 0;
//        final int SUMMARY_PAGE_DESCRIPTION = 6;
//        final int SUMMARY_PAGE_TITLE = 4;
//        final int SUMMARY_PAGE_UID = 3;
//        final int SUMMARY_PAGE_URL = 5;
//
//        String[] field = csvReefGuideSummary.split(",");
//        this.sortOrder = Integer.parseInt(field[SUMMARY_PAGE_SORT_ORDER]);
//        this.summaryPageDescription = field[SUMMARY_PAGE_DESCRIPTION];
//        this.summaryPageTitle = field[SUMMARY_PAGE_TITLE];
//        this.summaryPageUid = field[SUMMARY_PAGE_UID];
//        this.summaryPageUrl = field[SUMMARY_PAGE_URL];
//    }

    //<editor-fold desc="Getters and Setters">

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getReefGuideId() {
        return reefGuideId;
    }

    public void setReefGuideId(int reefGuideId) {
        this.reefGuideId = reefGuideId;
    }

    public String getSummaryPageTitle() {
        return summaryPageTitle;
    }

    public void setSummaryPageTitle(String summaryPageTitle) {
        this.summaryPageTitle = summaryPageTitle;
    }

    public String getSummaryPageUid() {
        return summaryPageUid;
    }

    public void setSummaryPageUid(String summaryPageUid) {
        this.summaryPageUid = summaryPageUid;
    }

    public String getSummaryPageUrl() {
        return summaryPageUrl;
    }

    public void setSummaryPageUrl(String summaryPageUrl) {
        this.summaryPageUrl = summaryPageUrl;
    }

    //</editor-fold> Getters and Setters

    @Override
    public String toString() {
        return summaryPageTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReefGuide reefGuide = (ReefGuide) o;

        return summaryPageUid.equals(reefGuide.summaryPageUid);
    }

    @Override
    public int hashCode() {
        return summaryPageUid.hashCode();
    }

    private static DatabaseReference nodeReefGuides() {
        return dbReference.child(NODE_REEF_GUIDES);
    }

    public static DatabaseReference nodeCaribbeanReefGuide() {
        return nodeReefGuides().child(NODE_CARIBBEAN);
    }

    public static DatabaseReference nodeHawaiiReefGuide() {
        return nodeReefGuides().child(NODE_HAWAII);
    }

    public static DatabaseReference nodeSouthFloridaReefGuide() {
        return nodeReefGuides().child(NODE_SOUTH_FLORIDA);
    }

    public static DatabaseReference nodeTropicalPacificReefGuide() {
        return nodeReefGuides().child(NODE_TROPICAL_PACIFIC);
    }

    public static DatabaseReference nodeReefGuides(@NonNull String reefGuideNode, String summaryPageUid) {
        return nodeReefGuides().child(reefGuideNode).child(summaryPageUid);
    }

    public static void save(@NonNull String reefGuideNode, @NonNull ReefGuide reefGuide) {
        nodeReefGuides(reefGuideNode, reefGuide.getSummaryPageUid()).setValue(reefGuide);
        Timber.i("Saved \"%s\" with uid: %s.", reefGuide.toString(), reefGuide.getSummaryPageUid());
    }

    public static void remove(@NonNull String reefGuideNode, @NonNull ReefGuide reefGuide) {
        nodeReefGuides(reefGuideNode, reefGuide.getSummaryPageUid()).removeValue();
        Timber.i("Removed \"%s\" with uid: %s.", reefGuide.toString(), reefGuide.getSummaryPageUid());
    }

    public static void removeAllReefGuides() {
        nodeReefGuides().removeValue();
        Timber.i("removeAllReefGuides()");
    }

    public static DatabaseReference reefGuides(int reefGuideId) {
        DatabaseReference node = null;
        switch (reefGuideId) {
            case CARIBBEAN:
                node = nodeCaribbeanReefGuide();
                break;

            case HAWAII:
                node = nodeHawaiiReefGuide();
                break;

            case SOUTH_FLORIDA:
                node = nodeSouthFloridaReefGuide();
                break;

            case TROPICAL_PACIFIC:
                node = nodeTropicalPacificReefGuide();
                break;

            default:
                Timber.e("reefGuides(): Unknown reefGuideId");
        }
        return node;
    }

    @Exclude
    public static String getTitle(Context context, int reefGuideId) {
        String[] reefGuideTitles = context.getResources().getStringArray(R.array.reef_guide_titles);
        return reefGuideTitles[reefGuideId];
    }
}

