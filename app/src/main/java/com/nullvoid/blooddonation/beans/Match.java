package com.nullvoid.blooddonation.beans;

import java.util.ArrayList;

/**
 * Created by sanath on 28/06/17.
 */

public class Match {
    private String matchId, doneeId, matchedDate, matchedTime;
    private ArrayList<String> contactedDonors, helpedDonors;
    private boolean completed;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getDoneeId() {
        return doneeId;
    }

    public void setDoneeId(String doneeId) {
        this.doneeId = doneeId;
    }

    public String getMatchedDate() {
        return matchedDate;
    }

    public void setMatchedDate(String matchedDate) {
        this.matchedDate = matchedDate;
    }

    public String getMatchedTime() {
        return matchedTime;
    }

    public void setMatchedTime(String matchedTime) {
        this.matchedTime = matchedTime;
    }

    public ArrayList<String> getContactedDonors() {
        return contactedDonors;
    }

    public void setContactedDonors(ArrayList<String> contactedDonors) {
        this.contactedDonors = contactedDonors;
    }

    public ArrayList<String> getHelpedDonors() {
        return helpedDonors;
    }

    public void setHelpedDonors(ArrayList<String> helpedDonors) {
        this.helpedDonors = helpedDonors;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}