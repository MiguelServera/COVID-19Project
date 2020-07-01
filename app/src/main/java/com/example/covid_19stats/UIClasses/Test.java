package com.example.covid_19stats.UIClasses;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;

public class Test extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        DBInterface db = new DBInterface(this);
        db.obre();
    }
}
