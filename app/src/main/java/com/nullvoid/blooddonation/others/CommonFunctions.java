package com.nullvoid.blooddonation.others;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nullvoid.blooddonation.R;
import com.nullvoid.blooddonation.beans.Donor;

import static android.content.Context.MODE_PRIVATE;
import static com.nullvoid.blooddonation.others.Constants.currentUser;

/**
 * Created by sanath on 15/07/17.
 */

public class CommonFunctions {

    public static void call(final Context context, final String number) {

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            Dexter.withActivity((Activity) context).withPermission(Manifest.permission.CALL_PHONE).
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

    public static void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showSnackBar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    public static String toCamelCase(final String init) {
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public static String generateOTP() {
        int randomNum = (int) ((Math.random() * (9999 + 1 - 1000)) + 1000);
        return String.valueOf(randomNum);
    }

    public static void signInUser(Context context, Donor donor) {
        String usersAsString = new Gson().toJson(donor);
        SharedPreferences.Editor editor = context
                .getSharedPreferences(Constants.currentUser, MODE_PRIVATE).edit();
        editor.putString(Constants.currentUser, usersAsString).apply();
    }

    public static Donor signOutUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.currentUser, MODE_PRIVATE);
        Donor currentUser = new Gson().fromJson(sp.getString(Constants.currentUser, null), Donor.class);
        sp.edit().putString(Constants.currentUser, null).apply();
        return currentUser;
    }

    public static Donor getCurrentUser(Context context) {

        SharedPreferences mPrefs = context.getSharedPreferences(currentUser, MODE_PRIVATE);
        String userAsString = mPrefs.getString(Constants.currentUser, null);
        Donor user = new Gson().fromJson(userAsString, Donor.class);

        return user;
    }

}
