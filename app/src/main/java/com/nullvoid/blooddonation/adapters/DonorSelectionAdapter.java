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

import org.parceler.Parcels;

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
                inflate(R.layout.card_donor, parent, false);
        return new DonorSelectionAdapter.DonorSelectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DonorSelectionViewHolder holder, int position) {
        final SelectionDonor selectionDonor = selectionDonors.get(position);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! selectionDonor.isSelected()){
                    sendSelectionChange(context.getString(R.string.select), selectionDonor);
                }else{
                    sendSelectionChange(context.getString(R.string.remove), selectionDonor);
                }
                selectionDonor.setSelected(!selectionDonor.isSelected());
                holder.view.setBackgroundColor(selectionDonor.isSelected() ? Color.parseColor("#b3ffb6") : Color.WHITE);
            }
        });

        holder.donorName.setText(selectionDonor.getDonor().getName());
        holder.donorGender.setText(selectionDonor.getDonor().getGender());
        holder.bloodGroup.setText(selectionDonor.getDonor().getBloodGroup());
        holder.donorAge.setText(selectionDonor.getDonor().getAge());
        holder.donorDob.setText(selectionDonor.getDonor().getDateOfBirth());
        holder.donationDate.setText(selectionDonor.getDonor().getDonatedDate());
        holder.phoneNumber.setText(selectionDonor.getDonor().getPhoneNumber());
        holder.donorEmail.setText(selectionDonor.getDonor().getEmail());
        holder.donorAddress.setText(selectionDonor.getDonor().getAddress());
        holder.donorLocation.setText(selectionDonor.getDonor().getLocation());
        holder.donorPin.setText(selectionDonor.getDonor().getPincode());
        holder.chooseThisDonor.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return selectionDonors.size();
    }

    public void sendSelectionChange(String action, SelectionDonor data){
        Intent intent = new Intent(context.getString(R.string.selection_change));
        intent.putExtra("action", action);
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
            chooseThisDonor = (TextView) v.findViewById(R.id.donor_layout_button);
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
}
