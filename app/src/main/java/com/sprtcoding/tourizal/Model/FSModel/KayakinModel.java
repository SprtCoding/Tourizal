package com.sprtcoding.tourizal.Model.FSModel;

public class KayakinModel {
    String OWNER_UID, RESORT_UID, KAYAKIN_UID, KAYAKIN_PIC_ID, KAYAKIN_PIC_NAME, KAYAKIN_IMG_URL, KAYAKIN_DESC;
    int KAYAKIN_NO;
    float RENT_PRICE;

    public KayakinModel() {
    }

    public KayakinModel(String OWNER_UID, String RESORT_UID, String KAYAKIN_UID, String KAYAKIN_PIC_ID, String KAYAKIN_PIC_NAME, String KAYAKIN_IMG_URL, String KAYAKIN_DESC, int KAYAKIN_NO, float RENT_PRICE) {
        this.OWNER_UID = OWNER_UID;
        this.RESORT_UID = RESORT_UID;
        this.KAYAKIN_UID = KAYAKIN_UID;
        this.KAYAKIN_PIC_ID = KAYAKIN_PIC_ID;
        this.KAYAKIN_PIC_NAME = KAYAKIN_PIC_NAME;
        this.KAYAKIN_IMG_URL = KAYAKIN_IMG_URL;
        this.KAYAKIN_DESC = KAYAKIN_DESC;
        this.KAYAKIN_NO = KAYAKIN_NO;
        this.RENT_PRICE = RENT_PRICE;
    }

    public String getOWNER_UID() {
        return OWNER_UID;
    }

    public void setOWNER_UID(String OWNER_UID) {
        this.OWNER_UID = OWNER_UID;
    }

    public String getRESORT_UID() {
        return RESORT_UID;
    }

    public void setRESORT_UID(String RESORT_UID) {
        this.RESORT_UID = RESORT_UID;
    }

    public String getKAYAKIN_UID() {
        return KAYAKIN_UID;
    }

    public void setKAYAKIN_UID(String KAYAKIN_UID) {
        this.KAYAKIN_UID = KAYAKIN_UID;
    }

    public String getKAYAKIN_PIC_ID() {
        return KAYAKIN_PIC_ID;
    }

    public void setKAYAKIN_PIC_ID(String KAYAKIN_PIC_ID) {
        this.KAYAKIN_PIC_ID = KAYAKIN_PIC_ID;
    }

    public String getKAYAKIN_PIC_NAME() {
        return KAYAKIN_PIC_NAME;
    }

    public void setKAYAKIN_PIC_NAME(String KAYAKIN_PIC_NAME) {
        this.KAYAKIN_PIC_NAME = KAYAKIN_PIC_NAME;
    }

    public String getKAYAKIN_IMG_URL() {
        return KAYAKIN_IMG_URL;
    }

    public void setKAYAKIN_IMG_URL(String KAYAKIN_IMG_URL) {
        this.KAYAKIN_IMG_URL = KAYAKIN_IMG_URL;
    }

    public String getKAYAKIN_DESC() {
        return KAYAKIN_DESC;
    }

    public void setKAYAKIN_DESC(String KAYAKIN_DESC) {
        this.KAYAKIN_DESC = KAYAKIN_DESC;
    }

    public int getKAYAKIN_NO() {
        return KAYAKIN_NO;
    }

    public void setKAYAKIN_NO(int KAYAKIN_NO) {
        this.KAYAKIN_NO = KAYAKIN_NO;
    }

    public float getRENT_PRICE() {
        return RENT_PRICE;
    }

    public void setRENT_PRICE(float RENT_PRICE) {
        this.RENT_PRICE = RENT_PRICE;
    }
}
