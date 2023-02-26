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
import com.example.dietplan.adapter.BmiHistoryAdapter;
import com.example.dietplan.item.BmiHistoryList;
import com.example.dietplan.response.BmiHistoryRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.example.dietplan.util.API;
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

public class BmiHistoryFragment extends Fragment {

    private Method method;
    private RecyclerView recyclerView;
    private ConstraintLayout conNoData;
    private List<BmiHistoryList> bmiHistoryLists;
    private ProgressBar progressBar;
    private BmiHistoryAdapter bmiHistoryAdapter;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bmi_history_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.bmi_title_pro));
        }

        method = new Method(getActivity());

        bmiHistoryLists = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressbar_bmiHistory_fragment);
        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_bmiHistory_fragment);

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
                    if (bmiHistoryAdapter != null) {
                        bmiHistoryAdapter.hideHeader();
                    }
                }
            }
        });

        callData();

        return view;

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            bmiHistory(method.userId());
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    public void bmiHistory(String userId) {

        if (getActivity() != null) {

            if (bmiHistoryAdapter == null) {
                bmiHistoryLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("page", paginationIndex);
            jsObj.addProperty("method_name", "get_bmi_history");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<BmiHistoryRP> call = apiService.getBmiHistory(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<BmiHistoryRP>() {
                @Override
                public void onResponse(@NotNull Call<BmiHistoryRP> call, @NotNull Response<BmiHistoryRP> response) {

                    if (getActivity() != null) {

                        try {
                            BmiHistoryRP bmiHistoryRP = response.body();
                            assert bmiHistoryRP != null;

                            if (bmiHistoryRP.getStatus().equals("1")) {

                                if (bmiHistoryRP.getBmiHistoryLists().size() == 0) {
                                    if (bmiHistoryAdapter != null) {
                                        bmiHistoryAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    bmiHistoryLists.addAll(bmiHistoryRP.getBmiHistoryLists());
                                }

                                if (bmiHistoryAdapter == null) {
                                    if (bmiHistoryLists.size() != 0) {
                                        bmiHistoryAdapter = new BmiHistoryAdapter(getActivity(), "bmiHistory", bmiHistoryLists);
                                        recyclerView.setAdapter(bmiHistoryAdapter);
                                    } else {
                                        conNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    bmiHistoryAdapter.notifyDataSetChanged();
                                }

                            } else if (bmiHistoryRP.getStatus().equals("2")) {
                                method.suspend(bmiHistoryRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(bmiHistoryRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<BmiHistoryRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }

    }

}
