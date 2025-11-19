package com.motivation.quotes.app.ui.adapters;

import static com.motivation.quotes.app.utils.Constant.ADS_ENABLE;
import static com.motivation.quotes.app.utils.Constant.IS_SUBSCRIBE;
import static com.motivation.quotes.app.utils.Constant.NATIVE_AD_ENABLED;
import static com.motivation.quotes.app.utils.Constant.NATIVE_AD_ID;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.ump.ConsentInformation;
import com.motivation.quotes.app.AdsUtils.GDPRChecker;
import com.motivation.quotes.app.CustomVideoView;
import com.growwthapps.dailypost.v2.R;
import com.motivation.quotes.app.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.AdViewBinding;
import com.growwthapps.dailypost.v2.databinding.ItemMainLayout2Binding;
import com.growwthapps.dailypost.v2.databinding.ItemMainLayout3Binding;
import com.growwthapps.dailypost.v2.databinding.ItemMainLayoutBinding;
import com.motivation.quotes.app.model.PostItem;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.PreferenceManager;
import com.motivation.quotes.app.utils.SnapHelperOneByOne;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DailyPostAdapter extends RecyclerView.Adapter<DailyPostAdapter.ViewHolder> {

    Activity context;
    List<PostItem> list;
    private PreferenceManager preferenceManager;
    private String userName;
    private String userImage;
    private String userNumber;
    private String userDesignation, userAddress, userSocial;
    private String businessName, businessMedia, businessMail, businessAddress, businessDesignation, businessNumber, businessImage;

    String video = "";
    private CustomVideoView currentlyPlayingVideo;


    private boolean typeBusiness = false;

    public void setData(List<PostItem> daily_post) {


        int size = list.size();

        this.list = daily_post;


        notifyItemRangeInserted(size, daily_post.size());

    }

    public interface OnClickEvent {
        void onClick(View view, View posterview, View posterview2, int size, PostItem postItem);
    }

    OnClickEvent onClickEvent;


    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int VIEW_TYPE_AD = 0;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_AD) {
            AdViewBinding adBinding = AdViewBinding.inflate(inflater, parent, false);
            return new ViewHolder(adBinding);
        } else {

            if (preferenceManager.getString("DataType").equals("Business")) {


                int randomChoice = new Random().nextInt(2); // Randomly returns 0 or 1

                if (randomChoice == 0) {
                    ItemMainLayout2Binding itemBinding2 = ItemMainLayout2Binding.inflate(inflater, parent, false);

                    typeBusiness = true;

                    return new ViewHolder(itemBinding2);
                } else {
                    ItemMainLayout3Binding itemBinding3 = ItemMainLayout3Binding.inflate(inflater, parent, false);
                    typeBusiness = false;
                    return new ViewHolder(itemBinding3);
                }


            } else {
                ItemMainLayoutBinding itemBinding = ItemMainLayoutBinding.inflate(inflater, parent, false);
                return new ViewHolder(itemBinding);
            }


        }
    }

    public DailyPostAdapter(Activity context, List<PostItem> list, OnClickEvent onClickEvent) {
        this.context = context;
        this.list = list;
        this.onClickEvent = onClickEvent;
        this.preferenceManager = new PreferenceManager(context);
    }

    public void clearData() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.binding2 != null && holder.binding2.video.isPlaying()) {
            holder.binding2.video.pause();
        }
        if (holder.binding3 != null && holder.binding3.video.isPlaying()) {
            holder.binding3.video.pause();
        }
        if (holder.binding != null && holder.binding.video.isPlaying()) {
            holder.binding.video.pause();
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder2, int position) {
        int itemViewType = holder2.getItemViewType();
        final ViewHolder holder = (ViewHolder) holder2;
        holder.setIsRecyclable(false);

        if (itemViewType == VIEW_TYPE_AD) {
            Log.d("IS_SUBSCRIBE", "onBindViewHolder: 11 ");
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {
                Log.d("IS_SUBSCRIBE", "onBindViewHolder: " + IS_SUBSCRIBE);

                if (preferenceManager.getBoolean(ADS_ENABLE) && preferenceManager.getBoolean(NATIVE_AD_ENABLED)) {

                    Log.d("IS_SUBSCRIBE", "ADS_ENABLE: " + preferenceManager.getBoolean(ADS_ENABLE) + " .. " + preferenceManager.getBoolean(NATIVE_AD_ENABLED));

                    AdLoader.Builder builder = new AdLoader.Builder(context, preferenceManager.getString(NATIVE_AD_ID))
                            .forNativeAd(nativeAd1 -> {


                                holder.binding1.shimmer.stopShimmer();
                                holder.binding1.shimmer.setVisibility(View.GONE);


                                RelativeLayout adView = (RelativeLayout) context.getLayoutInflater()
                                        .inflate(R.layout.ad_creation, null);


                                populateUnifiedNativeAdView(nativeAd1, adView.findViewById(R.id.unified));
                                holder.binding1.flAdplaceholder.removeAllViews();
                                holder.binding1.flAdplaceholder.addView(adView);

                            });


                    AdRequest.Builder m_builder = new AdRequest.Builder();

                    int request = GDPRChecker.getStatus();
                    if (request == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                        // load non Personalized ads
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        m_builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                    }

                    builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(m_builder.build());


                }

            }

        } else {

            try {

                video = list.get(position).video_path;
                if (preferenceManager.getString("DataType").equals("Business")) {

                    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(list.get(position).image_url);

                    final int random = new Random().nextInt((3 - 1) + 1) + 1;
                    SnapHelperOneByOne snapHelperOneByOne = new SnapHelperOneByOne();

                    Log.d("Business", "onBindViewHolder: " + list.size());

                    if (typeBusiness) {

                        if (list.get(position).is_premium) {

                            holder.binding2.ivPremium.setVisibility(View.VISIBLE);
                        }

                        if (list.get(position).is_video) {
                            holder.binding2.video.setVisibility(View.VISIBLE);
                            holder.binding2.progressBar.setVisibility(View.VISIBLE);
                            holder.binding2.videoThumb.setVisibility(View.VISIBLE);
                            holder.binding2.video.setVideoPath(video);
                            holder.binding2.video.setOnPreparedListener(mediaPlayer -> {
                                holder.binding2.progressBar.setVisibility(View.GONE);
                                holder.binding2.imgPlay.setVisibility(View.VISIBLE);
                                holder.binding2.ivPlay.setVisibility(View.VISIBLE);
                                holder.binding2.videoThumb.setVisibility(View.VISIBLE);
                                if (currentlyPlayingVideo != null && currentlyPlayingVideo != holder.binding2.video) {
                                    currentlyPlayingVideo.pause();
                                }

                                currentlyPlayingVideo = holder.binding2.video;
                                //mediaPlayer.setLooping(true); // If you want loop
                              //  mediaPlayer.start();
                                holder.binding2.ivPlay.setVisibility(View.VISIBLE);
                            });

                            holder.binding2.imgPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (holder.binding2.video.isPlaying()) {

                                        holder.binding2.video.pause();
                                        holder.binding2.ivPlay.setVisibility(View.VISIBLE);
                                        holder.binding2.videoThumb.setVisibility(View.GONE);

                                    }else {
                                        holder.binding2.video.start();
                                        holder.binding2.ivPlay.setVisibility(View.GONE);
                                        holder.binding2.videoThumb.setVisibility(View.GONE);

                                    }
                                }
                            });

                            holder.binding2.ivPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Log.d("Business1111", "onBindViewHolder:yyyyy ");

                                    if (holder.binding2.video.isPlaying()) {

                                        holder.binding2.video.pause();
                                        holder.binding2.ivPlay.setVisibility(View.VISIBLE);
                                        holder.binding2.videoThumb.setVisibility(View.GONE);


                                    }else {
                                        holder.binding2.video.start();
                                        holder.binding2.ivPlay.setVisibility(View.GONE);
                                        holder.binding2.videoThumb.setVisibility(View.GONE);

                                    }

                                }
                            });

                            GlideDataBinding.bindImage(holder.binding2.videoThumb, list.get(position).thumbnail);

                        } else {
                            GlideDataBinding.bindImage(holder.binding2.imagePost, list.get(position).image_url);
                            holder.binding2.imgPlay.setVisibility(View.VISIBLE);
                            holder.binding2.ivPlay.setVisibility(View.VISIBLE);
                        }

                        holder.binding2.recyclerview.setLayoutManager(linearLayoutManager);
                        holder.binding2.recyclerview.setAdapter(customPagerAdapter);
                        holder.binding2.indicator2.attachToRecyclerView(holder.binding2.recyclerview);

                        snapHelperOneByOne.attachToRecyclerView(holder.binding2.recyclerview);
                        holder.binding2.recyclerview.scrollToPosition(random);

                        if (preferenceManager.getBoolean(Constant.IS_SUBSCRIBE)) {
                            holder.binding2.watermarkLayout.setVisibility(View.GONE);
                        }
                        holder.binding2.watermarkLayout.setOnClickListener(view -> {

                            onClickEvent.onClick(view, holder.binding2.mainLayOut, holder.binding2.mainLayOut2, getTopMarginPx(holder.binding2.constraintLayout2), list.get(position));
                        });

                        holder.binding2.downloadBtn.setOnClickListener(view -> {

                            onClickEvent.onClick(view, holder.binding2.mainLayOut, holder.binding2.mainLayOut2, getTopMarginPx(holder.binding2.constraintLayout2), list.get(position));
                        });
                        holder.binding2.shareBtn.setOnClickListener(view -> {
                            onClickEvent.onClick(view, holder.binding2.mainLayOut, holder.binding2.mainLayOut2, getTopMarginPx(holder.binding2.constraintLayout2), list.get(position));
                        });
                        for (LinearLayout linearLayout : Arrays.asList(holder.binding2.shareBtn, holder.binding2.editBtn)) {
                            linearLayout.setOnClickListener(view -> onClickEvent.onClick(view, holder.binding2.mainLayOut, holder.binding2.mainLayOut2, getTopMarginPx(holder.binding2.constraintLayout2), list.get(position)));
                        }
                        holder.binding2.nextBtn.setOnClickListener(view -> next(holder, position, view));

                        setupColorChangeListeners2(holder, customPagerAdapter);

                    } else {



                        if (list.get(position).is_premium) {

                            holder.binding3.ivPremium.setVisibility(View.VISIBLE);
                        }

                        if (list.get(position).is_video) {

                            holder.binding3.video.setVisibility(View.VISIBLE);
                            holder.binding3.progressBar.setVisibility(View.VISIBLE);
                            holder.binding3.videoThumb.setVisibility(View.VISIBLE);
                            holder.binding3.video.setVideoPath(video);
                            holder.binding3.video.setOnPreparedListener(mediaPlayer -> {
                                holder.binding3.progressBar.setVisibility(View.GONE);
                                holder.binding3.videoThumb.setVisibility(View.VISIBLE);
                                holder.binding3.imgPlay.setVisibility(View.VISIBLE);
                                holder.binding3.ivPlay.setVisibility(View.VISIBLE);

                                if (currentlyPlayingVideo != null && currentlyPlayingVideo != holder.binding3.video) {
                                    currentlyPlayingVideo.pause();
                                }

                                currentlyPlayingVideo = holder.binding3.video;

                                holder.binding3.ivPlay.setVisibility(View.VISIBLE);
                            });

                            holder.binding3.imgPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (holder.binding3.video.isPlaying()) {

                                        holder.binding3.video.pause();
                                        holder.binding3.ivPlay.setVisibility(View.VISIBLE);
                                        holder.binding3.videoThumb.setVisibility(View.GONE);


                                    }else {
                                        holder.binding3.video.start();
                                        holder.binding3.ivPlay.setVisibility(View.GONE);
                                        holder.binding3.videoThumb.setVisibility(View.GONE);

                                    }
                                }
                            });

                            holder.binding3.ivPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Log.d("Business1111", "onBindViewHolder:bbbb ");
                                    if (holder.binding3.video.isPlaying()) {

                                        holder.binding3.video.pause();
                                        holder.binding3.ivPlay.setVisibility(View.VISIBLE);
                                        holder.binding3.videoThumb.setVisibility(View.GONE);

                                    }else {
                                        holder.binding3.video.start();
                                        holder.binding3.ivPlay.setVisibility(View.GONE);
                                        holder.binding3.videoThumb.setVisibility(View.GONE);
                                    }

                                }
                            });

                            GlideDataBinding.bindImage(holder.binding3.videoThumb, list.get(position).thumbnail);

                        } else {
                            GlideDataBinding.bindImage(holder.binding3.imagePost, list.get(position).image_url);
                            holder.binding3.imgPlay.setVisibility(View.GONE);
                            holder.binding3.ivPlay.setVisibility(View.GONE);
                        }

                        holder.binding3.recyclerview.setLayoutManager(linearLayoutManager);
                        holder.binding3.recyclerview.setAdapter(customPagerAdapter);

                        holder.binding3.indicator.attachToRecyclerView(holder.binding3.recyclerview);

                        snapHelperOneByOne.attachToRecyclerView(holder.binding3.recyclerview);
                        holder.binding3.recyclerview.scrollToPosition(random);

                        if (preferenceManager.getBoolean(Constant.IS_SUBSCRIBE)) {
                            holder.binding3.watermarkLayout.setVisibility(View.GONE);
                        }

                        holder.binding3.watermarkLayout.setOnClickListener(view -> onClickEvent.onClick(view, holder.binding3.mainLayOut, holder.binding3.mainLayOut2, getTopMarginPx(holder.binding3.constraintLayout2), list.get(position)));
                        holder.binding3.downloadBtn.setOnClickListener(view -> {
                            holder.binding3.ivWatermark.setImageResource(R.drawable.watermark);

                            onClickEvent.onClick(view, holder.binding3.mainLayOut, holder.binding3.mainLayOut2,  getTopMarginPx(holder.binding3.constraintLayout2), list.get(position));
                        });
                        holder.binding3.shareBtn.setOnClickListener(view -> {

                            holder.binding3.ivWatermark.setImageResource(R.drawable.watermark);
                            onClickEvent.onClick(view, holder.binding3.mainLayOut, holder.binding3.mainLayOut2, getTopMarginPx(holder.binding3.constraintLayout2), list.get(position));
                        });
                        for (LinearLayout linearLayout : Arrays.asList(holder.binding3.shareBtn, holder.binding3.editBtn)) {
                            linearLayout.setOnClickListener(view -> onClickEvent.onClick(view, holder.binding3.mainLayOut, holder.binding3.mainLayOut2, getTopMarginPx(holder.binding3.constraintLayout2), list.get(position)));
                        }
                        holder.binding3.nextBtn.setOnClickListener(view -> next(holder, position, view));

                        setupColorChangeListeners3(holder, customPagerAdapter);

                    }

                } else {


                    if (list.get(position).is_premium) {

                        holder.binding.ivPremium.setVisibility(View.VISIBLE);
                    }

                    if (list.get(position).video_path!=null && !list.get(position).video_path.isEmpty()) {

                        holder.binding.video.setVisibility(View.VISIBLE);
                        holder.binding.progressBar.setVisibility(View.VISIBLE);
                        holder.binding.videoThumb.setVisibility(View.VISIBLE);
                        holder.binding.video.setVideoPath(video);

                        holder.binding.video.setOnPreparedListener(mediaPlayer -> {
                            holder.binding.progressBar.setVisibility(View.GONE);
                            holder.binding.videoThumb.setVisibility(View.VISIBLE);
                            holder.binding.imgPlay.setVisibility(View.VISIBLE);
                            holder.binding.ivPlay.setVisibility(View.VISIBLE);
                            if (currentlyPlayingVideo != null && currentlyPlayingVideo != holder.binding.video) {
                                currentlyPlayingVideo.pause();
                            }
                            currentlyPlayingVideo = holder.binding.video;

                            holder.binding.ivPlay.setVisibility(View.VISIBLE);
                        });

                        holder.binding.imgPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (holder.binding.video.isPlaying()) {

                                    holder.binding.video.pause();
                                    holder.binding.ivPlay.setVisibility(View.VISIBLE);
                                    holder.binding.videoThumb.setVisibility(View.GONE);


                                }else {
                                    holder.binding.video.start();
                                    holder.binding.ivPlay.setVisibility(View.GONE);
                                    holder.binding.videoThumb.setVisibility(View.GONE);

                                }
                            }
                        });

                        holder.binding.ivPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (holder.binding.video.isPlaying()) {

                                    holder.binding.video.pause();
                                    holder.binding.ivPlay.setVisibility(View.VISIBLE);
                                    holder.binding.videoThumb.setVisibility(View.GONE);


                                }else {
                                    holder.binding.video.start();
                                    holder.binding.ivPlay.setVisibility(View.GONE);
                                    holder.binding.videoThumb.setVisibility(View.GONE);

                                }

                            }
                        });

                        GlideDataBinding.bindImage(holder.binding.videoThumb, list.get(position).video_path);
                    } else {
                        GlideDataBinding.bindImage(holder.binding.imagePost, list.get(position).video_path);
                        holder.binding.imgPlay.setVisibility(View.GONE);
                        holder.binding.ivPlay.setVisibility(View.GONE);
                    }

                    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(list.get(position).image_url);
                    holder.binding.recyclerview.setLayoutManager(linearLayoutManager);
                    holder.binding.recyclerview.setAdapter(customPagerAdapter);

                    holder.binding.indicator.attachToRecyclerView(holder.binding.recyclerview);

                    final int random = new Random().nextInt((3 - 1) + 1) + 1;
                    SnapHelperOneByOne snapHelperOneByOne = new SnapHelperOneByOne();
                    snapHelperOneByOne.attachToRecyclerView(holder.binding.recyclerview);
                    holder.binding.recyclerview.scrollToPosition(random);

                    if (preferenceManager.getBoolean(Constant.IS_SUBSCRIBE)) {
                        holder.binding.watermarkLayout.setVisibility(View.GONE);
                    }

                    holder.binding.watermarkLayout.setOnClickListener(view -> onClickEvent.onClick(view, holder.binding.mainLayOut, holder.binding.mainLayOut2, getTopMarginPx(holder.binding.constraintLayout2), list.get(position)));

                    holder.binding.downloadBtn.setOnClickListener(view -> {
                        holder.binding.ivWatermark.setImageResource(R.drawable.watermark);

                        onClickEvent.onClick(view, holder.binding.mainLayOut, holder.binding.mainLayOut2, getTopMarginPx(holder.binding.constraintLayout2), list.get(position));
                    });

                    holder.binding.shareBtn.setOnClickListener(view -> {

                        holder.binding.ivWatermark.setImageResource(R.drawable.watermark);
                        onClickEvent.onClick(view, holder.binding.mainLayOut, holder.binding.mainLayOut2, getTopMarginPx(holder.binding.constraintLayout2), list.get(position));
                    });
                    for (LinearLayout linearLayout : Arrays.asList(holder.binding.shareBtn, holder.binding.editBtn)) {
                        linearLayout.setOnClickListener(view -> onClickEvent.onClick(view, holder.binding.mainLayOut, holder.binding.mainLayOut2, getTopMarginPx(holder.binding.constraintLayout2), list.get(position)));
                    }

                    holder.binding.nextBtn.setOnClickListener(view -> next(holder, position, view));

                    setupColorChangeListeners(holder, customPagerAdapter);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public int getTopMarginPx(View view) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            return params.topMargin; // pixels
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) != null ? VIEW_TYPE_CONTENT : VIEW_TYPE_AD;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemMainLayoutBinding binding;
        ItemMainLayout2Binding binding2;
        ItemMainLayout3Binding binding3;
        AdViewBinding binding1;

        public ViewHolder(@NonNull ItemMainLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public ViewHolder(@NonNull ItemMainLayout2Binding itemView2) {
            super(itemView2.getRoot());
            binding2 = itemView2;
        }

        public ViewHolder(@NonNull ItemMainLayout3Binding itemView3) {
            super(itemView3.getRoot());
            binding3 = itemView3;
        }

        public ViewHolder(@NonNull AdViewBinding binding) {
            super(binding.getRoot());
            this.binding1 = binding;

        }
    }


    private void setupColorChangeListeners3(ViewHolder holder, CustomPagerAdapter customPagerAdapter) {
        Map<View, Integer> colorMap = new HashMap<>();
        colorMap.put(holder.binding3.red, 1);
        colorMap.put(holder.binding3.blue, 2);
        colorMap.put(holder.binding3.brown, 3);
        colorMap.put(holder.binding3.green, 4);
        colorMap.put(holder.binding3.orange, 5);
        colorMap.put(holder.binding3.violet, 6);

        for (Map.Entry<View, Integer> entry : colorMap.entrySet()) {
            entry.getKey().setOnClickListener(view -> {
                int newColorId = entry.getValue();
                Log.d("ColorChange", "Color button clicked: " + newColorId);
                customPagerAdapter.setColor(newColorId); // Pass the color to the adapter.
                customPagerAdapter.notifyDataSetChanged();
            });
        }
    }

    private void setupColorChangeListeners2(ViewHolder holder, CustomPagerAdapter customPagerAdapter) {
        Map<View, Integer> colorMap = new HashMap<>();
        colorMap.put(holder.binding2.red, 1);
        colorMap.put(holder.binding2.blue, 2);
        colorMap.put(holder.binding2.brown, 3);
        colorMap.put(holder.binding2.green, 4);
        colorMap.put(holder.binding2.orange, 5);
        colorMap.put(holder.binding2.violet, 6);

        for (Map.Entry<View, Integer> entry : colorMap.entrySet()) {
            entry.getKey().setOnClickListener(view -> {
                int newColorId = entry.getValue();
                Log.d("ColorChange", "Color button clicked: " + newColorId);

                customPagerAdapter.setColor(newColorId); // Pass the color to the adapter.
                customPagerAdapter.notifyDataSetChanged();
            });
        }
    }

    private void setupColorChangeListeners(ViewHolder holder, CustomPagerAdapter customPagerAdapter) {
        Map<View, Integer> colorMap = new HashMap<>();
        colorMap.put(holder.binding.red, 1);
        colorMap.put(holder.binding.blue, 2);
        colorMap.put(holder.binding.brown, 3);
        colorMap.put(holder.binding.green, 4);
        colorMap.put(holder.binding.orange, 5);
        colorMap.put(holder.binding.violet, 6);

        for (Map.Entry<View, Integer> entry : colorMap.entrySet()) {
            entry.getKey().setOnClickListener(view -> {
                int newColorId = entry.getValue();
                Log.d("ColorChange", "Color button clicked: " + newColorId);

                customPagerAdapter.setColor(newColorId); // Pass the color to the adapter.
                customPagerAdapter.notifyDataSetChanged();
            });
        }

    }

    public class CustomPagerAdapter extends RecyclerView.Adapter<CustomPagerAdapter.ViewHolder> {

        String item_url;
        String frameColor = "";
        List<Integer> list = new ArrayList<>();

        private int color_id = 0;

        public void setColor(int colorId) {
            this.color_id = colorId;
        }

        public CustomPagerAdapter(String str) {
            this.item_url = str;

            if (preferenceManager.getString("DataType").equals("Business")) {

                if (typeBusiness) {
                    list.add(R.layout.layout_frame_business_1_1);
                    list.add(R.layout.layout_frame_business_1_2);
                    list.add(R.layout.layout_frame_business_1_3);
                    list.add(R.layout.layout_frame_business_1_4);

                } else {

                    list.add(R.layout.layout_frame_business_2_1);
                    list.add(R.layout.layout_frame_business_2_2);

                }

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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(list.get(viewType), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

             /*   if (frameColor != null && !frameColor.isEmpty()) {
                    holder.bottomLay.setBackgroundColor(Color.parseColor(frameColor));
                    holder.topLay.setBackgroundColor(Color.parseColor(frameColor));
                }*/

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

              /*  if (frameColor != null && !frameColor.isEmpty()) {
                    holder.bottomLay.setBackgroundColor(Color.parseColor(frameColor));
                    holder.topLay.setBackgroundColor(Color.parseColor(frameColor));
                }*/

                holder.nameTv.setText(userName);
                holder.number_tv.setText(userNumber);
                holder.ivDesignation.setText(userDesignation);
                holder.tvSocialMedia.setText(userSocial);

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
                bottomLay = itemView.findViewById(R.id.bottomLay);
                topLay = itemView.findViewById(R.id.topLay);
                imgSocialMedia = itemView.findViewById(R.id.imgSocialMedia);
                nameTv = itemView.findViewById(R.id.tvTitle);
                number_tv = itemView.findViewById(R.id.tvWhatsapp);
                ivDesignation = itemView.findViewById(R.id.tvDes);
                tvSocialMedia = itemView.findViewById(R.id.tvSocialMedia);
                ivAddress = itemView.findViewById(R.id.tvAddress);

            }
        }
    }

    public void next(ViewHolder viewHolder, int i, View view) {
        onClickEvent.onClick(view, viewHolder.binding.mainLayOut, viewHolder.binding.mainLayOut2, getTopMarginPx(viewHolder.binding2.constraintLayout2), list.get(i));
    }


    public void populateUnifiedNativeAdView(NativeAd ad, NativeAdView adView) {
        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(ad.getHeadline());
        adView.setHeadlineView(headlineView);

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        mediaView.setMediaContent(ad.getMediaContent());

        TextView install = adView.findViewById(R.id.ad_call_to_action);
        install.setText(ad.getCallToAction());

        adView.setCallToActionView(install);

        adView.setMediaView(mediaView);

        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(ad.getHeadline());
        adView.getMediaView().setMediaContent(ad.getMediaContent());

        if (ad.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(ad.getCallToAction());
        }

        if (ad.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    ad.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (ad.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(ad.getPrice());
        }

        if (ad.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(ad.getStore());
        }

        if (ad.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(ad.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (ad.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(ad.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(ad);


    }

}
