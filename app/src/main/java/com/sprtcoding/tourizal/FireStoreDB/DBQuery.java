package com.sprtcoding.tourizal.FireStoreDB;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.guieffect.qual.UI;

import java.util.HashMap;
import java.util.Map;

public class DBQuery {
    public static FirebaseFirestore g_firestore;
    public static String g_resort_id;

    public static void createUserData(String email, String name, String id, String accountType, String dob, int age,
                                      String gender, MyCompleteListener myCompleteListener) {
        Map<String, Object> userData = new HashMap<>();

        userData.put("USER_ID", id);
        userData.put("EMAIL_ID", email);
        userData.put("NAME", name);
        userData.put("ACCOUNT_TYPE", accountType);
        userData.put("DATE_OF_BIRTH", dob);
        userData.put("AGE", age);
        userData.put("GENDER", gender);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userDoc.set(userData)
                .addOnSuccessListener(unused -> {
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(e -> {
                    myCompleteListener.onFailure(e);
                });
    }

    public static void setResort(String UID, String ResortPicURL, String ResortEntranceFee,
                                 String ResortPicName, String ResortName, String Owner, String ResortLocation,
                                 double Lat, double Lng, String TimePosted, String DatePosted , MyCompleteListener myCompleteListener) {
        Map<String, Object> resortData = new HashMap<>();

        // Add data to Firestore with an auto-generated document ID
        DocumentReference newDocRef = g_firestore.collection("RESORTS").document();

        resortData.put("OWNER_UID", UID);
        resortData.put("RESORT_ID", newDocRef.getId());
        resortData.put("RESORT_PIC_URL", ResortPicURL);
        resortData.put("RESORT_ENTRANCE_FEE", ResortEntranceFee);
        resortData.put("RESORT_PIC_NAME", ResortPicName);
        resortData.put("RESORT_NAME", ResortName);
        resortData.put("OWNER_NAME", Owner);
        resortData.put("LOCATION", ResortLocation);
        resortData.put("LAT", Lat);
        resortData.put("LNG", Lng);
        resortData.put("TIME", TimePosted);
        resortData.put("DATE", DatePosted);

        newDocRef.set(resortData)
                .addOnSuccessListener(documentReference -> {
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(e -> {
                    myCompleteListener.onFailure(e);
                });
    }

    public static void setRooms(String UID,
                                String ResortID,
                                String RoomID,
                                String RoomPicID,
                                String RoomPicName,
                                int RoomNumber,
                                String Description,
                                String DayAvailability,
                                float DayPrice,
                                String NightAvailability,
                                float NightPrice,
                                String RoomsPhotoURL,
                                MyCompleteListener myCompleteListener) {
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("OWNER_UID", UID);
        roomData.put("RESORT_ID", ResortID);
        roomData.put("ROOM_ID", RoomID);
        roomData.put("ROOM_PIC_ID", RoomPicID);
        roomData.put("ROOM_PIC_NAME", RoomPicName);
        roomData.put("ROOM_NO", RoomNumber);
        roomData.put("DESCRIPTION", Description);
        roomData.put("DAY_AVAILABILITY", DayAvailability);
        roomData.put("DAY_PRICE", DayPrice);
        roomData.put("NIGHT_AVAILABILITY", NightAvailability);
        roomData.put("NIGHT_PRICE", NightPrice);
        roomData.put("ROOM_PHOTO_URL", RoomsPhotoURL);

        DocumentReference resortDoc = g_firestore
                .collection("RESORTS")
                .document(ResortID)
                .collection("ROOMS")
                .document(RoomID);

        resortDoc.set(roomData)
                .addOnSuccessListener(unused -> {
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(e -> {
                    myCompleteListener.onFailure(e);
                });
    }

    public static void setCottage(String UID,
                                String ResortID,
                                String CottageID,
                                String CottagePicID,
                                String CottagePicName,
                                  int CottageNumber,
                                String Description,
                                float Price,
                                String CottagePhotoURL,
                                MyCompleteListener myCompleteListener) {
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("OWNER_UID", UID);
        roomData.put("RESORT_ID", ResortID);
        roomData.put("COTTAGE_ID", CottageID);
        roomData.put("COTTAGE_PIC_ID", CottagePicID);
        roomData.put("COTTAGE_PIC_NAME", CottagePicName);
        roomData.put("COTTAGE_NO", CottageNumber);
        roomData.put("DESCRIPTION", Description);
        roomData.put("PRICE", Price);
        roomData.put("COTTAGE_PHOTO_URL", CottagePhotoURL);

        DocumentReference resortDoc = g_firestore
                .collection("RESORTS")
                .document(ResortID)
                .collection("COTTAGE")
                .document(CottageID);

        resortDoc.set(roomData)
                .addOnSuccessListener(unused -> {
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(e -> {
                    myCompleteListener.onFailure(e);
                });
    }

    public static void setFeatured(String UID,
                                    String ResortID,
                                    String FeaturedID,
                                    String FeaturedTitle,
                                    String FeaturedPicID,
                                    String FeaturedPicName,
                                    String ResortName,
                                    String OwnerName,
                                    String Location,
                                    String Lat,
                                    String Lng,
                                    String Time,
                                    String Date,
                                    String FeaturedPhotoURL,
                                    MyCompleteListener myCompleteListener) {
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("OWNER_UID", UID);
        roomData.put("RESORT_ID", ResortID);
        roomData.put("FEATURED_ID", FeaturedID);
        roomData.put("FEATURED_TITLE", FeaturedTitle);
        roomData.put("FEATURED_PHOTO_ID", FeaturedPicID);
        roomData.put("FEATURED_PHOTO_NAME", FeaturedPicName);
        roomData.put("RESORT_NAME", ResortName);
        roomData.put("OWNER_NAME", OwnerName);
        roomData.put("LOCATION", Location);
        roomData.put("LAT", Lat);
        roomData.put("LNG", Lng);
        roomData.put("TIME", Time);
        roomData.put("DATE", Date);
        roomData.put("FEATURED_PHOTO_URL", FeaturedPhotoURL);

        DocumentReference resortDoc = g_firestore
                .collection("RESORTS")
                .document(ResortID)
                .collection("FEATURED")
                .document(FeaturedID);

        resortDoc.set(roomData)
                .addOnSuccessListener(unused -> {
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(e -> {
                    myCompleteListener.onFailure(e);
                });
    }

    public static void setFeaturedAll(String UID,
                                   String ResortID,
                                   String FeaturedID,
                                   String FeaturedTitle,
                                   String FeaturedPicID,
                                   String FeaturedPicName,
                                   String ResortName,
                                   String OwnerName,
                                   String Location,
                                   String Lat,
                                   String Lng,
                                   String Time,
                                   String Date,
                                   String FeaturedPhotoURL) {
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("OWNER_UID", UID);
        roomData.put("RESORT_ID", ResortID);
        roomData.put("FEATURED_ID", FeaturedID);
        roomData.put("FEATURED_TITLE", FeaturedTitle);
        roomData.put("FEATURED_PHOTO_ID", FeaturedPicID);
        roomData.put("FEATURED_PHOTO_NAME", FeaturedPicName);
        roomData.put("RESORT_NAME", ResortName);
        roomData.put("OWNER_NAME", OwnerName);
        roomData.put("LOCATION", Location);
        roomData.put("LAT", Lat);
        roomData.put("LNG", Lng);
        roomData.put("TIME", Time);
        roomData.put("DATE", Date);
        roomData.put("FEATURED_PHOTO_URL", FeaturedPhotoURL);

        DocumentReference resortDoc = g_firestore
                .collection("FEATURED")
                .document(FeaturedID);

        resortDoc.set(roomData);
    }

    public static void setReservationRoomUser(String UID,
                                          String MY_ID,
                                          String AmenitiesType,
                                          boolean isRemoved,
                                          String ResortID,
                                          String AmenitiesID,
                                          String ReservedID,
                                          int AmenitiesNo,
                                          String NameOfUser,
                                          String ContactOfUser,
                                          String LocationOfUser,
                                          String DateOfReservation,
                                          String Time,
                                          String Date,
                                              String Status,
                                              String DayNight,
                                              float Price,
                                          boolean isRead,
                                          String userPic,
                                          String RoomPhotoURL,
                                              MyCompleteListener myCompleteListener) {
        Map<String, Object> reserveRoomData = new HashMap<>();
        reserveRoomData.put("OWNER_UID", UID);
        reserveRoomData.put("MY_UID", MY_ID);
        reserveRoomData.put("AMENITIES_TYPE", AmenitiesType);
        reserveRoomData.put("REMOVED", isRemoved);
        reserveRoomData.put("RESORT_ID", ResortID);
        reserveRoomData.put("AMENITIES_ID", AmenitiesID);
        reserveRoomData.put("RESERVED_ID", ReservedID);
        reserveRoomData.put("AMENITIES_NO", AmenitiesNo);
        reserveRoomData.put("NAME_OF_USER", NameOfUser);
        reserveRoomData.put("CONTACT_OF_USER", ContactOfUser);
        reserveRoomData.put("LOCATION_OF_USER", LocationOfUser);
        reserveRoomData.put("DATE_RESERVATION", DateOfReservation);
        reserveRoomData.put("TIME", Time);
        reserveRoomData.put("DATE", Date);
        reserveRoomData.put("STATUS", Status);
        reserveRoomData.put("DAYTIME", DayNight);
        reserveRoomData.put("PRICE", Price);
        reserveRoomData.put("READ", isRead);
        reserveRoomData.put("USER_PHOTO_URL", userPic);
        reserveRoomData.put("ROOM_PHOTO_URL", RoomPhotoURL);

        DocumentReference reservedDoc = g_firestore
                .collection("RESERVATION")
                .document(ReservedID);

        reservedDoc.set(reserveRoomData)
                .addOnSuccessListener(unused -> myCompleteListener.onSuccess())
                .addOnFailureListener(myCompleteListener::onFailure);
    }

    public static void updateIsReadAll(boolean isRead, String ReservedID,
                                    MyCompleteListener myCompleteListener) {
        Map<String, Object> reserveRoomData = new HashMap<>();
        reserveRoomData.put("READ", isRead);

        DocumentReference reservedDoc = g_firestore
                .collection("RESERVATION")
                .document(ReservedID);

        reservedDoc.update(reserveRoomData).addOnCompleteListener(task -> myCompleteListener.onSuccess())
                .addOnFailureListener(myCompleteListener::onFailure);
    }

    public static void updateIsRead(boolean isRead, String ReservedID, String ResortID) {
        Map<String, Object> reserveRoomData = new HashMap<>();
        reserveRoomData.put("READ", isRead);

        DocumentReference reservedDoc = g_firestore
                .collection("RESORTS")
                .document(ResortID)
                .collection("RESERVATION")
                .document(ReservedID);

        reservedDoc.update(reserveRoomData);
    }

    public static void cancelReservation(String ReservedID, String ResortID) {

        DocumentReference reservedDoc = g_firestore
                .collection("RESORTS")
                .document(ResortID)
                .collection("RESERVATION")
                .document(ReservedID);

        reservedDoc.delete();
    }

    public static void cancelReservationAll(String ReservedID,
                                            MyCompleteListener myCompleteListener) {

        DocumentReference reservedDoc = g_firestore
                .collection("RESERVATION")
                .document(ReservedID);

        reservedDoc.delete().addOnSuccessListener(unused -> myCompleteListener.onSuccess()).addOnFailureListener(myCompleteListener::onFailure);
    }

    public static void setRatings(String ResortID, String UserID, String OwnerID, String DatePosted, String NameOfUser, double Ratings, String Comments, String TimePosted,
                                            MyCompleteListener myCompleteListener) {

        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("RESORT_ID", ResortID);
        ratingData.put("USER_ID", UserID);
        ratingData.put("OWNER_ID", OwnerID);
        ratingData.put("DATE_POSTED", DatePosted);
        ratingData.put("TIME_POSTED", TimePosted);
        ratingData.put("NAME_OF_USER", NameOfUser);
        ratingData.put("RATINGS", Ratings);
        ratingData.put("COMMENTS", Comments);

        DocumentReference reservedDoc = g_firestore
                .collection("RESORTS")
                .document(ResortID)
                .collection("RATINGS")
                .document(UserID);

        reservedDoc.set(ratingData).addOnSuccessListener(unused -> myCompleteListener.onSuccess()).addOnFailureListener(myCompleteListener::onFailure);
    }
}
