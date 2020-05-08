package com.example.covid_19stats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class DBInterface {
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_COUNTRY_NAME = "country_name";
    public static final String KEY_CASES = "cases";
    public static final String KEY_RECU = "recu";
    public static final String KEY_DEATHS = "deaths";
    public static final String TAG = "DBInterface";
    public static final String BD_NAME = "COVIDBD";
    public static final String BD_TABLE = "CountryStats";
    public static final String BD_TABLE1 = "GlobalStats";
    public static final int VERSION = 1;
    public static final String BD_CREATE ="create table " + BD_TABLE + "( " +
            KEY_COUNTRY +" text not null, " +
            KEY_COUNTRY_NAME +" text not null, " +
            KEY_CASES + " text not null, " +
            //KEY_RECU + " text not null, " +
            KEY_DEATHS + " text not null);";

    public static final String BD_CREATE1 ="create table " + BD_TABLE1 + "( " +
            KEY_COUNTRY +" text not null, " +
            KEY_CASES + " text not null, " +
            //KEY_RECU + " text not null, " +
            KEY_DEATHS + " text not null);";

    private final Context context;
    private DBStats ajuda;
    private SQLiteDatabase bd;

    public DBInterface(Context con) {
        this.context = con;
        ajuda = new DBStats(context);
    }

    public DBInterface obre() throws SQLException {
        bd = ajuda.getWritableDatabase();
        return this;
    }
    public void tanca() {
        ajuda.close();
    }

    public long insertInformation(JSONObject stats) {
        ContentValues initialValues = new ContentValues();
        try {
            initialValues.put(KEY_COUNTRY, stats.getString("code"));
            initialValues.put(KEY_COUNTRY_NAME, stats.getString("name"));
            initialValues.put(KEY_CASES, stats.getString("cases"));
            //initialValues.put(KEY_RECU, stats.getJSONObject("dataList").getString("date"));
            initialValues.put(KEY_DEATHS, stats.getString("deaths"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bd.insert(BD_TABLE, null, initialValues);
    }
    public Cursor obtainAllInformation() {
        return bd.query(BD_TABLE, new String[] {KEY_COUNTRY, KEY_COUNTRY_NAME, KEY_CASES, KEY_DEATHS},
                null,null, null, null, null);
    }

    public void deleteDatabaseStats()
    {
        context.deleteDatabase("COVIDBD");
    }

    public long insertGlobalInformation(JSONObject stats) {
        ContentValues initialValues = new ContentValues();
        try {
            initialValues.put(KEY_COUNTRY, stats.getString("code"));
            initialValues.put(KEY_CASES, stats.getString("cases"));
            //initialValues.put(KEY_RECU, stats.getJSONObject("dataList").getString("date"));
            initialValues.put(KEY_DEATHS, stats.getString("deaths"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bd.insert(BD_TABLE1, null, initialValues);
    }

    public Cursor obtainAllGlobalInformation() {
        return bd.query(BD_TABLE1, new String[] {KEY_COUNTRY, KEY_CASES, KEY_DEATHS},
                null,null, null, null, null);
    }
}
