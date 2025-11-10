package com.growwthapps.dailypost.v2.api;


import com.growwthapps.dailypost.v2.model.AppInfos;

import com.growwthapps.dailypost.v2.model.AppStatus;
import com.growwthapps.dailypost.v2.model.CategoryItem;

import com.growwthapps.dailypost.v2.model.GreetingData;
import com.growwthapps.dailypost.v2.model.LanguageItem;
import com.growwthapps.dailypost.v2.model.LoginModel;
import com.growwthapps.dailypost.v2.model.PostItem;

import com.growwthapps.dailypost.v2.model.StoryItem;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;
import com.growwthapps.dailypost.v2.model.UpdateBusiness;
import com.growwthapps.dailypost.v2.model.UpdateUserProfile;
import com.growwthapps.dailypost.v2.model.UserDestroy;
import com.growwthapps.dailypost.v2.model.UserDetail;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {


    @GET("contactUs")
    Call<ApiStatus> contactUsMessage(@Query("user_id") String userid,
                                     @Query("name") String name,
                                     @Query("email") String email,
                                     @Query("mobile_no") String number,
                                     @Query("message") String massage);


    @GET("getGreetingPostByCategory")
    Call<List<GreetingData>> getGreetingPostByCategory();

    @GET("getCategory")
    Call<List<CategoryItem>> getCategories(@Query("type") String type);

    @GET("getBackgroundCategory")
    Call<List<CategoryItem>> getBackgroundCategory();


    @GET("getAllLanguages")
    Call<List<LanguageItem>> getLanguagess();

    @GET("getSubscriptionPlan")
    Call<List<SubscriptionModel>> getSubscriptionPlan();


    //*** Get App Info****
    @GET("dailyPost")
    Call<List<PostItem>> getDailyPostData(@Query("page") Integer page, @Query("catId") String categoryID, @Query("language") String language);


    //*** Get App Info****
    @GET("getAppInfo")
    Call<AppInfos> getAppInfo();

    @GET("getFestival")
    Call<List<StoryItem>> getFestival();

    @GET("getFestivalPost")
    Call<List<PostItem>> getFestivalPost(@Query("page") Integer page, @Query("festival_id") String categoryID, @Query("language_id") String language);

    @GET("getBackgroundByCategory")
    Call<List<PostItem>> getBackgroundByCategory(@Query("category_id") String categoryID);

    @GET("getGreetingPost")
    Call<List<PostItem>> getGreetingPost(@Query("cat_id") String categoryID);


    @GET("storeTransaction")
    Call<AppStatus> storeTransaction(@Query("user_id") String device_id,
                                     @Query("plan_id") String plan_id,
                                     @Query("amount") String amount,
                                     @Query("transaction_id") String transaction_id);


    @GET("userLogin")
    Call<LoginModel> userLogin(@Query("login_type") String login_type,
                               @Query("email") String email,
                               @Query("mobile") String mobile);

    @Multipart
    @POST("updateUserProfile")
    Call<UpdateUserProfile> updateUserProfile(@Part("user_id") RequestBody user_id,
                                              @Part("name") RequestBody name,
                                              @Part("email") RequestBody email,
                                              @Part("mobile") RequestBody mobile,
                                              @Part("logo") RequestBody logo,
                                              @Part MultipartBody.Part file,
                                              @Part("designation") RequestBody designation,
                                              @Part("socialmedia_type") RequestBody socialmedia_type,
                                              @Part("socialmedia_value") RequestBody socialmedia_value);

    @Multipart
    @POST("updateBusiness")
    Call<UpdateBusiness> updateBusiness(@Part("user_id") RequestBody user_id,
                                        @Part("name") RequestBody name,
                                        @Part("email") RequestBody email,
                                        @Part("mobile") RequestBody mobile,
                                        @Part("logo") RequestBody logo,
                                        @Part MultipartBody.Part file,
                                        @Part("about") RequestBody about,
                                        @Part("address") RequestBody address,
                                        @Part("socialmedia_type") RequestBody socialmedia_type,
                                        @Part("socialmedia_value") RequestBody socialmedia_value);


    @GET("userDetail")
    Call<UserDetail> userDetail(@Query("user_id") String user_id);

    @GET("destroyUser")
    Call<UserDestroy> userDestroy(@Query("user_id") String user_id);



}
