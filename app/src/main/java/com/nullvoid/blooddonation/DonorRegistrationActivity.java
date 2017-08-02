package com.nullvoid.blooddonation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.nullvoid.blooddonation.others.CommonFunctions;
import com.nullvoid.blooddonation.others.Constants;
import com.nullvoid.blooddonation.others.SMS;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 10/06/17.
 */

/**
 * Created by MeowMeow on 4/30/2017.
 */

public class DonorRegistrationActivity extends AppCompatActivity {
    Activity context = this;

    String otp;
    int  cYear = Calendar.getInstance().get(Calendar.YEAR);
    MaterialDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.regDateOfBirth) EditText dateOfBirth;
    @BindView(R.id.regGender) EditText gender;
    @BindView(R.id.tnc_text) TextView tncText;
    @BindView(R.id.donor_reg_parent_view) LinearLayout parentView;
    @BindView(R.id.regBloodGroup) EditText bloodGroup;
    @BindView(R.id.tnc_checkbox) CheckBox tncCheckBox;
    @BindView(R.id.regName) EditText name;
    @BindView(R.id.regEmail) EditText email;
    @BindView(R.id.regPhoneNumber) EditText phoneNumber;
    @BindView(R.id.regAddress) EditText address;
    @BindView(R.id.regLocation) EditText location;
    @BindView(R.id.regPinCode) EditText pincode;
    @BindView(R.id.regDonatedBeforeOption) RadioGroup DonatedBefore;
    @BindView(R.id.regSubmit) Button submitButton;

    Donor donor;
    private String dName, dGender, dBloodGroup, dAge, dDOB, dNumber, dEmail, dAddress, dLocation,
            dPincode, dRegisteredDate, dRegisteredTime;
    private boolean dDonationInLastSixMonths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);
        ButterKnife.bind(context);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        loadView();

    }

    public void loadView() {

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        tncText.setClickable(true);
        tncText.setMovementMethod(LinkMovementMethod.getInstance());
        String tnc = "I Accept the <a href='http://www.google.com'>Terms and Conditions</a> " +
                "Privacy Policy of RSS HSS Blood Donors Bureau";
        tncText.setText(Html.fromHtml(tnc));

        //blood groups drop down list
        bloodGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title(R.string.choose_your_group)
                        .items(R.array.blood_group)
                        .itemsColor(Color.BLACK)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(text != null)
                                    bloodGroup.setText(text.toString());
                                return true;
                            }
                        })
                        .positiveText(R.string.select)
                        .negativeText(R.string.cancel)
                        .show();
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title(R.string.choose_gender_title)
                        .items(R.array.gender_array)
                        .contentColor(Color.BLACK)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog,
                                                       View itemView, int which,
                                                       CharSequence text) {
                                if (text != null) gender.setText(text.toString());
                                return true;
                            }
                        })
                        .positiveText(R.string.select)
                        .negativeText(R.string.cancel)
                        .show();
            }
        });

        //donated before radio buttons
        DonatedBefore.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton donatedBefore = (RadioButton) findViewById(DonatedBefore.getCheckedRadioButtonId());
                if (donatedBefore.getText().toString().equals("Yes")) {
                    dDonationInLastSixMonths = true;
                } else {
                    dDonationInLastSixMonths = false;
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEnteredDetails();
            }
        });

        TextWatcher dateWatcher = new TextWatcher() {

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > cYear) ? cYear : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    dateOfBirth.setText(current);
                    dateOfBirth.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
