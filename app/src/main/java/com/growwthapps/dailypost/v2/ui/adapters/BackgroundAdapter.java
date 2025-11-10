package com.growwthapps.dailypost.v2.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.ItemPosterAndGreetingsBinding;
import com.growwthapps.dailypost.v2.listener.ClickListener;
import com.growwthapps.dailypost.v2.model.CategoryItem;
import com.growwthapps.dailypost.v2.model.PostItem;

import java.util.List;

public class BackgroundAdapter extends RecyclerView.Adapter<BackgroundAdapter.MyViewHolder> {

    public Activity context;
    public ClickListener<CategoryItem> listener;
    public List<CategoryItem> postItems;

    public BackgroundAdapter(Activity context, List<CategoryItem> postItems, ClickListener<CategoryItem> listener) {
        this.context = context;
        this.listener = listener;
        this.postItems = postItems;
    }

    public void setFestivalPost(List<CategoryItem> categories) {
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


            if (postItems.size()> 5) {

                if (position == 5) {
                    holder.binding.seeAll.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.seeAll.setVisibility(View.GONE);
                }

            } else {
                holder.binding.seeAll.setVisibility(View.GONE);
            }

            if (position==0){
                holder.binding.categoryItem.setImageResource(R.drawable.add_your_img);

            }else {
              GlideDataBinding.bindImage(holder.binding.categoryItem, postItems.get(position).image);
            }



        }

        holder.itemView.setOnClickListener(v -> {

            listener.onClick(postItems.get(position));

        });

    }

    @Override
    public int getItemCount() {

        int size = 0;
        if (postItems != null && postItems.size() > 0 ) {

            if (postItems.size() >6){
                size = 6;
            }else {
                size = postItems.size();

            }

                return size;
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
