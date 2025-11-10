# ===============================
# Keep important app classes
# ===============================

-keep class com.growwthapps.dailypost.v2.model.** { *; }
-keep class com.growwthapps.dailypost.v2.viewmodel.** { *; }
-keep class com.growwthapps.dailypost.v2.respository.** { *; }
-keep class com.growwthapps.dailypost.v2.api.** { *; }
-keep class com.growwthapps.dailypost.v2.ui.adapters.GreetingAdapter { *; }

-keep class com.growwthapps.dailypost.v2.RoundedImageView { *; }
-keep class com.growwthapps.dailypost.v2.ToggleImageButton { *; }

# ===============================
# Keep Huawei dependencies
# ===============================
-keep class com.huawei.** { *; }

# ===============================
# Keep Yalantis uCrop (Image cropping)
# ===============================
-dontwarn com.yalantis.ucrop.**
-keep class com.yalantis.ucrop.** { *; }
-keep interface com.yalantis.ucrop.** { *; }

# ===============================
# Keep JSON Parsing (Gson)
# ===============================
-keep class com.google.gson.** { *; }
-keep public class com.google.gson.** { public *; }

# ===============================
# Keep Firebase & Google Play Services
# ===============================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# ===============================
# Keep RecyclerView ViewHolders & Adapters
# ===============================
-keepclassmembers class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder {
    public <init>(android.view.View);
}

-keep class * extends androidx.recyclerview.widget.RecyclerView$Adapter { *; }

# ===============================
# Keep Parcelable model classes
# ===============================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ===============================
# Keep Retrofit, OkHttp & Networking
# ===============================
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keepattributes *Annotation*
-dontwarn com.squareup.okhttp.**
-dontwarn retrofit2.**
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**

# Keep Retrofit annotations
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}

# ===============================
# Keep Activities, Services, BroadcastReceivers, and ContentProviders
# ===============================
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# ===============================
# Keep all Android Fragments
# ===============================
-keep class * extends androidx.fragment.app.Fragment { *; }

# ===============================
# Keep Reflection-Based Classes
# ===============================
-keepattributes Signature
-keepattributes *Annotation*

-keep @interface androidx.annotation.Keep
-keep class * {
    @androidx.annotation.Keep *;
}

# ===============================
# Keep ViewBinding & Synthetic Access
# ===============================
-keep class * extends androidx.viewbinding.ViewBinding { *; }

# ===============================
# Keep Media-related classes
# ===============================
-keep class android.support.v4.media.** { *; }
-keep class androidx.media.** { *; }

# ===============================
# Keep all classes used in JSON serialization
# ===============================
-keep class com.growwthapps.dailypost.model.** { *; }

# ===============================
# Suppress warnings for missing annotations
# ===============================
-dontwarn proguard.annotation.Keep
-dontwarn proguard.annotation.KeepClassMembers

-dontwarn com.google.android.apps.nbu.paisa.inapp.client.api.PaymentsClient
-dontwarn com.google.android.apps.nbu.paisa.inapp.client.api.Wallet
-dontwarn com.google.android.apps.nbu.paisa.inapp.client.api.WalletUtils

-dontwarn io.michaelrocks.libphonenumber.android.NumberParseException
-dontwarn io.michaelrocks.libphonenumber.android.PhoneNumberUtil$PhoneNumberFormat
-dontwarn io.michaelrocks.libphonenumber.android.PhoneNumberUtil$PhoneNumberType
-dontwarn io.michaelrocks.libphonenumber.android.PhoneNumberUtil
-dontwarn io.michaelrocks.libphonenumber.android.Phonenumber$PhoneNumber
