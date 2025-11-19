package com.motivation.quotes.app.ui.activities;

import static com.motivation.quotes.app.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.growwthapps.dailypost.v2.BuildConfig;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ActivitySettingBinding;
import com.motivation.quotes.app.model.UserDestroy;
import com.motivation.quotes.app.ui.dialog.UniversalDialog;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.PreferenceManager;
import com.motivation.quotes.app.viewmodel.HomeViewModel;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    Activity context;
    private PreferenceManager preferenceManager;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    HomeViewModel homeViewModel;
    UserDestroy userDestroy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        context = this;

        topIconBar(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_setting), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.sub_bg_color));




        preferenceManager = new PreferenceManager(context);
        homeViewModel = new HomeViewModel();

        binding.settingVersionTxt.setText("Version " + BuildConfig.VERSION_NAME);

        binding.backImg.setOnClickListener(v -> {
            onBackPressed();
        });

        if (preferenceManager.getString(Constant.LANGUAGE_NAME) != null && !preferenceManager.getString(Constant.LANGUAGE_NAME).isEmpty()) {

            binding.tvLanguage.setText(preferenceManager.getString(Constant.LANGUAGE_NAME));

        }

        binding.tvLanguage.setText(preferenceManager.getString(Constant.LANGUAGE_NAME));

        binding.llPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyActivity.class);
            intent.putExtra("type", Constant.PRIVACY_POLICY);
            startActivity(intent);
        });

        binding.llTc.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyActivity.class);
            intent.putExtra("type", Constant.TERM_CONDITION);
            startActivity(intent);
        });


        binding.llLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(this, LanguageActivity.class);
            startActivity(intent);
        });

        binding.contactUsLy.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
        });

        binding.llRefund.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyActivity.class);
            intent.putExtra("type", Constant.REFUND_POLICY);
            startActivity(intent);
        });

        binding.llFeedback.setOnClickListener(v -> {
            try {
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
            } catch (android.content.ActivityNotFoundException anfe) {
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }
        });

        binding.llLogout.setOnClickListener(view -> {

            UniversalDialog universalDialog = new UniversalDialog(context, false);
            universalDialog.showConfirmDialog(getString(R.string.menu_logout), getString(R.string.message__want_to_logout), getString(R.string.message__logout), getString(R.string.message__cancel_close));

            universalDialog.okBtn.setOnClickListener(v -> {
                universalDialog.cancel();
                preferenceManager.clearAllDataOnLogout();
                preferenceManager.setBoolean(Constant.IS_LOGIN, false);

                mAuth.signOut();
                startActivity(new Intent(context, CustomSplashActivity.class));
                finishAffinity();
            });

            universalDialog.cancelBtn.setOnClickListener(v -> universalDialog.cancel());

            universalDialog.show();


        });

        binding.llDeleteAc.setOnClickListener(view -> {

            UniversalDialog universalDialog = new UniversalDialog(this, false);
            universalDialog.showDeleteDialog(context.getString(R.string.delete), context.getString(R.string.sure_delete), context.getString(R.string.delete), context.getString(R.string.cancel));
            universalDialog.okBtn.setBackgroundTintList(getColorStateList(R.color.colorLogo));
            universalDialog.show();

            universalDialog.okBtn.setOnClickListener(v -> {
                universalDialog.cancel();

                Log.d("Dest11", "onCreate:yeee "+preferenceManager.getString(Constant.USER_ID));

                homeViewModel.userDestroy(preferenceManager.getString(Constant.USER_ID)).observe(this, userDestroy -> {

                    preferenceManager.setBoolean(Constant.IS_LOGIN, false);
                    preferenceManager.clearAllDataOnLogout();

                    Log.d("Dest11", "onCreate: "+ new Gson().toJson(userDestroy));
                    mAuth.signOut();
                    startActivity(new Intent(this, CustomSplashActivity.class));
                    finishAffinity();

                });
            });

            universalDialog.cancelBtn.setOnClickListener(v -> universalDialog.cancel());


        });


    }


}