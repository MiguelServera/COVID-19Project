package com.example.covid_19stats.UIClasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

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

import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;

/*First class of the app. Is a login activity that let's you register your user going to another activity
or login if you have one already, and enter to the MenuActivity.
 */

public class MainLogin extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences prefs;
    EditText editEmail, editPassword;
    Button registerButton, loginButton;
    Intent launchRegisterActivity, launchMainMenu;
    DBInterface db;
    public static SharedPreferences.Editor editor;
    static boolean firstStart, firstUser, loggedIn;
    static String email;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        giveValues();
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = prefs.edit();
        firstStart = prefs.getBoolean("firstStart", true);
        firstUser = prefs.getBoolean("firstUser", false);
        loggedIn = prefs.getBoolean("loggedIn", true);
        email = prefs.getString("userEmail", "");
        //Checks if we have permission, additionally, checks if we are already logged in or if it's our first time on the APP.
        verifyStoragePermissions(this);
        if (firstUser) loginButton.setEnabled(true);
        if (!firstStart) {
            if (loggedIn) {
                finish();
                startActivity(launchMainMenu);
            }
        }
    }

    private void giveValues()
    {
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        launchRegisterActivity = new Intent(this, RegisterActivity.class);
        launchMainMenu = new Intent(this, MenuActivity.class);
        launchMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        db = new DBInterface(this);
    }

    @Override
    public void onClick(View view) {
        if (view == registerButton) {
            startActivity(launchRegisterActivity);
        } else if (view == loginButton) {
            Cursor c;
            String textEmail, textPassword;
            textEmail = editEmail.getText().toString();
            textPassword = editPassword.getText().toString();
            if ((textEmail.equals("") && textPassword.equals("")) || (textEmail.equals("") || textPassword.equals(""))) {
                Toast.makeText(this, "The camps can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                db.obre();
                //Check if there is an user like that inside or not.
                c = db.obtainUserInfo(textEmail);
                if (c.getCount() == 0)
                    Toast.makeText(this, "There is no user like that", Toast.LENGTH_SHORT).show();
                else if (c.getCount() == 1) {
                    c.moveToNext();
                    Toast.makeText(this, "Hello, " + c.getString(0), Toast.LENGTH_SHORT).show();
                    editor.putString("userEmail", textEmail);
                    editor.putBoolean("loggedIn", true);
                    editor.apply();
                    finishAffinity();
                    startActivity(launchMainMenu);
                }
                db.tanca();
            }
        }
    }

    //Checks if we have storage permission and if not, prompts the user to accept the permission
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
