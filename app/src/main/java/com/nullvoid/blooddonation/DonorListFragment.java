package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.adapters.DonorAdapter;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.AppConstants;

import java.util.ArrayList;

/**
 * Created by sanath on 13/06/17.
 */

public class DonorListFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager llm;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    LinearLayout searchLayout;

    DatabaseReference db;

    ArrayList<Donor> donors;

    DonorAdapter donorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        donors = new ArrayList<Donor>();

        db = FirebaseDatabase.getInstance().getReference(AppConstants.donors());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Donor donor = postSnapshot.getValue(Donor.class);
                    donors.add(donor);
                }
                donorAdapter = new DonorAdapter(donors, getActivity());
                recyclerView.setAdapter(donorAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_list_view, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        loadAdditionals(rootView);

        return rootView;
    }


    public void loadAdditionals(View rootView){

        //loading the search bar
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        searchLayout = (LinearLayout) rootView.findViewById(R.id.search_layout);
        final EditText searchField = (EditText) rootView.findViewById(R.id.search_bar_edittext);
        ImageView clearText = (ImageView) rootView.findViewById(R.id.search_bar_close);

        toolbar.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.VISIBLE);

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
//                donorAdapter.loadData(resultDonors);
            }
        });

    }
}

