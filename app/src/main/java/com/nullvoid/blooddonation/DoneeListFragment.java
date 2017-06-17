package com.nullvoid.blooddonation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.adapters.DoneeAdapter;
import com.nullvoid.blooddonation.beans.Donee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeListFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager llm;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;

    List<Donee> donees;
    Donee donee;

    public DoneeListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Retriving Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        View rootView = inflater.inflate(R.layout.list_view_layout, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        donees = new ArrayList<Donee>();

        dbRef = FirebaseDatabase.getInstance().getReference("donee");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    donee = postSnapshot.getValue(Donee.class);
                    donees.add(donee);
                }
                DoneeAdapter doneeAdapter = new DoneeAdapter(donees, getActivity());
                recyclerView.setAdapter(doneeAdapter);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

