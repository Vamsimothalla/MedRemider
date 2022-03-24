package com.perfex.medicineremainder.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.perfex.medicineremainder.ReminderType;

@Entity
public class Notification {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String title;
    private String description;
    private String type;
    private Long timeStamps;

    @Exclude
    @Ignore
    private ReminderType reminderType;

    private String typeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.reminderType=ReminderType.valueOf(type);
    }

    public String getTypeId() {
        return typeId;
    }

    public Long getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(Long timeStamps) {
        this.timeStamps = timeStamps;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public ReminderType getReminderType() {
        return reminderType;
    }

    public void setReminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
        this.type= reminderType.name();
    }
}
