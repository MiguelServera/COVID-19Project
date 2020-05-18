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
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GEO_ID = "geoid";
    public static final String KEY_ID = "id";
    public static final String KEY_DATE = "date";
    public static final String KEY_POPULATION = "population";
    public static final String BD_NAME = "COVIDBD";
    public static final String COUNTRYTABLE = "CountryStats";
    public static final String GLOBAL_TABLE = "GlobalStats";
    public static final String ONE_COUNTRY_STATS = "OneCountryStats";
    public static final String COUNTRY_POPULATION = "PopulationCountryStats";
    public static final String ACTUAL_DATE_TABLE = "ActualDate";
    public static final String USER_INFO_TABLE = "UserInfo";

    public static final int VERSION = 1;
    public static final String CREATE_COUNTRY_TABLE ="create table " + COUNTRYTABLE + "( " +
            KEY_COUNTRY +" text not null, " +
            KEY_COUNTRY_NAME +" text not null, " +
            KEY_CASES + " text not null, " +
            KEY_DEATHS + " text not null, " +
            KEY_RECU + " text not null);";

    public static final String CREATE_GLOBAL_TABLE ="create table " + GLOBAL_TABLE + "( " +
            KEY_COUNTRY +" text not null, " +
            KEY_CASES + " text not null, " +
            //KEY_RECU + " text not null, " +
            KEY_DEATHS + " text not null);";

    public static final String CREATE_ONE_COUNTRY_STATS_TABLE ="create table " + ONE_COUNTRY_STATS + "( " +
            KEY_COUNTRY +" text not null, " +
            KEY_COUNTRY_NAME +" text not null, " +
            KEY_DATE + " text not null, " +
            KEY_CASES + " text not null, " +
            KEY_DEATHS + " text not null, " +
            KEY_RECU + " text not null);";

    public static final String CREATE_COUNTRY_POPULATION ="create table " + COUNTRY_POPULATION + "( " +
            KEY_GEO_ID +" text not null, " +
            KEY_COUNTRY_NAME +" text not null, " +
            KEY_POPULATION +" text not null);";

    public static final String CREATE_ACTUAL_DATE_TABLE ="create table " + ACTUAL_DATE_TABLE + "( " +
            KEY_ID + " integer primary key, " +
            KEY_DATE + " text not null);";

    public static final String CREATE_USER_INFO_TABLE ="create table " + USER_INFO_TABLE + "( " +
            KEY_USERNAME + " text not null, " +
            KEY_EMAIL + " text not null, " +
            KEY_PASSWORD + " text not null);";

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
        return bd.insert(COUNTRYTABLE, null, initialValues);
    }
    public Cursor obtainAllInformation() {
        return bd.query(COUNTRYTABLE, new String[] {KEY_COUNTRY, KEY_COUNTRY_NAME, KEY_CASES, KEY_DEATHS, KEY_RECU},
                null,null, null, null, null);
    }

    public void deleteDatabaseStats()
    {
        bd.execSQL("DROP TABLE IF EXISTS " + COUNTRYTABLE);
        bd.execSQL("DROP TABLE IF EXISTS " + GLOBAL_TABLE);
        bd.execSQL("DROP TABLE IF EXISTS " + COUNTRY_POPULATION);
        bd.execSQL("DROP TABLE IF EXISTS " + ACTUAL_DATE_TABLE);
    }

    public void createTables()
    {
        bd.execSQL(CREATE_COUNTRY_TABLE);
        bd.execSQL(CREATE_GLOBAL_TABLE);
        bd.execSQL(CREATE_COUNTRY_POPULATION);
        bd.execSQL(CREATE_ACTUAL_DATE_TABLE);
    }

    public void deleteOneCountryStat()
    {
        bd.execSQL("DROP TABLE IF EXISTS " + ONE_COUNTRY_STATS);
    }

    public void createTableOneCountry()
    {
        bd.execSQL(CREATE_ONE_COUNTRY_STATS_TABLE);
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
        return bd.insert(GLOBAL_TABLE, null, initialValues);
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
        return bd.insert(COUNTRY_POPULATION, null, initialValues);
    }

    public long insertActualDate(String date)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DATE, date);
        return bd.insert(ACTUAL_DATE_TABLE, null, initialValues);
    }

    public long insertStatsFromOneCountry(JSONObject stats)
    {
        ContentValues initialValues = new ContentValues();
        try {

            initialValues.put(KEY_COUNTRY, stats.getString("code"));
            initialValues.put(KEY_COUNTRY_NAME, stats.getString("name"));
            initialValues.put(KEY_DATE, stats.getString("date"));
            initialValues.put(KEY_CASES, stats.getString("cases"));
            initialValues.put(KEY_DEATHS, stats.getString("deaths"));
            initialValues.put(KEY_RECU, stats.getString("cured"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bd.insert(ONE_COUNTRY_STATS, null, initialValues);
    }

    public long insertUserInfo(String username, String email, String password)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_PASSWORD, password);
        return bd.insert(USER_INFO_TABLE, null, initialValues);
    }

    public Cursor obtainOneCountryStat(String code)
    {
        return bd.query(ONE_COUNTRY_STATS, new String[] {KEY_COUNTRY, KEY_COUNTRY_NAME, KEY_DATE, KEY_CASES, KEY_DEATHS, KEY_RECU},
                KEY_COUNTRY + " = " + "'"+code+"'",null, null, null, null);
    }

    public Cursor obtainUserInfo(String email, String password)
    {
        System.out.println(bd.query(USER_INFO_TABLE, new String[] {KEY_USERNAME, KEY_EMAIL, KEY_PASSWORD},
                KEY_EMAIL + " = " + "'" + email + "'" + " AND " + KEY_PASSWORD + " = " + "'" + password + "'",
                null, null, null, null).toString());

        return bd.query(USER_INFO_TABLE, new String[] {KEY_USERNAME, KEY_EMAIL, KEY_PASSWORD},
                KEY_EMAIL + " = " + "'" + email + "'" + " AND " + KEY_PASSWORD + " = " + "'" + password + "'",
                null, null, null, null);
    }

    public Cursor obtainAllUserInfo()
    {
        return bd.query(USER_INFO_TABLE, new String[] {KEY_USERNAME, KEY_EMAIL, KEY_PASSWORD},
                null, null, null, null, null);
    }

    public Cursor obtainDate()
    {
        return bd.query(ACTUAL_DATE_TABLE, new String[] {KEY_ID, KEY_DATE},
                null,null, null, null, null);
    }
    public Cursor obtainPopulationFromOneCountry(String name) {
        return bd.query(COUNTRY_POPULATION, new String[] {KEY_GEO_ID, KEY_COUNTRY_NAME, KEY_POPULATION},
                KEY_COUNTRY_NAME + " = " + "'"+name+"'",null, null, null, null);
    }

    public Cursor obtainAllGlobalInformation() {
        return bd.query(GLOBAL_TABLE, new String[] {KEY_COUNTRY, KEY_CASES, KEY_DEATHS},
                null,null, null, null, null);
    }
}
