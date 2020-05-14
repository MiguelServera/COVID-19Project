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

    public CheckConnection()
    {
    }
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
}

