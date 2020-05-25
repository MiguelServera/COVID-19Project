package com.example.covid_19stats;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class DownloadFileCCAA extends AppCompatActivity {
    File file;

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
        finish();
    }
}
