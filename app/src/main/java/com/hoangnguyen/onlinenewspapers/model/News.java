package com.hoangnguyen.onlinenewspapers.model;

public class News {
    private String mTitle;
    private String mDescription;
    private String mUrl;
    private String mUrlToImage;
    private String mPubDate;
    private String mAuthor;
    private String mSource;
    private String mTime;

    public News() {
    }

    public News(String mTitle, String mDescription, String mUrl, String mUrlToImage, String mPubDate, String mAuthor, String mSource) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mUrl = mUrl;
        this.mUrlToImage = mUrlToImage;
        this.mPubDate = mPubDate;
        this.mAuthor = mAuthor;
        this.mSource = mSource;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmSource() {
        return mSource;
    }

    public void setmSource(String mSource) {
        this.mSource = mSource;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmUrlToImage() {
        return mUrlToImage;
    }

    public void setmUrlToImage(String mUrlToImage) {
        this.mUrlToImage = mUrlToImage;
    }

    public String getmPubDate() {
        return mPubDate;
    }

    public void setmPubDate(String mPubDate) {
        this.mPubDate = mPubDate;
    }
}
