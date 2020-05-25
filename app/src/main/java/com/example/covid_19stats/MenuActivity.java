package com.example.covid_19stats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    Button button3, button4, button5, button6;
    DBInterface db;
    Intent launchApiActivity, launchBdActivity, launchDownloadACActivity, launchGraphsActivity, launchACActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_layout);
        giveValues();
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        if (MainLogin.firstStart) {
            if (isNetworkConnected(getApplicationContext())) {
                MainLogin.editor.putBoolean("firstStart", false);
                MainLogin.editor.apply();
                startActivity(launchDownloadACActivity);
                startActivity(launchApiActivity);

            } else {
                button3.setEnabled(false);
                button4.setEnabled(false);
                button5.setEnabled(false);
                button6.setEnabled(false);
                Toast.makeText(this, "If this is your first time opening the app, please make sure you have connection to download data", Toast.LENGTH_LONG).show();
            }
        } else {
            if (isNetworkConnected(getApplicationContext())) {
                if (checkDate()) {
                    startActivity(launchDownloadACActivity);
                    startActivity(launchApiActivity);
                    Toast.makeText(this, "New data downloaded correctly", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You don't have internet connection to check if there's new data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void giveValues() {
        button3 = findViewById(R.id.ccaa_table_button);
        button4 = findViewById(R.id.global_table_button);
        button5 = findViewById(R.id.global_button);
        button6 = findViewById(R.id.country_button);
        launchApiActivity = new Intent(this, RetrieveStatsFromAPI.class);
        launchBdActivity = new Intent(this, ShowGlobalStats.class);
        launchGraphsActivity = new Intent(this, ShowGraphs.class);
        launchACActivity = new Intent(this, ShowCCAAInfo.class);
        launchDownloadACActivity = new Intent(this, DownloadFileCCAA.class);
        db = new DBInterface(this);

    }

    @Override
    public void onClick(View view) {
        if (view == button4) startActivity(launchBdActivity);

        else if (view == button3) startActivity(launchACActivity);

        else if (view == button5) {
            launchGraphsActivity.putExtra("graphType", "global");
            startActivity(launchGraphsActivity);
        } else if (view == button6) {
            launchGraphsActivity.putExtra("graphType", "topten");
            startActivity(launchGraphsActivity);
        }

    }


    private boolean checkDate() {
        db.obre();
        String todayData = getDateTime();
        String tomorrowDate = "";
        Date concatenedDate = null;
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
