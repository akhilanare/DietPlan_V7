package com.example.dietplan.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dietplan.R;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.item.CategoryList;
import com.example.dietplan.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class HomeCatAdapter extends RecyclerView.Adapter<HomeCatAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private List<CategoryList> categoryLists;

    public HomeCatAdapter(Activity activity, String type, List<CategoryList> categoryLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.categoryLists = categoryLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_cat_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(categoryLists.get(position).getCategory_name());

        Glide.with(activity).load(categoryLists.get(position).getCategory_image_thumb())
                .placeholder(R.drawable.place_holder_cat).into(holder.imageView);

        holder.cardView.setOnClickListener(v -> {
            method.click(position, type, "", categoryLists.get(position).getCategory_name(), categoryLists.get(position).getCid(), "", "");
        });

    }

    @Override
    public int getItemCount() {
        return categoryLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private MaterialTextView textView;
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_homeCat_adapter);
            textView = itemView.findViewById(R.id.textView_homeCat_adapter);
            cardView = itemView.findViewById(R.id.cardView_homeCat_adapter);

        }
    }
}
