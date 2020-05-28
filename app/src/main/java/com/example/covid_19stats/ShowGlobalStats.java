package com.example.covid_19stats;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.covid_19stats.CheckConnection.isNetworkWifi;

public class ShowGlobalStats extends AppCompatActivity {
    TextView totalCases;
    DBInterface db;
    Context appContext;
    ArrayList<Stat> arrayStats = new ArrayList<Stat>();
    ListView lv;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_countries_stats);
        db = new DBInterface(this);
        appContext = this;
        totalCases = findViewById(R.id.totalCases);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        try {
            db.obre();
            Cursor c = db.obtainAllInformation();
            while (c.moveToNext()) {
                Stat indiStat = new Stat(c.getString(0),
                        c.getString(1),
                        c.getInt(2),
                        c.getInt(3),
                        c.getInt(4));
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
                return stat.getName().compareTo(t1.getName());
            }
        });
    }

    private void retrieveGlobalInfo() {
        Cursor c = db.obtainAllGlobalInformation();
        c.moveToFirst();
        int cases, deaths, cured;
        int totalSum = 0, totalDeaths = 0, totalCured = 0;
        while (c.moveToNext()) {
            cases = c.getInt(c.getColumnIndex("cases"));
            totalSum = cases + totalSum;
            deaths = c.getInt(c.getColumnIndex("deaths"));
            totalDeaths = deaths + totalDeaths;
            cured = c.getInt(c.getColumnIndex("cured"));
            totalCured = cured + totalCured;
        }
        c.close();
        totalCases.setText("  Global cases: " + totalSum + "\n" + "  Total deaths: " + totalDeaths + "\n" + "  Total cured: " + totalCured);
    }

    private void selectedCountry() {
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            String codeName;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isNetworkWifi(getApplicationContext())) {
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
                } else {
                    Toast.makeText(appContext, "You need internet (Wifi) to access to every stat of the country!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

