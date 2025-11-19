package com.motivation.quotes.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;

import com.growwthapps.dailypost.v2.R;

public class Functions {


    public static String removeSpecialChar(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static Dialog dialog;

    public static void showLoader(Context context) {
        try {
            if (dialog != null) {
                cancelLoader();
                dialog = null;
            }
            {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_dialog_loading_view);
                dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_bg));
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }

        } catch (Exception e) {
            Log.d(Constant.tag, "Exception : " + e);
        }
    }

    public static void cancelLoader() {
        try {
            if (dialog != null || dialog.isShowing()) {
                dialog.cancel();
            }
        } catch (Exception e) {
            Log.d(Constant.tag, "Exception : " + e);
        }
    }

}
