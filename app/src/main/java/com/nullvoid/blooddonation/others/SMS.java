package com.nullvoid.blooddonation.others;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nullvoid.blooddonation.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by sanath on 31/07/17.
 */

public class SMS {

    public static class sendOtp extends AsyncTask<String, Void, String> {
        Context context;
        MaterialDialog progressDialog;

        public sendOtp(Context context) {
            this.context = context;
        }

        public sendOtp() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!CommonFunctions.isNetworkAvailable(context)) {
                cancel(true);
                CommonFunctions.showToast(context, context.getString(R.string.no_internet_message));
            }

            progressDialog = new MaterialDialog.Builder(context)
                    .title(R.string.loading)
                    .content(R.string.please_wait_message)
                    .contentColor(Color.BLACK)
                    .canceledOnTouchOutside(false)
                    .progress(true, -1)
                    .show();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;

            String authkey = Constants.smsApiKey;
            String mobiles = params[0];
            String senderId = Constants.smsSenderId;
            String message = "Your OTP is " + params[1];

            //transactional route is 4
            String route = "4";

            URLConnection myURLConnection = null;
            URL myURL = null;
            BufferedReader reader = null;

            String encoded_message = URLEncoder.encode(message);

            String mainUrl = "http://api.msg91.com/api/sendhttp.php?";

            StringBuilder sbPostData = new StringBuilder(mainUrl);
            sbPostData.append("authkey=" + authkey);
            sbPostData.append("&mobiles=" + mobiles);
            sbPostData.append("&message=" + encoded_message);
            sbPostData.append("&route=" + route);
            sbPostData.append("&sender=" + senderId);

            mainUrl = sbPostData.toString();
            try {
                //prepare connection
                myURL = new URL(mainUrl);
                myURLConnection = myURL.openConnection();
                myURLConnection.connect();
                reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                //reading response
                String r;
                while ((r = reader.readLine()) != null){
                    response = r;
                }
                Log.d("RESPONSE", response);

                //finally close connection
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.length() != 24) {
                return "f";
            } else {
                return "s";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public static class sendMessage extends AsyncTask<String, Void, String> {
        Context context;

        public sendMessage(Context context) {
            this.context = context;
        }

        public sendMessage() {
        }

        @Override
        protected String doInBackground(String... params) {

            String authkey = Constants.smsApiKey;
            String mobiles = params[0];
            String senderId = Constants.smsSenderId;
            String message = params[1];

            //transactional route is 4
            String route = "4";

            URLConnection myURLConnection = null;
            URL myURL = null;
            BufferedReader reader = null;

            String encoded_message = URLEncoder.encode(message);

            String mainUrl = "http://api.msg91.com/api/sendhttp.php?";

            StringBuilder sbPostData = new StringBuilder(mainUrl);
            sbPostData.append("authkey=" + authkey);
            sbPostData.append("&mobiles=" + mobiles);
            sbPostData.append("&message=" + encoded_message);
            sbPostData.append("&route=" + route);
            sbPostData.append("&sender=" + senderId);

            mainUrl = sbPostData.toString();
            try {
                //prepare connection
                myURL = new URL(mainUrl);
                myURLConnection = myURL.openConnection();
                myURLConnection.connect();
                reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                //reading response
                String response;
                while ((response = reader.readLine()) != null)
                    //print response
                    Log.d("RESPONSE", "" + response);

                //finally close connection
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}