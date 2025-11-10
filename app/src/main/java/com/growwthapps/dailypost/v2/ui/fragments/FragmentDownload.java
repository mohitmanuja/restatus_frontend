package com.growwthapps.dailypost.v2.ui.fragments;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.growwthapps.dailypost.v2.databinding.ActivityDownloadBinding;
import com.growwthapps.dailypost.v2.ui.adapters.SavedAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentDownload extends Fragment {
    private ActivityDownloadBinding binding;
    private SavedAdapter adapter;
    private Activity context;
    private int selectedPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityDownloadBinding.inflate(inflater, container, false);
        context = getActivity();

        binding.downloadTabLayout.setupWithViewPager(binding.viewpager);

        setupViewPager(binding.viewpager);
        return binding.getRoot();
    }


    private void setupViewPager(ViewPager viewPager) {
      ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new FragmentSubPosts(), "   Posts   ");
        adapter.addFragment(new FragmentSubGreetings(), "  Greetings  ");
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
