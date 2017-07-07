package com.nullvoid.blooddonation.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.adapters.DonorAdapter;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.AppConstants;

import java.util.ArrayList;

/**
 * Created by sanath on 13/06/17.
 */

public class DonorListActivity extends AppCompatActivity {

    Context context = DonorListActivity.this;

    RecyclerView recyclerView;
    LinearLayoutManager llm;
    ProgressDialog progressDialog;
    Toolbar searchbar, toolbar;

    DatabaseReference db;

    ArrayList<Donor> donors;

    DonorAdapter donorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_view);
        loadToolbars();

        donors = new ArrayList<Donor>();

        db = FirebaseDatabase.getInstance().getReference(AppConstants.donors());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Donor donor = postSnapshot.getValue(Donor.class);
                    donors.add(donor);
                }
                donorAdapter = new DonorAdapter(donors, context);
                recyclerView.setAdapter(donorAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
    }

    public void loadToolbars(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
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
                donorAdapter.loadData(donors);

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
                ArrayList<Donor> resultDonors = new ArrayList<Donor>();
                for(Donor donor : donors){
                    if(donor.getName().toLowerCase().contains(searchQuery) ||
                            donor.getBloodGroup().toLowerCase().contains(searchQuery) ||
                            donor.getLocation().toLowerCase().contains(searchQuery) ||
                            donor.getPincode().contains(searchQuery) ||
                            donor.getAddress().toLowerCase().contains(searchQuery)){
                        resultDonors.add(donor);
                    }
                }
                donorAdapter.loadData(resultDonors);
            }
        });

    }
}

