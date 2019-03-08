package com.lbconsulting.divelogfirebase.reefGuide;

import android.support.annotation.NonNull;

import com.lbconsulting.divelogfirebase.models.ReefGuide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import timber.log.Timber;


/**
 * Example program to list links from a URL.
 */
public class RetrieveReefGuideDetailUrls {
    private int mReefGuideId;
    private String mReefGuideSummaryPageUrl;

    public RetrieveReefGuideDetailUrls(int reefGuideId, @NonNull String reefGuideSummaryPageUrl) {
        this.mReefGuideId = reefGuideId;
        this.mReefGuideSummaryPageUrl = reefGuideSummaryPageUrl;
    }

    public ArrayList<ThumbnailAndUrl> fetch() {
        ArrayList<ThumbnailAndUrl> thumbnailsAndUrls = new ArrayList<>();
        switch (mReefGuideId) {
            case ReefGuide.CARIBBEAN:
                thumbnailsAndUrls = fetchCaribbean();
                break;

            case ReefGuide.HAWAII:
                thumbnailsAndUrls = fetchHawaii();
                break;


            case ReefGuide.SOUTH_FLORIDA:
                thumbnailsAndUrls = fetchSouthFlorida();
                break;


            case ReefGuide.TROPICAL_PACIFIC:
                thumbnailsAndUrls = fetchTropicalPacific();
                break;

            default:
                Timber.e("fetch(): Unknown reefGuideId!");

        }
        return thumbnailsAndUrls;
    }

