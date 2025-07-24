package com.windriver.pcgate.Databases;

public class UserHelperClass {
    String fullName, phoneNumber,username,email,date,gender,Occupation;

    public UserHelperClass(String fullName, String phoneNumber, String username, String email, String date, String gender, String Occupation) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.email = email;
        this.date = date;
        this.gender = gender;
        this.Occupation = Occupation;
    }

    public UserHelperClass(){}

    public String getOccupation() {
        return Occupation;
    }

    public void setOccupation(String Occupation) {
        this.Occupation = Occupation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
