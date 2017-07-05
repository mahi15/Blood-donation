package com.nullvoid.blooddonation.others;

/**
 * Created by sanath on 27/06/17.
 */

public class AppConstants {

    public static final String select(){
        return "SELECT";
    }
    public static final String remove(){
        return "REMOVE";
    }
    public static final String status(){
        return "status";
    }
    public static final String currentUser() {
        return "CURRENT_USER";
    }
    public static final String notProvided(){return "Not Provided";}

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

}
