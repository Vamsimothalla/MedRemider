package com.perfex.medicineremainder.ui.event.fragments.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.model.Refill;
import com.perfex.medicineremainder.ui.event.CheckUpDetailActivity;

import java.util.ArrayList;

public class RefillAdapter extends RecyclerView.Adapter<RefillAdapter.ViewHolder> {

    private ArrayList<Refill> localDataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView medicineType;
        private TextView purpose;
        private TextView dateRange;
        private View root;

        public ViewHolder(View view) {
            super(view);
            medicineType=view.findViewById(R.id.medicine_type);
            purpose=view.findViewById(R.id.purposeDosageTv);
            dateRange=view.findViewById(R.id.dateRange);
            root=view.findViewById(R.id.root);

        }

    }
    public RefillAdapter(ArrayList<Refill> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RefillAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.refill_single_item, viewGroup, false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.medicineType.setText(String.format("Medicine Type  : %s", localDataSet.get(position).getMedicineType()));
        viewHolder.purpose.setText(String.format("Purpose od medicine : %s", localDataSet.get(position).getPurpose()));
        viewHolder.dateRange.setText(String.format("Course range : %s - %s", localDataSet.get(position).getStartDate(),localDataSet.get(position).getStartDate()));
        viewHolder.root.setOnClickListener(v -> {
            Intent intent = new Intent(context, CheckUpDetailActivity.class);
            intent.putExtra("refill",localDataSet.get(viewHolder.getAdapterPosition()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
