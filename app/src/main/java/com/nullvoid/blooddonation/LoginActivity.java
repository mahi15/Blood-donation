package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nullvoid.blooddonation.beans.User;

import static android.widget.Toast.makeText;

/**
 * Created by sanath on 10/06/17.
 */

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText email, password;
    TextView forgotPassword, registerLink;
    ProgressDialog pd;

    String userEmail, userPassword;
    User user;

    //firebase stuff
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        checkLoginStatus();

        loginButton = (Button) findViewById(R.id.login_button);
        forgotPassword = (TextView) findViewById(R.id.forgot_pass);
        registerLink = (TextView) findViewById(R.id.register_link);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Logging In..");
                pd.setCancelable(false);
                pd.show();

                email = (EditText) findViewById(R.id.l_email);  // Get the user data
                password = (EditText) findViewById(R.id.l_password);

                userEmail = email.getText().toString().trim();
                userPassword = password.getText().toString().trim();

                if(!validateForm()){
                    pd.dismiss();
                    return;}

                validateUser(userEmail, userPassword);
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, UserSignupActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetEmail();
            }
        });
    }

    public void checkLoginStatus(){
        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    public void validateUser(String email, String password){
        //firebase method to signin the user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            pd.dismiss();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            pd.dismiss();
                            showToast("Login Failed :(");
                        }
                    }
                });
    }

    public void sendResetEmail(){
        final String[] resetEmail = new String[1];
        final ProgressDialog pd = new ProgressDialog(this);
        final AlertDialog.Builder passResetDialog = new AlertDialog.Builder(this);

        final AlertDialog.Builder resultDialog = new AlertDialog.Builder(this);
        resultDialog.setPositiveButton("OK", null);

        pd.setMessage("Sending Email");
        pd.setCanceledOnTouchOutside(false);

        passResetDialog.setTitle("Password Reset");
        passResetDialog.setMessage("Please enter your email to send the password reset email");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passResetDialog.setView(input);

        passResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetEmail[0] = input.getText().toString();
                if(TextUtils.isEmpty(resetEmail[0])){
                    input.setError("Can't be empty!");
                    return;
                }
                pd.show();
                FirebaseAuth.getInstance().sendPasswordResetEmail(resetEmail[0])
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    resultDialog.setTitle("Successfully Send");
                                    resultDialog.setMessage("The password reset email has been successfully sent to" +
                                            "the email '"+resetEmail[0]+"'");
                                    resultDialog.show();
                                }else{
                                    pd.dismiss();
                                    passResetDialog.setTitle("Error");
                                    passResetDialog.setMessage("Unable to send password reset email");

                                }
                            }
                        });
            }
        });
        passResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                pd.dismiss();
            }
        });
        passResetDialog.show();
    }

    public void showToast(String textToToast) {
        makeText(this, textToToast, Toast.LENGTH_SHORT).show();
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
