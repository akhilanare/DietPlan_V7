package com.example.dietplan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dietplan.R;
import com.example.dietplan.activity.Login;
import com.example.dietplan.activity.MainActivity;
import com.example.dietplan.adapter.FavouriteAdapter;
import com.example.dietplan.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

public class FavouriteFragment extends Fragment {

    private Method method;
    private String[] strings;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private MaterialButton buttonLogin;
    private FavouriteAdapter favouriteAdapter;
    private ConstraintLayout conMain, conNoData;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.favourite_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.favourite));
        }

        method = new Method(getActivity());

        strings = new String[]{getResources().getString(R.string.diet_plan), getResources().getString(R.string.fitness_video)};

        buttonLogin = view.findViewById(R.id.button_not_login);
        conMain = view.findViewById(R.id.con_favourite_fragment);
        conNoData = view.findViewById(R.id.con_not_login);
        tabLayout = view.findViewById(R.id.tab_favourite_fragment);
        viewPager2 = view.findViewById(R.id.viewPager2_favourite_fragment);

        //attach tab layout with ViewPager
        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        buttonLogin.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finishAffinity();
        });

        if (method.isLogin()) {

            conMain.setVisibility(View.VISIBLE);

            favouriteAdapter = new FavouriteAdapter(getChildFragmentManager(), strings.length, getLifecycle());
            viewPager2.setAdapter(favouriteAdapter);

            TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
                tab.setText(strings[position]);
                viewPager2.setCurrentItem(tab.getPosition(), true);
            });
            tabLayoutMediator.attach();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager2.setCurrentItem(tab.getPosition(), true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        } else {
            conNoData.setVisibility(View.VISIBLE);
        }

        return view;
    }

}
