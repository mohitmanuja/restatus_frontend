package com.growwthapps.dailypost.v2.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserInfo;
import com.google.gson.Gson;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ActivityLoginBinding;
import com.growwthapps.dailypost.v2.ui.dialog.UniversalDialog;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.NetworkConnectivity;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.utils.Util;
import com.growwthapps.dailypost.v2.viewmodel.HomeViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    public GoogleSignInClient mGoogleSignInClient;
    ActivityLoginBinding binding;
    FirebaseUser user;
    UniversalDialog universalDialog;
    ProgressDialog prgDialog;
    PreferenceManager preferenceManager;
    NetworkConnectivity networkConnectivity;

    String otpType = "phone";
    public static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public static PhoneAuthProvider.ForceResendingToken token;

    Activity context;
    private FirebaseAuth mAuth;
    String verificationId;
    HomeViewModel homeViewModel;
    boolean otpRequestCancelled = false;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    if (result.getData() != null) {

                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {

                                firebaseAuthWithGoogle(account);
                                Util.showLog("Google sign in success ");

                            }
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Util.showLog("Google sign in failed: " + e);
                            prgDialog.dismiss();

                        }
                    }
                } else {

                    prgDialog.dismiss();

                }
            });

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, 0);
            return insets;
        });
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));

        context = this;

        MyUtils.status_bar_light_white(context);

        homeViewModel = new HomeViewModel();

//        binding.countryRegister.setOnClickListener(v -> opencountry());

        preferenceManager = new PreferenceManager(this);
        universalDialog = new UniversalDialog(this, false);
        prgDialog = new ProgressDialog(this);
        networkConnectivity = new NetworkConnectivity(this);
        prgDialog.setMessage(getResources().getString(R.string.login_loading));
        prgDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            //
        };

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        String logoutRedirect = getIntent().getStringExtra(Constant.LOGIN_TYPE);
        if (logoutRedirect != null) {
            if (logoutRedirect.equalsIgnoreCase(Constant.GOOGLE)) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, task -> {
                            Toast.makeText(LoginActivity.this, "Logout", Toast.LENGTH_SHORT).show();

                            preferenceManager.setBoolean(Constant.IS_LOGIN, false);
                            preferenceManager.remove(Constant.USER_ID);

                        });
            }
        }


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                if (!otpRequestCancelled && context != null && !preferenceManager.getBoolean(Constant.IS_LOGIN)) {
                    Toast.makeText(context, "OTP Timeout, Please Re-generate the OTP Again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;

                binding.numberLay.setVisibility(View.GONE);
                binding.ivGoogle.setVisibility(View.GONE);
                binding.llText.setVisibility(View.GONE);
                binding.otpLay.setVisibility(View.VISIBLE);
                binding.tvOtp.setText("" + binding.countryRegister.selectedCountryCode() + " " + binding.numberEt.getText().toString());
                binding.txtTabLogin.setText(R.string.otp_verify);

                prgDialog.dismiss();

                Toast.makeText(context, context.getString(R.string.otp_send_success), Toast.LENGTH_SHORT).show();

                startCountdown();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                e.printStackTrace();
                prgDialog.dismiss();

                Log.d("number", "onVerificationFailed: " + e.getMessage());
            }
        };


