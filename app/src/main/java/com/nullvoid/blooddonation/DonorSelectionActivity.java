package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.adapters.DonorSelectionAdapter;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.beans.Match;
import com.nullvoid.blooddonation.beans.SelectionDonor;
import com.nullvoid.blooddonation.others.AppConstants;

import org.parceler.Parcels;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sanath on 19/06/17.
 */

public class DonorSelectionActivity extends AppCompatActivity {

    //View stuff
    RecyclerView recyclerView;
    LinearLayoutManager llm;
    DatabaseReference db;
    FloatingActionButton fab;
    Toolbar searchbar, toolbar;
    LinearLayout parentView;

    BroadcastReceiver selectionChangeReciever;
    Intent intent;
    Donee clickedDonee;

    ArrayList<SelectionDonor> donorsList;
    ArrayList<Donor> selectedDonorsList;
    DonorSelectionAdapter donorSelectionAdapter;
    DonorSelectionActivity context = this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        parentView = (LinearLayout) findViewById(R.id.list_view_parent);

        db = FirebaseDatabase.getInstance().getReference();

        //get the started intent
        intent = getIntent();
        clickedDonee = Parcels.unwrap(intent.getParcelableExtra(AppConstants.donee()));

        donorsList = new ArrayList<SelectionDonor>();

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        llm = new LinearLayoutManager(this);

        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        loadData();
    }

    public void loadAdditionalView() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDonorsList.isEmpty()) {
                    showSnackBar("Select at least one Donor");
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
                                if (clickedDonee.getStatus().equals(AppConstants.statusNotComplete())) {
                                    assignMatch();
                                } else if (clickedDonee.getStatus().equals(AppConstants.statusPending())) {
                                    updateMatch();
                                }
                            }
                        })
                        .show();
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.select_a_donor_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchbar = (Toolbar) findViewById(R.id.searchbar);
        searchbar.setVisibility(View.VISIBLE);
        final EditText searchField = (EditText) findViewById(R.id.search_bar_edittext);
        ImageView clearText = (ImageView) findViewById(R.id.search_bar_close);

        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchField.setText("");
                searchField.onEditorAction(EditorInfo.IME_ACTION_DONE);
                donorSelectionAdapter.loadData(donorsList);

            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().toLowerCase();
                ArrayList<SelectionDonor> resultDonors = new ArrayList<>();
                for (SelectionDonor selectionDonor : donorsList) {
                    if (selectionDonor.getDonor().getName().toLowerCase().contains(searchQuery) ||
                            selectionDonor.getDonor().getBloodGroup().toLowerCase().contains(searchQuery) ||
                            selectionDonor.getDonor().getLocation().toLowerCase().contains(searchQuery) ||
                            selectionDonor.getDonor().getPincode().contains(searchQuery) ||
                            selectionDonor.getDonor().getAddress().toLowerCase().contains(searchQuery)) {
                        resultDonors.add(selectionDonor);
                    }
                }
                donorSelectionAdapter.loadData(resultDonors);
            }
        });

    }

    public void updateMatch() {

        final ProgressDialog matchLoader = new ProgressDialog(DonorSelectionActivity.this);
        matchLoader.setTitle("Loading");
        matchLoader.setMessage("Updating match");
        matchLoader.setCancelable(false);
        matchLoader.show();

        //to hold the currently selected donors
        final ArrayList<String> selectedDonorsIdList = new ArrayList<String>();
        for (Donor donor : selectedDonorsList) {
            selectedDonorsIdList.add(donor.getDonorId());
        }

        //the ony thing we are gonna update is the donors so get the assignedDonors list from db
        db.child(AppConstants.matches()).child(clickedDonee.getDoneeId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> alreadySelectedDonorsList = new ArrayList<String>();
                        Match match = dataSnapshot.getValue(Match.class);
                        alreadySelectedDonorsList = match.getContactedDonors();

                        //check if any newly selected donor has been already assigned and skip him
                        for (String donorId : selectedDonorsIdList) {
                            if (!alreadySelectedDonorsList.contains(donorId)) {
                                alreadySelectedDonorsList.add(donorId);
                            }

                            //now update the donorslist in db
                            db.child(AppConstants.matches()).child(clickedDonee.getDoneeId())
                                    .child(AppConstants.contactedDonors()).setValue(alreadySelectedDonorsList)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            matchLoader.dismiss();
                                            if (task.isSuccessful()) {
                                                AlertDialog.Builder addedDialog = new AlertDialog.Builder(DonorSelectionActivity.this);
                                                addedDialog.setTitle("Success");
                                                addedDialog.setMessage("The match has been added to database");
                                                addedDialog.setCancelable(false);
                                                addedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                                addedDialog.show();
                                            } else {
                                                showSnackBar("There was some error communicating with the server");

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

        final MaterialDialog matchLoader = new MaterialDialog.Builder(context)
                .title(R.string.loading)
                .content(R.string.please_wait_message)
                .progress(true, 0)
                .cancelable(false)
                .show();


        final Match match = new Match();

        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat thisTime = new SimpleDateFormat("HH:mm");
        String date = thisDate.format(new Timestamp(System.currentTimeMillis()));
        String time = thisTime.format(new Timestamp(System.currentTimeMillis()));
        ArrayList<String> selectedDonorsIdList = new ArrayList<String>();
        for (Donor donor : selectedDonorsList) {
            selectedDonorsIdList.add(donor.getDonorId());
        }

        match.setDoneeId(clickedDonee.getDoneeId());
        match.setCompleted(false);
        match.setContactedDonors(selectedDonorsIdList);
        match.setMatchedDate(date);
        match.setMatchedTime(time);
        match.setHelpedDonors(new ArrayList<String>());
        match.setMatchId(clickedDonee.getDoneeId());
        match.setDoneeName(clickedDonee.getPatientName());
        match.setDoneeBloodGroup(clickedDonee.getRequiredBloodGroup());

        db.child(AppConstants.matches()).child(clickedDonee.getDoneeId()).setValue(match).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                matchLoader.dismiss();
                if (task.isSuccessful()) {

                    new MaterialDialog.Builder(context)
                            .cancelable(false)
                            .title(R.string.success)
                            .content(R.string.donor_notified_message)
                            .contentColor(Color.BLACK)
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    updateDoneeStatus();
                                    finish();
                                }
                            }).show();
                } else {
                    showSnackBar(getString(R.string.on_cancelled_message));

                }
            }
        });

    }

    public void updateDoneeStatus() {
        db.child(AppConstants.donees()).child(clickedDonee.getDoneeId()).child(AppConstants.status())
                .setValue(AppConstants.statusPending());
    }

    public void loadData() {

        db.child(AppConstants.donors()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SelectionDonor selectionDonor = new SelectionDonor(postSnapshot.getValue(Donor.class));
                    donorsList.add(selectionDonor);
                }
                donorSelectionAdapter = new DonorSelectionAdapter(donorsList,
                        DonorSelectionActivity.this);
                recyclerView.setAdapter(donorSelectionAdapter);
                loadAdditionalView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void showSnackBar(String text) {
        Snackbar.make(parentView, text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        selectedDonorsList = new ArrayList<Donor>();

        //get the selected or removes donors from the list
        selectionChangeReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SelectionDonor selectionDonor = Parcels.unwrap(intent.getParcelableExtra("data"));
                String action = intent.getStringExtra("action");
                if (action.equals(AppConstants.select())) {
                    selectedDonorsList.add(selectionDonor.getDonor());
                } else if (action.equals(AppConstants.remove())) {
                    selectedDonorsList.remove(selectionDonor.getDonor());
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
                new IntentFilter(getString(R.string.selection_change)));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(selectionChangeReciever);
        super.onDestroy();
    }
}
