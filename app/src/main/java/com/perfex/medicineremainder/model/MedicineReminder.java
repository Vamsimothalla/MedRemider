package com.perfex.medicineremainder.model;

public class MedicineReminder {
    private int id;
    private String MedicineName;
    private String MedicineDosage;
    private String weekDays;
    private String dayTimings;
    private String medicineImage;

    public MedicineReminder(){

    }
    public MedicineReminder(int id, String medicineName, String medicineDosage, String weekDays, String dayTimings, String medicineImage) {
        this.id = id;
        MedicineName = medicineName;
        MedicineDosage = medicineDosage;
        this.weekDays = weekDays;
        this.dayTimings = dayTimings;
        this.medicineImage = medicineImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicineName() {
        return MedicineName;
    }

    public void setMedicineName(String medicineName) {
        MedicineName = medicineName;
    }

    public String getMedicineDosage() {
        return MedicineDosage;
    }

    public void setMedicineDosage(String medicineDosage) {
        MedicineDosage = medicineDosage;
    }

    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }

    public String getDayTimings() {
        return dayTimings;
    }

    public void setDayTimings(String dayTimings) {
        this.dayTimings = dayTimings;
    }

    public String getMedicineImage() {
        return medicineImage;
    }

    public void setMedicineImage(String medicineImage) {
        this.medicineImage = medicineImage;
    }
}
