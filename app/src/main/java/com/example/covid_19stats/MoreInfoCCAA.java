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
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoreInfoCCAA extends AppCompatActivity {
    String[][] arrayCode;
    String code;
    Cursor c;
    Bundle extras;
    DBInterface db;
    LineChart lineChart;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_graphs);
        arrayCode = new String[][]{{"CL", "17/04/2020"}, {"GA", "28/04/2020"}, {"CM", "12/04/2020"}, {"MD", "26/04/2020"}};
        code = "CL";
        //lineChart = findViewById(R.id.linegraph);
        ArrayList<Entry> yValues = new ArrayList<>();
        db = new DBInterface(this);
        for (int i = 0; i>arrayCode.length; i++){
            if (code.equals(arrayCode[i][0]))
            {
                c = db.obtainCodeCCAAInformation(code);
                c.moveToFirst();
                while (!c.getString(1).equals(arrayCode[i][1]))
                {
                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(c.getString(1));
                        float dateInt = date.getDay();
                        float monthInt = date.getMonth();
                        yValues.add(new Entry(Float.parseFloat(c.getString(6)), i));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        LineDataSet firstSet = new LineDataSet(yValues, "01/01/2001");
        LineData data = new LineData();
        data.addDataSet(firstSet);

    }
}
