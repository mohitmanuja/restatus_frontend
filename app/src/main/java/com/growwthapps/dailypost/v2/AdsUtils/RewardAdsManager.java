package com.growwthapps.dailypost.v2.AdsUtils;

import static com.growwthapps.dailypost.v2.utils.Constant.ADS_ENABLE;
import static com.growwthapps.dailypost.v2.utils.Constant.IS_SUBSCRIBE;
import static com.growwthapps.dailypost.v2.utils.Constant.REWARD_AD_ENABLED;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentInformation;

public class RewardAdsManager {

    public boolean isAdWatchedFull = false;
    PreferenceManager preferenceManager;
    private Activity mContext;
    private RewardedAd mRewardedAd = null;

    public RewardAdsManager(Activity context, OnAdLoaded onAdLoadedLister) {
        mContext = context;
        preferenceManager = new PreferenceManager(context);

        if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {

            if (preferenceManager.getBoolean(ADS_ENABLE) && preferenceManager.getBoolean(REWARD_AD_ENABLED)) {

                loadRewardAds(onAdLoadedLister);

            } else {

                onAdLoadedLister.onAdClosed();

            }

        }

    }

    private void loadRewardAds(OnAdLoaded onAdLoadedLister) {



        if (mRewardedAd == null) {

            AdRequest.Builder builder = new AdRequest.Builder();

            int request = GDPRChecker.getStatus();
            if (request == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                // load non Personalized ads
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
            } // else do nothing , it will load PERSONALIZED ads

            RewardedAd.load(mContext, preferenceManager.getString(Constant.REWARD_AD), builder.build(), new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                    mRewardedAd = null;
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                    mRewardedAd = rewardedAd;


                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {


                            mRewardedAd = null;

                            if (isAdWatchedFull) {

                                onAdLoadedLister.onAdWatched();

                            } else {

                                onAdLoadedLister.onAdClosed();

                            }

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {

                            mRewardedAd = null;

                            onAdLoadedLister.onAdClosed();

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {

                            mRewardedAd = null;
                            isAdWatchedFull = true;

                        }
                    });

                    mRewardedAd.show(mContext, rewardItem -> {

                        mRewardedAd = null;
                        isAdWatchedFull = true;

                    });


                }
            });

        }

    }

    public interface OnAdLoaded {

        void onAdClosed();

        void onAdWatched();


    }


}