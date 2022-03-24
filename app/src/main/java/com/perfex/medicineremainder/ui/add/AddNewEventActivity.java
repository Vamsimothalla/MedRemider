package com.perfex.medicineremainder.ui.add;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.databinding.ActivityAddNewEventBinding;

public class AddNewEventActivity extends AppCompatActivity {
    ActivityAddNewEventBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddNewEventBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setCustomActionBar();
        binding.addAppointment.setOnClickListener(v -> {
            Intent intent=new Intent(AddNewEventActivity.this,AddAppointmentActivity.class);
            startActivity(intent);
        });
        binding.checkUp.setOnClickListener(v -> {
            Intent intent=new Intent(AddNewEventActivity.this,AddCheckUpActivity.class);
            startActivity(intent);
        });
        binding.addReffil.setOnClickListener(v -> {
            Intent intent=new Intent(AddNewEventActivity.this,AddRefilActivity.class);
            startActivity(intent);
        });
    }
    private void setCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New Event");
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}