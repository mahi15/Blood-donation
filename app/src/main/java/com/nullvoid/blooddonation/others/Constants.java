package com.nullvoid.blooddonation.others;

/**
 * Created by sanath on 27/06/17.
 */

public class Constants {

    public static final String smsApiKey = "167663A2Dyfg7UZI597df63f";

    public static final String select = "SELECT";
    public static final String remove = "REMOVE";
    public static final String action = "ACTION";
    public static final String status = "status";
    public static final String currentUser = "CURRENT_USER";
    public static final String notProvided =  "Not Provided";
    public static final String selectionChange = "SELECTION_CHANGE";

    //donne action buttons stuff
    public static final String donneAction(){
        return "DONNE_ACTION";
    }
    public static final String donneActionSelectedDonorsButton(){
        return "SELECTED_DONORS_BUTTON";
    }
    public static final String donneActionMarkCompletedButton(){
        return "MARK_COMPLETED_BUTTON";
    }

    //database node names
    public static final String donors = "donors";
    public static final String matches = "matches";
    public static final String donees = "donees";
    public static final String completedDate = "completedDate";
    public static final String donatetoday = "donatetoday";
    public static final String willingToDonateToday = "willingToDonateToday";

    //donor stuff
    public static final String phoneNumber = "phoneNumber";
    public static final String registeredDate(){
        return "registeredDate";
    }
    public static final String donationCount(){
        return "donationCount";
    }
    public static final String isAvailable = "isAvailable";
    public static final String donor = "donor";

    //donee stuff
    public static final String donee(){
        return "DONEE";
    }   //for sending through intents
    public static final String statusPending(){
        return "PENDING";
    }               //donation status
    public static final String statusNotComplete(){
        return "NOT_COMPLETE";
    }
    public static final String statusComplete(){
        return "COMPLETE";
    }
    public static final String  contactedDonors(){
        return "contactedDonors";
    }
    public static final String requestedDate(){
        return "requestedDate";
    }

    public static final String shareAppExtra = "Download RSS HSS Blood Donors Bureau android application from PlayStore now!\n" +
            " https://play.google.com/store/apps/details?id=blood_donation";
    public static final String shareAppBody = "RSS HSS Blood Donors Bureau android application is now available";

    //SMS
    public static final String smsSenderId = "RSSHSS";
    public static final String smsOtpUrl = "http://api.msg91.com/api/sendotp.php?";
}