    private ArrayList<ThumbnailAndUrl> fetchCaribbean() {

        ArrayList<String> reefGuideDetailUrls = new ArrayList<>();
        ArrayList<ThumbnailAndUrl> thumbnailsAndUrls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(mReefGuideSummaryPageUrl).get();
            Elements links = doc.select("div.nameid").select("a[href]");

            for (Element link : links) {
                if (link.attr("abs:href").contains("/carib/")
                        && !link.attr("abs:href").contains("index")
                        && !link.attr("abs:href").contains("cat")
                        && !link.attr("abs:href").contains("latest")) {
                    reefGuideDetailUrls.add(link.attr("abs:href"));
                }
            }

            Elements thumbNails = doc.select("img[src$=.jpg]");

            for (int i = 0; i < thumbNails.size(); i++) {
                Element image = thumbNails.get(i);
                String thumbNailUrl = image.absUrl("src");
                String fileNameWithoutExtn = thumbNailUrl.substring(0, thumbNailUrl.lastIndexOf('.'));
                String[] path = fileNameWithoutExtn.split("/");
                String filenameWithNoNumbers = path[path.length - 1].replaceAll("[^A-Za-z]", "");

                for (int j = 0; j < reefGuideDetailUrls.size(); j++) {
                    if (reefGuideDetailUrls.get(j).contains(filenameWithNoNumbers)) {
                        ThumbnailAndUrl thumbnailAndUrl = new ThumbnailAndUrl(
                                reefGuideDetailUrls.get(j), thumbNailUrl);
                        thumbnailsAndUrls.add(thumbnailAndUrl);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            Timber.e("fetchCaribbean(): Exception: %s.", e.getMessage());
        }

        return thumbnailsAndUrls;
    }

    private ArrayList<ThumbnailAndUrl> fetchHawaii() {
        ArrayList<String> reefGuideDetailUrls = new ArrayList<>();
        ArrayList<ThumbnailAndUrl> thumbnailsAndUrls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(mReefGuideSummaryPageUrl).get();
            Elements links = doc.select("div.nameid").select("a[href]");

            for (Element link : links) {
                if (link.attr("abs:href").contains("hawaii")
                        && !link.attr("abs:href").contains("index")
                        && !link.attr("abs:href").contains("cat")
                        && !link.attr("abs:href").contains("latest")) {
                    reefGuideDetailUrls.add(link.attr("abs:href"));
                }
            }

            Elements thumbNails = doc.select("img[src$=.jpg]");

            for (int i = 0; i < thumbNails.size(); i++) {
                Element image = thumbNails.get(i);
                String thumbNailUrl = image.absUrl("src");
                String fileNameWithoutExtn = thumbNailUrl.substring(0, thumbNailUrl.lastIndexOf('.'));
                String[] path = fileNameWithoutExtn.split("/");
                String filenameWithNoNumbers = path[path.length - 1].replaceAll("[^A-Za-z]", "");

                for (int j = 0; j < reefGuideDetailUrls.size(); j++) {
                    if (reefGuideDetailUrls.get(j).contains(filenameWithNoNumbers)) {
                        ThumbnailAndUrl thumbnailAndUrl = new ThumbnailAndUrl(
                                reefGuideDetailUrls.get(j), thumbNailUrl);
                        thumbnailsAndUrls.add(thumbnailAndUrl);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            Timber.e("fetchHawaii(): Exception: %s.", e.getMessage());
        }

        return thumbnailsAndUrls;
    }

    private ArrayList<ThumbnailAndUrl> fetchSouthFlorida() {
        ArrayList<String> reefGuideDetailUrls = new ArrayList<>();
        ArrayList<ThumbnailAndUrl> thumbnailsAndUrls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(mReefGuideSummaryPageUrl).get();
            Elements links = doc.select("div.nameid").select("a[href]");

            for (Element link : links) {
                if (link.attr("abs:href").contains("keys")
                        && !link.attr("abs:href").contains("index")
                        && !link.attr("abs:href").contains("cat")
                        && !link.attr("abs:href").contains("latest")) {
                    reefGuideDetailUrls.add(link.attr("abs:href"));
                }
            }

            Elements thumbNails = doc.select("img[src$=.jpg]");

            for (int i = 0; i < thumbNails.size(); i++) {
                Element image = thumbNails.get(i);
                String thumbNailUrl = image.absUrl("src");
                String fileNameWithoutExtn = thumbNailUrl.substring(0, thumbNailUrl.lastIndexOf('.'));
                String[] path = fileNameWithoutExtn.split("/");
                String filenameWithNoNumbers = path[path.length - 1].replaceAll("[^A-Za-z]", "");

                for (int j = 0; j < reefGuideDetailUrls.size(); j++) {
                    if (reefGuideDetailUrls.get(j).contains(filenameWithNoNumbers)) {
                        ThumbnailAndUrl thumbnailAndUrl = new ThumbnailAndUrl(
                                reefGuideDetailUrls.get(j), thumbNailUrl);
                        thumbnailsAndUrls.add(thumbnailAndUrl);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            Timber.e("fetchHawaii(): Exception: %s.", e.getMessage());
        }

        return thumbnailsAndUrls;
    }

    private ArrayList<ThumbnailAndUrl> fetchTropicalPacific() {
        ArrayList<String> reefGuideDetailUrls = new ArrayList<>();
        ArrayList<ThumbnailAndUrl> thumbnailsAndUrls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(mReefGuideSummaryPageUrl).get();
            Elements links = doc.select("div.nameid").select("a[href]");

            for (Element link : links) {
                if (link.attr("abs:href").contains("indopac")
                        && !link.attr("abs:href").contains("index")
                        && !link.attr("abs:href").contains("cat")
                        && !link.attr("abs:href").contains("latest")) {
                    reefGuideDetailUrls.add(link.attr("abs:href"));
                }
            }

            Elements thumbNails = doc.select("img[src$=.jpg]");

            for (int i = 0; i < thumbNails.size(); i++) {
                Element image = thumbNails.get(i);
                String thumbNailUrl = image.absUrl("src");
                String fileNameWithoutExtn = thumbNailUrl.substring(0, thumbNailUrl.lastIndexOf('.'));
                String[] path = fileNameWithoutExtn.split("/");
                String filenameWithNoNumbers = path[path.length - 1].replaceAll("[^A-Za-z]", "");

                for (int j = 0; j < reefGuideDetailUrls.size(); j++) {
                    if (reefGuideDetailUrls.get(j).contains(filenameWithNoNumbers)) {
                        ThumbnailAndUrl thumbnailAndUrl = new ThumbnailAndUrl(
                                reefGuideDetailUrls.get(j), thumbNailUrl);
                        thumbnailsAndUrls.add(thumbnailAndUrl);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            Timber.e("fetchHawaii(): Exception: %s.", e.getMessage());
        }

        return thumbnailsAndUrls;
    }

}
