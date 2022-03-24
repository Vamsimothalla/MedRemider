package com.perfex.medicineremainder.ui.add.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.perfex.medicineremainder.R;

import java.util.ArrayList;
import java.util.List;

public class WeekDaysSpinnerAdapter extends ArrayAdapter<CheckList> {
    private Context mContext;
    private ArrayList<CheckList> listState;
    private WeekDaysSpinnerAdapter myAdapter;
    private ArrayList<String> timings;
    private OnCheckChangedListener onCheckChangedListener;
    private boolean isFromView = false;

    public ArrayList<String> getTimings() {
        return timings;
    }

    public void setTimings(ArrayList<String> timings) {
        this.timings = timings;
    }

    public WeekDaysSpinnerAdapter(Context context, int resource, List<CheckList> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<CheckList>) objects;
        this.myAdapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public ArrayList<CheckList> getListState() {
        return listState;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.week_days_spinner_single_item, null);
            holder = new ViewHolder();
            holder.mTextView = convertView
                    .findViewById(R.id.text);
            holder.mCheckBox = convertView
                    .findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int getPosition = (Integer) buttonView.getTag();
            if (!isFromView) {
                listState.get(position).setSelected(isChecked);
            }
        });

        return convertView;
    }

    public interface OnCheckChangedListener{
        public void isChecked(boolean isChecked,int type);
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}