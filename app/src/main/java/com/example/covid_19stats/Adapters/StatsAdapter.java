package com.example.covid_19stats.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.covid_19stats.POJO.Stat;
import com.example.covid_19stats.R;

import java.util.ArrayList;
import java.util.List;

//Class to inflate the ListView, which is on the ShowCountryInfo.inflate
public class StatsAdapter extends ArrayAdapter {
    private Context context;
    private List<Stat> statsList;

    public StatsAdapter(Context context, int resource, ArrayList<Stat> objects) {
        super(context, resource, objects);
        this.context = context;
        this.statsList = objects;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.inflate_all_info, null);
        Stat stat = statsList.get(position);
        TextView code = (TextView) view.findViewById(R.id.idCountry);
        TextView name = (TextView) view.findViewById(R.id.nameCountry);
        TextView cases = (TextView) view.findViewById(R.id.casesCountry);
        TextView deaths = (TextView) view.findViewById(R.id.deathsCountry);
        TextView cured = (TextView) view.findViewById(R.id.curedCountry);


        code.setText(stat.getCode());
        name.setText(stat.getName());
        cases.setText(String.valueOf(stat.getCases()));
        deaths.setText(String.valueOf(stat.getDeaths()));
        cured.setText(String.valueOf(stat.getCured()));

        return view;
    }
}