package com.example.covid_19stats;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
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

public class ProvaActivity extends AppCompatActivity {
    TextView totalCases;
    DBInterface db;
    Context appContext;
    ArrayList<Stat> arrayStats = new ArrayList<Stat>();
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        db = new DBInterface(this);
        appContext = this;
        totalCases = findViewById(R.id.textView5);
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

                URL url = new URL("https://25.18.216.213:45455/api/covidTest");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("pass", "Secret");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(context.getSocketFactory());
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("POST");

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(postDataParams.toString());

                os.flush();
                os.close();

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
            System.out.println(result);
            try {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                String object;
                JSONObject insertjsonObject;
                JSONObject datalist;
                JSONObject datalistObject;
                makeGlobalResult(result);
                JSONArray stats = new JSONArray(result);

                for (int i = 0; i < stats.length(); i++) {
                    datalist = stats.getJSONObject(i);
                    datalistObject = datalist.getJSONArray("dataList").getJSONObject(datalist.getJSONArray("dataList").length() - 1);

                    if (datalist.getString("name").equals("Bonaire, Saint Eustatius and Saba")
                            && datalist.getString("code").equals("")) {
                        object = "{\"code\":\"NoCode\"" + datalist.getString("code") + ",\"name\":\""
                                + datalist.getString("name") + "\",\"cases\":" +
                                datalistObject.getString("cases") +
                                ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    } else if (datalist.getString("code").equals("") ||
                            datalist.getString("code").equals("N/A")) {
                        object = "{\"code\":\"NoCode\"" + ",\"name\":" +
                                datalist.getString("name") + ",\"cases\":" +
                                datalistObject.getString("cases") +
                                ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    } else if (datalist.getString("name").equals("Cases_on_an_international_conveyance_Japan")) {
                        object = "{\"code\":\"NoCode\"" + ",\"name\":\"" + datalist.getString("name") + "\",\"cases\":" +
                                datalistObject.getString("cases") +
                                ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    } else {
                        object = "{\"code\":" + datalist.getString("code") + ",\"name\":" +
                                datalist.getString("name") + ",\"cases\":" +
                                datalistObject.getString("cases") +
                                ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    }
                    insertjsonObject = new JSONObject(object);
                    Stat statsPo = new Gson().fromJson(insertjsonObject.toString(), Stat.class);
                    arrayStats.add(statsPo);
                    db.obre();
                    //db.deleteDatabaseStats();
                    if (db.insertInformation(insertjsonObject) != 1) ;
                    db.tanca();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            inflate(arrayStats);
        }

        public void inflate(final ArrayList<Stat> arrayStat) {
            StatsAdapter inflate = new StatsAdapter(getApplicationContext(), R.layout.inflateinfo, arrayStat);
            lv = (ListView) findViewById(R.id.listview);
            lv.setAdapter(inflate);
        }


        private void makeGlobalResult(final String result) {
            try {
                String object;
                JSONObject insertjsonObject, datalist;
                JSONObject datalistObject = null;
                JSONArray datalistArray;
                JSONArray stats = new JSONArray(result);
                int valueSumCases = 0;
                int valueDeathsCases = 0;

                for (int i = 0; i < stats.length(); i++) {
                    datalist = stats.getJSONObject(i);
                    datalistArray = datalist.getJSONArray("dataList");
                    for (int a = 0; a < datalistArray.length(); a++) {
                        datalistObject = datalistArray.getJSONObject(a);
                        valueSumCases = Integer.parseInt(datalistObject.getString("cases").trim());
                        valueSumCases += valueSumCases;
                        valueDeathsCases = Integer.parseInt(datalistObject.getString("deaths").trim());
                        valueDeathsCases += valueDeathsCases;
                    }

                    if (datalist.getString("code").equals("") ||
                            datalist.getString("code").equals("N/A")) {
                        object = "{\"code\":\"NoCode\"" + ",\"cases\":" +
                                datalistObject.getString("cases") +
                                ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    } else {
                        object = "{\"code\":" + datalist.getString("code") + ",\"cases\":" +
                                valueSumCases + ",\"deaths\":" + valueDeathsCases + "}";
                    }

                    insertjsonObject = new JSONObject(object);
                    Stat statsPo = new Gson().fromJson(insertjsonObject.toString(), Stat.class);
                    arrayStats.add(statsPo);
                    db.obre();
                    if (db.insertGlobalInformation(insertjsonObject) != 1) ;
                    db.tanca();


                    valueSumCases = 0;
                    valueDeathsCases = 0;
                }
                retrieveGlobalInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void retrieveGlobalInfo() {
            try {
                Cursor c = db.obtainAllGlobalInformation();
                c.moveToFirst();
                int cases;
                int totalSum = 0;

                while (!c.isAfterLast()) {
                    cases = Integer.parseInt(c.getString(c.getColumnIndex("cases")));
                    totalSum = cases + totalSum;
                    c.moveToNext();
                }
                totalCases.setText("Global cases: " + totalSum);

            } catch (final Exception ex) {
                Log.i("---", "Exception in thread");
            }
        }
    }
}

