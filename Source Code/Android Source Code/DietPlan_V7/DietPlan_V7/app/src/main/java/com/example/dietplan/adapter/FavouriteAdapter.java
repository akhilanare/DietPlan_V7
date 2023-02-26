package com.example.dietplan.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dietplan.fragment.DietPlanFragment;
import com.example.dietplan.fragment.FitnessVideoFragment;

public class FavouriteAdapter extends FragmentStateAdapter {

    private final int size;

    public FavouriteAdapter(FragmentManager fm, int size, Lifecycle lifecycle) {
        super(fm, lifecycle);
        this.size = size;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {

            case 0:
                DietPlanFragment dietPlanFragment = new DietPlanFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", "");
                bundle.putString("title", "");
                bundle.putString("type", "favDiet");
                dietPlanFragment.setArguments(bundle);
                return dietPlanFragment;

            case 1:
                FitnessVideoFragment fitnessVideoFragment = new FitnessVideoFragment();
                Bundle bundleVideo = new Bundle();
                bundleVideo.putString("type", "favVideo");
                bundleVideo.putString("title", "");
                fitnessVideoFragment.setArguments(bundleVideo);
                return fitnessVideoFragment;

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return size;
    }
}
