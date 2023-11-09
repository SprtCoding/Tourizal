package com.sprtcoding.tourizal.Model;

public class CottageModel {
    String ResortsID, CottageID, CottagePicID, Description, Price, CottagePhotoURL, CottagePicName;
    int CottageNumber;

    public CottageModel() {
    }

    public CottageModel(String resortsID, String cottageID, String cottagePicID, int cottageNumber, String description, String price, String cottagePhotoURL, String cottagePicName) {
        ResortsID = resortsID;
        CottageID = cottageID;
        CottagePicID = cottagePicID;
        CottageNumber = cottageNumber;
        Description = description;
        Price = price;
        CottagePhotoURL = cottagePhotoURL;
        CottagePicName = cottagePicName;
    }

    public String getCottagePicName() {
        return CottagePicName;
    }

    public void setCottagePicName(String cottagePicName) {
        CottagePicName = cottagePicName;
    }

    public String getCottagePicID() {
        return CottagePicID;
    }

    public void setCottagePicID(String cottagePicID) {
        CottagePicID = cottagePicID;
    }

    public String getResortsID() {
        return ResortsID;
    }

    public void setResortsID(String resortsID) {
        ResortsID = resortsID;
    }

    public String getCottageID() {
        return CottageID;
    }

    public void setCottageID(String cottageID) {
        CottageID = cottageID;
    }

    public int getCottageNumber() {
        return CottageNumber;
    }

    public void setCottageNumber(int cottageNumber) {
        CottageNumber = cottageNumber;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCottagePhotoURL() {
        return CottagePhotoURL;
    }

    public void setCottagePhotoURL(String cottagePhotoURL) {
        CottagePhotoURL = cottagePhotoURL;
    }
}
