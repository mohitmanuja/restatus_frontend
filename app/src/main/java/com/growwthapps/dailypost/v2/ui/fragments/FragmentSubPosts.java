package com.growwthapps.dailypost.v2.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.FragmentSubPostsBinding;
import com.growwthapps.dailypost.v2.model.DownloadItem;
import com.growwthapps.dailypost.v2.ui.adapters.SavedAdapter;
import com.growwthapps.dailypost.v2.ui.activities.ShareImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FragmentSubPosts extends Fragment {
    private FragmentSubPostsBinding binding;
    private List<DownloadItem> uriList;
    private SavedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uriList = new ArrayList<>();
        adapter = new SavedAdapter(getActivity(), uriList, item -> goToNextActivity(item.getUri()));
        binding.rvDownload.setAdapter(adapter);

        loadData();

        binding.swipeRefresh.setOnRefreshListener(this::refreshData);

    }

    private void refreshData() {
        loadData();
    }

    private void loadData() {
        new LoadImagesTask().execute();
    }

    private void goToNextActivity(String uri) {
        Intent intent = new Intent(requireContext(), ShareImageActivity.class);
        intent.putExtra("uri", uri);
        startActivity(intent);
    }

    private class LoadImagesTask extends AsyncTask<Void, Void, List<DownloadItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.shimmerLayout.setVisibility(View.VISIBLE);
            binding.llNotFound.setVisibility(View.GONE);
            binding.rvDownload.setVisibility(View.GONE);
        }

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
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

                    for (File file : files) {
                        if (isImageFile(file)) {
                            items.add(new DownloadItem(file.getAbsolutePath(), false));
                        }
                    }
                }
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<DownloadItem> items) {
            super.onPostExecute(items);
            binding.shimmerLayout.setVisibility(View.GONE);

            if (items.size() > 0) {
                binding.llNotFound.setVisibility(View.GONE);
                binding.rvDownload.setVisibility(View.VISIBLE);
            } else {
                binding.llNotFound.setVisibility(View.VISIBLE);
                binding.rvDownload.setVisibility(View.GONE);
            }

            uriList.clear();
            uriList.addAll(items);
            adapter.notifyDataSetChanged();

            binding.swipeRefresh.setRefreshing(false);
        }

        private boolean isImageFile(File file) {
            String name = file.getName();
            String extension = name.substring(name.lastIndexOf(".") + 1);
            return extension.equalsIgnoreCase("png") ||
                    extension.equalsIgnoreCase("jpg") ||
                    extension.equalsIgnoreCase("mp4") ||
                    extension.equalsIgnoreCase("jpeg");
        }
    }
}
