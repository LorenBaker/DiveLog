package com.lbconsulting.divelogfirebase.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import timber.log.Timber;

/**
 * Created by Loren on 2/5/2017.
 */
@IgnoreExtraProperties
public class ReefGuideItem {


    public static ArrayList<ReefGuideItem> sortReefGuideItemsByTitle(ArrayList<ReefGuideItem> reefGuideItems) {
        Collections.sort(reefGuideItems, sortAscendingByTitle);
        return reefGuideItems;
    }

    public static ArrayList<ReefGuideItem> sortReefGuideItemsBySortOrderKey(ArrayList<ReefGuideItem> reefGuideItems) {
        Collections.sort(reefGuideItems, sortAscendingBySortOrderKey);
        return reefGuideItems;
    }

    private static final Comparator<ReefGuideItem> sortAscendingByTitle = new Comparator<ReefGuideItem>() {
        public int compare(final ReefGuideItem item1, final ReefGuideItem item2) {
            return item1.getTitle().compareToIgnoreCase(item2.getTitle());
        }
    };

    private static final Comparator<ReefGuideItem> sortAscendingBySortOrderKey = new Comparator<ReefGuideItem>() {
        public int compare(final ReefGuideItem item1, final ReefGuideItem item2) {
            return item1.sortOrder - item2.sortOrder;
        }
    };

    private static final String NODE_REEF_GUIDE_ITEMS = "reefGuideItems";
    private static final DatabaseReference dbReference = FirebaseDatabase
            .getInstance().getReference();

    private String alsoKnownAs;
    private String speciesClass;
    private String speciesInfraClass;
    private String speciesInfraOrder;
    private String speciesOrder;
    private String speciesPhylum;
    private String speciesSuperOrder;
    private String subfamily;
    private String synonyms;
    private String category;
    private String depth;
    private String distribution;
    private String family;
    private String reefGuideDetailUrl;
    private String reefGuideItemUid;
    private String scientificName;
    private String size;
    private int sortOrder;
    private String summaryPageUid;
    private String thumbNailHeight;
    private String thumbNailUrl;
    private String thumbNailFirebaseUrl;
    private String thumbNailWidth;
    private String title;

    private boolean checked;

    public ReefGuideItem() {
    }

    public ReefGuideItem(String alsoKnownAs, String speciesClass, String speciesInfraClass,
                         String speciesInfraOrder, String speciesOrder, String speciesPhylum,
                         String speciesSuperOrder, String subfamily, String synonyms,
                         String category, String depth, String distribution, String family,
                         String reefGuideDetailUrl, String scientificName, String size,
                         int sortOrder, String summaryPageUid, String thumbNailHeight,
                         String thumbNailUrl, String thumbNailWidth, String title) {

        this.alsoKnownAs = alsoKnownAs;
        this.speciesClass = speciesClass;
        this.speciesInfraClass = speciesInfraClass;
        this.speciesInfraOrder = speciesInfraOrder;
        this.speciesOrder = speciesOrder;
        this.speciesPhylum = speciesPhylum;
        this.speciesSuperOrder = speciesSuperOrder;
        this.subfamily = subfamily;
        this.synonyms = synonyms;
        this.category = category;
        this.depth = depth;
        this.distribution = distribution;
        this.family = family;
        this.reefGuideDetailUrl = reefGuideDetailUrl;
        this.reefGuideItemUid = makeUid(thumbNailUrl);
        this.scientificName = scientificName;
        this.size = size;
        this.sortOrder = sortOrder;
        this.summaryPageUid = summaryPageUid;
        this.thumbNailHeight = thumbNailHeight;
        this.thumbNailFirebaseUrl = null;
        this.thumbNailUrl = thumbNailUrl;
        this.thumbNailWidth = thumbNailWidth;
        this.title = title;
    }

    private String makeUid(String thumbNailUrl) {

        String http = " http://";
        String uid = thumbNailUrl.substring(http.length() - 1);
        uid = uid.replace("/", "_");
        uid = uid.replace(".", "_");
        uid = uid.substring(0, uid.length() - 4);
        return uid;
    }

