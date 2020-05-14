package com.example.covid_19stats;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class MainLogin extends AppCompatActivity implements View.OnClickListener {

    Button button;
    Button button2;
    Calendar cal;
    DBInterface db;
    SharedPreferences prefs;
    Intent launchApiActivity;
    Intent launchBdActivity;
    boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        button = findViewById(R.id.registerButton);
        button.setOnClickListener(this);
        button2 = findViewById(R.id.loginButton);
        button2.setOnClickListener(this);
        comprovaConnectivitat();
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);
        launchApiActivity = new Intent(this, RetrieveStatsFromAPI.class);
        launchBdActivity = new Intent(this, RetrieveStatsFromBD.class);
    }

    private ReceptorXarxa receptor;

    public void comprovaConnectivitat() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //Obtenim l’estat de laxarxa
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(this, "Xarxa ok", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Xarxa no disponible", Toast.LENGTH_LONG).show();
        }
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean connectat4G = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean connectatWifi = networkInfo.isConnected();
        if (connectat4G) {
            Toast.makeText(this, "Connectat per 4G", Toast.LENGTH_LONG).show();
        }
        if (connectatWifi) {
            Toast.makeText(this, "Connectat per Wi-Fi", Toast.LENGTH_LONG).show();
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
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
            TimeZone timeZone = calendar.getTimeZone();
            System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
            /*Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);^*/
        } else if (view == button2) {
            System.out.println(firstStart);
            if (firstStart) {
                SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("firstStart", false);
                editor.apply();
                startActivity(launchApiActivity);

            } else {
                if (true) {
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
    }

    private boolean checkDate() {
        return false;
    }
}
