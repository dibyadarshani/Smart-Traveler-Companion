package com.example.smarttravelercompanion;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Dictionary;
import java.util.Hashtable;

public class CityImg extends Fragment {
    ImageView ims;
    //Button btn;
    TextView vtv;
    Dictionary cityvenues = new Hashtable();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_img, container, false);
        ims=(ImageView) view.findViewById(R.id.cimv);
        vtv=(TextView) view.findViewById(R.id.venuetv);
        //btn = (Button)view.findViewById(R.id.surl);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // we will get a DatabaseReference for the database root node
        DatabaseReference databaseReference = firebaseDatabase.getReference("smarttravelercompanion-default-rtdb");
        DatabaseReference getCVenues = databaseReference.child("CityVenues");

        getCVenues.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // getting a DataSnapshot for the location at the specified
                // relative path and getting in the link variable
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    String cityname=snapshot.child("cityname").getValue(String.class);
                    String venue1=snapshot.child("venue1").getValue(String.class);
                    String venue2=snapshot.child("venue2").getValue(String.class);
                    String venue3=snapshot.child("venue3").getValue(String.class);
                    String venue4=snapshot.child("venue4").getValue(String.class);
                    String venues="1. "+venue1+"\n2. "+venue2+"\n3. "+venue3+"\n4. "+venue4+"\n";
                    cityvenues.put(cityname,venues);
                    Log.d("MSG", "city and venues: "+cityname+" "+venues+"\n");

                }

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

    public void display(String link,String cname){
        Picasso.get().load(link).fit().centerCrop().into(ims);
        String venuelist = cityvenues.get(cname).toString();
        vtv.setText(venuelist);
        /*
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cname.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY, cname);
                    startActivity(intent);
                } else {
                    Log.d("msg","error in web search");
                }
            }
        });

         */
    }
}