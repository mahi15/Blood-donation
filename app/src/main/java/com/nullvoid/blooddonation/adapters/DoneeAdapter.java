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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.admin.DonorSelectionActivity;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.others.Constants;
import com.nullvoid.blooddonation.others.CommonFunctions;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        holder.hospitalNumber.setText(donee.getHospitalNumber());
        holder.hospitalPin.setText(donee.getHospitalPin());

        if (donee.getStatus().equals(Constants.statusPending())) {
            holder.pendingLable.setVisibility(View.VISIBLE);
        }
        loadButtons(holder, donee);
    }

    public void loadButtons(DoneeAdapter.DoneeViewHolder holder, final Donee donee){

        holder.selectDonorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the donne has already been assigned show this
                if (donee.getStatus().equals(Constants.statusPending())) {
                    new MaterialDialog.Builder(context).title(R.string.confirm)
                            .content(R.string.reassign_confirmation_message)
                            .contentColor(Color.BLACK).positiveText(R.string.yes).negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent intent = new Intent(context, DonorSelectionActivity.class);
                                    intent.putExtra(Constants.donee(), Parcels.wrap(donee));
                                    context.startActivity(intent);
                                }
                            }).show();
                } else {
                    Intent intent = new Intent(context, DonorSelectionActivity.class);
                    intent.putExtra(Constants.donee(), Parcels.wrap(donee));
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
        if (donee.getStatus().equals(Constants.statusPending())){
            holder.pendingDonneActionsLayout.setVisibility(View.VISIBLE);

            holder.viewSelectedDonorsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Constants.donneAction());
                    intent.putExtra(Constants.donee(), Parcels.wrap(donee));
                    intent.putExtra(Constants.action,
                            Constants.donneActionSelectedDonorsButton());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });

            holder.markCompleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Constants.donneAction());
                    intent.putExtra(Constants.donee(), Parcels.wrap(donee));
                    intent.putExtra(Constants.action,
                            Constants.donneActionMarkCompletedButton());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }
    }

    public void showNumbersToCallDialog(final String requesterName, final String attenderName,
                                        final String requesterNum, final String attenderNum) {

        String[] numbers;

        if (attenderNum.equals(Constants.notProvided)) {
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
                                CommonFunctions.call(context, requesterNum);
                                break;
                            case 1:
                                CommonFunctions.call(context, attenderNum);
                                break;
                        }
                        return true;
                    }
                })
                .positiveText(call)
                .negativeText(R.string.cancel)
                .show();
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
        View view;

        @BindView(R.id.requester_name) TextView requesterName;
        @BindView(R.id.phone_number) TextView phoneNumber;
        @BindView(R.id.donee_group) TextView bloodGroup;
        @BindView(R.id.req_date) TextView requDate;
        @BindView(R.id.req_time) TextView reqTime;
        @BindView(R.id.donor_name) TextView patientName;
        @BindView(R.id.patient_id) TextView patientId;
        @BindView(R.id.hospital_name) TextView hospitalName;
        @BindView(R.id.hospital_address) TextView hospitalAddress;
        @BindView(R.id.hospital_number) TextView hospitalNumber;
        @BindView(R.id.hospital_pin) TextView hospitalPin;
        @BindView(R.id.pending_lable) TextView pendingLable;
        //buttons
        @BindView(R.id.call_donee_image) ImageView callDoneeImage;
        @BindView(R.id.select_donors_image) ImageView selectDonorImage;
        @BindView(R.id.view_selected_donors_image) ImageView viewSelectedDonorsImage;
        @BindView(R.id.mark_complete_image) ImageView markCompleteImage;
        //layouts
        @BindView(R.id.pending_donne_actions_layout) LinearLayout pendingDonneActionsLayout;
        @BindView(R.id.hidden_part) LinearLayout hiddenPartLayout;
        @BindView(R.id.visible_part) RelativeLayout visiblePart;

        public DoneeViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
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
