package com.nullvoid.blooddonation.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nullvoid.blooddonation.DonorProfileActivity;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.Constants;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by sanath on 24/07/17.
 */

public class DonorMinimalAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Donor> donorsList;

    public DonorMinimalAdapter(Context context, ArrayList<Donor> donorsList) {
        super(context, R.layout.list_item_donor, donorsList);
        this.context = context;
        this.donorsList = donorsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Donor donor = donorsList.get(position);

        View donorItem = layoutInflater.inflate(R.layout.list_item_donor, parent, false);

        TextView donorName = (TextView) donorItem.findViewById(R.id.list_donor_name);
        TextView donorLocation = (TextView) donorItem.findViewById(R.id.list_donor_group);
        TextView donorPincode = (TextView) donorItem.findViewById(R.id.list_donor_pincode);
        LinearLayout donorItemLayout = (LinearLayout) donorItem.findViewById(R.id.list_donor_item);

        donorName.setText(donor.getName());
        donorLocation.setText(donor.getLocation());
        donorPincode.setText(donor.getPincode());
        donorItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonorProfileActivity.class);
                intent.putExtra(Constants.donor, Parcels.wrap(donor));
                context.startActivity(intent);
            }
        });

        return donorItem;
    }
}
