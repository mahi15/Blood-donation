package com.nullvoid.blooddonation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.adapters.DonorSelectionAdapter;
import com.nullvoid.blooddonation.beans.Donor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanath on 19/06/17.
 */

public class DonorSelectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager llm;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference db;

    List<Donor> donors;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_layout);

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        donors = new ArrayList<Donor>();

        db = FirebaseDatabase.getInstance().getReference("donors");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Donor donor = postSnapshot.getValue(Donor.class);
                    donors.add(donor);
                }
                DonorSelectionAdapter donorSelectionAdapter = new DonorSelectionAdapter(donors, getApplicationContext());
                recyclerView.setAdapter(donorSelectionAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
