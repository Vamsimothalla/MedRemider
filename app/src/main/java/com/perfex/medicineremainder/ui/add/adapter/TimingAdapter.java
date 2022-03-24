package com.perfex.medicineremainder.ui.add.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.perfex.medicineremainder.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TimingAdapter extends RecyclerView.Adapter<TimingAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Timing> listState;
    private TimingAdapter myAdapter;
    private HashMap<Integer,String > timingsMap  = new HashMap<>();
    public TimingAdapter(Context context, List<Timing> objects) {
        this.mContext = context;
        this.listState = (ArrayList<Timing>) objects;
        this.myAdapter = this;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.timing_bottom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextView.setText(listState.get(position).getTitle());
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        holder.before.setText(listState.get(position).getRadioList()[0]);
        holder.after.setText(listState.get(position).getRadioList()[1]);
        holder.mCheckBox.setTag(position);
        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = group.findViewById(checkedId);
            int index = group.indexOfChild(radioButton);
        });
        holder.timeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar myCalendar = Calendar.getInstance();
                TimePickerDialog.OnTimeSetListener time= (view, hourOfDay, minute) -> {
                    Log.d("TAG", "setTimeAndDate: "+hourOfDay);
                    if (!view.is24HourView()){
                        String setTime;
                        myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        myCalendar.set(Calendar.MINUTE,minute);
                        setTime = String.format("%02d", (myCalendar.get(Calendar.HOUR))) + ":" +
                                String.format("%02d", myCalendar.get(Calendar.MINUTE)) + " " + ((myCalendar.get(Calendar.AM_PM )== Calendar.PM) ?"pm":"am");
                        Log.d("", String.valueOf(myCalendar.getTime()));
                        timingsMap.put(position,setTime);
                        holder.mTime.setText(setTime);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,time,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),false);
                timePickerDialog.show();

            }
        });
        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            holder.timeLL.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if(!isChecked){
                timingsMap.remove(position);
            }
            listState.get(position).setSelected(isChecked);
        });
    }
    public HashMap<Integer, String> getTimingsMap() {
        return timingsMap;
    }

    @Override
    public int getItemCount() {
        return listState.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
        private TextView mTime;
        private LinearLayout timeLL;
        private RadioGroup radioGroup;
        private RadioButton before;
        private RadioButton after;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            mTextView = convertView.findViewById(R.id.text);
            mCheckBox = convertView.findViewById(R.id.checkbox);
            mTime = convertView.findViewById(R.id.timeSelected);
            timeLL = convertView.findViewById(R.id.timeLinearLayout);
            before=convertView.findViewById(R.id.before);
            after=convertView.findViewById(R.id.after);
            radioGroup=convertView.findViewById(R.id.radioGroup);
        }
    }
}