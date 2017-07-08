package com.nullvoid.blooddonation.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.SelectionDonor;
import com.nullvoid.blooddonation.others.AppConstants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanath on 15/06/17.
 */
public class DonorSelectionAdapter extends RecyclerView.Adapter<DonorSelectionAdapter.DonorSelectionViewHolder> {

    private List<SelectionDonor> selectionDonors;
    public final Context context;

    public DonorSelectionAdapter(List<SelectionDonor> selectionDonors, Context context) {
        this.selectionDonors = selectionDonors;
        this.context = context;
    }

    @Override
    public DonorSelectionAdapter.DonorSelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.view_card_donor, parent, false);
        return new DonorSelectionAdapter.DonorSelectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DonorSelectionViewHolder holder, int position) {
        final SelectionDonor selectionDonor = selectionDonors.get(position);

        if (selectionDonor.isSelected()){
            holder.view.setBackgroundColor(Color.parseColor("#b3ffb6"));
            holder.checkBox.setChecked(true);
        } else {
            holder.view.setBackgroundColor(Color.WHITE);
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectionDonor.isSelected()){
                    sendSelectionChange(AppConstants.remove(), selectionDonor);
                }else{
                    sendSelectionChange(AppConstants.select(), selectionDonor);
                }
                selectionDonor.setSelected(!selectionDonor.isSelected());

                notifyDataSetChanged();
                holder.view.setBackgroundColor(selectionDonor.isSelected() ? Color.parseColor("#b3ffb6") : Color.WHITE);
            }
        });

        holder.donorName.setText(selectionDonor.getDonor().getName());
        holder.donorGender.setText(selectionDonor.getDonor().getGender());
        holder.bloodGroup.setText(selectionDonor.getDonor().getBloodGroup());
        holder.donorAge.setText(selectionDonor.getDonor().getAge());
        holder.donorDob.setText(selectionDonor.getDonor().getDateOfBirth());
        holder.donationDate.setText(String.valueOf(selectionDonor.getDonor().isDonationInLastSixMonths()));
        holder.phoneNumber.setText(selectionDonor.getDonor().getPhoneNumber());
        holder.donorEmail.setText(selectionDonor.getDonor().getEmail());
        holder.donorAddress.setText(selectionDonor.getDonor().getAddress());
        holder.donorLocation.setText(selectionDonor.getDonor().getLocation());
        holder.donorPin.setText(selectionDonor.getDonor().getPincode());
    }

    @Override
    public int getItemCount() {
        return selectionDonors.size();
    }

    public void sendSelectionChange(String action, SelectionDonor data){
        Intent intent = new Intent(AppConstants.selectionChange());
        intent.putExtra(AppConstants.action(), action);
        intent.putExtra("data", Parcels.wrap(data));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static class DonorSelectionViewHolder extends RecyclerView.ViewHolder  {
        protected TextView donorName, donorGender, bloodGroup, donorAge, donorDob, donationDate, phoneNumber;
        protected  TextView donorEmail, donorAddress, donorLocation, donorPin, chooseThisDonor;
        protected LinearLayout hiddenPart;
        protected RelativeLayout visiblePart;
        CheckBox checkBox;
        private View view;


        public DonorSelectionViewHolder(View v) {
            super(v);
            view = v;
            checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            checkBox.setVisibility(View.VISIBLE);
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
            visiblePart = (RelativeLayout) v.findViewById(R.id.visible_part);
            hiddenPart = (LinearLayout) v.findViewById(R.id.hidden_part);

            visiblePart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hiddenPart.getVisibility() == View.VISIBLE){
                        hiddenPart.setVisibility(View.GONE);
                    }else{
                        hiddenPart.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void loadData(ArrayList<SelectionDonor> newList){
        selectionDonors = newList;
        notifyDataSetChanged();
    }
}
