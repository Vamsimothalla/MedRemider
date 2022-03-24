package com.perfex.medicineremainder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.database.user.medicine.Medicine;
import com.perfex.medicineremainder.model.Appointment;
import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.model.Refill;
import com.perfex.medicineremainder.ui.ReceiverActivity;
import com.perfex.medicineremainder.ui.add.AddActivity;
import com.perfex.medicineremainder.ui.event.AppointmentDetailActivity;
import com.perfex.medicineremainder.ui.event.CheckUpDetailActivity;
import com.perfex.medicineremainder.ui.home.MedicineReminderActivity;
import com.perfex.medicineremainder.ui.settings.Preferences;
import com.perfex.medicineremainder.utils.Constants;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;
    Preferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        AppDatabase appDatabase=AppDatabase.getDatabase(context);
        preferences = Preferences.getInstance(context);
        int mReceivedID = intent.getIntExtra("id",0);
        String type =  intent.getStringExtra("r_type");
        Log.d("TAG", "onReceive: "+type);
        Log.d("TAG", "onReceive: "+mReceivedID);
        ReminderType reminderType= ReminderType.valueOf(ReminderType.class,type) ;
        Log.d("TAG", "onReceive: "+reminderType);
        com.perfex.medicineremainder.model.Notification notification = new com.perfex.medicineremainder.model.Notification();
        switch (reminderType){
            case APPOINTMENT_REMINDER:
                Intent editIntent = new Intent(context, AppointmentDetailActivity.class);
                editIntent.putExtra(MedicineReminderActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
                Thread thread=new Thread(() -> {
                    Appointment appointment = appDatabase.appointmentDao().getById((long) mReceivedID);
                    String str = "Appointment for "+appointment.getPurposeOfAppointment()+ " at "+ appointment.getDate()+" "+appointment.getTime();
                    if (preferences.isNotificationEnabled()) notificationBuilder(context,mReceivedID,editIntent,"Appointment Reminder",str,reminderType,preferences.isPriorityNotificationEnabled());
                    startRingActivity(context,"Appointment Reminder","You have an Appointment", appointment.getTime(), appointment.getAddressOfHospital());
                    notification.setReminderType(ReminderType.CHECK_UP_REMINDER);
                    notification.setDescription(str);
                    notification.setTimeStamps(System.currentTimeMillis());
                    notification.setTitle("Appointment Reminder");
                    appDatabase.notificationDao().insert(notification);
                });
                thread.start();
                break;
            case REFILL_REMINDER:
                Intent refillInt = new Intent(context, CheckUpDetailActivity.class);
                refillInt.putExtra(MedicineReminderActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
                Thread t0=new Thread(() -> {
                    Refill checkUp = appDatabase.refillDao().getById((long) mReceivedID);
                    String str ="Its time for refill";
                    notificationBuilder(context,mReceivedID,refillInt,"Refill Reminder",str,reminderType, preferences.isPriorityNotificationEnabled());
                    notification.setReminderType(ReminderType.CHECK_UP_REMINDER);
                    notification.setDescription(str);
                    notification.setTimeStamps(System.currentTimeMillis());
                    notification.setTitle("Refill Reminder");
                    appDatabase.notificationDao().insert(notification);
                        startRingActivity(context,"Refill Reminder","its time for refill your medicines "+checkUp.getMedicineType(), checkUp.getEndDate(), null);
                });
                t0.start();
                break;
            case CHECK_UP_REMINDER:
                Intent checkUpInt = new Intent(context, CheckUpDetailActivity.class);
                checkUpInt.putExtra(MedicineReminderActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
                Thread t2=new Thread(() -> {
                    CheckUp checkUp = appDatabase.checkUpDao().getById((long) mReceivedID);
                    String str = "Check up for "+checkUp.getPurposeOfCheckUp()+ " at "+ checkUp.getDate()+" "+checkUp.getTime();
                    notificationBuilder(context,mReceivedID,checkUpInt,"Check Up Reminder",str,reminderType, preferences.isPriorityNotificationEnabled());
                    notification.setReminderType(ReminderType.CHECK_UP_REMINDER);
                    notification.setDescription(str);
                    notification.setTimeStamps(System.currentTimeMillis());
                    notification.setTitle("Check Up Reminder");
                    appDatabase.notificationDao().insert(notification);
                    startRingActivity(context,"Check Up Reminder","You have a Check Up",checkUp.getTime(), checkUp.getAddressOfHospital());

                });
                t2.start();
                break;
            case MEDICINE_REMINDER:
                Intent medicineInt = new Intent(context, AppointmentDetailActivity.class);
                medicineInt.putExtra(MedicineReminderActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
                Thread thread2=new Thread(() -> {
                    Medicine medicine1 = appDatabase.medicineDao().loadMedById(mReceivedID);
                    String str = "Take medicine for "+medicine1.getPurposeOfMedicine();
                    notificationBuilder(context,mReceivedID,medicineInt,"Appointment Reminder",str,reminderType, preferences.isPriorityNotificationEnabled());
                    notification.setReminderType(ReminderType.MEDICINE_REMINDER);
                    notification.setDescription(str);
                    notification.setTimeStamps(System.currentTimeMillis());
                    notification.setTitle("Medicine Reminder");
                    appDatabase.notificationDao().insert(notification);
                });
                thread2.start();
                break;
        }
    }

    private void startRingActivity(Context context,String title , String description,String time,String location) {
        Intent ring=new Intent(context, AddActivity.class);
        ring.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ring.putExtra(Constants.TITLE,title);
        ring.putExtra(Constants.DESCRIPTION,description);
        ring.putExtra(Constants.TIME,time);
        ring.putExtra(Constants.LOCATION,location);
        context.startActivity(ring);
    }

    public void setAlarm(Context context, Calendar calendar, int ID,ReminderType type) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", ID);
        intent.putExtra("r_type",type.name());
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);
        ComponentName receiver = new ComponentName(context, AlarmBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void setRepeatAlarm(@NonNull Context context, @NonNull Calendar calendar, int ID, long RepeatTime) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", ID);
        intent.putExtra("r_type",ReminderType.MEDICINE_REMINDER);
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime, RepeatTime , mPendingIntent);

        ComponentName receiver = new ComponentName(context, AlarmBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    public void setRepeatAlarm(@NonNull Context context, @NonNull Calendar calendar, int ID, long RepeatTime,int unq) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", ID);
        intent.putExtra("r_type",ReminderType.MEDICINE_REMINDER);
        mPendingIntent = PendingIntent.getBroadcast(context, unq, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime, RepeatTime , mPendingIntent);
        ComponentName receiver = new ComponentName(context, AlarmBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);
        ComponentName receiver = new ComponentName(context, AlarmBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
    private void notificationBuilder(Context context, int mReceivedID, Intent intent, String title, String content, ReminderType type, boolean notificationEnabled){
        PendingIntent mClick = PendingIntent.getActivity(context, mReceivedID, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        if(notificationEnabled) mBuilder.setPriority(Notification.PRIORITY_MAX);
        else mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = type.name();
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    type.name().replace("_"," "),
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        mNotificationManager.notify(mReceivedID, mBuilder.build());
    }
}