    //<editor-fold desc="Getters and Setters">

    public String getAlsoKnownAs() {
        return alsoKnownAs;
    }

    public void setAlsoKnownAs(String alsoKnownAs) {
        this.alsoKnownAs = alsoKnownAs;
    }

    public String getSpeciesClass() {
        return speciesClass;
    }

    public void setSpeciesClass(String speciesClass) {
        this.speciesClass = speciesClass;
    }

    public String getSpeciesInfraClass() {
        return speciesInfraClass;
    }

    public void setSpeciesInfraClass(String speciesInfraClass) {
        this.speciesInfraClass = speciesInfraClass;
    }

    public String getSpeciesInfraOrder() {
        return speciesInfraOrder;
    }

    public void setSpeciesInfraOrder(String speciesInfraOrder) {
        this.speciesInfraOrder = speciesInfraOrder;
    }

    public String getSpeciesOrder() {
        return speciesOrder;
    }

    public void setSpeciesOrder(String speciesOrder) {
        this.speciesOrder = speciesOrder;
    }

    public String getSpeciesPhylum() {
        return speciesPhylum;
    }

    public void setSpeciesPhylum(String speciesPhylum) {
        this.speciesPhylum = speciesPhylum;
    }

    public String getSpeciesSuperOrder() {
        return speciesSuperOrder;
    }

    public void setSpeciesSuperOrder(String speciesSuperOrder) {
        this.speciesSuperOrder = speciesSuperOrder;
    }

    public String getSubfamily() {
        return subfamily;
    }

