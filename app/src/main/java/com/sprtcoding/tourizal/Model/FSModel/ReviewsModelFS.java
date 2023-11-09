package com.sprtcoding.tourizal.Model.FSModel;

public class ReviewsModelFS {
    String RESORT_ID, USER_ID, OWNER_ID, DATE_POSTED, TIME_POSTED, NAME_OF_USER, COMMENTS;
    double RATINGS;

    public ReviewsModelFS() {
    }

    public ReviewsModelFS(String RESORT_ID, String USER_ID, String OWNER_ID, String DATE_POSTED, String TIME_POSTED, String NAME_OF_USER, String COMMENTS, double RATINGS) {
        this.RESORT_ID = RESORT_ID;
        this.USER_ID = USER_ID;
        this.OWNER_ID = OWNER_ID;
        this.DATE_POSTED = DATE_POSTED;
        this.TIME_POSTED = TIME_POSTED;
        this.NAME_OF_USER = NAME_OF_USER;
        this.COMMENTS = COMMENTS;
        this.RATINGS = RATINGS;
    }

    public String getRESORT_ID() {
        return RESORT_ID;
    }

    public void setRESORT_ID(String RESORT_ID) {
        this.RESORT_ID = RESORT_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getOWNER_ID() {
        return OWNER_ID;
    }

    public void setOWNER_ID(String OWNER_ID) {
        this.OWNER_ID = OWNER_ID;
    }

    public String getDATE_POSTED() {
        return DATE_POSTED;
    }

    public void setDATE_POSTED(String DATE_POSTED) {
        this.DATE_POSTED = DATE_POSTED;
    }

    public String getTIME_POSTED() {
        return TIME_POSTED;
    }

    public void setTIME_POSTED(String TIME_POSTED) {
        this.TIME_POSTED = TIME_POSTED;
    }

    public String getNAME_OF_USER() {
        return NAME_OF_USER;
    }

    public void setNAME_OF_USER(String NAME_OF_USER) {
        this.NAME_OF_USER = NAME_OF_USER;
    }

    public String getCOMMENTS() {
        return COMMENTS;
    }

    public void setCOMMENTS(String COMMENTS) {
        this.COMMENTS = COMMENTS;
    }

    public double getRATINGS() {
        return RATINGS;
    }

    public void setRATINGS(double RATINGS) {
        this.RATINGS = RATINGS;
    }
}
