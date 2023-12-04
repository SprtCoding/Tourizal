package com.sprtcoding.tourizal.Model.FSModel;

import java.util.ArrayList;

public class RoomFSModel {
    String OWNER_UID, RESORT_ID, ROOM_ID, DESCRIPTION, DAY_AVAILABILITY, NIGHT_AVAILABILITY;
    ArrayList<String> ROOM_PHOTO_URL;
    int ROOM_NO, MAX_DAY, MAX_NIGHT;
    float DAY_PRICE, NIGHT_PRICE, DAY_EXCESS, NIGHT_EXCESS;

    public RoomFSModel() {
    }

    public RoomFSModel(String OWNER_UID, String RESORT_ID, String ROOM_ID, String DESCRIPTION, String DAY_AVAILABILITY, String NIGHT_AVAILABILITY, ArrayList<String> ROOM_PHOTO_URL, int ROOM_NO, int MAX_DAY, int MAX_NIGHT, float DAY_PRICE, float NIGHT_PRICE, float DAY_EXCESS, float NIGHT_EXCESS) {
        this.OWNER_UID = OWNER_UID;
        this.RESORT_ID = RESORT_ID;
        this.ROOM_ID = ROOM_ID;
        this.DESCRIPTION = DESCRIPTION;
        this.DAY_AVAILABILITY = DAY_AVAILABILITY;
        this.NIGHT_AVAILABILITY = NIGHT_AVAILABILITY;
        this.ROOM_PHOTO_URL = ROOM_PHOTO_URL;
        this.ROOM_NO = ROOM_NO;
        this.MAX_DAY = MAX_DAY;
        this.MAX_NIGHT = MAX_NIGHT;
        this.DAY_PRICE = DAY_PRICE;
        this.NIGHT_PRICE = NIGHT_PRICE;
        this.DAY_EXCESS = DAY_EXCESS;
        this.NIGHT_EXCESS = NIGHT_EXCESS;
    }

    public int getMAX_DAY() {
        return MAX_DAY;
    }

    public void setMAX_DAY(int MAX_DAY) {
        this.MAX_DAY = MAX_DAY;
    }

    public int getMAX_NIGHT() {
        return MAX_NIGHT;
    }

    public void setMAX_NIGHT(int MAX_NIGHT) {
        this.MAX_NIGHT = MAX_NIGHT;
    }

    public float getDAY_EXCESS() {
        return DAY_EXCESS;
    }

    public void setDAY_EXCESS(float DAY_EXCESS) {
        this.DAY_EXCESS = DAY_EXCESS;
    }

    public float getNIGHT_EXCESS() {
        return NIGHT_EXCESS;
    }

    public void setNIGHT_EXCESS(float NIGHT_EXCESS) {
        this.NIGHT_EXCESS = NIGHT_EXCESS;
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

    public ArrayList<String> getROOM_PHOTO_URL() {
        return ROOM_PHOTO_URL;
    }

    public void setROOM_PHOTO_URL(ArrayList<String> ROOM_PHOTO_URL) {
        this.ROOM_PHOTO_URL = ROOM_PHOTO_URL;
    }
}
