package com.nullvoid.blooddonation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.nullvoid.blooddonation.beans.DonateToday;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.CommonFunctions;
import com.nullvoid.blooddonation.others.Constants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 30/07/17.
 */

public class DonateTodayActivity extends AppCompatActivity {
    Activity context = this;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.donate_today_info_card) CardView infoCard;
    @BindView(R.id.donate_today_form_card) CardView formCard;
    @BindView(R.id.donate_today_status_card) CardView statusCard;
    @BindView(R.id.donate_today_confirm_button) Button confirmButton;
    @BindView(R.id.donate_today_reason) EditText reasonText;
    @BindView(R.id.donate_today_location) EditText locationText;
    @BindView(R.id.donate_today_status_title) TextView statusTitle;
    @BindView(R.id.donate_today_status_message) TextView statusMessage;

    String location, reason;
    DatabaseReference db;

    Donor currentUser;
    MaterialDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_today);
        ButterKnife.bind(context);
        setSupportActionBar(toolbar);

        db = FirebaseDatabase.getInstance().getReference();

        String currentUserAsString = getSharedPreferences(Constants.currentUser, MODE_PRIVATE)
                .getString(Constants.currentUser, null);
        currentUser = new Gson().fromJson(currentUserAsString, Donor.class);

        loadView();
    }

    public void loadView() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!CommonFunctions.isNetworkAvailable(context)) {
            statusMessage.setText(R.string.no_internet_message);
            statusTitle.setText(R.string.error);
            return;
        }

        loadingDialog = new MaterialDialog.Builder(context)
                .title(R.string.loading)
                .content(R.string.please_wait_message)
                .progress(true, 0)
                .cancelable(false)
                .build();

        db.child(Constants.donatetoday).child(currentUser.getDonorId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            statusTitle.setText(R.string.donate_today_registered_status_title);
                            statusMessage.setText(R.string.donate_today_registered_status_message);
                        } else {
                            formCard.setVisibility(View.VISIBLE);
                            statusCard.setVisibility(View.GONE);
                            confirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveDetails();
                                }
                            });
                        }
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
    }

    public void saveDetails() {
        loadingDialog.show();

        location = locationText.getText().toString();
        reason = reasonText.getText().toString();

        if (!validateForm()) {return;}

        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        String todate = thisDate.format(new Timestamp(System.currentTimeMillis()));

        String currentUserAsString = getSharedPreferences(Constants.currentUser, MODE_PRIVATE)
                .getString(Constants.currentUser, null);
        Gson gson = new Gson();
        final Donor currentUser = gson.fromJson(currentUserAsString, Donor.class);
        String donorId = currentUser.getDonorId();

        DonateToday donateToday = new DonateToday(location, reason, donorId, todate);

        db.child(Constants.donatetoday).child(donorId).setValue(donateToday)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {

                    db.child(Constants.donors)
                            .child(currentUser.getDonorId())
                            .child(Constants.willingToDonateToday)
                            .setValue(true);

                    new MaterialDialog.Builder(context)
                            .title(R.string.success)
                            .content(R.string.donate_today_registered_message)
                            .contentColor(Color.BLACK)
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {
                                    finish();
                                }
                            }).show();
                }
            }
        });

    }

    public boolean validateForm() {
        String notValid = getString(R.string.not_valid_error),
                required = getString(R.string.required_error);
        if (TextUtils.isEmpty(location)) {
            locationText.setError(required);
            return false;
        }
        if (TextUtils.isEmpty(reason)) {
            reasonText.setError(required);
            return false;
        }
        if (location.length() < 4) {
            locationText.setError(notValid);
            return false;
        }
        if (reason.length() < 4) {
            reasonText.setError(notValid);
            return false;
        }
        return true;
    }
}
