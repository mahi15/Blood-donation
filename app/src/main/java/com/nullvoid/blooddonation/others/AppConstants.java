package com.nullvoid.blooddonation.others;

/**
 * Created by sanath on 27/06/17.
 */

public class AppConstants {

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
    public static final String donors(){
        return "donors";
    }
    public static final String matches(){
        return "matches";
    }
    public static final String donees(){
        return "donees";
    }

    //donor stuff
    public static final String phoneNumber(){
        return "phoneNumber";
    }
    public static final String registeredDate(){
        return "registeredDate";
    }
    public static final String donationCount(){
        return "donationCount";
    }
    public static final String isAvailable = "isAvailable";

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

}
