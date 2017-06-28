package com.nullvoid.blooddonation;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.others.AppConstants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeRequestActivity extends AppCompatActivity {

    //all the view elements
    private ArrayAdapter bloodGroupArray;
    private Spinner bloodGroupSpinner;
    private TextView name, phnumber, reqDate, reqAmount, reqAttendantName, reqAttendantNumber, pName, pId, hospitalName,
                    hospitalNumber, hospitalAddress, hospitalPin, reqAreaOfResidence;
    private Toolbar toolbar;
    private Button submitButton;
    private ProgressDialog progressDialog;

    //String for all the fields
    private String dName, dNumber, dRequiredDate, dBloodGroup, dReqAmount, dRequestedDate,
            dAttendantName, dAttendantNumber, dPatietsName, dPatientRefNumber,
            dHospitalName, dHospitalNumber, dHospitalAddress, dHospitalPincode, doneeId, dPAreaOfResidence;

    //Current date
    Calendar calendar = Calendar.getInstance();
    int cDay = calendar.get(Calendar.DAY_OF_MONTH);
    int cMonth = calendar.get(Calendar.MONTH);
    int cYear = calendar.get(Calendar.YEAR);
    int sDay, sMonth, sYear;

    //object to store all the user entered data
    private Donee donee;

    //firebase database stuff
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_donee_request);

        //Initilize important objects
        dbRef = FirebaseDatabase.getInstance().getReference();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bloodGroupSpinner = (Spinner)findViewById(R.id.reqBloodGroup);
        bloodGroupArray = ArrayAdapter.createFromResource(DoneeRequestActivity.this,
                R.array.blood_group, android.R.layout.simple_spinner_item);
        submitButton = (Button)findViewById(R.id.reqSubmit);
        reqDate = (TextView)findViewById(R.id.reqNeededDate);

        //getting ready
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bloodGroupArray.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        bloodGroupSpinner.setAdapter(bloodGroupArray);
        submitButton.setOnClickListener(getInfo);
        reqDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(DoneeRequestActivity.this
                        ,datePickerListener, cYear, cMonth, cDay);
                datePicker.show();
            }
        });

    }

    View.OnClickListener getInfo = new View.OnClickListener() {
        public void onClick(View v) {
            progressDialog = new ProgressDialog(DoneeRequestActivity.this);
            progressDialog.setTitle("Registering");
            progressDialog.setMessage("Sending your Information");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            name = (TextView)findViewById(R.id.reqName);
            phnumber = (TextView)findViewById(R.id.reqPhoneNumber);
            reqAttendantNumber = (TextView)findViewById(R.id.reqPatAttendantNumber);
            pName  = (TextView)findViewById(R.id.reqPName);
            reqAmount = (TextView) findViewById(R.id.requiredAmount);
            pId  = (TextView)findViewById(R.id.reqPId);
            hospitalName = (TextView)findViewById(R.id.reqHospitalName);
            hospitalNumber = (TextView)findViewById(R.id.reqHospitalAddress);
            hospitalAddress = (TextView)findViewById(R.id.reqHospitalAddress);
            hospitalPin = (TextView)findViewById(R.id.reqHospitalPin);
            reqAttendantName = (TextView)findViewById(R.id.reqPatAttendantName);
            reqAreaOfResidence = (TextView)findViewById(R.id.reqPAreaOfResidence);
            donee = new Donee();

            dName = name.getText().toString().toString();
            dNumber = phnumber.getText().toString().trim();
            dBloodGroup = bloodGroupSpinner.getSelectedItem().toString();
            dRequiredDate = reqDate.getText().toString().trim();
            dReqAmount = reqAmount.getText().toString().trim();
            dAttendantName = reqAttendantName.getText().toString().trim();
            dAttendantNumber = reqAttendantNumber.getText().toString().trim();
            dPatietsName = pName.getText().toString().trim();
            dPatientRefNumber = pId.getText().toString().trim();
            dHospitalName = hospitalName.getText().toString().trim();
            dHospitalNumber = hospitalNumber.getText().toString().trim();
            dHospitalAddress = hospitalAddress.getText().toString().trim();
            dHospitalPincode = hospitalPin.getText().toString().trim();
            dPAreaOfResidence = reqAreaOfResidence.getText().toString().trim();

            if(!validateForm()){
                progressDialog.dismiss();
                return;
            }

            SimpleDateFormat myDate = new SimpleDateFormat("dd/MM/yyyy/HH:mm");
            dRequestedDate = myDate.format(new Timestamp(System.currentTimeMillis()));

            donee.setRequesterName(toCamelCase(dName));
            donee.setRequesterPhoneNumber(dNumber);
            donee.setRequiredBloodGroup(dBloodGroup);
            donee.setRequiredAmount(dReqAmount);
            donee.setRequiredDate(dRequiredDate);
            donee.setPatientAttendantName(dAttendantName);
            donee.setPatientAttendantNumber(dAttendantNumber);
            donee.setPatientName(toCamelCase(dPatietsName));
            donee.setPatientID(dPatientRefNumber);
            donee.setHospitalName(toCamelCase(dHospitalName));
            donee.setHospitalNumber(dHospitalNumber);
            donee.setHospitalAddress(dHospitalAddress);
            donee.setHospitalPin(dHospitalPincode);
            donee.setPatientAreaofResidence(dPAreaOfResidence);
            donee.setRequestedDate(dRequestedDate);

            registerDonee(donee);
        }
    };

    public void registerDonee(Donee donee){

        //create unique key for donee
        doneeId = dbRef.push().getKey();
        donee.setDoneeId(doneeId);

        dbRef.child(AppConstants.donees()).child(doneeId).setValue(donee).
        addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.cancel();
                if (task.isSuccessful()) {
                    //If user is added to database
                    showToast("Request Successfully Sent!");
                    finish();
                    startActivity(new Intent(DoneeRequestActivity.this, MainActivity.class));
                } else {
                    //if it fails
                    showToast("Something went wrong :(");
                }
            }
        });

    }

    public void showToast(String text){
        Toast.makeText(DoneeRequestActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public boolean validateForm() {
        //validate if the data entered by user is valid nor not
        if (dName.length() < 3) {
            name.setError("Enter a valid name");
            name.requestFocus();
            return false;
        }
        if (dNumber.length() != 10) {
            phnumber.setError("Not Valid");
            phnumber.requestFocus();
            return false;
        }
        if(sDay < cDay || sMonth < cMonth || sYear < cYear){
            reqDate.setError("Select a valid date");
            reqDate.requestFocus();
            return false;
        }
        if (dPatietsName.length() < 3) {
            pName.setError("Enter a valid name");
            pName.requestFocus();
            return false;
        }
        if (dPAreaOfResidence.length() < 4) {
            reqAreaOfResidence.setError("Not Valid");
            reqAreaOfResidence.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dPatientRefNumber)) {
            dPatientRefNumber = "Not Provided";
            pId.requestFocus();
        }
        if (dAttendantName.length() < 3) {
            reqAttendantName.setError("Not Valid");
            reqAttendantName.requestFocus();
            return false;
        }
        if (dAttendantNumber.length() != 10) {
            reqAttendantNumber.setError("Not Valid");
            reqAttendantNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dHospitalName)) {
            hospitalName.setError("Required");
            hospitalName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dHospitalNumber)) {
            hospitalNumber.setError("Required");
            hospitalNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dHospitalAddress)) {
            hospitalAddress.setError("Required");
            hospitalAddress.requestFocus();
            return false;
        }
        if (dHospitalPincode.length() != 6) {
            hospitalPin.setError("Not Valid");
            hospitalPin.requestFocus();
            return false;
        }
        if(dBloodGroup.equals("Choose blood group")){
            TextView errorText = (TextView)bloodGroupSpinner.getSelectedView();
            errorText.setError("Please select the blood group!");
            errorText.setTextColor(Color.RED);
            errorText.requestFocus();
            errorText.setText("Please select the blood group!");
            bloodGroupSpinner.requestFocus();
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

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            sYear = selectedYear;
            sMonth = selectedMonth + 1;
            sDay = selectedDay;
            dRequiredDate = String.valueOf(sDay) + "/" + String.valueOf(sMonth) + "/" + String.valueOf(sYear);
            reqDate.setText(dRequiredDate);
        }
    };
}
