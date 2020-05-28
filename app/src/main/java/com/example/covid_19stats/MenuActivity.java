package com.example.covid_19stats;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.covid_19stats.CheckConnection.isNetworkConnected;
import static com.example.covid_19stats.CheckConnection.isNetworkWifi;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    public static Button ccaa_table, global_table, global_graph, topTen_graph;
    private DrawerLayout drawer;
    DBInterface db;
    Intent launchApiActivity, launchBdActivity, launchDownloadACActivity, launchGraphsActivity, launchACActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_layout);
        giveValues();
        ccaa_table.setOnClickListener(this);
        global_table.setOnClickListener(this);
        global_graph.setOnClickListener(this);
        topTen_graph.setOnClickListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (MainLogin.firstStart) {
            if (isNetworkConnected(getApplicationContext())) {
                if (isNetworkWifi(getApplicationContext())) {
                    editor.putBoolean("firstStart", false);
                    editor.apply();
                    editor.commit();
                    startActivity(launchApiActivity);
                    startActivity(launchDownloadACActivity);
                } else {
                    startActivity(launchDownloadACActivity);
                    global_table.setEnabled(false);
                    global_graph.setEnabled(false);
                    topTen_graph.setEnabled(false);
                    Toast.makeText(this, "If this is your first time opening the app, please make sure you have WIFI connection to download countries data", Toast.LENGTH_LONG).show();
                }

            } else {
                ccaa_table.setEnabled(false);
                global_table.setEnabled(false);
                global_graph.setEnabled(false);
                topTen_graph.setEnabled(false);
                Toast.makeText(this, "If this is your first time opening the app, please make sure you have connection to download data", Toast.LENGTH_LONG).show();
            }
        } else {
            if (isNetworkConnected(getApplicationContext())) {
                if (checkDate()) {
                    if (isNetworkWifi(getApplicationContext())) {
                        startActivity(launchApiActivity);
                        startActivity(launchDownloadACActivity);

                        Toast.makeText(this, "New data downloaded correctly", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(launchDownloadACActivity);
                        Toast.makeText(this, "Only new CCAA data downloaded, countries data requires WIFI", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "You don't have internet connection to check if there's new data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void giveValues() {
        ccaa_table = findViewById(R.id.ccaa_table_button);
        global_table = findViewById(R.id.global_table_button);
        global_graph = findViewById(R.id.global_button);
        topTen_graph = findViewById(R.id.country_button);
        launchApiActivity = new Intent(this, RetrieveStatsFromAPI.class);
        launchBdActivity = new Intent(this, ShowGlobalStats.class);
        launchGraphsActivity = new Intent(this, ShowGraphs.class);
        launchACActivity = new Intent(this, ShowCCAAInfo.class);
        launchDownloadACActivity = new Intent(this, DownloadFileCCAA.class);
        db = new DBInterface(this);
    }

    @Override
    public void onClick(View view) {
        if (view == global_table) startActivity(launchBdActivity);

        else if (view == ccaa_table) startActivity(launchACActivity);

        else if (view == global_graph) {
            launchGraphsActivity.putExtra("graphType", "global");
            startActivity(launchGraphsActivity);
        } else if (view == topTen_graph) {
            launchGraphsActivity.putExtra("graphType", "topten");
            startActivity(launchGraphsActivity);
        }
    }

    private boolean checkDate() {
        db.obre();
        String todayData = getDateTime();
        String tomorrowDate = "";
        Date concatenedDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            Cursor c = db.obtainDate();
            c.moveToFirst();
            String savedDate = c.getString(1);
            c.close();
            concatenedDate = dateFormat.parse(savedDate);
            cal.setTime(concatenedDate);
            cal.add(Calendar.DATE, 1);
            concatenedDate = cal.getTime();
            tomorrowDate = dateFormat.format(concatenedDate);
            db.tanca();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (todayData.compareTo(tomorrowDate + " 12:00:00") > 0) {
            return true;

        } else if (todayData.compareTo(tomorrowDate + " 12:00:00") < 0) {
            return false;

        } else {
            return false;
        }
    }

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
