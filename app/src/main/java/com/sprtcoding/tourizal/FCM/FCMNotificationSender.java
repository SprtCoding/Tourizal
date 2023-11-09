package com.sprtcoding.tourizal.FCM;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMNotificationSender {
    private static final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private static final String fcmServerKey = "key=AAAAs37sKzE:APA91bGESecIrVe1RUCVWvUIuRrlrB31rjB4OlANCLZU94ie3Ux2w8DhT6374IZyXMO-cKTiDiDhe-tzr45rkA-6-8llMGH90XheCf9mTRA7FEHCa2iKfGXgiFTprAPLSvV9XJz03Rn5";

    public static void sendNotification(Context context, String token, String title, String body) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", token);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            mainObj.put("notification", notiObject);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    postUrl,
                    mainObj,
                    response -> {

                    }, error -> {

            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", fcmServerKey);
                    return map;
                }
            };

            requestQueue.add(jsonObjectRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
