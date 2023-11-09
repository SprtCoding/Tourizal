package com.sprtcoding.tourizal.Model;

public class RoomsModel {
    String ResortsID, RoomsID, RoomPicID, RoomPicName, RoomNumber, Description, DayAvailability, DayPrice, NightAvailability, NightPrice, RoomsPhotoURL;

    public RoomsModel() {

    }

    public RoomsModel(String resortsID, String roomsID, String roomPicID, String roomPicName, String roomNumber, String description, String dayAvailability, String dayPrice, String nightAvailability, String nightPrice, String roomsPhotoURL) {
        ResortsID = resortsID;
        RoomsID = roomsID;
        RoomPicID = roomPicID;
        RoomPicName = roomPicName;
        RoomNumber = roomNumber;
        Description = description;
        DayAvailability = dayAvailability;
        DayPrice = dayPrice;
        NightAvailability = nightAvailability;
        NightPrice = nightPrice;
        RoomsPhotoURL = roomsPhotoURL;
    }

    public String getRoomPicID() {
        return RoomPicID;
    }

    public void setRoomPicID(String roomPicID) {
        RoomPicID = roomPicID;
    }

    public String getRoomPicName() {
        return RoomPicName;
    }

    public void setRoomPicName(String roomPicName) {
        RoomPicName = roomPicName;
    }

    public String getResortsID() {
        return ResortsID;
    }

    public void setResortsID(String resortsID) {
        ResortsID = resortsID;
    }

    public String getRoomsID() {
        return RoomsID;
    }

    public void setRoomsID(String roomsID) {
        RoomsID = roomsID;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        RoomNumber = roomNumber;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDayAvailability() {
        return DayAvailability;
    }

    public void setDayAvailability(String dayAvailability) {
        DayAvailability = dayAvailability;
    }

    public String getDayPrice() {
        return DayPrice;
    }

    public void setDayPrice(String dayPrice) {
        DayPrice = dayPrice;
    }

    public String getNightAvailability() {
        return NightAvailability;
    }

    public void setNightAvailability(String nightAvailability) {
        NightAvailability = nightAvailability;
    }

    public String getNightPrice() {
        return NightPrice;
    }

    public void setNightPrice(String nightPrice) {
        NightPrice = nightPrice;
    }

    public String getRoomsPhotoURL() {
        return RoomsPhotoURL;
    }

    public void setRoomsPhotoURL(String roomsPhotoURL) {
        RoomsPhotoURL = roomsPhotoURL;
    }
}
