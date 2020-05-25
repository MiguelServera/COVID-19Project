package com.example.covid_19stats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CheckConnection extends AppCompatActivity {

    private ReceptorXarxa receptor;

    public CheckConnection() {
    }

    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
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
            isNetworkConnected(getApplicationContext());
        }
    }
}

