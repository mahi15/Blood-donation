package com.nullvoid.blooddonation.beans;

import org.parceler.Parcel;

/**
 * Created by sanath on 11/06/17.
 */

@Parcel
public class Donee {
    private String doneeId, requesterName, requesterPhoneNumber, requiredBloodGroup, requiredAmount, requiredDate,
            patientAttendantName, patientAttendantNumber, patientName, patientAreaofResidence, patientID,
            hospitalName, hospitalNumber, hospitalAddress, hospitalPin, requestedDate, requestedTime, status;

    public Donee() {
    }

    public String getDoneeId() {
        return doneeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDoneeId(String doneeId) {
        this.doneeId = doneeId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterPhoneNumber() {
        return requesterPhoneNumber;
    }

    public void setRequesterPhoneNumber(String requesterPhoneNumber) {
        this.requesterPhoneNumber = requesterPhoneNumber;
    }

    public String getRequiredBloodGroup() {
        return requiredBloodGroup;
    }

    public void setRequiredBloodGroup(String requiredBloodGroup) {
        this.requiredBloodGroup = requiredBloodGroup;
    }

    public String getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(String requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public String getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(String requiredDate) {
        this.requiredDate = requiredDate;
    }

    public String getPatientAttendantName() {
        return patientAttendantName;
    }

    public void setPatientAttendantName(String patientAttendantName) {
        this.patientAttendantName = patientAttendantName;
    }

    public String getPatientAttendantNumber() {
        return patientAttendantNumber;
    }

    public void setPatientAttendantNumber(String patientAttendantNumber) {
        this.patientAttendantNumber = patientAttendantNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAreaofResidence() {
        return patientAreaofResidence;
    }

    public void setPatientAreaofResidence(String patientAreaofResidence) {
        this.patientAreaofResidence = patientAreaofResidence;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalNumber() {
        return hospitalNumber;
    }

    public void setHospitalNumber(String hospitalNumber) {
        this.hospitalNumber = hospitalNumber;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getHospitalPin() {
        return hospitalPin;
    }

    public void setHospitalPin(String hospitalPin) {
        this.hospitalPin = hospitalPin;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(String requestedTime) {
        this.requestedTime = requestedTime;
    }
}
