package com.example.covid_19stats.Resources;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_19stats.UIClasses.MenuActivity;
import com.example.covid_19stats.R;
import com.example.covid_19stats.UIClasses.ShowGlobalStats;
import com.google.gson.JsonArray;

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


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/*Class to download the information from the API.
Once downloaded it inserts all the necessary information on the database.
 */

public class RetrieveStatsFromAPI extends AppCompatActivity {
    DBInterface db;
    Context appContext;
    GlobalResults gr;
    Intent i;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar_activity);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        db = new DBInterface(this);
        appContext = this;
        i = new Intent(getApplicationContext(), ShowGlobalStats.class);
        new SendRequest().execute();
    }

    //Background asyntask to download the information
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

                URL url = new URL("https://192.168.1.70:45455/api/covidTest");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("pass", "Secret");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(context.getSocketFactory());
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(15 * 100);
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
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstStart", true);
                    editor.apply();
                    finish();
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstStart", true);
                editor.apply();
                finish();
                return new String("Exception: " + e.getMessage());
            }
        }

        //I call every thread here so it can all execute in one method.
        public void onPostExecute(String result) {
            try {
                db.obre();
                db.deleteDatabaseStats();
                db.createTables();
                db.insertActualDate(MenuActivity.getDate());
                JSONObject datalist;
                JSONArray stats = new JSONArray(result);
                for (int i = 0; i < stats.length(); i++) {
                    datalist = stats.getJSONObject(i);
                    gr = new GlobalResults(datalist);
                    gr.start();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }
    }

    /*Thread extended class which will insert only the last information of each country.
    The API didn't returned correct values so I had to do several checks to ensure the insertion.
     */
    class GlobalResults extends Thread {
        JSONObject datalist;

        public GlobalResults(JSONObject datalist) {
            this.datalist = datalist;
        }

        @Override
        public void run() {
            try {
                String object;
                JSONObject insertjsonObject;
                JSONObject datalistObject;
                int dataListLength = datalist.getJSONArray("dataList").length();
                    for (int i = dataListLength-8; i <= dataListLength - 1;i++) {
                        datalistObject = datalist.getJSONArray("dataList").getJSONObject(i);
                        if (datalist.getString("name").equals("Bonaire, Saint Eustatius and Saba")) {
                            object = "{\"code\":" + datalist.getString("code") +
                                    ",\"name\":\"" + datalist.getString("name") +
                                    "\",\"date\":" + "\"" + datalistObject.getJSONObject("date").getString("date") + "\"" +
                                    ",\"cases\":" + datalistObject.getString("cases") +
                                    ",\"deaths\":" + datalistObject.getString("deaths") +
                                    ",\"cured\":" + datalistObject.getString("cured") + "}";

                        } else if (datalist.getString("code").equals("") ||
                                datalist.getString("code").equals("N/A")) {
                            object = "{\"code\":\"NoCode\"" +
                                    ",\"name\":" + datalist.getString("name") +
                                    ",\"date\":" + datalistObject.getJSONObject("date").getString("date") +
                                    ",\"cases\":" + datalistObject.getString("cases") +
                                    ",\"deaths\":" + datalistObject.getString("deaths") +
                                    ",\"cured\":" + datalistObject.getString("cured") + "}";

                        } else if (datalist.getString("name").equals("Cases_on_an_international_conveyance_Japan")) {
                            object = "{\"code\":\"NoCode\"" +
                                    ",\"name\":\"" + datalist.getString("name") +
                                    ",\"date\":" + "\"" + datalistObject.getJSONObject("date").getString("date") + "\"" +
                                    ",\"cases\":" + datalistObject.getString("cases") +
                                    ",\"deaths\":" + datalistObject.getString("deaths") +
                                    ",\"cured\":" + datalistObject.getString("cured") + "}";

                        } else {
                            object = "{\"code\":" + datalist.getString("code") +
                                    ",\"name\":" + datalist.getString("name") +
                                    ",\"date\":" + "\"" + datalistObject.getJSONObject("date").getString("date") + "\"" +
                                    ",\"cases\":" + datalistObject.getString("cases") +
                                    ",\"deaths\":" + datalistObject.getString("deaths") +
                                    ",\"cured\":" + datalistObject.getString("cured") + "}";
                        }
                        insertjsonObject = new JSONObject(object);
                        if (db.insertLastInformation(insertjsonObject) != 1) ;
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            globalResults(datalist);
        }

        /*Method which will insert the total of cases, cured and deaths.
        The API didn't returned correct values so I had to do several checks to ensure the insertion.
         */
        private void globalResults(JSONObject datalist) {
            try {
                String object;
                JSONObject insertjsonObject;
                JSONObject datalistObject = null;
                JSONArray datalistArray;
                int totalSumCases = 0;
                int totalDeathsCases = 0;
                int totalCuredCases = 0;
                int valueSumCases;
                int valueDeathsCases;
                int valueCuredCases;
                datalistArray = datalist.getJSONArray("dataList");
                for (int a = 0; a < datalistArray.length(); a++) {
                    datalistObject = datalistArray.getJSONObject(a);
                    valueSumCases = Integer.parseInt(datalistObject.getString("cases").trim());
                    totalSumCases = valueSumCases + totalSumCases;

                    valueDeathsCases = Integer.parseInt(datalistObject.getString("deaths").trim());

                    totalDeathsCases = valueDeathsCases + totalDeathsCases;

                    valueCuredCases = Integer.parseInt(datalistObject.getString("cured").trim());

                    totalCuredCases = valueCuredCases + totalCuredCases;
                }

                if (datalist.getString("name").equals("Bonaire, Saint Eustatius and Saba")) {
                    object = "{\"geoID\":" + datalist.getString("geoID")
                            + ",\"name\":\"" + datalist.getString("name") +
                            "\",\"population\":" + datalist.getString("population")
                            + "\",\"cases\":" + totalSumCases +
                            ",\"deaths\":" + totalDeathsCases +
                            ",\"cured\":" + totalCuredCases + "}";

                } else if (datalist.getString("name").equals("Cases_on_an_international_conveyance_Japan")) {
                    object = "{\"geoID\":" + datalist.getString("geoID") +
                            ",\"name\":\"" + datalist.getString("name") +
                            ",\"population\":" + datalist.getString("population") +
                            "\",\"cases\":" + totalSumCases +
                            ",\"deaths\":" + totalDeathsCases +
                            ",\"cured\":" + totalCuredCases + "}";

                } else if (datalist.getString("code").equals("") ||
                        datalist.getString("code").equals("N/A")) {
                    object = "{\"geoID\":" + datalist.getString("geoID") +
                            ",\"name\":" + datalist.getString("name") +
                            ",\"population\":" + datalist.getString("population") +
                            ",\"cases\":" + totalSumCases +
                            ",\"deaths\":" + totalDeathsCases +
                            ",\"cured\":" + totalCuredCases + "}";
                } else {
                    object = "{\"geoID\":" + datalist.getString("geoID") +
                            ",\"name\":" + datalist.getString("name") +
                            ",\"population\":" + datalist.getString("population") +
                            ",\"cases\":" + totalSumCases +
                            ",\"deaths\":" + totalDeathsCases +
                            ",\"cured\":" + totalCuredCases + "}";
                }
                insertjsonObject = new JSONObject(object);
                if (db.insertPopulationAndTotalValues(insertjsonObject) != 1) ;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

