package com.example.covid_19stats;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class MoreInfoCCAA extends AppCompatActivity {
    String[][] arrayCode;
    String code;
    String infoCCAA = "";
    DBInterface db;
    LineChart lineChartUci, lineChartHospi, lineChartPcr, lineChartDeaths;
    TextView textView;
    ArrayList<String> xLabel = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ccaa_graph);
        Bundle extras = getIntent().getExtras();
        code = extras.getString("code");
        System.out.println(code);
        lineChartUci = findViewById(R.id.uciTotal);
        lineChartHospi = findViewById(R.id.hospiTotal);
        lineChartPcr = findViewById(R.id.pcrTotal);
        lineChartDeaths = findViewById(R.id.deathTotal);
        textView = findViewById(R.id.moreInfoCCAA);

        db = new DBInterface(this);
        db.obre();
        GetUciTotalCases();
        GetHospitalizedTotalCases();
        GetPcrTotalCases();
        GetDeceasesTotalCases();
        textView.setText(infoCCAA + "\"" + "If needed, you can pinch(zoom) out and in to see detailed information. ");
        textView.setMovementMethod(new ScrollingMovementMethod());
        db.tanca();
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
        infoCCAA = infoCCAA + "Average augment of people on UCI per day: " + (AVGUcicases / manyDates + "\n");
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
        infoCCAA = infoCCAA + "Average augment of people hospitalized per day: " +(AVGHospcases / manyDates + "\n");
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
        infoCCAA = infoCCAA + "Average augment of people PCR per day: " + (AVGPcrcases / manyDates + "\n");
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
        infoCCAA = infoCCAA + "Average amount of deceases per day: " + (AVGDeathcases / manyDates + "\n");
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
}
