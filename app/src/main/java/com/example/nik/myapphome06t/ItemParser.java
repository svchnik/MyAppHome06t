package com.example.nik.myapphome06t;


public class ItemParser {

    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;

    public String toString() {
        return mCaption;
    }


    public String geCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        this.mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }
    public void setOwner(String owner) {
        mOwner = owner;
    }
    public String getPhotoPageUrl() {
        return "http://www.flickr.com/photos/" + mOwner + "/" + mId;
    }
}
