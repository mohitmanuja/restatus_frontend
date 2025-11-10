package com.growwthapps.dailypost.v2.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.ItemPostBinding;
import com.growwthapps.dailypost.v2.databinding.ItemPosterAndGreetingsBinding;
import com.growwthapps.dailypost.v2.listener.ClickListener;
import com.growwthapps.dailypost.v2.model.PostItem;

import java.util.List;

public class GreetingPostAdapter extends RecyclerView.Adapter<GreetingPostAdapter.MyViewHolder> {

    public Activity context;
    public ClickListener<PostItem> listener;
    public List<PostItem> postItems;

    public GreetingPostAdapter(Activity context, ClickListener<PostItem> listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setFestivalPost(List<PostItem> categories) {
        this.postItems = categories;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (postItems != null) {

            GlideDataBinding.bindImage(holder.binding.ivPost, postItems.get(position).image_url);

        }

        holder.itemView.setOnClickListener(v -> {

            listener.onClick(postItems.get(position));

        });

    }

    @Override
    public int getItemCount() {
        if (postItems != null && postItems.size() > 0) {

                return postItems.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemPostBinding binding;

        public MyViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
