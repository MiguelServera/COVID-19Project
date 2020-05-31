package com.example.covid_19stats.UIClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.covid_19stats.POJO.StatPercent;
import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Collections;

public class ShowGraphs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    SharedPreferences prefs;
    static BarChart barChart;
    static PieChart pieTotal, piePercent;
    static DBInterface db;
    private DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_graphs);
        barChart = findViewById(R.id.bargraph);
        pieTotal = findViewById(R.id.pieTotal);
        piePercent = findViewById(R.id.piePercent);
        pieTotal.setExtraOffsets(5, 10, 5, 5);
        pieTotal.setDrawHoleEnabled(false);
        pieTotal.setTransparentCircleRadius(60f);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        db = new DBInterface(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Bundle extras = getIntent().getExtras();
        String graphType = extras.getString("graphType");
        if (graphType.equals("global")) GlobalGraph();
        if (graphType.equals("topten")) {
            CountryGraph();
            PercentGraph();
        }
    }

    public void GlobalGraph() {
        db.obre();
        Cursor c = db.obtainAllGlobalInformation();
        c.moveToFirst();
        float cases, deaths, cured;
        float totalSum = 0, totalDeaths = 0, totalCured = 0;
        while (c.moveToNext()) {
            cases = Float.parseFloat(c.getString(c.getColumnIndex("cases")));
            totalSum = cases + totalSum;
            deaths = Float.parseFloat(c.getString(c.getColumnIndex("deaths")));
            totalDeaths = deaths + totalDeaths;
            cured = Float.parseFloat(c.getString(c.getColumnIndex("cured")));
            totalCured = cured + totalCured;
        }
        c.close();
        db.tanca();
        ArrayList<BarEntry> barEntriesCases = new ArrayList<>();
        ArrayList<BarEntry> barEntriesDeaths = new ArrayList<>();
        ArrayList<BarEntry> barEntriesCured = new ArrayList<>();
        barEntriesCases.add(new BarEntry(0, totalSum));
        barEntriesDeaths.add(new BarEntry(1, totalDeaths));
        barEntriesCured.add(new BarEntry(2, totalCured));

        BarDataSet setDataCases = new BarDataSet(barEntriesCases, "Cases");
        setDataCases.setValueTextSize(14f);
        BarDataSet setDataDeaths = new BarDataSet(barEntriesDeaths, "Deaths");
        setDataDeaths.setValueTextSize(14f);
        BarDataSet setDataCured = new BarDataSet(barEntriesCured, "Cured");
        setDataCured.setValueTextSize(14f);
        setDataCases.setColor(Color.rgb(255,165,0));
        setDataDeaths.setColor(Color.RED);
        setDataCured.setColor(Color.GREEN);

        BarData getData = new BarData();
        getData.addDataSet(setDataCases);
        getData.addDataSet(setDataDeaths);
        getData.addDataSet(setDataCured);
        getData.setBarWidth(0.5f);
        barChart.getDescription().setText("Top Cases Worldwide");
        barChart.getDescription().setTextSize(10f);
        barChart.setData(getData);
        pieTotal.setAlpha(0);
        piePercent.setAlpha(0);
    }

    private void PercentGraph() {
        db.obre();
        Cursor c = db.obtainAllGlobalInformation();
        ArrayList<PieEntry> valuesCCAA = new ArrayList<>();
        ArrayList<StatPercent> topSevenPercent = new ArrayList<>();
        while (c.moveToNext()) {
            Cursor c2 = db.obtainPopulationFromOneCountry(c.getString(1));
            c2.moveToFirst();
            float cases = Integer.parseInt(c.getString(2));
            float population = Integer.parseInt(c2.getString(2));
            float percentOfCases = cases * 100 / population;
            StatPercent newStat = new StatPercent(c.getString(1), percentOfCases);
            topSevenPercent.add(newStat);
        }

        Collections.sort(topSevenPercent, StatPercent.descendingStats);

        for (int i = 0; i <= topSevenPercent.size() - 1; i++) {
            StatPercent sortedStat = topSevenPercent.get(i);
            if (valuesCCAA.size() != 7) {
                if (sortedStat.getPercent() == 4.0 / 0.0) ;

                else {
                    valuesCCAA.add(new PieEntry(sortedStat.getPercent(), sortedStat.getCode()));
                }
            } else {
                break;
            }
        }
        PieDataSet pieDataSet = new PieDataSet(valuesCCAA, "");
        pieDataSet.setValueTextColor(Color.DKGRAY);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setSelectionShift(6f);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter());
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueTextSize(10f);
        piePercent.getDescription().setText("Top 7 countries with more cases per population");
        piePercent.getDescription().setTextSize(13f);
        piePercent.setMinAngleForSlices(30.0f);
        piePercent.setData(pieData);
        piePercent.setEntryLabelColor(Color.BLACK);
        barChart.setAlpha(0);
        c.close();
        db.tanca();
    }


    private void CountryGraph() {
        db.obre();
        Cursor c = db.obtainTopSevenInformation();
        ArrayList<PieEntry> valuesCCAA = new ArrayList<>();
        while (c.moveToNext()) {
            float cases = Float.parseFloat(c.getString(2));
            valuesCCAA.add(new PieEntry(cases, c.getString(1)));
        }
        PieDataSet pieDataSet = new PieDataSet(valuesCCAA, "");
        pieDataSet.setValueTextColor(Color.DKGRAY);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setSelectionShift(6f);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);
        pieTotal.setEntryLabelColor(Color.BLACK);
        pieTotal.setMinAngleForSlices(20.0f);
        pieTotal.setData(pieData);
        pieTotal.getDescription().setText("Top 7 countries with more cases");
        pieTotal.getDescription().setTextSize(13f);
        barChart.setAlpha(0);
        c.close();
        db.tanca();
    }

    class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return value + "%";
        }
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
