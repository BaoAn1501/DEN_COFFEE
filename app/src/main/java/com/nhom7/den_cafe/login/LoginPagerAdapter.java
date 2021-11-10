package com.nhom7.den_cafe.login;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginPagerAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public LoginPagerAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        this.myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SignInFragment loginFragment = new SignInFragment();
                return loginFragment;
            case 1:
                SignUpFragment signinFragment = new SignUpFragment();
                return signinFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return "Log In";
        } else {
            return "Sign In";
        }
    }
}
