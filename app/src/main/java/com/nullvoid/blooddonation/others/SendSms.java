package com.nullvoid.blooddonation.others;

/**
 * Created by sanath on 18/07/17.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SendSms {

    public String sendSms(String number, String message) {
        try {
            // Construct data
            String apiKey = "apikey=" + Constants.smsApiKey;
            message = "&message=" + message;
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + number;
            String test = "&test=1";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data = apiKey + numbers + message + sender + test;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();

            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
    }

    public ArrayList<String> sendBatchSms(ArrayList<String> numbers, String message) {
        ArrayList<String> responses = new ArrayList<>();

        for (String num : numbers) {
            try {
                // Construct data
                String apiKey = "apikey=" + Constants.smsApiKey;
                message = "&message=" + message;
                String sender = "&sender=" + "TXTLCL";
                String number = "&numbers=" + num;
                String test = "&test=1";

                // Send data
                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
                String data = apiKey + numbers + message + sender + test;
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                conn.getOutputStream().write(data.getBytes("UTF-8"));
                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    stringBuffer.append(line);
                }
                rd.close();

                responses.add(stringBuffer.toString());
            } catch (Exception e) {
                responses.add("Error "+e);
            }
        }
        return responses;
    }
}
