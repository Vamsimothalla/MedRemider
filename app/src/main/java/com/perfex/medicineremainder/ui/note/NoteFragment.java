package com.perfex.medicineremainder.ui.note;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.model.Appointment;
import com.perfex.medicineremainder.model.Note;
import com.perfex.medicineremainder.ui.event.fragments.adapter.AppointmentAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class NoteFragment extends Fragment {

    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    private FirebaseAuth mAuth =FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    public NoteFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_add2, container, false);
        floatingActionButton=view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity() , AddNoteActivity.class);
            requireActivity().startActivity(intent);
        });
        recyclerView = view.findViewById(R.id.recyclerView);
        ref.child("Note").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG", "onDataChange: "+snapshot);
                ArrayList<Note> notes = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Note note = ds.getValue(Note.class);
                    notes.add(note);
                }
                NoteAdapter noteAdapter = new NoteAdapter(notes, getContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(noteAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}