package com.motivation.quotes.app.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.databinding.ItemCategoryBinding;
import com.motivation.quotes.app.listener.AdapterClickListener;

import java.util.List;

public class CategorysAdapter extends RecyclerView.Adapter<CategorysAdapter.MyViewHolder> {

    public Context context;
    private AdapterClickListener listener;

    private List<String> titles;

    int selectedPosition = 0;

    public CategorysAdapter(Context context,  List<String> list, AdapterClickListener listener) {
        this.context = context;
        this.titles = list;
        this.listener = listener;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (this.selectedPosition == position) {
            holder.binding.llCategory.setActivated(true);
            holder.binding.catName.setTextColor(Color.WHITE);
        } else {
            holder.binding.llCategory.setActivated(false);
            holder.binding.catName.setTextColor(Color.BLACK);
        }

       holder.binding.catName.setText("" + titles.get(position));
        holder.binding.getRoot().setOnClickListener(view -> {
            CategorysAdapter recyclerOverLayAdapter = CategorysAdapter.this;
            recyclerOverLayAdapter.notifyItemChanged(recyclerOverLayAdapter.selectedPosition);
            CategorysAdapter recyclerOverLayAdapter2 = CategorysAdapter.this;
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
        ItemCategoryBinding binding;

        public MyViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
