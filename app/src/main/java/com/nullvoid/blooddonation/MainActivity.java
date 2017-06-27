package com.nullvoid.blooddonation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.AppConstants;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button donateBlood, reqBlood, admin;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    Donor currentUser;

    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        admin = (Button) findViewById(R.id.admin);

        setCurrentUser();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationDrawer();

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        donateBlood = (Button) findViewById(R.id.btn_donate_blood);
        reqBlood = (Button) findViewById(R.id.btn_req_blood);

        reqBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DoneeRequestActivity.class));
            }
        });

        donateBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DonorRegistrationActivity.class));
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminConsoleActivity.class));
            }
        });
    }

    public void logoutUser() {
        Gson gson = new Gson();
        SharedPreferences mPrefs = getSharedPreferences(AppConstants.currentUser(), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear().apply();
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
                        drawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), DoneeRequestActivity.class));
                        break;
                    case R.id.nav_don_from:
                        drawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), DonorRegistrationActivity.class));
                        break;
                    case R.id.nav_about:
                        drawerLayout.closeDrawers();
                        Toast.makeText(getApplicationContext(), "Have to implement", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_profile:
                        drawerLayout.closeDrawers();
                        if(currentUser == null){
                            signInDonor();
                        } else {
                            startActivity(new Intent(getApplicationContext(), DonorProfileActivity.class));
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        logoutUser();
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        break;
                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView headerText = (TextView) header.findViewById(R.id.header_text);
        if (currentUser != null) {
            headerText.setText(currentUser.getName());
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public void setCurrentUser() {
        SharedPreferences mPref = getSharedPreferences(AppConstants.currentUser(), MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPref.getString(AppConstants.currentUser(), null);
        currentUser = gson.fromJson(json, Donor.class);

        if(currentUser != null){
            if(currentUser.isAdmin()){
                admin.setVisibility(View.VISIBLE);
            }
        }
    }

    public void signInDonor() {

        AlertDialog.Builder phoneDialog = new AlertDialog.Builder(MainActivity.this);
        phoneDialog.setTitle("Enter your Phone Number");
        final EditText phoneText = new EditText(MainActivity.this);
        phoneDialog.setView(phoneText);
        phoneDialog.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phoneNumber = phoneText.getText().toString().trim();
                dbRef.child(AppConstants.donors()).orderByChild("phoneNumber").equalTo(phoneNumber).limitToFirst(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                    Donor user = d.getValue(Donor.class);
                                    verifyPhoneNumber(user);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });

        phoneDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        phoneDialog.show();
    }

    public void verifyPhoneNumber(final Donor user) {

        PhoneAuthProvider phoneAuth = PhoneAuthProvider.getInstance();
        phoneAuth.verifyPhoneNumber(user.getPhoneNumber(), 120, TimeUnit.SECONDS, MainActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        writeToSharedPreference(user);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        AlertDialog.Builder otpDialog = new AlertDialog.Builder(MainActivity.this);
                        final EditText otpText = new EditText(MainActivity.this);
                        otpDialog.setTitle(getString(R.string.enter_otp_title));
                        otpDialog.setMessage(getString(R.string.enter_otp_message));
                        otpDialog.setView(otpText);
                        otpDialog.setCancelable(false);
                        otpDialog.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String enteredCode = otpText.getText().toString().trim();
                                String sentCode = PhoneAuthProvider.getCredential(s, enteredCode).getSmsCode();
                                if (sentCode.equals(enteredCode)) {
                                    writeToSharedPreference(user);
                                } else {
                                    AlertDialog.Builder vFailDialog = new AlertDialog.Builder(MainActivity.this);
                                    vFailDialog.setTitle("Failed");
                                    vFailDialog.setMessage(getString(R.string.verification_unsuccessful));
                                    vFailDialog.setPositiveButton("OK", null);
                                }
                            }
                        });
                        otpDialog.show();
                    }
                });
    }

    public void writeToSharedPreference(Donor donor) {
        SharedPreferences mPrefs = getSharedPreferences(AppConstants.currentUser(), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String jsonDonor = gson.toJson(donor);
        prefsEditor.putString(AppConstants.currentUser(), jsonDonor);
        prefsEditor.commit();
        finish();
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }
}

