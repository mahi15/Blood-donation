package com.nullvoid.blooddonation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.Constants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 27/06/17.
 */

public class DonorProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_name) TextView profileName;
    @BindView(R.id.profile_dob) TextView profileDob;
    @BindView(R.id.profile_blood_group) TextView profileBloodGroup;
    @BindView(R.id.profile_gender) TextView profileGender;
    @BindView(R.id.profile_number) TextView profileNumber;
    @BindView(R.id.profile_email) TextView profileEmail;
    @BindView(R.id.profile_address) TextView profileAddress;
    @BindView(R.id.profile_location) TextView profileLocation;
    @BindView(R.id.profile_pincode) TextView profilePincode;
    @BindView(R.id.profile_donation_count) TextView profileDonationCount;
    @BindView(R.id.profile_available_switch) Switch availableSwitch;
    @BindView(R.id.available_loading_progress) ProgressBar availableProgress;
    @BindView(R.id.toolbar) Toolbar toolbar;

    DonorProfileActivity context = this;

    DatabaseReference db;

    Donor user;
    boolean adminMode;
    SharedPreferences sp;
    Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);
        ButterKnife.bind(this);
        db = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Donor otherUser = Parcels.unwrap(getIntent().getParcelableExtra(Constants.donor));

        adminMode = otherUser != null;

        if (adminMode) {
            user = otherUser;
            getSupportActionBar().setTitle(user.getName());
        } else {
            sp = getSharedPreferences(Constants.currentUser, MODE_PRIVATE);
            String userJson = sp.getString(Constants.currentUser, null);
            gson = new Gson();
            user = gson.fromJson(userJson, Donor.class);
        }
        setProfileContent(user);
    }



    public void toggleAvailable(final Donor user, final boolean available) {
        db.child(Constants.donors())
                .child(user.getDonorId())
                .child(Constants.isAvailable)
                .setValue(available)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        availableSwitch.setVisibility(View.VISIBLE);
                        availableProgress.setVisibility(View.GONE);

                        if (!adminMode) {
                            user.setAvailable(available);
                            SharedPreferences.Editor editor = sp.edit();
                            String userJson = gson.toJson(user);
                            editor.putString(Constants.currentUser, userJson);
                            editor.apply();
                        }

                        //show completion dialog
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

    private void setProfileContent(final Donor user) {
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
                toggleAvailable(user, isChecked);
                availableSwitch.setVisibility(View.GONE);
                availableProgress.setVisibility(View.VISIBLE);
            }
        });
    }
}
