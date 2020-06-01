package com.example.covid_19stats.UIClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;
import com.example.covid_19stats.Resources.DownloadFileCCAA;
import com.example.covid_19stats.Resources.RetrieveStatsFromAPI;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.covid_19stats.Resources.CheckConnection.isNetworkConnected;
import static com.example.covid_19stats.Resources.CheckConnection.isNetworkWifi;

//Class where we will choose what information to see. I implemented a few necessary checks.
public class MenuActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static Button ccaa_table, global_table, global_graph, topTen_graph;
    private DrawerLayout drawer;
    DBInterface db;
    Intent launchApiActivity, launchBdActivity, launchDownloadACActivity, launchGraphsActivity, launchACActivity;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_layout);
        giveValues();
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        CheckWhatToDo();
    }

    private void giveValues() {
        ccaa_table = findViewById(R.id.ccaa_table_button);
        global_table = findViewById(R.id.global_table_button);
        global_graph = findViewById(R.id.global_button);
        topTen_graph = findViewById(R.id.country_button);
        ccaa_table.setOnClickListener(this);
        global_table.setOnClickListener(this);
        global_graph.setOnClickListener(this);
        topTen_graph.setOnClickListener(this);
        launchApiActivity = new Intent(this, RetrieveStatsFromAPI.class);
        launchBdActivity = new Intent(this, ShowGlobalStats.class);
        launchGraphsActivity = new Intent(this, ShowGraphs.class);
        launchACActivity = new Intent(this, ShowCCAAInfo.class);
        launchDownloadACActivity = new Intent(this, DownloadFileCCAA.class);
        db = new DBInterface(this);
    }

    /*Checks done in this method:
    If the user logs for the first time, it checks if the user has internet connection to download data.
    If we only have mobile data, we will only download CSV file and the other buttons will lock until
    the user starts this activity again.
    If it's not the first time the user enters for the first time, check if the user has connection.
    If the user has wifi connection a day has passed since the last download, download data.
    If the user has mobile data but a day has passed anyway, download only CSV data.
    If a day has not passed just use the database.
    If the user doesn't have connection a toast will prompt to notify the user.
     */
    private void CheckWhatToDo() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (MainLogin.firstStart) {
                if (isNetworkConnected(getApplicationContext())) {
                    if (isNetworkWifi(getApplicationContext())) {
                        editor.putBoolean("firstStart", false);
                        editor.apply();
                        startActivity(launchApiActivity);
                        startActivity(launchDownloadACActivity);
                    } else {
                        startActivity(launchDownloadACActivity);
                        ccaa_table.setEnabled(true);
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

    //Method that will check if today's date is less or greater than tomorrow's.
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

    //Method that gives the actual date with no time
    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //Method that gives the actual date with time
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //NavBar funcionality.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        switch (menuItem.getItemId()){
            case R.id.nav_precautions:
                startActivity(new Intent(this, Precautions.class));
                break;

            case R.id.nav_help:
                startActivity(new Intent(this, SymptomsHelp.class));
                break;

            case R.id.nav_logout:
                editor.putBoolean("loggedIn", false);
                editor.apply();
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainLogin.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Doesn't work for the intended purpose, to clear all activities always
                finishAffinity();
                startActivity(intent);
                break;

            case R.id.nav_share:
                Intent shareIntent = new Intent();
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.ACTION_SEND, "CVODI19-Stats app");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Prova la meva aplicació per al actual estat del COVID! https://drive.google.com/open?id=1VjWmxjmgpAcTWxAFZqlOmCG7i8Q6XRVC");
                startActivity(Intent.createChooser(shareIntent, "choose one"));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
