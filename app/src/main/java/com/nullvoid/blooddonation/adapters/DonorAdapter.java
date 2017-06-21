package com.nullvoid.blooddonation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.Donor;

import java.util.List;

/**
 * Created by sanath on 15/06/17.
 */
public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorViewHolder> {

    final Context context;
    private List<Donor> donors;

    public DonorAdapter(List<Donor> donors, Context context) {
        this.donors = donors;
        this.context = context;
    }

    @Override
    public DonorAdapter.DonorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_donor, parent, false);
        return new DonorAdapter.DonorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DonorAdapter.DonorViewHolder holder, int position) {
        Donor donor = donors.get(position);

        holder.donorName.setText(donor.getName());
        holder.donorGender.setText(donor.getGender());
        holder.bloodGroup.setText(donor.getBloodGroup());
        holder.donorAge.setText(donor.getAge());
        holder.donorDob.setText(donor.getDateOfBirth());
        holder.donationDate.setText(donor.getDonatedDate());
        holder.phoneNumber.setText(donor.getPhoneNumber());
        holder.donorEmail.setText(donor.getEmail());
        holder.donorAddress.setText(donor.getAddress());
        holder.donorLocation.setText(donor.getLocation());
        holder.donorPin.setText(donor.getPincode());

        holder.chooseADonee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Assing a donee selected", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return donors.size();
    }

    public static class DonorViewHolder extends RecyclerView.ViewHolder {
        protected TextView donorName, donorGender, bloodGroup, donorAge, donorDob, donationDate, phoneNumber;
        protected TextView donorEmail, donorAddress, donorLocation, donorPin, chooseADonee;
        protected LinearLayout hiddenPart;
        protected RelativeLayout visiblePart;


        public DonorViewHolder(View v) {
            super(v);
            donorName = (TextView) v.findViewById(R.id.donor_name);
            donorGender = (TextView) v.findViewById(R.id.donor_gender);
            bloodGroup = (TextView) v.findViewById(R.id.donor_group);
            donorAge = (TextView) v.findViewById(R.id.donor_age);
            donorDob = (TextView) v.findViewById(R.id.donor_dob);
            donationDate = (TextView) v.findViewById(R.id.donation_date);
            phoneNumber = (TextView) v.findViewById(R.id.phone_number);
            donorEmail = (TextView) v.findViewById(R.id.donor_email);
            donorAddress = (TextView) v.findViewById(R.id.donor_address);
            donorLocation = (TextView) v.findViewById(R.id.donor_location);
            donorPin = (TextView) v.findViewById(R.id.donor_pin);

            chooseADonee = (TextView) v.findViewById(R.id.donor_layout_button);

            visiblePart = (RelativeLayout) v.findViewById(R.id.visible_part);
            hiddenPart = (LinearLayout) v.findViewById(R.id.hidden_part);

            visiblePart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hiddenPart.getVisibility() == View.VISIBLE) {
                        hiddenPart.setVisibility(View.GONE);
                    } else {
                        hiddenPart.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
