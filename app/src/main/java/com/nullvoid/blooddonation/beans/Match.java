package com.nullvoid.blooddonation.beans;

import java.util.ArrayList;

/**
 * Created by sanath on 28/06/17.
 */

public class Match {
    private Donee donee;
    private String matchId, matchedDate, matchedTime, completedDate, matchCategory;
    private ArrayList<Donor> contactedDonors, helpedDonors;
    private boolean completed;

    public Donee getDonee() {
        return donee;
    }

    public String getMatchCategory() {
        return matchCategory;
    }

    public void setMatchCategory(String matchCategory) {
        this.matchCategory = matchCategory;
    }

    public void setDonee(Donee donee) {
        this.donee = donee;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
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

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public ArrayList<Donor> getContactedDonors() {
        return contactedDonors;
    }

    public void setContactedDonors(ArrayList<Donor> contactedDonors) {
        this.contactedDonors = contactedDonors;
    }

    public ArrayList<Donor> getHelpedDonors() {
        return helpedDonors;
    }

    public void setHelpedDonors(ArrayList<Donor> helpedDonors) {
        this.helpedDonors = helpedDonors;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}