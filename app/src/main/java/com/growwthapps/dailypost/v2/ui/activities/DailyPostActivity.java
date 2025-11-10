package com.growwthapps.dailypost.v2.ui.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.growwthapps.dailypost.v2.utils.Constant.INTENT_FEST_ID;
import static com.growwthapps.dailypost.v2.utils.Constant.INTENT_FEST_NAME;
import static com.growwthapps.dailypost.v2.utils.Constant.IS_SUBSCRIBE;
import static com.growwthapps.dailypost.v2.utils.Constant.NATIVE_AD_COUNT;
import static com.growwthapps.dailypost.v2.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.growwthapps.dailypost.v2.AdsUtils.AdsUtils;
import com.growwthapps.dailypost.v2.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.AdsUtils.RewardAdsManager;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.ui.adapters.DailyPostAdapter;
import com.growwthapps.dailypost.v2.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.ActivityPersonalPostBinding;
import com.growwthapps.dailypost.v2.model.PostItem;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.PaginationListener;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DailyPostActivity extends AppCompatActivity {

    ActivityPersonalPostBinding binding;
    Activity context;
    int pageCount = 1;
    int toSize = 0;
    GridLayoutManager layoutManager;
    boolean loading = false;
    DailyPostAdapter adapter;
    List<PostItem> dailyPost = new ArrayList<>();
    PreferenceManager preferenceManager;
    private String selectedLanguage = "";
    private String selectedCat = "-1";
    private InterstitialsAdsManager interstitialsAdsManager;
    public static Dialog dialogPremium;
    private Dialog dialogWatermarkOption;

    View currentView, currentView2;
    RelativeLayout rewateBtn;
    ImageView remove, premium;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalPostBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        topIconBar(this);

        context = this;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainPersonalPost), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        interstitialsAdsManager = new InterstitialsAdsManager(context);

        new AdsUtils(context).showBannerAds(context);

        preferenceManager = new PreferenceManager(context);

        shimmer(View.VISIBLE);

        String name = getIntent().getStringExtra(INTENT_FEST_NAME);
        selectedCat = getIntent().getStringExtra(INTENT_FEST_ID);

        binding.toolbar.rlToolbar.setVisibility(View.VISIBLE);
        layoutManager = new GridLayoutManager(context, 1);
        binding.toolbar.back.setOnClickListener(view -> finish());
        binding.toolbar.toolName.setText(name);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            pageCount = 1;
            loading = false;

            binding.swipeRefresh.setRefreshing(false);
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.allVideos.setVisibility(View.VISIBLE);
            binding.noDataLayout.setVisibility(View.GONE);

            new Handler().postDelayed(() -> getData(), 2000);

        });

        getData();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {


        binding.allVideos.setLayoutManager(layoutManager);

        binding.allVideos.addOnScrollListener(new PaginationListener(layoutManager) {
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

                new Handler().postDelayed(() -> loadDataMore(), 100);


            }
        });


    }

    private void getData() {

        binding.noDataLayout.setVisibility(View.GONE);

        selectedLanguage = preferenceManager.getString(Constant.USER_LANGUAGE);
        if (selectedLanguage.equals("-1")) {

            selectedLanguage = "";
        }
        Log.d("selectedLanguage", "getData: " + selectedLanguage + " " + selectedCat + " " + pageCount);
        dailyPost.clear();

        if (adapter != null) {
            adapter.clearData();
            adapter.notifyDataSetChanged();
        }


        Constant.getHomeViewModel(this).getFestivalPost(pageCount, selectedCat, selectedLanguage).observe(this, postItems -> {

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

                adapter = new DailyPostAdapter(context, dailyPost, (view, posterew, posterview2,  size,postItem) -> {
                    currentView = posterew;
                    currentView2 = posterview2;
                    toSize = size;
                    rewateBtn = currentView.findViewById(R.id.watermarkLayout);
                    remove = currentView.findViewById(R.id.removeWatermark);
                    premium = currentView.findViewById(R.id.iv_premium);

                    if (preferenceManager.getBoolean(IS_SUBSCRIBE)) {

                        remove.setVisibility(View.GONE);
                        premium.setVisibility(View.GONE);
                        rewateBtn.setVisibility(View.GONE);

                    }

                    if (view.getId() == R.id.downloadBtn) {

                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {

                            setupDialogPremium(postItem, "download");

                            return;
                        }

                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE)){

                            setupDialogWatermarkOption(postItem,"download");
                            return;
                        }

                        remove.setVisibility(View.GONE);
                        premium.setVisibility(View.GONE);

                        if (postItem.is_video){
                            MyUtils.applyFrameNMusicProcess(context, toSize, posterview2, posterew, postItem.video_path, "download");
                        }else {

                            saveImage(GlideDataBinding.viewToBitmap(currentView), "download");
                        }




                    } else if (view.getId() == R.id.shareBtn) {


                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {

                            setupDialogPremium(postItem, "Share");

                            return;
                        }


                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE)){

                            setupDialogWatermarkOption(postItem,"Share");
                            return;
                        }
                        remove.setVisibility(View.GONE);
                        premium.setVisibility(View.GONE);
                        if (postItem.is_video){
                            MyUtils.applyFrameNMusicProcess(context, toSize, posterview2, posterew, postItem.video_path, "Share");
                        }else {

                            saveImage(GlideDataBinding.viewToBitmap(currentView), "Share");
                        }


                    } else if (view.getId() == R.id.edit_Btn) {

                        startActivity(new Intent(context, EditProfileActivity.class));

                    }

                });
                adapter.setData(dailyPost);
                binding.allVideos.setVisibility(View.VISIBLE);
                binding.allVideos.setAdapter(adapter);
                binding.noDataLayout.setVisibility(View.GONE);
                binding.shimmerViewContainer.setVisibility(View.GONE);

            } else {

                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.allVideos.setVisibility(View.VISIBLE);
                binding.noDataLayout.setVisibility(View.VISIBLE);

            }

        });
    }

    private void loadDataMore() {

        Constant.getHomeViewModel(this).getFestivalPost(pageCount, selectedCat, selectedLanguage).observe(this, postItems -> {

            if (postItems != null) {

                int i = 0;

                while (i < postItems.size()) {

                    if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {

                        if (i % preferenceManager.getInt(NATIVE_AD_COUNT) == 0 && i != 0) {

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

    private void shimmer(int gone) {

        binding.shimmerViewContainer.setVisibility(gone);
    }

    private void saveImage(Bitmap bitmap, String type) {

        remove.setVisibility(View.GONE);
        premium.setVisibility(View.GONE);

        interstitialsAdsManager.showInterstitialAd(new InterstitialsAdsManager.onAdClosedListener() {
            @Override
            public void onAdClosed() {

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

                        preferenceManager.setBoolean("save_image", true);
                    } else {
                        Util.showToast(context, getString(R.string.error));
                    }

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

    private void setupDialogWatermarkOption(PostItem postItem, String type) {


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
        LinearLayout with_watermark = dialogWatermarkOption.findViewById(R.id.with_watermark);

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
                    //  Toast.makeText(context, "Congratulations, Remove Watermark", Toast.LENGTH_SHORT).show();
                    dialogWatermarkOption.dismiss();

                    if (postItem.is_video){
                        MyUtils.applyFrameNMusicProcess(context, toSize, currentView2, currentView, postItem.video_path, type);
                    }else {

                        saveImage(GlideDataBinding.viewToBitmap(currentView), type);
                    }

                }
            });


        });


        with_watermark.setOnClickListener(view -> {
            remove.setVisibility(View.GONE);
            premium.setVisibility(View.GONE);

            if (postItem.is_video){
                MyUtils.applyFrameNMusicProcess(context, toSize, currentView2, currentView, postItem.video_path, type);
            }else {

                saveImage(GlideDataBinding.viewToBitmap(currentView), type);
            }

            dialogWatermarkOption.dismiss();
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

                        rewateBtn.setVisibility(View.GONE);
                        remove.setVisibility(View.GONE);
                        premium.setVisibility(View.GONE);

                        if (postItem.is_video){
                            MyUtils.applyFrameNMusicProcess(context, toSize, currentView2, currentView, postItem.video_path, type);
                        }else {
                            saveImage(GlideDataBinding.viewToBitmap(currentView), type);
                        }



                    }


                }
            });


        });

        dialogPremium.show();
        Window window = dialogWatermarkOption.getWindow();
        window.setLayout(MATCH_PARENT, MATCH_PARENT);

    }
}