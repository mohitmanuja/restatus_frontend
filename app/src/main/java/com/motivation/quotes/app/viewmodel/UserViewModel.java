package com.motivation.quotes.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.motivation.quotes.app.api.ApiStatus;
import com.motivation.quotes.app.model.AppStatus;
import com.motivation.quotes.app.model.GreetingData;
import com.motivation.quotes.app.respository.UserRespository;

import java.util.List;

public class UserViewModel extends ViewModel {


    UserRespository respository;

    public UserViewModel() {
        this.respository = new UserRespository();
    }

    public LiveData<ApiStatus> contactUsMessage(String uid, String name, String email, String number, String message) {
        return respository.contactUsMessage(uid, name, email, number, message);

    }

    public LiveData<AppStatus> storeTransaction(String device_id, String plan_id, String amount, String transaction_id) {

        return respository.storeTransaction(device_id, plan_id, amount, transaction_id);
    }


    public LiveData<List<GreetingData>> getGreetingPostByCategory() {

        return respository.getGreetingPostByCategory();
    }

}

