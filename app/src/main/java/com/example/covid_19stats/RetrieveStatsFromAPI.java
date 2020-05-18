package com.example.covid_19stats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

import static com.example.covid_19stats.MainLogin.editor;

public class RetrieveStatsFromAPI extends AppCompatActivity {
    DBInterface db;
    Context appContext;
    LastResults ls;
    InsertEachCountryData countryData;
    Intent i;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBInterface(this);
        appContext = this;
        i = new Intent(getApplicationContext(), RetrieveGlobalStatsFromBD.class);
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
                            return new String("false : " + responseCode);
                        }
                    } catch (Exception e) {
                        editor.putBoolean("firstStart", true);
                        editor.apply();
                return new String("Exception: " + e.getMessage());
            }
        }

        public void onPostExecute(String result) {
            try {
                db.obre();
                db.deleteDatabaseStats();
                db.createTables();
                db.insertActualDate(MainLogin.getDate());
                JSONObject datalist;
                JSONArray stats = new JSONArray(result);

                for (int i = 0; i < stats.length(); i++) {
                    datalist = stats.getJSONObject(i);
                    countryData = new InsertEachCountryData(datalist);
                    countryData.start();
                    ls = new LastResults(datalist);
                    ls.start();
                    globalResults(datalist);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db.tanca();
                startActivity(i);
                finish();
            }
        }
    }

    class InsertEachCountryData extends Thread {
        JSONObject datalist;

        public InsertEachCountryData(JSONObject datalist) {
            this.datalist = datalist;
        }

        public void run() {
            try {
                db.insertCountryCodeAndPopulation(datalist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class LastResults extends Thread {
        JSONObject datalist;
        public LastResults(JSONObject datalist)
        {
            this.datalist = datalist;
        }

        @Override
        public void run() {
            try {
                String object;
                JSONObject insertjsonObject;
                JSONObject datalistObject;
                datalistObject = datalist.getJSONArray("dataList").getJSONObject(datalist.getJSONArray("dataList").length() - 1);

                if (datalist.getString("name").equals("Bonaire, Saint Eustatius and Saba")
                        && datalist.getString("code").equals("")) {

                    object = "{\"code\":\"NoCode\"" + datalist.getString("code")
                            + ",\"name\":\"" + datalist.getString("name")
                            + "\",\"cases\":" + datalistObject.getString("cases") +
                            ",\"deaths\":" + datalistObject.getString("deaths") +
                            ",\"cured\":" + datalistObject.getString("cured") + "}";

                } else if (datalist.getString("code").equals("") ||
                        datalist.getString("code").equals("N/A")) {
                    object = "{\"code\":\"NoCode\"" + ",\"name\":" + datalist.getString("name")
                            + ",\"cases\":" + datalistObject.getString("cases") +
                            ",\"deaths\":" + datalistObject.getString("deaths") +
                            ",\"cured\":" + datalistObject.getString("cured") + "}";

                } else if (datalist.getString("name").equals("Cases_on_an_international_conveyance_Japan")) {
                    object = "{\"code\":\"NoCode\"" + ",\"name\":\"" + datalist.getString("name")
                            + "\",\"cases\":" + datalistObject.getString("cases") +
                            ",\"deaths\":" + datalistObject.getString("deaths") +
                            ",\"cured\":" + datalistObject.getString("cured") + "}";

                } else {
                    object = "{\"code\":" + datalist.getString("code")
                            + ",\"name\":" + datalist.getString("name")
                            + ",\"cases\":" + datalistObject.getString("cases") +
                            ",\"deaths\":" + datalistObject.getString("deaths") +
                            ",\"cured\":" + datalistObject.getString("cured") + "}";
                }

                insertjsonObject = new JSONObject(object);
                if (db.insertInformation(insertjsonObject) != 1) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void globalResults(JSONObject datalist) {
        try {
            String object;
            JSONObject insertjsonObject;
            JSONObject datalistObject = null;
            JSONArray datalistArray;
            int totalSumCases = 0;
            int totalDeathsCases = 0;
            int valueSumCases;
            int valueDeathsCases;
            datalistArray = datalist.getJSONArray("dataList");
            for (int a = 0; a < datalistArray.length(); a++) {
                datalistObject = datalistArray.getJSONObject(a);

                valueSumCases = Integer.parseInt(datalistObject.getString("cases").trim());

                totalSumCases = valueSumCases + totalSumCases;

                valueDeathsCases = Integer.parseInt(datalistObject.getString("deaths").trim());

                totalDeathsCases = valueDeathsCases + totalDeathsCases;

            }

            if (datalist.getString("code").equals("") ||
                    datalist.getString("code").equals("N/A")) {
                object = "{\"code\":\"NoCode\"" + ",\"cases\":" +
                        totalSumCases + ",\"deaths\":" + totalDeathsCases + "}";
            } else {
                object = "{\"code\":" + datalist.getString("code") + ",\"cases\":" +
                        totalSumCases+ ",\"deaths\":" + totalDeathsCases + "}";
            }

            insertjsonObject = new JSONObject(object);
            if (db.insertGlobalInformation(insertjsonObject) != 1) ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

