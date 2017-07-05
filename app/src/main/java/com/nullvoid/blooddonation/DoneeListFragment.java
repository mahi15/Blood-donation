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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.adapters.DoneeAdapter;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.others.AppConstants;

import java.util.ArrayList;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeListFragment extends Fragment {

    RecyclerView recyclerView;
    View rootView;
    LinearLayoutManager llm;
    ProgressDialog progressDialog;

    Toolbar toolbar;

    DatabaseReference dbRef;

    ArrayList<Donee> doneesList;
    DoneeAdapter doneeAdapter;

    public DoneeListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doneesList = new ArrayList<Donee>();
        dbRef = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Retriving Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.layout_list_view, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAdditionals(rootView);
    }

    //ive added data retriva from db in onresume
    //cos when returning from donorselection activity we need to refresh the data
    @Override
    public void onResume() {
        super.onResume();

        dbRef.child(AppConstants.donees()).orderByChild(AppConstants.status())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        doneesList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Donee donee = postSnapshot.getValue(Donee.class);
                            doneesList.add(donee);
                        }
                        doneeAdapter = new DoneeAdapter(doneesList, getActivity());
                        recyclerView.setAdapter(doneeAdapter);
                        progressDialog.dismiss();
                        doneeAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    public void loadAdditionals(View rootView){

        //loading the search bar
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        final EditText searchField = (EditText) rootView.findViewById(R.id.search_bar_edittext);
        ImageView clearText = (ImageView) rootView.findViewById(R.id.search_bar_close);

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
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

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

