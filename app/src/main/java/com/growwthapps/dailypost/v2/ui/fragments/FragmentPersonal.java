package com.growwthapps.dailypost.v2.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.gson.Gson;
import com.growwthapps.dailypost.v2.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.DialogSocialMediaBinding;
import com.growwthapps.dailypost.v2.databinding.FragmentPersonalDetailBinding;
import com.growwthapps.dailypost.v2.model.UpdateUserProfile;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.ImageCropperFragment;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.viewmodel.HomeViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;


public class FragmentPersonal extends Fragment {
    private FragmentPersonalDetailBinding binding;
    Activity context;
    PreferenceManager preferenceManager;
    private InterstitialsAdsManager interstitialsAdsManager;
    String profileImagePath;
    Uri imageUri1;
    private String userImageUrl;

    Uri imageUri;

    HomeViewModel homeViewModel;


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
    private String userImage;
    private String mediaType = "instagram";

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

                imageUri = selectedImage;

                beginCrop(imageUri);


            }
        }
    }

    private void beginCrop(Uri uri) {
        if (uri != null) {
            try {
                Uri destinationUri = Uri.fromFile(new File(context.getCacheDir(), new File(uri.getPath()).getName()));
                UCrop.Options options2 = new UCrop.Options();
                options2.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options2.setFreeStyleCropEnabled(true);

                // Start cropping activity with startActivityForResult
                UCrop.of(uri, destinationUri)
                        .withAspectRatio(Constant.BUSINESS_LOGO_WIDTH, Constant.BUSINESS_LOGO_HEIGHT)
                        .withOptions(options2)
                        .start(context, this);  // "this" refers to your activity or fragment
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new HomeViewModel();


        interstitialsAdsManager = new InterstitialsAdsManager(context);
        preferenceManager = new PreferenceManager(context);

        binding.pLayChangeSocialMedia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        if (preferenceManager.getString(Constant.USER_NAME) != null && !preferenceManager.getString(Constant.USER_NAME).isEmpty()) {
            binding.etname.setText(preferenceManager.getString(Constant.USER_NAME));

        }
        if (preferenceManager.getString(Constant.USER_DESIGNATION) != null && !preferenceManager.getString(Constant.USER_DESIGNATION).isEmpty()) {
            binding.etDesignation.setText(preferenceManager.getString(Constant.USER_DESIGNATION));
        }
        if (preferenceManager.getString(Constant.USER_PHONE) != null && !preferenceManager.getString(Constant.USER_PHONE).isEmpty()) {
            binding.etMobile.setText(preferenceManager.getString(Constant.USER_PHONE));
        }

        if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA) != null && !preferenceManager.getString(Constant.USER_SOCIAL_MEDIA).isEmpty()) {
            binding.etSocialMedia.setText(preferenceManager.getString(Constant.USER_SOCIAL_MEDIA));
        }
        if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
            userImageUrl = preferenceManager.getString(Constant.USER_IMAGE);

            GlideDataBinding.bindImage(binding.pProfileImg, userImageUrl);
            binding.pProfileImg.setVisibility(View.VISIBLE);
            binding.llUploadImg.setVisibility(View.GONE);

        }

        if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE) != null && !preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).isEmpty()) {

            mediaType = preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE);
            if (mediaType.equals("instagram")) {
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
                binding.pTxtSocialMedia.setText(R.string.str_47);
            } else if (mediaType.equals("facebook")) {
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
                binding.pTxtSocialMedia.setText(R.string.str_52);
            } else if (mediaType.equals("website")) {
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_website_img);
                binding.pTxtSocialMedia.setText(R.string.website);
            } else if (mediaType.equals("email")) {
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_email_img);
                binding.pTxtSocialMedia.setText(R.string.email);
            } else if (mediaType.equals("twitter")) {
                binding.pImgSocialMedia.setImageResource(R.drawable.share_twitter_img);
                binding.pTxtSocialMedia.setText(R.string.str_53);
            } else if (mediaType.equals("youtube")) {
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
                binding.pTxtSocialMedia.setText(R.string.str_51);
            }

        }

        binding.updateTxt.setOnClickListener(v1 -> {

            interstitialsAdsManager.showInterstitialAd(() -> {

                String userName = binding.etname.getText().toString();
                String designation = binding.etDesignation.getText().toString();
                String phone = binding.etMobile.getText().toString();
                String userSocialMedia = binding.etSocialMedia.getText().toString();
                String userImage = profileImagePath;


                homeViewModel.updateUserProfile(preferenceManager.getString(Constant.USER_ID), userName, "", phone, userImage, designation, mediaType, userSocialMedia).observe(getViewLifecycleOwner(), new Observer<UpdateUserProfile>() {
                    @Override
                    public void onChanged(UpdateUserProfile updateUserProfile) {

                        preferenceManager.setString(Constant.USER_NAME, updateUserProfile.data.name);
                        preferenceManager.setString(Constant.USER_SOCIAL_MEDIA, updateUserProfile.data.socialmedia_value);
                        preferenceManager.setString(Constant.USER_SOCIAL_MEDIA_TYPE, updateUserProfile.data.socialmedia_type);
                        preferenceManager.setString(Constant.USER_IMAGE, updateUserProfile.data.logo);
                        preferenceManager.setString(Constant.USER_PHONE, updateUserProfile.data.mobile);
                        preferenceManager.setString(Constant.USER_DESIGNATION, updateUserProfile.data.designation);
                        context.finish();
                        Toast.makeText(context, "Details Saved", Toast.LENGTH_SHORT).show();

                    }
                });


            });
        });

        binding.pProfileLay.setOnClickListener(v -> {

            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            someActivityResultLauncher.launch(i);

        });

    }


    private void showDialog() {
        RoundedBottomSheetDialog dialog = new RoundedBottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogSocialMediaBinding bindingDialog = DialogSocialMediaBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingDialog.getRoot());

        bindingDialog.dSMEmailLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "email";
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_email_img);
                binding.pTxtSocialMedia.setText(getString(R.string.email));
                binding.etSocialMedia.setHint(getString(R.string.email) + " Id");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMFacebookLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "facebook";
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
                binding.pTxtSocialMedia.setText(getString(R.string.str_52));
                binding.etSocialMedia.setHint(getString(R.string.str_52) + " Id");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMInstagramLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "instagram";
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
                binding.pTxtSocialMedia.setText(getString(R.string.str_47));
                binding.etSocialMedia.setHint(getString(R.string.str_47) + " Id");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMTwitterLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "twitter";
                binding.pImgSocialMedia.setImageResource(R.drawable.share_twitter_img);
                binding.pTxtSocialMedia.setText(getString(R.string.str_53));
                binding.etSocialMedia.setHint(getString(R.string.str_53) + " Id");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMYoutubeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "youtube";

                binding.pImgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
                binding.pTxtSocialMedia.setText(getString(R.string.str_51));
                binding.etSocialMedia.setHint(getString(R.string.str_51) + " Url");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMWebsiteLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "website";

                binding.pImgSocialMedia.setImageResource(R.drawable.ep_website_img);
                binding.pTxtSocialMedia.setText(getString(R.string.website));
                binding.etSocialMedia.setHint(getString(R.string.website) + " Url");
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalDetailBinding.inflate(getLayoutInflater());
        context = getActivity();
        return binding.getRoot();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == UCrop.REQUEST_CROP) {


            if (data != null) {


                new ImageCropperFragment(0, MyUtils.getPathFromURI(context, UCrop.getOutput(data)), (id, out) -> {

                    profileImagePath = out;
                    userImageUrl = out;
                    //  imageUri = Uri.parse(out);
                    GlideDataBinding.bindImage(binding.pProfileImg, out);

                    binding.pProfileImg.setVisibility(View.VISIBLE);

                }).show(getChildFragmentManager(), "");
            }

        }

    }
}