package com.nullvoid.blooddonation;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nullvoid.blooddonation.adapters.AdminPagerAdapter;

/**
 * Created by sanath on 13/06/17.
 */

public class AdminConsoleActivity extends AppCompatActivity{

private ViewPager viewPager;
private AdminPagerAdapter mAdapter;
private ActionBar actionBar;
// Tab titles
private String[]tabs={"Top Rated","Games","Movies"};

@Override
protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_console_layout);

        //adding the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initilization
        viewPager = (ViewPager)findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter=new AdminPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager.setAdapter(mAdapter);

    }
}