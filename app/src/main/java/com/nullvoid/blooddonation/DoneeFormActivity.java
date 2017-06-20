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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nullvoid.blooddonation.beans.Donee;

import java.util.Calendar;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeFormActivity extends AppCompatActivity {
    private ArrayAdapter bloodGroupArray;
    private Spinner bloodGroupSpinner;
    private TextView name, phnumber, reqDate, reqAttendantName, reqAttendantNumber, pName, pId, hospitalName,
            hospitalNumber, hospitalAddress, hospitalPin;
    private Button submitButton;

    private ProgressDialog progressDialog;

    private String dName, dNumber, dRequiredDate, dBloodGroup, dAttendantName, dAttendantNumber, dPatietsName, dPatientRefNumber,
                    dHospitalName, dHospitalNumber, dHospitalAddress, dHospitalPincode, doneeId;
    private DatePicker datePicker;
    private Calendar calendar;

    private Donee donee;

    private FirebaseUser fbUser;
    private FirebaseDatabase dbRef;
    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_donee_request);

        bloodGroupSpinner = (Spinner)findViewById(R.id.reqBloodGroup);
        bloodGroupArray = ArrayAdapter.createFromResource(DoneeFormActivity.this, R.array.blood_group, android.R.layout.simple_spinner_item);
        bloodGroupArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupArray);

        submitButton = (Button)findViewById(R.id.reqSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                progressDialog = new ProgressDialog(DoneeFormActivity.this);
                progressDialog.setTitle("Registering");
                progressDialog.setMessage("Sending your Information");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                name = (TextView)findViewById(R.id.reqName);
                phnumber = (TextView)findViewById(R.id.reqPhoneNumber);
                reqDate = (TextView)findViewById(R.id.reqNeededDate);
                reqAttendantNumber = (TextView)findViewById(R.id.reqPatAttendantNumber);
                pName  = (TextView)findViewById(R.id.reqPName);
                pId  = (TextView)findViewById(R.id.reqPId);
                hospitalName = (TextView)findViewById(R.id.reqHospitalName);
                hospitalNumber = (TextView)findViewById(R.id.reqHospitalAddress);
                hospitalAddress = (TextView)findViewById(R.id.reqHospitalAddress);
                hospitalPin = (TextView)findViewById(R.id.reqHospitalPin);
                reqAttendantName = (TextView)findViewById(R.id.reqPatAttendantName);
                donee = new Donee();

                dName = name.getText().toString().toString();
                dNumber = phnumber.getText().toString().trim();
                dBloodGroup = bloodGroupSpinner.getSelectedItem().toString();
                dRequiredDate = reqDate.getText().toString().trim();
                dAttendantName = reqAttendantName.getText().toString().trim();
                dAttendantNumber = reqAttendantNumber.getText().toString().trim();
                dPatietsName = pName.getText().toString().trim();
                dPatientRefNumber = pId.getText().toString().trim();
                dHospitalName = hospitalName.getText().toString().trim();
                dHospitalNumber = hospitalNumber.getText().toString().trim();
                dHospitalAddress = hospitalAddress.getText().toString().trim();
                dHospitalPincode = hospitalPin.getText().toString().trim();

                if(!validateForm()){
                    progressDialog.dismiss();
                    return;
                }

                donee.setName(toCamelCase(dName));
                donee.setPhoneNumber(dNumber);
                donee.setBloodGroup(dBloodGroup);
                donee.setReqDate(dRequiredDate);
                donee.setPatientAttendantName(dAttendantName);
                donee.setPatientAttendantNumber(dAttendantNumber);
                donee.setPatientName(toCamelCase(dPatietsName));
                donee.setPatientID(dPatientRefNumber);
                donee.setHospitalName(toCamelCase(dHospitalName));
                donee.setHospitalNumber(dHospitalNumber);
                donee.setHospitalAddress(dHospitalAddress);
                donee.setHospitalPin(dHospitalPincode);

                registerDonee(donee);
            }
        });
    }

    public void registerDonee(Donee donee){

        dbRef = FirebaseDatabase.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        uId = fbUser.getUid();

        //create unique key for donee
        doneeId = dbRef.getReference().push().getKey();

        donee.setDoneeId(doneeId);
        donee.setRequestedBy(uId);
        dbRef.getReference("donee").child(doneeId).setValue(donee).
        addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.cancel();
                if (task.isSuccessful()) {
                    //If user is added to database
                    showToast("Request Successfully Sent!");
                    finish();
                    startActivity(new Intent(DoneeFormActivity.this, MainActivity.class));
                } else {
                    //if it fails
                    showToast("Something went wrong :(");
                }
            }
        });

    }

    public void showToast(String text){
        Toast.makeText(DoneeFormActivity.this, text, Toast.LENGTH_SHORT).show();
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
        if (TextUtils.isEmpty(dAttendantName)) {
            reqAttendantName.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dAttendantNumber)) {
            reqAttendantNumber.setError("Required");
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

    public String toCamelCase(final String init) {
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }
}
