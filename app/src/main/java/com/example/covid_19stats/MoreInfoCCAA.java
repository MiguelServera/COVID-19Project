package com.example.covid_19stats;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoreInfoCCAA extends AppCompatActivity {
    String[][] arrayCode;
    String[] arrayDates;
    String code;
    Bundle extras;
    DBInterface db;
    LineChart lineChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_graphs);
        arrayCode = new String[][]{{"CL", "17/4/2020"}, {"GA", "28/4/2020"}, {"CM", "12/4/2020"}, {"MD", "26/4/2020"}};
        code = "CL";
        lineChart = findViewById(R.id.linegraph);
        ArrayList<Entry> yValues = new ArrayList<>();
        db = new DBInterface(this);
        System.out.println(code == arrayCode[0][0]);
        db.obre();
        int a = 0;
        for (int i = 0; i < arrayCode.length; i++) {
            if (code == arrayCode[i][0]) {
                Cursor c = db.obtainCodeCCAAInformation(code);
                c.moveToPosition(c.getCount()-c.getCount()/2);
                arrayDates = new String[c.getCount()-c.getCount()/2];
                while (c.moveToNext()) {
                    if (!c.getString(1).equals(arrayCode[i][1])) {
                        yValues.add(new Entry(a++, Float.parseFloat(c.getString(6))));
                        arrayDates[i] = c.getString(1);
                    }
                }
                break;
            }
        }
        LineDataSet firstSet = new LineDataSet(yValues, "Dates");
        LineData data = new LineData();
        data.addDataSet(firstSet);
        db.tanca();
        lineChart.setData(data);

    }

    private class MyXAxisValueFormatter extends ValueFormatter{
        String dataValue[];
        public MyXAxisValueFormatter(String[] dataValue)
        {
            this.dataValue = dataValue;
        }
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            return dataValue[((int) value)];
        }
    }
}
