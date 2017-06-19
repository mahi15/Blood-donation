package com.nullvoid.blooddonation;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nullvoid.blooddonation.adapters.AdminPagerAdapter;

/**
 * Created by sanath on 13/06/17.
 */

public class AdminConsoleActivity extends AppCompatActivity {

    //reciever for donor mode
    BroadcastReceiver mMessageReceiver;
    private ViewPager viewPager;
    private AdminPagerAdapter mAdapter;
    // Tab titles
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_console_layout);

        //adding the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new AdminPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mAdapter.getTabView(i));
        }
        viewPager.setAdapter(mAdapter);

    }

}