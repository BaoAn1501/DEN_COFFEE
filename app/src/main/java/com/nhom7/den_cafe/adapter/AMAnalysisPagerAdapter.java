package com.nhom7.den_cafe.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nhom7.den_cafe.analysis.AMProductAnalysisFragment;
import com.nhom7.den_cafe.analysis.AMRevenueFragment;
import com.nhom7.den_cafe.product.ListProductAFragment;
import com.nhom7.den_cafe.product.ListProductTypeFragment;

public class AMAnalysisPagerAdapter extends FragmentStatePagerAdapter {
    public AMAnalysisPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return new AMRevenueFragment();
        }
        else if(i == 1){
            return new AMProductAnalysisFragment();
        }
        else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Doanh thu";
            case 1:
                return "Sản phẩm";
        }
        return null;
    }
}
