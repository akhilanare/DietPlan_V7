package com.example.dietplan.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.dietplan.R;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.item.SliderList;
import com.example.dietplan.util.Method;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeSliderAdapter extends PagerAdapter {

    private Activity activity;
    private Method method;
    private String type;
    private List<SliderList> sliderLists;

    public HomeSliderAdapter(Activity activity, String type, List<SliderList> sliderLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.sliderLists = sliderLists;
        method = new Method(activity, onClick);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View view = activity.getLayoutInflater().inflate(R.layout.home_slider_adapter, container, false);

        ImageView imageView = view.findViewById(R.id.imageView_homeSlider_adapter);
        ImageView imageViewPlay = view.findViewById(R.id.imageViewPlay_homeSlider_adapter);
        MaterialTextView textView = view.findViewById(R.id.textView_homeSlider_adapter);
        ConstraintLayout con = view.findViewById(R.id.con_homeSlider_adapter);

        String typeSlider = sliderLists.get(position).getSlider_type();

        if (typeSlider.equals("diet") || typeSlider.equals("external")) {
            imageViewPlay.setVisibility(View.GONE);
        } else {
            imageViewPlay.setVisibility(View.VISIBLE);
        }

        textView.setText(sliderLists.get(position).getSlider_title());

        Glide.with(activity).load(sliderLists.get(position).getThumbnail_b())
                .placeholder(R.drawable.place_holder).into(imageView);

        con.setOnClickListener(v -> {
            if (typeSlider.equals("external")) {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sliderLists.get(position).getExternal_link())));
                } catch (Exception e) {
                    method.alertBox(activity.getResources().getString(R.string.wrong));
                }
            } else if (typeSlider.equals("diet")) {
                method.click(position, type, typeSlider, sliderLists.get(position).getSlider_title(), sliderLists.get(position).getId(), "", "");
            } else {
                if (sliderLists.get(position).getVideo_type().equals("youtube")) {
                    method.click(position, type, typeSlider, sliderLists.get(position).getSlider_title(), sliderLists.get(position).getId(), sliderLists.get(position).getVideo_type(), sliderLists.get(position).getVideo_id());
                } else {
                    method.click(position, type, typeSlider, sliderLists.get(position).getSlider_title(), sliderLists.get(position).getId(), sliderLists.get(position).getVideo_type(), sliderLists.get(position).getVideo_url());
                }
            }
        });

        container.addView(view, 0);
        return view;

    }

    @Override
    public int getCount() {
        return sliderLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }

}
