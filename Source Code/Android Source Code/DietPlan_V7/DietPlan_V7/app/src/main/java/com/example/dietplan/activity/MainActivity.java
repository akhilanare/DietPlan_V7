package com.example.dietplan.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.example.dietplan.BuildConfig;
import com.example.dietplan.R;
import com.example.dietplan.fragment.BmiCalculatorFragment;
import com.example.dietplan.fragment.CategoryFragment;
import com.example.dietplan.fragment.DietPlanDetailFragment;
import com.example.dietplan.fragment.DietPlanFragment;
import com.example.dietplan.fragment.FavouriteFragment;
import com.example.dietplan.fragment.FitnessVideoFragment;
import com.example.dietplan.fragment.HomeFragment;
import com.example.dietplan.fragment.ProfileFragment;
import com.example.dietplan.fragment.SettingFragment;
import com.example.dietplan.response.AppRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.example.dietplan.util.API;
import com.example.dietplan.util.Constant;
import com.example.dietplan.util.Events;
import com.example.dietplan.util.GlobalBus;
import com.example.dietplan.util.Method;
import com.facebook.login.LoginManager;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Method method;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    public static MaterialToolbar toolbar;
    private InputMethodManager imm;
    private ConsentForm form;
    private LinearLayout linearLayout;
    private String id = "", title = "", type = "";
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "{your-package-name}", //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        GlobalBus.getBus().register(this);

        method = new Method(MainActivity.this);
        method.forceRTLIfSupported();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id");
            title = intent.getStringExtra("title");
            type = intent.getStringExtra("type");
        }

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        setSupportActionBar(toolbar);

        linearLayout = findViewById(R.id.linearLayout_main);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.video).setVisible(false);
        checkLogin();

        navigationView.setNavigationItemSelectedListener(this);

        if (method.isNetworkAvailable()) {
            appDetail();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    String title;
                    if (!(getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount()) instanceof SupportRequestManagerFragment)) {
                        title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount()).getTag();
                    } else {
                        title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1).getTag();
                    }
                    if (title != null) {
                        toolbar.setTitle(title);
                    }
                    super.onBackPressed();
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getResources().getString(R.string.Please_click_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {

        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        // Handle navigation view item clicks here.
        //Checking if the item is in checked state or not, if not make it in checked state
        item.setChecked(!item.isChecked());

        //Closing drawer on item click
        drawer.closeDrawers();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.home:
                backStackRemove();
                selectDrawerItem(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                        getResources().getString(R.string.home)).commitAllowingStateLoss();
                return true;

            case R.id.categories:
                backStackRemove();
                selectDrawerItem(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new CategoryFragment(),
                        getResources().getString(R.string.category)).commitAllowingStateLoss();
                return true;

            case R.id.favorite:
                backStackRemove();
                selectDrawerItem(2);
                if (Constant.appRP != null) {
                    if (Constant.appRP.getVideo_status().equals("true")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new FavouriteFragment(),
                                getResources().getString(R.string.favourite)).commitAllowingStateLoss();
                    } else {
                        favouriteDiet();
                    }
                } else {
                    favouriteDiet();
                }
                return true;

            case R.id.bmi:
                backStackRemove();
                selectDrawerItem(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new BmiCalculatorFragment(),
                        getResources().getString(R.string.bmi_calculator)).commitAllowingStateLoss();
                return true;

            case R.id.video:
                backStackRemove();
                selectDrawerItem(4);
                FitnessVideoFragment fitnessVideoFragment = new FitnessVideoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "videoHome");
                bundle.putString("title", getResources().getString(R.string.fitness_video));
                fitnessVideoFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, fitnessVideoFragment,
                        getResources().getString(R.string.fitness_video)).commitAllowingStateLoss();
                return true;

            case R.id.profile:
                backStackRemove();
                selectDrawerItem(5);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new ProfileFragment(),
                        getResources().getString(R.string.profile)).commitAllowingStateLoss();
                return true;

            case R.id.setting:
                backStackRemove();
                selectDrawerItem(6);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new SettingFragment(),
                        getResources().getString(R.string.setting)).commitAllowingStateLoss();
                return true;

            case R.id.login:
                deselectDrawerItem(7);
                if (method.isLogin()) {
                    logout();
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finishAffinity();
                }
                return true;

            default:
                return true;
        }
    }

    public void selectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    public void deselectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setCheckable(false);
        navigationView.getMenu().getItem(position).setChecked(false);
    }

    public void backStackRemove() {
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void favouriteDiet() {
        DietPlanFragment dietPlanFragment = new DietPlanFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", "");
        bundle.putString("title", getResources().getString(R.string.favourite));
        bundle.putString("type", "favDietHome");
        dietPlanFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, dietPlanFragment,
                getResources().getString(R.string.favourite)).commitAllowingStateLoss();
    }

    public void appDetail() {

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("method_name", "get_app_details");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AppRP> call = apiService.getAppData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AppRP>() {
            @Override
            public void onResponse(@NotNull Call<AppRP> call, @NotNull Response<AppRP> response) {

                try {

                    Constant.appRP = response.body();
                    assert Constant.appRP != null;

                    method.initializeAds();

                    if (Constant.appRP.getStatus().equals("1")) {

                        navigationView.getMenu().findItem(R.id.video).setVisible(Constant.appRP.getVideo_status().equals("true"));

                        if (Constant.appRP.isApp_update_status() && Constant.appRP.getApp_new_version() > BuildConfig.VERSION_CODE) {
                            showAppDialog(Constant.appRP.getApp_update_desc(),
                                    Constant.appRP.getApp_redirect_url(),
                                    Constant.appRP.isCancel_update_status());
                        }

                        if (Constant.appRP.getInterstitial_ad_click().equals("")) {
                            Constant.interstitialAdShow = 0;
                        } else {
                            Constant.interstitialAdShow = Integer.parseInt(Constant.appRP.getInterstitial_ad_click());
                        }

                        if (Constant.appRP.getNative_ad_position().equals("")) {
                            Constant.nativeAdPos = 0;
                        } else {
                            Constant.nativeAdPos = Integer.parseInt(Constant.appRP.getNative_ad_position());
                        }

                        if(method.isAdmobFBAds()) {
                            checkForConsent();
                        } else {
                            method.showBannerAd(linearLayout);
                        }

                        try {

                            if (type.equals("category")) {
                                DietPlanFragment dietPlanFragment = new DietPlanFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
                                bundle.putString("title", title);
                                bundle.putString("type", type);
                                dietPlanFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, dietPlanFragment,
                                        title).commitAllowingStateLoss();
                            } else if (type.equals("diet") || type.equals("deep_link")) {
                                DietPlanDetailFragment dietPlanDetailFragment = new DietPlanDetailFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
                                bundle.putString("title", title);
                                dietPlanDetailFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, dietPlanDetailFragment,
                                        title).commitAllowingStateLoss();
                            } else {
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                                        getResources().getString(R.string.home)).commitAllowingStateLoss();
                                selectDrawerItem(0);
                            }


                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        method.alertBox(Constant.appRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(@NotNull Call<AppRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void checkForConsent() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {Constant.appRP.getPublisher_id()};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Method.personalization_ad = true;
                        method.showBannerAd(linearLayout);
                        break;
                    case NON_PERSONALIZED:
                        Method.personalization_ad = false;
                        method.showBannerAd(linearLayout);
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            Method.personalization_ad = true;
                            method.showBannerAd(linearLayout);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }

    public void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(Constant.appRP.getPrivacy_policy_link());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm();
                        // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                Method.personalization_ad = true;
                                method.showBannerAd(linearLayout);
                                break;
                            case NON_PERSONALIZED:
                            case UNKNOWN:
                                Method.personalization_ad = false;
                                method.showBannerAd(linearLayout);
                                break;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    private boolean getBannerAdType() {
        return Constant.appRP.getBanner_ad_type().equals("admob");
    }

    @Subscribe
    public void getLogin(Events.Login login) {
        if (method != null) {
            checkLogin();
        }
    }

    private void checkLogin() {
        if (navigationView != null) {
            int position = 7;
            if (method.isLogin()) {
                navigationView.getMenu().getItem(position).setIcon(R.drawable.ic_logout);
                navigationView.getMenu().getItem(position).setTitle(getResources().getString(R.string.logout));
            } else {
                navigationView.getMenu().getItem(position).setIcon(R.drawable.ic_login);
                navigationView.getMenu().getItem(position).setTitle(getResources().getString(R.string.login));
            }
        }
    }

    //alert message box
    public void logout() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.DialogTitleTextStyle);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.logout_message));
        builder.setPositiveButton(getResources().getString(R.string.logout),
                (arg0, arg1) -> {
                    if (method.getLoginType().equals("google")) {

                        // Configure sign-in to request the ic_user_login's ID, email address, and basic
                        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        // Build a GoogleSignInClient with the options specified by gso.
                        //Google login
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(MainActivity.this, task -> {
                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(MainActivity.this, Login.class));
                                    finishAffinity();
                                });
                    } else if (method.getLoginType().equals("facebook")) {
                        LoginManager.getInstance().logOut();
                        method.editor.putBoolean(method.pref_login, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    } else {
                        method.editor.putBoolean(method.pref_login, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                (dialogInterface, i) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showAppDialog(String description, String link, boolean isCancel) {

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_app);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

        MaterialTextView textViewDes = dialog.findViewById(R.id.textView_description_dialog_update);
        MaterialButton buttonUpdate = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        if (isCancel) {
            buttonCancel.setVisibility(View.VISIBLE);
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        textViewDes.setText(description);

        buttonUpdate.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        super.onDestroy();
    }

}
