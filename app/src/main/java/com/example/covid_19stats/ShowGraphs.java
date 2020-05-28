package com.example.covid_19stats;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ShowGraphs extends AppCompatActivity {
    static BarChart barChart;
    static PieChart pieTotal, piePercent;
    static DBInterface db;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_graphs);
        barChart = findViewById(R.id.bargraph);
        pieTotal = findViewById(R.id.pieTotal);
        piePercent = findViewById(R.id.piePercent);
        pieTotal.setExtraOffsets(5, 10, 5, 5);
        pieTotal.setDrawHoleEnabled(false);
        pieTotal.getDescription().setEnabled(false);
        pieTotal.setTransparentCircleRadius(60f);

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
        float cases, deaths;
        float totalSum = 0, totalDeaths = 0;
        while (c.moveToNext()) {
            cases = Float.parseFloat(c.getString(c.getColumnIndex("cases")));
            totalSum = cases + totalSum;
            deaths = Float.parseFloat(c.getString(c.getColumnIndex("deaths")));
            totalDeaths = deaths + totalDeaths;
        }
        c.close();
        db.tanca();
        ArrayList<BarEntry> barEntriesCases = new ArrayList<>();
        ArrayList<BarEntry> barEntriesDeaths = new ArrayList<>();
        barEntriesCases.add(new BarEntry(0, totalSum));
        barEntriesDeaths.add(new BarEntry(1, totalDeaths));

        BarDataSet setDataCases = new BarDataSet(barEntriesCases, "Cases");
        BarDataSet setDataDeaths = new BarDataSet(barEntriesDeaths, "Deaths");
        setDataCases.setColor(Color.YELLOW);
        setDataDeaths.setColor(Color.RED);

        BarData getData = new BarData();
        getData.addDataSet(setDataCases);
        getData.addDataSet(setDataDeaths);
        getData.setBarWidth(0.5f);
        barChart.setData(getData);
        pieTotal.setAlpha(0);
    }

    private void CountryGraph() {
        db.obre();
        Cursor c = db.obtainTopSevenInformation();
        ArrayList<PieEntry> valuesCCAA = new ArrayList<>();
        while (c.moveToNext()) {
            float cases = Float.parseFloat(c.getString(2));
            valuesCCAA.add(new PieEntry(cases, c.getString(1)));
        }
        PieDataSet pieDataSet = new PieDataSet(valuesCCAA, "Top Cases Worldwide");
        pieDataSet.setValueTextColor(Color.DKGRAY);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setSelectionShift(6f);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);
        pieTotal.setData(pieData);
        barChart.setAlpha(0);
        c.close();
        db.tanca();
    }

    private void PercentGraph() {
        db.obre();
        Cursor c = db.obtainTopSevenInformation();
        ArrayList<PieEntry> valuesCCAA = new ArrayList<>();
        while (c.moveToNext()) {
            Cursor c2 = db.obtainPopulationFromOneCountry(c.getString(1));
            c2.moveToFirst();
            int cases = Integer.parseInt(c.getString(2));
            int population = Integer.parseInt(c2.getString(2));
            float percentOfCases = (float) population / (cases * 100);
            valuesCCAA.add(new PieEntry(percentOfCases, c.getString(1)));
        }
        PieDataSet pieDataSet = new PieDataSet(valuesCCAA, "Top Cases Worldwide");
        pieDataSet.setValueTextColor(Color.DKGRAY);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setSelectionShift(6f);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);
        piePercent.isUsePercentValuesEnabled();
        piePercent.setData(pieData);
        barChart.setAlpha(0);
        c.close();
        db.tanca();
    }
}
