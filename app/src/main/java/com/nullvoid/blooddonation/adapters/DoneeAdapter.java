package com.nullvoid.blooddonation.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nullvoid.blooddonation.admin.AdminDonneActivity;
import com.nullvoid.blooddonation.admin.DonorSelectionActivity;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.others.AppConstants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.nullvoid.blooddonation.R.string.call;

/**
 * Created by sanath on 15/06/17.
 */
public class DoneeAdapter extends RecyclerView.Adapter<DoneeAdapter.DoneeViewHolder> {

    Context context;
    private List<Donee> donees;

    public DoneeAdapter(List<Donee> donees, Context context) {
        this.donees = donees;
        this.context = context;
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

        loadButtons(holder, donee);
    }

    public void loadButtons(DoneeAdapter.DoneeViewHolder holder, final Donee donee){

        holder.selectDonorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if the donne has already been assigned show this
                if (donee.getStatus().equals(AppConstants.statusPending())) {
                    new MaterialDialog.Builder(context)
                            .title(R.string.confirm)
                            .content(R.string.reassign_confirmation_message)
                            .contentColor(Color.BLACK)
                            .positiveText(R.string.yes)
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent intent = new Intent(context, DonorSelectionActivity.class);
                                    intent.putExtra(AppConstants.donee(), Parcels.wrap(donee));
                                    context.startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    Intent intent = new Intent(context, DonorSelectionActivity.class);
                    intent.putExtra(AppConstants.donee(), Parcels.wrap(donee));
                    context.startActivity(intent);
                }
            }
        });

        holder.callDoneeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumbersToCallDialog(donee.getRequesterName(), donee.getPatientAttendantName(),
                        donee.getRequesterPhoneNumber(), donee.getPatientAttendantNumber());
            }
        });

        //this part is for additional actions for the pending donne part
        if (donee.getStatus().equals(AppConstants.statusPending())){
            holder.pendingDonneActionsLayout.setVisibility(View.VISIBLE);

            holder.viewSelectedDonorsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AppConstants.donneAction());
                    intent.putExtra(AppConstants.donee(), Parcels.wrap(donee));
                    intent.putExtra(AppConstants.action(),
                            AppConstants.donneActionSelectedDonorsButton());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });

            holder.markCompleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AppConstants.donneAction());
                    intent.putExtra(AppConstants.donee(), Parcels.wrap(donee));
                    intent.putExtra(AppConstants.action(),
                            AppConstants.donneActionMarkCompletedButton());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }
    }


    public void showNumbersToCallDialog(final String requesterName, final String attenderName,
                                        final String requesterNum, final String attenderNum) {

        String[] numbers;

        if (attenderNum.equals(AppConstants.notProvided())) {
            numbers = new String[]{"Requester:\n" + requesterName};
        } else {
            numbers = new String[]{"Requester:\n" + requesterName, "Patient's Attender:\n" + attenderName};
        }

        new MaterialDialog.Builder(context)
                .title(call)
                .items(numbers)
                .itemsColor(Color.BLACK)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which){
                            case 0:
                                call(requesterNum);
                                break;
                            case 1:
                                call(attenderNum);
                                break;
                        }
                        return true;
                    }
                })
                .positiveText(call)
                .negativeText(R.string.cancel)
                .show();
    }

    public void call(final String number) {

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            Dexter.withActivity((AdminDonneActivity) context).withPermission(Manifest.permission.CALL_PHONE).
                    withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + number));
                            context.startActivity(callIntent);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(context, context.getString(R.string.call_permission_denied_message),
                                    Toast.LENGTH_SHORT).show();
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
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        }

    }

    public void loadData(ArrayList<Donee> newDoneeList) {
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
        protected ImageView callDoneeImage, selectDonorImage, viewSelectedDonorsImage, markCompleteImage;
        protected LinearLayout hiddenPartLayout, pendingDonneActionsLayout;
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
            viewSelectedDonorsImage = (ImageView) v.findViewById(R.id.view_selected_donors_image);
            pendingDonneActionsLayout = (LinearLayout) v.findViewById(R.id.pending_donne_actions_layout);
            markCompleteImage = (ImageView) v.findViewById(R.id.mark_complete_image);

            hiddenPartLayout = (LinearLayout) v.findViewById(R.id.hidden_part);
            visiblePart = (RelativeLayout) v.findViewById(R.id.visible_part);
            visiblePart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hiddenPartLayout.getVisibility() == View.VISIBLE) {
                        hiddenPartLayout.setVisibility(View.GONE);
                    } else {
                        hiddenPartLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

}
