package com.sprtcoding.tourizal.Model;

public class ReviewModel {
    String UID, ResortID, Ratings, Review, NameOfReviewer, TimePosted, DatePosted;

    public ReviewModel() {
    }

    public ReviewModel(String UID, String resortID, String ratings, String review, String nameOfReviewer, String timePosted, String datePosted) {
        this.UID = UID;
        ResortID = resortID;
        Ratings = ratings;
        Review = review;
        NameOfReviewer = nameOfReviewer;
        TimePosted = timePosted;
        DatePosted = datePosted;
    }

    public String getResortID() {
        return ResortID;
    }

    public void setResortID(String resortID) {
        ResortID = resortID;
    }

    public String getNameOfReviewer() {
        return NameOfReviewer;
    }

    public void setNameOfReviewer(String nameOfReviewer) {
        NameOfReviewer = nameOfReviewer;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getRatings() {
        return Ratings;
    }

    public void setRatings(String ratings) {
        Ratings = ratings;
    }

    public String getReview() {
        return Review;
    }

    public void setReview(String review) {
        Review = review;
    }

    public String getTimePosted() {
        return TimePosted;
    }

    public void setTimePosted(String timePosted) {
        TimePosted = timePosted;
    }

    public String getDatePosted() {
        return DatePosted;
    }

    public void setDatePosted(String datePosted) {
        DatePosted = datePosted;
    }
}
