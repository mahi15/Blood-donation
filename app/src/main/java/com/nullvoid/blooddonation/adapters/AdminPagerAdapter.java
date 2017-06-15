package com.nullvoid.blooddonation.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nullvoid.blooddonation.DoneeListFragment;

/**
 * Created by sanath on 15/06/17.
 */

public class AdminPagerAdapter extends FragmentPagerAdapter {
    Context context;

    public AdminPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DoneeListFragment();
            case 1:
                //return new DonorListFragment();
                return new DoneeListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "DONEES";
            case 1:
                return "DONORS";
        }
        return null;
    }
}

