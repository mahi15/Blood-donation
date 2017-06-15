package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.adapters.DonorAdapter;
import com.nullvoid.blooddonation.beans.Donor;

import java.util.ArrayList;
import java.util.List;

//import android.support.v7.app.ActionBar;
//our current action bar

/**
 * Created by sanath on 13/06/17.
 */

public class DonorListFragment extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager llm;

    ProgressDialog progressDialog;

    boolean selectionMode = false;

    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference db;

    List<Donor> donors;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        setMode();

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        donors = new ArrayList<Donor>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retriving Donors..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db = FirebaseDatabase.getInstance().getReference("donors");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Donor donor = postSnapshot.getValue(Donor.class);
                    donors.add(donor);
                }
                DonorAdapter donorAdapter = new DonorAdapter(donors, DonorListFragment.this, selectionMode);
                recyclerView.setAdapter(donorAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setMode(){

        if(getIntent().getExtras() == null){
            selectionMode = false;
        }else if(getIntent().getExtras().getString("from").equals("doneepage")){
            selectionMode = true;
        }
    }
}

