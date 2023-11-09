package com.sprtcoding.tourizal.Model.FSModel;

public class FeaturedModelFS {
    String OWNER_UID,
            RESORT_ID,
            FEATURED_ID,
            FEATURED_TITLE,
            FEATURED_PHOTO_ID,
            FEATURED_PHOTO_NAME,
            RESORT_NAME,
            OWNER_NAME,
            LOCATION,
            LAT,
            LNG,
            TIME,
            DATE,
            FEATURED_PHOTO_URL;

    public FeaturedModelFS() {
    }

    public FeaturedModelFS(String OWNER_UID, String RESORT_ID, String FEATURED_ID, String FEATURED_TITLE, String FEATURED_PHOTO_ID, String FEATURED_PHOTO_NAME, String RESORT_NAME, String OWNER_NAME, String LOCATION, String LAT, String LNG, String TIME, String DATE, String FEATURED_PHOTO_URL) {
        this.OWNER_UID = OWNER_UID;
        this.RESORT_ID = RESORT_ID;
        this.FEATURED_ID = FEATURED_ID;
        this.FEATURED_TITLE = FEATURED_TITLE;
        this.FEATURED_PHOTO_ID = FEATURED_PHOTO_ID;
        this.FEATURED_PHOTO_NAME = FEATURED_PHOTO_NAME;
        this.RESORT_NAME = RESORT_NAME;
        this.OWNER_NAME = OWNER_NAME;
        this.LOCATION = LOCATION;
        this.LAT = LAT;
        this.LNG = LNG;
        this.TIME = TIME;
        this.DATE = DATE;
        this.FEATURED_PHOTO_URL = FEATURED_PHOTO_URL;
    }

    public String getOWNER_UID() {
        return OWNER_UID;
    }

    public void setOWNER_UID(String OWNER_UID) {
        this.OWNER_UID = OWNER_UID;
    }

    public String getRESORT_ID() {
        return RESORT_ID;
    }

    public void setRESORT_ID(String RESORT_ID) {
        this.RESORT_ID = RESORT_ID;
    }

    public String getFEATURED_ID() {
        return FEATURED_ID;
    }

    public void setFEATURED_ID(String FEATURED_ID) {
        this.FEATURED_ID = FEATURED_ID;
    }

    public String getFEATURED_TITLE() {
        return FEATURED_TITLE;
    }

    public void setFEATURED_TITLE(String FEATURED_TITLE) {
        this.FEATURED_TITLE = FEATURED_TITLE;
    }

    public String getFEATURED_PHOTO_ID() {
        return FEATURED_PHOTO_ID;
    }

    public void setFEATURED_PHOTO_ID(String FEATURED_PHOTO_ID) {
        this.FEATURED_PHOTO_ID = FEATURED_PHOTO_ID;
    }

    public String getFEATURED_PHOTO_NAME() {
        return FEATURED_PHOTO_NAME;
    }

    public void setFEATURED_PHOTO_NAME(String FEATURED_PHOTO_NAME) {
        this.FEATURED_PHOTO_NAME = FEATURED_PHOTO_NAME;
    }

    public String getRESORT_NAME() {
        return RESORT_NAME;
    }

    public void setRESORT_NAME(String RESORT_NAME) {
        this.RESORT_NAME = RESORT_NAME;
    }

    public String getOWNER_NAME() {
        return OWNER_NAME;
    }

    public void setOWNER_NAME(String OWNER_NAME) {
        this.OWNER_NAME = OWNER_NAME;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String LOCATION) {
        this.LOCATION = LOCATION;
    }

    public String getLAT() {
        return LAT;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public String getLNG() {
        return LNG;
    }

    public void setLNG(String LNG) {
        this.LNG = LNG;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getFEATURED_PHOTO_URL() {
        return FEATURED_PHOTO_URL;
    }

    public void setFEATURED_PHOTO_URL(String FEATURED_PHOTO_URL) {
        this.FEATURED_PHOTO_URL = FEATURED_PHOTO_URL;
    }
}
