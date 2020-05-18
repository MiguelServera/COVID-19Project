package com.example.covid_19stats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StatsFromOneCountryAdapter extends ArrayAdapter {
    private Context context;
    private List<StatFromOneCountry> statsList;

    public StatsFromOneCountryAdapter(Context context, int resource, ArrayList<StatFromOneCountry> objects) {
        super(context, resource, objects);
        this.context = context;
        this.statsList = objects;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.inflate_one_country, null);
                StatFromOneCountry stat = statsList.get(position);
                TextView date = (TextView) view.findViewById(R.id.dateOfCases);
                TextView cases = (TextView) view.findViewById(R.id.casesInDate);
                TextView deaths = (TextView) view.findViewById(R.id.deathsInDate);
                TextView cured = (TextView) view.findViewById(R.id.curedInDate);
                date.setText(stat.getDate());
                cases.setText(stat.getCases());
                deaths.setText(stat.getDeaths());
                cured.setText(stat.getCured());
        return view;
    }
}
