package com.growwthapps.dailypost.v2.ui.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.growwthapps.dailypost.v2.utils.Constant.IS_SUBSCRIBE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.AdsUtils.RewardAdsManager;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.ActivityMainBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;
import com.growwthapps.dailypost.v2.model.CategoryItem;
import com.growwthapps.dailypost.v2.model.PostItem;
import com.growwthapps.dailypost.v2.ui.adapters.CategorysAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.DailyPostAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.StoryAdapter;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.NetworkConnectivity;
import com.growwthapps.dailypost.v2.utils.PaginationListener;
import com.growwthapps.dailypost.v2.utils.PermissionUtils;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    StoryAdapter festivalAdapter;
    Activity context;
    private InterstitialsAdsManager interstitialsAdsManager;
    private String selectedLanguage = "";
    PreferenceManager preferenceManager;
    private String selectedCat = "-1";
    int pageCount = 1;
    List<PostItem> dailyPost = new ArrayList<>();
    DailyPostAdapter adapter;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        MyUtils.status_bar_deny(this);

        networkConnectivity = new NetworkConnectivity(this);

        takePermissionUtils = new PermissionUtils(this, mPermissionResult);

        setContentView(binding.getRoot());
        context = this;
        preferenceManager = new PreferenceManager(context);
        interstitialsAdsManager = new InterstitialsAdsManager(context);
        layoutManager = new GridLayoutManager(context, 1);

        binding.shimmerViewContainer.setVisibility(View.VISIBLE);
        festival();
        loadCategories();
        getData();

        binding.downloadPost.setOnClickListener(view -> {
            Intent intent = new Intent(this, DownloadActivity.class);
            startActivity(intent);
        });
        binding.createActionButton.setBackgroundTintList(null);
        binding.createActionButton.setBackgroundResource(R.drawable.action_btn_bg_1);
        binding.createActionButton.shrink();

        binding.tryAgain.setOnClickListener(v -> {
            recreate();
        });

        binding.createActionButton.setOnClickListener(view -> binding.createActionButton.extend());

        binding.circularImageView.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        binding.createActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivity(intent);
        });

        setUpRecyclerView();

        if (preferenceManager.getString("DataType").equals("Business")) {
            binding.changeProfileImg.setImageResource(R.drawable.db_personal_img);
            if (preferenceManager.getString(Constant.BUSINESS_LOGO) != null && !preferenceManager.getString(Constant.BUSINESS_LOGO).isEmpty()) {
                GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.BUSINESS_LOGO));
            }

        } else {

            binding.changeProfileImg.setImageResource(R.drawable.ic_union);
            if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
                GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.USER_IMAGE));
            }

        }

        binding.changeProfileLay.setOnClickListener(view -> {

            pageCount = 1;
            loading = false;
            selectedCat = "-1";

            if (binding.switchProfile.clRoot.getVisibility() == View.GONE) {

                if (!preferenceManager.getString("DataType").equals("Business")) {

                    preferenceManager.setString("DataType", "Business");
                    binding.switchProfile.dialogMessageTxt.setText("Switching to Business");
                    binding.changeProfileImg.setImageResource(R.drawable.db_personal_img);
                    binding.switchProfile.dialogProfileImg.setImageResource(R.drawable.dialog_business_img);
                    binding.createActionButton.setVisibility(View.GONE);

                    if (preferenceManager.getString(Constant.BUSINESS_LOGO) != null && !preferenceManager.getString(Constant.BUSINESS_LOGO).isEmpty()) {

                        GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.BUSINESS_LOGO));
                    }
                    getData();
                } else {

                    preferenceManager.setString("DataType", "Personal");
                    binding.switchProfile.dialogMessageTxt.setText("Switching to Personal");
                    binding.changeProfileImg.setImageResource(R.drawable.ic_union);
                    binding.switchProfile.dialogProfileImg.setImageResource(R.drawable.dialog_personal_img);
                    binding.createActionButton.setVisibility(View.GONE);

                    if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {

                        GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.USER_IMAGE));

                    }

                    getData();
                }

                binding.switchProfile.clRoot.setVisibility(View.VISIBLE);

            }

        });

        binding.premium.setOnClickListener(view -> startActivity(new Intent(context, SubscriptionActivity.class)));

        binding.swipeRefresh.setOnRefreshListener(() -> {
            pageCount = 1;
            loading = false;
            selectedCat = "-1";
            binding.swipeRefresh.setRefreshing(false);
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.main.setVisibility(View.GONE);
            dailyPost.clear();

            if (adapter != null) {
                adapter.clearData();
            }
            if (festivalAdapter != null) {
                festivalAdapter.clearData();
            }
            festival();
            loadCategories();
            categoryItemList.clear();

            new Handler().postDelayed(() -> {
                getData();

            }, 2000);

        });


        binding.allVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItemPosition = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Get the layout manager of the RecyclerView
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

                    // Get the current first visible item position
                    int currentFirstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    // Check if scrolling up (dy < 0 means user is scrolling up)
                    if (dy < 0) {
                        binding.createActionButton.shrink();
                    }

                    // Check if scrolling down (dy > 0 means user is scrolling down)
                    if (dy > 0) {
                        binding.createActionButton.extend();
                    }

                    // Update the last visible item position
                    lastVisibleItemPosition = currentFirstVisibleItem;
                }
            }
        });


    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                }
            });


    private void setUpRecyclerView() {


        binding.allVideo.setLayoutManager(layoutManager);

        binding.allVideo.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return loading;
            }

            @Override
            public void loadMoreItems() {
                loading = true;
                pageCount = pageCount + 1;

                //   binding.progreee.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> loadDataMore(), 100);


            }
        });


    }

    //load festival
    private void festival() {

        Constant.getHomeViewModel(this).getFestival().observe(this, featureItems -> {
            if (featureItems != null && featureItems.size() > 0) {


                festivalAdapter = new StoryAdapter(context, item -> {

                    Intent intent = new Intent(context, DailyPostActivity.class);
                    intent.putExtra(Constant.INTENT_FEST_NAME, item.title);
                    intent.putExtra(Constant.INTENT_FEST_ID, item.festivalId);

                    startActivity(intent);
                });
                festivalAdapter.setItemList(featureItems);
                binding.rvStory.setAdapter(festivalAdapter);
                binding.llStory.setVisibility(View.VISIBLE);

            }else {
                binding.llStory.setVisibility(View.GONE);
            }
        });

    }


    //load categories
    private void loadCategories() {


        Constant.getHomeViewModel(this).getCategories("featured").observe(this, categoryItems -> {

            if (categoryItems != null) {
                categoryItemList.add(new CategoryItem("-1", "All", R.drawable.logo, false));


                categoryItemList.addAll(categoryItems);


                List<String> subCT = new ArrayList<>();
                for (int i = 0; i < categoryItemList.size(); i++) {
                    subCT.add(categoryItemList.get(i).getName());
                }
                binding.rvCategory.setAdapter(new CategorysAdapter(context, subCT, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {
                        selectedCat = categoryItemList.get(pos).getId();
                        pageCount = 1;
                        getData();
                    }
                }));


            }


        });


    }

    //load Post by category
    private void getData() {

        if (!networkConnectivity.isConnected()) {

            binding.noInternetLayout.setVisibility(View.VISIBLE);
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.main.setVisibility(View.GONE);
            binding.createActionButton.setVisibility(View.VISIBLE);

            return;
        }


        binding.noDataLayout.setVisibility(View.GONE);
        selectedLanguage = preferenceManager.getString(Constant.USER_LANGUAGE);
        if (selectedLanguage.equals("-1")){

            selectedLanguage = "";
        }
        Log.d("selectedLanguage", "getData: " + selectedLanguage + " " + selectedCat + " " + pageCount);
        dailyPost.clear();

        if (adapter != null) {
            adapter.clearData();
            adapter.notifyDataSetChanged();
        }
        Log.d("IS_SUBSCRIBE", "getData: "+preferenceManager.getBoolean(IS_SUBSCRIBE));

        Constant.getHomeViewModel(this).getDailyPosts(pageCount, selectedCat, selectedLanguage).observe(this, postItems -> {

            if (postItems != null && postItems.size() > 0) {


                int i = 0;

                while (i < postItems.size()) {
                    if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {

                        if (i % 3 == 0 && i != 0) {

                            dailyPost.add(null);
                        }

                    }
                    dailyPost.add(postItems.get(i));
                    i++;
                }

                MyUtils.showResponse(dailyPost);

                adapter = new DailyPostAdapter(context, dailyPost, (view, posterew, posterview2,  size, postItem) -> {
                    currentView = posterew;
                    rewateBtn = currentView.findViewById(R.id.watermarkLayout);
                    remove = currentView.findViewById(R.id.removeWatermark);
                    premium = currentView.findViewById(R.id.iv_premium);

                    if (preferenceManager.getBoolean(IS_SUBSCRIBE)){

                        remove.setVisibility(View.GONE);
                        premium.setVisibility(View.GONE);
                        rewateBtn.setVisibility(View.GONE);

                    }

                    if (view.getId() == R.id.watermarkLayout) {
                        setupDialogWatermarkOption();
                    } else if (view.getId() == R.id.downloadBtn) {
                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {

                            setupDialogPremium(postItem, "download");

                            return;
                        }
                        remove.setVisibility(View.GONE);
                        premium.setVisibility(View.GONE);
                        saveImage(GlideDataBinding.viewToBitmap(currentView), "download");

                    } else if (view.getId() == R.id.shareBtn) {


                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {

                            setupDialogPremium(postItem, "Share");

                            return;
                        }
                        remove.setVisibility(View.GONE);
                        premium.setVisibility(View.GONE);
                        saveImage(GlideDataBinding.viewToBitmap(currentView), "Share");

                    } else if (view.getId() == R.id.edit_Btn) {

                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {

                            setupDialogPremium(postItem, "edit");

                            return;
                        }

                        startActivity(new Intent(context, EditProfileActivity.class)
                                .putExtra("imageUrl", postItem.image_url));

                        Log.d("imageUrl", "getData: " + postItem.image_url);
                    }

                });
                adapter.setData(dailyPost);
                binding.allVideo.setAdapter(adapter);
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.main.setVisibility(View.VISIBLE);
                binding.switchProfile.clRoot.setVisibility(View.GONE);
                binding.noDataLayout.setVisibility(View.GONE);
                binding.premium.setVisibility(View.VISIBLE);
                binding.createActionButton.setVisibility(View.VISIBLE);

            } else {

                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.main.setVisibility(View.VISIBLE);
                binding.switchProfile.clRoot.setVisibility(View.GONE);
                binding.noDataLayout.setVisibility(View.VISIBLE);
                binding.premium.setVisibility(View.GONE);
                binding.createActionButton.setVisibility(View.GONE);

            }

        });
    }

    private void loadDataMore() {

        Constant.getHomeViewModel(this).getDailyPosts(pageCount, selectedCat, selectedLanguage).observe(this, postItems -> {

            if (postItems != null) {

                int i = 0;

                while (i < postItems.size()) {

                    if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {

                        if (i % 3 == 0 && i != 0) {

                            dailyPost.add(null);
                        }


                    }
                    dailyPost.add(postItems.get(i));
                    i++;
                }
                if (adapter != null) {
                    adapter.setData(dailyPost);
                }


                loading = false;

            }


        });
    }

    private void saveImage(Bitmap bitmap, String type) {

        interstitialsAdsManager.showInterstitialAd(() -> {

            String fileName = System.currentTimeMillis() + ".png";
            String filePath = Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                    + File.separator + fileName;


            boolean success = false;

            if (!new File(filePath).exists()) {
                try {
                    File file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                    ), "/" + getResources().getString(R.string.app_name));
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.create_dir_err),
                                    Toast.LENGTH_LONG).show();
                            success = false;
                        }
                    }
                    File file2 = new File(file.getAbsolutePath() + "/" + fileName);

                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                                bitmap.getHeight(), bitmap.getConfig());
                        Canvas canvas = new Canvas(createBitmap);
                        canvas.drawColor(-1);
                        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                        createBitmap.compress(Bitmap.CompressFormat.PNG,
                                100, fileOutputStream);
                        createBitmap.recycle();
                        fileOutputStream.flush();
                        fileOutputStream.close();


                        MediaScannerConnection.scanFile(context, new String[]{file2.getAbsolutePath()},
                                (String[]) null, (str, uri) -> {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("-> uri=");
                                    sb.append(uri);
                                    sb.append("-> FILE=");
                                    sb.append(file2.getAbsolutePath());
                                    Uri muri = Uri.fromFile(file2);
                                });
                        success = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        success = false;
                    }

                } catch (Exception e) {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (success) {
                    if (type.equals("download")) {


                        Util.showToast(context, getString(R.string.image_saved));
                        Intent intent = new Intent(context, ShareImageActivity.class);
                        intent.putExtra("uri", filePath);
                        startActivity(intent);
                    } else {
                        shareFileImageUri(getImageContentUri(new File(filePath)), type);
                    }
                } else {
                    Util.showToast(context, getString(R.string.error));
                }

            }
        });

    }

    public Uri getImageContentUri(File imageFile) {
        return Uri.parse(imageFile.getAbsolutePath());
    }

    public void shareFileImageUri(Uri path, String shareTo) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        switch (shareTo) {
            case "whtsapp":
                shareIntent.setPackage("com.whatsapp");
                break;
            case "fb":
                shareIntent.setPackage("com.facebook.katana");
                break;
            case "insta":
                shareIntent.setPackage("com.instagram.android");
                break;
            case "twter":
                shareIntent.setPackage("com.twitter.android");
                break;
        }
        shareIntent.setDataAndType(path, "image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);

        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt) + getPackageName());

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_txt) + getPackageName()));
    }

    public void setupDialogWatermarkOption() {


        dialogWatermarkOption = new Dialog(context);
        dialogWatermarkOption.requestWindowFeature(1);
        dialogWatermarkOption.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogWatermarkOption.setContentView(R.layout.dialog_layout_watermark_option);
        dialogWatermarkOption.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        ImageView close = dialogWatermarkOption.findViewById(R.id.cancel);
        LinearLayout subscription = dialogWatermarkOption.findViewById(R.id.cv_no);

        ProgressBar progressBar = dialogWatermarkOption.findViewById(R.id.pb_loading);
        close.setOnClickListener(view -> dialogWatermarkOption.dismiss());
        subscription.setOnClickListener(view -> startActivity(new Intent(context, SubscriptionActivity.class)));

        LinearLayout withouWatermark = dialogWatermarkOption.findViewById(R.id.cv_yes);

        withouWatermark.setOnClickListener(view -> {

            progressBar.setVisibility(View.VISIBLE);

            new RewardAdsManager(context, new RewardAdsManager.OnAdLoaded() {
                @Override
                public void onAdClosed() {

                    Toast.makeText(context, "Ad not loaded, Try Again", Toast.LENGTH_SHORT).show();
                    rewateBtn.setVisibility(View.VISIBLE);
                    dialogWatermarkOption.dismiss();

                }

                @Override
                public void onAdWatched() {

                    rewateBtn.setVisibility(View.GONE);
                    Toast.makeText(context, "Congratulations, Remove Watermark", Toast.LENGTH_SHORT).show();
                    dialogWatermarkOption.dismiss();

                }
            });


        });

        dialogWatermarkOption.show();
        Window window = dialogWatermarkOption.getWindow();
        window.setLayout(MATCH_PARENT, MATCH_PARENT);

    }

    public void setupDialogPremium(PostItem postItem, String type) {

        dialogPremium = new Dialog(context);
        dialogPremium.requestWindowFeature(1);
        dialogPremium.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogPremium.setContentView(R.layout.dialog_premium);
        dialogPremium.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        ImageView close = dialogPremium.findViewById(R.id.cancel);
        LinearLayout subscription = dialogPremium.findViewById(R.id.cv_no);

        ProgressBar progressBar = dialogPremium.findViewById(R.id.pb_loading);
        close.setOnClickListener(view -> dialogPremium.dismiss());
        subscription.setOnClickListener(view -> context.startActivity(new Intent(context, SubscriptionActivity.class)));

        LinearLayout withouWatermark = dialogPremium.findViewById(R.id.cv_yes);

        withouWatermark.setOnClickListener(view -> {

            progressBar.setVisibility(View.VISIBLE);

            new RewardAdsManager(context, new RewardAdsManager.OnAdLoaded() {
                @Override
                public void onAdClosed() {

                    Toast.makeText(context, "Ad not loaded, Try Again", Toast.LENGTH_SHORT).show();
                    dialogPremium.dismiss();

                }

                @Override
                public void onAdWatched() {


                    Toast.makeText(context, "Congratulations, Unlock Your Post", Toast.LENGTH_SHORT).show();
                    dialogPremium.dismiss();

                    if (type.equals("edit")) {

                        startActivity(new Intent(context, EditProfileActivity.class)
                                .putExtra("imageUrl", postItem.image_url));
                    } else {
                        remove.setVisibility(View.GONE);
                        premium.setVisibility(View.GONE);
                        saveImage(GlideDataBinding.viewToBitmap(currentView), type);

                    }


                }
            });


        });

        dialogPremium.show();
        Window window = dialogWatermarkOption.getWindow();
        window.setLayout(MATCH_PARENT, MATCH_PARENT);

    }


}