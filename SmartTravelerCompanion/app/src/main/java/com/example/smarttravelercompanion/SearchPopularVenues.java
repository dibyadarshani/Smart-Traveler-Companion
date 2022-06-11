package com.example.smarttravelercompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class SearchPopularVenues extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_popular_venues);
        WebView browser = (WebView) findViewById(R.id.wbv1);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.loadUrl("https://dibyadarshani.github.io/VenueByLocation.html");
    }
}