/*
        addLoginInfo(null, null, "7990928652", "", Constant.PHONE);
*/

        initUi();
    }

    @Override
    public void onBackPressed() {

        if (binding.layoutLogin.getVisibility() == View.GONE) {
            binding.layoutLogin.setVisibility(View.VISIBLE);
            binding.layoutLogin.startAnimation(Constant.getAnimUp(context));
            binding.editTvLayout.setVisibility(View.GONE);
            binding.numberEt.setText("");
            binding.otpEdit.setText("");
            binding.otpLay.setVisibility(View.GONE);
            binding.numberLay.setVisibility(View.VISIBLE);

        } else {

            super.onBackPressed();
        }

    }

    private void initUi() {

        binding.phoneBtn.setOnClickListener(view1 -> {
            otpType = "phone";
            binding.editTvLayout.setVisibility(View.VISIBLE);
            binding.layoutLogin.setVisibility(View.GONE);
        });

        binding.whatsappBtn.setOnClickListener(view1 -> {
            binding.titleTv.setText(R.string.enter_wp_number);
            otpType = "whatsapp";
            binding.editTvLayout.setVisibility(View.VISIBLE);
            binding.layoutLogin.setVisibility(View.GONE);
        });

        binding.ivGoogle.setOnClickListener(v -> {
            if (!networkConnectivity.isConnected()) {
                Util.showToast(this, getString(R.string.error_message__no_internet));
                return;
            }

            signIn();

        });

        binding.recentBtn.setOnClickListener(view1 -> {
            if (binding.numberEt.getText().toString().length() > 9) {

                binding.otpEdit.setText("");

                if (otpType.equals("phone")) {

                    prgDialog.show();

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(binding.countryRegister.selectedCountryCode() + binding.numberEt.getText().toString())       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(this)                 // (optional) Activity for callback binding
                                    // If no activity is passed, reCAPTCHA verification can not be used.
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                } else {
//                    getWhatsappOtp(ccp.getDefaultCountryCodeWithPlus() + binding.numberEt.getText().toString());
                }
            } else {
                Util.showToast(context, getString(R.string.please_enter_correct_number));
                prgDialog.dismiss();
            }
        });

        binding.ivEditNum.setOnClickListener(v -> {
            otpRequestCancelled = true;

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            sec = 60;
            isTimerRunning = false;
            binding.recentBtn.setText(getString(R.string.resend_otp));
            binding.recentBtn.setEnabled(true);

            binding.numberLay.setVisibility(View.VISIBLE);
            binding.otpLay.setVisibility(View.GONE);
            binding.ivGoogle.setVisibility(View.VISIBLE);
            binding.llText.setVisibility(View.VISIBLE);
            binding.txtTabLogin.setText(R.string.login_signup);


        });

        binding.otpBtn.setEnabled(false);

        binding.numberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.otpBtn.setEnabled(s.length() > 9);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.otpBtn.setOnClickListener(v -> {

            if (binding.numberEt.getText().toString().length() > 9) {
                prgDialog.show();
                if (otpType.equals("phone")) {

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(binding.countryRegister.selectedCountryCode() + binding.numberEt.getText().toString())       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(this)                 // (optional) Activity for callback binding
                                    // If no activity is passed, reCAPTCHA verification can not be used.
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                } else {
                    prgDialog.show();
//                    getWhatsappOtp("+91" + binding.numberEt.getText().toString());


                }
            } else {
                Util.showToast(context, getString(R.string.please_enter_correct_number));
                prgDialog.dismiss();
            }
        });

        binding.submitBtn.setOnClickListener(v -> {
            String otp = binding.otpEdit.getText().toString();

            int totalBoxes = 6;
            int enteredLength = otp.length();

            if (enteredLength < totalBoxes) {
                // Show full OTP error visually
                binding.otpEdit.setItemBackground(getResources().getDrawable(R.drawable.bg_pin_error));
                binding.otpEdit.setLineColor(ContextCompat.getColor(this, R.color.holo_red_light2));

                // Show toast
                Toast.makeText(context, getString(R.string.enter_incorrect_otp), Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    binding.otpEdit.setItemBackground(getResources().getDrawable(R.drawable.bg_pin_default));
                    binding.otpEdit.setLineColor(ContextCompat.getColor(this, R.color.gray));
                }, 2200);

                return;
            }

            if (otpType.equals("phone")) {
                verifyAuth(PhoneAuthProvider.getCredential(verificationId, otp));
            }
        });


        binding.otpEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Reset to normal background on any change
                binding.otpEdit.setItemBackground(getResources().getDrawable(R.drawable.bg_pin_default));
                binding.otpEdit.setLineColor(ContextCompat.getColor(context, R.color.gray));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void signIn() {


        safeShowDialog(prgDialog);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);

    }

    @Override
    protected void onDestroy() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.dismiss();
        }
        super.onDestroy();
    }

    private void safeShowDialog(Dialog dialog) {
        if (!isFinishing() && !isDestroyed()) {
            dialog.show();
        }
    }




