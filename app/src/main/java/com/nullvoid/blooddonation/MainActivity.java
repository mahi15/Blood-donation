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

    Button donateBlood, reqBlood, login, admin;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        donateBlood = (Button) findViewById(R.id.btn_donate_blood);
        reqBlood = (Button) findViewById(R.id.btn_req_blood);
        login = (Button) findViewById(R.id.btn_login);
        admin = (Button) findViewById(R.id.admin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginUser.class));
            }
        });

        reqBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLoginStatus() != null){
                    startActivity(new Intent(MainActivity.this, DoneeFrom.class));
                }else{
                    showToast("You Must Login First");
                    startActivity(new Intent(MainActivity.this, LoginUser.class));
                }
            }
        });

        donateBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLoginStatus() != null){
                    startActivity(new Intent(MainActivity.this, DonorRegistration.class));
                }else{
                    showToast("You Must Login First");
                    startActivity(new Intent(MainActivity.this, LoginUser.class));
                }
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DoneeList.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public String checkLoginStatus(){
        mAuth = FirebaseAuth.getInstance();
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
