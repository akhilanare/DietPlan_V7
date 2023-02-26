package com.example.dietplan.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dietplan.R;
import com.example.dietplan.activity.Login;
import com.example.dietplan.activity.MainActivity;
import com.example.dietplan.interfaces.FavouriteIF;
import com.example.dietplan.response.DietPlanDetailRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.example.dietplan.util.API;
import com.example.dietplan.util.Method;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DietPlanDetailFragment extends Fragment {

    private Method method;
    private String id;
    private Animation myAnim;
    private WebView webView;
    private ProgressBar progressBar;
    private MaterialTextView textView;
    private DietPlanDetailRP dietPlanDetailRP;
    private ImageView imageView, imageViewFav;
    private ConstraintLayout conMain, conNoData;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.diet_plan_detail_fragment, container, false);

        method = new Method(getActivity());

        assert getArguments() != null;
        id = getArguments().getString("id");
        String title = getArguments().getString("title");

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(title);
        }

        progressBar = view.findViewById(R.id.progressbar_dietPlanDetail);
        conNoData = view.findViewById(R.id.con_noDataFound);
        conMain = view.findViewById(R.id.con_main_dietPlanDetail);
        imageView = view.findViewById(R.id.imageView_dietPlanDetail);
        imageViewFav = view.findViewById(R.id.imageView_fav_dietPlanDetail);
        textView = view.findViewById(R.id.textView_dietPlanDetail);
        webView = view.findViewById(R.id.webView_dietPlanDetail);

        conNoData.setVisibility(View.GONE);
        conMain.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                dietPlanDetail(method.userId());
            } else {
                dietPlanDetail("0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.share_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // action with ID action_refresh was selected
        if (item.getItemId() == R.id.ic_share) {
            if (dietPlanDetailRP != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, dietPlanDetailRP.getShare_link());
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_one)));
            } else {
                method.alertBox(getResources().getString(R.string.wrong));
            }
        }
        return true;
    }

    public void dietPlanDetail(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("diet_id", id);
            jsObj.addProperty("method_name", "single_diet");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DietPlanDetailRP> call = apiService.getDietPlanDetail(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DietPlanDetailRP>() {
                @SuppressLint({"SetJavaScriptEnabled", "UseCompatLoadingForDrawables"})
                @Override
                public void onResponse(@NotNull Call<DietPlanDetailRP> call, @NotNull Response<DietPlanDetailRP> response) {

                    if (getActivity() != null) {

                        try {
                            dietPlanDetailRP = response.body();
                            assert dietPlanDetailRP != null;

                            if (dietPlanDetailRP.getStatus().equals("1")) {

                                if (dietPlanDetailRP.getStatus().equals("1")) {

                                    if (MainActivity.toolbar != null) {
                                        MainActivity.toolbar.setTitle(dietPlanDetailRP.getDiet_title());
                                    }

                                    if (dietPlanDetailRP.getIs_fav().equals("true")) {
                                        imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_hov));
                                    } else {
                                        imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
                                    }

                                    imageView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (method.getScreenWidth() / 1.8)));

                                    Glide.with(getActivity().getApplicationContext()).load(dietPlanDetailRP.getDiet_image())
                                            .placeholder(R.drawable.place_holder)
                                            .into(imageView);

                                    textView.setText(dietPlanDetailRP.getDiet_title());

                                    webView.setBackgroundColor(Color.TRANSPARENT);
                                    webView.setFocusableInTouchMode(false);
                                    webView.setFocusable(false);
                                    webView.getSettings().setDefaultTextEncodingName("UTF-8");
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    String mimeType = "text/html";
                                    String encoding = "utf-8";

                                    String text = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/montserrat_semibold.otf\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}"
                                            + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                            + "</style>"
                                            + "</head>"
                                            + "<body>"
                                            + dietPlanDetailRP.getDiet_info()
                                            + "</body></html>";

                                    webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                                    imageViewFav.setOnClickListener(v -> {
                                        imageViewFav.startAnimation(myAnim);
                                        if (method.isLogin()) {
                                            FavouriteIF favouriteIF = (isFavourite, message) -> {
                                                if (isFavourite) {
                                                    imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_hov));
                                                } else {
                                                    imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
                                                }
                                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                            };
                                            method.addToFav(dietPlanDetailRP.getId(), method.userId(), "diet", 0, favouriteIF);
                                        } else {
                                            Method.loginBack = true;
                                            startActivity(new Intent(getActivity(), Login.class));
                                        }
                                    });

                                    conMain.setVisibility(View.VISIBLE);

                                } else {
                                    conNoData.setVisibility(View.VISIBLE);
                                    method.alertBox(dietPlanDetailRP.getMsg());
                                }

                            } else if (dietPlanDetailRP.getStatus().equals("2")) {
                                method.suspend(dietPlanDetailRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(dietPlanDetailRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<DietPlanDetailRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }

    }

}
