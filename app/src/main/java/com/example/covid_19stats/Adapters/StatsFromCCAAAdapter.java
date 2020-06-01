package com.example.covid_19stats.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.covid_19stats.POJO.CCAAStats;
import com.example.covid_19stats.R;

import java.util.ArrayList;
import java.util.List;

//Class to inflate the ListView, which is on the ShowCCAAInfo.inflateCCAA
public class StatsFromCCAAAdapter extends ArrayAdapter {

    private Context context;
    private List<CCAAStats> statsList;

    public StatsFromCCAAAdapter(Context context, int resource, ArrayList<CCAAStats> objects) {
        super(context, resource, objects);
        this.context = context;
        this.statsList = objects;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.inflate_all_ccaa, null);
        CCAAStats stat = statsList.get(position);
        TextView date = (TextView) view.findViewById(R.id.dateCCAA);
        TextView pcr = (TextView) view.findViewById(R.id.pcrCCAA);
        TextView testAC = (TextView) view.findViewById(R.id.testAC);
        TextView hospitalized = (TextView) view.findViewById(R.id.hospitalizedCCAA);
        TextView uci = (TextView) view.findViewById(R.id.uci);
        TextView deaths = (TextView) view.findViewById(R.id.deathsCCAA);

        date.setText(String.valueOf(stat.getDate()));
        pcr.setText(String.valueOf(stat.getPcr()));
        testAC.setText(String.valueOf(stat.getTestAC()));
        hospitalized.setText(String.valueOf(stat.getHospitalized()));
        uci.setText(String.valueOf(stat.getUci()));
        deaths.setText(String.valueOf(stat.getDeaths()));
        return view;
    }
}
