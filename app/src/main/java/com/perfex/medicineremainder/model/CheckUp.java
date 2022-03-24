package com.perfex.medicineremainder.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class CheckUp implements Serializable {
    @PrimaryKey
    private long id;
    private String doctorName;
    private String hospitalName;
    private String purposeOfCheckUp;
    private String addressOfHospital;
    private String phoneNumber;
    private String date;
    private String time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getPurposeOfCheckUp() {
        return purposeOfCheckUp;
    }

    public void setPurposeOfCheckUp(String purposeOfCheckUp) {
        this.purposeOfCheckUp = purposeOfCheckUp;
    }

    public String getAddressOfHospital() {
        return addressOfHospital;
    }

    public void setAddressOfHospital(String addressOfHospital) {
        this.addressOfHospital = addressOfHospital;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
