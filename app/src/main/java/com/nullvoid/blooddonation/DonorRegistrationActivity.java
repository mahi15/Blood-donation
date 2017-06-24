package com.nullvoid.blooddonation;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.beans.User;

import java.util.Calendar;

/**
 * Created by sanath on 10/06/17.
 */

/**
 * Created by MeowMeow on 4/30/2017.
 */

public class DonorRegistrationActivity extends AppCompatActivity {

    private String dName, dGender, dBloodGroup, dAge, dDOB, dDonationDate, dNumber, dEmail, dAddress, dLocation;
    private String dPincode, currentUserId;

    ArrayAdapter bloodGroupArray;
    Button submitButton;
    EditText name, email, age, phoneNumber, dateOfBirth, address;
    EditText location, pincode, DateOfDonation;
    Spinner bloodGroupSpinner;
    RadioGroup Gender, DonatedBefore;
    RadioButton gender, donatedBefore;
    ProgressDialog progressDialog;

    Calendar c = Calendar.getInstance();
    int cDay = c.get(Calendar.DAY_OF_MONTH);
    int cMonth = c.get(Calendar.MONTH);
    int cYear = c.get(Calendar.YEAR);
    int sDay, sMonth, sYear;

    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;

    DrawerLayout drawerLayout;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_donor_registration);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();

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

        dateOfBirth = (EditText)findViewById(R.id.regDateOfBirth);
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(DonorRegistrationActivity.this
                        ,datePickerListener, cYear, cMonth, cDay);
                datePicker.show();
            }
        });


        //Now add all the donorDetails input to a object
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                name = (EditText)findViewById(R.id.regName);
                Gender = (RadioGroup) findViewById(R.id.regGender);
                age = (EditText)findViewById(R.id.regAge);
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

                donor.setName(toCamelCase(dName));
                donor.setGender(dGender);
                donor.setBloodGroup(dBloodGroup);
                donor.setDateOfBirth(dDOB);
                donor.setAge(dAge);
                donor.setDonatedDate(dDonationDate);
                donor.setPhoneNumber(dNumber);
                donor.setEmail(dEmail);
                donor.setAddress(dAddress);
                donor.setLocation(toCamelCase(dLocation));
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

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        currentUserId = fbUser.getUid();
        String donorId = dbRef.push().getKey();

        donor.setDonorId(donorId);
        donor.setRegisteredBy(currentUserId);



        dbRef.child("donors").child(donorId).setValue(donor).
        addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.cancel();
                if (task.isSuccessful()) {
                    //If user is added to database
                    addRegistrationCount();
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

    public void addRegistrationCount(){

        dbRef.child("users").child(currentUserId).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                dbRef.child("users").child(currentUserId).child("registrationCount").setValue(user.getRegistrationCount()+1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
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
        if(dBloodGroup.equals("Choose blood group")){
            TextView errorText = (TextView)bloodGroupSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select the blood group!");

            return false;
        }
        return true;
    }

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

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_req_from:
                        startActivity(new Intent(getApplicationContext(), DoneeRequestActivity.class));
                        break;
                    case R.id.nav_about:
                        Toast.makeText(getApplicationContext(), "Have to implement", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_settings:
                        Toast.makeText(getApplicationContext(), "Have to implement", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        break;
                }
                return true;
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}