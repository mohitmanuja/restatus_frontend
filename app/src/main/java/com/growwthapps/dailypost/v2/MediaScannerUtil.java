package com.growwthapps.dailypost.v2;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

public class MediaScannerUtil {

    public static void scanFile(Context context, String filePath) {
        MediaScannerConnection.scanFile(context, new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                // Scan completed, the file should now be visible in the gallery
                System.out.println("Scanned " + path + ":");
                System.out.println("-> uri=" + uri);
            }
        });
    }
}