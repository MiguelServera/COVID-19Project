package com.example.covid_19stats.UIClasses;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_19stats.R;

public class UserProfile extends AppCompatActivity implements View.OnClickListener{
    Button editProfile, changeImage, saveButton, cancelButton, logoutButton;
    EditText editUsername, editPassword, editConfirmPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        SharedPreferences getpref = getSharedPreferences("prefs", MODE_PRIVATE);
        editProfile = findViewById(R.id.editProfileButton);
        editProfile.setOnClickListener(this);
        changeImage = findViewById(R.id.changeimageButton);
        changeImage.setOnClickListener(this);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        changeImage = findViewById(R.id.changeimageButton);
        changeImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == editProfile)
        {
            changeImage.setVisibility(View.VISIBLE);
            editUsername.setEnabled(true);
            editPassword.setEnabled(true);
            editConfirmPassword.setEnabled(true);
            changeImage.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
    }
}
