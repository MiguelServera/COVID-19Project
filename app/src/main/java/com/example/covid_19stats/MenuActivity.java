package com.example.covid_19stats;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    TextView textview1, textview2, textview3, textview4;
    DBInterface bd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        textview1 = findViewById(R.id.textView);
        textview2 = findViewById(R.id.textView2);
        textview3 = findViewById(R.id.textView3);
        textview4 = findViewById(R.id.textView4);
        fillTable();
    }

    public void fillTable(){
        bd = new DBInterface(this);
        bd.obre();

    }
}
