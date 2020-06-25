package com.example.covid_19stats.UIClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.covid_19stats.POJO.Stat;
import com.example.covid_19stats.Adapters.StatsAdapter;
import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.covid_19stats.Resources.CheckConnection.isNetworkConnected;
import static com.example.covid_19stats.Resources.CheckConnection.isNetworkWifi;

public class ShowGlobalStats extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView totalCases;
    DBInterface db;
    Context appContext;
    ArrayList<Stat> arrayStats = new ArrayList<Stat>();
    ListView lv;
    SharedPreferences prefs;
    NavigationView navigationView;
    StatsAdapter inflate;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_countries_stats);
        giveValues();
        Spinner spinner = findViewById(R.id.spinnerFilter);
        final EditText countryFilter = findViewById(R.id.searchBar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.filter,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Toast.makeText(appContext, "You can click on each country to see detailed info!", Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        try {
            db.obre();
            Cursor c = db.obtainAllInformation();
            while (c.moveToNext()) {
                Stat indiStat = new Stat(c.getString(0),
                        c.getString(1),
                        c.getInt(2),
                        c.getInt(3),
                        c.getInt(4));
                arrayStats.add(indiStat);
            }
            c.close();
            inflate(arrayStats);
            selectedCountry();
        } finally {
            retrieveGlobalInfo();
            db.tanca();
        }

        countryFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inflate.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void giveValues()
    {
        db = new DBInterface(this);
        appContext = this;
        totalCases = findViewById(R.id.totalCases);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void inflate(ArrayList<Stat> arrayStat) {
        inflate = new StatsAdapter(getApplicationContext(), R.layout.inflate_all_info, arrayStat);
        lv = (ListView) findViewById(R.id.listview);
        lv.setDivider(null);
        lv.setDividerHeight(0);
        lv.setAdapter(inflate);
        Collections.sort(arrayStat, new Comparator<Stat>() {
            @Override
            public int compare(Stat stat, Stat t1) {
                return stat.getName().compareTo(t1.getName());
            }
        });
    }

    private void retrieveGlobalInfo() {
        Cursor c = db.obtainAllGlobalInformation();
        c.moveToFirst();
        int cases, deaths, cured;
        int totalSum = 0, totalDeaths = 0, totalCured = 0;
        while (c.moveToNext()) {
            cases = c.getInt(c.getColumnIndex("cases"));
            totalSum = cases + totalSum;
            deaths = c.getInt(c.getColumnIndex("deaths"));
            totalDeaths = deaths + totalDeaths;
            cured = c.getInt(c.getColumnIndex("cured"));
            totalCured = cured + totalCured;
        }
        c.close();
        totalCases.setText("These are the actual new cases for each country." + "\n" + "Global cases: " + totalSum + "\n" + "Total deaths: " + totalDeaths + "\n" + "Total cured: " + totalCured);
    }

    private void selectedCountry() {
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            String codeName;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isNetworkConnected(getApplicationContext())) {
                    if (isNetworkWifi(getApplicationContext())) {
                        db.obre();
                        TextView textName = view.findViewById(R.id.nameCountry);
                        codeName = textName.getText().toString();
                        Toast.makeText(getApplicationContext(), "You selected : " + codeName, Toast.LENGTH_SHORT).show();
                        Cursor c = db.obtainPopulationFromOneCountry(codeName);
                        c.moveToFirst();
                        Intent i = new Intent(getApplicationContext(), ShowCountryInfo.class);
                        i.putExtra("name", c.getString(1));
                        i.putExtra("codename", c.getString(0));
                        startActivity(i);
                        db.tanca();
                    } else {
                        Toast.makeText(appContext, "You need internet (Wifi) to access to every stat of the country!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(appContext, "You need internet (Wifi) to access to every stat of the country!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //NavBar funcionality.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        SharedPreferences.Editor editor = prefs.edit();
        switch (menuItem.getItemId()){
            case R.id.nav_info:
                startActivity(new Intent(this, MenuActivity.class));
                break;

            case R.id.nav_precautions:
                startActivity(new Intent(this, Precautions.class));
                break;

            case R.id.nav_help:
                startActivity(new Intent(this, SymptomsHelp.class));
                break;

            case R.id.nav_logout:
                editor.putBoolean("loggedIn", false);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), MainLogin.class);
                finishAffinity();
                startActivity(intent);
                break;

            case R.id.nav_share:
                Intent shareIntent = new Intent();
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.ACTION_SEND, "CVODI19-Stats app");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Prova la meva aplicació per al actual estat del COVID! https://drive.google.com/open?id=1VjWmxjmgpAcTWxAFZqlOmCG7i8Q6XRVC");
                startActivity(Intent.createChooser(shareIntent, "choose one"));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

