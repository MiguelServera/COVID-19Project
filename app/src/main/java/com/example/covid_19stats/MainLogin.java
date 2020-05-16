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
    Intent launchApiActivity, launchBdActivity, launchRegisterActivity;
    Cursor c;
    String textEmail, textPassword;
    static boolean firstStart;
    static boolean firstUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        giveValues();
        comprovaConnectivitat();
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        firstStart = prefs.getBoolean("firstStart", true);
        firstUser = prefs.getBoolean("firstUser", false);
        db = new DBInterface(this);
        if (firstUser) button2.setEnabled(true);
    }

    private void giveValues() {
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        button = findViewById(R.id.registerButton);
        button2 = findViewById(R.id.loginButton);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = prefs.edit();
        launchApiActivity = new Intent(this, RetrieveStatsFromAPI.class);
        launchBdActivity = new Intent(this, RetrieveStatsFromBD.class);
        launchRegisterActivity = new Intent(this, RegisterActivity.class);
    }

    private ReceptorXarxa receptor;

    public boolean comprovaConnectivitat() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //Obtenim l’estat de laxarxa
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(this, "Internet on", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, "Offline mode", Toast.LENGTH_SHORT).show();
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
            startActivity(launchRegisterActivity);
        } else if (view == button2) {
            textEmail = editEmail.getText().toString();
            textPassword = editPassword.getText().toString();
            db.obre();
            c = db.obtainUserInfo(textEmail, textPassword);
            if (c.getCount() == 0)
                Toast.makeText(this, "No hay un usuario así dentro", Toast.LENGTH_SHORT).show();
            else if (c.getCount() == 1)
            {
                Toast.makeText(this, "Sí hay un usuario así dentro", Toast.LENGTH_SHORT).show();
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
        }
    }


    private boolean checkDate() {
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
            tomorrowDate =  dateFormat.format(concatenedDate);
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

    /*private boolean checkUserInfo()
    {
    }*/
}
