package com.nullvoid.blooddonation.admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.adapters.DonationHistoryAdapter;
import com.nullvoid.blooddonation.beans.Match;
import com.nullvoid.blooddonation.others.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 16/07/17.
 */

public class MatchListActivity extends AppCompatActivity {

    MatchListActivity context = this;

    DatabaseReference db;
    ArrayList<Match> matchList = new ArrayList<>();
    DonationHistoryAdapter adapter;

    @BindView(R.id.cardList) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.searchbar) Toolbar searchBar;
    @BindView(R.id.list_view_message_box) CardView listMessageBox;
    @BindView(R.id.list_view_message) TextView listMessageText;
    @BindView(R.id.list_view_title) TextView listTitleText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        listMessageBox.setVisibility(View.VISIBLE);
        listTitleText.setText(R.string.loading);

        searchBar.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = FirebaseDatabase.getInstance().getReference();

        db.child(Constants.matches).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Match match = ds.getValue(Match.class);
                    matchList.add(match);
                }
                adapter = new DonationHistoryAdapter(context, matchList);
                recyclerView.setAdapter(adapter);
                listMessageBox.setVisibility(View.GONE);
            }
            @Override public void onCancelled(DatabaseError databaseError) {
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

    }

}
