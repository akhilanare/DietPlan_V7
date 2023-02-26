package com.example.dietplan.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.dietplan.R;
import com.example.dietplan.activity.MainActivity;
import com.example.dietplan.activity.VideoPlayer;
import com.example.dietplan.activity.YoutubePlayActivity;
import com.example.dietplan.adapter.HomeCatAdapter;
import com.example.dietplan.adapter.HomeDietAdapter;
import com.example.dietplan.adapter.HomeSliderAdapter;
import com.example.dietplan.adapter.HomeVideoAdapter;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.response.DataRP;
import com.example.dietplan.response.HomeRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.example.dietplan.util.API;
import com.example.dietplan.util.Method;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private ViewPager viewPager;
    private TextInputEditText editTextSearch;
    private ImageView imageViewSearch;
    private InputMethodManager imm;
    private ConstraintLayout conMain, conNoData;
    private RecyclerView recyclerViewCat, recyclerViewRVDiet, recyclerViewRVVideo;
    private ConstraintLayout conSlider, conCat, conRecentViewDiet, conRecentViewVideo;
    private MaterialTextView textViewCat, textViewRecentViewDiet, textViewRecentViewVideo;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }

        progressDialog = new ProgressDialog(getActivity());

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        method = new Method(getActivity(), onClick = (position, type, otherType, title, id, videoType, videoLink) -> {
            switch (type) {
                case "home_slider":
                    if (otherType.equals("diet")) {
                        callDietDetail(id, title);
                    } else {
                        callVideo(id, videoType, videoLink);
                    }
                    break;
                case "home_cat": {
                    callDiet(id, title, type);
                    break;
                }
                case "home_recent_diet": {
                    callDietDetail(id, title);
                    break;
                }
                default:
                    callVideo(id, videoType, videoLink);
                    break;
            }

        });

        progressBar = view.findViewById(R.id.progressBar_home);
        conNoData = view.findViewById(R.id.con_noDataFound);
        conMain = view.findViewById(R.id.con_main_home);
        editTextSearch = view.findViewById(R.id.editText_home);
        imageViewSearch = view.findViewById(R.id.imageView_search_home);
        viewPager = view.findViewById(R.id.viewPager_slider_home);
        textViewCat = view.findViewById(R.id.textView_cat_home);
        textViewRecentViewDiet = view.findViewById(R.id.textView_recentViewDiet_home);
        textViewRecentViewVideo = view.findViewById(R.id.textView_recentViewVideo_home);
        recyclerViewCat = view.findViewById(R.id.recyclerView_cat_home);
        recyclerViewRVDiet = view.findViewById(R.id.recyclerView_recentViewDiet_home);
        recyclerViewRVVideo = view.findViewById(R.id.recyclerView_recentViewVideo_home);
        conSlider = view.findViewById(R.id.con_slider_home);
        conCat = view.findViewById(R.id.con_cat_home);
        conRecentViewDiet = view.findViewById(R.id.con_recentViewDiet_home);
        conRecentViewVideo = view.findViewById(R.id.con_recentViewVideo_home);

        viewPager.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, (int) (method.getScreenWidth() / 2)));

        recyclerViewCat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewCat.setLayoutManager(layoutManager);
        recyclerViewCat.setNestedScrollingEnabled(false);

        recyclerViewRVDiet.setHasFixedSize(true);
        LinearLayoutManager layoutManagerDiet = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewRVDiet.setLayoutManager(layoutManagerDiet);
        recyclerViewRVDiet.setNestedScrollingEnabled(false);

        recyclerViewRVVideo.setHasFixedSize(true);
        LinearLayoutManager layoutManagerVideo = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewRVVideo.setLayoutManager(layoutManagerVideo);
        recyclerViewRVVideo.setNestedScrollingEnabled(false);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
            }
            return false;
        });

        imageViewSearch.setOnClickListener(v -> search());

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                home(method.userId());
            } else {
                home("0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void search() {

        String search = editTextSearch.getText().toString();
        if (!search.isEmpty() || !search.equals("")) {
            editTextSearch.clearFocus();
            imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
            callDiet("", editTextSearch.getText().toString(), "searchDiet");
        } else {
            if (getActivity().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            method.alertBox(getResources().getString(R.string.please_enter_keyWord));
        }

    }

    private void callDiet(String id, String title, String type) {
        DietPlanFragment dietPlanFragment = new DietPlanFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title);
        bundle.putString("type", type);
        dietPlanFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, dietPlanFragment,
                title).addToBackStack(title).commitAllowingStateLoss();
    }

    private void callDietDetail(String id, String title) {
        DietPlanDetailFragment dietPlanDetailFragment = new DietPlanDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title);
        dietPlanDetailFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, dietPlanDetailFragment, title)
                .addToBackStack(title).commitAllowingStateLoss();
    }

    private void callVideo(String id, String videoType, String videoLink) {
        if (method.isLogin()) {
            recentViewVideo(method.userId(), id, videoType, videoLink);
        } else {
            if (videoType.equals("youtube")) {
                startActivity(new Intent(getActivity(), YoutubePlayActivity.class)
                        .putExtra("id", videoLink));
            } else {
                startActivity(new Intent(getActivity(), VideoPlayer.class)
                        .putExtra("Video_url", videoLink));
            }
        }
    }

    public void home(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "get_home");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HomeRP> call = apiService.getHome(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<HomeRP>() {
                @Override
                public void onResponse(@NotNull Call<HomeRP> call, @NotNull Response<HomeRP> response) {

                    if (getActivity() != null) {

                        try {
                            HomeRP homeRP = response.body();
                            assert homeRP != null;

                            if (homeRP.getStatus().equals("1")) {

                                if (homeRP.getSliderLists().size() != 0) {

                                    viewPager.setClipToPadding(false);// allow full width shown with padding

                                    //increase this offset to show more of left/right
                                    viewPager.setPadding(0, 0, dpToPx(60), 0);

                                    HomeSliderAdapter homeSliderAdapter = new HomeSliderAdapter(getActivity(), "home_slider", homeRP.getSliderLists(), onClick);
                                    viewPager.setAdapter(homeSliderAdapter);

                                } else {
                                    conSlider.setVisibility(View.GONE);
                                }

                                if (homeRP.getCategoryLists().size() != 0) {
                                    HomeCatAdapter homeCatAdapter = new HomeCatAdapter(getActivity(), "home_cat", homeRP.getCategoryLists(), onClick);
                                    recyclerViewCat.setAdapter(homeCatAdapter);
                                } else {
                                    conCat.setVisibility(View.GONE);
                                }

                                if (homeRP.getRecentDietPlanLists().size() != 0) {
                                    HomeDietAdapter homeDietAdapter = new HomeDietAdapter(getActivity(), "home_recent_diet", homeRP.getRecentDietPlanLists(), onClick);
                                    recyclerViewRVDiet.setAdapter(homeDietAdapter);
                                } else {
                                    conRecentViewDiet.setVisibility(View.GONE);
                                }

                                if (homeRP.getRecentVideoLists().size() != 0) {
                                    HomeVideoAdapter homeVideoAdapter = new HomeVideoAdapter(getActivity(), "home_recent_video", homeRP.getRecentVideoLists(), onClick);
                                    recyclerViewRVVideo.setAdapter(homeVideoAdapter);
                                } else {
                                    conRecentViewVideo.setVisibility(View.GONE);
                                }

                                textViewCat.setOnClickListener(v -> {
                                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new CategoryFragment(),
                                            getResources().getString(R.string.category)).addToBackStack(getResources().getString(R.string.category))
                                            .commitAllowingStateLoss();
                                });

                                textViewRecentViewDiet.setOnClickListener(v -> {
                                    callDiet("", getResources().getString(R.string.recentView_diet), "recentViewDiet");
                                });

                                textViewRecentViewVideo.setOnClickListener(v -> {
                                    FitnessVideoFragment fitnessVideoFragment = new FitnessVideoFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("type", "recentViewVideo");
                                    bundle.putString("title", getResources().getString(R.string.recentView_video));
                                    fitnessVideoFragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, fitnessVideoFragment, getResources().getString(R.string.recentView_video))
                                            .addToBackStack(getResources().getString(R.string.recentView_video)).commitAllowingStateLoss();
                                });

                                conMain.setVisibility(View.VISIBLE);

                            } else if (homeRP.getStatus().equals("2")) {
                                method.suspend(homeRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(homeRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NotNull Call<HomeRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    public void recentViewVideo(String userId, String videoId, String videoType, String videoLink) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("video_id", videoId);
            jsObj.addProperty("method_name", "recent_video");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.getForgotPass(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                    if (getActivity() != null) {

                        try {

                            DataRP dataRP = response.body();

                            assert dataRP != null;
                            if (dataRP.getStatus().equals("1")) {
                                if (dataRP.getSuccess().equals("0")) {
                                    Toast.makeText(getActivity(), dataRP.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                                if (videoType.equals("youtube")) {
                                    startActivity(new Intent(getActivity(), YoutubePlayActivity.class)
                                            .putExtra("id", videoLink));
                                } else {
                                    startActivity(new Intent(getActivity(), VideoPlayer.class)
                                            .putExtra("Video_url", videoLink));
                                }
                            } else if (dataRP.getStatus().equals("2")) {
                                method.suspend(dataRP.getMessage());
                            } else {
                                method.alertBox(dataRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
