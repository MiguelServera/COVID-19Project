package com.example.covid_19stats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainLogin extends AppCompatActivity implements View.OnClickListener {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        button = findViewById(R.id.registerButton);
        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == button)
        {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }
}
