package com.example.covid_19stats;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class ProvaActivity extends AppCompatActivity{
    EditText country;
    EditText cases;
    EditText recus;
    EditText deaths;
    DBInterface db;
    Context appContext;
    ArrayList<Stat> arrayStats = new ArrayList<Stat>();
    ListView lv;
    MenuActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        db = new DBInterface(this);
        country = findViewById(R.id.EditText5);
        cases = findViewById(R.id.EditText9);
        recus = findViewById(R.id.EditText10);
        deaths = findViewById(R.id.EditText11);
        appContext = this;
        new SendRequest().execute();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            System.out.println("Ha començat el proces");
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Resources resources = appContext.getResources();
                InputStream rawResource = resources.openRawResource(R.raw.certificate);
                InputStream caInput = new BufferedInputStream(rawResource);
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
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
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestMethod("POST");
                System.out.println("Conexion establecida");

                Log.i("JSON", postDataParams.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(postDataParams.toString());

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        System.out.println("Poniendo informacion en stringbuffer");
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
                String object = "";
                JSONObject insertjsonObject;
                JSONObject datalist;
                JSONObject datalistObject = null;
                JSONArray datalistArray;

                JSONArray stats = new JSONArray(result);
                Log.i("statsArray", stats.length() + "");
                for (int i = 0; i < stats.length(); i++)
                {
                    datalist = stats.getJSONObject(i);
                    datalistArray = datalist.getJSONArray("dataList");
                    Log.i("DataList", "" + datalistArray.length());
                    for ( int a = 0; a < datalistArray.length(); a++)
                    {
                        datalistObject = datalistArray.getJSONObject(a);
                    }

                    if (datalist.getString("name").equals("Bonaire, Saint Eustatius and Saba")
                            && datalist.getString("code").equals("")){
                    object = "{\"code\":\"NoCode\"" + datalist.getString("code") + ",\"name\":\""
                            + datalist.getString("name") + "\",\"cases\":" +
                            datalistObject.getString("cases") +
                            ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    }

                    else if (datalist.getString("code").equals("") ||
                            datalist.getString("code").equals("N/A")){
                        object = "{\"code\":\"NoCode\"" + ",\"name\":" +
                                datalist.getString("name") + ",\"cases\":" +
                                datalistObject.getString("cases") +
                                ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    }

                    else if (datalist.getString("name").equals("Cases_on_an_international_conveyance_Japan")){
                        object = "{\"code\":\"NoCode\"" + ",\"name\":\"" + datalist.getString("name") + "\",\"cases\":" +
                                datalistObject.getString("cases") +
                                ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    }

                    else
                    {
                        object = "{\"code\":" + datalist.getString("code") + ",\"name\":" +
                                datalist.getString("name") + ",\"cases\":" +
                                datalistObject.getString("cases") +
                                ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    }

                    insertjsonObject = new JSONObject(object);
                    Stat statsPo = new Gson().fromJson(insertjsonObject.toString(), Stat.class);
                    arrayStats.add(statsPo);
                    db.obre();
                    if (db.insertInformation(insertjsonObject) != 1) System.out.println("All good!");
                    db.tanca();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            inflate(arrayStats);
            Log.i("Retrieve", result);
        }

        public void inflate(ArrayList<Stat> arrayStat) {
            StatsAdapter inflate = new StatsAdapter(getApplicationContext(), R.layout.inflateinfo, arrayStat);
            lv = (ListView) findViewById(R.id.listview);
            lv.setAdapter(inflate);
        }
    }
}
