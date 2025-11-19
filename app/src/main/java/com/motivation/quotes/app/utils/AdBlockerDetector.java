package com.motivation.quotes.app.utils;

import android.os.Handler;
import android.os.Looper;

import java.net.HttpURLConnection;
import java.net.URL;

public class AdBlockerDetector {

    public interface AdBlockerListener {
        void onAdBlockerDetected();
        void onAdBlockerNotDetected();
    }

    public static void checkAdBlocker(AdBlockerListener listener) {
        new Thread(() -> {
            try {
                // Try connecting to an AdMob URL
                URL url = new URL("https://googleads.g.doubleclick.net");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(2500);
                connection.setReadTimeout(3000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    // Ad service reachable
                    new Handler(Looper.getMainLooper()).post(listener::onAdBlockerNotDetected);
                } else {
                    // Ad service blocked
                    new Handler(Looper.getMainLooper()).post(listener::onAdBlockerDetected);
                }
            } catch (Exception e) {
                // Network issue or blocked
                new Handler(Looper.getMainLooper()).post(listener::onAdBlockerDetected);
            }
        }).start();
    }

    /*public static void checkAdBlocker(AdBlockerListener listener) {
        new Thread(() -> {
            String[] testUrls = {
                    "https://googleads.g.doubleclick.net", // AdMob / Google
                    "https://ads.facebook.com",            // Facebook Ads
                    "https://config.unityads.unity3d.com", // Unity Ads
                    "https://d.applovin.com"               // AppLovin
            };

            boolean adBlocked = false;

            for (String testUrl : testUrls) {
                try {
                    URL url = new URL(testUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        adBlocked = true;
                        break;
                    }
                } catch (Exception e) {
                    adBlocked = true;
                    break;
                }
            }

            final boolean isBlocked = adBlocked;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isBlocked) {
                    listener.onAdBlockerDetected();
                } else {
                    listener.onAdBlockerNotDetected();
                }
            });
        }).start();
    }*/

}
