package com.perfex.medicineremainder.ui.event.fragments.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.model.Appointment;
import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.ui.event.AppointmentDetailActivity;
import com.perfex.medicineremainder.ui.event.CheckUpDetailActivity;

import java.util.ArrayList;

public class CheckUpAdapter  extends RecyclerView.Adapter<CheckUpAdapter.ViewHolder> {

    private ArrayList<CheckUp> localDataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView doctorName;
        private TextView hospitalName;
        private TextView purposeOfAppointment;
        private TextView dateAndTime;
        private View root;

        public ViewHolder(View view) {
            super(view);
            doctorName=view.findViewById(R.id.doctorNameTv);
            hospitalName=view.findViewById(R.id.medicineDosageTv);
            purposeOfAppointment=view.findViewById(R.id.purposeCheckTv);
            dateAndTime=view.findViewById(R.id.medicineTimeTv);
            root=view.findViewById(R.id.root);

        }

    }
    public CheckUpAdapter(ArrayList<CheckUp> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.check_up_single_item, viewGroup, false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.doctorName.setText(String.format("Doctor Name : %s", localDataSet.get(position).getDoctorName()));
        viewHolder.hospitalName.setText(String.format("Hospital Name : %s", localDataSet.get(position).getHospitalName()));
        viewHolder.purposeOfAppointment.setText(String.format("Purpose of CheckUp : %s", localDataSet.get(position).getPurposeOfCheckUp()));
        viewHolder.dateAndTime.setText(String.format("Date And time : %s %s", localDataSet.get(position).getDate(), localDataSet.get(position).getTime()));
        viewHolder.root.setOnClickListener(v -> {
            Intent intent = new Intent(context, CheckUpDetailActivity.class);
            intent.putExtra("checkup",localDataSet.get(viewHolder.getAdapterPosition()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
