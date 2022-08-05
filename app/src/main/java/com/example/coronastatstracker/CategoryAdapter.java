package com.example.coronastatstracker;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CategoryAdapter extends FragmentPagerAdapter {

    public CategoryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String str = "";

        if(position == 0)
            str = "Latest Stats";

        else if (position == 1)
            str = "Feed";

        return str;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment f = null;

        if (position == 0) {
            f = new MainFragment();
        } else if (position == 1) {
            f = new SecondFragment();
        }

        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
