package com.example.covid_19stats;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class ProvaActivity extends AppCompatActivity implements View.OnClickListener {
    Button button;
    EditText country;
    EditText cases;
    EditText recus;
    EditText deaths;
    DBInterface db;
    Context appContext;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prova_activity);
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(this);
        db = new DBInterface(this);
        country = findViewById(R.id.EditText5);
        cases = findViewById(R.id.EditText9);
        recus = findViewById(R.id.EditText10);
        deaths = findViewById(R.id.EditText11);
        appContext = this;
    }

    @Override
    public void onClick(View view) {
        if (view == button) {
            System.out.println("Hola");
            new SendRequest().execute();
            Intent i = new Intent(this, MenuActivity.class);
            startActivity(i);
        }
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
            /*try {
                JSONArray stats = new JSONArray(result);
                Log.i("Stats", stats.length() + "");
                for (int i = 0; i <= stats.length(); i++) {
                    JSONObject prova = stats.getJSONObject(i);
                    Log.i("Prova", prova.toString());
                    try {
                        db.insertInformation(stats.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                inflateCountryCases();
                inflateCountryCode();
                inflateCountryDeaths();
                inflateCountryName();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SQLiteConstraintException c) {
                c.printStackTrace();
            }*/
            try {
                String object = "";
                JSONObject insertjsonObject;
                JSONObject datalist;
                JSONObject datalistObject = null;
                JSONArray datalistArray;
                JSONArray stats = new JSONArray(result);
                for (int i = 0; i < 1; i++)
                {
                    datalist = stats.getJSONObject(i);
                    datalistArray = datalist.getJSONArray("dataList");
                    for ( int a = 0; a < datalistArray.length(); a++)
                    {
                        datalistObject = datalistArray.getJSONObject(a);
                    }
                    object = "{\"code\":" + datalist.getString("code") + ",\"name\":" +
                    datalist.getString("name") + ",\"cases\":" + datalistObject.getString("cases") +
                    ",\"deaths\":" + datalistObject.getString("deaths") + "}";
                    System.out.println(object);
                    insertjsonObject = new JSONObject(object);
                    db.obre();
                    if (db.insertInformation(insertjsonObject) != 1) System.out.println("All good!");
                    db.tanca();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("Retrieve", result);
        }
    }
}
