package com.nullvoid.blooddonation.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.beans.Match;
import com.nullvoid.blooddonation.others.CommonFunctions;
import com.nullvoid.blooddonation.others.Constants;

import org.parceler.Parcels;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 19/06/17.
 */

public class DonorSelectionActivity extends AdminDonorActivity {

    DonorSelectionActivity context = this;

    BroadcastReceiver selectionChangeReciever;
    Intent intent;

    Donee clickedDonee;
    ArrayList<Donor> selectedDonorsList;

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.list_view_parent) LinearLayout parentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        //get the started intent
        intent = getIntent();
        clickedDonee = Parcels.unwrap(intent.getParcelableExtra(Constants.donee()));

        loadAdditionalView();
    }

    public void loadAdditionalView() {

        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDonorsList.isEmpty()) {
                    CommonFunctions.showSnackBar(parentView, "No donors selected!");
                    return;
                }
                String confirmMessage = String
                        .format(getResources()
                                        .getQuantityString(
                                                R.plurals.notify_donors_confirm_message,
                                                selectedDonorsList.size()),
                                selectedDonorsList.size());
                new MaterialDialog.Builder(context)
                        .title(R.string.confirm)
                        .content(confirmMessage)
                        .contentColor(Color.BLACK)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (clickedDonee.getStatus().equals(Constants.statusNotComplete())) {
                                    assignMatch();
                                } else if (clickedDonee.getStatus().equals(Constants.statusPending())) {
                                    updateMatch();
                                }
                            }
                        })
                        .show();
            }
        });

        super.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (fab.isShown()) fab.hide();
                } else {
                    if (!fab.isShown()) fab.show();
                }
            }
        });

    }

    public void updateMatch() {

        final MaterialDialog matchLoader = new MaterialDialog.Builder(context)
                .title(R.string.loading)
                .content(R.string.please_wait_message)
                .progress(true, 0)
                .cancelable(false)
                .show();

        for (int i=0; i<selectedDonorsList.size(); i++){
            selectedDonorsList.get(i).setSelected(false);
        }

        //the ony thing we are gonna update is the donors so get the assignedDonors list from db
        db.child(Constants.matches).child(clickedDonee.getDoneeId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Match match = dataSnapshot.getValue(Match.class);

                        ArrayList<Donor> alreadySelectedDonorsList = match.getContactedDonors();

                        //check if any newly selected donor has been already assigned and skip him
                        for (Donor donor : selectedDonorsList) {
                            if (!alreadySelectedDonorsList.contains(donor)) {
                                alreadySelectedDonorsList.add(donor);
                            }

                            //now update the donorslist in db
                            db.child(Constants.matches).child(clickedDonee.getDoneeId())
                                    .child(Constants.contactedDonors()).setValue(alreadySelectedDonorsList)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            matchLoader.dismiss();
                                            if (task.isSuccessful()) {

                                                new MaterialDialog.Builder(context)
                                                        .title(R.string.success)
                                                        .content(R.string.donor_notified_message)
                                                        .contentColor(Color.BLACK)
                                                        .autoDismiss(true)
                                                        .cancelable(false)
                                                        .positiveText(R.string.ok)
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(MaterialDialog dialog,
                                                                                DialogAction which) {
                                                                finish();
                                                            }
                                                        })
                                                        .show();
                                            } else {
                                                CommonFunctions.showSnackBar(parentView,
                                                        getString(R.string.on_cancelled_message));
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void assignMatch() {

        final MaterialDialog matchLoader = new MaterialDialog.Builder(context).title(R.string.loading)
                .content(R.string.please_wait_message).progress(true, 0)
                .cancelable(false).show();

        for (int i=0; i<selectedDonorsList.size(); i++){
            selectedDonorsList.get(i).setSelected(false);
        }

        final Match match = new Match();

        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat thisTime = new SimpleDateFormat("HH:mm");
        String date = thisDate.format(new Timestamp(System.currentTimeMillis()));
        String time = thisTime.format(new Timestamp(System.currentTimeMillis()));

        match.setMatchId(clickedDonee.getDoneeId());
        match.setDonee(clickedDonee);
        match.setCompleted(false);
        match.setContactedDonors(selectedDonorsList);
        match.setMatchedDate(date);
        match.setMatchedTime(time);
        db.child(Constants.matches)
                .child(clickedDonee.getDoneeId())
                .setValue(match).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                matchLoader.dismiss();
                if (task.isSuccessful()) {

                    //update the status of the donne
                    db.child(Constants.donees()).child(clickedDonee.getDoneeId())
                            .child(Constants.status).setValue(Constants.statusPending());

                    new MaterialDialog.Builder(context)
                            .cancelable(false).title(R.string.success)
                            .content(R.string.donor_notified_message).contentColor(Color.BLACK)
                            .positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            }).show();
                } else {
                    CommonFunctions.showSnackBar(parentView,
                            getString(R.string.on_cancelled_message));
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        selectedDonorsList = new ArrayList<Donor>();

        //get the selected or removes donors from the list
        selectionChangeReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Donor selectedDonor = Parcels.unwrap(intent.getParcelableExtra("data"));
                String action = intent.getStringExtra(Constants.action);
                if (action.equals(Constants.select)) {
                    selectedDonorsList.add(selectedDonor);
                } else if (action.equals(Constants.remove)) {
                    selectedDonorsList.remove(selectedDonor);
                }
                //set the no of donors selected in title
                if (selectedDonorsList.isEmpty()) {
                    toolbar.setTitle(R.string.select_a_donor_title);

                } else {
                    String title = getResources()
                            .getQuantityString(R.plurals.selected_donors_count_title, selectedDonorsList.size());
                    String formattedTitle = String.format(title, selectedDonorsList.size());
                    toolbar.setTitle(formattedTitle);
                }
            }
        };

        //register to reciever SELECTION_CHANGE broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(selectionChangeReciever,
                new IntentFilter(Constants.selectionChange));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(selectionChangeReciever);
        super.onDestroy();
    }
}