    public void setSubfamily(String subfamily) {
        this.subfamily = subfamily;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getReefGuideDetailUrl() {
        return reefGuideDetailUrl;
    }

    public void setReefGuideDetailUrl(String reefGuideDetailUrl) {
        this.reefGuideDetailUrl = reefGuideDetailUrl;
    }

    public String getReefGuideItemUid() {
        return reefGuideItemUid;
    }

    public void setReefGuideItemUid(String reefGuideItemUid) {
        this.reefGuideItemUid = reefGuideItemUid;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSummaryPageUid() {
        return summaryPageUid;
    }

    public void setSummaryPageUid(String summaryPageUid) {
        this.summaryPageUid = summaryPageUid;
    }

    public String getThumbNailHeight() {
        return thumbNailHeight;
    }

    public void setThumbNailHeight(String thumbNailHeight) {
        this.thumbNailHeight = thumbNailHeight;
    }

    public String getThumbNailUrl() {
        return thumbNailUrl;
    }

    public void setThumbNailUrl(String thumbNailUrl) {
        this.thumbNailUrl = thumbNailUrl;
    }

    public String getThumbNailFirebaseUrl() {
        return thumbNailFirebaseUrl;
    }

    public void setThumbNailFirebaseUrl(String thumbNailFirebaseUrl) {
        this.thumbNailFirebaseUrl = thumbNailFirebaseUrl;
    }

    public String getThumbNailWidth() {
        return thumbNailWidth;
    }

    public void setThumbNailWidth(String thumbNailWidth) {
        this.thumbNailWidth = thumbNailWidth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    public boolean isChecked() {
        return checked;
    }

    @Exclude
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

//</editor-fold> Getters and Setters

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReefGuideItem that = (ReefGuideItem) o;

        return reefGuideItemUid.equals(that.reefGuideItemUid);

    }

    @Override
    public int hashCode() {
        return reefGuideItemUid.hashCode();
    }

    public static DatabaseReference nodeAllCaribbeanReefGuideItems() {
        return dbReference.child(NODE_REEF_GUIDE_ITEMS).child(ReefGuide.NODE_CARIBBEAN);
    }

    public static DatabaseReference nodeAllHawaiianReefGuideItems() {
        return dbReference.child(NODE_REEF_GUIDE_ITEMS).child(ReefGuide.NODE_HAWAII);
    }

    public static DatabaseReference nodeAllSouthFloridaReefGuideItems() {
        return dbReference.child(NODE_REEF_GUIDE_ITEMS).child(ReefGuide.NODE_SOUTH_FLORIDA);
    }

    public static DatabaseReference nodeAllTropicalPacificReefGuideItems() {
        return dbReference.child(NODE_REEF_GUIDE_ITEMS).child(ReefGuide.NODE_TROPICAL_PACIFIC);
    }

    public static DatabaseReference nodeReefGuideItems(int reefGuideId, @NonNull String summaryPageUid) {
        DatabaseReference node = null;
        switch (reefGuideId) {
            case ReefGuide.CARIBBEAN:
                node = nodeAllCaribbeanReefGuideItems().child(summaryPageUid);
                break;

            case ReefGuide.HAWAII:
                node = nodeAllHawaiianReefGuideItems().child(summaryPageUid);
                break;

            case ReefGuide.SOUTH_FLORIDA:
                node = nodeAllSouthFloridaReefGuideItems().child(summaryPageUid);
                break;

            case ReefGuide.TROPICAL_PACIFIC:
                node = nodeAllTropicalPacificReefGuideItems().child(summaryPageUid);
                break;
            default:
                Timber.e("nodeReefGuideItems(): Unknown reefGuideId: %d", reefGuideId);
        }
        return node;
    }

    public static DatabaseReference nodeReefGuideItem(int reefGuideId,
                                                      @NonNull String summaryPageUid,
                                                      @NonNull String reefGuideItemUid) {
        return nodeReefGuideItems(reefGuideId, summaryPageUid).child(reefGuideItemUid);
    }

    public static void save(int reefGuideId, @NonNull String summaryPageUid, @NonNull ReefGuideItem reefGuideItem) {
        nodeReefGuideItem(reefGuideId, summaryPageUid, reefGuideItem.getReefGuideItemUid()).setValue(reefGuideItem);
        Timber.i("Saved \"%s\" with url: %s.", reefGuideItem.toString(), reefGuideItem.getThumbNailUrl());
    }

    public static void remove(int reefGuideId, @NonNull String summaryPageUid, @NonNull ReefGuideItem reefGuideItem) {
        nodeReefGuideItem(reefGuideId, summaryPageUid, reefGuideItem.getReefGuideItemUid()).removeValue();
        Timber.i("Removed \"%s\" with reefGuideItemUid: %s.",
                reefGuideItem.toString(), reefGuideItem.getReefGuideItemUid());
    }

    public static void removeAllReefGuideItems(int reefGuideId, String reefGuideTitle) {
        Timber.i("removeAllReefGuideItems(): ReefGuide: %s.", reefGuideTitle);
        switch (reefGuideId) {
            case ReefGuide.CARIBBEAN:
                nodeAllCaribbeanReefGuideItems().removeValue();
                break;

            case ReefGuide.HAWAII:
                nodeAllHawaiianReefGuideItems().removeValue();
                break;

            case ReefGuide.SOUTH_FLORIDA:
                nodeAllSouthFloridaReefGuideItems().removeValue();
                break;

            case ReefGuide.TROPICAL_PACIFIC:
                nodeAllTropicalPacificReefGuideItems().removeValue();
                break;
            default:
                Timber.e("removeAllReefGuideItems(): Unknown reefGuideId: %d", reefGuideId);
        }

    }

    public static void removeReefGuideItems(int reefGuideId, @NonNull String summaryPageUid) {
        nodeReefGuideItems(reefGuideId, summaryPageUid).removeValue();
        Timber.i("removeReefGuideItems() with summaryPageUid: ", summaryPageUid);
    }

//    public static void saveImageToFirebase(@NonNull String userUid,
//                                           @NonNull String diveLogUid,
//                                           @NonNull ReefGuideItem reefGuideItem) {
//
////        rootReference.child(userUid).child(diveLogUid)
//    }

//    private class StoreImage extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            return null;
//        }
//    }
}
