package com.motivation.quotes.app.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.growwthapps.dailypost.v2.R;

import com.motivation.quotes.app.model.SubsPlanItem;
import com.motivation.quotes.app.viewmodel.HomeViewModel;
import com.motivation.quotes.app.viewmodel.UserViewModel;

public class Constant {

    public static final String TEMPLATE_TYPE_POSTER = "poster";
    public static final String TEMPLATE_TYPE_VIDEO = "video";
    public static final String IS_PREMIUM = "premium";
    public static final String NATIVE_AD_COUNT = "native_ad_click";
    public static final String REWARD_AD = "rewardAd";
    public static final String BUSINESS_SUB = "business_sub";
    public static final String PLAN_EXPIRED = "plan_expired";
    public static final String PLAN_DURATION = "plan_duration";
    public static final String TODAY_DATE_PATTERN = "yyyy-MM-dd";
    public static final String ONESIGNAL_APP_ID = "ONESIGNAL_APP_ID";
    public static final String TAG_SEARCH_TERM = "tag_search";
    public static final String BUSSINESS_IMAGE = "bussinessImage";
    public static final String multipart = "multipart/form-data";
    public static final String DARK_MODE_ON = "CheckedItem";
    public static final String NOTIFICATION_ENABLED = "NotificationEnabled";
    public static final String NOTIFICATION_FIRST = "NOTIFICATION_FIRST";
    public static final String IS_LOGIN = "IS_LOGIN";
    public static final String api_key = "demoKey";
    public static final String GOOGLE = "google";
    public static final String FESTIVAL = "festival";
    public static final String CATEGORY = "category";
    public static final String CUSTOM = "custom";
    public static final String BUSINESS = "business";
    public static final String SUBS_PLAN = "subscriptionPlan";
    public static final String EXTERNAL = "externalLink";
    public static final String EMAILAUTH = "password"; // don't change
    public static final String GOOGLEAUTH = "google.com"; // don't change
    public static final String PRIVACY_POLICY = "PRIVACY_POLICY";
    public static final String TERM_CONDITION = "TERM_CONDITION";
    public static final String REFUND_POLICY = "REFUND_POLICY";
    public static final String RAZORPAY_KEY_ID = "RAZORPAY_KEY_ID";

    public static final String STRIPE_KEY = "STRIPE_KEY";
    public static final String STRIPE_SECRET_KEY = "STRIPE_SECRET_KEY";

    public static final String PAYTM_ID = "PAYTM_ID";
    public static final String PAYTM_KEY = "PAYTM_KEY";

    public static final String CURRENCY = "currency";
    public static final String RazorPay = "razorpay"; // don't change'
    public static final String Paytm = "paytm"; // don't change'
    public static final String Stripe = "stripe"; // don't change'
    public static final String IS_SUBSCRIBE = "IS_SUBSCRIBE";

    /**
     * Ads Data
     */
    public static final String PRIVACY_POLICY_LINK = "PRIVACY_POLICY_LINK";
    public static final String ADS_ENABLE = "ADS_ENABLE";
    public static final String PUBLISHER_ID = "PUBLISHER_ID";
    public static final String BANNER_AD_ID = "BANNER_AD_ID";
    public static final String INTERSTITIAL_AD_ID = "INTERSTITIAL_AD_ID";
    public static final String INTERSTITIAL_AD_CLICK = "INTERSTITIAL_AD_CLICK";
    public static final String NATIVE_AD_ID = "NATIVE_AD_ID";
    public static final String OPEN_AD_ID = "OPEN_AD_ID";
    public static final String INTERSTITIAL_AD_ENABLED = "INTERSTITIAL_AD_ENABLED";
    public static final String BANNER_AD_ENABLED ="BANNER_AD_ENABLED" ;
    public static final String NATIVE_AD_ENABLED = "NATIVE_AD_ENABLED";
    public static final String OPEN_APP_AD_ENABLED =  "OPEN_APP_AD_ENABLED";
    public static final String REWARD_AD_ENABLED = "REWARD_AD_ENABLED";

    public static final String OFFER_IMAGE = "OFFERIMAGE";

    public static final int RESULT_OK = -1;

    /**
     * User Data
     */
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_ADDRESS = "USER_ADDRESS";

    public static final String USER_PHONE = "USER_PHONE";
    public static final String USER_DESIGNATION = "USER_DESIGNATION";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_PASSWORD = "password";
    public static final String USER_IMAGE = "USER_IMAGE";
    public static final String USER_LANGUAGE = "USER_LANGUAGE";
    public static final String LOGIN_TYPE = "LOGIN_TYPE";
    public static final String USER_BUSINESS_LIMIT = "USER_BUSINESS_LIMIT";
    public static final String STATUS = "STATUS";
    public static final String DEVICE = "android";

    // user Login
    public static final String SUBSCRIPTION_ID = "subscription_id";
    public static final String SUBSCRIPTION_START_DATE = "subscription_start_date";
    public static final String SUBSCRIPTION_END_DATE = "subscription_end_date";

