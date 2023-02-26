package com.example.dietplan.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.example.dietplan.R;
import com.example.dietplan.activity.MainActivity;
import com.example.dietplan.interfaces.FavouriteIF;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.response.FavouriteRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.login.LoginManager;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.ads.interstitial.InterstitialAd.load;

public class Method {

    public Activity activity;
    private OnClick onClick;
    public static boolean loginBack = false;
    public static boolean personalization_ad = false;

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "DietPlan";
    public String pref_login = "pref_login";
    private String firstTime = "firstTime";
    public String profileId = "profileId";
    public String loginType = "loginType";
    public String show_login = "show_login";
    public String themSetting = "them";
    public String notification = "notification";

    public Method(Activity activity) {
        this.activity = activity;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public Method(Activity activity, OnClick onClick) {
        this.activity = activity;
        this.onClick = onClick;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public void login() {
        if (!pref.getBoolean(firstTime, false)) {
            editor.putBoolean(pref_login, false);
            editor.putBoolean(firstTime, true);
            editor.commit();
        }
    }

    //user login or not
    public boolean isLogin() {
        return pref.getBoolean(pref_login, false);
    }

    //get login type
    public String getLoginType() {
        return pref.getString(loginType, "");
    }

    //get user id
    public String userId() {
        return pref.getString(profileId, "");
    }

    //rtl
    public void forceRTLIfSupported() {
        if (activity.getResources().getString(R.string.isRTL).equals("true")) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public String themMode() {
        return pref.getString(themSetting, "system");
    }

    //rtl or not
    public boolean isRtl() {
        return activity.getResources().getString(R.string.isRTL).equals("true");
    }

    public void changeStatusBarColor() {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    //get device id
    @SuppressLint("HardwareIds")
    public String getDevice() {
        String deviceId;
        try {
            deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            deviceId = "Not Found";
        }
        return deviceId;
    }

    //network check
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //screen get height and width
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        point.x = display.getWidth();
        point.y = display.getHeight();
        columnWidth = point.x;
        return columnWidth;
    }

    //add to favourite
    public void addToFav(String id, String userId, String type, int position, FavouriteIF favouriteIF) {

        ProgressDialog progressDialog = new ProgressDialog(activity);

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("id", id);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("type", type);
        jsObj.addProperty("method_name", "add_favourite");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<FavouriteRP> call = apiService.isFavouriteOrNot(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<FavouriteRP>() {
            @Override
            public void onResponse(@NotNull Call<FavouriteRP> call, @NotNull Response<FavouriteRP> response) {

                try {
                    FavouriteRP favouriteRP = response.body();
                    assert favouriteRP != null;

                    if (favouriteRP.getStatus().equals("1")) {
                        if (favouriteRP.getSuccess().equals("1")) {
                            favouriteIF.isFavourite(favouriteRP.isIs_favourite(), favouriteRP.getMsg());
                        } else {
                            alertBox(favouriteRP.getMsg());
                        }
                    } else if (favouriteRP.getStatus().equals("2")) {
                        suspend(favouriteRP.getMessage());
                    } else {
                        alertBox(favouriteRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<FavouriteRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressDialog.dismiss();
                alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public boolean isAdmobFBAds() {
        return Constant.appRP.getBanner_ad_type().equals(Constant.AD_TYPE_ADMOB) ||
                Constant.appRP.getInterstitial_ad_type().equals(Constant.AD_TYPE_ADMOB) ||
//                Constant.appRP.getNative_ad_type().equals(Constant.AD_TYPE_ADMOB) ||
                Constant.appRP.getBanner_ad_type().equals(Constant.AD_TYPE_FACEBOOK) ||
                Constant.appRP.getInterstitial_ad_type().equals(Constant.AD_TYPE_FACEBOOK);
//                Constant.appRP.getNative_ad_type().equals(Constant.AD_TYPE_FACEBOOK);
    }

    public boolean isStartAppAds() {
        return Constant.appRP.getBanner_ad_type().equals(Constant.AD_TYPE_STARTAPP) ||
                Constant.appRP.getInterstitial_ad_type().equals(Constant.AD_TYPE_STARTAPP);
//                Constant.appRP.getNative_ad_type().equals(Constant.AD_TYPE_STARTAPP);
    }

    public boolean isApplovinAds() {
        return Constant.appRP.getBanner_ad_type().equals(Constant.AD_TYPE_APPLOVIN) ||
                Constant.appRP.getInterstitial_ad_type().equals(Constant.AD_TYPE_APPLOVIN);
//                Constant.appRP.getNative_ad_type().equals(Constant.AD_TYPE_APPLOVIN);
    }

    public void initializeAds() {
        if (isAdmobFBAds()) {
            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }

        if (isStartAppAds()) {
            StartAppSDK.init(activity, Constant.appRP.getStartapp_app_id(), false);
            StartAppAd.disableSplash();
        }

        if (isApplovinAds()) {
            if (!AppLovinSdk.getInstance(activity).isInitialized()) {
                AppLovinSdk.initializeSdk(activity);
                AppLovinSdk.getInstance(activity).setMediationProvider("max");
                AppLovinSdk.getInstance(activity).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("bb6822d9-18de-41b0-994e-41d4245a4d63", "749d75a2-1ef2-4ff9-88a5-c50374843ac6, 91c3aa6b-666c-46dc-a503-d16c7cd9a64a"));
            }
        }
    }

    //---------------Interstitial Ad---------------//

    public void click(final int position, final String type, final String otherType, String title, final String id, final String videoType, final String videoLink) {

        ProgressDialog progressDialog = new ProgressDialog(activity);

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        if (videoType.equals("youtube")) {
            progressDialog.dismiss();
            onClick.click(position, type, otherType, title, id, videoType, videoLink);
        } else {
            if (Constant.appRP != null) {

                if (Constant.appRP.isInterstitial_ad()) {

                    Constant.AD_COUNT = Constant.AD_COUNT + 1;
                    if (Constant.AD_COUNT == Constant.interstitialAdShow) {
                        Constant.AD_COUNT = 0;

                        switch (Constant.appRP.getInterstitial_ad_type()) {
                            case Constant.AD_TYPE_ADMOB:
                            case Constant.AD_TYPE_FACEBOOK:

                                AdRequest.Builder builder = new AdRequest.Builder();
                                if (personalization_ad) {
                                    Bundle extras = new Bundle();
                                    extras.putString("npa", "1");
                                    builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                                }
                                Constant.AD_COUNT = 0;
                                load(activity, Constant.appRP.getInterstitial_ad_id(), builder.build(), new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                        super.onAdLoaded(interstitialAd);
                                        interstitialAd.show(activity);
                                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                            @Override
                                            public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                                super.onAdFailedToShowFullScreenContent(adError);
                                                progressDialog.dismiss();
                                                onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                            }

                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                super.onAdDismissedFullScreenContent();
                                                progressDialog.dismiss();
                                                onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        progressDialog.dismiss();
                                        onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                    }
                                });
                                break;

                            case Constant.AD_TYPE_STARTAPP:
                                Constant.AD_COUNT = 0;
                                StartAppAd startAppAd = new StartAppAd(activity);
                                startAppAd.loadAd(StartAppAd.AdMode.FULLPAGE, new AdEventListener() {
                                    @Override
                                    public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                        startAppAd.showAd(new AdDisplayListener() {
                                            @Override
                                            public void adHidden(com.startapp.sdk.adsbase.Ad ad) {
                                                progressDialog.dismiss();
                                                onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                            }

                                            @Override
                                            public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                                                progressDialog.dismiss();
                                            }

                                            @Override
                                            public void adClicked(com.startapp.sdk.adsbase.Ad ad) {
                                                progressDialog.dismiss();
                                            }

                                            @Override
                                            public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                                                progressDialog.dismiss();
                                                onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailedToReceiveAd(@Nullable com.startapp.sdk.adsbase.Ad ad) {
                                        progressDialog.dismiss();
                                        onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                    }
                                });
                                break;

                            case Constant.AD_TYPE_APPLOVIN:
                                Constant.AD_COUNT = 0;
                                MaxInterstitialAd interstitialAd = new MaxInterstitialAd(Constant.appRP.getInterstitial_ad_id(), activity);
                                interstitialAd.loadAd();
                                interstitialAd.setListener(new MaxAdListener() {

                                    @Override
                                    public void onAdLoaded(MaxAd ad) {
                                        interstitialAd.showAd();
                                    }

                                    @Override
                                    public void onAdDisplayed(MaxAd ad) {

                                    }

                                    @Override
                                    public void onAdHidden(MaxAd ad) {
                                        progressDialog.dismiss();
                                        onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                    }

                                    @Override
                                    public void onAdClicked(MaxAd ad) {

                                    }

                                    @Override
                                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                                        progressDialog.dismiss();
                                        onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                    }

                                    @Override
                                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                        progressDialog.dismiss();
                                        onClick.click(position, type, otherType, title, id, videoType, videoLink);
                                    }
                                });
                                break;
                        }
                    } else {
                        progressDialog.dismiss();
                        onClick.click(position, type, otherType, title, id, videoType, videoLink);
                    }

                } else {
                    progressDialog.dismiss();
                    onClick.click(position, type, otherType, title, id, videoType, videoLink);
                }
            } else {
                progressDialog.dismiss();
                onClick.click(position, type, otherType, title, id, videoType, videoLink);
            }
        }
    }


    //---------------Interstitial Ad---------------//

    //---------------Banner Ad---------------//

    public void showBannerAd(LinearLayout linearLayout) {
        if (Constant.appRP != null) {
            if (Constant.appRP.isBanner_ad()) {
                switch (Constant.appRP.getBanner_ad_type()) {
                    case Constant.AD_TYPE_ADMOB:
                    case Constant.AD_TYPE_FACEBOOK:
                        AdView mAdView = new AdView(activity);
                        mAdView.setAdSize(AdSize.BANNER);
                        mAdView.setAdUnitId(Constant.appRP.getBanner_ad_id());
                        AdRequest.Builder builder = new AdRequest.Builder();
                        if (!personalization_ad) {
                            // load non Personalized ads
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                        } // else do nothing , it will load PERSONALIZED ads
                        mAdView.loadAd(builder.build());
                        linearLayout.addView(mAdView);
                        linearLayout.setGravity(Gravity.CENTER);
                        break;
                    case Constant.AD_TYPE_STARTAPP:
                        Banner startAppBanner = new Banner(activity);
                        startAppBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        linearLayout.addView(startAppBanner);
                        startAppBanner.loadAd();
                        break;
                    case Constant.AD_TYPE_APPLOVIN:
                        MaxAdView adView = new MaxAdView(Constant.appRP.getBanner_ad_id(), activity);
                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                        int heightPx = activity.getResources().getDimensionPixelSize(R.dimen.banner_height);
                        adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                        linearLayout.addView(adView);
                        adView.loadAd();
                        break;
                }
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    //---------------Banner Ad---------------//

    public String getTempUploadPath(Uri uri) {
        File root = activity.getExternalCacheDir().getAbsoluteFile();
        try {
            String filePath = root.getPath() + File.separator + System.currentTimeMillis() + ".jpg";

            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            Bitmap bm = BitmapFactory.decodeStream(inputStream);

            if (saveBitMap(root, bm, filePath)) {
                return filePath;
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    private boolean saveBitMap(File root, Bitmap Final_bitmap, String filePath) {
        if (!root.exists()) {
            boolean isDirectoryCreated = root.mkdirs();
            if (!isDirectoryCreated)
                return false;
        }
        String filename = filePath;
        File pictureFile = new File(filename);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            Final_bitmap.compress(Bitmap.CompressFormat.PNG, 18, oStream);
            oStream.flush();
            oStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //account suspend
    public void suspend(String message) {

        if (isLogin()) {

            String typeLogin = getLoginType();
            assert typeLogin != null;
            if (typeLogin.equals("google")) {

                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(activity, task -> {

                        });
            } else if (typeLogin.equals("facebook")) {
                LoginManager.getInstance().logOut();
            }

            editor.putBoolean(pref_login, false);
            editor.commit();
            Events.Login loginNotify = new Events.Login("");
            GlobalBus.getBus().post(loginNotify);
        }

        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                    builder.setMessage(Html.fromHtml(message));
                    builder.setCancelable(false);
                    builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                            (arg0, arg1) -> {
                                activity.startActivity(new Intent(activity, MainActivity.class));
                                activity.finishAffinity();
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }

    }

    //---------------Alert Box---------------//

    //alert message box
    public void alertBox(String message) {

        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                    builder.setMessage(Html.fromHtml(message));
                    builder.setCancelable(false);
                    builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                            (arg0, arg1) -> {

                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }

    }

    //---------------Alert Box---------------//


    public String webViewText() {
        String color;
        if (isDarkMode()) {
            color = Constant.webViewTextDark;
        } else {
            color = Constant.webViewText;
        }
        return color;
    }

    public String webViewLink() {
        String color;
        if (isDarkMode()) {
            color = Constant.webViewLinkDark;
        } else {
            color = Constant.webViewLink;
        }
        return color;
    }

    public String isWebViewTextRtl() {
        String isRtl;
        if (isRtl()) {
            isRtl = "rtl";
        } else {
            isRtl = "ltr";
        }
        return isRtl;
    }

    //check dark mode or not
    public boolean isDarkMode() {
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return true;
            default:
                return false;
        }
    }

}
