package com.nullvoid.blooddonation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.others.CommonFunctions;
import com.nullvoid.blooddonation.others.Constants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nullvoid.blooddonation.others.CommonFunctions.toCamelCase;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeRequestActivity extends AppCompatActivity {
    Activity context = this;
    private MaterialDialog progressDialog;

    //String for all the fields
    private String dName, dNumber, dRequiredDate, dBloodGroup, dReqAmount, dRequestedDate, dRequestedTime,
            dAttendantName, dAttendantNumber, dPatientsName, dPatientRefNumber,
            dHospitalName, dHospitalNumber, dHospitalAddress, dHospitalPincode, doneeId, dPAreaOfResidence;

    //Current date
    Calendar calendar = Calendar.getInstance();
    int cYear = calendar.get(Calendar.YEAR);

    private Donee donee;

    private DatabaseReference dbRef;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.reqBloodGroup) EditText bloodGroup;
    @BindView(R.id.reqName) EditText name;
    @BindView(R.id.reqPhoneNumber) EditText phnumber;
    @BindView(R.id.reqPatAttendantNumber) EditText reqAttendantNumber;
    @BindView(R.id.reqPName) EditText pName;
    @BindView(R.id.requiredAmount) EditText reqAmount;
    @BindView(R.id.reqPId) EditText pId;
    @BindView(R.id.reqHospitalName) EditText hospitalName;
    @BindView(R.id.reqHospitalPhone) EditText hospitalNumber;
    @BindView(R.id.reqHospitalAddress) EditText hospitalAddress;
    @BindView(R.id.reqHospitalPin) EditText hospitalPin;
    @BindView(R.id.reqPatAttendantName) EditText reqAttendantName;
    @BindView(R.id.reqPAreaOfResidence) EditText reqAreaOfResidence;
    @BindView(R.id.reqNeededDate) EditText reqDate;
    @BindView(R.id.reqSubmit) Button submitButton;
    @BindView(R.id.donee_req_parent_view) LinearLayout parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donee_request);
        ButterKnife.bind(context);
        setSupportActionBar(toolbar);

        dbRef = FirebaseDatabase.getInstance().getReference();

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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoFromView();
            }
        });

        reqDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    public void getInfoFromView() {
        progressDialog = new MaterialDialog.Builder(DoneeRequestActivity.this)
                .title(R.string.loading)
                .content(R.string.registering_request_message)
                .progress(true, 0).cancelable(false)
                .show();

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
        donee.setStatus(Constants.statusNotComplete());

        registerDonee(donee);
    }

    public void registerDonee(Donee donee){

        //create unique key for donee
        doneeId = dbRef.push().getKey();
        donee.setDoneeId(doneeId);

        dbRef.child(Constants.donees).child(doneeId).setValue(donee).
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

    public void showDatePicker() {

        //date picker
        String[] months = getResources().getStringArray(R.array.months);
        final String[] days31 = new String[31];

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
                android.R.layout.simple_spinner_dropdown_item, Arrays.asList(String.valueOf(cYear)));

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

                        reqDate.setText(dobDate);
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
            reqAmount.setText(Constants.notProvided);
            dReqAmount = Constants.notProvided;
        }
        if(TextUtils.isEmpty(dRequiredDate)) {
            reqDate.setError(required);
            reqDate.requestFocus();
            return false;
        }
        String[] rd = dRequiredDate.split("/");
        int d = Integer.parseInt(rd[0]);
        int m = Integer.parseInt(rd[1]);
        int y = Integer.parseInt(rd[2]);
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
        if (dPAreaOfResidence.length() < 4) {
            reqAreaOfResidence.setError(notValid);
            reqAreaOfResidence.requestFocus();
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
            dHospitalNumber = Constants.notProvided;
        } else if (dHospitalNumber.length() < 6) {
            hospitalNumber.setError(notValid);
            hospitalNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dHospitalAddress)) {
            dHospitalAddress = Constants.notProvided;
        } else if (dHospitalAddress.length() < 5) {
            hospitalAddress.setError(notValid);
            hospitalAddress.requestFocus();
            return false;
        }
        if (dHospitalPincode.length() != 6) {
            hospitalPin.setError(notValid);
            hospitalPin.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dPatientRefNumber)) {
            dPatientRefNumber = Constants.notProvided;
        }
        if (TextUtils.isEmpty(dAttendantName)) {
            dAttendantName = Constants.notProvided;
        }
        else if (dAttendantName.length() < 3){
            reqAttendantName.setError(leaveBlank);
        }
        if (TextUtils.isEmpty(dAttendantNumber)){
            reqAttendantNumber.setError(required);
            reqAttendantNumber.requestFocus();
        }
        else if (dAttendantNumber.length() != 10) {
            reqAttendantNumber.setError(leaveBlank);
            reqAttendantNumber.requestFocus();
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

}
