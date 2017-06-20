package com.nullvoid.blooddonation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    Button donateBlood, reqBlood, logout, admin;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        admin = (Button) findViewById(R.id.admin);
        isUserAdmin();

        donateBlood = (Button) findViewById(R.id.btn_donate_blood);
        reqBlood = (Button) findViewById(R.id.btn_req_blood);
        logout = (Button) findViewById(R.id.btn_logout);

        logout.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                getSharedPreferences(getString(R.string.user_details), Context.MODE_PRIVATE).edit().clear().apply();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        reqBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLoginStatus() != null){
                    startActivity(new Intent(MainActivity.this, DoneeFormActivity.class));
                }else{
                    showToast("You Must Login First");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        donateBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLoginStatus() != null){
                    startActivity(new Intent(MainActivity.this, DonorRegistrationActivity.class));
                }else{
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

    public void isUserAdmin(){
        boolean isAdmin;
        dbRef.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("admin").getValue(Boolean.class)){
                    admin.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public String checkLoginStatus(){
        fbUser = mAuth.getCurrentUser();
        if(fbUser != null){
            return fbUser.getUid();
        }else{
            return null;
        }
    }

    public void showToast(String textToToast) {
        Toast.makeText(this, textToToast, Toast.LENGTH_SHORT).show();
    }
}
