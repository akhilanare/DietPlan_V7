package com.example.dietplan.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietplan.R;
import com.example.dietplan.activity.MainActivity;
import com.example.dietplan.adapter.CategoryAdapter;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.item.CategoryList;
import com.example.dietplan.response.CategoryRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.example.dietplan.util.API;
import com.example.dietplan.util.Constant;
import com.example.dietplan.util.EndlessRecyclerViewScrollListener;
import com.example.dietplan.util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private RecyclerView recyclerView;
    private ConstraintLayout conNoData;
    private List<CategoryList> categoryLists;
    private ProgressBar progressBar;
    private CategoryAdapter categoryAdapter;
    private Boolean isOver = false;
    private int paginationIndex = 1, totalArraySize = 0;
    private String ads_param = "1";

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.category));
        }

        onClick = (position, type, otherType, title, id, videoType, videoLink) -> {
            DietPlanFragment dietPlanFragment = new DietPlanFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("title", title);
            bundle.putString("type", type);
            dietPlanFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, dietPlanFragment,
                    title).addToBackStack(title).commitAllowingStateLoss();
        };
        method = new Method(getActivity(), onClick);

        categoryLists = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressbar_category_fragment);
        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_category_fragment);

        conNoData.setVisibility(View.GONE);
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
                    if (categoryAdapter != null) {
                        categoryAdapter.hideHeader();
                    }
                }
            }
        });

        callData();

        return view;

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            category();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    public void category() {

        if (getActivity() != null) {

            if (categoryAdapter == null) {
                categoryLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            jsObj.addProperty("ads_param", ads_param);
            jsObj.addProperty("method_name", "get_category");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CategoryRP> call = apiService.getCategory(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<CategoryRP>() {
                @Override
                public void onResponse(@NotNull Call<CategoryRP> call, @NotNull Response<CategoryRP> response) {

                    if (getActivity() != null) {

                        try {
                            CategoryRP categoryRP = response.body();
                            assert categoryRP != null;

                            if (categoryRP.getStatus().equals("1")) {

                                ads_param = categoryRP.getAds_param();

                                if (categoryRP.getCategoryLists().size() == 0) {
                                    if (categoryAdapter != null) {
                                        categoryAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    totalArraySize = totalArraySize + categoryRP.getCategoryLists().size();
                                    for (int i = 0; i < categoryRP.getCategoryLists().size(); i++) {
                                        categoryLists.add(categoryRP.getCategoryLists().get(i));

                                        if (Constant.appRP != null && Constant.nativeAdPos != 0 && Constant.appRP.isNative_ad()) {
                                            int abc = categoryLists.lastIndexOf(null);
                                            if (((categoryLists.size() - (abc + 1)) % Constant.nativeAdPos == 0) && (categoryRP.getCategoryLists().size() - 1 != i || totalArraySize != 1000)) {
                                                categoryLists.add(null);
                                            }
                                        }
                                    }
                                }

                                if (categoryAdapter == null) {
                                    if (categoryLists.size() != 0) {
                                        categoryAdapter = new CategoryAdapter(getActivity(), categoryLists, "category", onClick);
                                        recyclerView.setAdapter(categoryAdapter);
                                    } else {
                                        conNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    categoryAdapter.notifyDataSetChanged();
                                }

                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(categoryRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<CategoryRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

}
