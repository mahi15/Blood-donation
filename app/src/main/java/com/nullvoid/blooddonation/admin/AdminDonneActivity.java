package com.nullvoid.blooddonation.admin;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.adapters.AdminPagerAdapter;

/**
 * Created by sanath on 13/06/17.
 */

public class AdminDonneActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private AdminPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_donne);

        //adding the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new AdminPagerAdapter(getSupportFragmentManager(), AdminDonneActivity.this);

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