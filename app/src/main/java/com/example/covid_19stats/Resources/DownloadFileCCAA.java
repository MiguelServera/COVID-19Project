package com.example.covid_19stats.Resources;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_19stats.POJO.CCAAStats;
import com.example.covid_19stats.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*Class to download de CSV file to retrieve CCAA information.
Once downloaded, it inserts the information on the database.
Couldn't do this on background because it was creating conflicts with the other background method, on the RetrieveStatsFromAPI class.
Instead, I used a thread to insert it on the database.
 */
public class DownloadFileCCAA extends AppCompatActivity {
    File file;
    DBInterface db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar_activity);
        db = new DBInterface(this);
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
        } finally {
            Download download = new Download();
            download.start();
        }
    }

    private class Download extends Thread{
        public void run()
        {
            db.obre();
            db.deleteDatabaseCCAA();
            db.createCCAACountry();
            while(true)
            {
                if (file.exists()) break;
            }
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+"COVIDStats");
            System.out.println(file);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                //Step over the headers
                Download.sleep(180);
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split(",", -1);
                    if (tokens[0].length() > 2) {
                    } else {
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
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                finish();
                db.tanca();
            }
        }
    }
}
