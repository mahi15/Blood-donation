package com.nullvoid.blooddonation.adapters;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.nullvoid.blooddonation.DonorSelectionActivity;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.others.AppConstants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanath on 15/06/17.
 */
public class DoneeAdapter extends RecyclerView.Adapter<DoneeAdapter.DoneeViewHolder> {

    Context adminConsoleActivity;
    private List<Donee> donees;

    public DoneeAdapter(List<Donee> donees, Context adminConsoleActivity) {
        this.donees = donees;
        this.adminConsoleActivity = adminConsoleActivity;
    }

    @Override
    public DoneeAdapter.DoneeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.view_card_donee, parent, false);
        return new DoneeAdapter.DoneeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DoneeAdapter.DoneeViewHolder holder, int position) {
        final Donee donee = donees.get(position);
        holder.requesterName.setText(donee.getRequesterName());
        holder.phoneNumber.setText(donee.getRequesterPhoneNumber());
        holder.bloodGroup.setText(donee.getRequiredBloodGroup());
        holder.requDate.setText(donee.getRequiredDate());
        holder.reqTime.setText(donee.getPatientAttendantNumber());
        holder.patientName.setText(donee.getPatientName());
        holder.patientId.setText(donee.getPatientID());
        holder.hospitalName.setText(donee.getHospitalName());
        holder.hospitalAddress.setText(donee.getHospitalAddress());
        holder.hospitalName.setText(donee.getHospitalNumber());
        holder.hospitalPin.setText(donee.getHospitalPin());

        holder.selectDonorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adminConsoleActivity, DonorSelectionActivity.class);
                intent.putExtra(AppConstants.donee(), Parcels.wrap(donee));
                adminConsoleActivity.startActivity(intent);
            }
        });

        holder.callDoneeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showNumbersToCallDialog(donee.getRequesterName(), donee.getPatientAttendantName(),
                    donee.getRequesterPhoneNumber(), donee.getPatientAttendantNumber());
            }
        });

        //set the color of the donee based on status
        if (donee.getStatus().equals(AppConstants.statusNotComplete())) {
            holder.view.setBackgroundColor(Color.parseColor("#ffb5b7"));
        } else if (donee.getStatus().equals(AppConstants.statusPending())) {
            holder.view.setBackgroundColor(Color.parseColor("#fffab5"));
        } else if (donee.getStatus().equals(AppConstants.statusComplete())) {
            holder.view.setBackgroundColor(Color.parseColor("#b3ffb6"));
        }
    }

    public void showNumbersToCallDialog(final String requesterName, final String attenderName,
                                        final String requesterNum, final String attenderNum) {

        AlertDialog.Builder builder = new AlertDialog.Builder(adminConsoleActivity);
        builder.setTitle("Choose whom to call");

        //if attender details not provided
        if(attenderNum.equals(AppConstants.notProvided())){
            String[] numbers = {"Requester:\n"+requesterName};
            builder.setItems(numbers, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            call(requesterNum);
                            break;
                    }
                }
            });
        }
        //if attender details is provided
        else {
            final String[] numbers = {"Requester:\n"+requesterName, "Patient's Attender:\n"+attenderName};
            builder.setItems(numbers, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            call(requesterNum);
                            break;
                        case 1:
                            call(attenderNum);
                            break;
                    }
                }
            });
        }
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });

        builder.show();
    }

    public void call(final String number){

        if (ActivityCompat.checkSelfPermission(adminConsoleActivity,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            Dexter.withActivity((AdminConsoleActivity)adminConsoleActivity).withPermission(Manifest.permission.CALL_PHONE).
                    withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + number));
                            adminConsoleActivity.startActivity(callIntent);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(adminConsoleActivity,
                                    "Cannot make phone calls without granting permission", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).withErrorListener(new PermissionRequestErrorListener() {
                @Override
                public void onError(DexterError error) {
                    Toast.makeText(adminConsoleActivity, error.name(), Toast.LENGTH_SHORT).show();
                }
            }).check();
        }
        else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            adminConsoleActivity.startActivity(callIntent);
        }

    }

    public void loadData(ArrayList<Donee> newDoneeList){
        donees = newDoneeList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return donees.size();
    }

    public static class DoneeViewHolder extends RecyclerView.ViewHolder {
        protected TextView requesterName, phoneNumber, bloodGroup, requDate, reqTime, patientName;
        protected TextView patientId, hospitalName, hospitalNumber, hospitalAddress, hospitalPin;
        protected ImageView callDoneeImage, selectDonorImage;
        protected LinearLayout hiddenPart;
        protected RelativeLayout visiblePart;
        View view;

        public DoneeViewHolder(View v) {
            super(v);

            view = v;
            requesterName = (TextView) v.findViewById(R.id.requester_name);
            phoneNumber = (TextView) v.findViewById(R.id.phone_number);
            bloodGroup = (TextView) v.findViewById(R.id.donee_group);
            requDate = (TextView) v.findViewById(R.id.req_date);
            reqTime = (TextView) v.findViewById(R.id.req_time);
            patientName = (TextView) v.findViewById(R.id.donor_name);
            patientId = (TextView) v.findViewById(R.id.patient_id);
            hospitalName = (TextView) v.findViewById(R.id.hospital_name);
            hospitalAddress = (TextView) v.findViewById(R.id.hospital_address);
            hospitalNumber = (TextView) v.findViewById(R.id.hospital_number);
            hospitalPin = (TextView) v.findViewById(R.id.hospital_pin);
            selectDonorImage = (ImageView) v.findViewById(R.id.select_donors_image);
            callDoneeImage = (ImageView) v.findViewById(R.id.call_donee_image);

            hiddenPart = (LinearLayout) v.findViewById(R.id.hidden_part);
            visiblePart = (RelativeLayout) v.findViewById(R.id.visible_part);
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
