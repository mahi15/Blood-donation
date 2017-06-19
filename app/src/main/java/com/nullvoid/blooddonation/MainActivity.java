package com.nullvoid.blooddonation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button donateBlood, reqBlood, logout, admin;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        donateBlood = (Button) findViewById(R.id.btn_donate_blood);
        reqBlood = (Button) findViewById(R.id.btn_req_blood);
        logout = (Button) findViewById(R.id.btn_logout);
        admin = (Button) findViewById(R.id.admin);

        mAuth = FirebaseAuth.getInstance();

        logout.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
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

    @Override
    protected void onStart() {
        super.onStart();

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
