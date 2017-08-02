package com.nullvoid.blooddonation.beans;

/**
 * Created by sanath on 01/08/17.
 */

public class DonateToday {
    private String availableLocation, reasonForDonation, donorId, date;

    public DonateToday() {
    }

    public DonateToday(String availableLocation, String reasonForDonation, String donorId, String date) {
        this.availableLocation = availableLocation;
        this.reasonForDonation = reasonForDonation;
        this.donorId = donorId;
        this.date = date;
    }

    public String getAvailableLocation() {
        return availableLocation;
    }

    public void setAvailableLocation(String availableLocation) {
        this.availableLocation = availableLocation;
    }

    public String getReasonForDonation() {
        return reasonForDonation;
    }

    public void setReasonForDonation(String reasonForDonation) {
        this.reasonForDonation = reasonForDonation;
    }

    public String getDonorId() {
        return donorId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
