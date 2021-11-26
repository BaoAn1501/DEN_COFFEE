package com.nhom7.den_cafe.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nhom7.den_cafe.analysis.AMAllTimeFragment;
import com.nhom7.den_cafe.analysis.AMMonthFragment;
import com.nhom7.den_cafe.analysis.AMTodayFragment;
import com.nhom7.den_cafe.product.ListProductAFragment;
import com.nhom7.den_cafe.product.ListProductTypeFragment;

public class AMRevenuePagerAdapter extends FragmentStatePagerAdapter {
    public AMRevenuePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i==0){
            return new AMTodayFragment();
        } else if(i==1){
            return new AMMonthFragment();
        } else if(i==2){
            return new AMAllTimeFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Hôm nay";
            case 1:
                return "Tháng này";
            case 2:
                return "Tất cả";
        }
        return null;
    }
}