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
import com.example.dietplan.item.VideoList;
import com.example.dietplan.util.Constant;
import com.example.dietplan.util.Method;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeVideoAdapter extends RecyclerView.Adapter<HomeVideoAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private List<VideoList> videoLists;

    public HomeVideoAdapter(Activity activity, String type, List<VideoList> videoLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.videoLists = videoLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_video_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (videoLists.get(position).getVideo_type().equals("youtube")) {
            Glide.with(activity).load(Constant.YOUTUBE_IMAGE_FRONT + videoLists.get(position).getVideo_id() + Constant.YOUTUBE_SMALL_IMAGE_BACK)
                    .placeholder(R.drawable.place_holder)
                    .into(holder.imageView);
        } else {
            Glide.with(activity).load(videoLists.get(position).getVideo_thumbnail_s())
                    .placeholder(R.drawable.place_holder).into(holder.imageView);
        }

        holder.cardView.setOnClickListener(v -> {
            if (videoLists.get(position).getVideo_type().equals("youtube")) {
                method.click(position, type, "", videoLists.get(position).getVideo_title(), videoLists.get(position).getId(), videoLists.get(position).getVideo_type(), videoLists.get(position).getVideo_id());
            } else {
                method.click(position, type, "", videoLists.get(position).getVideo_title(), videoLists.get(position).getId(), videoLists.get(position).getVideo_type(), videoLists.get(position).getVideo_url());
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_homeVideo_adapter);
            cardView = itemView.findViewById(R.id.cardView_homeVideo_adapter);

        }
    }
}
