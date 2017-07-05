package com.nullvoid.blooddonation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
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
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    MaterialDialog loadingDialog;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    Donor currentUser;
    Donor tempDonor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        admin = (Button) findViewById(R.id.admin);

        setCurrentUserFromSharedPreference();

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

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        drawerLayout.closeDrawers();
                        drawerLayout.clearFocus();
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
                        if (currentUser == null) {
                            checkIfDonorAlreadyExists();
                        } else {
                            startActivity(new Intent(getApplicationContext(), DonorProfileActivity.class));
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        logoutUser();
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

    public void setCurrentUserFromSharedPreference() {
        SharedPreferences mPref = getSharedPreferences(AppConstants.currentUser(), MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPref.getString(AppConstants.currentUser(), null);
        currentUser = gson.fromJson(json, Donor.class);

        if (currentUser != null) {
            if (currentUser.isAdmin()) {
                admin.setVisibility(View.VISIBLE);
            }
        }
    }

    public void checkIfDonorAlreadyExists() {

        if (!isNetworkAvailable()) {
            showSnackBar(getString(R.string.no_internet_message));
            return;
        }


        new MaterialDialog.Builder(this)
                .title(R.string.enter_number_title)
                .content(R.string.enter_number_message)
                .contentColor(Color.BLACK)
                .cancelable(false)
                .negativeText(R.string.cancel)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .inputRange(10,10, Color.RED)
                .input(getString(R.string.enter_here), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        loadingDialog = new MaterialDialog.Builder(MainActivity.this)
                                .title(R.string.loading)
                                .content(R.string.please_wait_message)
                                .progress(true, 0)
                                .cancelable(false)
                                .show();

                        String number = input.toString().trim();
                        dbRef.child(AppConstants.donors()).orderByChild(AppConstants.phoneNumber())
                                .equalTo(number).limitToFirst(1)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                                            tempDonor = d.getValue(Donor.class);
                                        }
                                        if (tempDonor == null) {
                                            loadingDialog.dismiss();
                                            showFailedDialog(getString(R.string.no_donor_message));
                                            return;
                                        }
                                        verifyPhoneNumber(tempDonor.getPhoneNumber());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        loadingDialog.dismiss();
                                        showFailedDialog(getString(R.string.on_cancelled_message));
                                    }
                                });
                    }
                }).show();
    }

    public void showFailedDialog(String message) {

        new MaterialDialog.Builder(MainActivity.this)
                .neutralText(R.string.ok)
                .title(R.string.failed)
                .content(message)
                .show();
    }

    public void verifyPhoneNumber(final String phoneNumber) {

        final PhoneAuthProvider phoneAuth = PhoneAuthProvider.getInstance();
        phoneAuth.verifyPhoneNumber(phoneNumber, 120, TimeUnit.SECONDS, MainActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        loadingDialog.dismiss();
                        showFailedDialog(getString(R.string.verification_failed));
                    }

                    @Override
                    public void onCodeSent(final String verificationId, final PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        showOtpPrompt(verificationId);
                    }
                });
    }

    private void showOtpPrompt(final String verificationId) {

        new MaterialDialog.Builder(MainActivity.this)
                .cancelable(false)
                .positiveText(R.string.submit)
                .negativeText(R.string.cancel)
                .title(R.string.enter_otp_title)
                .content(R.string.enter_otp_message)
                .input(R.string.enter_here, R.string.blank, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String enteredCode = input.toString().trim();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredCode);
                        signInUser(credential);
                    }
                })
                .show();
    }

    public void signInUser(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    writeToSharedPreference(tempDonor);
                } else {
                    showFailedDialog("Unable to verify\nPlease try again later");
                }
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

    public void logoutUser() {
        if (currentUser == null) {
            new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.error)
                    .content(R.string.logout_error_message)
                    .positiveText(R.string.ok)
                    .show();
        } else {

            final SharedPreferences mPrefs = getSharedPreferences(AppConstants.currentUser(), MODE_PRIVATE);
            new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.confirm)
                    .content(R.string.logout_confirm_message)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mAuth.signOut();
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.clear().apply();
                            finish();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }
                    })
                    .show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showSnackBar(String text) {
        Snackbar.make(drawerLayout, text, Snackbar.LENGTH_SHORT).show();
    }
}

