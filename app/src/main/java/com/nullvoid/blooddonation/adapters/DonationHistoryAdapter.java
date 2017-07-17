package com.nullvoid.blooddonation.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.admin.MatchDetailActivity;
import com.nullvoid.blooddonation.beans.Match;
import com.nullvoid.blooddonation.others.Constants;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 16/07/17.
 */

public class DonationHistoryAdapter extends
        RecyclerView.Adapter<DonationHistoryAdapter.DonationHistoryViewHolder> {

    Context context;
    ArrayList<Match> donationsList;

    public DonationHistoryAdapter(Context context, ArrayList<Match> donationsList) {
        this.context = context;
        this.donationsList = donationsList;
    }

    @Override
    public DonationHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_card_donation_history, parent, false);
        return new DonationHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DonationHistoryViewHolder holder, int position) {
        final Match match = donationsList.get(position);
        holder.donneName.setText(match.getDonee().getPatientName());
        holder.donatedDate.setText(match.getCompletedDate());
        holder.requestedDate.setText(match.getDonee().getRequestedDate());
        holder.hospitalName.setText(match.getDonee().getHospitalName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MatchDetailActivity.class);
                intent.putExtra(Constants.matches, Parcels.wrap(match));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donationsList.size();
    }

    public class DonationHistoryViewHolder extends RecyclerView.ViewHolder{
        View view;
        @BindView(R.id.donne_name) TextView donneName;
        @BindView(R.id.hospital_name) TextView hospitalName;
        @BindView(R.id.requested_date) TextView requestedDate;
        @BindView(R.id.donated_date) TextView donatedDate;

        public DonationHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view = itemView;
        }
    }

}
