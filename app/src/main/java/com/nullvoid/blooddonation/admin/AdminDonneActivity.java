package com.nullvoid.blooddonation.admin;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.adapters.AdminPagerAdapter;
import com.nullvoid.blooddonation.beans.Donee;
import com.nullvoid.blooddonation.beans.Donor;
import com.nullvoid.blooddonation.others.AppConstants;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by sanath on 13/06/17.
 */

public class AdminDonneActivity extends AppCompatActivity {

    DatabaseReference db;
    Context context;
    private ViewPager viewPager;
    private AdminPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_donne);

        context = AdminDonneActivity.this;

        //adding the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new AdminPagerAdapter(getSupportFragmentManager(), AdminDonneActivity.this);
        db = FirebaseDatabase.getInstance().getReference();

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mAdapter.getTabView(i));
        }
        viewPager.setAdapter(mAdapter);
    }

    public void getSelectedDonors(final String donneId) {

        final MaterialDialog loadingDialog = new MaterialDialog.Builder(context)
                .title(R.string.loading)
                .content(R.string.please_wait_message)
                .progress(true, 0)
                .cancelable(false)
                .show();

        final ArrayList<Donor> contactedDonorsList = new ArrayList<>();

        db.child(AppConstants.matches())
                .child(donneId)
                .child(AppConstants.contactedDonors())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshotMain) {

                        for (DataSnapshot ds : dataSnapshotMain.getChildren()) {
                            db.child(AppConstants.donors())
                                    .child(ds.getValue(String.class))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Donor donor = dataSnapshot.getValue(Donor.class);
                                            contactedDonorsList.add(donor);

                                            if (contactedDonorsList.size() == dataSnapshotMain.getChildrenCount()) {
                                                loadingDialog.dismiss();
                                                showSelectedDonorsDialog(contactedDonorsList);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void showSelectedDonorsDialog(final ArrayList<Donor> contactedDonorsList) {

        final ArrayList<String> contactedDonors = new ArrayList<>();
        final ArrayList<Donor> selectedDonorsList = new ArrayList<>();

        for (Donor donor : contactedDonorsList){
            String item = donor.getName() + "\n" + donor.getLocation() + " - " + donor.getPincode();
            contactedDonors.add(item);
        }

        final MaterialDialog.Builder contactedDonorsDialog =
                new MaterialDialog.Builder(context);

        contactedDonorsDialog.title(R.string.contacted_donors_title)
                .contentColor(Color.BLACK)
                .items(contactedDonors)
                .positiveText(R.string.call)
                .negativeText(R.string.message)
                .neutralText(R.string.close)
                .alwaysCallMultiChoiceCallback()
                .itemsCallbackMultiChoice(null,
                        new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog,
                                                       Integer[] which,
                                                       CharSequence[] text) {
                                for (int i : which){
                                    selectedDonorsList.add(contactedDonorsList.get(i));
                                }

                                if (which.length > 1) {
                                    dialog.getActionButton(DialogAction.POSITIVE)
                                            .setEnabled(false);
                                } else {
                                    dialog.getActionButton(DialogAction.POSITIVE)
                                            .setEnabled(true);
                                }
                                return true;
                            }
                        })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        makeCall(selectedDonorsList.get(0).getPhoneNumber(),
                                selectedDonorsList.get(0).getName());
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sendMessage(selectedDonorsList  );
                    }
                })
                .show();
    }

    public void makeCall(final String number, final String name) {

        new MaterialDialog.Builder(context)
                .title(R.string.confirm)
                .content(getString(R.string.call) + " " + name + "\n" + number)
                .autoDismiss(true)
                .positiveText(R.string.call)
                .negativeText(R.string.cancel)
                .contentColor(Color.BLACK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                            Dexter.withActivity((AdminDonneActivity)context).withPermission(Manifest.permission.CALL_PHONE).
                                    withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + number));
                                            context.startActivity(callIntent);
                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {
                                            Toast.makeText(context,
                                                    R.string.call_permission_denied_message, Toast.LENGTH_SHORT).show();
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
                })
                .show();

    }
    public void sendMessage(ArrayList<Donor> selectedDonorsList){

    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter actionIntentFilter = new IntentFilter(AppConstants.donneAction());
        BroadcastReceiver actionInfoReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getStringExtra(AppConstants.action());
                Donee donee = Parcels.unwrap(intent.getParcelableExtra(AppConstants.donee()));

                if (action.equals(AppConstants.donneActionSelectedDonorsButton())) {
                    getSelectedDonors(donee.getDoneeId());
                }
            }
        };

        LocalBroadcastManager
                .getInstance(context)
                .registerReceiver(actionInfoReciever, actionIntentFilter);
    }
}