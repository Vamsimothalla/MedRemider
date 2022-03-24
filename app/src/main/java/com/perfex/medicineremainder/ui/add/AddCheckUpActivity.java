package com.perfex.medicineremainder.ui.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perfex.medicineremainder.AlarmReceiver;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.ReminderType;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.databinding.ActivityAddCheckUpBinding;
import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.utils.Validator;

import java.util.Calendar;

public class AddCheckUpActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference();
    private AppDatabase appDatabase;
    AlarmReceiver alarmReceiver = new AlarmReceiver();
    private FirebaseAuth mAuth;
    ActivityAddCheckUpBinding binding;
    private Calendar myCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddCheckUpBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        appDatabase = AppDatabase.getDatabase(this);
        setCustomActionBar();
        setTimeAndDate();
    }
    private void setCustomActionBar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Add Checkup Reminder </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
    }
    private void setTimeAndDate() {
        myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.SECOND,0);
        binding.dateButton.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String setDate = String.format("%02d", month + 1) + "/" + String.format("%02d", day) + "/" + String.format("%04d", year);
                binding.startDateSelected.setText(setDate);
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddCheckUpActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
            datePickerDialog.show();
                }
        );
        binding.timeButton.setOnClickListener(v -> {
            TimePickerDialog.OnTimeSetListener time= (view, hourOfDay, minute) -> {
                Log.d("TAG", "setTimeAndDate: "+hourOfDay);
                if (!view.is24HourView()){
                    String setTime;
                    myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    myCalendar.set(Calendar.MINUTE,minute);
                    setTime = String.format("%02d", (myCalendar.get(Calendar.HOUR))) + ":" + String.format("%02d", myCalendar.get(Calendar.MINUTE)) + " " + ((myCalendar.get(Calendar.AM_PM )== Calendar.PM) ?"pm":"am");
                    Log.d("", String.valueOf(myCalendar.getTime()));
                    binding.timeSelected.setText(setTime);
                }
            };
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,time,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),false);
            timePickerDialog.show();
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
        }
        if (id==R.id.save){
            save();
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings({"UnnecessaryBoxing", "ConstantConditions"})
    private void save() {
        if (!Validator.isEditTextValid(binding.doctorNameEt)){
            return;
        }
        if (!Validator.isEditTextValid(binding.hospitalNameEt)){
            return;
        }
        if (!Validator.isEditTextValid(binding.addressEt)){
            return;
        }
        if (!Validator.isEditTextValid(binding.phoneNumberEt)){
            return;
        }
        CheckUp checkUp=new CheckUp();
        checkUp.setId(Integer.valueOf(Math.abs((int) System.currentTimeMillis())));
        checkUp.setDoctorName(binding.doctorNameEt.getText().toString());
        checkUp.setHospitalName(binding.hospitalNameEt.getText().toString());
        checkUp.setPurposeOfCheckUp(binding.typeOfAppointmentEt.getText().toString());
        checkUp.setPhoneNumber(binding.phoneNumberEt.getText().toString());
        checkUp.setAddressOfHospital(binding.addressEt.getText().toString());
        checkUp.setTime(binding.timeSelected.getText().toString());
        checkUp.setDate(binding.startDateSelected.getText().toString());
        ref.child("CheckUp").child(mAuth.getUid()).child(String.valueOf(checkUp.getId())).setValue(checkUp).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(AddCheckUpActivity.this,"Appointment created",Toast.LENGTH_LONG).show();
                Thread thread=new Thread(() -> appDatabase.checkUpDao().insert(checkUp));
                thread.start();
                alarmReceiver.setAlarm(AddCheckUpActivity.this,myCalendar, (int) checkUp.getId(), ReminderType.CHECK_UP_REMINDER);
                finish();
            }
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }
}