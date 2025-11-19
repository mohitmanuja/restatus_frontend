package com.motivation.quotes.app.respository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.motivation.quotes.app.api.ApiClient;
import com.motivation.quotes.app.api.ApiService;
import com.motivation.quotes.app.api.ApiStatus;
import com.motivation.quotes.app.model.AppStatus;
import com.motivation.quotes.app.model.GreetingData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRespository {


    private ApiService apiService;

    public UserRespository() {
        apiService = ApiClient.getApiDataService();
    }

    public LiveData<ApiStatus> contactUsMessage(String uid, String name, String email, String number, String message) {
        MutableLiveData<ApiStatus> data = new MutableLiveData<>();
        apiService.contactUsMessage(uid, name, email, number, message).enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<GreetingData>> getGreetingPostByCategory() {
        MutableLiveData<List<GreetingData>> data = new MutableLiveData<>();
        apiService.getGreetingPostByCategory().enqueue(new Callback<List<GreetingData>>() {
            @Override
            public void onResponse(Call<List<GreetingData>> call, Response<List<GreetingData>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<GreetingData>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }


    public LiveData<AppStatus> storeTransaction(String device_id, String plan_id, String amount, String transaction_id) {
        MutableLiveData<AppStatus> data = new MutableLiveData<>();

        apiService.storeTransaction(device_id, plan_id, amount, transaction_id).enqueue(new Callback<AppStatus>() {
            @Override
            public void onResponse(Call<AppStatus> call, Response<AppStatus> response) {
                data.setValue(response.body());
                Log.d("createTransactions", "createTransactions: "+response.body());

            }

            @Override
            public void onFailure(Call<AppStatus> call, Throwable t) {
                t.printStackTrace();
                data.setValue(null);
                Log.d("createTransactions", "onFailure: "+t);

            }
        });
        return data;
    }

}
