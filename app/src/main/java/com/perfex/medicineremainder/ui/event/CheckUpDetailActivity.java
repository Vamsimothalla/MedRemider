package com.perfex.medicineremainder.ui.event;

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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.ReminderType;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.databinding.ActivityCheckUpDetailBinding;
import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.ui.add.AddCheckUpActivity;
import com.perfex.medicineremainder.utils.Validator;
import com.perfex.medicineremainder.utils.ViewGroupUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CheckUpDetailActivity extends AppCompatActivity {

    private ActivityCheckUpDetailBinding binding;
    private CheckUp checkUp;
    private boolean isEditMode = false;
    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    private AppDatabase appDatabase;
    private FirebaseAuth mAuth;
    ArrayList<EditText> oldEditText,newEditText;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckUpDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        checkUp = (CheckUp) getIntent().getSerializableExtra("checkup");
        binding.setCheckUp(checkUp);
        mAuth=FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'> Check Up Details </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        oldEditText =new ArrayList<>();
        newEditText = new ArrayList<>();
        findAllEditTexts();
        try {
            setDateAndTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setTimeAndDate();
    }
    private void findAllEditTexts() {
        oldEditText.add(binding.addressEt);
        oldEditText.add(binding.doctorNameEt);
        oldEditText.add(binding.hospitalNameEt);
        oldEditText.add(binding.typeOfAppointmentEt);
        oldEditText.add(binding.phoneNumberEt);
        for (EditText e : oldEditText) {
            EditText newEt = new EditText(this);
            newEditText.add(newEt);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        binding.timeButton.setClickable(isEditMode);
        binding.dateButton.setClickable(isEditMode);
        int id=item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
        }
        if (id == R.id.save){
            isEditMode = false;
            invalidateOptionsMenu();
            //((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
            for (int i = 0; i < oldEditText.size(); i++) {
                String text = newEditText.get(i).getText().toString();
                ViewGroupUtil.replaceView(newEditText.get(i),oldEditText.get(i));
                oldEditText.get(i).setText(text);
            }
            uploadDataToDatabase();
        }
        if(id==R.id.edit){
            isEditMode = true;
            invalidateOptionsMenu();
            for (int i = 0; i < oldEditText.size(); i++) {
                String text = oldEditText.get(i).getText().toString();
                ViewGroupUtil.replaceView(oldEditText.get(i),newEditText.get(i));
                newEditText.get(i).setText(text);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadDataToDatabase() {
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
        checkUp.setId(this.checkUp.getId());
        checkUp.setDoctorName(binding.doctorNameEt.getText().toString());
        checkUp.setHospitalName(binding.hospitalNameEt.getText().toString());
        checkUp.setPurposeOfCheckUp(binding.typeOfAppointmentEt.getText().toString());
        checkUp.setPhoneNumber(binding.phoneNumberEt.getText().toString());
        checkUp.setAddressOfHospital(binding.addressEt.getText().toString());
        checkUp.setTime(binding.timeSelected.getText().toString());
        checkUp.setDate(binding.startDateSelected.getText().toString());
        ref.child("CheckUp").child(mAuth.getUid()).child(String.valueOf(checkUp.getId())).setValue(checkUp).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(CheckUpDetailActivity.this,"checkUp Updated",Toast.LENGTH_LONG).show();
                Thread thread=new Thread(() -> appDatabase.checkUpDao().update(checkUp));
                thread.start();
                //alarmReceiver.setAlarm(AddCheckUpActivity.this,myCalendar, (int) checkUp.getId(), ReminderType.CHECK_UP_REMINDER);
                //finish();
            }
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        selectMenu(menu);
        return true;
    }
    private void selectMenu(Menu menu){
        menu.clear();
        if(isEditMode){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.save_menu, menu);
        }
        else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.edit_menu, menu);
        }
    }
    private void setDateAndTime() throws ParseException {
        setTime();
        setDate();
    }

    private void setTime() throws ParseException {
        Calendar temp = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ROOT);
        Date time = sdf.parse(checkUp.getTime());
        temp.setTime(time);
        myCalendar.set(Calendar.MINUTE,temp.get(Calendar.MINUTE));
        myCalendar.set(Calendar.HOUR_OF_DAY,temp.get(Calendar.HOUR_OF_DAY));
    }

    private void setDate() throws ParseException {
        Calendar temp = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ROOT);
        Date time = sdf.parse(checkUp.getTime());
        temp.setTime(time);
        myCalendar.set(Calendar.DAY_OF_MONTH,temp.get(Calendar.DAY_OF_MONTH));
        myCalendar.set(Calendar.MONTH,temp.get(Calendar.MONTH));
        myCalendar.set(Calendar.YEAR,temp.get(Calendar.YEAR));
    }
    private void setTimeAndDate() {
        binding.dateButton.setOnClickListener(v -> {
              DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        @SuppressLint("DefaultLocale")
                        String setDate = String.format("%02d", month + 1) + "/" + String.format("%02d", day) + "/" + String.format("%04d", year);
                        binding.startDateSelected.setText(setDate);
                    };
                    DatePickerDialog datePickerDialog = new DatePickerDialog(CheckUpDetailActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
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
}