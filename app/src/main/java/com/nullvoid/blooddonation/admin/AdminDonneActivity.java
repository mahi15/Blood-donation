package com.nullvoid.blooddonation.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.adapters.DonnePagerAdapter;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.beans.Match;
import com.nullvoid.blooddonation.others.CommonFunctions;
import com.nullvoid.blooddonation.others.Constants;

import org.parceler.Parcels;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sanath on 13/06/17.
 */

public class AdminDonneActivity extends AppCompatActivity {

    DatabaseReference db;
    Context context;
    private ViewPager viewPager;
    private DonnePagerAdapter mAdapter;
    MaterialDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_donne);

        context = AdminDonneActivity.this;

        //adding the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new DonnePagerAdapter(getSupportFragmentManager(), AdminDonneActivity.this);
        db = FirebaseDatabase.getInstance().getReference();

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mAdapter.getTabView(i));
        }
        viewPager.setAdapter(mAdapter);

        //other view stuff
        loadingDialog  = new MaterialDialog.Builder(this)
                .title(R.string.loading)
                .content(R.string.please_wait_message)
                .progress(true, 0)
                .build();
    }

    public void getSelectedDonors(final Donee donee, final String action) {


                loadingDialog.show();

        db.child(Constants.matches)
                .child(donee.getDoneeId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        loadingDialog.dismiss();

                        Match donationMatch = dataSnapshot.getValue(Match.class);

                        if (action.equals(Constants.donneActionSelectedDonorsButton())){
                            showContactedDonorsDialog(donationMatch);
                        }
                        if (action.equals(Constants.donneActionMarkCompletedButton())){
                            showMarkCompletedDialog(donationMatch);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void showMarkCompletedDialog(final Match match) {

        final ArrayList<Donor> selectedDonorsList = new ArrayList<>();

        final ArrayList<String> contactedDonors = new ArrayList<>();
        for (Donor donor : match.getContactedDonors()) {
            String item = donor.getName() + "\n" + donor.getLocation() + " - " + donor.getPincode();
            contactedDonors.add(item);
        }

        final MaterialDialog contactedDonorsDialog =
                new MaterialDialog.Builder(context)
                        .title(R.string.mark_complete_title)
                        .contentColor(Color.BLACK)
                        .items(contactedDonors)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .autoDismiss(true)
                        .alwaysCallMultiChoiceCallback()
                        .itemsCallbackMultiChoice(null,
                                new MaterialDialog.ListCallbackMultiChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog,
                                                               Integer[] which,
                                                               CharSequence[] text) {
                                        for (int i : which) {
                                            selectedDonorsList.add(match.getContactedDonors().get(i));
                                        }
                                        if (which.length == 0) {
                                            dialog.getActionButton(DialogAction.POSITIVE)
                                                    .setEnabled(false);
                                        } else {
                                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                        }
                                        return true;
                                    }
                                })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                markAsComplete(match, selectedDonorsList);
                            }
                        })
                        .build();
        contactedDonorsDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        contactedDonorsDialog.show();
    }

    public void showContactedDonorsDialog(final Match match) {

        final ArrayList<String> contactedDonors = new ArrayList<>();
        final ArrayList<Donor> selectedDonorsList = new ArrayList<>();

        for (Donor donor : match.getContactedDonors()) {
            String item = donor.getName() + "\n" + donor.getLocation() + " - " + donor.getPincode();
            contactedDonors.add(item);
        }

        final MaterialDialog contactedDonorsDialog =
                new MaterialDialog.Builder(context)
                        .title(R.string.contacted_donors_title)
                        .contentColor(Color.BLACK)
                        .items(contactedDonors)
                        .positiveText(R.string.call)
                        .negativeText(R.string.message)
                        .neutralText(R.string.close)
                        .autoDismiss(true)
                        .alwaysCallMultiChoiceCallback()
                        .itemsCallbackMultiChoice(null,
                                new MaterialDialog.ListCallbackMultiChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog,
                                                               Integer[] which,
                                                               CharSequence[] text) {
                                        for (int i : which) {
                                            selectedDonorsList.add(match.getContactedDonors().get(i));
                                        }
                                        if (which.length == 0) {
                                            dialog.getActionButton(DialogAction.NEGATIVE)
                                                    .setEnabled(false);
                                            dialog.getActionButton(DialogAction.POSITIVE)
                                                    .setEnabled(false);
                                        } else {
                                            if (which.length > 0) {
                                                dialog.getActionButton(DialogAction.NEGATIVE).setEnabled(true);
                                            }
                                            if (which.length == 1) {
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                            } else {
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                            }
                                        }
                                        return true;
                                    }
                                })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                showCallConfirmDialog(selectedDonorsList.get(0).getPhoneNumber(),
                                        selectedDonorsList.get(0).getName());
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                sendMessage(selectedDonorsList);
                            }
                        })
                        .build();
        contactedDonorsDialog.getActionButton(DialogAction.NEGATIVE).setEnabled(false);
        contactedDonorsDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        contactedDonorsDialog.show();
    }

    public void showCallConfirmDialog(final String number, final String name) {

        new MaterialDialog.Builder(context)
                .title(R.string.confirm)
                .content(getString(R.string.call) + " " + name + "\n" + number)
                .autoDismiss(true)
                .positiveText(R.string.call)
                .negativeText(R.string.cancel)
                .contentColor(Color.BLACK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonFunctions.call(context, number);
                    }
                })
                .show();
    }

    public void sendMessage( ArrayList<Donor> selectedDonorsList) {
        //TODO
    }

    public void markAsComplete(final Match match, final ArrayList<Donor> helpedDonors){

        loadingDialog.show();

        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        String date = thisDate.format(new Timestamp(System.currentTimeMillis()));

        match.setHelpedDonors(helpedDonors);
        match.setCompletedDate(date);
        match.setCompleted(true);

        db.child(Constants.matches)
                .child(match.getMatchId())
                .setValue(match)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            new MaterialDialog.Builder(context)
                                    .title(R.string.success)
                                    .content(R.string.donation_complete_message)
                                    .contentColor(Color.BLACK)
                                    .positiveText(R.string.ok)
                                    .show();
                            loadingDialog.dismiss();
                        }
                        //update donne status in Database
                        db.child(Constants.donees())
                                .child(match.getMatchId())
                                .child(Constants.status)
                                .setValue(Constants.statusComplete());

                        //update donor's donation count in database
                        for (Donor donor : helpedDonors){
                            db.child(Constants.donors())
                                    .child(donor.getDonorId())
                                    .child(Constants.donationCount())
                                    .setValue(donor.getDonationCount()+1);
                        }

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter actionIntentFilter = new IntentFilter(Constants.donneAction());
        BroadcastReceiver actionInfoReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getStringExtra(Constants.action);
                Donee donee = Parcels.unwrap(intent.getParcelableExtra(Constants.donee()));

                getSelectedDonors(donee, action);
            }
        };

        LocalBroadcastManager
                .getInstance(context)
                .registerReceiver(actionInfoReciever, actionIntentFilter);
    }
}