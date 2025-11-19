package com.motivation.quotes.app.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/* loaded from: classes3.dex */
public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap[]> {
    private BitmapDownloadListener listener;

    /* loaded from: classes3.dex */
    public interface BitmapDownloadListener {
        void onBitmapsDownloaded(Bitmap[] bitmapArr);
    }

    public BitmapDownloaderTask(BitmapDownloadListener bitmapDownloadListener) {
        this.listener = bitmapDownloadListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Bitmap[] doInBackground(String... strArr) {
        InputStream inputStream;
        Bitmap[] bitmapArr = new Bitmap[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            try {
                URLConnection openConnection = new URL(strArr[i]).openConnection();
                if (openConnection instanceof HttpsURLConnection) {
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) openConnection;
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.connect();
                    inputStream = httpsURLConnection.getInputStream();
                } else {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                }
                bitmapArr[i] = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e("BitmapDownloaderTask", e.getMessage());
            }
        }
        return bitmapArr;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Bitmap[] bitmapArr) {
        BitmapDownloadListener bitmapDownloadListener = this.listener;
        if (bitmapDownloadListener != null) {
            bitmapDownloadListener.onBitmapsDownloaded(bitmapArr);
        }
    }
}