    /**
     * Intent Data
     */
    public static final String INTENT_TYPE = "INTENT_TYPE";
    public static final String INTENT_FEST_ID = "INTENT_FEST_ID";
    public static final String INTENT_FEST_NAME = "INTENT_FEST_NAME";
    public static final String INTENT_POST_IMAGE = "INTENT_POST_IMAGE";
    public static final String INTENT_POST_LIST = "INTENT_POST_LIST";
    public static final String INTENT_VIDEO = "INTENT_VIDEO";
    public static final String INTENT_POS = "INTENT_POS";
    public static final String INTENT_IS_FROM_NOTIFICATION = "INTENT_IS_FROM_NOTIFICATION";
    public static final String IS_NOT = "IS_NOT";
    public static final String PRF_ID = "PRF_ID";
    public static final String PRF_NAME = "PRF_NAME";
    public static final String PRF_TYPE = "PRF_TYPE";
    public static final String PRF_LINK = "PRF_LINK";
    public static final String APP_HIDED_FOLDER = "Postermaker/";
    public static final String PHONE = "phone";
    public static final String GREETING = "greeting";
    public static final Integer DATA_LIMIT = 500;
    public static final String BUSINESS_ID = "business_id";

    public static final String BUSINESS_ITEM = "Business";
    public static final String BUSINESS_NAME = "Business_name";
    public static final String BUSINESS_LOGO = "Business_logo";
    public static final String BUSINESS_DETAIL = "business_detail";
    public static final String BUSINESS_ADDRESS = "business_addrress";
    public static final String BUSINESS_MOBILE = "mobile_no";
    public static final String BUSINESS_WEBSITE = "website";
    public static final String BUSINESS_CATEGORY_NAME = "category_name";
    public static final String BUSINESS_CATEGORY_ID = "category_id";
    public static final String BUSINESS_IS_DEFAULT= "is_default";
    public static final String BUSSINESS_FACEBOOK = "business_fb";
    public static final String BUSSINESS_TWITTER = "business_twitter";
    public static final String BUSSINESS_INSTAGRAM = "BUSSINESS_INSTAGRAM";
    public static final String BUSSINESS_WEB = "BUSSINESS_WEB";
    public static final String BUSSINESS_EMAIL = "BUSSINESS_EMAIL";
    public static final String BUSSINESS_YOUTUBE = "BUSSINESS_YOUTUBE";
    public static final String BUSINESS_TAGLINE = "BUSINESS_TAGLINE";
    public static final int BUSINESS_LOGO_WIDTH = 150;
    public static final int BUSINESS_LOGO_HEIGHT = 150;
    public static final String BUSINESS_UPDATED = "BUSINESS_UPDATED";
    public static final String FRAME_TYPE_IMAGE = "image";
    public static final String FRAME_TYPE_ANIMATED= "animated";
    public static final String SELECTED_FRAME_POSITION = "SELECTED_FRAME_POSITION";
    public static final String BUSINESS_LOGO_PATH = "BUSINESS_LOGO_PATH";
    public static final String USER_IMAGE_PATH = "USER_IMAGE_PATH";
    public static final Integer ANIMATED_CUSTOM_SHOW_LIMIT = 6;
    public static final String ENABLE = "ENABLE";
    public static final String WHATSAPP_NUMBER = "WHATSAPP_NUMBER";
    public static final String LOAD_DATA = "LOAD_DATA";
    public static final String LANGUAGE_NAME = "LANGUAGE_NAME";
    public static final String RAZORPAY_SECRET_KEY = "RAZORPAY_SECRET_KEY";
    public static final String USER_SOCIAL_MEDIA = "USER_SOCIAL_MEDIA";
    public static final String USER_SOCIAL_MEDIA_TYPE = "USER_SOCIAL_MEDIA_TYPE";
    public static final String BUSINESS_SOCIAL_MEDIA_TYPE = "BUSINESS_SOCIAL_MEDIA_TYPE";
    public static String sdcardPath = null;
    private static final String PLATFORM = "PLATFORM";

    public static Bitmap bitmap;
    public static Bitmap bitmaps = null;
    public static String selectedRatio = "1:1";
    public static Bitmap bitmapSticker = null;
    public static String isRated = "isRated";
    public static String onTimeLayerScroll = "onTimeLayerScroll";
    public static String onTimeRecentHint = "onTimeRecentHint";
    public static String rewid = "";
    public static String uri = "";
    public static SubsPlanItem subsPlanItem;


    public static final String tag = "DailyPost__";

    public static HomeViewModel getHomeViewModel(ViewModelStoreOwner viewModelStoreOwner) {

        return new ViewModelProvider(viewModelStoreOwner).get(HomeViewModel.class);
    }


    public static UserViewModel getUserViewModel(ViewModelStoreOwner viewModelStoreOwner){

        return new ViewModelProvider(viewModelStoreOwner).get(UserViewModel.class);

    }


    public static Animation getAnimUp(Activity activity) {
        return AnimationUtils.loadAnimation(activity, R.anim.slide_up);
    }





}
