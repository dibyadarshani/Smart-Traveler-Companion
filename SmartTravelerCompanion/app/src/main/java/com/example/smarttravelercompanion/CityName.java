package com.example.smarttravelercompanion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

public class CityName extends ListFragment {

    ArrayList<String> itemname = new ArrayList<String>();
    ArrayList<String> imgid = new ArrayList<String>();
    String basepath="https://storage.googleapis.com/smarttravelercompanion.appspot.com/PopularCitiesImages/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_city_name, container, false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // we will get a DatabaseReference for the database root node
        DatabaseReference databaseReference = firebaseDatabase.getReference("smarttravelercompanion-default-rtdb");
        DatabaseReference getCNames = databaseReference.child("PopularCities");
        getCNames.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // getting a DataSnapshot for the location at the specified
                // relative path and getting in the link variable
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    String cityname=snapshot.child("cityname").getValue(String.class);
                    String imgfilename=snapshot.child("imgfilename").getValue(String.class);
                    Log.d("MSG", "city and filename: "+cityname+" "+imgfilename+"\n");
                    itemname.add(cityname);
                    imgid.add(imgfilename);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, itemname);
                setListAdapter(adapter);

            }
            // this will called when any problem
            // occurs in getting data
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message
                Log.d("MSG", "error occured");
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CityImg cobj =
                (CityImg) getFragmentManager().findFragmentById(R.id.f2);
        String link=basepath+imgid.get(position);
        cobj.display(link,itemname.get(position));
    }

}