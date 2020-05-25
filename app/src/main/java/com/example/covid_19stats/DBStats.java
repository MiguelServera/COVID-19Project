package com.example.covid_19stats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.covid_19stats.DBInterface.*;

import static com.example.covid_19stats.DBInterface.BD_NAME;
import static com.example.covid_19stats.DBInterface.CREATE_ACTUAL_DATE_TABLE;
import static com.example.covid_19stats.DBInterface.CREATE_CCAA_STATS_TABLE;
import static com.example.covid_19stats.DBInterface.CREATE_COUNTRY_TABLE;
import static com.example.covid_19stats.DBInterface.CREATE_GLOBAL_TABLE;
import static com.example.covid_19stats.DBInterface.CREATE_ONE_COUNTRY_STATS_TABLE;
import static com.example.covid_19stats.DBInterface.CREATE_USER_INFO_TABLE;
import static com.example.covid_19stats.DBInterface.VERSION;

public class DBStats extends SQLiteOpenHelper {
    private static final String GLOBAL_TABLE = "";

    DBStats(Context con) {
        super(con, BD_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_COUNTRY_TABLE);
            db.execSQL(CREATE_GLOBAL_TABLE);
            db.execSQL(CREATE_ONE_COUNTRY_STATS_TABLE);
            db.execSQL(CREATE_ACTUAL_DATE_TABLE);
            db.execSQL(CREATE_USER_INFO_TABLE);
            db.execSQL(CREATE_CCAA_STATS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int VersioAntiga, int
            VersioNova) {
        db.execSQL("DROP TABLE IF EXISTS " + GLOBAL_TABLE);
        onCreate(db); }
}