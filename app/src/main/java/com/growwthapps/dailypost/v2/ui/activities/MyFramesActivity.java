package com.growwthapps.dailypost.v2.ui.activities;

import static com.growwthapps.dailypost.v2.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ActivityMyFramesBinding;
import com.growwthapps.dailypost.v2.utils.MyUtils;

public class MyFramesActivity extends AppCompatActivity {

    private ActivityMyFramesBinding binding;
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyFramesBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        topIconBar(this);

        context = this;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainMyFrames), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        binding.backImg.setOnClickListener(view -> {onBackPressed();});

    }
}