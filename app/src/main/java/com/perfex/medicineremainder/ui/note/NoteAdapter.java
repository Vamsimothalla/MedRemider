package com.perfex.medicineremainder.ui.note;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.model.Note;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

private ArrayList<Note> localDataSet;
private Context context;

public static class ViewHolder extends RecyclerView.ViewHolder {

    private TextView titleTv;
    private TextView prescripyionTv;
    private View root;

    public ViewHolder(View view) {
        super(view);
        titleTv =view.findViewById(R.id.doctorNameTv);
        prescripyionTv =view.findViewById(R.id.medicineDosageTv);
        root=view.findViewById(R.id.root);
    }
}
    public NoteAdapter(ArrayList<Note> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_single_item, viewGroup, false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.titleTv.setText(String.format("Title : %s", localDataSet.get(position).getTitle()));
        viewHolder.prescripyionTv.setText(String.format("Prescription : %s", localDataSet.get(position).getContent()));
        viewHolder.root.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetailsActivity.class);
            intent.putExtra("note",localDataSet.get(viewHolder.getAdapterPosition()));
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}