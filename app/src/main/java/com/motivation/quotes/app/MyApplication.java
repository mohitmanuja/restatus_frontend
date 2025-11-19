package com.motivation.quotes.app;





import static com.motivation.quotes.app.utils.Constant.IS_SUBSCRIBE;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.motivation.quotes.app.AdsUtils.AppOpenManager;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.NetworkConnectivity;

import com.motivation.quotes.app.utils.PreferenceManager;
import com.motivation.quotes.app.utils.Util;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;


public class MyApplication extends Application {

    public static Context context;
    public AppOpenManager appOpenAdManager;
    public static MyApplication myApplication;
    private static MyApplication mInstance;
    PreferenceManager preferenceManager;
    NetworkConnectivity networkConnectivity;
    int notificationId = 1;

    public void showAppOpenAds() {
        try {
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {
                if (preferenceManager.getBoolean(Constant.OPEN_APP_AD_ENABLED) && preferenceManager.getBoolean(Constant.ADS_ENABLE)) {
                    appOpenAdManager = new AppOpenManager(myApplication, preferenceManager.getString(Constant.OPEN_AD_ID));
                }
            }
        } catch (Exception e) {
            Util.showErrorLog(e.getMessage(), e);
        }
    }

    public static synchronized MyApplication getInstance() {
        MyApplication myApplication;
        synchronized (MyApplication.class) {
            synchronized (MyApplication.class) {
                myApplication = mInstance;
            }
        }
        return myApplication;
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = myApplication = this;
        mInstance = MyApplication.this;
        MyApplication.context = getApplicationContext();

        networkConnectivity = new NetworkConnectivity(this);
        preferenceManager = new PreferenceManager(this);

        FirebaseApp.initializeApp(this);

        context = this;
        if (networkConnectivity.isConnected()) {
            AppConfig.IS_CONNECTED = true;
        } else {
            AppConfig.IS_CONNECTED = false;
        }

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());


        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId("209470e9-572d-4143-a99a-5e47bfb6f7f2");

        showAppOpenAds();

        // Admob Ads Initialization
        MobileAds.initialize(this, initializationStatus -> {

        });

    }

}
