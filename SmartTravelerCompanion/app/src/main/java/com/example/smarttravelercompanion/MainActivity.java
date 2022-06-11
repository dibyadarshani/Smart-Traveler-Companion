package com.example.smarttravelercompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        sharedpreferences = getSharedPreferences("loginID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        Intent i=new Intent(getApplicationContext(),login.class);
        startActivity(i);
    }

    public void goToPopularDest(View view) {
        Intent i=new Intent(getApplicationContext(),viewPopDest.class);
        startActivity(i);
    }

    public void goToDestRecc(View view) {
        Intent i=new Intent(getApplicationContext(),DestRecc.class);
        startActivity(i);
    }

    public void goToCustTrip(View view) {
        Intent i=new Intent(getApplicationContext(),createCustTrip.class);
        startActivity(i);
    }

    public void goToIQ(View view) {
        Intent i=new Intent(getApplicationContext(),IQuestionnaire.class);
        startActivity(i);
    }

    public void goToDash(View view) {
        Intent i=new Intent(getApplicationContext(),Dashboard.class);
        startActivity(i);
    }

    public void searchPopPlaces(View view) {
        Intent i=new Intent(getApplicationContext(),SearchPopularVenues.class);
        startActivity(i);
    }

    public void searchNearbyPlaces(View view) {
        Intent i=new Intent(getApplicationContext(),SearchNearbyVenues.class);
        startActivity(i);
    }
}