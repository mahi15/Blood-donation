package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

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
    Toolbar toolbar;
    LinearLayout parentView;

    BroadcastReceiver selectionChangeReciever;
    Intent intent;
    Donee clickedDonee;

    ArrayList<SelectionDonor> donorsList;
    ArrayList<Donor> selectedDonorsList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get ready the necessary view components
        setContentView(R.layout.layout_list_view);
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        parentView = (LinearLayout) findViewById(R.id.list_view_parent);

        db = FirebaseDatabase.getInstance().getReference();

        //get the started intent
        intent = getIntent();
        clickedDonee = Parcels.unwrap(intent.getParcelableExtra(AppConstants.donee()));

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        donorsList = new ArrayList<SelectionDonor>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMatch();
            }
        });

        loadData();

    }

    public void registerMatch(){
        if (selectedDonorsList.isEmpty()){
            showSnackBar("Select at least one Donor");
            return;
        }

        final ProgressDialog matchLoader= new ProgressDialog(DonorSelectionActivity.this);
        matchLoader.setTitle("Loading");
        matchLoader.setMessage("Adding match to database");
        matchLoader.setCancelable(false);
        matchLoader.show();

        final Match match = new Match();

        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat thisTime = new SimpleDateFormat("HH:mm");
        String date = thisDate.format(new Timestamp(System.currentTimeMillis()));
        String time = thisTime.format(new Timestamp(System.currentTimeMillis()));
        String matchId = db.push().getKey();
        ArrayList<String> selectedDonorsIdList = new ArrayList<String>();
        for(Donor donor : selectedDonorsList){
            selectedDonorsIdList.add(donor.getDonorId());
        }

        match.setDoneeId(clickedDonee.getDoneeId());
        match.setCompleted(false);
        match.setContactedDonors(selectedDonorsIdList);
        match.setMatchedDate(date);
        match.setMatchedTime(time);
        match.setHelpedDonors(new ArrayList<String>());
        match.setMatchId(matchId);

        db.child(AppConstants.matches()).child(matchId).setValue(match).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                matchLoader.dismiss();
                if (task.isSuccessful()){
                    AlertDialog.Builder addedDialog = new AlertDialog.Builder(DonorSelectionActivity.this);
                    addedDialog.setTitle("Success");
                    addedDialog.setMessage("The match has been added to database");
                    addedDialog.setCancelable(false);
                    addedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateDoneeStatus();
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

    public void updateDoneeStatus(){
        db.child(AppConstants.donees()).child(clickedDonee.getDoneeId()).child(AppConstants.status())
                .setValue(AppConstants.statusPending());
    }

    public void loadData(){

        db.child(AppConstants.donors()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Donor donor = postSnapshot.getValue(Donor.class);
                    SelectionDonor selectionDonor = new SelectionDonor(donor);
                    donorsList.add(selectionDonor);
                }
                DonorSelectionAdapter donorSelectionAdapter = new DonorSelectionAdapter(donorsList,
                        getApplicationContext());
                recyclerView.setAdapter(donorSelectionAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }

    public void showSnackBar(String text){
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
                if(action.equals(AppConstants.select())){
                    selectedDonorsList.add(selectionDonor.getDonor());
                }else if(action.equals(AppConstants.remove())){
                    selectedDonorsList.remove(selectionDonor.getDonor());
                }
                //set the no of donors selected in title
                if (selectedDonorsList.isEmpty()){
                    toolbar.setTitle("Select a Donor");
                } else {
                    toolbar.setTitle(selectedDonorsList.size()+" Donors Selected");
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
