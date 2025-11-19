package com.motivation.quotes.app.ui.activities;

import static com.motivation.quotes.app.utils.MyUtils.topIconBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.growwthapps.dailypost.v2.R;
import com.motivation.quotes.app.listener.AdapterClickListener;
import com.motivation.quotes.app.ui.adapters.LanguageAdapter;
import com.growwthapps.dailypost.v2.databinding.ActivityLanguageBinding;
import com.motivation.quotes.app.model.LanguageItem;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity {
    private ActivityLanguageBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        topIconBar(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_language), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        preferenceManager = new PreferenceManager(this);
        binding.backImg.setOnClickListener(view -> {
            onBackPressed();
        });

        List<LanguageItem> categoryItemList = new ArrayList<>();

        categoryItemList.add(new LanguageItem(-1, "", "All", true));

        Constant.getHomeViewModel(this).getLanguagess().observe(this, languageItems -> {

            if (languageItems != null) {

                if (languageItems.size() > 0) {
                    categoryItemList.addAll(languageItems);
                    binding.rvLanguage.setLayoutManager(new GridLayoutManager(this, 1));
                    LanguageAdapter languageAdapter = new LanguageAdapter(this, categoryItemList, new AdapterClickListener() {
                        @Override
                        public void onItemClick(View view, int pos, Object object) {
                            LanguageItem languageModel = (LanguageItem) object;

                            preferenceManager.setBoolean(Constant.LOAD_DATA, true);

                            preferenceManager.setString(Constant.USER_LANGUAGE, String.valueOf(languageModel.id));
                            preferenceManager.setString(Constant.LANGUAGE_NAME, languageModel.title);


                            Intent intent = new Intent(LanguageActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                    languageAdapter.selectItemById(Integer.parseInt(preferenceManager.getString(Constant.USER_LANGUAGE)));


                    binding.rvLanguage.setAdapter(languageAdapter);
                    binding.shimer.setVisibility(View.GONE);
                    binding.mainRecycleview.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "data not found", Toast.LENGTH_SHORT).show();
                    binding.shimer.setVisibility(View.VISIBLE);
                }

            }

        });

    }
}