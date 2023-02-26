package com.example.dietplan.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietplan.R;
import com.example.dietplan.activity.MainActivity;
import com.example.dietplan.adapter.DietPlanAdapter;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.item.DietPlanList;
import com.example.dietplan.response.DietPlanRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.example.dietplan.util.API;
import com.example.dietplan.util.Constant;
import com.example.dietplan.util.EndlessRecyclerViewScrollListener;
import com.example.dietplan.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DietPlanFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private String id, type, title;
    private RecyclerView recyclerView;
    private ConstraintLayout conNoData;
    private List<DietPlanList> dietPlanLists;
    private ProgressBar progressBar;
    private DietPlanAdapter dietPlanAdapter;
    private MaterialButton buttonLogin;
    private ImageView imageViewData;
    private MaterialTextView textViewNotLogin;
    private Boolean isOver = false;
    private int paginationIndex = 1, totalArraySize = 0;
    private String adsParam = "1";

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.diet_plan_fragment, container, false);

        dietPlanLists = new ArrayList<>();

        assert getArguments() != null;
        type = getArguments().getString("type");
        id = getArguments().getString("id");
        title = getArguments().getString("title");

        if (!type.equals("favDiet")) {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(title);
            }
        }

        onClick = (position, type, otherType, getTitle, id, videoType, videoLink) -> {
            DietPlanDetailFragment dietPlanDetailFragment = new DietPlanDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("title", getTitle);
            dietPlanDetailFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, dietPlanDetailFragment,
                    title).addToBackStack(title).commitAllowingStateLoss();
        };
        method = new Method(getActivity(), onClick);
        method.forceRTLIfSupported();

        progressBar = view.findViewById(R.id.progressbar_dietPlan_fragment);
        imageViewData = view.findViewById(R.id.imageView_not_login);
        buttonLogin = view.findViewById(R.id.button_not_login);
        textViewNotLogin = view.findViewById(R.id.textView_not_login);
        conNoData = view.findViewById(R.id.con_not_login);
        recyclerView = view.findViewById(R.id.recyclerView_dietPlan_fragment);

        data(false, false);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    if (dietPlanAdapter != null) {
                        dietPlanAdapter.hideHeader();
                    }
                }
            }
        });

        if (method.isNetworkAvailable()) {
            callData();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            if (type.equals("favDietHome")) {
                if (method.isLogin()) {
                    dietPlanList();
                } else {
                    data(true, true);
                }
            } else {
                dietPlanList();
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void data(boolean isShow, boolean isLogin) {
        if (isShow) {
            if (isLogin) {
                buttonLogin.setVisibility(View.VISIBLE);
                textViewNotLogin.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                buttonLogin.setVisibility(View.GONE);
                textViewNotLogin.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    public void dietPlanList() {

        if (getActivity() != null) {

            if (dietPlanAdapter == null) {
                dietPlanLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("ads_param", adsParam);
            jsObj.addProperty("page", paginationIndex);
            switch (type) {
                case "recentViewDiet":
                    jsObj.addProperty("type", "diet");
                    jsObj.addProperty("user_id", method.userId());
                    jsObj.addProperty("method_name", "recent_views_all");
                    break;
                case "favDiet":
                case "favDietHome":
                    jsObj.addProperty("type", "diet");
                    jsObj.addProperty("user_id", method.userId());
                    jsObj.addProperty("method_name", "get_favourite_list");
                    break;
                case "searchDiet":
                    jsObj.addProperty("search_text", title);
                    jsObj.addProperty("method_name", "search_diet");
                    break;
                default:
                    jsObj.addProperty("cat_id", id);
                    jsObj.addProperty("method_name", "diet_by_cat");
                    break;
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DietPlanRP> call = apiService.getDietPlanList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DietPlanRP>() {
                @Override
                public void onResponse(@NotNull Call<DietPlanRP> call, @NotNull Response<DietPlanRP> response) {

                    if (getActivity() != null) {

                        try {
                            DietPlanRP dietPlanRP = response.body();
                            assert dietPlanRP != null;

                            if (dietPlanRP.getStatus().equals("1")) {

                                adsParam = dietPlanRP.getAds_param();

                                if (dietPlanRP.getDietPlanLists().size() == 0) {
                                    if (dietPlanAdapter != null) {
                                        dietPlanAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    totalArraySize = totalArraySize + dietPlanRP.getDietPlanLists().size();
                                    for (int i = 0; i < dietPlanRP.getDietPlanLists().size(); i++) {
                                        dietPlanLists.add(dietPlanRP.getDietPlanLists().get(i));

                                        if (Constant.appRP != null && Constant.nativeAdPos != 0 && Constant.appRP.isNative_ad()) {
                                            int abc = dietPlanLists.lastIndexOf(null);
                                            if (((dietPlanLists.size() - (abc + 1)) % Constant.nativeAdPos == 0) && (dietPlanRP.getDietPlanLists().size() - 1 != i || totalArraySize != 1000)) {
                                                dietPlanLists.add(null);
                                            }
                                        }
                                    }
                                }

                                if (dietPlanAdapter == null) {
                                    if (dietPlanLists.size() != 0) {
                                        dietPlanAdapter = new DietPlanAdapter(getActivity(), "diet_plan", dietPlanLists, onClick);
                                        recyclerView.setAdapter(dietPlanAdapter);
                                    } else {
                                        data(true, false);
                                    }
                                } else {
                                    dietPlanAdapter.notifyDataSetChanged();
                                }

                            } else if (dietPlanRP.getStatus().equals("2")) {
                                method.suspend(dietPlanRP.getMessage());
                            } else {
                                data(true, false);
                                method.alertBox(dietPlanRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<DietPlanRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }

    }

}
