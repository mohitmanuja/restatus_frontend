package com.motivation.quotes.app.ui.activities;

import static com.motivation.quotes.app.binding.GlideDataBinding.viewToBitmap;
import static com.motivation.quotes.app.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.growwthapps.dailypost.v2.R;
import com.motivation.quotes.app.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.ActivitySavePostBinding;
import com.growwthapps.dailypost.v2.databinding.AdViewBinding;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.PreferenceManager;
import com.motivation.quotes.app.utils.SnapHelperOneByOne;
import com.motivation.quotes.app.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.activity.EdgeToEdge;

public class SavePostActivity extends AppCompatActivity {


    private Activity context;
    private String path = null;
    private PreferenceManager preferenceManager;
    private String userName;
    private String userImage;
    private String userNumber;
    private String userDesignation, userAddress, userSocial;
    private String businessName, businessMedia, businessMail, businessAddress, businessDesignation, businessNumber, businessImage;
    public static ActivitySavePostBinding binding;
    CustomPagerAdapter customPagerAdapter;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavePostBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        topIconBar(this);

        context = this;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainSavePost), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.bg_screen));

        preferenceManager = new PreferenceManager(context);

        path = getIntent().getStringExtra("uri");
        type = getIntent().getStringExtra("type");

        Log.d("typetype", "onCreate: "+type);

        if (preferenceManager.getString("DataType").equals("Business")) {

            binding.imageB.setVisibility(View.VISIBLE);
            binding.imageP.setVisibility(View.GONE);
        } else {
            binding.imageP.setVisibility(View.VISIBLE);
            binding.imageB.setVisibility(View.GONE);

        }

        binding.changeProfileLay.setOnClickListener(view -> {
            binding.switchProfile.clRoot.setVisibility(View.VISIBLE);
            if (!preferenceManager.getString("DataType").equals("Business")) {

                preferenceManager.setString("DataType", "Business");
                binding.imageB.setVisibility(View.VISIBLE);
                binding.imageP.setVisibility(View.GONE);
                binding.textFrameType.setText("Personal Frame");
                binding.switchProfile.dialogProfileImg.setImageResource(R.drawable.dialog_business_img);
                binding.switchProfile.dialogMessageTxt.setText(R.string.str_31);
                if (customPagerAdapter != null) {
                    customPagerAdapter.notifyDataSetChanged();
                    customPagerAdapter = new CustomPagerAdapter(path);
                    binding.recyclerview.setAdapter(customPagerAdapter);
                    binding.indicator.attachToRecyclerView(binding.recyclerview);

                }
                setupColorChangeListeners3(customPagerAdapter);
            } else {
                binding.imageP.setVisibility(View.VISIBLE);
                binding.imageB.setVisibility(View.GONE);
                binding.textFrameType.setText("Business Frame");
                preferenceManager.setString("DataType", "Personal");
                binding.switchProfile.dialogProfileImg.setImageResource(R.drawable.dialog_personal_img);
                binding.switchProfile.dialogMessageTxt.setText("Switching to Personal");
                if (customPagerAdapter != null) {
                    customPagerAdapter.notifyDataSetChanged();
                    customPagerAdapter = new CustomPagerAdapter(path);
                    binding.recyclerview.setAdapter(customPagerAdapter);
                    binding.indicator.attachToRecyclerView(binding.recyclerview);
                }
                setupColorChangeListeners3(customPagerAdapter);
            }
            new Handler().postDelayed(() -> {
                binding.switchProfile.clRoot.setVisibility(View.GONE);

            }, 2000);



        });

        Glide.with(this).load(path).placeholder(R.drawable.spaceholder).into(binding.imageB);
        Glide.with(this).load(path).placeholder(R.drawable.spaceholder).into(binding.imageP);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        customPagerAdapter = new CustomPagerAdapter(path);
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        binding.recyclerview.setAdapter(customPagerAdapter);
        binding.indicator.attachToRecyclerView(binding.recyclerview);

        SnapHelperOneByOne snapHelperOneByOne = new SnapHelperOneByOne();
        snapHelperOneByOne.attachToRecyclerView(binding.recyclerview);

        binding.layShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(viewToBitmap(binding.pLayoutTemp) , false);
            }
        });

        binding.layDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(viewToBitmap(binding.pLayoutTemp) , true);
            }
        });

        binding.backImg.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.editTxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        setupColorChangeListeners3(customPagerAdapter);

    }


    public class CustomPagerAdapter extends RecyclerView.Adapter<CustomPagerAdapter.ViewHolder> {


        String item_url;
        List<Integer> list = new ArrayList<>();
        private int color_id = 0;

        public void setColor(int colorId) {
            this.color_id = colorId;
        }


        public CustomPagerAdapter(String str) {
            this.item_url = str;


            if (preferenceManager.getString("DataType").equals("Business")) {


                list.add(R.layout.layout_frame_business_1_1);
                list.add(R.layout.layout_frame_business_1_2);
                list.add(R.layout.layout_frame_business_1_3);
                list.add(R.layout.layout_frame_business_1_4);


            } else {

                list.add(R.layout.layout_frame_personal_1_1);
                list.add(R.layout.layout_frame_personal_1_2);
                list.add(R.layout.layout_frame_personal_1_3);
                list.add(R.layout.layout_frame_personal_1_4);
                list.add(R.layout.layout_frame_personal_2_1);
                list.add(R.layout.layout_frame_personal_2_2);

            }


        }

        @NonNull
        @Override
        public CustomPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CustomPagerAdapter.ViewHolder(LayoutInflater.from(context).inflate(list.get(viewType), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CustomPagerAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);

            if (preferenceManager.getString("DataType").equals("Business")) {

                businessName = preferenceManager.getString(Constant.BUSINESS_NAME);
                businessImage = preferenceManager.getString(Constant.BUSINESS_LOGO);
                businessNumber = preferenceManager.getString(Constant.BUSINESS_MOBILE);
                businessDesignation = preferenceManager.getString(Constant.BUSINESS_DETAIL);
                businessAddress = preferenceManager.getString(Constant.BUSINESS_ADDRESS);
                businessMail = preferenceManager.getString(Constant.BUSSINESS_EMAIL);
                businessMedia = preferenceManager.getString(Constant.BUSSINESS_INSTAGRAM);

                if (businessName.isEmpty()) {

                    businessName = "Your business Name";
                }
                if (businessAddress.isEmpty()) {
                    businessAddress = "Your business Address";
                }
                if (businessMail.isEmpty()) {
                    businessMail = "Your Mail";
                }
                if (businessNumber.isEmpty()) {
                    businessNumber = "Your business Number";
                }
                if (businessDesignation.isEmpty()) {
                    businessDesignation = "Your business Designation";
                }
                if (businessMail.isEmpty()) {
                    businessMail = "Your Mail";
                }
                if (businessMedia.isEmpty()) {
                    businessMedia = "Your Media handle";
                }

                if (color_id == 1) {

                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.red_A700));
                    holder.topLay.setBackgroundColor(context.getResources().getColor(R.color.red_A700));
                }else if (color_id == 2) {

                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.blue_color));
                    holder.topLay.setBackgroundColor(context.getResources().getColor(R.color.blue_color));

                }else if (color_id == 3) {
                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.brown_color));
                    holder.topLay.setBackgroundColor(context.getResources().getColor(R.color.brown_color));

                }else if (color_id == 4) {
                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.green_600));
                    holder.topLay.setBackgroundColor(context.getResources().getColor(R.color.green_600));
                }else if (color_id == 5) {

                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.ucrop_color_blaze_orange));
                    holder.topLay.setBackgroundColor(context.getResources().getColor(R.color.ucrop_color_blaze_orange));

                } else if (color_id == 6) {
                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.app_color));
                    holder.topLay.setBackgroundColor(context.getResources().getColor(R.color.app_color));
                }



                if (businessImage != null && !businessImage.isEmpty()) {

                    GlideDataBinding.bindImage(holder.imageView, businessImage);

                }

                if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE) != null && !preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).isEmpty()) {


                    if (preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE).equals("instagram")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
                    } else if (preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE).equals("facebook")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
                    } else if (preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE).equals("website")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_website_img);
                    } else if (preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE).equals("email")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_email_img);
                    } else if (preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE).equals("twitter")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.share_twitter_img);
                    } else if (preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE).equals("youtube")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
                    }

                }

                holder.nameTv.setText(businessName);
                holder.number_tv.setText(businessNumber);
                holder.ivDesignation.setText(businessDesignation);
                holder.tvSocialMedia.setText(businessMedia);
                holder.ivAddress.setText(businessAddress);

            } else {

                userName = preferenceManager.getString(Constant.USER_NAME);
                userImage = preferenceManager.getString(Constant.USER_IMAGE);
                userNumber = preferenceManager.getString(Constant.USER_PHONE);
                userDesignation = preferenceManager.getString(Constant.USER_DESIGNATION);
                userSocial = preferenceManager.getString(Constant.USER_SOCIAL_MEDIA);

                if (userImage != null && !userImage.isEmpty()) {
                    GlideDataBinding.bindImage(holder.imageView, userImage);
                }

                if (userName.isEmpty()) {
                    userName = "Your Name";
                }
                if (userNumber.isEmpty()) {
                    userNumber = "Your Number";
                }
                if (userDesignation.isEmpty()) {
                    userDesignation = "Your Designation";
                }
                if (userSocial.isEmpty()) {
                    userSocial = "Your media handle";
                }

                if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE) != null && !preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).isEmpty()) {

                    if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).equals("instagram")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
                    } else if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).equals("facebook")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
                    } else if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).equals("website")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_website_img);
                    } else if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).equals("email")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_email_img);
                    } else if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).equals("twitter")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.share_twitter_img);
                    } else if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).equals("youtube")) {
                        holder.imgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
                    }

                }

                holder.nameTv.setText(userName);
                holder.number_tv.setText(userNumber);
                holder.ivDesignation.setText(userDesignation);
                holder.tvSocialMedia.setText(userSocial);
                //   holder.ivAddress.setText(usreAddress);

                if (color_id == 1) {

                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.red_A700));
                }else if (color_id == 2) {

                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.blue_color));

                }else if (color_id == 3) {
                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.brown_color));

                }else if (color_id == 4) {
                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.green_600));
                }else if (color_id == 5) {

                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.ucrop_color_blaze_orange));

                } else if (color_id == 6) {
                    holder.bottomLay.setBackgroundColor(context.getResources().getColor(R.color.app_color));
                }


            }


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }



        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView nameTv, number_tv, ivDesignation;
            TextView ivAddress;
            TextView tvSocialMedia;
            ImageView imageView, imgSocialMedia;
            LinearLayout bottomLay, topLay;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);


                imageView = itemView.findViewById(R.id.logoImg);
                imgSocialMedia = itemView.findViewById(R.id.imgSocialMedia);
                nameTv = itemView.findViewById(R.id.tvTitle);
                number_tv = itemView.findViewById(R.id.tvWhatsapp);
                ivDesignation = itemView.findViewById(R.id.tvDes);
                tvSocialMedia = itemView.findViewById(R.id.tvSocialMedia);
                ivAddress = itemView.findViewById(R.id.tvAddress);
                bottomLay = itemView.findViewById(R.id.bottomLay);
                topLay = itemView.findViewById(R.id.topLay);

            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AdViewBinding binding1;

        public ViewHolder(@NonNull AdViewBinding binding) {
            super(binding.getRoot());
            this.binding1 = binding;

        }
    }

    private void setupColorChangeListeners3( CustomPagerAdapter customPagerAdapter) {
        Map<View, Integer> colorMap = new HashMap<>();
        colorMap.put(binding.red, 1);
        colorMap.put(binding.blue, 2);
        colorMap.put(binding.brown, 3);
        colorMap.put(binding.green, 4);
        colorMap.put(binding.orange, 5);
        colorMap.put(binding.violet, 6);

        for (Map.Entry<View, Integer> entry : colorMap.entrySet()) {
            entry.getKey().setOnClickListener(view -> {
                int newColorId = entry.getValue();
                Log.d("ColorChange", "Color button clicked: " + newColorId);

                customPagerAdapter.setColor(newColorId); // Pass the color to the adapter.
                customPagerAdapter.notifyDataSetChanged();
            });
        }
    }


    private void saveImage(Bitmap bitmap, boolean save) {

        String fileName = System.currentTimeMillis() + ".png";
        String filePath = "";
        if (type.contains("greeting")){

            filePath = Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                    + File.separator + "greeting" + File.separator + fileName;

        }else {
           filePath = Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                    + File.separator + fileName;
        }



        boolean success = false;

        if (!new File(filePath).exists()) {
            try {
                File file = null;
                if (type.contains("greeting")) {

                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + getResources().getString(R.string.app_name) + File.separator + "greeting");
                } else {

                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + getResources().getString(R.string.app_name));
                }

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


                if (save){
                    Util.showToast(context, getString(R.string.image_saved));
                    Intent intent = new Intent(context, ShareImageActivity.class);
                    intent.putExtra("uri", filePath);
                    startActivity(intent);
                }else {
                    shareFileImageUri(getImageContentUri(new File(filePath)), "type");
                }


                preferenceManager.setBoolean("save_image", true);

            } else {
                Util.showToast(context, getString(R.string.error));
            }

        }

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


}