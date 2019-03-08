package com.lbconsulting.divelogfirebase.reefGuide;

import android.support.annotation.NonNull;

import com.lbconsulting.divelogfirebase.models.ReefGuideItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import timber.log.Timber;

/**
 * Example program to list links from a URL.
 */
public class RetrieveReefGuideDetail {
    ThumbnailAndUrl mThumbnailAndUrl;
    String mSummaryPageUid;

    public RetrieveReefGuideDetail(@NonNull ThumbnailAndUrl thumbnailAndUrl,
                                   @NonNull String summaryPageUid) {
        this.mThumbnailAndUrl = thumbnailAndUrl;
        this.mSummaryPageUid = summaryPageUid;
    }

    public ReefGuideItem fetch() {
        Timber.i("fetching %s", mThumbnailAndUrl.getDetailUrl());
        ReefGuideItem reefGuideItem = null;

        String alsoKnownAs = null;
        String speciesClass = null;
        String speciesInfraClass = null;
        String speciesInfraOrder = null;
        String speciesOrder = null;
        String speciesPhylum = null;
        String speciesSuperOrder = null;
        String subfamily = null;
        String synonyms = null;
        String category = null;
        String depth = null;
        String distribution = null;
        String family = null;
        String scientificName = null;
        String size = null;
        String thumbNailHeight = "276";
//        String thumbNailUrl = null;
        String thumbNailWidth = "370";
        String title = null;

        try {
            Document doc = Jsoup.connect(mThumbnailAndUrl.getDetailUrl()).get();


            Element titledetails = doc.select("div.titledetails").first();
            if (titledetails != null) {
                title = titledetails.text();
            }

            Elements infodetails = doc.select("div.infodetails");
            for (Element element : infodetails) {
                if (element.children().size() > 0) {
                    String elementTitle = element.child(0).text();
                    if (elementTitle.contains("Scientific Name")) {
                        scientificName = element.child(1).text();
                    } else if (elementTitle.contains("Family")) {
                        family = element.child(1).text();
                    } else if (elementTitle.contains("Category")) {
                        category = element.child(1).text();
                    } else if (elementTitle.contains("Size")) {
                        size = element.child(1).text();
                    } else if (elementTitle.contains("Depth")) {
                        depth = element.child(1).text();
                    } else if (elementTitle.contains("Distribution")) {
                        distribution = element.child(1).text();
                    } else if (elementTitle.contains("Also known as")) {
                        alsoKnownAs = element.child(1).text();
                    } else if (elementTitle.contains("Synonyms")) {
                        synonyms = element.child(1).text();
                    } else if (elementTitle.contains("Subfamily")) {
                        subfamily = element.child(1).text();
                    } else if (elementTitle.contains("Superorder")) {
                        speciesSuperOrder = element.child(1).text();
                    } else if (elementTitle.contains("Infraorder")) {
                        speciesInfraOrder = element.child(1).text();
                    } else if (elementTitle.contains("Order")) {
                        speciesOrder = element.child(1).text();
                    } else if (elementTitle.contains("Infraclass")) {
                        speciesInfraClass = element.child(1).text();
                    } else if (elementTitle.contains("Class")) {
                        speciesClass = element.child(1).text();
                    } else if (elementTitle.contains("Phylum")) {
                        speciesPhylum = element.child(1).text();
                    } else {
                        Timber.e("fetch(): unknown elementTitle: %s", elementTitle);
                    }
                }
            }

//            Elements jpgs = doc.select("img[src$=.jpg]");
//            String temp = "";

//            Elements media = doc.select("div.galleryspan");
//            if(media.size()>0){
//                Element galleryspan = media.get(0);
//                Elements src = galleryspan.select("img.selframe");
//                thumbNailUrl = src.attr("src");
//            }
//            for (Element src : media) {
//                if (src.tagName().equals("img")) {
//                    if (src.attr("abs:src").contains("thumb")) {
//                        thumbNailUrl = src.attr("abs:src");
//                        if (!src.attr("width").isEmpty() && !src.attr("height").isEmpty()) {
//                            thumbNailWidth = src.attr("width");
//                            thumbNailHeight = src.attr("height");
//                        }
//                    }
//                }
//            }

            reefGuideItem = new ReefGuideItem(alsoKnownAs, speciesClass, speciesInfraClass,
                    speciesInfraOrder, speciesOrder, speciesPhylum, speciesSuperOrder,
                    subfamily, synonyms, category, depth, distribution, family, mThumbnailAndUrl.getDetailUrl(),
                    scientificName, size, -1, mSummaryPageUid, thumbNailHeight, mThumbnailAndUrl.getThumbNail(),
                    thumbNailWidth, title);

        } catch (IOException e) {
            Timber.e("fetch(): Exception: %s.", e.getMessage());
        }

        return reefGuideItem;
    }

}
