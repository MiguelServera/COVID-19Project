package com.example.covid_19stats.UIClasses;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class CountryComparator extends AppCompatActivity implements View.OnClickListener {
    DrawerLayout drawer;
    DBInterface db;
    AutoCompleteTextView editFirstCountry, editSecondCountry;
    Button compareButton;
    LineChart currentActiveCases, currentTotalCured, currentTotalCases, currentTotalDeaths;
    ArrayList<String> xLabel = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comparelayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        giveValues();
    }

    private void giveValues() {
        db = new DBInterface(this);
        editFirstCountry = findViewById(R.id.editCountry1);
        editSecondCountry = findViewById(R.id.editCountry2);
        String [] arrayCountries = getResources().getStringArray(R.array.countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayCountries);
        editFirstCountry.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayCountries);
        editSecondCountry.setAdapter(adapter2);
        compareButton = findViewById(R.id.compareButton);
        compareButton.setOnClickListener(this);
        currentActiveCases = findViewById(R.id.compareCurrentInfected);
        currentTotalCured = findViewById(R.id.compareCured);
        currentTotalCases = findViewById(R.id.compareCases);
        currentTotalDeaths = findViewById(R.id.compareDeaths);
    }

    @Override
    public void onClick(View v) {
        if (v == compareButton) {
            currentTotalDeaths.clear();
            currentActiveCases.clear();
            currentTotalCases.clear();
            currentTotalCured.clear();
            try {
                db.obre();
                CompareDeaths cd = new CompareDeaths();
                CompareCured cc = new CompareCured();
                CompareCases ccases = new CompareCases();
                CompareCurrentCases currcases = new CompareCurrentCases();
                currcases.start();
                ccases.start();
                cc.start();
                cd.start();
            } finally {
            }
        }
    }

    class CompareDeaths extends Thread {
        @Override
        public void run() {
                ArrayList<Entry> yValues = new ArrayList<>();
                ArrayList<Entry> yValues2 = new ArrayList<>();
                Cursor c = db.obtainLastCountryInformation(editFirstCountry.getText().toString());
                Cursor c1 = db.obtainLastCountryInformation(editSecondCountry.getText().toString());
                while (c.moveToNext()) {
                    yValues.add(new Entry(c.getPosition(), c.getInt(5)));

                    xLabel.add(c.getString(3));
                }

                while (c1.moveToNext()) {
                    yValues2.add(new Entry(c1.getPosition(), c1.getInt(5)));
                }

                XAxis xAxis = currentTotalDeaths.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setLabelRotationAngle(60f);
                xAxis.setValueFormatter(new MyXAxisValueFormatter());
                LineDataSet firstSet = new LineDataSet(yValues, editFirstCountry.getText().toString());
                LineDataSet secondSet = new LineDataSet(yValues2, editSecondCountry.getText().toString());
                firstSet.setColor(Color.MAGENTA);
                firstSet.setValueTextSize(8f);
                LineData data = new LineData();
                data.addDataSet(firstSet);
                data.addDataSet(secondSet);
                currentTotalDeaths.getDescription().setText("Total deaths per day");
                currentTotalDeaths.getDescription().setTextSize(12f);
                currentTotalDeaths.setData(data);
        }
    }

       class CompareCured extends Thread{
           @Override
           public void run() {
               ArrayList<Entry> yValues = new ArrayList<>();
               ArrayList<Entry> yValues2 = new ArrayList<>();
               Cursor c = db.obtainLastCountryInformation(editFirstCountry.getText().toString());
               Cursor c1 = db.obtainLastCountryInformation(editSecondCountry.getText().toString());
               while (c.moveToNext())
               {
                   yValues.add(new Entry(c.getPosition(), c.getInt(6)));

                   xLabel.add(c.getString(3));
               }

               while (c1.moveToNext())
               {
                   yValues2.add(new Entry(c1.getPosition(), c1.getInt(6)));
               }

               XAxis xAxis = currentTotalCured.getXAxis();
               xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
               xAxis.setDrawGridLines(false);
               xAxis.setLabelRotationAngle(60f);
               xAxis.setValueFormatter(new MyXAxisValueFormatter());
               LineDataSet firstSet = new LineDataSet(yValues, editFirstCountry.getText().toString());
               LineDataSet secondSet = new LineDataSet(yValues2, editSecondCountry.getText().toString());
               firstSet.setColor(Color.MAGENTA);
               firstSet.setValueTextSize(8f);
               LineData data = new LineData();
               data.addDataSet(firstSet);
               data.addDataSet(secondSet);
               currentTotalCured.getDescription().setText("Total cured per day");
               currentTotalCured.getDescription().setTextSize(12f);
               currentTotalCured.setData(data);
           }
        }

        class CompareCases extends Thread {
            @Override
            public void run() {
                ArrayList<Entry> yValues = new ArrayList<>();
                ArrayList<Entry> yValues2 = new ArrayList<>();
                Cursor c = db.obtainLastCountryInformation(editFirstCountry.getText().toString());
                Cursor c1 = db.obtainLastCountryInformation(editSecondCountry.getText().toString());
                while (c.moveToNext()) {
                    yValues.add(new Entry(c.getPosition(), c.getInt(4)));

                    xLabel.add(c.getString(3));
                }

                while (c1.moveToNext()) {
                    yValues2.add(new Entry(c1.getPosition(), c1.getInt(4)));
                }

                XAxis xAxis = currentTotalCases.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setLabelRotationAngle(60f);
                xAxis.setValueFormatter(new MyXAxisValueFormatter());
                LineDataSet firstSet = new LineDataSet(yValues, editFirstCountry.getText().toString());
                LineDataSet secondSet = new LineDataSet(yValues2, editSecondCountry.getText().toString());
                firstSet.setColor(Color.MAGENTA);
                firstSet.setValueTextSize(8f);
                LineData data = new LineData();
                data.addDataSet(firstSet);
                data.addDataSet(secondSet);
                currentTotalCases.getDescription().setText("Total cases per day");
                currentTotalCases.getDescription().setTextSize(12f);
                currentTotalCases.setData(data);
            }
        }

        class CompareCurrentCases extends Thread {
            @Override
            public void run() {
                ArrayList<Entry> yValues = new ArrayList<>();
                ArrayList<Entry> yValues2 = new ArrayList<>();
                Cursor c = db.obtainLastCountryInformation(editFirstCountry.getText().toString());
                Cursor c1 = db.obtainLastCountryInformation(editSecondCountry.getText().toString());
                while (c.moveToNext()) {
                    int isItZero = c.getInt(4) - c.getInt(5) - c.getInt(6);
                    if (isItZero < 0) yValues.add(new Entry(c.getPosition(), 0));
                    else yValues.add(new Entry(c.getPosition(), isItZero));
                    xLabel.add(c.getString(3));
                }

                while (c1.moveToNext()) {
                    int isItZero = c1.getInt(4) - c1.getInt(5) - c1.getInt(6);
                    if (isItZero < 0) yValues.add(new Entry(c1.getPosition(), 0));
                    else yValues2.add(new Entry(c1.getPosition(), isItZero));
                }

                XAxis xAxis = currentActiveCases.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setLabelRotationAngle(60f);
                xAxis.setValueFormatter(new MyXAxisValueFormatter());
                LineDataSet firstSet = new LineDataSet(yValues, editFirstCountry.getText().toString());
                LineDataSet secondSet = new LineDataSet(yValues2, editSecondCountry.getText().toString());
                firstSet.setColor(Color.MAGENTA);
                firstSet.setValueTextSize(8f);
                LineData data = new LineData();
                data.addDataSet(firstSet);
                data.addDataSet(secondSet);
                currentActiveCases.getDescription().setText("Active cases per day");
                currentActiveCases.getDescription().setTextSize(12f);
                currentActiveCases.setData(data);
            }
        }

        private class MyXAxisValueFormatter extends ValueFormatter {

            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int) value);
            }
        }
    }
