package com.nullvoid.blooddonation.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nullvoid.blooddonation.AdminConsoleActivity;
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
                inflate(R.layout.view_card_donor, parent, false);
        return new DonorAdapter.DonorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DonorAdapter.DonorViewHolder holder, int position) {
        final Donor donor = donors.get(position);

        holder.donorName.setText(donor.getName());
        holder.donorGender.setText(donor.getGender());
        holder.bloodGroup.setText(donor.getBloodGroup());
        holder.donorAge.setText(donor.getAge());
        holder.donorDob.setText(donor.getDateOfBirth());
        if(donor.isDonationInLastSixMonths()){
            holder.donationDate.setText("Yes");
        }else{
            holder.donationDate.setText("No");
        }
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

    public void makeCall(final String number, final String name) {

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            Dexter.withActivity((AdminConsoleActivity)context).withPermission(Manifest.permission.CALL_PHONE).
                    withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            AlertDialog.Builder callConfirmBuilder = new AlertDialog.Builder(context);
                            callConfirmBuilder.setTitle("Call "+name+" ?");
                            callConfirmBuilder.setMessage("Are you sure you want to call the number"
                            +number+" ?");
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(context,
                                    "Cannot make phone calls without granting permission", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).withErrorListener(new PermissionRequestErrorListener() {
                @Override
                public void onError(DexterError error) {
                    Toast.makeText(context, error.name(), Toast.LENGTH_SHORT).show();
                }
            }).check();
        }
        else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        }

    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    public static class DonorViewHolder extends RecyclerView.ViewHolder {
        protected TextView donorName, donorGender, bloodGroup, donorAge, donorDob, donationDate, phoneNumber;
        protected TextView donorEmail, donorAddress, donorLocation, donorPin;
        protected ImageView callDonorImage;
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
            callDonorImage = (ImageView) v.findViewById(R.id.call_donor_image);

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
