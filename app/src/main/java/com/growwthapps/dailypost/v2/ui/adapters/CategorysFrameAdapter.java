package com.growwthapps.dailypost.v2.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ItemCategoryBinding;
import com.growwthapps.dailypost.v2.databinding.ItemFrameBinding;
import com.growwthapps.dailypost.v2.databinding.ItemFrameCategoriesBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;

import java.util.List;

public class CategorysFrameAdapter extends RecyclerView.Adapter<CategorysFrameAdapter.MyViewHolder> {

    public Context context;
    private AdapterClickListener listener;

    private List<String> titles;

    int selectedPosition = 0;

    public CategorysFrameAdapter(Context context, List<String> list, AdapterClickListener listener) {
        this.context = context;
        this.titles = list;
        this.listener = listener;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFrameCategoriesBinding binding = ItemFrameCategoriesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (this.selectedPosition == position) {
            holder.binding.llCategory.setActivated(true);
            holder.binding.catName.setTextColor(Color.WHITE);
        } else {
            holder.binding.llCategory.setActivated(false);
            holder.binding.catName.setTextColor(ContextCompat.getColor(context, R.color.black1));
        }

       holder.binding.catName.setText("" + titles.get(position));
        holder.binding.getRoot().setOnClickListener(view -> {
            CategorysFrameAdapter recyclerOverLayAdapter = CategorysFrameAdapter.this;
            recyclerOverLayAdapter.notifyItemChanged(recyclerOverLayAdapter.selectedPosition);
            CategorysFrameAdapter recyclerOverLayAdapter2 = CategorysFrameAdapter.this;
            recyclerOverLayAdapter2.selectedPosition = position;
            recyclerOverLayAdapter2.notifyItemChanged(recyclerOverLayAdapter2.selectedPosition);
            listener.onItemClick(view, position, null);
        });




    }
    @Override
    public int getItemCount() {
        return titles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemFrameCategoriesBinding binding;

        public MyViewHolder(@NonNull ItemFrameCategoriesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
