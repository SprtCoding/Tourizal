package com.sprtcoding.tourizal.Model;

public class ResortsModel {
    String UID, ResortID, ResortName, ResortLocation, ResortEntranceFee, Owner, ResortPicURL, TimePosted, DatePosted, ResortPicName, Lat, Lng;

    public ResortsModel() {
    }

    public ResortsModel(String UID, String resortID, String resortName, String resortLocation, String resortEntranceFee, String owner, String resortPicURL, String timePosted, String datePosted, String resortPicName, String lat, String lng) {
        this.UID = UID;
        ResortID = resortID;
        ResortName = resortName;
        ResortLocation = resortLocation;
        ResortEntranceFee = resortEntranceFee;
        Owner = owner;
        ResortPicURL = resortPicURL;
        TimePosted = timePosted;
        DatePosted = datePosted;
        ResortPicName = resortPicName;
        Lat = lat;
        Lng = lng;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getResortPicName() {
        return ResortPicName;
    }

    public void setResortPicName(String resortPicName) {
        ResortPicName = resortPicName;
    }

    public String getResortID() {
        return ResortID;
    }

    public void setResortID(String resortID) {
        ResortID = resortID;
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

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getResortName() {
        return ResortName;
    }

    public void setResortName(String resortName) {
        ResortName = resortName;
    }

    public String getResortLocation() {
        return ResortLocation;
    }

    public void setResortLocation(String resortLocation) {
        ResortLocation = resortLocation;
    }

    public String getResortEntranceFee() {
        return ResortEntranceFee;
    }

    public void setResortEntranceFee(String resortEntranceFee) {
        ResortEntranceFee = resortEntranceFee;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getResortPicURL() {
        return ResortPicURL;
    }

    public void setResortPicURL(String resortPicURL) {
        ResortPicURL = resortPicURL;
    }
}