//        dateOfBirth.addTextChangedListener(dateWatcher);
    }

    public void showDatePicker() {

        //date picker
        String[] months = getResources().getStringArray(R.array.months);
        final String[] years = new String[90];
        final String[] days31 = new String[31];
        for (int i = 0; i < 90; i++) {
            years[i] = String.valueOf(cYear - 100 + i+1); }
        for (int i = 0; i < 31; i++) {
            days31[i] = String.valueOf(i+1);
        }
        final String[] days30 = Arrays.copyOfRange(days31, 0, 29);
        final String[] days29 = Arrays.copyOfRange(days31, 0, 27);

                final ArrayAdapter dayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, days31);
        ArrayAdapter monthAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, months);
        ArrayAdapter yearAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, years);

        MaterialDialog datePicker = new MaterialDialog.Builder(context)
                .title(R.string.dob_title)
                .customView(R.layout.date_picker_layout, false)
                .positiveText(R.string.ok)
                .cancelable(false)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog,
                                        DialogAction which) {
                        View view = dialog.getView();
                        final Spinner daySpinner = (Spinner) view.findViewById(R.id.day_spinner);
                        Spinner monthSpinner = (Spinner) view.findViewById(R.id.month_spinner);
                        Spinner yearSpinner = (Spinner) view.findViewById(R.id.year_spinner);

                        String d = daySpinner.getSelectedItem().toString();
                        d = d.length() == 1 ? "0"+d : d;
                        String m = String.valueOf(monthSpinner.getSelectedItemPosition() + 1);
                        m = m.length() == 1 ? "0"+m : m;

                        String dobDate =  d + "/" + m + "/" + yearSpinner.getSelectedItem().toString();

                        dateOfBirth.setText(dobDate);
                    }
                }).show();
        View view = datePicker.getCustomView();
        Spinner daySpinner = (Spinner) view.findViewById(R.id.day_spinner);
        Spinner monthSpinner = (Spinner) view.findViewById(R.id.month_spinner);
        Spinner yearSpinner = (Spinner) view.findViewById(R.id.year_spinner);

        daySpinner.setAdapter(dayAdapter);
        monthSpinner.setAdapter(monthAdapter);
        yearSpinner.setAdapter(yearAdapter);
    }

    public void getEnteredDetails() {

        if(!CommonFunctions.isNetworkAvailable(context)){
            CommonFunctions.showSnackBar(parentView, getString(R.string.no_internet_message));
            return;
        }

        progressDialog = new MaterialDialog.Builder(DonorRegistrationActivity.this)
                .title(R.string.loading)
                .content(R.string.registering_request_message)
                .progress(true, 0).cancelable(false)
                .show();

        dName = name.getText().toString().trim();
        dGender = gender.getText().toString();
        dBloodGroup = bloodGroup.getText().toString();
        dDOB = dateOfBirth.getText().toString();
        try {
            int birthYear = Integer.parseInt(dDOB.split("/")[2]);
            dAge = String.valueOf(cYear - birthYear);
        } catch (Exception e){}
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
        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat thisTime = new SimpleDateFormat("HH:mm");
        dRegisteredDate = thisDate.format(new Timestamp(System.currentTimeMillis()));
        dRegisteredTime = thisTime.format(new Timestamp(System.currentTimeMillis()));

        donor.setName(CommonFunctions.toCamelCase(dName));
        donor.setGender(dGender);
        donor.setBloodGroup(dBloodGroup);
        donor.setDateOfBirth(dDOB);
        donor.setAge(dAge);
        donor.setDonationInLastSixMonths(dDonationInLastSixMonths);
        donor.setPhoneNumber(dNumber);
        donor.setEmail(dEmail);
        donor.setAddress(dAddress);
        donor.setLocation(CommonFunctions.toCamelCase(dLocation));
        donor.setPincode(dPincode);
        donor.setRegisteredDate(dRegisteredDate);
        donor.setRegisteredTime(dRegisteredTime);
        donor.setAdmin(false);
        donor.setDonationCount(0);

        checkIfDonorAlreadyExist();
    }

    public boolean validateForm() {
        //validate the data entered by user
            final String required = getString(R.string.required_error);
            final String notValid = getString(R.string.not_valid_error);

            final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        if (TextUtils.isEmpty(dName)) {
            name.setError(required);
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dGender)) {
            gender.callOnClick();
            return false;
        }
        if (TextUtils.isEmpty(dBloodGroup)) {
            bloodGroup.callOnClick();
            return false;
        }
        if(TextUtils.isEmpty(dDOB)) {
            dateOfBirth.setError(required);
            dateOfBirth.requestFocus();
            return false;
        }
        String[] dob = dDOB.split("/");
        int d = Integer.parseInt(dob[0]);
        int m = Integer.parseInt(dob[1]);
        int y = Integer.parseInt(dob[2]);
        if (m == 2){
            if ( (y % 4 != 0) && d >= 29 ) {
                CommonFunctions.showToast(context, "Not a valid date");
                return false;
            }
        } else {
            if (m % 2 == 0) {
                if (!(d <= 30)) {
                    CommonFunctions.showToast(context, "Not a valid date");
                    return false;
                }
            }
        }
        if (dNumber.length() != 10) {
            phoneNumber.setError(notValid);
            phoneNumber.requestFocus();
            return false;
        }
        Matcher emailMatcher = VALID_EMAIL_ADDRESS_REGEX .matcher(dEmail);
        if(!emailMatcher.find()){
            email.setError(notValid);
            email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dAddress)) {
            address.setError(required);
            address.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dLocation)) {
            location.setError(required);
            location.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dPincode)) {
            pincode.setError(required);
            pincode.requestFocus();
            return false;
        }
        if(!tncCheckBox.isChecked()){
            CommonFunctions.showToast(context,
                    getString(R.string.tnc_error));
            return false;
        }
        return true;
    }

    public void checkIfDonorAlreadyExist(){

        dbRef.child(Constants.donors).orderByChild(Constants.phoneNumber).equalTo(donor.getPhoneNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() == 1){
                            //if there is already a user with the phone nubmer then show an error alert
                            progressDialog.dismiss();

                            new MaterialDialog.Builder(DonorRegistrationActivity.this)
                                    .title(R.string.failed)
                                    .content(R.string.already_exist_error)
                                    .contentColor(Color.BLACK)
                                    .positiveText(R.string.ok)
                                    .show();
                        }
                        else{
                            verifyPhoneNumber();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        new MaterialDialog.Builder(DonorRegistrationActivity.this)
                                .title(R.string.failed)
                                .content(R.string.on_cancelled_message)
                                .positiveText(R.string.ok)
                                .show();
                    }
                });
    }

    public void verifyPhoneNumber(){

        otp = CommonFunctions.generateOTP();
//        new SMS.sendOtp(context).execute(donor.getPhoneNumber(), otp);

        new SMS.sendOtp(context) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                new MaterialDialog.Builder(context)
                        .cancelable(false)
                        .positiveText(R.string.submit)
                        .negativeText(R.string.cancel)
                        .title(R.string.enter_otp_title)
                        .content(R.string.enter_otp_message)
                        .input(R.string.enter_here, R.string.blank, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String enteredCode = input.toString().trim();
                                if (otp.equals(enteredCode)) {
                                    writeToDatabase();
                                }
                            }
                        })
                        .show();
            }
        }.execute(donor.getPhoneNumber(), otp);

    }

    public void writeToDatabase(){

        String userID = dbRef.push().getKey();
        donor.setDonorId(userID);

        dbRef.child(Constants.donors).child(donor.getDonorId()).setValue(donor).
                addOnCompleteListener(DonorRegistrationActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.cancel();
                        if (task.isSuccessful()) {
                            CommonFunctions.signInUser(context, donor);

                            new MaterialDialog.Builder(context)
                                    .title(R.string.success)
                                    .content(R.string.thank_for_reg_message)
                                    .contentColor(Color.BLACK)
                                    .cancelable(false)
                                    .positiveText(R.string.ok)
                                    .onAny(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog,
                                                            DialogAction which) {
                                            finish();
                                            startActivity(new Intent(context, MainActivity.class));
                                        }
                                    }).show();

                        } else {
                            //if it fails
                            progressDialog.dismiss();
                            CommonFunctions.showSnackBar(parentView, getString(R.string.registration_unsuccessful));
                        }
                    }
                });
    }
}