//    @SuppressLint("WrongConstant")
//    public void opencountry() {
//        ccp.showCountryCodePickerDialog();
//        ccp.setOnCountryChangeListener(selectedCountry -> {
//        });
//    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAuth != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        if (user != null) {
                            List<? extends UserInfo> userInfoList = user.getProviderData();

                            String email = "";
                            String uid = "";
                            String displayName = "";
                            String photoUrl = "";
                            String mobile = "";
                            for (int i = 0; i < userInfoList.size(); i++) {

                                email = userInfoList.get(i).getEmail();

                                if (email != null && !email.equals("")) {
                                    uid = userInfoList.get(i).getUid();
                                    displayName = userInfoList.get(i).getDisplayName();
                                    photoUrl = String.valueOf(userInfoList.get(i).getPhotoUrl());
                                    mobile = userInfoList.get(i).getPhoneNumber();
                                    break;
                                }

                            }

//                            addLoginInfo(email, displayName, photoUrl, mobile, Constant.GOOGLE);
//                            Toast.makeText(this, "mail:" + email, Toast.LENGTH_SHORT).show();

                            homeViewModel.userLogin("email", email, "").observe(this, loginModel -> {

                                if (loginModel != null) {

                                    preferenceManager.setBoolean(Constant.IS_LOGIN, true);

                                    preferenceManager.setString(Constant.SUBSCRIPTION_ID, loginModel.data.subscription_id);
                                    preferenceManager.setString(Constant.SUBSCRIPTION_START_DATE, loginModel.data.subscription_start_date);
                                    preferenceManager.setString(Constant.PLAN_EXPIRED, loginModel.data.subscription_end_date);
                                    preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, loginModel.data.is_subscribed);

                                    preferenceManager.setString(Constant.USER_EMAIL, loginModel.data.email);
                                    preferenceManager.setString(Constant.USER_NAME, loginModel.data.name);
                                    preferenceManager.setString(Constant.USER_PHONE, loginModel.data.mobile);
                                    preferenceManager.setString(Constant.USER_IMAGE, loginModel.data.logo);
                                    preferenceManager.setString(Constant.USER_DESIGNATION, loginModel.data.designation);
                                    preferenceManager.setString(Constant.USER_ID, loginModel.data.user_id);
                                    preferenceManager.setString(Constant.LOGIN_TYPE, loginModel.data.login_type);
                                    preferenceManager.setString(Constant.USER_SOCIAL_MEDIA, loginModel.data.socialmedia1);
                                    preferenceManager.setString(Constant.USER_SOCIAL_MEDIA_TYPE, loginModel.data.socialmedia2);


                                    preferenceManager.setString(Constant.BUSINESS_NAME, loginModel.data.business_name);
                                    preferenceManager.setString(Constant.BUSINESS_ADDRESS, loginModel.data.business_address);
                                    preferenceManager.setString(Constant.BUSINESS_DETAIL, loginModel.data.business_about);
                                    preferenceManager.setString(Constant.BUSINESS_MOBILE, loginModel.data.business_mobile);
                                    preferenceManager.setString(Constant.BUSSINESS_INSTAGRAM, loginModel.data.business_socialmedia_value);
                                    preferenceManager.setString(Constant.BUSINESS_LOGO, loginModel.data.logo);
                                    preferenceManager.setString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE, loginModel.data.business_socialmedia_type);

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }

                            });


                        } else {
                            // Error Message
                            Toast.makeText(this, getString(R.string.login__fail_account), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // display message to user when it fails
                        Toast.makeText(this, getString(R.string.login__fail), Toast.LENGTH_LONG).show();
                        String email = user.getEmail();
                        handleFirebaseAuthError(email);

                    }
                });
    }

    private void handleFirebaseAuthError(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SignInMethodQueryResult result = task.getResult();
                List<String> signInMethod = result.getSignInMethods();

                Util.showLog("SignInMethod  =" + signInMethod);
                if (signInMethod.contains(Constant.EMAILAUTH)) {
                    universalDialog.showErrorDialog("[" + email + "]" + getString(R.string.login__auth_email), getString(R.string.ok));
                    universalDialog.show();
                } else if (signInMethod.contains(Constant.GOOGLEAUTH)) {
                    universalDialog.showErrorDialog("[" + email + "]" + getString(R.string.login__auth_google), getString(R.string.ok));
                    universalDialog.show();
                }
            }
        });
    }

    String whatsappOTP;


    int sec = 60;
    boolean isTimerRunning = false;
    CountDownTimer countDownTimer;

    private void startCountdown() {
        sec = 60;
        isTimerRunning = true;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isTimerRunning) {
                    sec--;
                    binding.recentBtn.setEnabled(false);
                    binding.recentBtn.setText(" (" + sec + ")");
                }
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                if (context != null) {
                    binding.recentBtn.setText(getString(R.string.resend_otp));
                    binding.recentBtn.setEnabled(true);
                }
            }
        }.start();
    }


    private void verifyAuth(PhoneAuthCredential credential) {
        safeShowDialog(prgDialog);

        mAuth.signInWithCredential(credential).addOnCompleteListener(context, task -> {

            if (task.isSuccessful()) {
                user = mAuth.getCurrentUser();
                homeViewModel.userLogin("mobile", "", user.getPhoneNumber()).observe(this, loginModel -> {

                    Log.d("login111", "verifyAuth: "+ new Gson().toJson(loginModel));

                    if (loginModel != null) {

                        preferenceManager.setBoolean(Constant.IS_LOGIN, true);

                        preferenceManager.setString(Constant.SUBSCRIPTION_ID, loginModel.data.subscription_id);
                        preferenceManager.setString(Constant.SUBSCRIPTION_START_DATE, loginModel.data.subscription_start_date);
                        preferenceManager.setString(Constant.PLAN_EXPIRED, loginModel.data.subscription_end_date);
                        preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, loginModel.data.is_subscribed);

                        preferenceManager.setString(Constant.USER_EMAIL, loginModel.data.email);
                        preferenceManager.setString(Constant.USER_NAME, loginModel.data.name);
                        preferenceManager.setString(Constant.USER_PHONE, loginModel.data.mobile);
                        preferenceManager.setString(Constant.USER_IMAGE, loginModel.data.logo);
                        preferenceManager.setString(Constant.USER_DESIGNATION, loginModel.data.designation);
                        preferenceManager.setString(Constant.USER_ID, loginModel.data.user_id);
                        preferenceManager.setString(Constant.LOGIN_TYPE, loginModel.data.login_type);
                        preferenceManager.setString(Constant.USER_SOCIAL_MEDIA, loginModel.data.socialmedia1);
                        preferenceManager.setString(Constant.USER_SOCIAL_MEDIA_TYPE, loginModel.data.socialmedia2);


                        preferenceManager.setString(Constant.BUSINESS_NAME, loginModel.data.business_name);
                        preferenceManager.setString(Constant.BUSINESS_ADDRESS, loginModel.data.business_address);
                        preferenceManager.setString(Constant.BUSINESS_DETAIL, loginModel.data.business_about);
                        preferenceManager.setString(Constant.BUSINESS_MOBILE, loginModel.data.business_mobile);
                        preferenceManager.setString(Constant.BUSSINESS_INSTAGRAM, loginModel.data.business_socialmedia_value);
                        preferenceManager.setString(Constant.BUSINESS_LOGO, loginModel.data.logo);
                        preferenceManager.setString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE, loginModel.data.business_socialmedia_type);

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                });

            } else {


                binding.otpEdit.setItemBackground(getResources().getDrawable(R.drawable.bg_pin_error));
                binding.otpEdit.setLineColor(ContextCompat.getColor(this, R.color.holo_red_light));

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    binding.otpEdit.setItemBackground(getResources().getDrawable(R.drawable.bg_pin_default));
                    binding.otpEdit.setLineColor(ContextCompat.getColor(this, R.color.gray));
                }, 2200);

                Toast.makeText(context, "Incorrect OTP !", Toast.LENGTH_SHORT).show();
                prgDialog.dismiss();

            }
        });
    }

}