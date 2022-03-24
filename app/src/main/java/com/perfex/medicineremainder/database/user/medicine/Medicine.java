package com.perfex.medicineremainder.database.user.medicine;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.perfex.medicineremainder.database.user.Converter;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class Medicine implements Serializable {
        @PrimaryKey(autoGenerate = true)
        @NonNull
        private int id;
        private int tagDaily;
        private String medicineName;
        private String purposeOfMedicine;
        private String dosage;
        private String imagePath;
        private String weekDays;
        private String timings;
        private String imageUrl;
        @Ignore
        @Exclude
        private ArrayList<String> weekDaysArrayList=new ArrayList<>();

        @Ignore
        @Exclude
        private ArrayList<String> timingsArrayList;

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public int getTagDaily() {
                return tagDaily;
        }

        public void setTagDaily(int tagDaily) {
                this.tagDaily = tagDaily;
        }

        public String getMedicineName() {
                return medicineName;
        }

        public void setMedicineName(String medicineName) {
                this.medicineName = medicineName;
        }

        public String getPurposeOfMedicine() {
                return purposeOfMedicine;
        }

        public void setPurposeOfMedicine(String purposeOfMedicine) {
                this.purposeOfMedicine = purposeOfMedicine;
        }

        public String getDosage() {
                return dosage;
        }

        public void setDosage(String dosage) {
                this.dosage = dosage;
        }

        public String getImagePath() {
                return imagePath;
        }

        public void setImagePath(String imagePath) {
                this.imagePath = imagePath;
        }

        public String getWeekDays() {
                return weekDays;
        }

        public void setWeekDays(String weekDays) {
                this.weekDays = weekDays;
                this.weekDaysArrayList=Converter.fromString(weekDays);
        }

        public String getTimings() {
                return timings;
        }

        public void setTimings(String timings) {
                this.timings = timings;
                this.timingsArrayList=Converter.fromString(timings);
        }

        @Ignore
        @Exclude
        public ArrayList<String> getWeekDaysArrayList() {
                return weekDaysArrayList;
        }
        @Ignore
        @Exclude
        public void setWeekDaysArrayList(ArrayList<String> weekDaysArrayList) {
                this.weekDaysArrayList = weekDaysArrayList;
                this.weekDays=Converter.fromArrayList(weekDaysArrayList);
        }
        @Ignore
        @Exclude
        public ArrayList<String> getTimingsArrayList() {
                return timingsArrayList;
        }
        @Ignore
        @Exclude
        public void setTimingsArrayList(ArrayList<String> timingsArrayList) {
                this.timingsArrayList = timingsArrayList;
                this.timings= Converter.fromArrayList(timingsArrayList);
        }

        public String getImageUrl() {
                return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
        }
}
