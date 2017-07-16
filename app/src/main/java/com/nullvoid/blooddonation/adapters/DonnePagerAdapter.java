package com.nullvoid.blooddonation.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nullvoid.blooddonation.admin.AdminDonneActivity;
import com.nullvoid.blooddonation.admin.DoneeListFragment;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.others.Constants;

/**
 * Created by sanath on 15/06/17.
 */

public class DonnePagerAdapter extends FragmentPagerAdapter {
    AdminDonneActivity parentActivity;

    public DonnePagerAdapter(FragmentManager fm, AdminDonneActivity parentActivity) {
        super(fm);
        this.parentActivity = parentActivity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DoneeListFragment.newInstance(Constants.statusNotComplete());
            case 1:
                return DoneeListFragment.newInstance(Constants.statusPending());
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    public View getTabView(int position) {
        View tab = LayoutInflater.from(parentActivity).inflate(R.layout.view_console_tab, null);
        TextView tv = (TextView) tab.findViewById(R.id.custom_text);
        tv.setText(getPageTitle(position));
        return tab;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "NEW REQUESTS";
            case 1:
                return "PENDING REQUESTS";
        }
        return null;
    }
}

