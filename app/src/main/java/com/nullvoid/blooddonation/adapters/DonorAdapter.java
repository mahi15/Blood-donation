package com.nullvoid.blooddonation.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.Constants;
import com.nullvoid.blooddonation.others.CommonFunctions;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 15/06/17.
 */
public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorViewHolder> {

    public final Context context;
    public List<Donor> donors;

    public DonorAdapter(List<Donor> donors, Context context) {
        this.donors = donors;
        this.context = context;
    }

    @Override
    public DonorAdapter.DonorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.view_card_donor, parent, false);
        return new DonorAdapter.DonorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DonorAdapter.DonorViewHolder holder, int position) {
        final Donor donor = donors.get(position);

        if (donor.isSelected()){
            holder.view.setBackgroundColor(Color.parseColor("#b3ffb6"));
            holder.checkBox.setChecked(true);
        } else {
            holder.view.setBackgroundColor(Color.WHITE);
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String action = donor.isSelected() ? Constants.remove : Constants.select;
                sendSelectionChange(action, donor);
                donor.setSelected(!donor.isSelected());

                notifyDataSetChanged();
                holder.view.setBackgroundColor(donor.isSelected() ? Color.parseColor("#b3ffb6") : Color.WHITE);
            }
        });

        holder.donorName.setText(donor.getName());
        holder.donorGender.setText(donor.getGender());
        holder.bloodGroup.setText(donor.getBloodGroup());
        holder.donorAge.setText(donor.getAge());
        holder.donorDob.setText(donor.getDateOfBirth());
        String lastDonated = donor.isDonationInLastSixMonths() ? "Yes" : "No";
        holder.donationDate.setText(lastDonated);
        holder.phoneNumber.setText(donor.getPhoneNumber());
        holder.donorEmail.setText(donor.getEmail());
        holder.donorAddress.setText(donor.getAddress());
        holder.donorLocation.setText(donor.getLocation());
        holder.donorPin.setText(donor.getPincode());

        holder.callDonorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall(donor.getPhoneNumber(), donor.getName());
            }
        });
    }

    public void loadData(ArrayList<Donor> newDonorList) {
        donors = newDonorList;
        notifyDataSetChanged();
    }

    public void makeCall(final String number, final String name) {

        new MaterialDialog.Builder(context)
                .title(R.string.confirm)
                .content(context.getString(R.string.call) + " " + name + "\n" + number)
                .autoDismiss(true)
                .positiveText(R.string.call)
                .negativeText(R.string.cancel)
                .contentColor(Color.BLACK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonFunctions.call(context, number);
                    }
                })
                .autoDismiss(true)
                .show();
    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    public void sendSelectionChange(String action, Donor data){
        Intent intent = new Intent(Constants.selectionChange);
        intent.putExtra(Constants.action, action);
        intent.putExtra("data", Parcels.wrap(data));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static class DonorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.donor_name) TextView donorName;
        @BindView(R.id.donor_gender) TextView donorGender;
        @BindView(R.id.donor_group) TextView bloodGroup;
        @BindView(R.id.donor_age) TextView donorAge;
        @BindView(R.id.donor_dob) TextView donorDob;
        @BindView(R.id.donation_date) TextView donationDate;
        @BindView(R.id.phone_number) TextView phoneNumber;
        @BindView(R.id.donor_email) TextView donorEmail;
        @BindView(R.id.donor_address) TextView donorAddress;
        @BindView(R.id.donor_location) TextView donorLocation;
        @BindView(R.id.donor_pin) TextView donorPin;
        @BindView(R.id.call_donor_image) ImageView callDonorImage;
        @BindView(R.id.visible_part) RelativeLayout visiblePart;
        @BindView(R.id.hidden_part) LinearLayout hiddenPart;
        @BindView(R.id.selection_checkBox) CheckBox checkBox;

        public View view;

        public DonorViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            checkBox.setVisibility(View.VISIBLE);

            view = v;
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
