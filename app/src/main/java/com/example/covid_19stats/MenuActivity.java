package com.example.covid_19stats;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        inflate();
        db.tanca();
    }

    public void inflate() {
        Cursor c = db.obtainAllInformation();
        ArrayList<String> info = new ArrayList<String>();
        c.moveToFirst();

        while(!c.isAfterLast()) {
            info.add(c.getString(c.getColumnIndex(db.KEY_COUNTRY)));
            info.add(c.getString(c.getColumnIndex(db.KEY_COUNTRY_NAME)));
            info.add(c.getString(c.getColumnIndex(db.KEY_CASES)));
            info.add(c.getString(c.getColumnIndex(db.KEY_DEATHS)));
            c.moveToNext();
        }
        ArrayAdapter inflate = new ArrayAdapter(getApplicationContext(), R.layout.inflate_all_info, R.id.textView, info);
        lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(inflate);
    }
}
