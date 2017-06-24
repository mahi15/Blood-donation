package com.nullvoid.blooddonation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button donateBlood, reqBlood, admin;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;

    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationDrawer();

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        admin = (Button) findViewById(R.id.admin);
        isUserAdmin();

        donateBlood = (Button) findViewById(R.id.btn_donate_blood);
        reqBlood = (Button) findViewById(R.id.btn_req_blood);

        reqBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLoginStatus() != null) {
                    startActivity(new Intent(MainActivity.this, DoneeRequestActivity.class));
                } else {
                    showToast("You Must Login First");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        donateBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLoginStatus() != null) {
                    startActivity(new Intent(MainActivity.this, DonorRegistrationActivity.class));
                } else {
                    showToast("You Must Login First");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminConsoleActivity.class));
            }
        });
    }

    public void isUserAdmin() {
        boolean isAdmin;
        dbRef.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("admin").getValue(Boolean.class)) {
                    admin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String checkLoginStatus() {
        fbUser = mAuth.getCurrentUser();
        if (fbUser != null) {
            return fbUser.getUid();
        } else {
            return null;
        }
    }

    public void showToast(String textToToast) {
        Toast.makeText(this, textToToast, Toast.LENGTH_SHORT).show();
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_req_from:
                        startActivity(new Intent(getApplicationContext(), DoneeRequestActivity.class));
                        break;
                    case R.id.nav_about:
                        Toast.makeText(getApplicationContext(), "Have to implement", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_settings:
                        Toast.makeText(getApplicationContext(), "Have to implement", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        break;
                }
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);}
            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);}
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
}

