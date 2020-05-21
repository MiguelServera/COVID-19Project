package com.example.covid_19stats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editEmail, editPassword, editUsername;
    Button button;
    DBInterface db;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        editEmail = findViewById(R.id.editRegiEmail);
        editPassword = findViewById(R.id.editRegiPassword);
        editUsername = findViewById(R.id.editRegiName);
        button = findViewById(R.id.registerUserButton);
        button.setOnClickListener(this);
        db = new DBInterface(this);
        i = new Intent(this, MainLogin.class);
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            db.obre();
            String textUsername = editUsername.getText().toString();
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

            else if (MainLogin.firstStart == true && MainLogin.firstUser == false)
            {
                if(Patterns.EMAIL_ADDRESS.matcher(textEmail).matches() && isValidPassword(textPassword)) {
                    db.insertUserInfo(textUsername, textEmail, textPassword);
                    MainLogin.editor.putBoolean("firstUser", true);
                    MainLogin.editor.apply();
                    Toast.makeText(this, "First User Inserted", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(this, "Email or password not okay!", Toast.LENGTH_SHORT).show();
                }
            }

            else
            {
                if(Patterns.EMAIL_ADDRESS.matcher(textEmail).matches() && isValidPassword(textPassword)) {
                    if (db.insertUserInfo(textUsername, textEmail, textPassword) != -1) Toast.makeText(this, "User inserted", Toast.LENGTH_SHORT).show();

                    else Toast.makeText(this, "The user is already inserted", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(this, "Credencials do not meet the requirements", Toast.LENGTH_LONG).show();
                }
            }

            db.tanca();
        }
    }
    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}