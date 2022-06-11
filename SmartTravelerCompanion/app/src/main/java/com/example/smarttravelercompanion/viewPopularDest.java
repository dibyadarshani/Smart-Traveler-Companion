package com.example.smarttravelercompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.style.LineHeightSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class viewPopularDest extends AppCompatActivity {

    ArrayList<String> itemname = new ArrayList<String>();
    ArrayList<String> imgid = new ArrayList<String>();
    String basepath="https://storage.googleapis.com/smarttravelercompanion.appspot.com/PopularCitiesImages/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_popular_dest);
        LinearLayout ll=findViewById(R.id.layout1);
        TextView tv1=findViewById(R.id.tv1);
        TextView tv2=findViewById(R.id.tv2);
        TextView tv3=findViewById(R.id.tv3);
        TextView tv4=findViewById(R.id.tv4);
        TextView tv5=findViewById(R.id.tv5);
        ImageView im1=findViewById(R.id.im1);
        ImageView im2=findViewById(R.id.im2);
        ImageView im3=findViewById(R.id.im3);
        ImageView im4=findViewById(R.id.im4);
        ImageView im5=findViewById(R.id.im5);
        TextView[] tvs={tv1,tv2,tv3,tv4,tv5};
        ImageView[] ims={im1,im2,im3,im4,im5};

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // we will get a DatabaseReference for the database root node
        DatabaseReference databaseReference = firebaseDatabase.getReference("smarttravelercompanion-default-rtdb");
        DatabaseReference getImage = databaseReference.child("PopularCities");
        getImage.addListenerForSingleValueEvent(new ValueEventListener() {
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
                for (int i = 0; i < 5; i++) {
                    int num=ThreadLocalRandom.current().nextInt(0, itemname.size() );
                    tvs[i].setText(itemname.get(num));
                    String link=basepath+imgid.get(num);
                    Picasso.get().load(link).fit().centerCrop().into(ims[i]);
                }

            }
            // this will called when any problem
            // occurs in getting data
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message in toast
                Toast.makeText(viewPopularDest.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }
        });

    }

}