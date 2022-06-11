package com.example.smarttravelercompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.AsyncTask;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DestRecc extends AppCompatActivity {

    String baseurl="http://192.168.203.125:104/inference?";
    String[] citytypes = {"Dynamic cities, bars, night, amazing",
            "Popular cities, history, museums, enjoyable",
            "Family-oriented cities, kids, shopping, lovely"};
    String[] usertypes = {"Openness, Conscientiousness, Agreeableness, Neuroticis, Backpacker, Beach Goer, Nature Lover, Nightlife Seeker",
            "Conscientiousness, Extraversion, Agreeableness, Beach Goer,Nightlife Seeker, Urban Explorer",
            "Openness, Like a Local, Thrill Seeker, Nightlife Seeker",
            "Extraversion, Agreeableness, Foodie, Luxury Traveller, Trendsetter",
            "Agreeableness, Neuroticis, Nature Lover, Thrifty Traveller, Eco-tourist, Trendsetter",
            "Neuroticism, Beach Goer, History Buff, Nature Lover, Vegetarian"};
    String[] agetypes = {"Below 35", "35-49", "50 and above"};
    String[] gendertypes = {"Male", "Female"};
    String scity, suser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dest_recc);
        Spinner city = (Spinner) findViewById(R.id.city);
        Spinner user = (Spinner) findViewById(R.id.user);
        //Spinner age = (Spinner) findViewById(R.id.age);
        //Spinner gender = (Spinner) findViewById(R.id.gender);
        Button btn = (Button) findViewById(R.id.dbtn);
        ArrayAdapter aa1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,citytypes);
        ArrayAdapter aa2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,usertypes);
        //ArrayAdapter aa3 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,agetypes);
        //ArrayAdapter aa4 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,gendertypes);
        city.setAdapter(aa1);
        user.setAdapter(aa2);
        //age.setAdapter(aa3);
        //gender.setAdapter(aa4);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scity=String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    suser="25";
                if(position==1)
                    suser="168";
                if(position==2)
                    suser="38";
                if(position==3)
                    suser="18";
                if(position==4)
                    suser="125";
                if(position==5)
                    suser="27";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlEndpoint = baseurl+"city="+scity+"&user="+suser;
                CallReccApi data = new CallReccApi();
                data.execute(urlEndpoint);
            }
        });
    }
    private class CallReccApi extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);
                JsonReader jsonReader = new JsonReader(isw);
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String key = jsonReader.nextName();
                    if (key.equals("pred1") || key.equals("pred2") || key.equals("pred3")) {
                        String value = jsonReader.nextString();
                        result += value;
                        result += "\n";

                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.close();
                urlConnection.disconnect();
                return result;

            }catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            TextView ans = (TextView) findViewById(R.id.drecc);
            ans.setText(s);
        }
    }


}