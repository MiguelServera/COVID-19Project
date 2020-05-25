package com.example.covid_19stats;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ShowGraphs extends AppCompatActivity {
    static BarChart barChart;
    static PieChart pieChart;
    static DBInterface db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_graphs);
        barChart = findViewById(R.id.bargraph);
        pieChart = findViewById(R.id.piegraph);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setTransparentCircleRadius(60f);
        db = new DBInterface(this);
        Bundle extras = getIntent().getExtras();
        String graphType = extras.getString("graphType");
        if (graphType.equals("global")) GlobalGraph();
        if (graphType.equals("topten")) CountryGraph();
        if (graphType.equals("ccaa")) CCAAGraph();
    }

    public void GlobalGraph() {
        System.out.println("Y ahora por squi");
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
    }

    private void CountryGraph() {
        db.obre();
        Cursor c= db.obtainTopTenInformation();
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
        pieChart.setData(pieData);
        c.close();
        db.tanca();
    }

    private void CCAAGraph() {
    }
}
