package com.nullvoid.blooddonation;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by sanath on 10/06/17.
 */

/**
 * Created by MeowMeow on 4/30/2017.
 */

public class DonorRegistrationActivity extends AppCompatActivity {

    ArrayAdapter bloodGroupAdapter;
    Button submitButton;
    EditText name, email, age, phoneNumber, dateOfBirth, address, location, pincode;
    Spinner bloodGroupSpinner;
    RadioGroup Gender, DonatedBefore;
    RadioButton gender, donatedBefore;
    ProgressDialog progressDialog;
    Calendar c = Calendar.getInstance();
    int cDay = c.get(Calendar.DAY_OF_MONTH), cMonth = c.get(Calendar.MONTH), cYear = c.get(Calendar.YEAR),
            sDay, sMonth, sYear;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    Donor donor;
    private String dName, dGender, dBloodGroup, dAge, dDOB, dNumber, dEmail, dAddress, dLocation,
            dPincode, dRegisteredDate, enteredCode;
    private boolean dDonationInLastSixMonths;

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            sYear = selectedYear;
            sMonth = selectedMonth + 1;
            sDay = selectedDay;
            dDOB = String.valueOf(sDay) + "/" + String.valueOf(sMonth) + "/" + String.valueOf(sYear);
            dateOfBirth.setText(dDOB);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_donor_registration);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //Add the blood groups to the bloodGroupSpinner (drop down list) in the donorDetails page
        bloodGroupSpinner = (Spinner) findViewById(R.id.regBloodGroup);
        bloodGroupAdapter = ArrayAdapter.createFromResource(this, R.array.blood_group, android.R.layout.simple_spinner_item);
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);

        DonatedBefore = (RadioGroup) findViewById(R.id.regDonatedBeforeOption);
        DonatedBefore.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                donatedBefore = (RadioButton) findViewById(DonatedBefore.getCheckedRadioButtonId());
                if (donatedBefore.getText().toString().equals("Yes")) {
                    dDonationInLastSixMonths = true;
                } else {
                    dDonationInLastSixMonths = false;
                }
            }
        });

        dateOfBirth = (EditText) findViewById(R.id.regDateOfBirth);
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(DonorRegistrationActivity.this
                        , datePickerListener, cYear, cMonth, cDay);
                datePicker.show();
            }
        });

        submitButton = (Button) findViewById(R.id.regSubmit);
        submitButton.setOnClickListener(registerDonor);
    }

    //Registration logic
    View.OnClickListener registerDonor = new View.OnClickListener() {

        public void onClick(View v) {

            if(!isNetworkAvailable()){
                showToast(getString(R.string.no_internet_message));
                return;
            }

            progressDialog = new ProgressDialog(DonorRegistrationActivity.this);
            progressDialog.setMessage("Registering Donor");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            name = (EditText) findViewById(R.id.regName);
            Gender = (RadioGroup) findViewById(R.id.regGender);
            age = (EditText) findViewById(R.id.regAge);
            email = (EditText) findViewById(R.id.regEmail);
            phoneNumber = (EditText) findViewById(R.id.regPhoneNumber);
            address = (EditText) findViewById(R.id.regAddress);
            location = (EditText) findViewById(R.id.regLocation);
            pincode = (EditText) findViewById(R.id.regPinCode);

            dName = name.getText().toString().trim();
            gender = (RadioButton) findViewById(Gender.getCheckedRadioButtonId());
            dGender = gender.getText().toString();
            dBloodGroup = bloodGroupSpinner.getSelectedItem().toString();
            dAge = age.getText().toString().trim();
            dDOB = dateOfBirth.getText().toString();
            dNumber = phoneNumber.getText().toString();
            dEmail = email.getText().toString();
            dAddress = address.getText().toString().trim();
            dLocation = location.getText().toString().trim();
            dPincode = pincode.getText().toString().trim();

            if (!validateForm()) {
                progressDialog.dismiss();
                return;
            }

            donor = new Donor();
            SimpleDateFormat myDate = new SimpleDateFormat("dd/MM/yyyy/HH:mm");
            dRegisteredDate = myDate.format(new Timestamp(System.currentTimeMillis()));

            donor.setName(toCamelCase(dName));
            donor.setGender(dGender);
            donor.setBloodGroup(dBloodGroup);
            donor.setDateOfBirth(dDOB);
            donor.setAge(dAge);
            donor.setDonationInLastSixMonths(dDonationInLastSixMonths);
            donor.setPhoneNumber(dNumber);
            donor.setEmail(dEmail);
            donor.setAddress(dAddress);
            donor.setLocation(toCamelCase(dLocation));
            donor.setPincode(dPincode);
            donor.setRegisteredDate(dRegisteredDate);
            donor.setAdmin(false);

            checkIfDonorAlreadyExist();
        }
    };
    public boolean validateForm() {
        //validate if the data entered by user is valid nor not
        if (TextUtils.isEmpty(dName)) {
            name.setError("Required");
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dAge)) {
            age.setError("Required");
            age.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dDOB)) {
            dateOfBirth.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(dNumber)) {
            phoneNumber.setError("Required");
            phoneNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dEmail)) {
            email.setError("Required");
            return false;
        } else if (!dEmail.contains("@") && dEmail.contains(".")) {
            email.setError("Enter a valid email");
            email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dAddress)) {
            address.setError("Required");
            address.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dLocation)) {
            location.setError("Required");
            location.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dPincode)) {
            pincode.setError("Required");
            pincode.requestFocus();
            return false;
        }
        if (dBloodGroup.equals("Choose blood group")) {
            TextView errorText = (TextView) bloodGroupSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select the blood group!");
            errorText.requestFocus();
            return false;
        }
        return true;
    }

    public void checkIfDonorAlreadyExist(){

        dbRef.child(AppConstants.donors()).orderByChild("phoneNumber").equalTo(donor.getPhoneNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() == 1){
                            //if there is already a user with the phone nubmer then show an error alert
                            AlertDialog.Builder alreadyExistPrompt = new AlertDialog.Builder(DonorRegistrationActivity.this);
                            alreadyExistPrompt.setTitle("Cannot Register");
                            alreadyExistPrompt.setMessage(getString(R.string.already_exist_error));
                            alreadyExistPrompt.setPositiveButton("OK", null);
                            progressDialog.dismiss();
                            alreadyExistPrompt.show();
                        }
                        else{
                            verifyPhoneNumber();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    public void verifyPhoneNumber(){

        PhoneAuthProvider phoneAuth = PhoneAuthProvider.getInstance();
        phoneAuth.verifyPhoneNumber(donor.getPhoneNumber(), 120, TimeUnit.SECONDS, DonorRegistrationActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        showToast(getString(R.string.registration_unsuccessful));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCodeSent(final String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        showOtpPrompt(verificationId);
                    }
                });
    }

    private void showOtpPrompt(final String verificationId){
        AlertDialog.Builder builder = new AlertDialog.Builder(DonorRegistrationActivity.this);
        final EditText otpText = new EditText(DonorRegistrationActivity.this);

        otpText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setTitle(getString(R.string.enter_otp_title));
        builder.setMessage(getString(R.string.enter_otp_message));
        builder.setView(otpText);
        builder.setCancelable(false);

        builder.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enteredCode = otpText.getText().toString().trim();
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
                    writeToDatabase();
                }
            }
        });

    }

    public void writeToDatabase(){

        String userID = mAuth.getCurrentUser().getUid();
        donor.setDonorId(userID);

        dbRef.child(AppConstants.donors()).child(donor.getDonorId()).setValue(donor).
                addOnCompleteListener(DonorRegistrationActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.cancel();
                        if (task.isSuccessful()) {
                            writeToSharedPreference(donor);
                            showToast(getString(R.string.registration_successful_greeting));
                            finish();
                            startActivity(new Intent(DonorRegistrationActivity.this, MainActivity.class));
                        } else {
                            //if it fails
                            progressDialog.dismiss();
                            showToast(getString(R.string.registration_unsuccessful));
                        }
                    }
                });
    }

    public void writeToSharedPreference(Donor donor){
        SharedPreferences mPrefs = getSharedPreferences(AppConstants.currentUser(), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String jsonDonor = gson.toJson(donor);
        prefsEditor.putString(AppConstants.currentUser(), jsonDonor);
        prefsEditor.commit();
    }

    public void showToast(String text) {
        Toast toast = new Toast(DonorRegistrationActivity.this);
        toast.makeText(DonorRegistrationActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}