package com.nullvoid.blooddonation.beans;

/**
 * Created by sanath on 11/06/17.
 */

public class Donee {
    private String doneeId, requestedBy, name,phoneNumber, bloodGroup, reqAmount, reqDate, patientAttendantName,
            patientAttendantNumber, patientName, patientAreaofResidence, patientID, hospitalName,
            hospitalNumber, hospitalAddress, hospitalPin;

    public Donee() {
    }

    public String getDoneeId() {
        return doneeId;
    }

    public void setDoneeId(String doneeId) {
        this.doneeId = doneeId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReqAmount() {
        return reqAmount;
    }

    public void setReqAmount(String reqAmount) {
        this.reqAmount = reqAmount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getPatientAttendantName() {
        return patientAttendantName;
    }

    public String getPatientAreaofResidence() {
        return patientAreaofResidence;
    }

    public void setPatientAreaofResidence(String patientAreaofResidence) {
        this.patientAreaofResidence = patientAreaofResidence;
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
}
