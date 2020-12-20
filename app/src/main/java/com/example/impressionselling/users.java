package com.example.impressionselling;

public class users {
    private String name, phone,whatsapp, party ,address, city, pincode, emailID, type, gst, classP, payment, creditLimit, creditDay;

    users() {
    }

    public users(String name, String phone, String whatsapp, String party, String address, String city, String pincode, String emailID, String type, String gst, String classP, String payment, String creditLimit, String creditDay) {
        this.name = name;
        this.phone = phone;
        this.whatsapp = whatsapp;
        this.party = party;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
        this.emailID = emailID;
        this.type = type;
        this.gst = gst;
        this.classP = classP;
        this.payment = payment;
        this.creditLimit = creditLimit;
        this.creditDay = creditDay;
    }

    public String getClassP() {
        return classP;
    }

    public void setClassP(String classP) {
        this.classP = classP;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCreditDay() {
        return creditDay;
    }

    public void setCreditDay(String creditDay) {
        this.creditDay = creditDay;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
