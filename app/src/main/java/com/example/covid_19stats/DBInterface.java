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
    public static final String KEY_RECU = "cured";
    public static final String KEY_DEATHS = "deaths";
    public static final String TAG = "DBInterface";
    public static final String BD_NAME = "COVIDBD";
    public static final String BD_TABLE = "CountryStats";
    public static final String BD_TABLE1 = "GlobalStats";
    public static final String BD_TABLE2 = "OneCountryStats";
    public static final String KEY_GEO_ID = "geoid";
    public static final String KEY_DATE = "date";
    public static final String KEY_POPULATION = "population";
    public static final int VERSION = 1;
    public static final String BD_CREATE ="create table " + BD_TABLE + "( " +
            KEY_COUNTRY +" text not null, " +
            KEY_COUNTRY_NAME +" text not null, " +
            KEY_CASES + " text not null, " +
            KEY_DEATHS + " text not null, " +
            KEY_RECU + " text not null);";

    public static final String BD_CREATE1 ="create table " + BD_TABLE1 + "( " +
            KEY_COUNTRY +" text not null, " +
            KEY_CASES + " text not null, " +
            //KEY_RECU + " text not null, " +
            KEY_DEATHS + " text not null);";

    public static final String BD_CREATE2 ="create table " + BD_TABLE2 + "( " +
            KEY_GEO_ID +" text not null, " +
            KEY_COUNTRY_NAME +" text not null, " +
            KEY_POPULATION +" text not null);";

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
            initialValues.put(KEY_DEATHS, stats.getString("deaths"));
            initialValues.put(KEY_RECU, stats.getString("cured"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bd.insert(BD_TABLE, null, initialValues);
    }
    public Cursor obtainAllInformation() {
        return bd.query(BD_TABLE, new String[] {KEY_COUNTRY, KEY_COUNTRY_NAME, KEY_CASES, KEY_DEATHS, KEY_RECU},
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
    public long insertCountryCodeAndPopulation(JSONObject stats) {
        ContentValues initialValues = new ContentValues();
        try {
            initialValues.put(KEY_GEO_ID, stats.getString("geoID"));
            initialValues.put(KEY_COUNTRY_NAME, stats.getString("name"));
            initialValues.put(KEY_POPULATION, stats.getString("population"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bd.insert(BD_TABLE2, null, initialValues);
    }


    public Cursor obtainDataFromOneCountry(String name) {
        return bd.query(BD_TABLE2, new String[] {KEY_GEO_ID, KEY_COUNTRY_NAME, KEY_POPULATION},
                KEY_COUNTRY_NAME + " = " + "'"+name+"'",null, null, null, null);
    }

    public Cursor obtainAllGlobalInformation() {
        return bd.query(BD_TABLE1, new String[] {KEY_COUNTRY, KEY_CASES, KEY_DEATHS},
                null,null, null, null, null);
    }
}
