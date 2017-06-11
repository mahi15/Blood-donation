package com.nullvoid.blooddonation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public void onBindViewHolder(DoneeViewHolder holder, int position) {
        Donee donee = donees.get(position);
        holder.patientName.setText(donee.getPatientName());
        holder.bloodGroup.setText(donee.getBloodGroup());
        holder.requDate.setText(donee.getReqDate());
        holder.location.setText(donee.getHospitalAddress());
    }

    @Override
    public int getItemCount() {
        return donees.size();
    }

    public static class DoneeViewHolder extends RecyclerView.ViewHolder  {
        protected TextView patientName, location, requDate, bloodGroup;

        public DoneeViewHolder(View v) {
            super(v);
            bloodGroup = (TextView) v.findViewById(R.id.donee_group);
            patientName = (TextView) v.findViewById(R.id.patient_name);
            location = (TextView) v.findViewById(R.id.location);
            requDate = (TextView) v.findViewById(R.id.requested_on);
        }
    }
}
