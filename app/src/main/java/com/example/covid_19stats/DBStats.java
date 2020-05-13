package com.example.covid_19stats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static com.example.covid_19stats.DBInterface.BD_CREATE;
import static com.example.covid_19stats.DBInterface.BD_CREATE1;
import static com.example.covid_19stats.DBInterface.BD_CREATE2;
import static com.example.covid_19stats.DBInterface.BD_NAME;
import static com.example.covid_19stats.DBInterface.BD_TABLE;
import static com.example.covid_19stats.DBInterface.TAG;
import static com.example.covid_19stats.DBInterface.VERSION;

public class DBStats extends SQLiteOpenHelper {
    DBStats(Context con) {
        super(con, BD_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(BD_CREATE);
            db.execSQL(BD_CREATE1);
            db.execSQL(BD_CREATE2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int VersioAntiga, int
            VersioNova) {
        db.execSQL("DROP TABLE IF EXISTS " + BD_TABLE);
        onCreate(db); }
}