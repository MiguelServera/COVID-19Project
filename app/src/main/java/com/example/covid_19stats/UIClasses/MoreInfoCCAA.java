package com.example.covid_19stats.UIClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

//Class that will draw the graphs for the CCAA information. Pretty proud of this one.
public class MoreInfoCCAA extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    String code;
    DBInterface db;
    LineChart lineChartUci, lineChartHospi, lineChartPcr, lineChartDeaths;
    TextView textInfo, textUci, textPcr, textHosp, textDeath;
    ArrayList<String> xLabel = new ArrayList<>();
    SharedPreferences prefs;
    NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ccaa_graph);
        Bundle extras = getIntent().getExtras();
        code = extras.getString("code");
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        giveValues();
        db.obre();
        GetUciTotalCases();
        GetHospitalizedTotalCases();
        GetPcrTotalCases();
        GetDeceasesTotalCases();
        textInfo.setText("If needed, you can pinch(zoom) out and in to see detailed information. ");
        textInfo.setMovementMethod(new ScrollingMovementMethod());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        db.tanca();
    }

    private void giveValues()
    {
        lineChartUci = findViewById(R.id.uciTotal);
        lineChartHospi = findViewById(R.id.hospiTotal);
        lineChartPcr = findViewById(R.id.pcrTotal);
        lineChartDeaths = findViewById(R.id.deathTotal);
        textInfo = findViewById(R.id.moreInfoCCAA);
        textUci = findViewById(R.id.textUci);
        textPcr = findViewById(R.id.textPcr);
        textHosp = findViewById(R.id.textHosp);
        textDeath = findViewById(R.id.textDeaths);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        db = new DBInterface(this);
    }

    private void GetUciTotalCases() {
        ArrayList<Entry> yValues = new ArrayList<>();
        float a = 0f;
        float AVGUcicases = 0;
        float manyDates = 0;
        Cursor c = db.obtainCodeCCAAInformation(code);
        c.moveToPosition(c.getCount() - 14);
        while (c.moveToNext()) {
            {
                c.moveToPrevious();
                int previousCase = Integer.parseInt(c.getString(6));
                c.moveToNext();
                int actualCase = Integer.parseInt(c.getString(6));
                AVGUcicases = AVGUcicases + actualCase - previousCase;
                a = a + 1.0f;
                yValues.add(new Entry(a, Float.parseFloat(c.getString(6))));
                xLabel.add(c.getString(1));
                manyDates = manyDates + 1.0f;
            }
        }
        textUci.setText("Average augment of people on UCI per day: " + (AVGUcicases / manyDates));
        XAxis xAxis = lineChartUci.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(60f);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        LineDataSet firstSet = new LineDataSet(yValues, "Dates");
        firstSet.setColor(Color.MAGENTA);
        firstSet.setValueTextSize(8f);
        LineData data = new LineData();
        data.addDataSet(firstSet);
        lineChartUci.getDescription().setText("UCI total cases per day");
        lineChartUci.getDescription().setTextSize(12f);
        lineChartUci.setData(data);
    }

    private void GetHospitalizedTotalCases() {
        ArrayList<Entry> yValues = new ArrayList<>();
        float a = 0f;
        float AVGHospcases = 0;
        float manyDates = 0;
        Cursor c = db.obtainCodeCCAAInformation(code);
        c.moveToPosition(c.getCount() - 14);
        while (c.moveToNext()) {
            {
                c.moveToPrevious();
                int previousCase = Integer.parseInt(c.getString(5));
                c.moveToNext();
                int actualCase = Integer.parseInt(c.getString(5));
                AVGHospcases = AVGHospcases + actualCase - previousCase;
                a = a + 1.0f;
                yValues.add(new Entry(a, Float.parseFloat(c.getString(5))));
                xLabel.add(c.getString(1));
                manyDates = manyDates + 1.0f;
            }
        }
        textHosp.setText("Average augment of people hospitalized per day: " + (AVGHospcases / manyDates));
        XAxis xAxis = lineChartHospi.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(60f);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        LineDataSet firstSet = new LineDataSet(yValues, "Dates");
        firstSet.setColor(Color.GREEN);
        firstSet.setValueTextSize(8f);
        LineData data = new LineData();
        data.addDataSet(firstSet);
        lineChartHospi.getDescription().setText("Hospitalized total cases per day");
        lineChartHospi.getDescription().setTextSize(12f);
        lineChartHospi.setData(data);
    }

    private void GetPcrTotalCases() {
        ArrayList<Entry> yValues = new ArrayList<>();

        float a = 0f;
        float AVGPcrcases = 0;
        float manyDates = 0;
        Cursor c = db.obtainCodeCCAAInformation(code);
        c.moveToPosition(c.getCount() - 14);
        while (c.moveToNext()) {
            {
                c.moveToPrevious();
                int previousCase = Integer.parseInt(c.getString(3));
                c.moveToNext();
                int actualCase = Integer.parseInt(c.getString(3));
                AVGPcrcases = AVGPcrcases + actualCase - previousCase;
                a = a + 1.0f;
                yValues.add(new Entry(a, Float.parseFloat(c.getString(3))));
                xLabel.add(c.getString(1));
                manyDates = manyDates + 1.0f;
            }
        }
        textPcr.setText("Average augment of PCR tests per day: " + (AVGPcrcases / manyDates));
        XAxis xAxis = lineChartPcr.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(60f);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        LineDataSet firstSet = new LineDataSet(yValues, "Dates");
        firstSet.setColor(Color.BLUE);
        firstSet.setValueTextSize(8f);
        LineData data = new LineData();
        data.addDataSet(firstSet);
        lineChartPcr.getDescription().setText("PCR total cases per day");
        lineChartPcr.getDescription().setTextSize(12f);
        lineChartPcr.setData(data);
    }

    private void GetDeceasesTotalCases() {
        ArrayList<Entry> yValues = new ArrayList<>();

        float a = 0f;
        float AVGDeathcases = 0;
        float manyDates = 0;
        Cursor c = db.obtainCodeCCAAInformation(code);
        c.moveToPosition(c.getCount() - 14);
        while (c.moveToNext()) {
            {
                c.moveToPrevious();
                int previousCase = Integer.parseInt(c.getString(7));
                c.moveToNext();
                int actualCase = Integer.parseInt(c.getString(7));
                AVGDeathcases = AVGDeathcases + actualCase - previousCase;
                a = a + 1.0f;
                yValues.add(new Entry(a, Float.parseFloat(c.getString(7))));
                xLabel.add(c.getString(1));
                manyDates = manyDates + 1.0f;
            }
        }
        textDeath.setText("Average amount of deceases per day: " + (AVGDeathcases / manyDates));
        XAxis xAxis = lineChartDeaths.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(60f);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        LineDataSet firstSet = new LineDataSet(yValues, "Dates");
        firstSet.setColor(Color.RED);
        firstSet.setValueTextSize(8f);
        LineData data = new LineData();
        data.addDataSet(firstSet);
        lineChartDeaths.getDescription().setText("Deceases total cases per day");
        lineChartDeaths.getDescription().setTextSize(12f);
        lineChartDeaths.setData(data);
    }

    private class MyXAxisValueFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return xLabel.get((int) value);
        }
    }

    //NavBar funcionality.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        SharedPreferences.Editor editor = prefs.edit();
        switch (menuItem.getItemId()){
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
