package com.motivation.quotes.app.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.motivation.quotes.app.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.ItemPosterAndGreetingsBinding;
import com.motivation.quotes.app.listener.ClickListener;
import com.motivation.quotes.app.model.PostItem;

import java.util.List;

public class FestivalPostAdapter extends RecyclerView.Adapter<FestivalPostAdapter.MyViewHolder> {

    public Activity context;
    public ClickListener<PostItem> listener;
    public List<PostItem> postItems;
    Boolean post;

    public FestivalPostAdapter(Activity context, List<PostItem> postItems, ClickListener<PostItem> listener, boolean post) {
        this.context = context;
        this.listener = listener;
        this.postItems = postItems;
        this.post = post;

    }

    public void setFestivalPost(List<PostItem> categories) {
        this.postItems = categories;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPosterAndGreetingsBinding binding = ItemPosterAndGreetingsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (postItems != null) {

            holder.binding.setCategoryData(postItems.get(position));
            GlideDataBinding.bindImage(holder.binding.categoryItem, postItems.get(position).image_url);

        }
        if (post){
            holder.binding.stroke.setVisibility(View.VISIBLE);
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
        ItemPosterAndGreetingsBinding binding;

        public MyViewHolder(@NonNull ItemPosterAndGreetingsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
