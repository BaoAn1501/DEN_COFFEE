package com.nhom7.den_cafe.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.AMAnalysisPagerAdapter;
import com.nhom7.den_cafe.adapter.AMRevenuePagerAdapter;
import com.nhom7.den_cafe.adapter.AddProductPagerAdapter;

import org.jetbrains.annotations.NotNull;

public class AMRevenueFragment extends Fragment {
    View view;
    TabLayout tabLayout;
    ViewPager viewPager;
    AMRevenuePagerAdapter adapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_revenue_admin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tabLayoutAMRF);
        viewPager = view.findViewById(R.id.viewPagerAMRF);
        adapter = new AMRevenuePagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
