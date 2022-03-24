package com.perfex.medicineremainder.ui.notification.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.model.Notification;
import com.perfex.medicineremainder.ui.event.AppointmentDetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<Notification> localDataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView description;
        private TextView purposeOfAppointment;
        private TextView dateAndTime;

        public ViewHolder(View view) {
            super(view);
            title =view.findViewById(R.id.title);
            description =view.findViewById(R.id.description);
            dateAndTime=view.findViewById(R.id.time);

        }

    }
    public NotificationAdapter(ArrayList<Notification> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notification_single_item, viewGroup, false);

        return new NotificationAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.title.setText(String.format("%s", localDataSet.get(position).getTitle()));
        viewHolder.description.setText(String.format("%s", localDataSet.get(position).getDescription()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
        viewHolder.dateAndTime.setText(simpleDateFormat.format(new Date(localDataSet.get(position).getTimeStamps())));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}