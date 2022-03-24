package com.perfex.medicineremainder.ui.event.fragments.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.perfex.medicineremainder.ui.event.fragments.AppointmentFragment;
import com.perfex.medicineremainder.ui.event.fragments.CheckUpFragment;
import com.perfex.medicineremainder.ui.event.fragments.RefillFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;
    public FragmentAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AppointmentFragment appointmentFragment = new AppointmentFragment();
                return appointmentFragment;
            case 1:
                CheckUpFragment checkUpFragment = new CheckUpFragment();
                return checkUpFragment;
            case 2:
                RefillFragment refillFragment = new RefillFragment();
                return refillFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
