package com.perfex.medicineremainder.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.perfex.medicineremainder.database.user.medicine.Medicine;
import com.perfex.medicineremainder.databinding.FragmentHomeBinding;
import com.perfex.medicineremainder.model.Refill;
import com.perfex.medicineremainder.ui.event.fragments.adapter.RefillAdapter;
import com.perfex.medicineremainder.ui.home.adapter.HomeAdapter;
import com.perfex.medicineremainder.views.ShimmerFrameLayout;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    private final DatabaseReference ref = database.getReference( );
    private final FirebaseAuth mAuth =FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHomeBinding.bind(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        shimmerFrameLayout.startShimmer();
        ArrayList<Medicine> medicines = new ArrayList<>();
        ref.child("MedicineReminder").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG", "onDataChange: "+snapshot);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Medicine medicine = ds.getValue(Medicine.class);
                    medicines.add(medicine);
                }
                HomeAdapter refillAdapter = new HomeAdapter(medicines, getContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(refillAdapter);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.hideShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }});
        return view;
    }

}