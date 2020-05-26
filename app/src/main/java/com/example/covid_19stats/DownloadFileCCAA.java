package com.example.covid_19stats;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DownloadFileCCAA extends AppCompatActivity {
    File file;
    DBInterface db;
    String description = "";
    String ccaaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/COVIDStats");
        if (file.exists()) file.delete();
        try {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://cnecovid.isciii.es/covid19/resources/agregados.csv");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("My File");
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(false);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "COVIDStats");
            downloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InsertCCAAInfo();
    }

    public void InsertCCAAInfo() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/COVIDStats");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            //Step over the headers
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens[0].length() > 2) {
                    description = description + tokens[0] + "\n";
                }
                if (tokens[0].equals(ccaaText.substring(ccaaText.length() - 2, ccaaText.length())))
                //Doesn't work if I try to put "-1 or -2"
                {
                    CCAAStats acStats = new CCAAStats();
                    acStats.setCode(tokens[0]);
                    acStats.setDate(tokens[1]);
                    if (tokens[2].length() == 0) tokens[2] = "0";
                    acStats.setCases(Integer.parseInt(tokens[2]));

                    if (tokens[3].length() == 0) tokens[3] = "0";
                    acStats.setPcr(Integer.parseInt(tokens[3]));

                    if (tokens[4].length() == 0) tokens[4] = "0";
                    acStats.setTestAC(Integer.parseInt(tokens[4]));

                    if (tokens[5].length() == 0) tokens[5] = "0";
                    acStats.setHospitalized(Integer.parseInt(tokens[5]));

                    if (tokens[6].length() == 0) tokens[6] = "0";
                    acStats.setUci(Integer.parseInt(tokens[6]));

                    if (tokens[7].length() == 0) tokens[7] = "0";
                    acStats.setDeaths(Integer.parseInt(tokens[7]));
                    db.insertCCAAInformation(acStats);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("ThisActivity", "Error readin data file on line");
            e.printStackTrace();
        } finally {
            db.tanca();
        }
    }
}
