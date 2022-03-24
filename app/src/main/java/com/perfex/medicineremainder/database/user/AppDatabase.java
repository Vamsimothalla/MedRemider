package com.perfex.medicineremainder.database.user;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.perfex.medicineremainder.database.event.AppointmentDao;
import com.perfex.medicineremainder.database.event.CheckUpDao;
import com.perfex.medicineremainder.database.event.NoteDao;
import com.perfex.medicineremainder.database.event.NotificationDao;
import com.perfex.medicineremainder.database.event.RefillDao;
import com.perfex.medicineremainder.database.user.medicine.Medicine;
import com.perfex.medicineremainder.database.user.medicine.MedicineDao;
import com.perfex.medicineremainder.model.Appointment;
import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.model.Note;
import com.perfex.medicineremainder.model.Notification;
import com.perfex.medicineremainder.model.Refill;


@Database(entities = { User.class, Medicine.class, Appointment.class, CheckUp.class, Refill.class, Note.class, Notification.class},version = 4)
@TypeConverters(Converter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public abstract UserDao userDao();
    public abstract MedicineDao medicineDao();
    public abstract AppointmentDao appointmentDao();
    public abstract CheckUpDao checkUpDao();
    public abstract RefillDao refillDao();
    public abstract NoteDao noteDao();
    public abstract NotificationDao notificationDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,"MedicineReminder")
            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }

}
