package com.nullvoid.blooddonation.admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.adapters.DoneeAdapter;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.others.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeListFragment extends Fragment {

    View rootView;
    LinearLayoutManager llm;

    String status;

    DatabaseReference dbRef;

    ArrayList<Donee> doneesList;
    DoneeAdapter doneeAdapter;


    @BindView(R.id.list_view_message_box) CardView listMessageBox;
    @BindView(R.id.list_view_message) TextView listMessageText;
    @BindView(R.id.list_view_title) TextView listTitleText;
    @BindView(R.id.cardList) RecyclerView recyclerView;
    @BindView(R.id.search_bar_edittext) EditText searchField;
    @BindView(R.id.searchbar) Toolbar toolbar;
    @BindView(R.id.search_bar_close) ImageView clearText;

    public DoneeListFragment() {
    }

    public static DoneeListFragment newInstance(String status){
        DoneeListFragment doneeFragment = new DoneeListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.status, status);
        doneeFragment.setArguments(args);
        return doneeFragment;
    }

    public void readArguments(){
        Bundle args = getArguments();
        status = (String) args.get(Constants.status);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readArguments();

        doneesList = new ArrayList<Donee>();
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_list_view, container, false);
        ButterKnife.bind(this, rootView);

        //show loading card till the data is loaded
        listMessageBox.setVisibility(View.VISIBLE);
        listTitleText.setText(R.string.loading);

        doneeAdapter = new DoneeAdapter(doneesList, getActivity());
        llm = new LinearLayoutManager(getActivity());

        recyclerView.setAdapter(doneeAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        dbRef.child(Constants.donees)
                .orderByChild(Constants.status)
                .equalTo(status)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        doneesList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Donee donee = postSnapshot.getValue(Donee.class);
                            doneesList.add(donee);
                        }
                        doneeAdapter.notifyDataSetChanged();

                        listMessageBox.setVisibility(View.GONE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        loadAdditionals(rootView);

        return rootView;
    }


    public void loadAdditionals(View rootView){

        toolbar.setVisibility(View.VISIBLE);

        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchField.setText("");
                searchField.onEditorAction(EditorInfo.IME_ACTION_DONE);
                doneeAdapter.loadData(doneesList);

            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().toLowerCase();
                ArrayList<Donee> resultDonees = new ArrayList<Donee>();
                for(Donee donee : doneesList){
                    if (donee.getPatientName().toLowerCase().contains(searchQuery) ||
                            donee.getPatientAreaofResidence().toLowerCase().contains(searchQuery) ||
                            donee.getHospitalPin().contains(searchQuery) ||
                            donee.getHospitalAddress().toLowerCase().contains(searchQuery)){
                        resultDonees.add(donee);
                    }
                }
                doneeAdapter.loadData(resultDonees);
            }
        });
    }
}

