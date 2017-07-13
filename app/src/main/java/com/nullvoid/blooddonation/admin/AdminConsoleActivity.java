package com.nullvoid.blooddonation.admin;

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
import com.nullvoid.blooddonation.others.AppConstants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by sanath on 06/07/17.
 */

public class AdminConsoleActivity extends AppCompatActivity {

    Button donneButton, donorButton, historButton;
    Toolbar toolbar;
    DatabaseReference db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_console);

        db = FirebaseDatabase.getInstance().getReference();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        donorButton = (Button) findViewById(R.id.admin_donor_button);
        donorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminConsoleActivity.this, DonorListActivity.class));
            }
        });

        historButton = (Button) findViewById(R.id.admin_history_button);
        historButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(AdminConsoleActivity.this, AdminDonneActivity.class));
            }
        });

        donneButton = (Button) findViewById(R.id.admin_donne_button);
        donneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminConsoleActivity.this, AdminDonneActivity.class));
            }
        });

        loadStatus();
    }

    public void loadStatus(){

        final TextView donateTodayCountView, requestsTodayCountView;

        SimpleDateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy");
        String todate = thisDate.format(new Timestamp(System.currentTimeMillis()));

        donateTodayCountView = (TextView) findViewById(R.id.donate_today_count);
        requestsTodayCountView = (TextView) findViewById(R.id.req_made_today_count);

        db.child(AppConstants.donees())
                .orderByChild(AppConstants.requestedDate())
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

        db.child(AppConstants.donors())
                .orderByChild(AppConstants.registeredDate())
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
