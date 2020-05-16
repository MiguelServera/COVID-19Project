package com.example.covid_19stats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editEmail, editPassword;
    Button button;
    DBInterface db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        editEmail = findViewById(R.id.editRegiEmail);
        editPassword = findViewById(R.id.editRegiPassword);
        button = findViewById(R.id.registerUserButton);
        button.setOnClickListener(this);
        db = new DBInterface(this);
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            db.obre();
            String textEmail = editEmail.getText().toString();
            String textPassword = editPassword.getText().toString();
            db.insertUserInfo(textEmail, textPassword);
            db.tanca();
        }
    }
}