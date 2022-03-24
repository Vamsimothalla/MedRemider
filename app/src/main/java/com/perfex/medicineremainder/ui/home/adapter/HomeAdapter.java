package com.perfex.medicineremainder.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.perfex.medicineremainder.MainActivity;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.database.user.medicine.Medicine;
import com.perfex.medicineremainder.ui.home.MedicineReminderActivity;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private ArrayList<Medicine> localDataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView medicineType;
        private TextView purposeOfMedicine;
        private TextView medicineDosage;
        private TextView date;
        private TextView time;
        private View root;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            medicineType = view.findViewById(R.id.medicineNameTv);
            purposeOfMedicine = view.findViewById(R.id.medicineDosageTv);
            medicineDosage = view.findViewById(R.id.purposeDosageTv);
            date = view.findViewById(R.id.medicineWeekdaysTv);
            time = view.findViewById(R.id.medicineTimeTv);
            root = view.findViewById(R.id.root);
            imageView = view.findViewById(R.id.imageView);

        }

    }
    public HomeAdapter(ArrayList<Medicine> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.medicine_single_item, viewGroup, false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.medicineType.setText(String.format("Medicine Type: %s ",localDataSet.get(position).getMedicineName()));
        viewHolder.purposeOfMedicine.setText(String.format("Purpose of medicine: %s ",localDataSet.get(position).getPurposeOfMedicine()));
        viewHolder.medicineDosage.setText(String.format("Dosage: %s ",localDataSet.get(position).getDosage()));
        int i=0;
        StringBuilder stringBuilder=new StringBuilder();
        if (localDataSet.get(position).getWeekDaysArrayList().size()==0 || localDataSet.get(position).getWeekDaysArrayList().size()==7){
            stringBuilder.append("Every Day");
        }
        else {
            for (String str : localDataSet.get(position).getWeekDaysArrayList()) {
                if (i != 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(str.substring(0, 3));
                i++;
            }
        }
        viewHolder.date.setText(String.format("Week Days: %s ",stringBuilder.toString()));
        viewHolder.root.setOnClickListener(v -> {
            Intent intent = new Intent(context, MedicineReminderActivity.class);
            intent.putExtra("reminder",localDataSet.get(viewHolder.getAdapterPosition()));
            context.startActivity(intent);
        });
        viewHolder.date.setText(String.format("Week Days: %s ",stringBuilder.toString()));
        viewHolder.time.setText("");
        Glide.with(context).load(localDataSet.get(position).getImageUrl()).centerCrop().into(viewHolder.imageView);
    }
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
