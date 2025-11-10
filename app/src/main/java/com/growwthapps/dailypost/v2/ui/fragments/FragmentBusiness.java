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
import com.growwthapps.dailypost.v2.databinding.FragmentBusinessDetailsBinding;
import com.growwthapps.dailypost.v2.model.UpdateBusiness;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.ImageCropperFragment;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.viewmodel.HomeViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class FragmentBusiness extends Fragment {
    private FragmentBusinessDetailsBinding binding;
    Activity context;
    private String userImageUrl;
    PreferenceManager preferenceManager;
    private InterstitialsAdsManager interstitialsAdsManager;
    String profileImagePath;
    Uri imageUri;
    private String mediaType = "instagram";
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
                options2.setFreeStyleCropEnabled(false);

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

        binding.bLayChangeSocialMedia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        if (preferenceManager.getString(Constant.BUSINESS_NAME) != null && !preferenceManager.getString(Constant.BUSINESS_NAME).isEmpty()) {
            binding.etBusinessName.setText(preferenceManager.getString(Constant.BUSINESS_NAME));
        }


        if (preferenceManager.getString(Constant.BUSSINESS_INSTAGRAM) != null && !preferenceManager.getString(Constant.BUSSINESS_INSTAGRAM).isEmpty()) {
            binding.etBusinessSocialMedia.setText(preferenceManager.getString(Constant.BUSSINESS_INSTAGRAM));
        }

        if (preferenceManager.getString(Constant.BUSINESS_DETAIL) != null && !preferenceManager.getString(Constant.BUSINESS_DETAIL).isEmpty()) {
            binding.etBusinessDesignation.setText(preferenceManager.getString(Constant.BUSINESS_DETAIL));
        }

        if (preferenceManager.getString(Constant.BUSINESS_ADDRESS) != null && !preferenceManager.getString(Constant.BUSINESS_ADDRESS).isEmpty()) {
            binding.etBusinessAddress.setText(preferenceManager.getString(Constant.BUSINESS_ADDRESS));
        }

        if (preferenceManager.getString(Constant.BUSINESS_MOBILE) != null && !preferenceManager.getString(Constant.BUSINESS_MOBILE).isEmpty()) {
            binding.etContact.setText(preferenceManager.getString(Constant.BUSINESS_MOBILE));
        }

        if (preferenceManager.getString(Constant.BUSINESS_LOGO) != null && !preferenceManager.getString(Constant.BUSINESS_LOGO).isEmpty()) {
            userImageUrl = preferenceManager.getString(Constant.BUSINESS_LOGO);

            GlideDataBinding.bindImage(binding.bProfileImg, userImageUrl);
            binding.bProfileImg.setVisibility(View.VISIBLE);
        }

        if (preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE) != null && !preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE).isEmpty()) {

            mediaType = preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE);


            if (mediaType.equals("instagram")) {
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
                binding.bTxtSocialMedia.setText(R.string.str_47);
            } else if (mediaType.equals("facebook")) {
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
                binding.bTxtSocialMedia.setText(R.string.str_52);
            } else if (mediaType.equals("website")) {
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_website_img);
                binding.bTxtSocialMedia.setText(R.string.website);
            } else if (mediaType.equals("email")) {
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_email_img);
                binding.bTxtSocialMedia.setText(R.string.email);
            } else if (mediaType.equals("twitter")) {
                binding.bImgSocialMedia.setImageResource(R.drawable.share_twitter_img);
                binding.bTxtSocialMedia.setText(R.string.str_53);
            } else if (mediaType.equals("youtube")) {
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
                binding.bTxtSocialMedia.setText(R.string.str_51);
            }

        }


        binding.updateTxt.setOnClickListener(v1 -> {

            interstitialsAdsManager.showInterstitialAd(() -> {

                String businessName =  binding.etBusinessName.getText().toString();
                String businessAddress =  binding.etBusinessAddress.getText().toString();
                String businessDesignation =  binding.etBusinessDesignation.getText().toString();
                String businessContact =  binding.etContact.getText().toString();
                String businessSocialMedia =  binding.etBusinessSocialMedia.getText().toString();
                String businessLogo =  profileImagePath;


                Log.d("businessName1111", "onChanged: "+ userImageUrl);

                homeViewModel.updateBusiness(preferenceManager.getString(Constant.USER_ID),businessName, "",businessContact,businessLogo,businessDesignation,businessAddress,mediaType,businessSocialMedia).observe(getViewLifecycleOwner(), new Observer<UpdateBusiness>() {
                    @Override
                    public void onChanged(UpdateBusiness updateBusiness) {

                        Log.d("businessName22", "onChanged: "+ new Gson().toJson(updateBusiness));

                        preferenceManager.setString(Constant.BUSINESS_NAME, updateBusiness.data.name);
                        preferenceManager.setString(Constant.BUSINESS_ADDRESS, updateBusiness.data.address);
                        preferenceManager.setString(Constant.BUSINESS_DETAIL, updateBusiness.data.about);
                        preferenceManager.setString(Constant.BUSINESS_MOBILE, updateBusiness.data.mobile);
                        preferenceManager.setString(Constant.BUSSINESS_INSTAGRAM, updateBusiness.data.socialmedia_value);
                        preferenceManager.setString(Constant.BUSINESS_LOGO, updateBusiness.data.logo);
                        preferenceManager.setString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE, updateBusiness.data.socialmedia_type);

                        Toast.makeText(context, "Business Details Saved", Toast.LENGTH_SHORT).show();

                        context.finish();

                    }
                });



            });
        });

        binding.bProfileLay.setOnClickListener(v -> {

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
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_email_img);
                binding.bTxtSocialMedia.setText(getString(R.string.email));
                binding.etBusinessSocialMedia.setHint(getString(R.string.email) + " Id");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMFacebookLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "facebook";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
                binding.bTxtSocialMedia.setText(getString(R.string.str_52));
                binding.etBusinessSocialMedia.setHint(getString(R.string.str_52) + " Id");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMInstagramLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "instagram";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
                binding.bTxtSocialMedia.setText(getString(R.string.str_47));
                binding.etBusinessSocialMedia.setHint(getString(R.string.str_47) + " Id");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMTwitterLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "twitter";
                binding.bImgSocialMedia.setImageResource(R.drawable.share_twitter_img);
                binding.bTxtSocialMedia.setText(getString(R.string.str_53));
                binding.etBusinessSocialMedia.setHint(getString(R.string.str_53) + " Id");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMYoutubeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "youtube";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
                binding.bTxtSocialMedia.setText(getString(R.string.str_51));
                binding.etBusinessSocialMedia.setHint(getString(R.string.str_51) + " Url");
                dialog.dismiss();
            }
        });
        bindingDialog.dSMWebsiteLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "website";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_website_img);
                binding.bTxtSocialMedia.setHint(getString(R.string.website));
                binding.etBusinessSocialMedia.setHint(getString(R.string.website) + " Url");
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBusinessDetailsBinding.inflate(getLayoutInflater());
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
                    imageUri = Uri.parse(out);
                    GlideDataBinding.bindImage(binding.bProfileImg, out);
                    binding.bProfileImg.setVisibility(View.VISIBLE);

                }).show(getChildFragmentManager(), "");
            }

        }

    }
}