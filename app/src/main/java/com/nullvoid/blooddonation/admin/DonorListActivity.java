package com.nullvoid.blooddonation.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.adapters.DonorAdapter;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 13/06/17.
 */

public class DonorListActivity extends AppCompatActivity {

    Context context = this;
    ProgressDialog progressDialog;

    public @BindView(R.id.cardList) RecyclerView recyclerView;
    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.searchbar) Toolbar searchbar;
    @BindView(R.id.list_view_message_box) CardView listMessageBox;
    @BindView(R.id.list_view_message) TextView listMessageText;
    @BindView(R.id.list_view_title) TextView listTitleText;

    DatabaseReference db;

    ArrayList<Donor> donors = new ArrayList<>();
    DonorAdapter donorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ButterKnife.bind(this);
        loadToolbars();
        db = FirebaseDatabase.getInstance().getReference();

        listMessageBox.setVisibility(View.VISIBLE);
        listTitleText.setText(R.string.loading);

        loadData();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
    }

    public void loadData() {
        db.child(Constants.donors).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Donor donor = postSnapshot.getValue(Donor.class);
                    donors.add(donor);
                }
                donorAdapter = new DonorAdapter(donors, context, context.getClass().equals(DonorSelectionListActivity.class));
                recyclerView.setAdapter(donorAdapter);
                listMessageBox.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    public void loadToolbars(){

        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                ArrayList<Donor> resultDonors = new ArrayList<>();
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

