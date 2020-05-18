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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RetrieveGlobalStatsFromBD extends AppCompatActivity {
    TextView totalCases;
    DBInterface db;
    Context appContext;
    ArrayList<Stat> arrayStats = new ArrayList<Stat>();
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        db = new DBInterface(this);
        appContext = this;
        totalCases = findViewById(R.id.textView5);
        try {
            db.obre();
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
        while (c.moveToNext()) {
            cases = Integer.parseInt(c.getString(c.getColumnIndex("cases")));
            totalSum = cases + totalSum;
        }
        c.close();
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
                Cursor c = db.obtainPopulationFromOneCountry(codeName);
                c.moveToFirst();
                Intent i = new Intent(getApplicationContext(), ShowCountryInfo.class);
                i.putExtra("name", c.getString(1));
                i.putExtra("codename", c.getString(0));
                startActivity(i);
                db.tanca();
            }
        });
    }
}

