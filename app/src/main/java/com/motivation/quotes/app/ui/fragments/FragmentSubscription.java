package com.motivation.quotes.app.ui.fragments;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ActivitySubscriptionBinding;
import com.motivation.quotes.app.listener.AdapterClickListener;
import com.motivation.quotes.app.model.SubscriptionModel;
import com.motivation.quotes.app.ui.adapters.SubscriptionAdapter;
import com.motivation.quotes.app.ui.dialog.UniversalDialog;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.PreferenceManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentSubscription extends Fragment implements PaymentResultListener {

    private ActivitySubscriptionBinding binding;
    private PreferenceManager preferenceManager;
    private UniversalDialog universalDialog;
    private SubscriptionAdapter adapter;
    private List<SubscriptionModel> plan_list = new ArrayList<>();
    private String selected_id = "", selected_price = "", selected_name = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding.shimmerLay.setVisibility(View.VISIBLE);
//        binding.recycler.setVisibility(View.INVISIBLE);

        preferenceManager = new PreferenceManager(getActivity());
        universalDialog = new UniversalDialog(getActivity(), false);

        // Observe the subscription plans from the ViewModel
        Constant.getHomeViewModel(getActivity()).getSubscriptionPlan().observe(getActivity(), subscriptionModels -> {
            if (subscriptionModels != null && subscriptionModels.size()> 0){

                plan_list = subscriptionModels;
                adapter = new SubscriptionAdapter(getActivity(), plan_list, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {
                        SubscriptionModel model = (SubscriptionModel) object;
                        selected_price = model.discount_price;
                        selected_name = model.name;
                        selected_id = model.id;
                    }
                });

                selected_price = plan_list.get(0).discount_price;
                binding.shimmerLay.setVisibility(View.GONE);
                binding.recycler.setVisibility(View.VISIBLE);
                binding.recycler.setAdapter(adapter);
                setupBuyBtn();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivitySubscriptionBinding.inflate(inflater, container, false);
        binding.backBtn.setOnClickListener(view -> requireActivity().onBackPressed());
        return binding.getRoot();

    }

    private void setupBuyBtn() {
        binding.llSubscribeNow.setOnClickListener(view -> {
            manageRazorPay(Integer.parseInt(selected_price));
        });
    }

    private void manageRazorPay(int price) {
        try {
            String mobile = preferenceManager.getString(Constant.USER_PHONE);
            String email = preferenceManager.getString(Constant.USER_EMAIL);

            JSONObject object = new JSONObject();
            object.put("name", getString(R.string.app_name));
            object.put("description", price * 100);
            object.put("theme.color", "#FF8E0B");
            object.put("currency", "INR");
            object.put("amount", price * 100);
            if (mobile != null && !mobile.trim().isEmpty()) object.put("prefill.contact", mobile);
            if (email != null && !email.trim().isEmpty()) object.put("prefill.email", email);

            Checkout checkout = new Checkout();
            checkout.setImage(R.drawable.logo);
            checkout.setKeyID(preferenceManager.getString(Constant.RAZORPAY_KEY_ID));
            checkout.open(requireActivity(), object);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        if (razorpayPaymentID != null && !razorpayPaymentID.isEmpty()) {
            capturePayment(razorpayPaymentID, Integer.parseInt(selected_price));
        } else {
            requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        if (response != null && !response.trim().isEmpty()) {
            requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), response.trim(), Toast.LENGTH_LONG).show());
        } else {
            requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Access Denied", Toast.LENGTH_LONG).show());
        }
    }

    private void capturePayment(String paymentID, int price) {
        OkHttpClient client = new OkHttpClient();

        if (paymentID == null || paymentID.isEmpty()) {
            Log.e("Capture Payment", "Payment ID is null or empty");
            return;
        }
        if (price == 0) {
            Log.e("Capture Payment", "Price is null or empty");
            return;
        }

        String keyId = preferenceManager.getString(Constant.RAZORPAY_KEY_ID);
        String secretKey = preferenceManager.getString(Constant.RAZORPAY_SECRET_KEY);

        if (keyId == null || secretKey == null || keyId.isEmpty() || secretKey.isEmpty()) {
            Log.e("Capture Payment", "Razorpay Key ID or Secret Key is null or empty");
            return;
        }

        String credentials = keyId + ":" + secretKey;
        String authHeader = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        RequestBody body = new FormBody.Builder()
                .add("amount", String.valueOf(price * 100))
                .build();

        Request request = new Request.Builder()
                .url("https://api.razorpay.com/v1/payments/" + paymentID + "/capture")
                .addHeader("Authorization", authHeader)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Capture Payment", "Request failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("Capture Payment", "Unexpected response: " + response);
                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.d("Capture Payment", "Payment Captured Successfully: " + response.body().string());
                }
            }
        });
    }


}
