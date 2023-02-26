package com.example.dietplan.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dietplan.R;
import com.example.dietplan.activity.MainActivity;
import com.example.dietplan.activity.VideoPlayer;
import com.example.dietplan.activity.YoutubePlayActivity;
import com.example.dietplan.adapter.FitnessVideoAdapter;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.item.VideoList;
import com.example.dietplan.response.DataRP;
import com.example.dietplan.response.FitnessVideoRP;
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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FitnessVideoFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private String type, title, adsParam = "1";
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private ConstraintLayout conNoData;
    private List<VideoList> videoLists;
    private RecyclerView recyclerView;
    private FitnessVideoAdapter fitnessVideoAdapter;
    private Boolean isOver = false;
    private int paginationIndex = 1, totalArraySize = 0;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fitness_video_fragment, container, false);

        assert getArguments() != null;
        type = getArguments().getString("type");
        title = getArguments().getString("title");

        if (!type.equals("favVideo")) {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(title);
            }
        }

        progressDialog = new ProgressDialog(getActivity());

        onClick = (position, type, otherType, title, id, videoType, videoLink) -> {
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
        };
        method = new Method(getActivity(), onClick);

        videoLists = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressbar_video);
        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_video);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        conNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    fitnessVideoAdapter.hideHeader();
                }
            }
        });

        callData();

        if (!type.equals("favVideo")) {
            setHasOptionsMenu(true);
        }
        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.ic_searchView);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener((new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (getActivity() != null) {

                    getActivity().getSupportFragmentManager().popBackStack();

                    FitnessVideoFragment fitnessVideoFragment = new FitnessVideoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "searchVideo");
                    bundle.putString("title", query);
                    fitnessVideoFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, fitnessVideoFragment, query)
                            .addToBackStack(query).commitAllowingStateLoss();

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        }));

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // action with ID action_refresh was selected
        return super.onOptionsItemSelected(item);
    }

    public void callData() {
        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                videoList(method.userId());
            } else {
                videoList("0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    public void videoList(String userId) {

        if (getActivity() != null) {

            if (fitnessVideoAdapter == null) {
                videoLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("ads_param", adsParam);
            jsObj.addProperty("page", paginationIndex);
            switch (type) {
                case "favVideo":
                    jsObj.addProperty("type", "video");
                    jsObj.addProperty("method_name", "get_favourite_list");
                    break;
                case "recentViewVideo":
                    jsObj.addProperty("type", "video");
                    jsObj.addProperty("method_name", "recent_views_all");
                    break;
                case "searchVideo":
                    jsObj.addProperty("search_text", title);
                    jsObj.addProperty("method_name", "video_search");
                    break;
                default:
                    jsObj.addProperty("method_name", "video_list");
                    break;
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<FitnessVideoRP> call = apiService.getFitnessVideo(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<FitnessVideoRP>() {
                @Override
                public void onResponse(@NotNull Call<FitnessVideoRP> call, @NotNull Response<FitnessVideoRP> response) {

                    if (getActivity() != null) {

                        try {
                            FitnessVideoRP fitnessVideoRP = response.body();
                            assert fitnessVideoRP != null;

                            if (fitnessVideoRP.getStatus().equals("1")) {

                                adsParam = fitnessVideoRP.getAds_param();

                                if (fitnessVideoRP.getVideoLists().size() == 0) {
                                    if (fitnessVideoAdapter != null) {
                                        fitnessVideoAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    totalArraySize = totalArraySize + fitnessVideoRP.getVideoLists().size();
                                    for (int i = 0; i < fitnessVideoRP.getVideoLists().size(); i++) {
                                        videoLists.add(fitnessVideoRP.getVideoLists().get(i));

                                        if (Constant.appRP != null && Constant.nativeAdPos != 0 && Constant.appRP.isNative_ad()) {
                                            int abc = videoLists.lastIndexOf(null);
                                            if (((videoLists.size() - (abc + 1)) % Constant.nativeAdPos == 0) && (fitnessVideoRP.getVideoLists().size() - 1 != i || totalArraySize != 1000)) {
                                                videoLists.add(null);
                                            }
                                        }
                                    }
                                }

                                if (fitnessVideoAdapter == null) {
                                    if (videoLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        conNoData.setVisibility(View.GONE);
                                        fitnessVideoAdapter = new FitnessVideoAdapter(getActivity(), "fitness_video", videoLists, onClick);
                                        recyclerView.setAdapter(fitnessVideoAdapter);
                                    }
                                } else {
                                    fitnessVideoAdapter.notifyDataSetChanged();
                                }
                            } else if (fitnessVideoRP.getStatus().equals("2")) {
                                method.suspend(fitnessVideoRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(fitnessVideoRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<FitnessVideoRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
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

}
