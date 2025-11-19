package com.motivation.quotes.app.ui.activities;

import static com.motivation.quotes.app.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.growwthapps.dailypost.v2.R;
import com.motivation.quotes.app.ui.adapters.SavedAdapter;
import com.growwthapps.dailypost.v2.databinding.ActivityDownloadBinding;
import com.motivation.quotes.app.model.DownloadItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {
    private ActivityDownloadBinding binding;
    private List<DownloadItem> uriList;
    private SavedAdapter adapter;
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        context = this;

        topIconBar(this);

        uriList = new ArrayList<>();
        adapter = new SavedAdapter(context, uriList, (item) -> goToNextActivity(item.getUri()));

        loadData();

        binding.backImg.setOnClickListener(v -> {onBackPressed();});

    }

    private void loadData() {
        new LoadImagesTask().execute();
    }

    private void goToNextActivity(String uri) {
        Intent intent = new Intent(context, ShareImageActivity.class);
        intent.putExtra("uri", uri.toString());
        startActivity(intent);
    }

    private class LoadImagesTask extends AsyncTask<Void, Void, List<DownloadItem>> {

        @Override
        protected List<DownloadItem> doInBackground(Void... voids) {
            List<DownloadItem> items = new ArrayList<>();

            String folderPath = Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name);

            File directory = new File(folderPath);

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    // Sort files by last modified date in descending order
                    Arrays.sort(files, new Comparator<File>() {
                        @Override
                        public int compare(File f1, File f2) {
                            return Long.compare(f2.lastModified(), f1.lastModified());
                        }
                    });

                    for (File file : files) {
                        if (isImageFile(file)) {
                            items.add(new DownloadItem(""+file, false));
                        }
                    }
                }
            }
            return items;
        }

        private boolean isImageFile(File file) {
            String name = file.getName();
            String extension = name.substring(name.lastIndexOf(".") + 1);
            return extension.equalsIgnoreCase("png") ||
                    extension.equalsIgnoreCase("jpg") ||
                    extension.equalsIgnoreCase("jpeg");
        }
    }
}