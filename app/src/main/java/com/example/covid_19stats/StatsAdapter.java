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


        code.setText(stat.getCodeC());
                name.setText(stat.getNameC());
                cases.setText(stat.getCases());
                deaths.setText(stat.getDeaths());
                cured.setText(stat.getCured());

        return view;
    }
}
