package com.example.covid_19stats.Resources;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//Class that will check if we have internet connection and what type of connection, if Wifi or Mobile data.
public class CheckConnection extends AppCompatActivity {

    private NetworkReciever receptor;

    public CheckConnection() {
    }

    //Checks if we have internet connection
    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    //Check what type of internet connection.
    public static boolean isNetworkWifi(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean connected4G = netInfo.isConnected();
        if (connected4G) {
            return false;
        } else return true; //Else return true because only one more type of internet is available and that's Wifi.
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

