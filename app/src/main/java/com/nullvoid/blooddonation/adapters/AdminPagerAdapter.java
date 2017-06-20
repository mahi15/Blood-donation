package com.nullvoid.blooddonation.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nullvoid.blooddonation.DoneeListFragment;
import com.nullvoid.blooddonation.DonorListFragment;
import com.nullvoid.blooddonation.R;

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
                return new DonorListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    public View getTabView(int position) {
        View tab = LayoutInflater.from(context).inflate(R.layout.view_console_tab, null);
        TextView tv = (TextView) tab.findViewById(R.id.custom_text);
        tv.setText(getPageTitle(position));
        return tab;
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

