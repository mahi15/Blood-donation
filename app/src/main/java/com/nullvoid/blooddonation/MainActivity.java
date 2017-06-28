package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    ProgressDialog verifying;

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

        if(currentUser != null){
            if(currentUser.isAdmin()){
                admin.setVisibility(View.VISIBLE);
            }
        }
    }

    public void checkIfDonorAlreadyExists() {

        if(!isNetworkAvailable()){
            Toast.makeText(MainActivity.this, getString(R.string.no_internet_message),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //show a dialog box for the user to enter the phone number
        AlertDialog.Builder phoneDialog = new AlertDialog.Builder(MainActivity.this);
        phoneDialog.setTitle("Enter your Phone Number");
        final EditText phoneText = new EditText(MainActivity.this);
        phoneText.setInputType(InputType.TYPE_CLASS_NUMBER);
        phoneText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        phoneDialog.setView(phoneText);
        phoneDialog.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //show a loader while verfying the number
                verifying = new ProgressDialog(MainActivity.this);
                verifying.setTitle("Loading");
                verifying.setMessage("Verifying your phone number");
                verifying.setCanceledOnTouchOutside(false);
                verifying.show();

                String phoneNumber = phoneText.getText().toString().trim();
                dbRef.child(AppConstants.donors()).orderByChild("phoneNumber").equalTo(phoneNumber).limitToFirst(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                    tempDonor = d.getValue(Donor.class);
                                }
                                if(tempDonor.getPhoneNumber() == null){
                                    verifying.dismiss();
                                    showFailedDialog("There is no donor with the given phone number");
                                    return;
                                }
                                verifyPhoneNumber(tempDonor.getPhoneNumber());
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                verifying.dismiss();
                                showFailedDialog("Something went wrong\nPlease try again later.");
                            }
                        });
            }
        });

        phoneDialog.setNegativeButton("CANCEL", null);
        phoneDialog.show();
    }

    public void showFailedDialog(String message){
        AlertDialog.Builder failedDialog = new AlertDialog.Builder(MainActivity.this);
        failedDialog.setTitle("Verification Failed");
        failedDialog.setMessage(message);
        failedDialog.setCancelable(false);
        failedDialog.setPositiveButton("OK", null);
        failedDialog.show();
    }

    public void verifyPhoneNumber(final String phoneNumber) {

        final PhoneAuthProvider phoneAuth = PhoneAuthProvider.getInstance();
        phoneAuth.verifyPhoneNumber(phoneNumber, 120, TimeUnit.SECONDS, MainActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInUser(phoneAuthCredential);
                        verifying.dismiss();
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        verifying.dismiss();
                        showFailedDialog(getString(R.string.verification_unsuccessful));
                    }

                    @Override
                    public void onCodeSent(final String verificationId, final PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        verifying.dismiss();
                        showOtpPrompt(verificationId);
                    }
                });
    }

    private void showOtpPrompt(final String verificationId){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText otpText = new EditText(MainActivity.this);

        otpText.setInputType(InputType.TYPE_CLASS_NUMBER);
        otpText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        builder.setTitle(getString(R.string.enter_otp_title));
        builder.setMessage(getString(R.string.enter_otp_message));
        builder.setView(otpText);
        builder.setCancelable(false);

        builder.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredCode = otpText.getText().toString().trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredCode);
                signInUser(credential);
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    public void signInUser(PhoneAuthCredential credential){

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
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

        mAuth.signOut();
        SharedPreferences mPrefs = getSharedPreferences(AppConstants.currentUser(), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear().apply();
        finish();
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

