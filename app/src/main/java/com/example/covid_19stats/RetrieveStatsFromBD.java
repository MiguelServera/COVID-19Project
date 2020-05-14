package com.example.covid_19stats;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class RetrieveStatsFromBD extends AppCompatActivity {
    TextView totalCases;
    DBInterface db;
    Context appContext;
    ArrayList<Stat> arrayStats = new ArrayList<Stat>();
    ListView lv;
    InsertEachCountryData countryData;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        db = new DBInterface(this);
        appContext = this;
        totalCases = findViewById(R.id.textView5);
        //progressBar = findViewById(R.id.progressBar);
        try {
            db.obre();
            JSONObject datalist;
            Cursor c = db.obtainAllInformation();
            while (c.moveToNext()) {
                Stat indiStat = new Stat(c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4));
                arrayStats.add(indiStat);
            }
            c.close();
            inflate(arrayStats);
            selectedCountry();
        } finally {
            retrieveGlobalInfo();
            db.tanca();
        }
    }

    class InsertEachCountryData extends Thread {
        JSONObject datalist;

        public InsertEachCountryData(JSONObject datalist) {
            this.datalist = datalist;
        }

        public void run() {
            try {
                db.insertCountryCodeAndPopulation(datalist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inflate(ArrayList<Stat> arrayStat) {
        StatsAdapter inflate = new StatsAdapter(getApplicationContext(), R.layout.inflate_all_info, arrayStat);
        lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(inflate);
        Collections.sort(arrayStat, new Comparator<Stat>() {
            @Override
            public int compare(Stat stat, Stat t1) {
                return stat.getNameC().compareTo(t1.getNameC());
            }
        });
    }

    private void retrieveGlobalInfo() {
        Cursor c = db.obtainAllGlobalInformation();
        c.moveToFirst();
        int cases;
        int totalSum = 0;
        while (!c.isAfterLast()) {
            cases = Integer.parseInt(c.getString(c.getColumnIndex("cases")));
            totalSum = cases + totalSum;
            c.moveToNext();
        }
        totalCases.setText("Global cases: " + totalSum);
    }

    private void selectedCountry() {
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            String codeName;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                db.obre();
                TextView textName = view.findViewById(R.id.nameCountry);
                codeName = textName.getText().toString();
                Toast.makeText(getApplicationContext(), "You selected : " + codeName, Toast.LENGTH_SHORT).show();
                Cursor c = db.obtainDataFromOneCountry(codeName);
                c.moveToFirst();
                Log.i("geoid", c.getString(0));
                Intent i = new Intent(getApplicationContext(), ShowCountryInfo.class);
                i.putExtra("codename", c.getString(0));
                startActivity(i);
                db.tanca();
            }
        });
    }
}

