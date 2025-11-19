package com.motivation.quotes.app.ui.activities;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.motivation.quotes.app.binding.GlideDataBinding.viewToBitmap;
import static com.motivation.quotes.app.ui.adapters.FontsAdapter.getEnglishFonts;
import static com.motivation.quotes.app.utils.MyUtils.getAppFolder;
import static com.motivation.quotes.app.utils.MyUtils.topIconBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.bumptech.glide.Glide;
import com.growwthapps.dailypost.v2.R;
import com.motivation.quotes.app.View.AutoFitEditText;
import com.motivation.quotes.app.View.ViewIdGenerator;
import com.motivation.quotes.app.View.text.AutofitTextInfo;
import com.motivation.quotes.app.View.text.AutofitTextRel;
import com.motivation.quotes.app.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.ActivityEditorBinding;
import com.motivation.quotes.app.listener.AdapterClickListener;
import com.motivation.quotes.app.listener.ClickListener;
import com.motivation.quotes.app.model.FontDataModel;
import com.motivation.quotes.app.model.TemplateInfo;
import com.motivation.quotes.app.ui.adapters.ColorAdapter;
import com.motivation.quotes.app.ui.adapters.FontsAdapter;
import com.motivation.quotes.app.ui.model.ElementInfo;
import com.motivation.quotes.app.ui.model.textInfo;
import com.motivation.quotes.app.ui.utility.ImageUtils;
import com.motivation.quotes.app.utils.PreferenceManager;
import com.motivation.quotes.app.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import androidx.activity.EdgeToEdge;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EditorActivity extends AppCompatActivity implements AutofitTextRel.TouchEventListener {

    private Activity context;
    private String ratio;
    private String type = "";


    private Bitmap bitmap;

    private Dialog dialogFrame;

    ListFragment listFragment;
    private int isTamplate;


    ArrayList<ElementInfo> elementInfos = new ArrayList<>();
    ArrayList<textInfo> textInfoArrayList = new ArrayList<>();
    ArrayList<AutofitTextInfo> textInfosUR = new ArrayList<>();
    HashMap<Integer, Object> txtShapeList;
    ArrayList<TemplateInfo> templateListUR = new ArrayList<>();


    private boolean isMovie;

    private float letterSpacing = 0.0f;
    private float lineSpacing = 0.0f;

    public static ActivityEditorBinding binding;
    String backgroundPosterPath;

    private AutofitTextRel selectedTextView;
    private String cameraTempFile;
    public static int bgColor = ViewCompat.MEASURED_STATE_MASK;
    private boolean isDirectBack = false;
    private String imageUri;
    private Dialog dialogdiscard;
    private PreferenceManager preferenceManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        topIconBar(this);

        context = this;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainEdit), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        preferenceManager = new PreferenceManager(context);

        exitDialog();


        imageUri = getIntent().getStringExtra("imageUri");

        if (getIntent().getStringExtra("greeting") != null){

            type = getIntent().getStringExtra("greeting");

        }

        if (type != null && type.equals("greeting")) {
            binding.rlGreetings.setVisibility(VISIBLE);
        }else {

            addTextNew("Enter Text");
        }

        intilization();
        GlideDataBinding.bindImage(binding.backgroundImg, imageUri);


        binding.changeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedTextView == null){
                    Toast.makeText(context, "Add Text", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.llFont.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(VISIBLE);
                    binding.llAlign.setVisibility(GONE);
                    binding.llShadow.setVisibility(GONE);
                    binding.llBoxColor.setVisibility(GONE);
                    binding.llChangeColor.setVisibility(GONE);

                    slideUp(binding.llFont);
                    binding.changeFontImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.changeFontTxt.setTextColor(getResources().getColor(R.color.see_more));
                }

            }
        });


        binding.boxColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTextView == null){
                    Toast.makeText(context, "Add Text", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.llBoxColor.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(GONE);
                    binding.llAlign.setVisibility(GONE);
                    binding.llShadow.setVisibility(GONE);
                    binding.llBoxColor.setVisibility(VISIBLE);
                    binding.llChangeColor.setVisibility(GONE);
                    slideUp(binding.llBoxColor);
                    binding.boxColorImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.boxColorTxt.setTextColor(getResources().getColor(R.color.see_more));
                }

            }
        });


        binding.changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTextView == null){
                    Toast.makeText(context, "Add Text", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.llChangeColor.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(GONE);
                    binding.llAlign.setVisibility(GONE);
                    binding.llShadow.setVisibility(GONE);
                    binding.llBoxColor.setVisibility(GONE);
                    binding.llChangeColor.setVisibility(VISIBLE);
                    slideUp(binding.llChangeColor);
                    binding.changeColorImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.changeColorTxt.setTextColor(getResources().getColor(R.color.see_more));
                }

            }
        });


        binding.alignMent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedTextView == null){
                    Toast.makeText(context, "Add Text", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.llAlign.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(GONE);
                    binding.llAlign.setVisibility(VISIBLE);
                    binding.llShadow.setVisibility(GONE);
                    binding.llBoxColor.setVisibility(GONE);
                    binding.llChangeColor.setVisibility(GONE);
                    slideUp(binding.llAlign);
                    binding.alignmentImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.alignmentTxt.setTextColor(getResources().getColor(R.color.see_more));
                }


            }
        });


        binding.shadowColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTextView == null){
                    Toast.makeText(context, "Add Text", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.llShadow.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(GONE);
                    binding.llAlign.setVisibility(GONE);
                    binding.llShadow.setVisibility(VISIBLE);
                    binding.llBoxColor.setVisibility(GONE);
                    binding.llChangeColor.setVisibility(GONE);
                    slideUp(binding.llShadow);
                    binding.shadowColorImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.shadowColorTxt.setTextColor(getResources().getColor(R.color.see_more));
                }

            }
        });


        binding.llAddText.setOnClickListener(view -> {
            hideTextStickerBorders();

           // binding.addTextImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
          //  binding.addTextTxt.setTextColor(getResources().getColor(R.color.see_more));

            addTextDialog(null);

        });


        setFontAdapter(getEnglishFonts());

        binding.backImg.setOnClickListener(view -> {

            if (dialogdiscard != null) {
                dialogdiscard.show();
            }
        });

        binding.tvSave.setOnClickListener(view -> {


            if (binding.rlGreetings.getVisibility() == GONE){

                hideAllLayouts("");

                dialogFrame();

            }else {

                Toast.makeText(context, "Add Image", Toast.LENGTH_SHORT).show();
            }

        });


        binding.addGreeting.setOnClickListener(view -> {
            hideAllLayouts("");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction("android.intent.action.PICK");
            resultCallbackForGreeting.launch(intent);
        });


    }

    @Override
    public void onBackPressed() {

        if (!isDirectBack) {

            if (dialogdiscard != null) {
                dialogdiscard.show();
            }
        } else {
            super.onBackPressed();
        }

    }


    private void exitDialog() {

        dialogdiscard = new Dialog(this);
        dialogdiscard.requestWindowFeature(1);
        dialogdiscard.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogdiscard.setContentView(R.layout.dialog_editor_backpress);
        dialogdiscard.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogdiscard.setCanceledOnTouchOutside(false);
        dialogdiscard.setCancelable(false);

        TextView yes = dialogdiscard.findViewById(R.id.discardBtn);
        TextView no = dialogdiscard.findViewById(R.id.cancelBtn);

        yes.setOnClickListener(view12 -> {
            isDirectBack = true;
            onBackPressed();
        });

        no.setOnClickListener(view12 -> {
            if (dialogdiscard.isShowing())
                dialogdiscard.dismiss();
        });

        Window window = dialogdiscard.getWindow();
        window.setLayout(MATCH_PARENT, MATCH_PARENT);

    }


    public void saveBitmapUndu() {
        try {
            TemplateInfo templateInfo = new TemplateInfo();
            templateInfo.setRATIO(this.ratio);
            templateInfo.setBACKGROUND_PATH(this.backgroundPosterPath);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int childCount = binding.textViewRelative.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = binding.textViewRelative.getChildAt(i);
                if (childAt instanceof AutofitTextRel) {
                    AutofitTextInfo textInfo = ((AutofitTextRel) childAt).getTextInfo();
                    textInfo.setORDER(i);
                    textInfo.setTYPE("TEXT");
                    arrayList.add(textInfo);
                }
            }
            templateInfo.setTextInfoArrayList(arrayList);
            templateInfo.setElementInfoArrayList(arrayList2);
            this.templateListUR.add(templateInfo);
            //  iconVisibility();
        } catch (Exception e) {
            Log.i("testing", "Exception " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable th) {
        }
    }

    private void clearTint() {

        binding.alignmentImg.clearColorFilter();
        binding.addTextImg.clearColorFilter();
        binding.boxColorImg.clearColorFilter();
        binding.changeColorImg.clearColorFilter();
        binding.shadowColorImg.clearColorFilter();
        binding.changeFontImg.clearColorFilter();
        binding.addTextTxt.setTextColor(getResources().getColor(R.color.black));
        binding.alignmentTxt.setTextColor(getResources().getColor(R.color.black));
        binding.boxColorTxt.setTextColor(getResources().getColor(R.color.black));
        binding.changeColorTxt.setTextColor(getResources().getColor(R.color.black));
        binding.shadowColorTxt.setTextColor(getResources().getColor(R.color.black));
        binding.changeFontTxt.setTextColor(getResources().getColor(R.color.black));

    }


    public void downoloadFonts(String str, String str2, String str3) {

        AndroidNetworking.download("" + str, "" + str2, "" + str3).build().startDownload(new DownloadListener() {
            public void onDownloadComplete() {

                setTextFonts(str3);
            }

            public void onError(ANError aNError) {

            }
        });
    }

    public String ExtractFileNameFromUrl(String urlString) {
        try {
            String fileName = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Path path = Paths.get(new URL(urlString).getPath());
                fileName = path.getFileName().toString();
            }
            return fileName;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public void setTextFonts(String str) {
        if (selectedTextView != null) {
            selectedTextView.setTextFont(str);
            saveBitmapUndu();
        }
    }

    private void setBoldFonts() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                Log.d("setTextFont___", "Check -> " + selectedTextView.isBold());
                if (selectedTextView.isBold()) {
                    selectedTextView.setNormalFont();
                } else {
                    selectedTextView.setBoldFont();
                }
                saveBitmapUndu();
            }
        }
    }

    private void setItalicFont() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                selectedTextView.setItalicFont();
                saveBitmapUndu();
            }
        }
    }

    private void setLeftAlignMent() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                selectedTextView.setLeftAlignMent();
                saveBitmapUndu();
            }
        }
    }

    private void setCenterAlignMent() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                selectedTextView.setCenterAlignMent();
                saveBitmapUndu();
            }
        }
    }

    private void setRightAlignMent() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                selectedTextView.setRightAlignMent();
                saveBitmapUndu();
            }
        }
    }

    private void copyTextView() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                AutofitTextRel autofitTextRel2 = new AutofitTextRel(this);
                binding.textViewRelative.addView(autofitTextRel2);
                autofitTextRel2.setTextInfo(selectedTextView.getTextInfo(), false);
                autofitTextRel2.setId(ViewIdGenerator.generateViewId());
                autofitTextRel2.setOnTouchCallbackListener(this);
                autofitTextRel2.setBorderVisibility(true);
                saveBitmapUndu();
            }
        }
    }

    boolean editMode = false;

    public void addTextDialog(final AutofitTextInfo originAutofitTextInfo) {
        hideAllLayouts("");
        final Dialog dialog = new Dialog(this, R.style.MyAlertDialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog.setContentView(R.layout.add_text_dialog);

        final AutoFitEditText autoFitEditText = dialog.findViewById(R.id.auto_fit_edit_text);
        ImageView button = dialog.findViewById(R.id.btnCancelDialog);
        LinearLayout button2 = dialog.findViewById(R.id.btnAddTextSDialog);

        if (originAutofitTextInfo != null) {
            autoFitEditText.setText(originAutofitTextInfo.getTEXT());
        } else {
            autoFitEditText.setText("Enter Text");
        }

        button.setOnClickListener(view -> dialog.dismiss());
        button2.setOnClickListener(view -> {
            if (autoFitEditText.getText().toString().length() > 0) {
                if (originAutofitTextInfo != null) {
                    selectedTextView.setText(autoFitEditText.getText().toString());
                } else {
                    addText(autoFitEditText.getText().toString());
                }
                dialog.dismiss();
            } else {
                Toast.makeText(EditorActivity.this, "Please enter text here.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(MATCH_PARENT, MATCH_PARENT);
    }

    private void addText(String text) {
        AutofitTextInfo autofitTextInfo = new AutofitTextInfo();
        AutofitTextRel autofitTextRel = null;
        autofitTextInfo.setTEXT(text);
        autofitTextInfo.setTEXT_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setTEXT_ALPHA(100);
        autofitTextInfo.setSHADOW_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setSHADOW_PROG(0);
        autofitTextInfo.setBG_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setBG_DRAWABLE("0");
        autofitTextInfo.setBG_ALPHA(0);
        autofitTextInfo.setROTATION(0.0f);

        // Calculate center positions dynamically
        float posX = (binding.textViewRelative.getWidth() / 2) - (ImageUtils.dpToPx(EditorActivity.this, 100.0f)); // Half of text width
        float posY = (binding.textViewRelative.getHeight() / 2) - (ImageUtils.dpToPx(EditorActivity.this, 25.0f)); // Half of text height

        autofitTextInfo.setPOS_X(posX);
        autofitTextInfo.setPOS_Y(posY);
        autofitTextInfo.setWIDTH(ImageUtils.dpToPx(EditorActivity.this, 200.0f)); // Text width
        autofitTextInfo.setHEIGHT(ImageUtils.dpToPx(EditorActivity.this, 50.0f));  // Text height

        try {
            autofitTextRel = new AutofitTextRel(EditorActivity.this);
            binding.textViewRelative.addView(autofitTextRel);
            autofitTextRel.setTextInfo(autofitTextInfo, false);
            autofitTextRel.setId(ViewIdGenerator.generateViewId());
            autofitTextRel.setOnTouchCallbackListener(EditorActivity.this);
            autofitTextRel.setBorderVisibility(true);
        } catch (ArrayIndexOutOfBoundsException e2) {
            e2.printStackTrace();
        }
        if (autofitTextRel != null) {
            selectedTextView = autofitTextRel;
            //   showTextControlles();
        }
        saveBitmapUndu();
    }

    private void addTextNew(String text) {
        AutofitTextInfo autofitTextInfo = new AutofitTextInfo();
        AutofitTextRel autofitTextRel = null;
        autofitTextInfo.setTEXT(text);
        autofitTextInfo.setTEXT_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setTEXT_ALPHA(100);
        autofitTextInfo.setSHADOW_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setSHADOW_PROG(0);
        autofitTextInfo.setBG_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setBG_DRAWABLE("0");
        autofitTextInfo.setBG_ALPHA(0);
        autofitTextInfo.setROTATION(0.0f);


        autofitTextInfo.setPOS_X((float) ((binding.textViewRelative.getWidth() / 2) - ImageUtils.dpToPx(EditorActivity.this, -80.0f)));
        autofitTextInfo.setPOS_Y((float) ((binding.textViewRelative.getHeight() / 2) - ImageUtils.dpToPx(EditorActivity.this, -180.0f)));
        autofitTextInfo.setWIDTH(ImageUtils.dpToPx(EditorActivity.this, 200.0f));
        autofitTextInfo.setHEIGHT(ImageUtils.dpToPx(EditorActivity.this, 50.0f));
        try {
            autofitTextRel = new AutofitTextRel(EditorActivity.this);
            binding.textViewRelative.addView(autofitTextRel);
            autofitTextRel.setTextInfo(autofitTextInfo, false);
            autofitTextRel.setId(ViewIdGenerator.generateViewId());
            autofitTextRel.setOnTouchCallbackListener(EditorActivity.this);
            autofitTextRel.setBorderVisibility(true);
        } catch (ArrayIndexOutOfBoundsException e2) {
            e2.printStackTrace();
        }
        if (autofitTextRel != null) {
            selectedTextView = autofitTextRel;
            //   showTextControlles();
        }
        saveBitmapUndu();
    }

    private void setFontAdapter(List<FontDataModel> list) {
        ((RecyclerView) findViewById(R.id.ep_fontRv)).setAdapter(new FontsAdapter(context, list, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                FontDataModel model = (FontDataModel) object;
                if (!new File(getAppFolder(context) + "font/" + ExtractFileNameFromUrl(model.getUrl())).exists()) {
                    downoloadFonts(model.getUrl(), getAppFolder(context) + "font/", ExtractFileNameFromUrl(model.getUrl()));
                } else {
                    setTextFonts(ExtractFileNameFromUrl(model.getUrl()));
                }
            }
        }));
    }


    private void hideAllLayouts(String nohide) {

        if (!(nohide.equals("text") || nohide.equals("image"))) {
            hideTextStickerBorders();
        }

    }

    public void slideUp(View view) {
        view.setVisibility(VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(300);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }, 500);
    }



    public void hideTextStickerBorders() {
        RelativeLayout relativeLayout = binding.textViewRelative;
        if (relativeLayout != null) {
            int childCount = relativeLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = binding.textViewRelative.getChildAt(i);
                if (childAt instanceof AutofitTextRel) {
                    ((AutofitTextRel) childAt).setBorderVisibility(false);
                }

            }
        }
    }

    private void touchDown(View view, String str) {
        Log.d("editorLogs--->", "check -> touchDown()");
        if (str.equals("hideboder")) {
            hideTextStickerBorders();
        }
    }

    private void touchMove(View view) {
        Log.d("editorLogs--->", "check -> touchMove()");
    }

    private void touchUp(final View view) {
        Log.d("editorLogs--->", "check -> touchUp()");
        if (view instanceof AutofitTextRel) {
            selectedTextView = (AutofitTextRel) view;
            saveBitmapUndu();

        }

    }


    @Override
    public void onEdit(View view, Uri uri) {

    }


    public void onRotateDown(View view) {
        touchDown(view, "viewboder");
    }

    public void onRotateMove(View view) {
        touchMove(view);
    }

    public void onRotateUp(View view) {
        touchUp(view);
    }

    public void onScaleDown(View view) {
        touchDown(view, "viewboder");
    }

    public void onScaleMove(View view) {
        touchMove(view);
    }

    public void onScaleUp(View view) {
        touchUp(view);
    }

    boolean checkTouchContinue = false;

    public void onTouchDown(View view) {
        touchDown(view, "hideboder");
        if (this.checkTouchContinue) {

        }
    }

    public void onTouchMove(View view) {
        touchMove(view);
    }

    @Override
    public void onTouchMoveUpClick(View view) {

    }

    public void onTouchUp(View view) {
        this.checkTouchContinue = false;
        touchUp(view);
    }


    @Override
    public void onDelete() {

    }

    public void onDoubleTap() {
        doubleTabPrass();
    }

    private void doubleTabPrass() {
        this.editMode = true;
        try {
            if (selectedTextView.getBorderVisibility()) {
                AutofitTextInfo autofitTextInfo = selectedTextView.getTextInfo();
                addTextDialog(autofitTextInfo);
            }
        } catch (NullPointerException e) {

            e.printStackTrace();

        }
    }

    private void intilization() {
        binding.mainRelativeLay.setOnClickListener(view -> {
          //  hideAllLayouts("");
        });


        binding.fontBoldImg.setOnClickListener(view -> {
            setBoldFonts();
        });
        binding.fontItalicImg.setOnClickListener(view -> {
            setItalicFont();
        });

        binding.alignLeftLay.setOnClickListener(view -> {
            setLeftAlignMent();
        });
        binding.alignCenterLay.setOnClickListener(view -> {
            setCenterAlignMent();
        });
        binding.alignRightLay.setOnClickListener(view -> {
            setRightAlignMent();
        });
        binding.llAddText.setOnClickListener(view -> {
            copyTextView();
        });

        binding.colorBtn.setOnClickListener(view -> {
            new AmbilWarnaDialog(this, selectedTextView.getTextColor(), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                    selectedTextView.setTextColor(i);
                }

                public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {

                }
            }).show();
        });

        binding.colorPickerImg2.setOnClickListener(view -> {
            new AmbilWarnaDialog(this, selectedTextView.getBgColor(), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                    selectedTextView.setBgColor(i);
                }

                public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {

                }
            }).show();
        });


        binding.colorPickerImg.setOnClickListener(view -> {
            new AmbilWarnaDialog(this, selectedTextView.getTextShadowColor(), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                public void onOk(AmbilWarnaDialog ambilWarnaDialog, int color) {
                    selectedTextView.setTextShadowColor(color);
                    saveBitmapUndu();
                }

                public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {

                }
            }).show();
        });



        binding.fontSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b && selectedTextView != null) {
                    selectedTextView.setFontSize(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                saveBitmapUndu();


            }
        });

        binding.fontOpacitySeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectedTextView.setTextAlpha(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        binding.fontBgOpacitySeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectedTextView.setBgAlpha(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        if (selectedTextView != null) {
            binding.fontSizeSeekBar.setProgress((int) selectedTextView.getFontSize());

            selectedTextView.setTextShadowOpacity(100);
        }





        binding.shadowSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (selectedTextView.getBorderVisibility()) {


                        selectedTextView.setTextShadowProg(i);

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveBitmapUndu();
            }
        });




        ArrayList<Integer> colorList = new ArrayList<>();
        colorList.add(Integer.valueOf(Color.parseColor("#00000000")));
        colorList.add(Integer.valueOf(Color.parseColor("#000000")));
        colorList.add(Integer.valueOf(Color.parseColor("#0098F1")));
        colorList.add(Integer.valueOf(Color.parseColor("#4CC259")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFC859")));
        colorList.add(Integer.valueOf(Color.parseColor("#FF8523")));
        colorList.add(Integer.valueOf(Color.parseColor("#FF3A4A")));
        colorList.add(Integer.valueOf(Color.parseColor("#E90060")));
        colorList.add(Integer.valueOf(Color.parseColor("#B300B6")));
        colorList.add(Integer.valueOf(Color.parseColor("#FF0000")));
        colorList.add(Integer.valueOf(Color.parseColor("#FF7E88")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFD0D1")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFDAB2")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFC07E")));
        colorList.add(Integer.valueOf(Color.parseColor("#E18B42")));
        colorList.add(Integer.valueOf(Color.parseColor("#a36138")));
        colorList.add(Integer.valueOf(Color.parseColor("#4A2829")));
        colorList.add(Integer.valueOf(Color.parseColor("#004C30")));
        colorList.add(Integer.valueOf(Color.parseColor("#2C2C2C")));
        colorList.add(Integer.valueOf(Color.parseColor("#393939")));
        colorList.add(Integer.valueOf(Color.parseColor("#555555")));
        colorList.add(Integer.valueOf(Color.parseColor("#727272")));
        colorList.add(Integer.valueOf(Color.parseColor("#989898")));
        colorList.add(Integer.valueOf(Color.parseColor("#B1B1B1")));
        colorList.add(Integer.valueOf(Color.parseColor("#C7C7C7")));
        colorList.add(Integer.valueOf(Color.parseColor("#DBDBDB")));
        colorList.add(Integer.valueOf(Color.parseColor("#F0F0F0")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFFFFF")));

// Add the rest of your colors


        ArrayList<Integer> fontColor = new ArrayList<>();
        fontColor.add(Integer.valueOf(Color.parseColor("#000000")));
        fontColor.add(Integer.valueOf(Color.parseColor("#000000")));
        fontColor.add(Integer.valueOf(Color.parseColor("#0098F1")));
        fontColor.add(Integer.valueOf(Color.parseColor("#4CC259")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFC859")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FF8523")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FF3A4A")));
        fontColor.add(Integer.valueOf(Color.parseColor("#E90060")));
        fontColor.add(Integer.valueOf(Color.parseColor("#B300B6")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FF0000")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FF7E88")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFD0D1")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFDAB2")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFC07E")));
        fontColor.add(Integer.valueOf(Color.parseColor("#E18B42")));
        fontColor.add(Integer.valueOf(Color.parseColor("#a36138")));
        fontColor.add(Integer.valueOf(Color.parseColor("#4A2829")));
        fontColor.add(Integer.valueOf(Color.parseColor("#004C30")));
        fontColor.add(Integer.valueOf(Color.parseColor("#2C2C2C")));
        fontColor.add(Integer.valueOf(Color.parseColor("#393939")));
        fontColor.add(Integer.valueOf(Color.parseColor("#555555")));
        fontColor.add(Integer.valueOf(Color.parseColor("#727272")));
        fontColor.add(Integer.valueOf(Color.parseColor("#989898")));
        fontColor.add(Integer.valueOf(Color.parseColor("#B1B1B1")));
        fontColor.add(Integer.valueOf(Color.parseColor("#C7C7C7")));
        fontColor.add(Integer.valueOf(Color.parseColor("#DBDBDB")));
        fontColor.add(Integer.valueOf(Color.parseColor("#F0F0F0")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFFFFF")));

        ColorAdapter colorAdapter = new ColorAdapter(fontColor, this, new ClickListener<Integer>() {
            @Override
            public void onClick(Integer data) {
                if (selectedTextView != null){

                    selectedTextView.setTextColor(data.intValue());
                }else {

                    Toast.makeText(context, "Select Text", Toast.LENGTH_SHORT).show();
                }


            }
        });
        binding.fontColorRv.setAdapter(colorAdapter);


        ColorAdapter colorAdapter2 = new ColorAdapter(colorList, this, new ClickListener<Integer>() {
            @Override
            public void onClick(Integer data) {
                if (selectedTextView != null){

                    selectedTextView.setBgColor(data.intValue());
                    bgColor = data.intValue();

                }else {

                    Toast.makeText(context, "Select Text", Toast.LENGTH_SHORT).show();
                }



            }
        });
        binding.boxColorRv.setAdapter(colorAdapter2);


        ColorAdapter colorAdapter3 = new ColorAdapter(colorList, this, new ClickListener<Integer>() {
            @Override
            public void onClick(Integer data) {

                if (selectedTextView != null){

                    selectedTextView.setTextShadowColor(data.intValue());

                    saveBitmapUndu();
                }else {

                    Toast.makeText(context, "Select Text", Toast.LENGTH_SHORT).show();
                }



            }
        });
        binding.shadowColorRv.setAdapter(colorAdapter3);


    }

    private void saveImage(Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + ".png";

        // Get the cache directory for your app
        File cacheDir = context.getExternalCacheDir(); // External cache directory
        if (cacheDir == null) {
            cacheDir = context.getCacheDir(); // Fallback to internal cache directory if external is not available
        }

        // Create the file in the cache directory
        File file = new File(cacheDir, fileName);
        boolean success = false;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawColor(-1);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            createBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            createBitmap.recycle();
            fileOutputStream.flush();
            fileOutputStream.close();

            // Notify media scanner to scan the file
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, (str, uri) -> {
                // Optional: Handle scanned file URI if needed
            });

            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        if (success) {
            Intent intent = new Intent(context, SavePostActivity.class);
            intent.putExtra("uri", file.getAbsolutePath());
            intent.putExtra("type", type);
            startActivity(intent);
            dialogFrame.dismiss();
        } else {
            Util.showToast(context, getString(R.string.error));
        }
    }

    public void dialogFrame() {

        dialogFrame = new Dialog(context);
       dialogFrame.requestWindowFeature(1);
       dialogFrame.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
       dialogFrame.setContentView(R.layout.dialog_save_frame);
       dialogFrame.getWindow().setBackgroundDrawable(new ColorDrawable(0));
       dialogFrame.setCancelable(true);
       dialogFrame.setCanceledOnTouchOutside(true);

       LinearLayout ll_frame = dialogFrame.findViewById(R.id.ll_frame);
       LinearLayout ll_save = dialogFrame.findViewById(R.id.ll_save);

       ll_frame.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               saveImage(viewToBitmap(binding.mainRelativeLay));

           }
       });

       ll_save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               saveImageWithout(viewToBitmap(binding.mainRelativeLay), true);
           }
       });

        dialogFrame.show();
        Window window = dialogFrame.getWindow();
        window.setLayout(MATCH_PARENT, WRAP_CONTENT);

    }

    private void saveImageWithout(Bitmap bitmap, boolean save) {

        String fileName = System.currentTimeMillis() + ".png";
        String filePath = "";
        if (type.contains("greeting")) {


            filePath = Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                    + File.separator + "greeting" + File.separator + fileName;
        } else {

            filePath = Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                    + File.separator + fileName;
        }

        boolean success = false;

        if (!new File(filePath).exists()) {
            try {
                File file = null;
                if (type.contains("greeting")) {

                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + getResources().getString(R.string.app_name) + File.separator + "greeting");
                } else {

                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + getResources().getString(R.string.app_name));
                }

                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Toast.makeText(context,
                                getResources().getString(R.string.create_dir_err),
                                Toast.LENGTH_LONG).show();
                        success = false;
                    }
                }
                File file2 = new File(file.getAbsolutePath() + "/" + fileName);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), bitmap.getConfig());
                    Canvas canvas = new Canvas(createBitmap);
                    canvas.drawColor(-1);
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                    createBitmap.compress(Bitmap.CompressFormat.PNG,
                            100, fileOutputStream);
                    createBitmap.recycle();
                    fileOutputStream.flush();
                    fileOutputStream.close();


                    MediaScannerConnection.scanFile(context, new String[]{file2.getAbsolutePath()},
                            (String[]) null, (str, uri) -> {
                                StringBuilder sb = new StringBuilder();
                                sb.append("-> uri=");
                                sb.append(uri);
                                sb.append("-> FILE=");
                                sb.append(file2.getAbsolutePath());
                                Uri muri = Uri.fromFile(file2);
                            });
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }

            } catch (Exception e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (success) {


                if (save){
                    Intent intent = new Intent(context, ShareImageActivity.class);
                    intent.putExtra("uri", filePath);
                    startActivity(intent);
                }

                preferenceManager.setBoolean("save_image", true);

                dialogFrame.dismiss();

            } else {
                Util.showToast(context, getString(R.string.error));
            }

        }

    }

    ActivityResultLauncher<Intent> resultCallbackForGreeting = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            Uri resultUri = null;
                            if (cameraTempFile != null) {
                                resultUri = FileProvider.getUriForFile(context, getPackageName() + ".fileprovider", new File(cameraTempFile));
                            } else {
                                resultUri = result.getData().getData();
                            }

                            ImageView imageView = new ImageView(context);
                            Glide.with(context).load(resultUri).into(imageView);
                            binding.greetingZoomLay.addView(imageView);
                            binding.greetingZoomLay.setVisibility(VISIBLE);
                            binding.rlGreetings.setVisibility(GONE);

                        } catch (Exception e2) {
                            Toast.makeText(context, "" + e2.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
}
