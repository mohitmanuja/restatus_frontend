package com.growwthapps.dailypost.v2.ui.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import static com.growwthapps.dailypost.v2.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ActivityEditProfileBinding;
import com.growwthapps.dailypost.v2.ui.fragments.FragmentPersonal;
import com.growwthapps.dailypost.v2.ui.fragments.FragmentBusiness;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private int selectedPosition = 0;
    Activity context;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        setupViewPager(binding.viewpager);

        context = this;
        topIconBar(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        preferenceManager = new PreferenceManager(context);
        binding.editProfileTabLayout.setupWithViewPager(binding.viewpager);

        binding.settingImg.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });


        selectedPosition = preferenceManager.getString("DataType").contains("Business") ? 1 : 0;


        binding.backImg.setOnClickListener(view -> {onBackPressed();});

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new FragmentPersonal(), "Personal");
        adapter.addFragment(new FragmentBusiness(), "Business");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + binding.viewpager.getId() + ":" + selectedPosition);

        if (currentFragment instanceof FragmentPersonal) {
            ((FragmentPersonal) currentFragment).onActivityResult(requestCode, resultCode, data);
        } else if (currentFragment instanceof FragmentBusiness) {
            ((FragmentBusiness) currentFragment).onActivityResult(requestCode, resultCode, data);
        }
    }
}