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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nullvoid.blooddonation.beans.Donee;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeFrom extends AppCompatActivity {
    private ArrayAdapter bloodGroupArray;
    private Spinner bloodGroupSpinner;
    private TextView name, phnumber, reqDate, reqTime, pName, pId, hospitalName;
    private TextView hospitalNumber, hospitalAddress, hospitalPin;
    private Button submitButton;

    ProgressDialog progressDialog;

    private String dName, dNumber, dRequiredDate, dBloodGroup, dRequiredTime, dPatietsName, dPatientRefNumber,
                    dHospitalName, dHospitalNumber, dHospitalAddress, dHospitalPincode;

    private Donee donee;

    private FirebaseUser fbUser;
    private FirebaseDatabase dbRef;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.donee_request_layout);

        bloodGroupSpinner = (Spinner)findViewById(R.id.reqBloodGroup);
        bloodGroupArray = ArrayAdapter.createFromResource(this, R.array.blood_group, android.R.layout.simple_spinner_item);
        bloodGroupArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupArray);
        name = (TextView)findViewById(R.id.reqName);
        phnumber = (TextView)findViewById(R.id.reqPhoneNumber);
        reqDate = (TextView)findViewById(R.id.reqNeededDate);
        reqTime  = (TextView)findViewById(R.id.reqNeededTime);
        pName  = (TextView)findViewById(R.id.reqPName);
        pId  = (TextView)findViewById(R.id.reqPId);
        hospitalName = (TextView)findViewById(R.id.reqHospitalName);
        hospitalNumber = (TextView)findViewById(R.id.reqHospitalAddress);
        hospitalAddress = (TextView)findViewById(R.id.reqHospitalAddress);
        hospitalPin = (TextView)findViewById(R.id.reqHospitalPin);
        submitButton = (Button)findViewById(R.id.reqSubmit);
        donee = new Donee();


        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dName = name.getText().toString().toString();
                dNumber = phnumber.getText().toString().trim();
                dBloodGroup = bloodGroupSpinner.getSelectedItem().toString();
                dRequiredDate = reqDate.getText().toString().trim();
                dRequiredTime = reqTime.getText().toString().trim();
                dPatietsName = pName.getText().toString().trim();
                dPatientRefNumber = pId.getText().toString().trim();
                dHospitalName = hospitalName.getText().toString().trim();
                dHospitalNumber = hospitalNumber.getText().toString().trim();
                dHospitalAddress = hospitalAddress.getText().toString().trim();
                dHospitalPincode = hospitalPin.getText().toString().trim();

                if(!validateForm()){return;}

                donee.setName(dName);
                donee.setPhoneNumber(dNumber);
                donee.setBloodGroup(dBloodGroup);
                donee.setReqDate(dRequiredDate);
                donee.setReqTime(dRequiredTime);
                donee.setPatientName(dPatietsName);
                donee.setPatientID(dPatientRefNumber);
                donee.setHospitalName(dHospitalName);
                donee.setHospitalNumber(dHospitalNumber);
                donee.setHospitalAddress(dHospitalAddress);
                donee.setHospitalPin(dHospitalPincode);

                registerDonee(donee);
            }
        });
    }

    public void registerDonee(Donee donee){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending your Information...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbRef = FirebaseDatabase.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        uId = fbUser.getUid();

        donee.setRequestedBy(uId);
        dbRef.getReference("donee").child(donee.getPatientID()).setValue(donee).
        addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.cancel();
                if (task.isSuccessful()) {
                    //If user is added to database
                    showToast("Request Successfully Sent!");
                    finish();
                    startActivity(new Intent(DoneeFrom.this, MainActivity.class));
                } else {
                    //if it fails
                    showToast("Something went wrong :(");
                }
            }
        });

    }

    public void showToast(String text){
        Toast.makeText(DoneeFrom.this, text, Toast.LENGTH_SHORT).show();
    }

    public boolean validateForm() {
        //validate if the data entered by user is valid nor not
        if (TextUtils.isEmpty(dName)) {
            name.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dNumber)) {
            phnumber.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dRequiredDate)) {
            reqDate.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dRequiredTime)) {
            reqTime.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dPatietsName)) {
            pName.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dPatientRefNumber)) {
            pId.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dHospitalName)) {
            hospitalName.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dHospitalNumber)) {
            hospitalNumber.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dHospitalAddress)) {
            hospitalAddress.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dHospitalPincode)) {
            hospitalPin.setError("Required");
            return false;
        }
        return true;
    }
}
