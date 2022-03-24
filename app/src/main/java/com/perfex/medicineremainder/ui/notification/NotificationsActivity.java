package com.perfex.medicineremainder.ui.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.perfex.medicineremainder.App;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.model.Notification;
import com.perfex.medicineremainder.ui.notification.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private AppDatabase appDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        appDatabase = AppDatabase.getDatabase(this);
        Thread thread = new Thread(() -> {
            List<Notification> list = appDatabase.notificationDao().getAll();
            ArrayList<Notification> notifications = new ArrayList<>(list);
            NotificationAdapter notificationAdapter = new NotificationAdapter(notifications,this);
            RecyclerView recycler = findViewById(R.id.recyclerView);
            Log.d("TAG", "onCreate: "+list);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.setAdapter(notificationAdapter);
        });
        thread.start();
    }
}