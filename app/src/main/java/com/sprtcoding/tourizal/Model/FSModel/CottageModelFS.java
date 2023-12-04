package com.sprtcoding.tourizal.Model.FSModel;

public class CottageModelFS {
    String  OWNER_UID,
            RESORT_ID,
            COTTAGE_ID,
            COTTAGE_PIC_ID,
            COTTAGE_PIC_NAME,
            DESCRIPTION,
            COTTAGE_PHOTO_URL;

    float PRICE;
    int COTTAGE_NO;

    public CottageModelFS() {
    }

    public CottageModelFS(String OWNER_UID, String RESORT_ID, String COTTAGE_ID, String COTTAGE_PIC_ID, String COTTAGE_PIC_NAME, int COTTAGE_NO, String DESCRIPTION, String COTTAGE_PHOTO_URL, float PRICE) {
        this.OWNER_UID = OWNER_UID;
        this.RESORT_ID = RESORT_ID;
        this.COTTAGE_ID = COTTAGE_ID;
        this.COTTAGE_PIC_ID = COTTAGE_PIC_ID;
        this.COTTAGE_PIC_NAME = COTTAGE_PIC_NAME;
        this.COTTAGE_NO = COTTAGE_NO;
        this.DESCRIPTION = DESCRIPTION;
        this.COTTAGE_PHOTO_URL = COTTAGE_PHOTO_URL;
        this.PRICE = PRICE;
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

    public String getCOTTAGE_ID() {
        return COTTAGE_ID;
    }

    public void setCOTTAGE_ID(String COTTAGE_ID) {
        this.COTTAGE_ID = COTTAGE_ID;
    }

    public String getCOTTAGE_PIC_ID() {
        return COTTAGE_PIC_ID;
    }

    public void setCOTTAGE_PIC_ID(String COTTAGE_PIC_ID) {
        this.COTTAGE_PIC_ID = COTTAGE_PIC_ID;
    }

    public String getCOTTAGE_PIC_NAME() {
        return COTTAGE_PIC_NAME;
    }

    public void setCOTTAGE_PIC_NAME(String COTTAGE_PIC_NAME) {
        this.COTTAGE_PIC_NAME = COTTAGE_PIC_NAME;
    }

    public int getCOTTAGE_NO() {
        return COTTAGE_NO;
    }

    public void setCOTTAGE_NO(int COTTAGE_NO) {
        this.COTTAGE_NO = COTTAGE_NO;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public float getPRICE() {
        return PRICE;
    }

    public void setPRICE(float PRICE) {
        this.PRICE = PRICE;
    }

    public String getCOTTAGE_PHOTO_URL() {
        return COTTAGE_PHOTO_URL;
    }

    public void setCOTTAGE_PHOTO_URL(String COTTAGE_PHOTO_URL) {
        this.COTTAGE_PHOTO_URL = COTTAGE_PHOTO_URL;
    }
}
