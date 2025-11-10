package com.growwthapps.dailypost.v2.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.growwthapps.dailypost.v2.AppConfig;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;
import com.growwthapps.dailypost.v2.model.UserDetail;
import com.growwthapps.dailypost.v2.ui.dialog.UniversalDialog;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.NetworkConnectivity;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.utils.Util;
import com.growwthapps.dailypost.v2.viewmodel.HomeViewModel;

@SuppressLint("CustomSplashScreen")
public class CustomSplashActivity extends AppCompatActivity {

    PreferenceManager preferenceManager;
    UniversalDialog universalDialog;
    String status = "";
    NetworkConnectivity networkConnectivity;

    HomeViewModel homeViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashy);

        MyUtils.splash_status_bar(this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        preferenceManager = new PreferenceManager(this);
        universalDialog = new UniversalDialog(this, false);
        status = preferenceManager.getString(Constant.STATUS);

        networkConnectivity = new NetworkConnectivity(this);

        loadData();

    }


    public void loadData() {

        if (networkConnectivity.isConnected()) {
            FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(60)
                    .build();
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            firebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(task -> {

                        Util.showLog("API_KEY: " + firebaseRemoteConfig.getString("apiKey"));
                        preferenceManager.setString(Constant.api_key, firebaseRemoteConfig.getString("apiKey"));

                        AppConfig.API_KEY = preferenceManager.getString(Constant.api_key);
                        loadAppData();

                    })
                    .addOnFailureListener(e -> {
                        Util.showErrorLog("Firebase", e);
                        gotoMainActivity();
                    });

        } else {
            Util.showLog("Internet is not connected");
            gotoMainActivity();
        }
    }

    public void loadAppData() {


        homeViewModel.getAppInfo().observe(this, appInfos -> {


            preferenceManager.setString(Constant.USER_LANGUAGE, String.valueOf(-1));
            preferenceManager.setString(Constant.LANGUAGE_NAME, "All");

            if (appInfos != null) {

                MyUtils.showResponse(appInfos);


                if (appInfos.getAppInfo() != null) {


                    preferenceManager.setString(Constant.PRIVACY_POLICY, appInfos.getAppInfo().getPrivacyPolicy());
                    preferenceManager.setString(Constant.TERM_CONDITION, appInfos.getAppInfo().getTermsCondition());
                    preferenceManager.setString(Constant.REFUND_POLICY, appInfos.getAppInfo().getRefundPolicy());
                    preferenceManager.setString(Constant.PRIVACY_POLICY_LINK, appInfos.getAppInfo().getPrivacyPolicy());
                    preferenceManager.setString(Constant.ONESIGNAL_APP_ID, appInfos.getAppInfo().getOnesignalAppId());
                    preferenceManager.setString(Constant.RAZORPAY_KEY_ID, appInfos.getAppInfo().getRazorpayKeyId());
                    preferenceManager.setString(Constant.RAZORPAY_SECRET_KEY, appInfos.getAppInfo().getRazorpayKeySecret());
                    preferenceManager.setString(Constant.WHATSAPP_NUMBER, appInfos.getAppInfo().whatsappNumber);
                    preferenceManager.setString(Constant.CURRENCY, appInfos.getAppInfo().currency);

                }

                if (appInfos.getAdsModel() != null) {

                    preferenceManager.setString(Constant.PUBLISHER_ID, appInfos.getAdsModel().getAdmobAppId());
                    preferenceManager.setString(Constant.BANNER_AD_ID, appInfos.getAdsModel().getAdmobBanner());
                    preferenceManager.setString(Constant.INTERSTITIAL_AD_ID, appInfos.getAdsModel().getAdmobInter());
                    preferenceManager.setBoolean(Constant.ADS_ENABLE, appInfos.getAdsModel().getAdEnabled() == 1);
                    preferenceManager.setBoolean(Constant.INTERSTITIAL_AD_ENABLED, appInfos.getAdsModel().getInterstitialAdsEnable() == 1);
                    preferenceManager.setBoolean(Constant.BANNER_AD_ENABLED, appInfos.getAdsModel().getBannerAdsEnable() == 1);
                    preferenceManager.setBoolean(Constant.NATIVE_AD_ENABLED, appInfos.getAdsModel().getNativeAdsEnable() == 1);
                    preferenceManager.setBoolean(Constant.OPEN_APP_AD_ENABLED, appInfos.getAdsModel().getAppOpensAdsEnable() == 1);
                    preferenceManager.setBoolean(Constant.REWARD_AD_ENABLED, appInfos.getAdsModel().getRewardedAdsEnable() == 1);
                    preferenceManager.setInt(Constant.INTERSTITIAL_AD_CLICK, appInfos.getAdsModel().getAdmobInterCount());
                    preferenceManager.setString(Constant.NATIVE_AD_ID, appInfos.getAdsModel().getAdmobNative());
                    //   preferenceManager.setInt(Constant.NATIVE_AD_COUNT, appInfos.getAdsModel().getAdmobNativeCount());
                    preferenceManager.setString(Constant.REWARD_AD, appInfos.getAdsModel().getAdmobReward());
                    preferenceManager.setString(Constant.OPEN_AD_ID, appInfos.getAdsModel().getAdmobAppOpen());

                }

            }

//            storeDevice();
            if (preferenceManager.getBoolean(Constant.IS_LOGIN)) {
                userDetail();
            }
            else {
             Intent   intent = new Intent(CustomSplashActivity.this, LoginActivity.class);

                startActivity(intent);
                finish();            }

        });


    }



    private void userDetail() {


        homeViewModel.userDetail(preferenceManager.getString(Constant.USER_ID)).observe(this, new Observer<UserDetail>() {
            @Override
            public void onChanged(UserDetail userDetail) {

                preferenceManager.setBoolean(Constant.IS_LOGIN, true);

                preferenceManager.setString(Constant.SUBSCRIPTION_ID, userDetail.subscription_id);
                preferenceManager.setString(Constant.SUBSCRIPTION_START_DATE, userDetail.subscription_start_date);
                preferenceManager.setString(Constant.PLAN_EXPIRED, userDetail.subscription_end_date);
                preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, userDetail.is_subscribed);

                preferenceManager.setString(Constant.USER_EMAIL, userDetail.email);
                preferenceManager.setString(Constant.USER_NAME, userDetail.name);
                preferenceManager.setString(Constant.USER_PHONE, userDetail.mobile);
                preferenceManager.setString(Constant.USER_IMAGE, userDetail.logo);
                preferenceManager.setString(Constant.USER_DESIGNATION, userDetail.designation);
                preferenceManager.setString(Constant.USER_ID, userDetail.user_id);
                preferenceManager.setString(Constant.LOGIN_TYPE, userDetail.login_type);

                preferenceManager.setString(Constant.BUSINESS_NAME, userDetail.business_name);
                preferenceManager.setString(Constant.BUSINESS_ADDRESS, userDetail.business_address);
                preferenceManager.setString(Constant.BUSINESS_DETAIL, userDetail.business_about);
                preferenceManager.setString(Constant.BUSINESS_MOBILE, userDetail.business_mobile);
                preferenceManager.setString(Constant.BUSSINESS_INSTAGRAM, userDetail.business_socialmedia_value);
                preferenceManager.setString(Constant.BUSINESS_LOGO, userDetail.business_logo);
                preferenceManager.setString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE, userDetail.business_socialmedia_type);

                gotoMainActivity();


            }
        });
    }


    private void gotoMainActivity() {
        Intent intent;

        if (getIntent().getBooleanExtra(Constant.INTENT_IS_FROM_NOTIFICATION, false)) {


            intent = new Intent(CustomSplashActivity.this, LoginActivity.class);
            intent.putExtra(Constant.INTENT_TYPE, preferenceManager.getString(Constant.PRF_TYPE));
            intent.putExtra(Constant.INTENT_FEST_ID, preferenceManager.getString(Constant.PRF_ID));
            intent.putExtra(Constant.INTENT_FEST_NAME, preferenceManager.getString(Constant.PRF_NAME));
            intent.putExtra(Constant.INTENT_POST_IMAGE, "");
            intent.putExtra(Constant.INTENT_VIDEO, false);

            startActivity(intent);
            finish();

        } else {

            if (preferenceManager.getBoolean(Constant.IS_LOGIN)) {

                intent = new Intent(CustomSplashActivity.this, HomeActivity.class);

            } else {
                intent = new Intent(CustomSplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();


        }


    }

}