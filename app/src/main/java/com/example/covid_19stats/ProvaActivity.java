package com.example.covid_19stats;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class ProvaActivity extends AppCompatActivity implements View.OnClickListener {
    Button button;
    DBInterface bd;
    EditText country;
    EditText cases;
    EditText recus;
    EditText deaths;
    DBInterface db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prova_activity);
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(this);
        bd = new DBInterface(this);
        country = findViewById(R.id.EditText5);
        cases = findViewById(R.id.EditText9);
        recus = findViewById(R.id.EditText10);
        deaths = findViewById(R.id.EditText11);
    }

    @Override
    public void onClick(View view) {
        if (view == button) {
            System.out.println("Hola");
            retrieveInformation();
        }
    }

    public String retrieveInformation() {
        System.out.println("Ha començat el proces");
        try {
            URL url = new URL("https://covidpaucasesnoves.azurewebsites.net/api/covidTest");

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("pass", "Secret");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            System.out.println("Conexion establecida");

            /*OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            */System.out.println("Escribiendo");

            /*writer.flush();
            writer.close();
            os.close();*/

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
                in.close();
                return sb.toString();
            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
