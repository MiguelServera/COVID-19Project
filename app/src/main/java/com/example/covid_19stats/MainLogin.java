package com.example.covid_19stats;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainLogin extends AppCompatActivity implements View.OnClickListener {

    Button button, button2;
    EditText editEmail, editPassword;
    DBInterface db;
    SharedPreferences prefs;
    static SharedPreferences.Editor editor;
    Intent launchApiActivity;
    Intent launchBdActivity;
    static boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        button = findViewById(R.id.registerButton);
        button.setOnClickListener(this);
        button2 = findViewById(R.id.loginButton);
        button2.setOnClickListener(this);
        comprovaConnectivitat();
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = prefs.edit();
        firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart) { button2.setEnabled(false); }

        else {button2.setEnabled(true);}

        launchApiActivity = new Intent(this, RetrieveStatsFromAPI.class);
        launchBdActivity = new Intent(this, RetrieveStatsFromBD.class);
        System.out.println(firstStart);
        db = new DBInterface(this);
    }

    private ReceptorXarxa receptor;

    public boolean comprovaConnectivitat() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //Obtenim l’estat de laxarxa
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(this, "Internet on", Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "Offline mode", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receptor != null) {
            this.unregisterReceiver(receptor);
        }
    }

    public class ReceptorXarxa extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            comprovaConnectivitat();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view == button) {
        } else if (view == button2) {
            if (firstStart) {
                editor.putBoolean("firstStart", false);
                editor.apply();
                startActivity(launchApiActivity);

            } else {
                db.obre();
                if (comprovaConnectivitat()) {
                    if (checkDate()) {
                        startActivity(launchApiActivity);
                    } else {
                        startActivity(launchBdActivity);
                    }
                } else {
                    startActivity(launchBdActivity);
                }
            }
        }
        db.tanca();
    }

    private boolean checkDate() {
        String todayData = "";
        String tomorrowDate = "";
        Date concatenedDate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            todayData = dateFormat.format(date);
            Calendar cal = Calendar.getInstance();
            Cursor c = db.obtainDate();
            c.moveToFirst();
            String savedDate = c.getString(1);
            c.close();
            Log.i("Data from base", savedDate);
            concatenedDate = dateFormat.parse(savedDate);
            cal.setTime(concatenedDate);
            cal.add(Calendar.DATE, 1);
            concatenedDate = cal.getTime();
            tomorrowDate =  dateFormat.format(concatenedDate);
            Log.i("Data from ", tomorrowDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (todayData.compareTo(tomorrowDate + " 14:00:00") > 0) {
            return true;

        } else if (todayData.compareTo(concatenedDate.toString()) < 0) {
            return false;

        } else {
            return false;
        }
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
