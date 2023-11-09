package com.sprtcoding.tourizal.Model.FSModel;

public class ReservationModelFS {
    String OWNER_UID,MY_UID,RESORT_ID,AMENITIES_ID,RESERVED_ID,NAME_OF_USER,CONTACT_OF_USER,LOCATION_OF_USER,DATE_RESERVATION,TIME,DATE,STATUS,ROOM_PHOTO_URL,DAYTIME,USER_PHOTO_URL,AMENITIES_TYPE;
    float PRICE;
    int AMENITIES_NO;
    boolean READ;

    public ReservationModelFS() {
    }

    public ReservationModelFS(String OWNER_UID, String MY_UID, String RESORT_ID, String AMENITIES_ID, String RESERVED_ID, String NAME_OF_USER, String CONTACT_OF_USER, String LOCATION_OF_USER, String DATE_RESERVATION, String TIME, String DATE, String STATUS, String ROOM_PHOTO_URL, String DAYTIME, String USER_PHOTO_URL, String AMENITIES_TYPE, float PRICE, int AMENITIES_NO, boolean READ) {
        this.OWNER_UID = OWNER_UID;
        this.MY_UID = MY_UID;
        this.RESORT_ID = RESORT_ID;
        this.AMENITIES_ID = AMENITIES_ID;
        this.RESERVED_ID = RESERVED_ID;
        this.NAME_OF_USER = NAME_OF_USER;
        this.CONTACT_OF_USER = CONTACT_OF_USER;
        this.LOCATION_OF_USER = LOCATION_OF_USER;
        this.DATE_RESERVATION = DATE_RESERVATION;
        this.TIME = TIME;
        this.DATE = DATE;
        this.STATUS = STATUS;
        this.ROOM_PHOTO_URL = ROOM_PHOTO_URL;
        this.DAYTIME = DAYTIME;
        this.USER_PHOTO_URL = USER_PHOTO_URL;
        this.AMENITIES_TYPE = AMENITIES_TYPE;
        this.PRICE = PRICE;
        this.AMENITIES_NO = AMENITIES_NO;
        this.READ = READ;
    }

    public String getOWNER_UID() {
        return OWNER_UID;
    }

    public void setOWNER_UID(String OWNER_UID) {
        this.OWNER_UID = OWNER_UID;
    }

    public String getMY_UID() {
        return MY_UID;
    }

    public void setMY_UID(String MY_UID) {
        this.MY_UID = MY_UID;
    }

    public String getRESORT_ID() {
        return RESORT_ID;
    }

    public void setRESORT_ID(String RESORT_ID) {
        this.RESORT_ID = RESORT_ID;
    }

    public String getAMENITIES_ID() {
        return AMENITIES_ID;
    }

    public void setAMENITIES_ID(String AMENITIES_ID) {
        this.AMENITIES_ID = AMENITIES_ID;
    }

    public String getRESERVED_ID() {
        return RESERVED_ID;
    }

    public void setRESERVED_ID(String RESERVED_ID) {
        this.RESERVED_ID = RESERVED_ID;
    }

    public String getNAME_OF_USER() {
        return NAME_OF_USER;
    }

    public void setNAME_OF_USER(String NAME_OF_USER) {
        this.NAME_OF_USER = NAME_OF_USER;
    }

    public String getCONTACT_OF_USER() {
        return CONTACT_OF_USER;
    }

    public void setCONTACT_OF_USER(String CONTACT_OF_USER) {
        this.CONTACT_OF_USER = CONTACT_OF_USER;
    }

    public String getLOCATION_OF_USER() {
        return LOCATION_OF_USER;
    }

    public void setLOCATION_OF_USER(String LOCATION_OF_USER) {
        this.LOCATION_OF_USER = LOCATION_OF_USER;
    }

    public String getDATE_RESERVATION() {
        return DATE_RESERVATION;
    }

    public void setDATE_RESERVATION(String DATE_RESERVATION) {
        this.DATE_RESERVATION = DATE_RESERVATION;
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

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getROOM_PHOTO_URL() {
        return ROOM_PHOTO_URL;
    }

    public void setROOM_PHOTO_URL(String ROOM_PHOTO_URL) {
        this.ROOM_PHOTO_URL = ROOM_PHOTO_URL;
    }

    public String getDAYTIME() {
        return DAYTIME;
    }

    public void setDAYTIME(String DAYTIME) {
        this.DAYTIME = DAYTIME;
    }

    public String getUSER_PHOTO_URL() {
        return USER_PHOTO_URL;
    }

    public void setUSER_PHOTO_URL(String USER_PHOTO_URL) {
        this.USER_PHOTO_URL = USER_PHOTO_URL;
    }

    public String getAMENITIES_TYPE() {
        return AMENITIES_TYPE;
    }

    public void setAMENITIES_TYPE(String AMENITIES_TYPE) {
        this.AMENITIES_TYPE = AMENITIES_TYPE;
    }

    public float getPRICE() {
        return PRICE;
    }

    public void setPRICE(float PRICE) {
        this.PRICE = PRICE;
    }

    public int getAMENITIES_NO() {
        return AMENITIES_NO;
    }

    public void setAMENITIES_NO(int AMENITIES_NO) {
        this.AMENITIES_NO = AMENITIES_NO;
    }

    public boolean isREAD() {
        return READ;
    }

    public void setREAD(boolean READ) {
        this.READ = READ;
    }
}
