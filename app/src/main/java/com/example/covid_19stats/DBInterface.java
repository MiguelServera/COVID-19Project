package com.example.covid_19stats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class DBInterface {
    //Declaració de constants
    public static final String KEY_ID = "_id";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_CASES = "cases";
    public static final String KEY_RECU = "recu";
    public static final String KEY_DEATHS = "deaths";
    public static final String TAG = "DBInterface";
    public static final String BD_NAME = "COVIDBD";
    public static final String BD_TABLE = "CountryStats";
    public static final int VERSION = 1;
    public static final String BD_CREATE ="create table " + BD_TABLE + "( " + KEY_ID + " integer primary key autoincrement, " + KEY_COUNTRY +" text not null, " + KEY_CASES + " text not null, " + KEY_RECU + " text not null, " + KEY_DEATHS + " text not null);";
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
    //Tanca la Base de dades
    public void tanca() {
        ajuda.close();
    }

    //Insereix un contacte
    public long insertInformation(JSONObject stats) {
        ContentValues initialValues = new ContentValues();
        try {
            initialValues.put(KEY_COUNTRY, stats.getString("code"));
            initialValues.put(KEY_CASES, stats.getJSONObject("dataList").getString("cases"));
            initialValues.put(KEY_RECU, stats.getJSONObject("dataList").getString("date"));
            initialValues.put(KEY_DEATHS, stats.getJSONObject("dataList").getString("deaths"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bd.insert(BD_TABLE, null, initialValues);
    }

    //Retorna un contacte
    public Cursor obtainCountryInformation(long IDFila) throws SQLException {
        Cursor mCursor = bd.query(true, BD_TABLE, new String[] {KEY_ID,
                        KEY_COUNTRY,KEY_CASES},KEY_ID + " = " + IDFila, null, null, null, null,
                null);
        if(mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor obtainAllInformation() {
        return bd.query(BD_TABLE, new String[] {KEY_ID, KEY_COUNTRY, KEY_CASES},
                null,null, null, null, null);
    }

    //Modifica un contacte
    public boolean updateCases(long IDFila, String country, String cases) {
        ContentValues args = new ContentValues();
        args.put(KEY_COUNTRY, country);
        args.put(KEY_CASES, cases);
        return bd.update(BD_TABLE, args, KEY_ID + " = " + IDFila, null) > 0;
    }
}
