package com.motivation.quotes.app.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ActivityFillFrameDetailsBinding;
import com.growwthapps.dailypost.v2.databinding.DialogOrderCustomFrameBinding;
import com.motivation.quotes.app.model.BulletItem;
import com.motivation.quotes.app.ui.adapters.BulletAdapter;
import com.motivation.quotes.app.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class FillFrameDetails extends AppCompatActivity {

    private ActivityFillFrameDetailsBinding binding;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFillFrameDetailsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        MyUtils.topIconBar(this);

        context = this;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainFillFrame), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        binding.backImg.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.swipeRefresh.setRefreshing(false);

       /* binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
        });*/


        binding.btnSubmit.setOnClickListener(view -> {
            showDialog();
        });


    }

    private void showDialog() {
        RoundedBottomSheetDialog dialog = new RoundedBottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogOrderCustomFrameBinding bindingOrderDialog = DialogOrderCustomFrameBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingOrderDialog.getRoot());
        dialog.setCancelable(false);

        // Close button click
        bindingOrderDialog.tvClose.setOnClickListener(view -> dialog.dismiss());

        // Submit button click
        bindingOrderDialog.btnSubmit.setOnClickListener(view -> {
            context.startActivity(new Intent(context, MyFramesActivity.class));
            dialog.dismiss();
        });

        // Prepare bullet list
        List<BulletItem> bullets = new ArrayList<>();
        bullets.add(new BulletItem("Our designers manually design your frames"));
        bullets.add(new BulletItem("The frame price is ₹49 only"));
        bullets.add(new BulletItem("Your frame will be delivered within 24 hours"));

        // Set up RecyclerView
        BulletAdapter adapter = new BulletAdapter(context, bullets);
        bindingOrderDialog.recyclerBullet.setLayoutManager(new LinearLayoutManager(context));
        bindingOrderDialog.recyclerBullet.setAdapter(adapter);

        dialog.show();
    }

}