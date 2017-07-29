package com.nullvoid.blooddonation.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.others.Constants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 06/07/17.
 */

public class AdminConsoleActivity extends AppCompatActivity {

    Activity context = this;

    DatabaseReference db;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.admin_donor_button) Button donorButton;
    @BindView(R.id.admin_donne_button) Button donneButton;
    @BindView(R.id.admin_history_button) Button historyButton;
    @BindView(R.id.donate_today_count) TextView donateTodayCountView;
    @BindView(R.id.req_made_today_count) TextView requestsTodayCountView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_console);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = FirebaseDatabase.getInstance().getReference();


        donorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminConsoleActivity.this, DonorListActivity.class));
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DonationHistoryActivity.class));
            }
        });

        donneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminConsoleActivity.this, DonneListActivity.class));
            }
        });

        loadStatus();
    }

    public void loadStatus(){

        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        String todate = thisDate.format(new Timestamp(System.currentTimeMillis()));

        db.child(Constants.donees())
                .orderByChild(Constants.requestedDate())
                .equalTo(todate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String requestsTodayCount = String.valueOf(dataSnapshot.getChildrenCount());
                requestsTodayCountView.setText(requestsTodayCount);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        db.child(Constants.donors())
                .orderByChild(Constants.registeredDate())
                .equalTo(todate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String donorsTodayCount = String.valueOf(dataSnapshot.getChildrenCount());
                        donateTodayCountView.setText(donorsTodayCount);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
