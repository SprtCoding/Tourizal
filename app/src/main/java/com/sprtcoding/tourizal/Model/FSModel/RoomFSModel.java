package com.sprtcoding.tourizal.Model.FSModel;

public class RoomFSModel {
    String OWNER_UID, RESORT_ID, ROOM_ID, ROOM_PIC_ID, ROOM_PIC_NAME, DESCRIPTION, DAY_AVAILABILITY, NIGHT_AVAILABILITY, ROOM_PHOTO_URL;
    int ROOM_NO;
    float DAY_PRICE, NIGHT_PRICE;

    public RoomFSModel() {
    }

    public RoomFSModel(String OWNER_UID, String RESORT_ID, String ROOM_ID, String ROOM_PIC_ID, String ROOM_PIC_NAME, String DESCRIPTION, String DAY_AVAILABILITY, String NIGHT_AVAILABILITY, String ROOM_PHOTO_URL, int ROOM_NO, float DAY_PRICE, float NIGHT_PRICE) {
        this.OWNER_UID = OWNER_UID;
        this.RESORT_ID = RESORT_ID;
        this.ROOM_ID = ROOM_ID;
        this.ROOM_PIC_ID = ROOM_PIC_ID;
        this.ROOM_PIC_NAME = ROOM_PIC_NAME;
        this.DESCRIPTION = DESCRIPTION;
        this.DAY_AVAILABILITY = DAY_AVAILABILITY;
        this.NIGHT_AVAILABILITY = NIGHT_AVAILABILITY;
        this.ROOM_PHOTO_URL = ROOM_PHOTO_URL;
        this.ROOM_NO = ROOM_NO;
        this.DAY_PRICE = DAY_PRICE;
        this.NIGHT_PRICE = NIGHT_PRICE;
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

    public String getROOM_ID() {
        return ROOM_ID;
    }

    public void setROOM_ID(String ROOM_ID) {
        this.ROOM_ID = ROOM_ID;
    }

    public String getROOM_PIC_ID() {
        return ROOM_PIC_ID;
    }

    public void setROOM_PIC_ID(String ROOM_PIC_ID) {
        this.ROOM_PIC_ID = ROOM_PIC_ID;
    }

    public String getROOM_PIC_NAME() {
        return ROOM_PIC_NAME;
    }

    public void setROOM_PIC_NAME(String ROOM_PIC_NAME) {
        this.ROOM_PIC_NAME = ROOM_PIC_NAME;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getDAY_AVAILABILITY() {
        return DAY_AVAILABILITY;
    }

    public void setDAY_AVAILABILITY(String DAY_AVAILABILITY) {
        this.DAY_AVAILABILITY = DAY_AVAILABILITY;
    }

    public String getNIGHT_AVAILABILITY() {
        return NIGHT_AVAILABILITY;
    }

    public void setNIGHT_AVAILABILITY(String NIGHT_AVAILABILITY) {
        this.NIGHT_AVAILABILITY = NIGHT_AVAILABILITY;
    }

    public String getROOM_PHOTO_URL() {
        return ROOM_PHOTO_URL;
    }

    public void setROOM_PHOTO_URL(String ROOM_PHOTO_URL) {
        this.ROOM_PHOTO_URL = ROOM_PHOTO_URL;
    }

    public int getROOM_NO() {
        return ROOM_NO;
    }

    public void setROOM_NO(int ROOM_NO) {
        this.ROOM_NO = ROOM_NO;
    }

    public float getDAY_PRICE() {
        return DAY_PRICE;
    }

    public void setDAY_PRICE(float DAY_PRICE) {
        this.DAY_PRICE = DAY_PRICE;
    }

    public float getNIGHT_PRICE() {
        return NIGHT_PRICE;
    }

    public void setNIGHT_PRICE(float NIGHT_PRICE) {
        this.NIGHT_PRICE = NIGHT_PRICE;
    }
}
