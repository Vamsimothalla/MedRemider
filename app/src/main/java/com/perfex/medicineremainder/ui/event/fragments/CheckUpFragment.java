package com.perfex.medicineremainder.ui.event.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.model.Appointment;
import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.ui.event.CheckUpDetailActivity;
import com.perfex.medicineremainder.ui.event.RefillDetailActivity;
import com.perfex.medicineremainder.ui.event.fragments.adapter.AppointmentAdapter;
import com.perfex.medicineremainder.ui.event.fragments.adapter.CheckUpAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckUpFragment extends Fragment {

    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    private FirebaseAuth mAuth =FirebaseAuth.getInstance();

    private RecyclerView recyclerView;

    public CheckUpFragment() {
    }


    public static CheckUpFragment newInstance() {

        return new CheckUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_check_up, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        ArrayList<CheckUp> checkUps = new ArrayList<>();
        ref.child("CheckUp").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG", "onDataChange: "+snapshot);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CheckUp appointment = ds.getValue(CheckUp.class);
                    checkUps.add(appointment);
                }
               CheckUpAdapter checkUpAdapter = new CheckUpAdapter(checkUps, getContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(checkUpAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }
}