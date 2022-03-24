package com.perfex.medicineremainder.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Appointment implements Serializable {
    @PrimaryKey
    private long id;
    private String doctorName;
    private String hospitalName;
    private String purposeOfAppointment;
    private String addressOfHospital;
    private String phoneNumber;
    private String date;
    private String time;

    public Appointment(){

    }

    @NonNull
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", doctorName='" + doctorName + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", purposeOfAppointment='" + purposeOfAppointment + '\'' +
                ", addressOfHospital='" + addressOfHospital + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public Appointment(long id, String hospitalName, String purposeOfAppointment, String addressOfHospital, String phoneNumber, String date, String time) {
        this.id = id;
        this.hospitalName = hospitalName;
        this.purposeOfAppointment = purposeOfAppointment;
        this.addressOfHospital = addressOfHospital;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.time = time;
    }

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

    public String getPurposeOfAppointment() {
        return purposeOfAppointment;
    }

    public void setPurposeOfAppointment(String purposeOfAppointment) {
        this.purposeOfAppointment = purposeOfAppointment;
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
