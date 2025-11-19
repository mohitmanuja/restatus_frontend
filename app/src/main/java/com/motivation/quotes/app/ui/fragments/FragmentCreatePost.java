package com.motivation.quotes.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.motivation.quotes.app.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.databinding.FragmentCreatePostBinding;
import com.motivation.quotes.app.listener.ClickListener;
import com.motivation.quotes.app.model.CategoryItem;
import com.motivation.quotes.app.model.PostItem;
import com.motivation.quotes.app.ui.activities.CreatePostActivity;
import com.motivation.quotes.app.ui.activities.EditorActivity;
import com.motivation.quotes.app.ui.adapters.BackgroundAdapter;
import com.motivation.quotes.app.ui.adapters.FestivalPostAdapter;
import com.motivation.quotes.app.ui.adapters.GreetingAdapter;
import com.motivation.quotes.app.utils.Constant;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentCreatePost extends Fragment {
    private FragmentCreatePostBinding binding;
    private String profileImagePath;
    private String selectedCat = "10";
    private final List<CategoryItem> categoryItemList = new ArrayList<>();
    private FestivalPostAdapter festivalAdapter;
    private BackgroundAdapter backgroundAdapter;
    private GreetingAdapter greetingAdapter;
    private InterstitialsAdsManager interstitialsAdsManager;
    private Uri imageUri;
    private final List<PostItem> postItem = new ArrayList<>();
    private ActivityResultLauncher<Intent> cropImageLauncher;
    boolean loading = false;


    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    getImageFromURI(result);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.shimmerLayout.setVisibility(View.VISIBLE);

        interstitialsAdsManager = new InterstitialsAdsManager(getActivity());

        loadCategories();
        getGreeting();

    }

    private void getImageFromURI(ActivityResult result) {
        Uri selectedImage = result.getData().getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        if (selectedImage != null) {
            Cursor cursor = getActivity().getContentResolver().query(
                    selectedImage,
                    null,
                    null,
                    null,
                    null
            );

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
                Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_" + System.currentTimeMillis() + ".png"));

                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options.setFreeStyleCropEnabled(false);

                Intent cropIntent = UCrop.of(uri, destinationUri)
                        .withAspectRatio(1, 1) // Use 1:1 instead of 1080:1080 for correct aspect ratio
                        .withOptions(options)
                        .getIntent(requireContext());
                cropImageLauncher.launch(cropIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadCategories() {

        Constant.getHomeViewModel(this).getBackgroundCategory().observe(getActivity(), categoryItems -> {


            if (categoryItems != null && categoryItems.size() > 0) {

                categoryItemList.add(new CategoryItem("-1", "name", "", false));

                categoryItemList.addAll(categoryItems);

                backgroundAdapter = new BackgroundAdapter(getActivity(), categoryItemList, new ClickListener<CategoryItem>() {
                    @Override
                    public void onClick(CategoryItem data) {
                        int position = categoryItemList.indexOf(data);


                        if (position == 5) {
                            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                            startActivity(intent);

                        } else if (position == 0) {

                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            someActivityResultLauncher.launch(i);

                        } else {

                            Intent intent = new Intent(getActivity(), CreatePostActivity.class)
                                    .putExtra("title", data.name)
                                    .putExtra("catId", data.id);
                            startActivity(intent);

                        }


                    }
                });

                binding.rlBackground.setAdapter(backgroundAdapter);
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.llNotFound.setVisibility(View.GONE);
                binding.coordinatorLayout.setVisibility(View.VISIBLE);

            }

        });

    }

    private void getGreeting() {
        Constant.getUserViewModel(this).getGreetingPostByCategory().observe(getActivity(), greetingData -> {

            if (greetingData != null && greetingData.size() > 0){

                greetingAdapter = new GreetingAdapter(getActivity());
                greetingAdapter.setFeatureItemList(greetingData);
                binding.rlGreetings.setAdapter(greetingAdapter);
                binding.shimmerLayout.setVisibility(View.GONE);

            }

        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cropImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        interstitialsAdsManager.showInterstitialAd(() -> {
                            Uri imageUri = result.getData().getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
                            Intent intent = new Intent(requireContext(), EditorActivity.class);
                            intent.putExtra("imageUri", imageUri.toString());
                            startActivity(intent);
                        });
                    }
                }
        );
    }

}
