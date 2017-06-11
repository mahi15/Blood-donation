package com.nullvoid.blooddonation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nullvoid.blooddonation.beans.Donee;

import java.util.List;

/**
 * Created by sanath on 11/06/17.
 */

public class DoneeAdapter extends RecyclerView.Adapter<DoneeAdapter.DoneeViewHolder> {

    private List<Donee> donees;

    public DoneeAdapter(List<Donee> donees) {
        this.donees = donees;
    }

    @Override
    public DoneeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.donee_card, parent, false);
        return new DoneeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DoneeViewHolder holder, int position) {
        Donee donee = donees.get(position);
        holder.requesterName.setText(donee.getName());
        holder.phoneNumber.setText(donee.getPhoneNumber());
        holder.bloodGroup.setText(donee.getBloodGroup());
        holder.requDate.setText(donee.getReqDate());
        holder.reqTime.setText(donee.getReqTime());
        holder.patientName.setText(donee.getPatientName());
        holder.patientId.setText(donee.getPatientID());
        holder.hospitalName.setText(donee.getHospitalName());
        holder.hospitalAddress.setText(donee.getHospitalAddress());
        holder.hospitalName.setText(donee.getHospitalNumber());
        holder.hospitalPin.setText(donee.getHospitalPin());

        holder.selectDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.hiddenPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return donees.size();
    }

    public static class DoneeViewHolder extends RecyclerView.ViewHolder  {
        protected TextView requesterName, phoneNumber, bloodGroup, requDate, reqTime, patientName;
        protected  TextView patientId, hospitalName, hospitalNumber, hospitalAddress, hospitalPin, selectDonor;
        protected LinearLayout hiddenPart;
        protected RelativeLayout visiblePart;

        public DoneeViewHolder(View v) {
            super(v);

            final Context context;
            context = v.getContext();

            requesterName = (TextView) v.findViewById(R.id.requester_name);
            phoneNumber = (TextView) v.findViewById(R.id.phone_number);
            bloodGroup = (TextView) v.findViewById(R.id.donee_group);
            requDate = (TextView) v.findViewById(R.id.req_date);
            reqTime = (TextView) v.findViewById(R.id.req_time);
            patientName = (TextView) v.findViewById(R.id.patient_name);
            patientId = (TextView) v.findViewById(R.id.patient_id);
            hospitalName = (TextView) v.findViewById(R.id.hospital_name);
            hospitalAddress = (TextView) v.findViewById(R.id.hospital_address);
            hospitalNumber = (TextView) v.findViewById(R.id.hospital_number);
            hospitalPin = (TextView) v.findViewById(R.id.hospital_pin);
            selectDonor = (TextView) v.findViewById(R.id.select_donor);

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

            selectDonor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "WOkrss", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
