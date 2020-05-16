package com.example.covid_19stats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editEmail, editPassword;
    Button button;
    DBInterface db;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        editEmail = findViewById(R.id.editRegiEmail);
        editPassword = findViewById(R.id.editRegiPassword);
        button = findViewById(R.id.registerUserButton);
        button.setOnClickListener(this);
        db = new DBInterface(this);
        i = new Intent(this, MainLogin.class);
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            db.obre();
            String textEmail = editEmail.getText().toString();
            String textPassword = editPassword.getText().toString();
            if (textEmail.isEmpty() || textEmail.equals("") || textPassword.isEmpty() || textPassword.equals(""))
            {
                if ((textEmail.isEmpty() || textEmail.equals("")) && (textPassword.isEmpty() || textPassword.equals("")))
                    Toast.makeText(this, "The values can't be null!", Toast.LENGTH_LONG).show();

                if ((textEmail.isEmpty() || textEmail.equals("")) && !textPassword.isEmpty())
                    Toast.makeText(this, "The email can't be null!", Toast.LENGTH_LONG).show();

                if ((textPassword.isEmpty() || textPassword.equals("")) && !textEmail.isEmpty())
                    Toast.makeText(this, "The password can't be null!", Toast.LENGTH_LONG).show();
            }

            else if ((db.insertUserInfo(textEmail, textPassword) != -1) && MainLogin.firstStart == true)
            {
                MainLogin.editor.putBoolean("firstUser", true);
                MainLogin.editor.apply();
                Toast.makeText(this, "User inserted", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }

            else if ((db.insertUserInfo(textEmail, textPassword) != -1) && MainLogin.firstStart == false)
            {
                Toast.makeText(this, "User inserted", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }

            db.tanca();
        }
    }
}