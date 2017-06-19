package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nullvoid.blooddonation.beans.Donor;

/**
 * Created by sanath on 10/06/17.
 */

/**
 * Created by MeowMeow on 4/30/2017.
 */

public class DonorRegistrationActivity extends AppCompatActivity {

    private String dName, dGender, dBloodGroup, dAge, dDOB, dDonationDate, dNumber, dEmail, dAddress, dLocation;
    private String dPincode;

    ArrayAdapter bloodGroupArray;
    Button submitButton;
    EditText name, email, age, phoneNumber, dateOfBirth, address;
    EditText location, pincode, DateOfDonation;
    Spinner bloodGroupSpinner;
    RadioGroup Gender, DonatedBefore;
    RadioButton gender, donatedBefore;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    FirebaseDatabase dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donor_registration_layout);


        //Add the blood groups to the bloodGroupSpinner (drop down list) in the donorDetails page
        bloodGroupSpinner = (Spinner)findViewById(R.id.regBloodGroup);
        bloodGroupArray = ArrayAdapter.createFromResource(this, R.array.blood_group, android.R.layout.simple_spinner_item);
        bloodGroupArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupArray);

        submitButton = (Button)findViewById(R.id.regSubmit);

        DonatedBefore = (RadioGroup)findViewById(R.id.regDonatedBeforeOption);
        DateOfDonation = (EditText)findViewById(R.id.regDateOfDonation);
        DateOfDonation.setVisibility(View.GONE);
        DonatedBefore.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                donatedBefore = (RadioButton)findViewById(DonatedBefore.getCheckedRadioButtonId());
                if(donatedBefore.getText().toString().equals("Yes")){
                    DateOfDonation.setVisibility(View.VISIBLE);
                }
                else{
                    DateOfDonation.setVisibility(View.GONE);
                }
            }
        });


        //Now add all the donorDetails input to a object
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                name = (EditText)findViewById(R.id.regName);
                Gender = (RadioGroup) findViewById(R.id.regGender);
                age = (EditText)findViewById(R.id.regAge);
                dateOfBirth = (EditText)findViewById(R.id.regDateOfBirth);
                email = (EditText)findViewById(R.id.regEmail);
                phoneNumber = (EditText)findViewById(R.id.regPhoneNumber);
                address = (EditText)findViewById(R.id.regAddress);
                location = (EditText)findViewById(R.id.regLocation);
                pincode = (EditText)findViewById(R.id.regPinCode);
                Donor donor = new Donor();

                dName = name.getText().toString().trim();
                gender = (RadioButton)findViewById(Gender.getCheckedRadioButtonId());
                dGender = gender.getText().toString();
                dBloodGroup = bloodGroupSpinner.getSelectedItem().toString();
                dAge = age.getText().toString().trim();
                dDOB = dateOfBirth.getText().toString();
                dNumber = phoneNumber.getText().toString();
                dEmail = email.getText().toString();
                dAddress = address.getText().toString().trim();
                dLocation = location.getText().toString().trim();
                dPincode = pincode.getText().toString().trim();

                if(DateOfDonation.getVisibility() == View.VISIBLE){
                    dDonationDate = DateOfDonation.getText().toString();
                }else{
                    dDonationDate = "none";
                }

                if(!validateForm()){return;}

                donor.setName(dName);
                donor.setGender(dGender);
                donor.setBloodGroup(dBloodGroup);
                donor.setDateOfBirth(dDOB);
                donor.setAge(dAge);
                donor.setDonatedDate(dDonationDate);
                donor.setPhoneNumber(dNumber);
                donor.setEmail(dEmail);
                donor.setAddress(dAddress);
                donor.setLocation(dLocation);
                donor.setPincode(dPincode);

                registerDonor(donor);
            }
        });
    }

    public void registerDonor(Donor donor){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Your Information..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance();
        String uId = fbUser.getUid();

        dbRef.getReference("donors").child(uId).setValue(donor).
        addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.cancel();
                if (task.isSuccessful()) {
                    //If user is added to database
                    showToast("Thank You for Registering :)");
                    finish();
                    startActivity(new Intent(DonorRegistrationActivity.this, MainActivity.class));
                } else {
                    //if it fails
                    showToast("Something went wrong :(");
                }
            }
        });
    }


    public void showToast(String text){
        Toast.makeText(DonorRegistrationActivity.this, text, Toast.LENGTH_SHORT).show();
    }


    public boolean validateForm() {
        //validate if the data entered by user is valid nor not
        if (TextUtils.isEmpty(dName)) {
            name.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dAge)) {
            age.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dDOB)) {
            dateOfBirth.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dDonationDate)) {
            DateOfDonation.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dNumber)) {
            phoneNumber.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dEmail)) {
            email.setError("Required");
            return false;
        } else if (!dEmail.contains("@")) {
            email.setError("Enter a valid email");
            return false;
        }
        if (TextUtils.isEmpty(dAddress)) {
            address.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dLocation)) {
            location.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dPincode)) {
            pincode.setError("Required");
            return false;
        }
        return true;
    }
}