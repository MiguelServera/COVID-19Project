package com.example.covid_19stats;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    DBInterface db;
    ListView lv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        db = new DBInterface(this);
        db.obre();
        inflateCountryName();
        inflateCountryDeaths();
        inflateCountryCode();
        inflateCountryCases();
        db.tanca();
    }

    public void inflateCountryCode() {
        Cursor c = db.obtainAllInformation();
        ArrayList<String> info = new ArrayList<String>();
        c.moveToFirst();

        while(!c.isAfterLast()) {
            info.add(c.getString(c.getColumnIndex(db.KEY_COUNTRY)));
            c.moveToNext();
        }
        ArrayAdapter inflate = new ArrayAdapter(getApplicationContext(), R.layout.inflateinfo, R.id.textView, info);
        lv = (ListView) findViewById(R.id.listviewCountryCode);
        lv.setAdapter(inflate);
    }

    public void inflateCountryName() {
        Cursor c = db.obtainAllInformation();
        ArrayList<String> info = new ArrayList<String>();
        c.moveToFirst();

        while(!c.isAfterLast()) {
            info.add(c.getString(c.getColumnIndex(db.KEY_COUNTRY_NAME)));
            c.moveToNext();
        }
        ArrayAdapter inflate = new ArrayAdapter(getApplicationContext(), R.layout.inflateinfo, R.id.textView, info);
        lv = (ListView) findViewById(R.id.listviewCountryName);
        lv.setAdapter(inflate);
    }

    public void inflateCountryCases() {
        Cursor c = db.obtainAllInformation();
        ArrayList<String> info = new ArrayList<String>();
        c.moveToFirst();

        while(!c.isAfterLast()) {
            info.add(c.getString(c.getColumnIndex(db.KEY_CASES)));
            c.moveToNext();
        }
        ArrayAdapter inflate = new ArrayAdapter(getApplicationContext(), R.layout.inflateinfo, R.id.textView, info);
        lv = (ListView) findViewById(R.id.listviewCases);
        lv.setAdapter(inflate);
    }

    public void inflateCountryDeaths() {
        Cursor c = db.obtainAllInformation();
        ArrayList<String> info = new ArrayList<String>();
        c.moveToFirst();

        while(!c.isAfterLast()) {
            info.add(c.getString(c.getColumnIndex(db.KEY_DEATHS)));
            c.moveToNext();
        }
        ArrayAdapter inflate = new ArrayAdapter(getApplicationContext(), R.layout.inflateinfo, R.id.textView, info);
        lv = (ListView) findViewById(R.id.listviewDeaths);
        lv.setAdapter(inflate);
    }
}
