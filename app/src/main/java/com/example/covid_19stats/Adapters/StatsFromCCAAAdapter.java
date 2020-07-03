package com.example.covid_19stats.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_19stats.POJO.CCAAStats;
import com.example.covid_19stats.R;

import java.util.ArrayList;
import java.util.List;

//Class to inflate the ListView, which is on the ShowCCAAInfo.inflateCCAA
public class StatsFromCCAAAdapter extends RecyclerView.Adapter<StatsFromCCAAAdapter.ViewHolder> implements View.OnClickListener {
    public List<CCAAStats> ccaaStatsList;
    private View.OnClickListener listener;

    public StatsFromCCAAAdapter(ArrayList<CCAAStats> objects) {this.ccaaStatsList = objects;}

    @NonNull
    @Override
    public StatsFromCCAAAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_all_ccaa, null, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsFromCCAAAdapter.ViewHolder holder, int position) {
        CCAAStats listCCAA = ccaaStatsList.get(position);
        holder.date.setText(String.valueOf(listCCAA.getDate()));
        holder.pcr.setText(String.valueOf(listCCAA.getPcr()));
        holder.testAC.setText(String.valueOf(listCCAA.getTestAC()));
        holder.hospitalized.setText(String.valueOf(listCCAA.getHospitalized()));
        holder.uci.setText(String.valueOf(listCCAA.getUci()));
        holder.deaths.setText(String.valueOf(listCCAA.getDeaths()));
    }

    @Override
    public int getItemCount() {
        return ccaaStatsList.size();
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
        TextView date, pcr, testAC, hospitalized, uci, deaths;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.dateCCAA);
            pcr = (TextView) itemView.findViewById(R.id.pcrCCAA);
            testAC = (TextView) itemView.findViewById(R.id.testAC);
            hospitalized = (TextView) itemView.findViewById(R.id.hospitalizedCCAA);
            uci = (TextView) itemView.findViewById(R.id.uci);
            deaths = (TextView) itemView.findViewById(R.id.deathsCCAA);
        }
    }
}
