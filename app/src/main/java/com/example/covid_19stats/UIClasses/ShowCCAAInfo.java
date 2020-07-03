package com.example.covid_19stats.UIClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_19stats.Adapters.StatsAdapter;
import com.example.covid_19stats.Adapters.StatsFromCCAAAdapter;
import com.example.covid_19stats.POJO.CCAAStats;
import com.example.covid_19stats.POJO.Stat;
import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Class that will show the CCAA stats of each CCAA, we can select an item from the spinner and it will show the relative CCAA.
public class ShowCCAAInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    DBInterface db;
    ArrayList<CCAAStats> arrayCCAA = new ArrayList<>();
    RecyclerView rv;
    String ccaaText;
    StatsFromCCAAAdapter adapterCCAA;
    ImageView flagCCAA;
    Button moreInfoButton;
    SharedPreferences prefs;
    int[] flagsArray;
    boolean firstInfo = true;
    NavigationView navigationView;
    private DrawerLayout drawer;
    Spinner filter, spinner;
    String filterText = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_ccaa_info);
        giveValues();
        spinner = findViewById(R.id.spinnerCCAA);
        filter = findViewById(R.id.spinnerFilter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ccaaNames,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.filterCcaa,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapter2);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        spinner.setOnItemSelectedListener(this);
        filter.setOnItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void giveValues() {
        flagCCAA = findViewById(R.id.flagImage);
        moreInfoButton = findViewById(R.id.moreInfoButton);
        moreInfoButton.setOnClickListener(this);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        flagsArray = new int[]{R.drawable.andalucia, R.drawable.aragon, R.drawable.asturias,
                R.drawable.balears, R.drawable.canarias, R.drawable.cantabria, R.drawable.castillaleon,
                R.drawable.castilla, R.drawable.catalonia, R.drawable.valencia,
                R.drawable.extremadura, R.drawable.galicia, R.drawable.madrid, R.drawable.murcia,
                R.drawable.navarra, R.drawable.vasco, R.drawable.rioja, R.drawable.ceuta, R.drawable.melilla};
        db = new DBInterface(this);
    }

    //if the user desires to have more information, it will start a new activity with detailed graphs.
    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, MoreInfoCCAA.class);
        i.putExtra("code", ccaaText.substring(ccaaText.length() - 2, ccaaText.length()));
        startActivity(i);
    }

    //Start to retrieve the information and show it on item selected.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spinner) {
            ccaaText = parent.getItemAtPosition(position).toString();
            arrayCCAA.clear();
            RetrieveStatsAC();
            SelectFlag();
        } else if (parent == filter)
        {
            filterText = parent.getItemAtPosition(position).toString();
            System.out.println(filterText);
            rv.setAdapter(null);
            adapterCCAA.notifyDataSetChanged();
            inflateCCAA(filterText);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void inflateCCAA(String selectedText) {
        if (firstInfo) {
            adapterCCAA = new StatsFromCCAAAdapter(arrayCCAA);
            rv = (RecyclerView) findViewById(R.id.recyclerViewShowCCAA);
            rv.setLayoutManager(new GridLayoutManager(this, 1));
            rv.setAdapter(adapterCCAA);
        } else {
            rv.setAdapter(null);
            adapterCCAA.notifyDataSetChanged();
            adapterCCAA = new StatsFromCCAAAdapter(arrayCCAA);
            rv = (RecyclerView) findViewById(R.id.recyclerViewShowCCAA);
            rv.setLayoutManager(new GridLayoutManager(this, 1));
            rv.setAdapter(adapterCCAA);
        }
        if (selectedText.equals("PCR asc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat.getPcr(), stat1.getPcr());
                }
            });
        }
        else if (selectedText.equals("PCR desc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat1.getPcr(), stat.getPcr());
                }
            });
        }

        else if(selectedText.equals("TestAC asc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat.getTestAC(), stat1.getTestAC());
                }
            });
        }
        else if(selectedText.equals("TestAC desc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat1.getTestAC(), stat.getTestAC());
                }
            });
        }
        else if(selectedText.equals("Deaths asc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat.getDeaths(), stat1.getDeaths());
                }
            });
        }
        else if(selectedText.equals("Deaths desc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat1.getDeaths(), stat.getDeaths());
                }
            });
        }
        else if(selectedText.equals("Hosp asc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat.getHospitalized(), stat1.getHospitalized());
                }
            });
        }

        else if(selectedText.equals("Hosp desc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat1.getHospitalized(), stat.getHospitalized());
                }
            });
        }
        else if(selectedText.equals("UCI asc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat1.getUci(), stat.getUci());
                }
            });
        }

        else if(selectedText.equals("UCI desc."))
        {
            Collections.sort(arrayCCAA, new Comparator<CCAAStats>() {
                @Override
                public int compare(CCAAStats stat, CCAAStats stat1) {
                    return Integer.compare(stat1.getUci(), stat.getUci());
                }
            });
        }
    }

    //Obtains the information from the database and proceeds to insert it on a CCAAStats object. Then inflate the arrayadapter.
    private void RetrieveStatsAC() {
        db.obre();
        Cursor c = db.obtainCodeCCAAInformation(ccaaText.substring(ccaaText.length() - 2, ccaaText.length()));

        if (!c.moveToPrevious())
        {
            c.moveToLast();
            CCAAStats ccaaStats = new CCAAStats();
            ccaaStats.setCode(c.getString(0));
            ccaaStats.setDate(c.getString(1));
            ccaaStats.setCases(c.getInt(2));
            ccaaStats.setPcr(c.getInt(3));
            ccaaStats.setTestAC(c.getInt(4));
            ccaaStats.setHospitalized(c.getInt(5));
            ccaaStats.setUci(c.getInt(6));
            ccaaStats.setDeaths(c.getInt(7));
            arrayCCAA.add(ccaaStats);
        }

        while (c.moveToPrevious()) {
            CCAAStats ccaaStats = new CCAAStats();
            ccaaStats.setCode(c.getString(0));
            ccaaStats.setDate(c.getString(1));
            ccaaStats.setCases(c.getInt(2));
            ccaaStats.setPcr(c.getInt(3));
            ccaaStats.setTestAC(c.getInt(4));
            ccaaStats.setHospitalized(c.getInt(5));
            ccaaStats.setUci(c.getInt(6));
            ccaaStats.setDeaths(c.getInt(7));
            arrayCCAA.add(ccaaStats);
        }
        inflateCCAA(filterText);
        db.tanca();
    }

    private void SelectFlag() {
        //Doesn't work if I try to put "-1 or -2" on substring
        if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("AN"))
            flagCCAA.setImageResource(flagsArray[0]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("AR"))
            flagCCAA.setImageResource(flagsArray[1]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("AS"))
            flagCCAA.setImageResource(flagsArray[2]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("IB"))
            flagCCAA.setImageResource(flagsArray[3]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CN"))
            flagCCAA.setImageResource(flagsArray[4]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CB"))
            flagCCAA.setImageResource(flagsArray[5]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CL"))
            flagCCAA.setImageResource(flagsArray[6]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CM"))
            flagCCAA.setImageResource(flagsArray[7]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CT"))
            flagCCAA.setImageResource(flagsArray[8]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("VC"))
            flagCCAA.setImageResource(flagsArray[9]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("EX"))
            flagCCAA.setImageResource(flagsArray[10]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("GA"))
            flagCCAA.setImageResource(flagsArray[11]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("MD"))
            flagCCAA.setImageResource(flagsArray[12]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("MC"))
            flagCCAA.setImageResource(flagsArray[13]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("NC"))
            flagCCAA.setImageResource(flagsArray[14]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("PV"))
            flagCCAA.setImageResource(flagsArray[15]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("RI"))
            flagCCAA.setImageResource(flagsArray[16]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("CE"))
            flagCCAA.setImageResource(flagsArray[17]);
        else if (ccaaText.substring(ccaaText.length() - 2, ccaaText.length()).equals("ML"))
            flagCCAA.setImageResource(flagsArray[18]);
    }

    //NavBar funcionality.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        SharedPreferences.Editor editor = prefs.edit();
        switch (menuItem.getItemId()) {
            case R.id.nav_info:
                startActivity(new Intent(this, MenuActivity.class));
                break;

            case R.id.nav_precautions:
                startActivity(new Intent(this, Precautions.class));
                break;

            case R.id.nav_help:
                startActivity(new Intent(this, SymptomsHelp.class));
                break;

            case R.id.nav_logout:
                editor.putBoolean("loggedIn", false);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), MainLogin.class);
                finishAffinity();
                startActivity(intent);
                break;

            case R.id.nav_share:
                Intent shareIntent = new Intent();
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.ACTION_SEND, "CVODI19-Stats app");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Prova la meva aplicació per al actual estat del COVID! https://drive.google.com/open?id=1VjWmxjmgpAcTWxAFZqlOmCG7i8Q6XRVC");
                startActivity(Intent.createChooser(shareIntent, "choose one"));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
