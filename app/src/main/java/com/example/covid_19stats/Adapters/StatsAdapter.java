package com.example.covid_19stats.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_19stats.POJO.Stat;
import com.example.covid_19stats.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

//Class to inflate the ListView, which is on the ShowCountryInfo.inflate
public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> implements View.OnClickListener {
    public List<Stat> statsList;
    private View.OnClickListener listener;

    public StatsAdapter(ArrayList<Stat> objects) {
        this.statsList = objects;
    }

    @NonNull
    @Override
    public StatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_all_info, null, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsAdapter.ViewHolder holder, int position) {
        Stat listStat = statsList.get(position);

        holder.code.setText(listStat.getCode());
        holder.name.setText(listStat.getName());
        holder.cases.setText(String.valueOf(listStat.getCases()));
        holder.deaths.setText(String.valueOf(listStat.getDeaths()));
        holder.cured.setText(String.valueOf(listStat.getCured()));
    }

    @Override
    public int getItemCount() {
        return statsList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener!= null)
        {
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView code, name, cases, deaths, cured;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            code = (TextView) itemView.findViewById(R.id.idCountry);
            name = (TextView) itemView.findViewById(R.id.nameCountry);
            cases = (TextView) itemView.findViewById(R.id.casesCountry);
            deaths = (TextView) itemView.findViewById(R.id.deathsCountry);
            cured = (TextView) itemView.findViewById(R.id.curedCountry);
        }
    }
    public void filterList(ArrayList<Stat> filteredList) {
        statsList = filteredList;
        notifyDataSetChanged();
    }

}
