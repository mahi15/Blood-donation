package com.nullvoid.blooddonation.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.beans.Match;
import com.nullvoid.blooddonation.others.CommonFunctions;
import com.nullvoid.blooddonation.others.Constants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 29/07/17.
 */

public class DonneDetailActivity extends AppCompatActivity {
    AppCompatActivity context = this;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.dd_requested_date) TextView requestedDate;
    @BindView(R.id.dd_requested_time) TextView requestedTime;
    @BindView(R.id.dd_status) TextView status;
    @BindView(R.id.dd_requester_number) TextView requesterNumber;
    @BindView(R.id.dd_requester_name) TextView requesterName;
    @BindView(R.id.dd_patient_name) TextView patientName;
    @BindView(R.id.dd_req_blood) TextView patientBloodGroup;
    @BindView(R.id.dd_required_date) TextView requiredDate;
    @BindView(R.id.dd_required_amount) TextView requiredAmount;
    @BindView(R.id.dd_attender_name) TextView attenderName;
    @BindView(R.id.dd_attender_number) TextView attenderNumber;
    @BindView(R.id.dd_patient_residence) TextView patientResidence;
    @BindView(R.id.dd_patient_id) TextView patientId;
    @BindView(R.id.dd_hospital_name) TextView hospitalName;
    @BindView(R.id.dd_hospital_number) TextView hospitalNumber;
    @BindView(R.id.dd_hospital_address) TextView hospitalAddress;
    @BindView(R.id.dd_hospital_pincode) TextView hospitalPincode;
    @BindView(R.id.dd_view_match) TextView viewMatch;
    @BindView(R.id.dd_call_hospital) ImageView callHospitalImage;
    @BindView(R.id.dd_call_requester) ImageView callRequesterImage;
    @BindView(R.id.dd_call_attender) ImageView callAttenderImage;

    Donee donee;
    DatabaseReference db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donne_detail);
        ButterKnife.bind(context);
        setSupportActionBar(toolbar);
        donee = Parcels.unwrap(getIntent().getParcelableExtra(Constants.donee()));
        db = FirebaseDatabase.getInstance().getReference();

        loadView();
    }

    public void loadView() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        requestedDate.setText(donee.getRequestedDate());
        requestedTime.setText(donee.getRequestedTime());
        status.setText(donee.getStatus());
        requesterName.setText(donee.getRequesterName());
        requesterNumber.setText(donee.getRequesterPhoneNumber());
        patientName.setText(donee.getPatientName());
        patientBloodGroup.setText(donee.getRequiredBloodGroup());
        requiredDate.setText(donee.getRequiredDate());
        requiredAmount.setText(donee.getRequiredAmount());
        attenderName.setText(donee.getPatientAttendantName());
        attenderNumber.setText(donee.getPatientAttendantNumber());
        patientResidence.setText(donee.getPatientAreaofResidence());
        patientId.setText(donee.getPatientID());
        hospitalName.setText(donee.getHospitalName());
        hospitalNumber.setText(donee.getHospitalNumber());
        hospitalAddress.setText(donee.getHospitalAddress());
        hospitalPincode.setText(donee.getHospitalPin());

        if (donee.getPatientAttendantNumber().equals(Constants.notProvided)) {
            callAttenderImage.setVisibility(View.GONE);
        } else {
            callAttenderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunctions.call(context, donee.getPatientAttendantNumber());
                }
            });
        }
        callRequesterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunctions.call(context, donee.getRequesterPhoneNumber());
            }
        });
        callHospitalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunctions.call(context, donee.getHospitalNumber());
            }
        });

        if (donee.getStatus().equals(Constants.statusNotComplete())) {
            viewMatch.setVisibility(View.GONE);
        } else {

            viewMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CommonFunctions.isNetworkAvailable(context)) {
                        CommonFunctions.showToast(context, getString(R.string.no_internet_message));
                    }
                    db.child(Constants.matches).child(donee.getDoneeId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Match match = dataSnapshot.getValue(Match.class);
                                    Intent intent = new Intent(context, MatchDetailActivity.class);
                                    intent.putExtra(Constants.matches, Parcels.wrap(match));
                                    startActivity(intent);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    CommonFunctions.showToast(context, getString(R.string.no_internet_message));
                                }
                            });
                }
            });
        }
    }
}
