package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.beans.Donee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeList extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager llm;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;

    List<Donee> donees;
    Donee donee;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        donees = new ArrayList<Donee>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retriving Donees");
        progressDialog.show();

        dbRef = FirebaseDatabase.getInstance().getReference("donee");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    donee = postSnapshot.getValue(Donee.class);
                    Log.d("TEST::", donee.getPatientName());
                    donees.add(donee);
                }
                DoneeAdapter doneeAdapter = new DoneeAdapter(donees);
                recyclerView.setAdapter(doneeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        progressDialog.cancel();

    }
}
