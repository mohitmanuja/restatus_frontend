package com.growwthapps.dailypost.v2.ui.adapters;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.growwthapps.dailypost.v2.databinding.ActivityAllGreetingsBinding;
import com.growwthapps.dailypost.v2.databinding.GreetingPostGroupBinding;
import com.growwthapps.dailypost.v2.listener.ClickListener;
import com.growwthapps.dailypost.v2.model.GreetingData;
import com.growwthapps.dailypost.v2.model.PostItem;
import com.growwthapps.dailypost.v2.ui.activities.ActivityAllGreetings;
import com.growwthapps.dailypost.v2.ui.activities.CreatePostActivity;
import com.growwthapps.dailypost.v2.ui.activities.EditorActivity;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;


public class GreetingAdapter extends RecyclerView.Adapter<GreetingAdapter.MyViewHolder> {

    public List<GreetingData> featureItemList;
    static Activity context;

    public GreetingAdapter(Activity context) {
        this.context = context;

    }


    public void setFeatureItemList(List<GreetingData> featureItemList) {
        this.featureItemList = featureItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GreetingPostGroupBinding binding = GreetingPostGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {


        // MyUtils.setScaleAnimation(holder.itemView);
        setFadeAnimation(holder.itemView);

        holder.binding.viewAllBtn.setOnClickListener(v -> {
                goToPreviewActivityViewMore(position);
        });

        Log.d("featureItemList", "onBindViewHolder: "+new Gson().toJson(featureItemList));
        if (featureItemList.get(1).post != null) {
            holder.adapters = new GreetingPostAdapter(context,new ClickListener<PostItem>() {
                @Override
                public void onClick(PostItem data) {
                    Intent intent = new Intent(context, EditorActivity.class);
                    intent.putExtra("imageUri", data.image_url);
                    intent.putExtra("greeting", "greeting");
                    context.startActivity(intent);
                }
            });
            holder.adapters.setFestivalPost(featureItemList.get(position).post);
            holder.binding.recylerview.setAdapter(holder.adapters);

        }


        holder.binding.recylerview.setAdapter(holder.adapters);
        holder.binding.titleTv.setText(featureItemList.get(position).name);
        holder.adapters.setFestivalPost(featureItemList.get(position).post);

        Log.d("featureItemList", "onBindViewHolder: " + featureItemList.get(position).post.size());


    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }


    private void goToPreviewActivityViewMore(int position) {

        context.startActivity(new Intent(new Intent(context, ActivityAllGreetings.class)
                .putExtra("name", featureItemList.get(position).name)
                .putExtra("cat_id", featureItemList.get(position).id)));
    }

    @Override
    public int getItemCount() {
        if (featureItemList != null && featureItemList.size() > 0) {
            return featureItemList.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        GreetingPostGroupBinding binding;
        GreetingPostAdapter adapters;

        public MyViewHolder(@NonNull GreetingPostGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
