package com.perfex.medicineremainder.ui.add;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.databinding.FragmentAddBinding;
import com.perfex.medicineremainder.databinding.FragmentHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    private FragmentAddBinding binding;


    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentAddBinding.inflate(inflater,container,false);
        binding.addEvents.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),AddNewEventActivity.class);
            startActivity(intent);
        });
        binding.addReminder.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),AddReminderActivity.class);
            startActivity(intent);
        });
        return binding.getRoot();
    }
}