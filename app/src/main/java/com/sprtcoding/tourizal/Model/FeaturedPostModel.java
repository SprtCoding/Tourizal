package com.sprtcoding.tourizal.Model;

public class FeaturedPostModel {
    String UID, ThumbnailID, ThumbnailPhoto, FeaturedTitle, TimePosted, DatePosted, OwnerName, Location, ResortName, Lat, Lon, FeaturedPhotoID, FeaturedPhotoName;

    public FeaturedPostModel() {
    }

    public FeaturedPostModel(String UID, String thumbnailID, String thumbnailPhoto, String featuredTitle, String timePosted, String datePosted, String ownerName, String location, String resortName, String lat, String lon, String featuredPhotoID, String featuredPhotoName) {
        this.UID = UID;
        ThumbnailID = thumbnailID;
        ThumbnailPhoto = thumbnailPhoto;
        FeaturedTitle = featuredTitle;
        TimePosted = timePosted;
        DatePosted = datePosted;
        OwnerName = ownerName;
        Location = location;
        ResortName = resortName;
        Lat = lat;
        Lon = lon;
        FeaturedPhotoID = featuredPhotoID;
        FeaturedPhotoName = featuredPhotoName;
    }

    public String getFeaturedPhotoID() {
        return FeaturedPhotoID;
    }

    public void setFeaturedPhotoID(String featuredPhotoID) {
        FeaturedPhotoID = featuredPhotoID;
    }

    public String getFeaturedPhotoName() {
        return FeaturedPhotoName;
    }

    public void setFeaturedPhotoName(String featuredPhotoName) {
        FeaturedPhotoName = featuredPhotoName;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLon() {
        return Lon;
    }

    public void setLon(String lon) {
        Lon = lon;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getThumbnailID() {
        return ThumbnailID;
    }

    public void setThumbnailID(String thumbnailID) {
        ThumbnailID = thumbnailID;
    }

    public String getThumbnailPhoto() {
        return ThumbnailPhoto;
    }

    public void setThumbnailPhoto(String thumbnailPhoto) {
        ThumbnailPhoto = thumbnailPhoto;
    }

    public String getFeaturedTitle() {
        return FeaturedTitle;
    }

    public void setFeaturedTitle(String featuredTitle) {
        FeaturedTitle = featuredTitle;
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

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getResortName() {
        return ResortName;
    }

    public void setResortName(String resortName) {
        ResortName = resortName;
    }
}
