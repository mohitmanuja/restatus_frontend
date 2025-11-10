package com.growwthapps.dailypost.v2.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.growwthapps.dailypost.v2.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.FragmentFrameMallBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;
import com.growwthapps.dailypost.v2.model.CategoryItem;
import com.growwthapps.dailypost.v2.model.PostItem;
import com.growwthapps.dailypost.v2.ui.activities.FillFrameDetails;
import com.growwthapps.dailypost.v2.ui.adapters.CategorysFrameAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.StoryAdapter;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.NetworkConnectivity;
import com.growwthapps.dailypost.v2.utils.PermissionUtils;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentFrameMall extends Fragment {
    private FragmentFrameMallBinding binding;
    StoryAdapter festivalAdapter;
    Activity context;
    private InterstitialsAdsManager interstitialsAdsManager;
    private String selectedLanguage = "";
    PreferenceManager preferenceManager;
    private String selectedCat = "-1";
    int pageCount = 1;
    List<PostItem> dailyPost = new ArrayList<>();
    View currentView;
    RelativeLayout rewateBtn;
    boolean loading = false;
    private Dialog dialogWatermarkOption;
    private Dialog dialogPremium;
    GridLayoutManager layoutManager;
    List<CategoryItem> categoryItemList = new ArrayList<>();
    NetworkConnectivity networkConnectivity;
    PermissionUtils takePermissionUtils;

    ImageView remove, premium;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFrameMallBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        networkConnectivity = new NetworkConnectivity(context);
        takePermissionUtils = new PermissionUtils(context, mPermissionResult);

        preferenceManager = new PreferenceManager(context);
        interstitialsAdsManager = new InterstitialsAdsManager(context);
        layoutManager = new GridLayoutManager(context, 1);

//        binding.shimmerViewContainer.setVisibility(View.VISIBLE);
        loadCategories();

        binding.tryAgain.setOnClickListener(v -> {
            requireActivity().recreate();
        });

        binding.btnMyFrame.setOnClickListener(view1 -> {
            startActivity(new Intent(context, FillFrameDetails.class));
        });

    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                }
            });


    private void loadCategories() {


        Constant.getHomeViewModel(this).getCategories("featured").observe(getActivity(), categoryItems -> {

            if (categoryItems != null && categoryItems.size() > 0) {
                categoryItemList.add(new CategoryItem("-1", "All", R.drawable.logo, false));


                categoryItemList.addAll(categoryItems);


                List<String> subCT = new ArrayList<>();
                for (int i = 0; i < categoryItemList.size(); i++) {
                    subCT.add(categoryItemList.get(i).getName());
                }
                binding.rvCategory.setAdapter(new CategorysFrameAdapter(context, subCT, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {
                        selectedCat = categoryItemList.get(pos).getId();
                        pageCount = 1;
//                        binding.onclickShimmer.setVisibility(View.VISIBLE);
//                        getData();
                    }
                }));

            }


        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}