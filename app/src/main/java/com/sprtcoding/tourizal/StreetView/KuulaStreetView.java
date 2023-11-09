package com.sprtcoding.tourizal.StreetView;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sprtcoding.tourizal.R;

import java.util.Locale;

public class KuulaStreetView extends AppCompatActivity {
    private String locationName;
    private WebView webView;

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuula_street_view);
        webView = findViewById(R.id.webView);

        locationName = getIntent().getStringExtra("resort_name");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        if(locationName.equals("X's Place 2") || locationName.equals("x's place 2")) {
            webView.loadUrl("https://kuula.co/share/collection/7JCNF?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else if (locationName.equals("Sunrise Farm Resort") || locationName.equals("sunrise farm resort")) {
            webView.loadUrl("https://kuula.co/share/collection/7JCN1?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else if (locationName.equals("Elsa Panganiban Resort") || locationName.equals("elsa panganiban resort")) {
            webView.loadUrl("https://kuula.co/share/collection/7JCNW?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else if (locationName.equals("Jacob's Well Resort") || locationName.equals("jacob's well resort")) {
            webView.loadUrl("https://kuula.co/share/collection/7JC5M?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else if (locationName.equals("Caniwal Private Resort") || locationName.equals("caniwal private resort")) {
            webView.loadUrl("https://kuula.co/share/collection/7JghY?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else if (locationName.equals("Herederos Farm & Resort") || locationName.equals("herederos farm & resort")) {
            webView.loadUrl("https://kuula.co/share/collection/7Js9K?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else if (locationName.equals("Ding And Mhel Argel Farm & Resort") || locationName.equals("ding and mhel argel farm & resort")) {
            webView.loadUrl("https://kuula.co/share/collection/7X0YH?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else if (locationName.equals("Alp's Farm Resort") || locationName.equals("alp's farm resort")) {
            webView.loadUrl("https://kuula.co/share/collection/7X0YV?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else if (locationName.equals("Biggy's River Resort") || locationName.equals("biggy's river resort")) {
            webView.loadUrl("https://kuula.co/share/collection/7X0Y3?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }else {
            Toast.makeText(this, "Not found!", Toast.LENGTH_SHORT).show();
            webView.loadUrl("https://kuula.co/share/collection/7J?logo=1&info=1&fs=1&vr=0&thumbs=0&keys=0");
        }
    }
}