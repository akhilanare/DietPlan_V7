package com.example.dietplan.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dietplan.R;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.item.CategoryList;
import com.example.dietplan.item.DietPlanList;
import com.example.dietplan.util.Method;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeDietAdapter extends RecyclerView.Adapter<HomeDietAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private List<DietPlanList> dietPlanLists;

    public HomeDietAdapter(Activity activity, String type, List<DietPlanList> dietPlanLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.dietPlanLists = dietPlanLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_diet_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(activity).load(dietPlanLists.get(position).getDiet_image())
                .placeholder(R.drawable.place_holder).into(holder.imageView);

        holder.cardView.setOnClickListener(v -> {
            method.click(position, type, "", dietPlanLists.get(position).getDiet_title(), dietPlanLists.get(position).getId(),"","");
        });

    }

    @Override
    public int getItemCount() {
        return dietPlanLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_homeDiet_adapter);
            cardView = itemView.findViewById(R.id.cardView_homeDiet_adapter);

        }
    }
}
