package com.motivation.quotes.app.ui.activities;

import static com.motivation.quotes.app.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.motivation.quotes.app.AdsUtils.AdsUtils;
import com.motivation.quotes.app.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.R;
import com.motivation.quotes.app.ui.adapters.CategorysAdapter;
import com.motivation.quotes.app.ui.adapters.FestivalPostAdapter;
import com.growwthapps.dailypost.v2.databinding.ActivityCreatePostBinding;
import com.motivation.quotes.app.listener.AdapterClickListener;
import com.motivation.quotes.app.listener.ClickListener;
import com.motivation.quotes.app.model.CategoryItem;
import com.motivation.quotes.app.model.PostItem;
import com.motivation.quotes.app.utils.Constant;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {
    private ActivityCreatePostBinding binding;
    Activity context;
    String profileImagePath;

    private String selectedCat = "-1";
    private String catName = "";
    private String catId = "";
    List<CategoryItem> categoryItemList = new ArrayList<>();
    private FestivalPostAdapter festivalAdapter;
    private InterstitialsAdsManager interstitialsAdsManager;
    Uri imageUri;
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    if (result.getData() != null) {
                        getImageFromURI(result);
                    }
                }
            });


    private void getImageFromURI(ActivityResult result) {
        Uri selectedImage = result.getData().getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        if (selectedImage != null) {
            Cursor cursor = context.getContentResolver().query(selectedImage,
                    null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                profileImagePath = cursor.getString(columnIndex);
                cursor.close();

                beginCrop(selectedImage);
            }
        }
    }

    private void beginCrop(Uri uri) {
        if (uri != null) {
            try {
                Uri destinationUri = Uri.fromFile(new File(context.getCacheDir(), new File(uri.getPath()).getName()));
                UCrop.Options options2 = new UCrop.Options();
                options2.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options2.setFreeStyleCropEnabled(false);

                // Start cropping activity with startActivityForResult
                UCrop.of(uri, destinationUri)
                        .withAspectRatio(1080, 1080)
                        .withOptions(options2)
                        .start(context);  // "this" refers to your activity or fragment
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        topIconBar(this);

        context = this;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_createPost), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        interstitialsAdsManager = new InterstitialsAdsManager(context);

        new AdsUtils(context).showBannerAds(context);

        binding.shimmerGreetings.setVisibility(View.VISIBLE);
        interstitialsAdsManager = new InterstitialsAdsManager(context);

        binding.backImg.setOnClickListener(view -> {
            onBackPressed();
        });

        if (getIntent() != null){

            catId = getIntent().getStringExtra("catId");
            catName = getIntent().getStringExtra("title");
        }

        if (catId != null && !catId.isEmpty()){
            selectedCat = catId;
            binding.title.setText(""+catName);
            getBackground();
            binding.categoryBgRV.setVisibility(View.GONE);
            binding.mainBackground.setVisibility(View.VISIBLE);
            binding.shimmerGreetings.setVisibility(View.GONE);
            binding.shimmerGreetingOnclick.setVisibility(View.VISIBLE);
        }else {
            loadCategories();
            binding.title.setText("Background");

        }

    }


    private void loadCategories() {

        Constant.getHomeViewModel(this).getBackgroundCategory().observe(this, categoryItems -> {

            if (categoryItems != null && categoryItems.size() > 0) {
                categoryItemList.addAll(categoryItems);

                List<String> subCT = new ArrayList<>();
                for (int i = 0; i < categoryItemList.size(); i++) {
                    subCT.add(categoryItemList.get(i).getName());
                }
                selectedCat = categoryItemList.get(0).getId();
                getBackground();

                binding.categoryBgRV.setAdapter(new CategorysAdapter(context, subCT, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {
                        selectedCat = categoryItemList.get(pos).getId();
                        binding.shimmerGreetingOnclick.setVisibility(View.VISIBLE);
                        binding.mainBackground.setVisibility(View.GONE);
                        getBackground();
                    }
                }));

                binding.shimmerGreetings.setVisibility(View.GONE);
                binding.main.setVisibility(View.VISIBLE);
                binding.mainBackground.setVisibility(View.VISIBLE);
                binding.shimmerGreetingOnclick.setVisibility(View.GONE);
            }

        });

    }


    private void getBackground() {

        Constant.getHomeViewModel(this).getBackgroundByCategory(selectedCat).observe(this, postItems -> {

            if (postItems != null && !postItems.isEmpty() && postItems.size() > 0) {

                festivalAdapter = new FestivalPostAdapter(context, postItems, new ClickListener<PostItem>() {
                    @Override
                    public void onClick(PostItem data) {

                        beginCrop(Uri.parse(data.image_url));

                    }
                }, false);

                festivalAdapter.setFestivalPost(postItems);
                binding.backgroundRv.setAdapter(festivalAdapter);
                binding.main.setVisibility(View.VISIBLE);
                binding.mainBackground.setVisibility(View.VISIBLE);
                binding.shimmerGreetingOnclick.setVisibility(View.GONE);

            } else {

            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {

            if (data != null) {

                interstitialsAdsManager.showInterstitialAd(() -> {
                    imageUri = data.getExtras().getParcelable(UCrop.EXTRA_OUTPUT_URI);
                    Intent intent = new Intent(CreatePostActivity.this, EditorActivity.class);
                    intent.putExtra("imageUri", imageUri.toString());
                    startActivity(intent);
                });

            }

        }

    }

}