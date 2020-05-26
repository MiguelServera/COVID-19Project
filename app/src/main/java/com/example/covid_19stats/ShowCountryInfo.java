package com.example.covid_19stats;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class ShowCountryInfo extends AppCompatActivity {

    Context appContext;
    Bundle extras;
    String codeCountry, nameCountry;
    DBInterface db;
    ListView lv;
    ArrayList<StatFromOneCountry> arrayStats = new ArrayList<>();
    TextView textNameCountry, totalCases;
    private DrawerLayout drawer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_country_info);
        appContext = this;
        extras = getIntent().getExtras();
        codeCountry = extras.getString("codename");
        nameCountry = extras.getString("name");
        textNameCountry = findViewById(R.id.nameCountry);
        textNameCountry.setText(extras.getString("name"));
        totalCases = findViewById(R.id.detailsCountry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        db = new DBInterface(this);
        new SendRequest().execute();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Resources resources = appContext.getResources();
                InputStream rawResource = resources.openRawResource(R.raw.certificate);
                InputStream caInput = new BufferedInputStream(rawResource);
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                } finally {
                    caInput.close();
                }

                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);

                URL url = new URL("https://192.168.1.70:45455/api/covidTest/" + codeCountry);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(context.getSocketFactory());
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        public void onPostExecute(String result) {
            try {
                System.out.println(result);
                db.obre();
                db.deleteOneCountryStat();
                db.createTableOneCountry();
                JSONArray dataList;
                String object;
                JSONObject dataObject = new JSONObject(result);
                dataList = dataObject.getJSONArray("dataList");

                for (int a = dataList.length() - 1; a > dataList.length() - 31; a--) {
                    if (dataObject.getString("name").equals("Bonaire, Saint Eustatius and Saba")
                            && dataObject.getString("code").equals("")) {

                        object = "{\"code\":\"NoCode\"" + dataObject.getString("code")
                                + ",\"name\": \"" + dataObject.getString("name")
                                + "\",\"date\":" + "\"" + dataList.getJSONObject(a).getJSONObject("date").getString("date") + "\""
                                + ",\"cases\":" + dataList.getJSONObject(a).getString("cases")
                                + ",\"deaths\":" + dataList.getJSONObject(a).getString("deaths")
                                + ",\"cured\":" + dataList.getJSONObject(a).getString("cured") + "}";

                    } else if (dataObject.getString("code").equals("") ||
                            dataObject.getString("code").equals("N/A")) {
                        object = "{\"code\":\"NoCode\""
                                + ",\"name\":" + dataObject.getString("name")
                                + ",\"date\":" + "\"" + dataList.getJSONObject(a).getJSONObject("date").getString("date") + "\""
                                + ",\"cases\":" + dataList.getJSONObject(a).getString("cases")
                                + ",\"deaths\":" + dataList.getJSONObject(a).getString("deaths")
                                + ",\"cured\":" + dataList.getJSONObject(a).getString("cured") + "}";

                    } else if (dataObject.getString("name").equals("Cases_on_an_international_conveyance_Japan")) {
                        object = "{\"code\":\"NoCode\""
                                + ",\"name\":\"" + dataObject.getString("name")
                                + ",\"date\":" + "\"" + dataList.getJSONObject(a).getJSONObject("date").getString("date") + "\""
                                + ",\"cases\":" + dataList.getJSONObject(a).getString("cases")
                                + ",\"deaths\":" + dataList.getJSONObject(a).getString("deaths")
                                + ",\"cured\":" + dataList.getJSONObject(a).getString("cured") + "}";

                    } else {
                        object = "{\"code\":" + dataObject.getString("code")
                                + ",\"name\":" + dataObject.getString("name")
                                + ",\"date\":" + "\"" + dataList.getJSONObject(a).getJSONObject("date").getString("date") + "\""
                                + ",\"cases\":" + dataList.getJSONObject(a).getString("cases")
                                + ",\"deaths\":" + dataList.getJSONObject(a).getString("deaths")
                                + ",\"cured\":" + dataList.getJSONObject(a).getString("cured") + "}";
                    }
                    JSONObject insertJSONObject = new JSONObject(object);
                    System.out.println(insertJSONObject.toString());
                    db.insertStatsFromOneCountry(insertJSONObject);
                }
                System.out.println(dataObject.getString("name"));
                Cursor c = db.obtainOneCountryStat(dataObject.getString("name"));
                System.out.println(c.getCount());
                while (c.moveToNext()) {
                    System.out.println(c.getString(2));
                    StatFromOneCountry indiStat = new StatFromOneCountry(c.getString(2),
                            c.getInt(3),
                            c.getInt(4),
                            c.getInt(5));
                    arrayStats.add(indiStat);
                }
                c.close();
                inflate(arrayStats);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                totalCasesCountry();
                db.tanca();
            }
        }

        private void inflate(ArrayList<StatFromOneCountry> arrayStat) {
            StatsFromOneCountryAdapter inflate = new StatsFromOneCountryAdapter(getApplicationContext(), R.layout.inflate_one_country, arrayStat);
            lv = (ListView) findViewById(R.id.listViewShowCountry);
            lv.setAdapter(inflate);
        }

        public void totalCasesCountry()
        {
            Cursor c = db.obtainCountryInformation(nameCountry);
            c.moveToFirst();
            totalCases.setText("  Global cases: " + c.getInt(2) + "\n" + "  Total deaths: " + c.getInt(3) + "\n" + "  Total cured: " + c.getInt(4));

        }
    }
}
