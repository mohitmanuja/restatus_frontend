package com.motivation.quotes.app.ui.activities;

import static com.motivation.quotes.app.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ActivityAllGreetingsBinding;
import com.motivation.quotes.app.listener.ClickListener;
import com.motivation.quotes.app.model.PostItem;
import com.motivation.quotes.app.ui.adapters.FestivalPostAdapter;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.NetworkConnectivity;
import com.motivation.quotes.app.utils.PermissionUtils;

import java.util.Map;

public class ActivityAllGreetings extends AppCompatActivity {
    private ActivityAllGreetingsBinding binding;
    Activity context;
    private String catName = "";
    private String catId = "";
    private FestivalPostAdapter festivalAdapter;
    NetworkConnectivity networkConnectivity;
    PermissionUtils takePermissionUtils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllGreetingsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        topIconBar(this);

        context = this;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_AllGreetings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        networkConnectivity = new NetworkConnectivity(context);
        takePermissionUtils = new PermissionUtils(context, mPermissionResult);

        binding.shimmerGreetings.setVisibility(View.VISIBLE);

        binding.backImg.setOnClickListener(view -> {
            onBackPressed();
        });


        if (getIntent() != null){

            catId = getIntent().getStringExtra("cat_id");
            catName = getIntent().getStringExtra("name");
        }

            binding.title.setText(""+catName);
            getBackground();
            binding.mainBackground.setVisibility(View.GONE);
            binding.shimmerGreetings.setVisibility(View.VISIBLE);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
            binding.shimmerGreetings.setVisibility(View.VISIBLE);
            binding.noDataLayout.setVisibility(View.GONE);
            binding.noInternetLayout.setVisibility(View.GONE);
            binding.main.setVisibility(View.GONE);


            new Handler().postDelayed(this::getBackground, 1000);
        });


    }


    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                }
            });

    private void getBackground() {

        if (!networkConnectivity.isConnected()) {

            binding.noInternetLayout.setVisibility(View.VISIBLE);
            binding.noDataLayout.setVisibility(View.GONE);
            binding.shimmerGreetings.setVisibility(View.GONE);
            binding.main.setVisibility(View.GONE);

            return;
        }

        Constant.getHomeViewModel(this).getGreetingPost(catId).observe(this, postItems -> {

            if (postItems != null && !postItems.isEmpty() && postItems.size() > 0) {

                festivalAdapter = new FestivalPostAdapter(context, postItems, new ClickListener<PostItem>() {
                    @Override
                    public void onClick(PostItem data) {

                        Intent intent = new Intent(context, EditorActivity.class);
                        intent.putExtra("imageUri", data.image_url);
                        intent.putExtra("greeting", "greeting");
                        context.startActivity(intent);

                    }
                }, true);

                festivalAdapter.setFestivalPost(postItems);
                binding.backgroundRv.setAdapter(festivalAdapter);
                binding.main.setVisibility(View.VISIBLE);
                binding.shimmerGreetings.setVisibility(View.GONE);
                binding.mainBackground.setVisibility(View.VISIBLE);
                binding.noDataLayout.setVisibility(View.GONE);
                binding.noInternetLayout.setVisibility(View.GONE);

            }

        });

    }



}