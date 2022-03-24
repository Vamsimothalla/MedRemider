package com.perfex.medicineremainder.ui.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perfex.medicineremainder.AlarmReceiver;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.ReminderType;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.databinding.ActivityAddAppointmentBinding;
import com.perfex.medicineremainder.model.Appointment;
import com.perfex.medicineremainder.utils.Validator;

import java.util.Calendar;
import java.util.Objects;

public class AddAppointmentActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    private FirebaseAuth mAuth;
    private ActivityAddAppointmentBinding binding;
    private Calendar myCalendar=Calendar.getInstance();
    private AppDatabase appDatabase;
    AlarmReceiver alarmReceiver=new AlarmReceiver();
    private int year,month,date,hour,minutes;
    String setDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddAppointmentBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
        setTimeAndDate();
        setCustomActionBar();
    }
    private void setCustomActionBar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Add Appointment </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        appDatabase.appointmentDao();
    }
    private void setTimeAndDate() {
        binding.dateButton.setOnClickListener(v -> {
                    myCalendar = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                         setDate = String.format("%02d", month + 1) + "/" +
                                String.format("%02d", day) + "/" + String.format("%04d", year);
                        binding.startDateSelected.setText(setDate);
                    };
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddAppointmentActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                    datePickerDialog.show();
                }

        );
        binding.timeButton.setOnClickListener(v -> {
            @SuppressLint("DefaultLocale") TimePickerDialog.OnTimeSetListener time= (view, hourOfDay, minute) -> {
                Log.d("TAG", "setTimeAndDate: "+hourOfDay);
                if (!view.is24HourView()){
                    String setTime;
                    myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    myCalendar.set(Calendar.MINUTE,minute);
                    setTime = String.format("%02d", (myCalendar.get(Calendar.HOUR))) + ":" +
                            String.format("%02d", myCalendar.get(Calendar.MINUTE)) + " " + ((myCalendar.get(Calendar.AM_PM )== Calendar.PM) ?"pm":"am");

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
        Appointment appointment=new Appointment();
        appointment.setId(new Integer(Math.abs((int) System.currentTimeMillis())));
        appointment.setDoctorName(binding.doctorNameEt.getText().toString());
        appointment.setHospitalName(binding.hospitalNameEt.getText().toString());
        appointment.setPurposeOfAppointment(binding.typeOfAppointmentEt.getText().toString());
        appointment.setPhoneNumber(binding.phoneNumberEt.getText().toString());
        appointment.setAddressOfHospital(binding.addressEt.getText().toString());
        appointment.setTime(binding.timeSelected.getText().toString());
        appointment.setDate(binding.startDateSelected.getText().toString());
        ref.child("Appointment").child((mAuth.getUid())).child(String.valueOf(appointment.getId())).setValue(appointment).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(AddAppointmentActivity.this,"Appointment created",Toast.LENGTH_LONG).show();
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        appDatabase.appointmentDao().insert(appointment);
                    }
                });
                thread.start();
                alarmReceiver.setAlarm(AddAppointmentActivity.this,myCalendar, (int) appointment.getId(), ReminderType.APPOINTMENT_REMINDER);
                finish();
            }
            Log.d("TAG", "onComplete: "+task.toString());
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }
}