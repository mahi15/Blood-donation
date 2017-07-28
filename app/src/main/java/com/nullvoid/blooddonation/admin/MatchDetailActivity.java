package com.nullvoid.blooddonation.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.adapters.DonorMinimalAdapter;
import com.nullvoid.blooddonation.beans.Match;
import com.nullvoid.blooddonation.others.Constants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 17/07/17.
 */

public class MatchDetailActivity extends AppCompatActivity {
    Activity context = this;

    Match match;
    Intent startedIntent;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.match_donne_name) TextView donneName;
    @BindView(R.id.match_req_date) TextView donneRequestedOn;
    @BindView(R.id.match_hospital_name) TextView donneHospitalName;
    @BindView(R.id.match_blood_group) TextView donneBloodGroup;
    @BindView(R.id.match_donation_status) TextView donationStatus;
    @BindView(R.id.match_matched_date) TextView matchedDate;
    @BindView(R.id.match_matched_time) TextView matchedTime;
    @BindView(R.id.match_donors_contacted) TextView contactedDonorsCount;
    @BindView(R.id.match_donors_helped) TextView helpedDonorsCount;
    @BindView(R.id.match_contacted_donors_list) ExpandableHeightListView contactedDonorsListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);
        ButterKnife.bind(context);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        match = Parcels.unwrap(getIntent().getParcelableExtra(Constants.matches));

        donneName.setText(match.getDonee().getPatientName());
        donneRequestedOn.setText(match.getDonee().getRequestedDate());
        donneHospitalName.setText(match.getDonee().getHospitalName());
        donneBloodGroup.setText(match.getDonee().getRequiredBloodGroup());
        donationStatus.setText(match.isCompleted() ? "COMPLETED" : "NOT COMPLETE");
        matchedDate.setText(match.getMatchedDate());
        matchedTime.setText(match.getMatchedTime());
        contactedDonorsCount.setText(String.valueOf(match.getContactedDonors().size()));
        helpedDonorsCount.setText(match.getHelpedDonors() == null ? "NONE" : String.valueOf(match.getHelpedDonors().size()));

        DonorMinimalAdapter contactedDonorAdapter = new DonorMinimalAdapter(context, match.getContactedDonors());
        contactedDonorsListView.setAdapter(contactedDonorAdapter);
        contactedDonorsListView.setExpanded(true);
    }
}
