package com.example.dietplan.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietplan.R;
import com.example.dietplan.item.BmiHistoryList;
import com.example.dietplan.response.DataRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.example.dietplan.util.API;
import com.example.dietplan.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BmiHistoryAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Method method;
    private String type;
    private Animation myAnim;
    private List<BmiHistoryList> bmiHistoryLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public BmiHistoryAdapter(Activity activity, String type, List<BmiHistoryList> bmiHistoryLists) {
        this.activity = activity;
        this.type = type;
        this.bmiHistoryLists = bmiHistoryLists;
        method = new Method(activity);
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.bmi_history_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            String title = activity.getResources().getString(R.string.bmi) + " " + bmiHistoryLists.get(position).getTitle();
            viewHolder.textViewTitle.setText(title);
            viewHolder.textViewGender.setText(bmiHistoryLists.get(position).getGender());
            viewHolder.textViewScore.setText(bmiHistoryLists.get(position).getBmi_score());
            viewHolder.textViewStatus.setText(bmiHistoryLists.get(position).getBmi_status());

            viewHolder.cardViewDelete.setOnClickListener(v -> {
                viewHolder.cardViewDelete.startAnimation(myAnim);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                builder.setMessage(activity.getResources().getString(R.string.delete_bmi_history_msg));
                builder.setCancelable(false);
                builder.setPositiveButton(activity.getResources().getString(R.string.yes),
                        (arg0, arg1) -> {
                            deleteBmiHistory(bmiHistoryLists.get(position).getId(), method.userId(), position);
                        });
                builder.setNegativeButton(activity.getResources().getString(R.string.no), (dialog, which) -> {
                    dialog.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });

        }

    }

    @Override
    public int getItemCount() {
        return bmiHistoryLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == bmiHistoryLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardViewDelete;
        private MaterialTextView textViewTitle, textViewGender, textViewScore, textViewStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textView_title_bmiHistory_adapter);
            textViewGender = itemView.findViewById(R.id.textView_gender_bmiHistory_adapter);
            textViewScore = itemView.findViewById(R.id.textView_score_bmiHistory_adapter);
            textViewStatus = itemView.findViewById(R.id.textView_status_bmiHistory_adapter);
            cardViewDelete = itemView.findViewById(R.id.cardView_delete_bmiHistory_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }

    private void deleteBmiHistory(String id, String userId, int position) {

        ProgressDialog progressDialog = new ProgressDialog(activity);

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("id", id);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("method_name", "delete_bim_calculator");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.deleteBmiHistory(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {
                    DataRP dataRP = response.body();
                    assert dataRP != null;

                    if (dataRP.getStatus().equals("1")) {

                        if (dataRP.getSuccess().equals("1")) {
                            bmiHistoryLists.remove(position);
                            notifyDataSetChanged();
                        } else {
                            method.alertBox(dataRP.getMessage());
                        }

                    } else if (dataRP.getStatus().equals("2")) {
                        method.suspend(dataRP.getMessage());
                    } else {
                        method.alertBox(dataRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });


    }

}
