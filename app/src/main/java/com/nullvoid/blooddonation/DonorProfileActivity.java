package com.nullvoid.blooddonation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 27/06/17.
 */

public class DonorProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.profile_dob)
    TextView profileDob;

    @BindView(R.id.profile_blood_group)
    TextView profileBloodGroup;

    @BindView(R.id.profile_gender)
    TextView profileGender;

    @BindView(R.id.profile_number)
    TextView profileNumber;

    @BindView(R.id.profile_email)
    TextView profileEmail;

    @BindView(R.id.profile_address)
    TextView profileAddress;

    @BindView(R.id.profile_location)
    TextView profileLocation;

    @BindView(R.id.profile_pincode)
    TextView profilePincode;

    @BindView(R.id.profile_donation_count)
    TextView profileDonationCount;

    @BindView(R.id.profile_available_switch)
    Switch availableSwitch;

    DonorProfileActivity context = this;
    Donor user;
    DatabaseReference db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);
        ButterKnife.bind(this);

        SharedPreferences sp = getSharedPreferences(AppConstants.currentUser, MODE_PRIVATE);
        String userJson = sp.getString(AppConstants.currentUser, null);
        Gson gson = new Gson();
        user = gson.fromJson(userJson, Donor.class);

        db = FirebaseDatabase.getInstance().getReference();

        setProfileContent();
    }

    public void toggleAvailable(final boolean available) {
        db.child(AppConstants.donors())
                .child(user.getDonorId())
                .child(AppConstants.isAvailable)
                .setValue(available)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String content = getString(R.string.available_toggled_message);
                        String formattedContent = String.format(content, available ? "Available" : "Not Available");
                        new MaterialDialog.Builder(context)
                                .title(R.string.success)
                                .positiveText(R.string.ok)
                                .content(formattedContent)
                                .show();
                    }
                });
    }

    private void setProfileContent() {
        profileDonationCount.setText(String.valueOf(user.getDonationCount()));
        profileName.setText(user.getName());
        profileDob.setText(user.getDateOfBirth());
        profileBloodGroup.setText(user.getBloodGroup());
        profileGender.setText(user.getGender());
        profileEmail.setText(user.getEmail());
        profileNumber.setText(user.getPhoneNumber());
        profileAddress.setText(user.getAddress());
        profileLocation.setText(user.getLocation());
        profilePincode.setText(user.getPincode());

        if (user.isAvailable()) {
            availableSwitch.setChecked(true);
        }

        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleAvailable(isChecked);
            }
        });
    }
}
