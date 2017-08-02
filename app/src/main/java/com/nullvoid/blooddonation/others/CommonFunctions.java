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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import static android.R.attr.max;
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

    public static String sendSms(ArrayList<String> numbers, String message) {

        String response = "";

        String mobiles = "";
        String authkey = Constants.smsApiKey;
        String senderId = "102234";
        String route="default";
        for (int i = 1; i <= numbers.size(); i++) {
            mobiles += numbers.get(i-1);
            if (!(i == numbers.size())) {
                mobiles += ",";
            }
        }

        URLConnection myURLConnection=null;
        URL myURL=null;
        BufferedReader reader=null;

        String encoded_message= URLEncoder.encode(message);
        String mainUrl="http://api.msg91.com/api/sendhttp.php?";

        StringBuilder sbPostData= new StringBuilder(mainUrl);
        sbPostData.append("authkey="+authkey);
        sbPostData.append("&mobiles="+mobiles);
        sbPostData.append("&message="+encoded_message);
        sbPostData.append("&route="+route);
        sbPostData.append("&sender="+senderId);

        //final string
        mainUrl = sbPostData.toString();
        try
        {
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

            //reading response
            while ((response = reader.readLine()) != null)
                //print response
                Log.d("RESPONSE", ""+response);

            //finally close connection
            reader.close();
        }
        catch (IOException e)
        {
            Log.e("SMS", "Error Sending SMS");
        }
        return response;
    }

    public static String sendOtp(String number) {
        int randomNum = (int) ((Math.random() * (9999 + 1 - 1000)) + 9999);
        String response = "";

        String otp = String.valueOf(randomNum);
        String encoded_message = "Your OTP is " + otp;
        String authkey = Constants.smsApiKey;
        String mainUrl="http://api.msg91.com/api/sendotp.php?";

        URLConnection myURLConnection=null;
        BufferedReader reader=null;


        StringBuilder sbPostData= new StringBuilder(mainUrl);
        sbPostData.append("authkey="+authkey);
        sbPostData.append("&mobiles="+number);
        sbPostData.append("&message="+encoded_message);
        sbPostData.append("&otp="+otp);
//        sbPostData.append("&sender="+senderId);

        try
        {
            //prepare connection
            URL myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

            //reading response
            while ((response = reader.readLine()) != null)
                //print response
                Log.d("RESPONSE", ""+response);

            //finally close connection
            reader.close();
        }
        catch (IOException e)
        {
            Log.e("SMS", "Error Sending SMS");
        }
        return otp;
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
