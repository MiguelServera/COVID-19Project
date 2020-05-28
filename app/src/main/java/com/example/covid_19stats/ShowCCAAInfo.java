package com.example.covid_19stats;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;

public class ShowCCAAInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    DBInterface db;
    ArrayList<CCAAStats> arrayCCAA = new ArrayList<>();
    ListView lv;
    String ccaaText;
    StatsFromCCAAAdapter adapterCCAA;
    String description = "";
    TextView descriptionText;
    ImageView flagCCAA;
    Button moreInfoButton;
    int[] flagsArray;
    boolean firstInfo = true;
    private DrawerLayout drawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_ccaa_info);
        Spinner spinner = findViewById(R.id.spinnerCCAA);
        flagCCAA = findViewById(R.id.flagImage);
        moreInfoButton = findViewById(R.id.moreInfoButton);
        moreInfoButton.setOnClickListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ccaaNames, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        flagsArray = new int[]{R.drawable.andalucia, R.drawable.aragon, R.drawable.asturias,
                R.drawable.balears, R.drawable.canarias, R.drawable.cantabria, R.drawable.castillaleon,
                R.drawable.castilla, R.drawable.catalonia, R.drawable.valencia,
                R.drawable.extremadura, R.drawable.galicia, R.drawable.madrid, R.drawable.murcia,
                R.drawable.navarra, R.drawable.vasco, R.drawable.rioja, R.drawable.ceuta, R.drawable.melilla};
        db = new DBInterface(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, MoreInfoCCAA.class);
        i.putExtra("code", ccaaText.substring(ccaaText.length() - 2, ccaaText.length()));
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ccaaText = parent.getItemAtPosition(position).toString();
        arrayCCAA.clear();
        RetrieveStatsAC();
        SelectFlag();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void inflateCCAA() {
        if (firstInfo) {
            adapterCCAA = new StatsFromCCAAAdapter(getApplicationContext(), R.layout.inflate_all_ccaa, arrayCCAA);
            lv = (ListView) findViewById(R.id.listViewShowCCAA);
            lv.setAdapter(adapterCCAA);
        } else {
            adapterCCAA.clear();
            adapterCCAA.notifyDataSetChanged();
            adapterCCAA = new StatsFromCCAAAdapter(getApplicationContext(), R.layout.inflate_all_ccaa, arrayCCAA);
            lv = (ListView) findViewById(R.id.listViewShowCCAA);
            lv.setAdapter(adapterCCAA);
        }
    }

    private void RetrieveStatsAC() {
        db.obre();
        Cursor c = db.obtainAllCCAAInformation();
        c.moveToLast();
        while (c.moveToPrevious()) {
            CCAAStats ccaaStats = new CCAAStats();
            ccaaStats.setCode(c.getString(0));
            ccaaStats.setDate(c.getString(1));
            ccaaStats.setCases(c.getInt(2));
            ccaaStats.setPcr(c.getInt(3));
            ccaaStats.setTestAC(c.getInt(4));
            Log.i("TestAC", String.valueOf(c.getInt(4)));
            ccaaStats.setHospitalized(c.getInt(5));
            ccaaStats.setUci(c.getInt(6));
            ccaaStats.setDeaths(c.getInt(7));
            arrayCCAA.add(ccaaStats);
        }
        inflateCCAA();
        db.tanca();
    }

    private void SelectFlag() {
        //Doesn't work if I try to put "-1 or -2" on substring
        if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("AN")) flagCCAA.setImageResource(flagsArray[0]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("AR")) flagCCAA.setImageResource(flagsArray[1]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("AS")) flagCCAA.setImageResource(flagsArray[2]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("IB")) flagCCAA.setImageResource(flagsArray[3]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CN")) flagCCAA.setImageResource(flagsArray[4]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CB")) flagCCAA.setImageResource(flagsArray[5]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CL")) flagCCAA.setImageResource(flagsArray[6]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CM")) flagCCAA.setImageResource(flagsArray[7]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CT")) flagCCAA.setImageResource(flagsArray[8]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("VC")) flagCCAA.setImageResource(flagsArray[9]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("EX")) flagCCAA.setImageResource(flagsArray[10]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("GA")) flagCCAA.setImageResource(flagsArray[11]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("MD")) flagCCAA.setImageResource(flagsArray[12]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("MC")) flagCCAA.setImageResource(flagsArray[13]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("NC")) flagCCAA.setImageResource(flagsArray[14]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("PV")) flagCCAA.setImageResource(flagsArray[15]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("RI")) flagCCAA.setImageResource(flagsArray[16]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CE")) flagCCAA.setImageResource(flagsArray[17]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("ML")) flagCCAA.setImageResource(flagsArray[18]);
    }
}
