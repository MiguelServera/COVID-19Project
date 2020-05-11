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
        final View view = inflater.inflate(R.layout.inflateinfo, null);
                Stat stat = statsList.get(position);
                TextView code = (TextView) view.findViewById(R.id.textView);
                TextView name = (TextView) view.findViewById(R.id.textView2);
                TextView cases = (TextView) view.findViewById(R.id.textView3);
                TextView deaths = (TextView) view.findViewById(R.id.textView4);

                code.setText(stat.getCodeC());
                name.setText(stat.getNameC());
                cases.setText(stat.getCases());
                deaths.setText(stat.getDeaths());
        return view;
    }
}
