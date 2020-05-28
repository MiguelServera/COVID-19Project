package com.example.covid_19stats;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainLogin extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences prefs;
    EditText editEmail, editPassword;
    Button button, button2;
    Intent launchRegisterActivity, launchMainMenu;
    DBInterface db;
    static SharedPreferences.Editor editor;
    static boolean firstStart, firstUser, loggedIn;
    private DrawerLayout drawer;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        button = findViewById(R.id.registerButton);
        button2 = findViewById(R.id.loginButton);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        launchRegisterActivity = new Intent(this, RegisterActivity.class);
        launchMainMenu = new Intent(this, MenuActivity.class);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = prefs.edit();
        firstStart = prefs.getBoolean("firstStart", true);
        firstUser = prefs.getBoolean("firstUser", false);
        loggedIn = prefs.getBoolean("loggedIn", false);
        verifyStoragePermissions(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (firstUser) button2.setEnabled(true);
        if (!firstStart) {
            if (loggedIn) {
                finish();
                startActivity(launchMainMenu);
            }
        }
        db = new DBInterface(this);
    }

    @Override
    public void onClick(View view) {
        if (view == button) {
            startActivity(launchRegisterActivity);
        } else if (view == button2) {
            Cursor c;
            String textEmail, textPassword;
            textEmail = editEmail.getText().toString();
            textPassword = editPassword.getText().toString();
            if ((textEmail.equals("") && textPassword.equals("")) || (textEmail.equals("") || textPassword.equals(""))) {
                Toast.makeText(this, "The camps can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                db.obre();
                c = db.obtainUserInfo(textEmail, textPassword);
                if (c.getCount() == 0)
                    Toast.makeText(this, "There is no user like that", Toast.LENGTH_SHORT).show();
                else if (c.getCount() == 1) {
                    c.moveToNext();
                    Toast.makeText(this, "Hello, " + c.getString(0), Toast.LENGTH_SHORT).show();
                    editor.putBoolean("loggedIn", true);
                    editor.apply();
                    editor.commit();
                    startActivity(launchMainMenu);
                }
                db.tanca();
            }
        }
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        boolean checkPermissions;
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            //Check if you have permissions to install aplicattions from unknown sources too
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            checkPermissions = false;
        } else {
            checkPermissions = true;
        }
        return checkPermissions;
    }
}
