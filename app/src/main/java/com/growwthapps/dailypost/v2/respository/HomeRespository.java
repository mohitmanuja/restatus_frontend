package com.growwthapps.dailypost.v2.respository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.growwthapps.dailypost.v2.api.ApiClient;
import com.growwthapps.dailypost.v2.api.ApiService;

import com.growwthapps.dailypost.v2.model.AppInfos;
import com.growwthapps.dailypost.v2.model.CategoryItem;

import com.growwthapps.dailypost.v2.model.LanguageItem;
import com.growwthapps.dailypost.v2.model.LoginModel;
import com.growwthapps.dailypost.v2.model.PostItem;
import com.growwthapps.dailypost.v2.model.StoryItem;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;
import com.growwthapps.dailypost.v2.model.UpdateBusiness;
import com.growwthapps.dailypost.v2.model.UpdateUserProfile;
import com.growwthapps.dailypost.v2.model.UserDestroy;
import com.growwthapps.dailypost.v2.model.UserDetail;
import com.growwthapps.dailypost.v2.utils.Constant;


import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRespository {


    private ApiService apiService;

    public HomeRespository() {
        apiService = ApiClient.getApiDataService();
    }


    public LiveData<List<SubscriptionModel>> getSubscriptionPlan() {

        MutableLiveData<List<SubscriptionModel>> data = new MutableLiveData<>();
        apiService.getSubscriptionPlan().enqueue(new Callback<List<SubscriptionModel>>() {
            @Override
            public void onResponse(Call<List<SubscriptionModel>> call, Response<List<SubscriptionModel>> response) {
                data.setValue(response.body());
                Log.d("getFestival", "111 " + response.body());
            }

            @Override
            public void onFailure(Call<List<SubscriptionModel>> call, Throwable t) {
                data.setValue(null);

            }
        });
        return data;
    }

    public LiveData<List<CategoryItem>> getCategories(String type) {
        MutableLiveData<List<CategoryItem>> data = new MutableLiveData<>();
        apiService.getCategories(type).enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<CategoryItem>> getBackgroundCategory() {
        MutableLiveData<List<CategoryItem>> data = new MutableLiveData<>();
        apiService.getBackgroundCategory().enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }


    public LiveData<List<PostItem>> getDailyPosts(int page, String catid, String language) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getDailyPostData(page, catid, language).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<PostItem>> getFestivalPost(int page, String catid, String language) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getFestivalPost(page, catid, language).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<PostItem>> getBackgroundByCategory(String catid) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getBackgroundByCategory(catid).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<PostItem>> getGreetingPost(String catid) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getGreetingPost(catid).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }


    public LiveData<List<LanguageItem>> getLanguagess() {


        MutableLiveData<List<LanguageItem>> data = new MutableLiveData<>();

        apiService.getLanguagess().enqueue(new Callback<List<LanguageItem>>() {
            @Override
            public void onResponse(Call<List<LanguageItem>> call, Response<List<LanguageItem>> response) {

                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<LanguageItem>> call, Throwable t) {

                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<AppInfos> getAppInfo() {

        MutableLiveData<AppInfos> data = new MutableLiveData<>();

        apiService.getAppInfo().enqueue(new Callback<AppInfos>() {
            @Override
            public void onResponse(Call<AppInfos> call, Response<AppInfos> response) {

                data.setValue(response.body());

            }

            @Override
            public void onFailure(Call<AppInfos> call, Throwable t) {

                Log.d("errrorrr", "" + t.getMessage());

                t.printStackTrace();
                data.setValue(null);

            }
        });
        return data;
    }

    public LiveData<List<StoryItem>> getFestival() {

        MutableLiveData<List<StoryItem>> data = new MutableLiveData<>();
        apiService.getFestival().enqueue(new Callback<List<StoryItem>>() {
            @Override
            public void onResponse(Call<List<StoryItem>> call, Response<List<StoryItem>> response) {
                data.setValue(response.body());
                Log.d("getFestival", "" + response.body());
            }

            @Override
            public void onFailure(Call<List<StoryItem>> call, Throwable t) {
                data.setValue(null);

            }
        });
        return data;
    }



    public LiveData<LoginModel> userLogin(String login_type, String email, String mobile) {

        MutableLiveData<LoginModel> data = new MutableLiveData<>();

        apiService.userLogin(login_type, email, mobile).enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                data.setValue(response.body());

            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {

                Log.d("errrorrr", "" + t.getMessage());

                t.printStackTrace();
                data.setValue(null);

            }
        });
        return data;
    }


    public LiveData<UpdateUserProfile> updateUserProfile(String user_id, String userName, String userMail, String mobile, String logo, String userDesignation, String socialmediaType, String socialmediaValue) {

        MutableLiveData<UpdateUserProfile> data = new MutableLiveData<>();

        RequestBody requestFile = null;
        RequestBody imageuri = null;
        MultipartBody.Part body = null;


        if (logo != null) {

            File file = new File(logo);
            requestFile = RequestBody.create(MediaType.parse(Constant.multipart), file);
            imageuri = RequestBody.create(MediaType.parse(Constant.multipart), logo);
            body = MultipartBody.Part.createFormData("logo", file.getName(), requestFile);

        }

        RequestBody userid = RequestBody.create(MediaType.parse(Constant.multipart), user_id);
        RequestBody name = RequestBody.create(MediaType.parse(Constant.multipart), userName);
        RequestBody email = RequestBody.create(MediaType.parse(Constant.multipart), userMail);
        RequestBody phone = RequestBody.create(MediaType.parse(Constant.multipart), mobile);
        RequestBody designation = RequestBody.create(MediaType.parse(Constant.multipart), userDesignation);
        RequestBody socialmedia_type = RequestBody.create(MediaType.parse(Constant.multipart), socialmediaType);
        RequestBody socialmedia_value = RequestBody.create(MediaType.parse(Constant.multipart), socialmediaValue);


        ApiClient.getApiDataService().updateUserProfile(userid,name, email, phone, imageuri, body, designation, socialmedia_type, socialmedia_value).
                enqueue(new Callback<UpdateUserProfile>() {
                    @Override
                    public void onResponse(Call<UpdateUserProfile> callUpdateUserProfile, Response<UpdateUserProfile> response) {

                        Log.i("RESPONSE", "RESPONSE-->" + new Gson().toJson(response.body()));


                        data.setValue(response.body());


                    }

                    @Override
                    public void onFailure(Call<UpdateUserProfile> call, Throwable t) {
                        t.printStackTrace();

                        data.setValue(null);
                        Log.i("RESPONSE", "RESPONSE-->" + t);

                    }
                });


        return data;
    }



    public LiveData<UpdateBusiness> updateBusiness(String businessId, String businessName, String businessMail, String mobile, String logo, String about, String businessAddress, String socialmediaType, String socialmediaValue) {

        MutableLiveData<UpdateBusiness> data = new MutableLiveData<>();

        RequestBody requestFile = null;
        RequestBody imageuri = null;
        MultipartBody.Part body = null;


        if (logo != null) {

            File file = new File(logo);
            requestFile = RequestBody.create(MediaType.parse(Constant.multipart), file);
            imageuri = RequestBody.create(MediaType.parse(Constant.multipart), logo);
            body = MultipartBody.Part.createFormData("logo", file.getName(), requestFile);

        }

        RequestBody business_id = RequestBody.create(MediaType.parse(Constant.multipart), businessId);
        RequestBody name = RequestBody.create(MediaType.parse(Constant.multipart), businessName);
        RequestBody email = RequestBody.create(MediaType.parse(Constant.multipart), businessMail);
        RequestBody phone = RequestBody.create(MediaType.parse(Constant.multipart), mobile);
        RequestBody designation = RequestBody.create(MediaType.parse(Constant.multipart), about);
        RequestBody address = RequestBody.create(MediaType.parse(Constant.multipart), businessAddress);
        RequestBody socialmedia_type = RequestBody.create(MediaType.parse(Constant.multipart), socialmediaType);
        RequestBody socialmedia_value = RequestBody.create(MediaType.parse(Constant.multipart), socialmediaValue);


        ApiClient.getApiDataService().updateBusiness(business_id,name, email, phone, imageuri, body, designation, address, socialmedia_type, socialmedia_value).
                enqueue(new Callback<UpdateBusiness>() {
                    @Override
                    public void onResponse(Call<UpdateBusiness> callUpdateUserProfile, Response<UpdateBusiness> response) {

                        Log.i("RESPONSE", "RESPONSE-->" + new Gson().toJson(response.body()));


                        data.setValue(response.body());


                    }

                    @Override
                    public void onFailure(Call<UpdateBusiness> call, Throwable t) {
                        t.printStackTrace();

                        data.setValue(null);
                        Log.i("RESPONSE", "RESPONSE-->" + t);

                    }
                });


        return data;
    }


    public LiveData<UserDetail> userDetail(String user_id) {

        MutableLiveData<UserDetail> data = new MutableLiveData<>();

        apiService.userDetail(user_id).enqueue(new Callback<UserDetail>() {
            @Override
            public void onResponse(Call<UserDetail> call, Response<UserDetail> response) {

                data.setValue(response.body());

            }

            @Override
            public void onFailure(Call<UserDetail> call, Throwable t) {

                Log.d("errrorrr", "" + t.getMessage());

                t.printStackTrace();
                data.setValue(null);

            }
        });
        return data;
    }

    public LiveData<UserDestroy> userDestroy(String user_id) {

        MutableLiveData<UserDestroy> data = new MutableLiveData<>();

        apiService.userDestroy(user_id).enqueue(new Callback<UserDestroy>() {
            @Override
            public void onResponse(Call<UserDestroy> call, Response<UserDestroy> response) {

                data.setValue(response.body());

            }

            @Override
            public void onFailure(Call<UserDestroy> call, Throwable t) {

                Log.d("errrorrr", "" + t.getMessage());

                t.printStackTrace();
                data.setValue(null);

            }
        });
        return data;
    }
}
