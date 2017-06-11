package com.nullvoid.blooddonation.beans;

/**
 * Created by sanath on 10/06/17.
 */
public class Donor {

    private String name, gender, bloodGroup,
            dateOfBirth, donatedDate, age, phoneNumber, email, address, location, pincode;

    public Donor() {
    }

    public Donor(String name, String gender, String bloodGroup, String dateOfBirth,
                 String donatedDate, String age, String phoneNumber, String email, String address,
                 String location, String pincode) {
        this.name = name;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.dateOfBirth = dateOfBirth;
        this.donatedDate = donatedDate;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.location = location;
        this.pincode = pincode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDonatedDate() {
        return donatedDate;
    }

    public void setDonatedDate(String donatedDate) {
        this.donatedDate = donatedDate;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    @Override
    public String toString() {
        return "DonorDetails{" +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", donatedDate='" + donatedDate + '\'' +
                ", age='" + age + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                ", pincode='" + pincode + '\'' +
                '}';
    }
}