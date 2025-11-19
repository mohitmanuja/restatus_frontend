package com.motivation.quotes.app.utils;

import static android.os.Build.VERSION.SDK_INT;
import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;


import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.motivation.quotes.app.utils.PathUtil.isMediaDocument;
import static com.yalantis.ucrop.util.FileUtils.getDataColumn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.motivation.quotes.app.MediaScannerUtil;
import com.growwthapps.dailypost.v2.R;
import com.motivation.quotes.app.model.AdsModel;
import com.google.gson.Gson;
import com.motivation.quotes.app.ui.activities.ShareImageActivity;
import com.motivation.quotes.app.ui.dialog.UniversalDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyUtils {

    public static AdsModel model;
    public static AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    public static PreferenceManager preferenceManager;
    private static String TAG = "AdmobAppId";
    public Activity context;

    public static void showResponse(Object onject) {


        Log.i("MyResponse " + onject.getClass().getSimpleName(), "" + new Gson().toJson(onject));

    }

    public static String getDeviceId(Context context) {
        return android.provider.Settings.Secure.getString(
                context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public static void topIconBar(Activity activity) {
        Window window = activity.getWindow();

        // Enable system bar background changes
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getColor(R.color.white));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ → Dark icons
            WindowInsetsController insetsController = window.getDecorView().getWindowInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0–10 → Dark icons
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    public static void status_bar_deny(Activity context) {
        int i;
        Window window = context.getWindow();
        window.addFlags(Integer.MIN_VALUE);
        window.clearFlags(67108864);
        window.setStatusBarColor(1);
        int i3 = Build.VERSION.SDK_INT;
        View decorView = window.getDecorView();
        if (i3 >= 26) {
            i = 1296;
        } else {
            i = 1280;
        }
        if (SDK_INT >= Build.VERSION_CODES.R) {
            context.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS);
        }


        decorView.setSystemUiVisibility(i);
//       context.getWindow().setFlags(8192, 8192);
    }

    public static void status_bar_dark(Activity context) {
        int i;
        Window window = context.getWindow();
        window.addFlags(Integer.MIN_VALUE);
        window.clearFlags(67108864);
        window.setStatusBarColor(1);
        int i3 = Build.VERSION.SDK_INT;
        View decorView = window.getDecorView();
        if (i3 >= 26) {
            i = 1296;
        } else {
            i = 1280;
        }
        if (SDK_INT >= Build.VERSION_CODES.R) {
            context.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS);
        }


        decorView.setSystemUiVisibility(i);
//       context.getWindow().setFlags(8192, 8192);
    }

    public static void status_bar_allow(Activity context) {
        int i;
        Window window = context.getWindow();
        window.addFlags(Integer.MIN_VALUE);
        window.clearFlags(67108864);
        window.setStatusBarColor(0);
        int i3 = Build.VERSION.SDK_INT;
        View decorView = window.getDecorView();
        if (i3 >= 26) {
            i = 1296;
        } else {
            i = 1280;
        }
        if (SDK_INT >= Build.VERSION_CODES.R) {
            context.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS);
        }

        decorView.setSystemUiVisibility(i);
//        context.getWindow().setFlags(8192, 8192);
    }

    public static void status_bar_light_white(Activity activity) {
        Window window = activity.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.TRANSPARENT);       // Transparent Status Bar
            window.setNavigationBarColor(Color.TRANSPARENT);   // Transparent Nav Bar
        }

        View decorView = window.getDecorView();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        // Apply the flags
        decorView.setSystemUiVisibility(flags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
                // DO NOT apply APPEARANCE_LIGHT_NAVIGATION_BARS — keeps nav icons white
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 6.0 to 10
            decorView.setSystemUiVisibility(flags); // Do NOT apply LIGHT_STATUS_BAR, so icons stay white
        }
    }

    public static void splash_status_bar(Activity context) {
        Window window = context.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        View decorView = window.getDecorView();
        int flags;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        } else {
            flags = View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        decorView.setSystemUiVisibility(flags);

        // Set dark status bar icons for Android 11+ (API 30+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(
//                        0, //for white
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, //for black
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        }
    }

    public static void hideNavigation(Activity context, boolean statusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (statusBar) {

                window.setStatusBarColor(context.getColor(R.color.bg_screen));
                context.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS);

            } else {
                window.setStatusBarColor(context.getColor(R.color.transparent));
                context.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS);
            }

        }


    }

    public static String getAppFolder(Context activity) {
        return activity.getExternalFilesDir(null).getPath() + "/";
    }

    public static class GetImageFileAsync extends AsyncTask<String, Void, File> {

        Activity context;
        Bitmap bitmap;

        OnBitmapSaved mBitmapSavedListener;
        ProgressDialog dialog;

        public GetImageFileAsync(Activity context, Bitmap bitmap) {
            this.context = context;
            this.bitmap = bitmap;


            dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait...");

            dialog.setCancelable(false);
            dialog.show();


            this.execute();
        }

        public void onBitmapSaved(OnBitmapSaved mlistener) {
            this.mBitmapSavedListener = mlistener;
        }

        @Override
        protected File doInBackground(String... strings) {


            String path = context.getCacheDir().getAbsolutePath();

            String name = System.currentTimeMillis() + ".png";

            File file = null;

            try {

                OutputStream fOut = null;
                Integer counter = 0;


                file = new File(path, name);

                if (!file.exists())
                    file.createNewFile();
                // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                fOut = new FileOutputStream(file);

                Bitmap pictureBitmap = bitmap; // obtaining the Bitmap
                pictureBitmap.compress(Bitmap.CompressFormat.PNG, 70, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

            } catch (Exception e) {
                e.printStackTrace();
            }

            return file;
        }

        @Override
        protected void onPostExecute(File s) {
            super.onPostExecute(s);

            dialog.dismiss();

            mBitmapSavedListener.onSaved(s);
        }

        public interface OnBitmapSaved {
            void onSaved(File file);
        }
    }


    public static void applyFrameNMusicProcess(Activity context, int size, View mainLayout, View videoView, String videoPath, String type) {
        UniversalDialog universalDialog = new UniversalDialog(context, false);
        universalDialog.showSavingDialog(context.getString(R.string.saving_post), context.getString(R.string.saving_post_desc));
        universalDialog.show();

        try {
            String pngPath = convertViewToPng(context, mainLayout, videoPath);

            // 1️⃣ Get positions relative to main layout
            int[] layoutLocation = new int[2];
            int[] videoLocation = new int[2];
            mainLayout.getLocationOnScreen(layoutLocation);
            videoView.getLocationOnScreen(videoLocation);

            // 2️⃣ Calculate scaling factor (output width = 1080)
            int viewWidth = mainLayout.getWidth();
            int viewHeight = mainLayout.getHeight();
            float scaleFactor = 1080f / viewWidth;
            int outputHeight = Math.round(viewHeight * scaleFactor);

            // Scaled video dimensions
            int scaledVideoWidth = Math.round(videoView.getWidth() * scaleFactor);
            int scaledVideoHeight = Math.round(videoView.getHeight() * scaleFactor);

            // 3️⃣ Output path
            String fileName = System.currentTimeMillis() + ".mp4";

// Create directory
            File outputDir = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + Environment.DIRECTORY_PICTURES
                            + File.separator + context.getResources().getString(R.string.app_name)
            );

            if (!outputDir.exists()) {
                outputDir.mkdirs();   // <--- This line fixes the crash
            }

            String filePath = new File(outputDir, fileName).getAbsolutePath();

            // 4️⃣ FFmpeg command with proper padding
            List<String> cmdList = new ArrayList<>();
            cmdList.add("-i");
            cmdList.add(videoPath);
            cmdList.add("-i");
            cmdList.add(pngPath);

            // Updated filter complex with dynamic padding:
            cmdList.add("-filter_complex");
            cmdList.add(String.format(
                    "[0:v]scale=%d:%d:force_original_aspect_ratio=decrease[scaled];" +
                            "[scaled]pad=1080:%d:0:%d:black[padded];" +
                            "[padded][1:v]overlay=0:0[out]",
                    scaledVideoWidth,
                    scaledVideoHeight,
                    outputHeight,
                    size  // Use the calculated top padding
            ));

            cmdList.add("-map");
            cmdList.add("[out]");
            cmdList.add("-map");
            cmdList.add("0:a?"); // keep audio if exists
            cmdList.add("-c:v");
            cmdList.add("libx264");
            cmdList.add("-preset");
            cmdList.add("ultrafast");
            cmdList.add("-crf");
            cmdList.add("15");
            cmdList.add("-shortest");
            cmdList.add("-y");
            cmdList.add(filePath);

            String[] ffmpegCommand = cmdList.toArray(new String[0]);

            // 5️⃣ Execute FFmpeg
            FFmpeg.executeAsync(ffmpegCommand, (executionId, returnCode) -> {
                if (universalDialog.getDialog() != null) universalDialog.cancel();

                if (returnCode == RETURN_CODE_SUCCESS) {
                    // Delete temporary PNG file
                    new File(pngPath).delete();

                    if (type.contains("download")) {
                        Toast.makeText(context, "Video saved successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, ShareImageActivity.class);
                        intent.putExtra("uri", filePath);
                        context.startActivity(intent);
                    } else {
                        shareFileImageUri(context, getImageContentUri(new File(filePath)), type);
                    }
                    MediaScannerUtil.scanFile(context, filePath);
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.e("finalProcess__", "Command execution cancelled.");
                    Toast.makeText(context, "Cancelled.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("finalProcess__", "Execution failed: " + returnCode);
                    Toast.makeText(context, "Failed, try again.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (universalDialog.getDialog() != null) universalDialog.cancel();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String convertViewToPng(Activity context, View mainLayout, String videoPath) {
        // Get the view size
        int originalWidth = mainLayout.getWidth();
        int originalHeight = mainLayout.getHeight();

        if (originalWidth == 0 || originalHeight == 0) {
            Toast.makeText(context, "View has no size yet", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Capture original bitmap
        Bitmap originalBitmap = Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(originalBitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, Paint.FILTER_BITMAP_FLAG));
        mainLayout.draw(canvas);

        // Calculate scaled height based on width=1080
        float scaleRatio = 1080f / originalWidth;
        int scaledHeight = Math.round(originalHeight * scaleRatio);

        // Scale bitmap
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 1080, scaledHeight, true);

        // Save PNG
        File pngFile = new File(context.getCacheDir(), "overlay_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(pngFile)) {
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return pngFile.getAbsolutePath();
    }


/*
    public static void applyFrameNMusicProcess(Activity context, View framePath, String filePath, String type) {
        UniversalDialog universalDialog = new UniversalDialog(context, false);
        universalDialog.showSavingDialog(context.getString(R.string.saving_post), context.getString(R.string.saving_post_desc));

        universalDialog.show();

        Log.d("applyFrameNMusicProcess", "Video: " + filePath);
        Log.d("applyFrameNMusicProcess", "Overlay PNG: " + framePath);

        String outputDir = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name)
                + File.separator + context.getString(R.string.app_name) + System.currentTimeMillis() + ".mp4";

        List<String> ffmpegCommandList = new ArrayList<>();
        ffmpegCommandList.add("-i");
        ffmpegCommandList.add(filePath);

        String pngImageFrame;
        pngImageFrame = convertViewToPng(context, framePath, "1080:1600", filePath);
        //  deleteFilesArray.add(pngImageFrame);
        Log.d("applyFrameNMusicProcess", "Overlay 11PNG: " + framePath);

        ffmpegCommandList.add("-i");
        ffmpegCommandList.add(pngImageFrame);

        ffmpegCommandList.add("-filter_complex");
        ffmpegCommandList.add("[0:v][1:v]overlay[out]");

        ffmpegCommandList.add("-map");
        ffmpegCommandList.add("[out]");

        // Make audio mapping optional
        ffmpegCommandList.add("-map");
        ffmpegCommandList.add("0:a?");  // Add '?' to ignore if audio is missing

        ffmpegCommandList.add("-c:v");
        ffmpegCommandList.add("libx264");
        ffmpegCommandList.add("-preset");
        ffmpegCommandList.add("ultrafast");
        ffmpegCommandList.add("-crf");
        ffmpegCommandList.add("15");
        ffmpegCommandList.add("-shortest");
        ffmpegCommandList.add("-y");
        ffmpegCommandList.add(outputDir);

        String[] ffmpegCommand = ffmpegCommandList.toArray(new String[0]);

        FFmpeg.executeAsync(ffmpegCommand, (executionId, returnCode) -> {
//            universalDialog.dissmissLoadingDialog();
            if (universalDialog.getDialog() != null) {
                universalDialog.cancel();
            }

            if (returnCode == RETURN_CODE_SUCCESS) {
                if (type.contains("download")) {
                    Toast.makeText(context, "Video saved successfully.", Toast.LENGTH_SHORT).show();

                } else {
                    shareFileImageUri(context, getImageContentUri(new File(outputDir)), type);
                }
                MediaScannerUtil.scanFile(context, outputDir);
            } else if (returnCode == RETURN_CODE_CANCEL) {
                Log.e("finalProcess__", "Command execution cancelled by user.");
                Toast.makeText(context, "Command execution cancelled.", Toast.LENGTH_SHORT).show();

            } else {
                Log.i("finalProcess__", "Command execution failed: " + returnCode);
                Toast.makeText(context, "Command execution failed, try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String convertViewToPng(Activity context, View mainLayout, String ratio, String video) {
        // 1️⃣ Capture original bitmap at ARGB_8888
        Bitmap originalBitmap = Bitmap.createBitmap(mainLayout.getWidth(), mainLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(originalBitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, Paint.FILTER_BITMAP_FLAG));
        mainLayout.draw(canvas);

        Bitmap croppedBitmap = originalBitmap;

        // 2️⃣ Crop to ratio if provided
        if (ratio != null && !ratio.trim().isEmpty() && ratio.contains(":")) {
            try {
                String[] parts = ratio.split(":");
                int targetWidthRatio = Integer.parseInt(parts[0]);
                int targetHeightRatio = Integer.parseInt(parts[1]);

                float targetRatio = (float) targetWidthRatio / targetHeightRatio;
                float originalRatio = (float) originalBitmap.getWidth() / originalBitmap.getHeight();

                if (originalRatio > targetRatio) {
                    // Original is wider -> crop sides
                    int newWidth = (int) (originalBitmap.getHeight() * targetRatio);
                    int xOffset = (originalBitmap.getWidth() - newWidth) / 2;
                    croppedBitmap = Bitmap.createBitmap(originalBitmap, xOffset, 0, newWidth, originalBitmap.getHeight());
                } else if (originalRatio < targetRatio) {
                    // Original is taller -> crop top/bottom
                    int newHeight = (int) (originalBitmap.getWidth() / targetRatio);
                    int yOffset = (originalBitmap.getHeight() - newHeight) / 2;
                    croppedBitmap = Bitmap.createBitmap(originalBitmap, 0, yOffset, originalBitmap.getWidth(), newHeight);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // 3️⃣ Get video resolution for scaling
        int videoWidth = 1080; // fallback
        int videoHeight = 1080;

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(video);
            videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4️⃣ Smooth scale using ARGB_8888
        Bitmap finalBitmap = Bitmap.createScaledBitmap(croppedBitmap, videoWidth, videoHeight, true);

        // 5️⃣ Save as PNG (lossless, high quality)
        String outputPath = getAppFolder(context) + "/viewimage.png";
        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // 6️⃣ Clean up unused bitmaps
        if (croppedBitmap != originalBitmap) originalBitmap.recycle();
        finalBitmap.recycle(); // only recycle if you don't need it later

        return outputPath;
    }
*/
    public static void shareFileImageUri(Activity activity, Uri path, String shareTo) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        switch (shareTo) {
            case "whtsapp":
                shareIntent.setPackage("com.whatsapp");
                break;
            case "fb":
                shareIntent.setPackage("com.facebook.katana");
                break;
            case "insta":
                shareIntent.setPackage("com.instagram.android");
                break;
            case "twter":
                shareIntent.setPackage("com.twitter.android");
                break;
        }

        if (shareTo.contains("download") || shareTo.contains("Share")) {

            shareIntent.setDataAndType(path, "video/*");
        } else {
            shareIntent.setDataAndType(path, "image/*");
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM, path);

        shareIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_txt) + activity.getPackageName());

        activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.share_txt) + activity.getPackageName()));
    }

    public static Uri getImageContentUri(File imageFile) {
        return Uri.parse(imageFile.getAbsolutePath());
    }
}
