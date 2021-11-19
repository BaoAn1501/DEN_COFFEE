package com.nhom7.den_cafe.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nhom7.den_cafe.login.SignInFragment;
import com.nhom7.den_cafe.login.SignUpFragment;
import com.nhom7.den_cafe.product.ListProductAFragment;
import com.nhom7.den_cafe.product.ListProductTypeFragment;

public class AddProductPagerAdapter extends FragmentStatePagerAdapter {
    public AddProductPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return new ListProductAFragment();
        }
        else if(i == 1){
            return new ListProductTypeFragment();
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
                return "Sản phẩm";
            case 1:
                return "Loại sản phẩm";
        }
        return null;
    }
}
