package com.nullvoid.blooddonation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
    private EditText name, phnumber, reqDate, reqAmount, reqAttendantName, reqAttendantNumber, pName, pId, hospitalName,
                    hospitalNumber, hospitalAddress, hospitalPin, reqAreaOfResidence, bloodGroup;
    private Toolbar toolbar;
    private Button submitButton;
    private MaterialDialog progressDialog;
    private LinearLayout parentView;

    //String for all the fields
    private String dName, dNumber, dRequiredDate, dBloodGroup, dReqAmount, dRequestedDate, dRequestedTime,
            dAttendantName, dAttendantNumber, dPatientsName, dPatientRefNumber,
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
        bloodGroup = (EditText)findViewById(R.id.reqBloodGroup);
        submitButton = (Button)findViewById(R.id.reqSubmit);
        reqDate = (EditText) findViewById(R.id.reqNeededDate);
        parentView = (LinearLayout) findViewById(R.id.donee_req_parent_view);

        //getting ready
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bloodGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(DoneeRequestActivity.this)
                        .title(R.string.choose_req_group)
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
        submitButton.setOnClickListener(getInfo);
        reqDate = (EditText) findViewById(R.id.reqNeededDate);
        reqDate.addTextChangedListener(dateWatcher);

    }

    View.OnClickListener getInfo = new View.OnClickListener() {
        public void onClick(View v) {
            progressDialog = new MaterialDialog.Builder(DoneeRequestActivity.this)
                    .title(R.string.loading)
                    .content(R.string.registering_request_message)
                    .progress(true, 0).cancelable(false)
                    .show();

            name = (EditText)findViewById(R.id.reqName);
            phnumber = (EditText)findViewById(R.id.reqPhoneNumber);
            reqAttendantNumber = (EditText)findViewById(R.id.reqPatAttendantNumber);
            pName  = (EditText)findViewById(R.id.reqPName);
            reqAmount = (EditText) findViewById(R.id.requiredAmount);
            pId  = (EditText)findViewById(R.id.reqPId);
            hospitalName = (EditText)findViewById(R.id.reqHospitalName);
            hospitalNumber = (EditText)findViewById(R.id.reqHospitalAddress);
            hospitalAddress = (EditText)findViewById(R.id.reqHospitalAddress);
            hospitalPin = (EditText)findViewById(R.id.reqHospitalPin);
            reqAttendantName = (EditText)findViewById(R.id.reqPatAttendantName);
            reqAreaOfResidence = (EditText)findViewById(R.id.reqPAreaOfResidence);
            donee = new Donee();

            dName = name.getText().toString().toString();
            dNumber = phnumber.getText().toString().trim();
            dBloodGroup = bloodGroup.getText().toString();
            dRequiredDate = reqDate.getText().toString().trim();
            dReqAmount = reqAmount.getText().toString().trim();
            dAttendantName = reqAttendantName.getText().toString().trim();
            dAttendantNumber = reqAttendantNumber.getText().toString().trim();
            dPatientsName = pName.getText().toString().trim();
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

            SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat thisTime = new SimpleDateFormat("HH:mm");
            dRequestedDate = thisDate.format(new Timestamp(System.currentTimeMillis()));
            dRequestedTime = thisTime.format(new Timestamp(System.currentTimeMillis()));

            donee.setRequesterName(toCamelCase(dName));
            donee.setRequesterPhoneNumber(dNumber);
            donee.setRequiredBloodGroup(dBloodGroup);
            donee.setRequiredAmount(dReqAmount);
            donee.setRequiredDate(dRequiredDate);
            donee.setPatientAttendantName(dAttendantName);
            donee.setPatientAttendantNumber(dAttendantNumber);
            donee.setPatientName(toCamelCase(dPatientsName));
            donee.setPatientID(dPatientRefNumber);
            donee.setHospitalName(toCamelCase(dHospitalName));
            donee.setHospitalNumber(dHospitalNumber);
            donee.setHospitalAddress(dHospitalAddress);
            donee.setHospitalPin(dHospitalPincode);
            donee.setPatientAreaofResidence(dPAreaOfResidence);
            donee.setRequestedDate(dRequestedDate);
            donee.setRequestedTime(dRequestedTime);
            donee.setStatus(AppConstants.statusNotComplete());

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
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    //If user is added to database
                    new MaterialDialog.Builder(DoneeRequestActivity.this)
                            .title(R.string.success)
                            .content(R.string.req_submitted_message)
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                    .show();

                } else {
                    //if it fails
                    new MaterialDialog.Builder(DoneeRequestActivity.this)
                            .title(R.string.failed)
                            .content(R.string.on_cancelled_message)
                            .positiveText(R.string.ok)
                            .show();
                }
            }
        });

    }

    public void showToast(String text){
        Toast.makeText(DoneeRequestActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public boolean validateForm() {

        final String required = getString(R.string.required_error);
        final String notValid = getString(R.string.not_valid_error);
        final String leaveBlank = getString(R.string.leave_blank);

        //validate the data entered by user
        if (dName.length() < 3) {
            name.setError(notValid);
            name.requestFocus();
            return false;
        }
        if (dNumber.length() != 10) {
            phnumber.setError(notValid);
            phnumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dBloodGroup)) {
            bloodGroup.callOnClick();
            return false;
        }
        if (dPatientsName.length() < 3) {
            pName.setError(notValid);
            pName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dReqAmount)){
            reqAmount.setText(AppConstants.notProvided());
            dReqAmount = AppConstants.notProvided();
        }
        if(TextUtils.isEmpty(dRequiredDate)) {
            reqDate.setError(required);
            reqDate.requestFocus();
            return false;
        }
//        String[] rDate = dRequiredDate.split("/");
//        if (Integer.parseInt(rDate[2]) < cYear || Integer.parseInt(rDate[2]) < (cYear-100)){
//            reqDate.setError(notValid);
//            reqDate.requestFocus();
//            return false;
//        }
        if (dPAreaOfResidence.length() < 4) {
            reqAreaOfResidence.setError(notValid);
            reqAreaOfResidence.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dPatientRefNumber)) {
            dPatientRefNumber = AppConstants.notProvided();
        }
        if (TextUtils.isEmpty(dAttendantName)) {
            dAttendantName = AppConstants.notProvided();
        }
        else if (dAttendantName.length() < 3){
            reqAttendantName.setError(leaveBlank);
        }
        if (TextUtils.isEmpty(dAttendantNumber)){
            dAttendantNumber = AppConstants.notProvided();
        }
        else if (dAttendantNumber.length() != 10) {
            reqAttendantNumber.setError(leaveBlank);
            reqAttendantNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dHospitalName)) {
            hospitalName.setError(required);
            hospitalName.requestFocus();
            return false;
        }
        else if (dHospitalName.length() < 3){
            hospitalName.setError(notValid);
            hospitalName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dHospitalNumber)) {
            hospitalNumber.setError(required);
            hospitalNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dHospitalAddress)) {
            hospitalAddress.setError(required);
            hospitalAddress.requestFocus();
            return false;
        }
        if (dHospitalPincode.length() != 6) {
            hospitalPin.setError(notValid);
            hospitalPin.requestFocus();
            return false;
        }
        return true;
    }

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
                reqDate.setText(current);
                reqDate.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void afterTextChanged(Editable s) {
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

    public void showSnackBar(String text){
        Snackbar.make(parentView, text, Snackbar.LENGTH_SHORT).show();
    }

}
