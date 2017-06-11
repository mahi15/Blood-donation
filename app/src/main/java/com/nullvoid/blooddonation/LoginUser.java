package com.nullvoid.blooddonation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sanath on 10/06/17.
 */

public class LoginUser extends AppCompatActivity {

    Button loginButton;
    EditText email, password;
    TextView forgotPassword, registerLink;

    String userEmail, userPassword;

    //firebase stuff
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        loginButton = (Button) findViewById(R.id.login_button);
        email = (EditText) findViewById(R.id.l_email);
        password = (EditText) findViewById(R.id.l_password);
        forgotPassword = (TextView) findViewById(R.id.forgot_pass);
        registerLink = (TextView) findViewById(R.id.register_link);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userEmail = email.getText().toString().trim();
                userPassword = password.getText().toString().trim();

                if(!validateForm()){return;}

                validateUser(userEmail, userPassword);

            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginUser.this, SignupUser.class));
            }
        });
    }

    public void validateUser(String email, String password){
        //get an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        //firebase method to signin the user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showToast("Logged In Succesfully!");
                            finish();
                            //startActivity(new Intent(getApplicationContext(), HomePage.class));
                        } else {
                            showToast("Login Failed :(");
                        }
                    }
                });
    }

    public void showToast(String textToToast) {
        Toast.makeText(this, textToToast, Toast.LENGTH_SHORT).show();
    }

    public boolean validateForm() {
        //validate if the data entered by user is valid nor not
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Required");
            return false;
        } else if (!userEmail.contains("@")) {
            email.setError("Enter a valid email");
            return false;
        }
        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Required");
            return false;
        } else if (userPassword.length() < 4) {
            password.setError("Password should be more than 4");
            return false;
        }
        return true;
    }
}