package com.sprtcoding.tourizal.Model.FSModel;

public class ResortFireStoreModel {
    String USER_ID;
    String RESORT_ID;
    String RESORT_PIC_URL;
    String RESORT_ENTRANCE_FEE;
    String RESORT_PIC_NAME;
    String RESORT_NAME;
    String OWNER_NAME;
    String LOCATION;
    double LAT;
    double LNG;
    String TIME;
    String DATE;

    public ResortFireStoreModel() {
    }

    public ResortFireStoreModel(String USER_ID, String RESORT_ID, String RESORT_PIC_URL, String RESORT_ENTRANCE_FEE, String RESORT_PIC_NAME, String RESORT_NAME, String OWNER_NAME, String LOCATION, double LAT, double LNG, String TIME, String DATE) {
        this.USER_ID = USER_ID;
        this.RESORT_ID = RESORT_ID;
        this.RESORT_PIC_URL = RESORT_PIC_URL;
        this.RESORT_ENTRANCE_FEE = RESORT_ENTRANCE_FEE;
        this.RESORT_PIC_NAME = RESORT_PIC_NAME;
        this.RESORT_NAME = RESORT_NAME;
        this.OWNER_NAME = OWNER_NAME;
        this.LOCATION = LOCATION;
        this.LAT = LAT;
        this.LNG = LNG;
        this.TIME = TIME;
        this.DATE = DATE;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getRESORT_ID() {
        return RESORT_ID;
    }

    public void setRESORT_ID(String RESORT_ID) {
        this.RESORT_ID = RESORT_ID;
    }

    public String getRESORT_PIC_URL() {
        return RESORT_PIC_URL;
    }

    public void setRESORT_PIC_URL(String RESORT_PIC_URL) {
        this.RESORT_PIC_URL = RESORT_PIC_URL;
    }

    public String getRESORT_ENTRANCE_FEE() {
        return RESORT_ENTRANCE_FEE;
    }

    public void setRESORT_ENTRANCE_FEE(String RESORT_ENTRANCE_FEE) {
        this.RESORT_ENTRANCE_FEE = RESORT_ENTRANCE_FEE;
    }

    public String getRESORT_PIC_NAME() {
        return RESORT_PIC_NAME;
    }

    public void setRESORT_PIC_NAME(String RESORT_PIC_NAME) {
        this.RESORT_PIC_NAME = RESORT_PIC_NAME;
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

    public double getLAT() {
        return LAT;
    }

    public void setLAT(double LAT) {
        this.LAT = LAT;
    }

    public double getLNG() {
        return LNG;
    }

    public void setLNG(double LNG) {
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
}
