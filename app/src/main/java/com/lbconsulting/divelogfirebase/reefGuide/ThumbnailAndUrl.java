package com.lbconsulting.divelogfirebase.reefGuide;

/**
 * Created by Loren on 2/12/2017.
 */

public class ThumbnailAndUrl {
    String thumbNail;
    String detailUrl;

    public ThumbnailAndUrl(String detailUrl, String thumbNail) {
        this.detailUrl = detailUrl;
        this.thumbNail = thumbNail;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }
}
