package com.motivation.quotes.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


import com.motivation.quotes.app.model.AppInfos;
import com.motivation.quotes.app.model.CategoryItem;

import com.motivation.quotes.app.model.LanguageItem;
import com.motivation.quotes.app.model.LoginModel;
import com.motivation.quotes.app.model.PostItem;

import com.motivation.quotes.app.model.StoryItem;

import com.motivation.quotes.app.model.SubscriptionModel;
import com.motivation.quotes.app.model.UpdateBusiness;
import com.motivation.quotes.app.model.UpdateUserProfile;
import com.motivation.quotes.app.model.UserDestroy;
import com.motivation.quotes.app.model.UserDetail;
import com.motivation.quotes.app.respository.HomeRespository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private HomeRespository respository;

    public HomeViewModel() {
        respository = new HomeRespository();
    }

    public LiveData<List<PostItem>> getDailyPosts(int page, String catid, String language) {
        return respository.getDailyPosts(page, catid, language);
    }
    public LiveData<List<PostItem>> getFestivalPost(int page, String catid, String language) {
        return respository.getFestivalPost(page, catid, language);
    }

    public LiveData<List<PostItem>> getBackgroundByCategory(String catid) {
        return respository.getBackgroundByCategory(catid);
    }

    public LiveData<List<PostItem>> getGreetingPost(String catid) {
        return respository.getGreetingPost(catid);
    }

    public LiveData<List<CategoryItem>> getCategories(String type) {

        return respository.getCategories(type);
    }

    public LiveData<List<SubscriptionModel>> getSubscriptionPlan() {
        return respository.getSubscriptionPlan();
    }

    public LiveData<List<CategoryItem>> getBackgroundCategory() {

        return respository.getBackgroundCategory();
    }

    public LiveData<List<LanguageItem>> getLanguagess() {

        return respository.getLanguagess();
    }

    public LiveData<AppInfos> getAppInfo() {

        return respository.getAppInfo();
    }



    public LiveData<List<StoryItem>> getFestival() {
        return respository.getFestival();
    }

    public LiveData<LoginModel> userLogin(String login_type, String email, String mobile) {
        return respository.userLogin(login_type,email, mobile);
    }

    public LiveData<UpdateUserProfile> updateUserProfile(String user_id, String name, String email, String mobile, String logo, String designation, String socialmedia_type, String socialmedia_value) {
        return respository.updateUserProfile(user_id, name, email, mobile, logo, designation, socialmedia_type, socialmedia_value);
    }

    public LiveData<UpdateBusiness> updateBusiness(String user_id, String name, String email, String mobile, String logo, String about, String address, String socialmedia_type, String socialmedia_value) {
        return respository.updateBusiness(user_id, name, email, mobile, logo, about, address, socialmedia_type, socialmedia_value);
    }

    public LiveData<UserDetail> userDetail(String user_id) {
        return respository.userDetail(user_id);
    }

    public LiveData<UserDestroy> userDestroy(String user_id) {
        return respository.userDestroy(user_id);
    }



}
