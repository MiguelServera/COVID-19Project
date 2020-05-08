package com.example.covid_19stats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainLogin extends AppCompatActivity implements View.OnClickListener {

    Button button;
    Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        button = findViewById(R.id.registerButton);
        button.setOnClickListener(this);
        button2 = findViewById(R.id.loginButton);
        button2.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == button)
        {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
        else if (view == button2)
        {
            Intent i = new Intent(this, ProvaActivity.class);
            startActivity(i);
        }
    }
}
