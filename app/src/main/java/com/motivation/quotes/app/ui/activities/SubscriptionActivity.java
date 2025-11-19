package com.motivation.quotes.app.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.activity.EdgeToEdge;

import com.google.gson.Gson;
import com.growwthapps.dailypost.v2.R;
import com.motivation.quotes.app.model.AppStatus;
import com.motivation.quotes.app.ui.adapters.SubscriptionAdapter;
import com.growwthapps.dailypost.v2.databinding.ActivitySubscriptionBinding;
import com.motivation.quotes.app.listener.AdapterClickListener;
import com.motivation.quotes.app.model.SubscriptionModel;
import com.motivation.quotes.app.ui.dialog.UniversalDialog;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.MyUtils;
import com.motivation.quotes.app.utils.PreferenceManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubscriptionActivity extends AppCompatActivity implements PaymentResultListener {

    ActivitySubscriptionBinding binding;
    private PreferenceManager preferenceManager;
    private UniversalDialog universalDialog;
    SubscriptionAdapter adapter;
    List<SubscriptionModel> plan_list = new ArrayList<>();
    String selected_id = "", selected_price = "", selected_name = "";
    Activity context;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    String keyId;
    String keySecret;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        context = this;

        MyUtils.status_bar_deny(context);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainSubscription), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        universalDialog = new UniversalDialog(this, false);

        preferenceManager = new PreferenceManager(this);



        url = "https://api.razorpay.com/v1/orders";
        keyId = preferenceManager.getString(Constant.RAZORPAY_KEY_ID);
        keySecret = preferenceManager.getString(Constant.RAZORPAY_SECRET_KEY);



        binding.backBtn.setOnClickListener(view -> onBackPressed());

        Constant.getHomeViewModel(this).getSubscriptionPlan().observe(this, new Observer<List<SubscriptionModel>>() {
            @Override
            public void onChanged(List<SubscriptionModel> subscriptionModels) {

                if (subscriptionModels != null && subscriptionModels.size() > 0) {
                    plan_list = subscriptionModels;

                    adapter = new SubscriptionAdapter(context, plan_list, new AdapterClickListener() {
                        @Override
                        public void onItemClick(View view, int pos, Object object) {

                            SubscriptionModel model = (SubscriptionModel) object;
                            selected_price = model.discount_price;
                            selected_name = model.name;
                            selected_id = model.id;

                            //   Log.d("SubscriptionAdapter", "onItemClick: "+selected_price+ " "+selected_name+ " "+selected_id);

                        }
                    });

                    selected_id = plan_list.get(0).id;
                    selected_price = plan_list.get(0).discount_price;
                    selected_name = plan_list.get(0).name;
                    //  adapter.notifyDataSetChanged();
                    binding.shimmerLay.setVisibility(View.GONE);
                    binding.recycler.setVisibility(View.VISIBLE);
                    binding.recycler.setAdapter(adapter);
                    setupBuyBtn();
                }
            }
        });


    }

    private void setupBuyBtn() {
        binding.llSubscribeNow.setOnClickListener(view -> {
            createOrderAndStartPayment(selected_price);
        });
    }

    private void createOrderAndStartPayment(String price) {

        OkHttpClient client = new OkHttpClient();

        JSONObject params = new JSONObject();
        try {
            params.put("amount", Math.round(Float.parseFloat(price) * 100)); // Amount in paise
            params.put("currency", "INR");
            params.put("receipt", "order_rcptid_11"+System.currentTimeMillis());
            params.put("payment_capture", 1); // Auto-capture payment
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating order", Toast.LENGTH_LONG).show();
            return;
        }

        RequestBody body = RequestBody.create(params.toString(), JSON);
        String auth = "Basic " + Base64.encodeToString((keyId + ":" + keySecret).getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", auth)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to create order: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String orderId = jsonResponse.getString("id");
                        runOnUiThread(() -> startPayment(orderId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(context, "Error processing response", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(context, "Failed to create order: " + response.message(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private void startPayment(String orderId) {
        final Activity activity = this;

        Checkout checkout = new Checkout();
        checkout.setKeyID(keyId);
        Checkout.clearUserData(getApplicationContext());


        try {
            JSONObject options = new JSONObject();
            options.put("name", context.getString(R.string.app_name));
            options.put("description", "Payment for Frame  " + context.getString(R.string.app_name));
            options.put("currency", "INR");
            options.put("amount", Math.round(Float.parseFloat(selected_price) * 100)); // Amount in paise
            options.put("order_id", orderId); // Use the Razorpay order ID
            // ✅ Force show method selection first
            options.put("show_default_items", false);


            checkout.open(activity, options);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error starting payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("startPayment", "Error starting payment: ");
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {

        try {
            if (razorpayPaymentId != null && !razorpayPaymentId.isEmpty()) {
                createTransactions(razorpayPaymentId);
            } else {
                Toast.makeText(this, "Payment ID is empty", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("onPaymentSuccess111", "onPaymentSuccess: "+e.getMessage());
        }

    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("onPaymentError",getIntent().getStringExtra("price"));
        setResult(RESULT_CANCELED);
        finish();
    }

    private void createTransactions(String paymentId) {


        Constant.getUserViewModel(this).storeTransaction(preferenceManager.getString(Constant.USER_ID), selected_id, selected_price, paymentId).observe(this, new Observer<AppStatus>() {
            @Override
            public void onChanged(AppStatus appStatus) {

                Log.d("storeTransaction", "onChanged: "+new Gson().toJson(appStatus.subscriptionModel));
                if (appStatus.subscriptionModel != null) {
                    preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, true);
                    //   preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, userItem.is_subscribed);
                    preferenceManager.setString(Constant.PLAN_EXPIRED, appStatus.subscriptionModel.plan_end_date);
                    preferenceManager.setString(Constant.PLAN_DURATION, appStatus.subscriptionModel.plan_name);

                    universalDialog.showSuccessDialog("message", SubscriptionActivity.this.getString(R.string.ok));
                    universalDialog.show();
                    Toast.makeText(SubscriptionActivity.this, "Payment DONE Successfully!  " + appStatus.subscriptionModel.is_subscribed, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> {


                        Intent intent = new Intent(SubscriptionActivity.this, HomeActivity.class);
                        SubscriptionActivity.this.startActivity(intent);
                        SubscriptionActivity.this.finish();

                    }, 2500);

                }

            }
        });
    }
}