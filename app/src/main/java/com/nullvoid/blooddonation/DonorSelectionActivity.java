package com.nullvoid.blooddonation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.adapters.DonorSelectionAdapter;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.beans.SelectionDonor;
import com.nullvoid.blooddonation.others.AppConstants;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by sanath on 19/06/17.
 */

public class DonorSelectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager llm;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference db;
    FloatingActionButton fab;

    BroadcastReceiver selectionChangeBroadcastReciever;

    ArrayList<SelectionDonor> selectionDonors;
    ArrayList<SelectionDonor> selectedDonors;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        selectionDonors = new ArrayList<SelectionDonor>();
        loadData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        selectedDonors = new ArrayList<SelectionDonor>();

        //get the selected or removes donors from the list
        selectionChangeBroadcastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SelectionDonor selectionDonor = Parcels.unwrap(intent.getParcelableExtra("data"));
                String action = intent.getStringExtra("action");
                if(action.equals(getString(R.string.select))){
                    selectedDonors.add(selectionDonor);
                    Toast.makeText(getApplicationContext(), "S : "+selectionDonor.getDonor().getName(), Toast.LENGTH_SHORT).show();
                }else if(action.equals(getString(R.string.remove))){
                    Toast.makeText(getApplicationContext(), "R : "+selectionDonor.getDonor().getName(), Toast.LENGTH_SHORT).show();
                    selectedDonors.remove(selectionDonor);
                }
            }
        };

        //register to reciever SELECTION_CHANGE broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(selectionChangeBroadcastReciever,
                new IntentFilter(getString(R.string.selection_change)));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(selectionChangeBroadcastReciever);
        super.onDestroy();
    }

    public void loadData(){
        db = FirebaseDatabase.getInstance().getReference(AppConstants.donors());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Donor donor = postSnapshot.getValue(Donor.class);
                    SelectionDonor selectionDonor = new SelectionDonor(donor);
                    selectionDonors.add(selectionDonor);
                }
                DonorSelectionAdapter donorSelectionAdapter = new DonorSelectionAdapter(selectionDonors,
                        getApplicationContext());
                recyclerView.setAdapter(donorSelectionAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }
}
