package com.example.covid_19stats;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProvaActivity extends AppCompatActivity implements View.OnClickListener {
    Button button;
    DBInterface bd;
    EditText country;
    EditText cases;
    EditText recus;
    EditText deaths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prova_activity);
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(this);
        bd = new DBInterface(this);
        country = findViewById(R.id.EditText5);
        cases = findViewById(R.id.EditText9);
        recus = findViewById(R.id.EditText10);
        deaths = findViewById(R.id.EditText11);

    }
    @Override
    public void onClick(View view) {
        bd.obre();
        if (view == button)
        {
            if (bd.insertInformation(country.getText().toString(),
                    cases.getText().toString(),
                    recus.getText().toString(),
                    deaths.getText().toString()) != -1) {
                Toast.makeText(this, "Afegit correctament",
                        Toast.LENGTH_SHORT).show();;
            } else {
                Toast.makeText(this, "Error a l’afegir",
                        Toast.LENGTH_SHORT).show();
            }
            bd.tanca();
            finish();
        }
    }
}
