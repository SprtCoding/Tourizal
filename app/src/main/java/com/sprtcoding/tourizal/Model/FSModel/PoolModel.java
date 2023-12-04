package com.sprtcoding.tourizal.Model.FSModel;

public class PoolModel {
    String POOL_IMG_URL, POOL_TYPE, POOL_INFO, POOL_DESC, POOL_PIC_ID, POOL_PIC_NAME, OWNER_UID, RESORT_ID, POOL_ID;
    int POOL_NO;
    double POOL_SIZE;

    public PoolModel() {
    }

    public PoolModel(String POOL_IMG_URL, String POOL_TYPE, String POOL_INFO, String POOL_DESC, String POOL_PIC_ID, String POOL_PIC_NAME, String OWNER_UID, String RESORT_ID, String POOL_ID, double POOL_SIZE, int POOL_NO) {
        this.POOL_IMG_URL = POOL_IMG_URL;
        this.POOL_TYPE = POOL_TYPE;
        this.POOL_INFO = POOL_INFO;
        this.POOL_DESC = POOL_DESC;
        this.POOL_PIC_ID = POOL_PIC_ID;
        this.POOL_PIC_NAME = POOL_PIC_NAME;
        this.OWNER_UID = OWNER_UID;
        this.RESORT_ID = RESORT_ID;
        this.POOL_ID = POOL_ID;
        this.POOL_SIZE = POOL_SIZE;
        this.POOL_NO = POOL_NO;
    }

    public String getPOOL_PIC_ID() {
        return POOL_PIC_ID;
    }

    public void setPOOL_PIC_ID(String POOL_PIC_ID) {
        this.POOL_PIC_ID = POOL_PIC_ID;
    }

    public String getPOOL_PIC_NAME() {
        return POOL_PIC_NAME;
    }

    public void setPOOL_PIC_NAME(String POOL_PIC_NAME) {
        this.POOL_PIC_NAME = POOL_PIC_NAME;
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

    public String getPOOL_ID() {
        return POOL_ID;
    }

    public void setPOOL_ID(String POOL_ID) {
        this.POOL_ID = POOL_ID;
    }

    public String getPOOL_IMG_URL() {
        return POOL_IMG_URL;
    }

    public void setPOOL_IMG_URL(String POOL_IMG_URL) {
        this.POOL_IMG_URL = POOL_IMG_URL;
    }

    public String getPOOL_TYPE() {
        return POOL_TYPE;
    }

    public void setPOOL_TYPE(String POOL_TYPE) {
        this.POOL_TYPE = POOL_TYPE;
    }

    public String getPOOL_INFO() {
        return POOL_INFO;
    }

    public void setPOOL_INFO(String POOL_INFO) {
        this.POOL_INFO = POOL_INFO;
    }

    public String getPOOL_DESC() {
        return POOL_DESC;
    }

    public void setPOOL_DESC(String POOL_DESC) {
        this.POOL_DESC = POOL_DESC;
    }

    public double getPOOL_SIZE() {
        return POOL_SIZE;
    }

    public void setPOOL_SIZE(double POOL_SIZE) {
        this.POOL_SIZE = POOL_SIZE;
    }

    public int getPOOL_NO() {
        return POOL_NO;
    }

    public void setPOOL_NO(int POOL_NO) {
        this.POOL_NO = POOL_NO;
    }
}
