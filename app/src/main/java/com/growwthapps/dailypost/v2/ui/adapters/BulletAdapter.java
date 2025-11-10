package com.growwthapps.dailypost.v2.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.model.BulletItem;

import java.util.List;

public class BulletAdapter extends RecyclerView.Adapter<BulletAdapter.BulletViewHolder> {

    private final List<BulletItem> bulletList;
    private final Context context;

    public BulletAdapter(Context context, List<BulletItem> bulletList) {
        this.context = context;
        this.bulletList = bulletList;
    }

    @NonNull
    @Override
    public BulletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bullet_point, parent, false);
        return new BulletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BulletViewHolder holder, int position) {
        BulletItem item = bulletList.get(position);
        holder.textView.setText(item.getText());
    }

    @Override
    public int getItemCount() {
        return bulletList.size();
    }

    static class BulletViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public BulletViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
