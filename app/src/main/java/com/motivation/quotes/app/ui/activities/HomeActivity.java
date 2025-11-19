package com.motivation.quotes.app.ui.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.motivation.quotes.app.NonSwipeableViewPager;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ActivityHomeBinding;
import com.motivation.quotes.app.ui.fragments.FragmentCreatePost;
import com.motivation.quotes.app.ui.fragments.FragmentDownload;
import com.motivation.quotes.app.ui.fragments.FragmentFestivals;
import com.motivation.quotes.app.ui.fragments.FragmentHome;
import com.motivation.quotes.app.utils.MyUtils;
import com.motivation.quotes.app.utils.PreferenceManager;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    Activity context;

    private PreferenceManager preferenceManager;

    private int positionTab = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        MyUtils.status_bar_deny(this);

        context = this;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        preferenceManager = new PreferenceManager(context);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(4);

        setupTabClickListeners();

    }


    private void setupTabClickListeners() {
        binding.homeBtn.setOnClickListener(view -> setSelectedTab(0));
        binding.festivalBtn.setOnClickListener(view -> setSelectedTab(1));
        binding.personalFrameBtn.setOnClickListener(view -> setSelectedTab(2));
        binding.downloadBtn.setOnClickListener(view -> setSelectedTab(3));
        binding.frameMallBtn.setOnClickListener(view -> setSelectedTab(4));
        binding.premiumBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, SubscriptionActivity.class);
            startActivity(intent);
        });
    }


    private void setSelectedTab(int position) {
        binding.viewPager.setCurrentItem(position);

        positionTab = position;

        // Reset all icons and text colors to default
        binding.homeImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray3)));
        binding.festivalImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray3)));
        binding.downloadImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray3)));
        binding.premiumImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray3)));
        binding.frameMallImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray3)));

        binding.homeTv.setTextColor(getResources().getColor(R.color.gray3));
        binding.festivalTv.setTextColor(getResources().getColor(R.color.gray3));
        binding.personalFrameTv.setTextColor(getResources().getColor(R.color.gray3));
        binding.downloadTv.setTextColor(getResources().getColor(R.color.gray3));
        binding.premiumTv.setTextColor(getResources().getColor(R.color.gray3));
        binding.frameMallTv.setTextColor(getResources().getColor(R.color.gray3));

        if (position == 0) {
            binding.homeImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.app_color1)));
            binding.homeTv.setTextColor(getResources().getColor(R.color.app_color1));

            MyUtils.status_bar_deny(this);

        } else if (position == 1) {
            binding.festivalImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.app_color1)));
            binding.festivalTv.setTextColor(getResources().getColor(R.color.app_color1));
            MyUtils.status_bar_deny(this);

        } else if (position == 2) {
            binding.personalFrameTv.setTextColor(getResources().getColor(R.color.app_color1));
            MyUtils.status_bar_allow(this);

        } else if (position == 3) {
            binding.downloadImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.app_color1)));
            binding.downloadTv.setTextColor(getResources().getColor(R.color.app_color1));
            MyUtils.status_bar_allow(this);
        } else if (position == 4) {
            binding.frameMallImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.app_color1)));
            binding.frameMallTv.setTextColor(getResources().getColor(R.color.app_color1));
            MyUtils.status_bar_allow(this);
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new FragmentHome();
            } else if (position == 1) {
                return new FragmentFestivals();
            } else if (position == 2) {
                return new FragmentCreatePost();
            } else if (position == 3) {
                return new FragmentDownload();
            }  else {
                return new FragmentHome();
            }
        }


        @Override
        public int getCount() {
            return 5;
        }
    }

    @Override
    public void onBackPressed() {
        NonSwipeableViewPager viewPager = findViewById(R.id.viewPager);
        int currentItem = viewPager.getCurrentItem();
        if (currentItem != 0) {

            setSelectedTab(0);
        } else {

            showExitDialog();
        }
    }

    private void showExitDialog() {
        Dialog dialogBack = new Dialog(this, R.style.MyAlertDialog);
        dialogBack.setContentView(R.layout.dialog_back);

        Window window = dialogBack.getWindow();
        window.setLayout(MATCH_PARENT, MATCH_PARENT);
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        dialogBack.setCanceledOnTouchOutside(false);
        dialogBack.setCancelable(false);

        TextView cancelBtn = dialogBack.findViewById(R.id.cancelBtn);
        TextView exit = dialogBack.findViewById(R.id.discardBtn);

        exit.setOnClickListener(view -> {
            finishAffinity();
        });

        cancelBtn.setOnClickListener(view -> {
            dialogBack.dismiss();
        });

        dialogBack.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (preferenceManager.getBoolean("save_image")) {
            preferenceManager.setBoolean("save_image", false);
            recreate();
            setSelectedTab(positionTab);
        }

    }




}