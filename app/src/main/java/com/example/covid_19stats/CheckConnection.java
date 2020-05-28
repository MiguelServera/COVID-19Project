package com.example.covid_19stats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CheckConnection extends AppCompatActivity {

    private NetworkReciever receptor;

    public CheckConnection() {
    }

    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static boolean isNetworkWifi(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean connected4G = netInfo.isConnected();
        netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (connected4G) {
            return false;
        } else return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receptor != null) {
            this.unregisterReceiver(receptor);
        }
    }

    public class NetworkReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isNetworkConnected(getApplicationContext());
        }
    }
}

