package com.nullvoid.blooddonation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.nullvoid.blooddonation.admin.AdminConsoleActivity;
import com.nullvoid.blooddonation.beans.DonateToday;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.CommonFunctions;
import com.nullvoid.blooddonation.others.Constants;
import com.nullvoid.blooddonation.others.SMS;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    MainActivity context = this;

    MaterialDialog loadingDialog;

    FirebaseAuth mAuth;
    DatabaseReference db;

    SharedPreferences mPrefs;
    Gson gson;

    Donor currentUser;
    Donor tempDonor;

    @BindView(R.id.parent_view) LinearLayout parentView;
    @BindView(R.id.admin) Button adminButton;
    @BindView(R.id.btn_donate_today) Button donateTodayButton;
    @BindView(R.id.btn_donate_blood) Button donateBloodButton;
    @BindView(R.id.btn_req_blood) Button requestBloodButton;
    @BindView(R.id.btn_profile) Button profileButton;
    @BindView(R.id.btn_share) Button shareButton;
    @BindView(R.id.share_image) ImageView shareImage;
    @BindView(R.id.info_image) ImageView infoImage;
    @BindView(R.id.logo_image) ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //firebase stuff
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        currentUser = CommonFunctions.getCurrentUser(context);
        resetDonateToday();

        loadView();
    }

    public void loadView() {

        loadingDialog = new MaterialDialog.Builder(context)
                .title(R.string.loading)
                .content(R.string.please_wait_message)
                .progress(true, 0)
                .cancelable(false)
                .build();

        requestBloodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DoneeRequestActivity.class));
            }
        });

        donateBloodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DonorRegistrationActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null){
                    new MaterialDialog.Builder(context)
                            .title(R.string.not_registered)
                            .content(R.string.register_or_login)
                            .contentColor(Color.BLACK)
                            .autoDismiss(true)
                            .positiveText(R.string.register)
                            .negativeText(R.string.login)
                            .neutralText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog,
                                                    DialogAction which) {
                                    startActivity(new Intent(context, DonorRegistrationActivity.class));
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog,
                                                    DialogAction which) {
                                    checkIfDonorExists();
                                }
                            })
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog,
                                                    DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    startActivity(new Intent(context, DonorProfileActivity.class));
                }
            }
        });

        donateTodayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null){
                    new MaterialDialog.Builder(context)
                            .title(R.string.not_registered)
                            .content(R.string.register_or_login)
                            .contentColor(Color.BLACK)
                            .autoDismiss(true)
                            .positiveText(R.string.register)
                            .negativeText(R.string.login)
                            .neutralText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog,
                                                    DialogAction which) {
                                    startActivity(new Intent(context, DonorRegistrationActivity.class));
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog,
                                                    DialogAction which) {
                                    checkIfDonorExists();
                                }
                            })
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog,
                                                    DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    startActivity(new Intent(context, DonateTodayActivity.class));
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIt();
            }
        });

        if (currentUser != null && currentUser.isAdmin()) {
            adminButton.setVisibility(View.VISIBLE);
            adminButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, AdminConsoleActivity.class));
                }
            });
        }

        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIt();
            }
        });
    }

    public void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Constants.shareAppBody);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constants.shareAppExtra);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void checkIfDonorExists() {

        if (!CommonFunctions.isNetworkAvailable(context)) {
            CommonFunctions.showSnackBar(parentView, getString(R.string.no_internet_message));
            return;
        }

        new MaterialDialog.Builder(this)
                .title(R.string.enter_number_title)
                .content(R.string.enter_number_message)
                .contentColor(Color.BLACK)
                .cancelable(false)
                .negativeText(R.string.cancel)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .inputRange(10,10, Color.RED)
                .input(getString(R.string.enter_here), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        loadingDialog.show();

                        String number = input.toString().trim();
                        db.child(Constants.donors).orderByChild(Constants.phoneNumber)
                                .equalTo(number)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            tempDonor = ds.getValue(Donor.class);
                                        }
                                        loadingDialog.dismiss();
                                        if (tempDonor == null) {
                                            showFailedDialog(getString(R.string.no_donor_message));
                                            return;
                                        }
                                        verifyPhoneNumber(tempDonor);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        loadingDialog.dismiss();
                                        showFailedDialog(getString(R.string.on_cancelled_message));
                                    }
                                });
                    }
                }).show();
    }

    public void showFailedDialog(String message) {

        new MaterialDialog.Builder(MainActivity.this)
                .positiveText(R.string.ok)
                .title(R.string.failed)
                .content(message)
                .show();
    }

    public void verifyPhoneNumber(final Donor donor){

        if(!CommonFunctions.isNetworkAvailable(context)) {
            CommonFunctions.showToast(context, getString(R.string.no_internet_message));
            return;
        }

        final String otp = CommonFunctions.generateOTP();

        new SMS.sendOtp(context) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
                if (s.equals("f")) {
                    showFailedDialog(getString(R.string.on_cancelled_message));
                    return;
                }

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
                                    loadingDialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            CommonFunctions.signInUser(context, donor);
                                            CommonFunctions.showToast(context, getString(R.string.login_success_message));
                                            Intent i = new Intent(context, MainActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                            loadingDialog.dismiss();
                                        }
                                    }, 2000);

                                }
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog,
                                                DialogAction which) {
                                cancel(true);
                            }
                        })
                        .show();
            }
        }.execute(donor.getPhoneNumber(), otp);
    }

    //to be made as a server side function
    public void resetDonateToday() {

        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        final String todate = thisDate.format(new Timestamp(System.currentTimeMillis()));

        final ArrayList<DonateToday> donateTodays = new ArrayList<>();
        db.child(Constants.donatetoday)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DonateToday dt = ds.getValue(DonateToday.class);
                    donateTodays.add(dt);
                    if (!dt.getDate().equals(todate)) {
                        db.child(Constants.donors)
                                .child(dt.getDonorId())
                                .child(Constants.willingToDonateToday)
                                .setValue(false);

                        db.child(Constants.donatetoday).child(dt.getDonorId()).removeValue();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}