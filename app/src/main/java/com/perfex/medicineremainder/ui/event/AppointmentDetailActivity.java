package com.perfex.medicineremainder.ui.event;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perfex.medicineremainder.AlarmReceiver;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.ReminderType;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.databinding.ActivityAppointmentDetailBinding;
import com.perfex.medicineremainder.model.Appointment;
import com.perfex.medicineremainder.ui.add.AddAppointmentActivity;
import com.perfex.medicineremainder.utils.Constants;
import com.perfex.medicineremainder.utils.ViewGroupUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentDetailActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    private AppDatabase appDatabase;
    private FirebaseAuth mAuth;
    ActivityAppointmentDetailBinding binding;
    ArrayList<EditText> oldEditText,newEditText;
    private boolean isEditMode = false,isUpdated = false;
    private Appointment appointment;
    private Calendar myCalendar = Calendar.getInstance();
    private String setDate;
    private AlarmReceiver alarmReceiver;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAppointmentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
         appointment= (Appointment) getIntent().getSerializableExtra("appointment");
        Log.d("TAG", "onCreate: "+appointment.toString());
        binding.setAppointment(appointment);
        setSupportActionBar(findViewById(R.id.toolbar));
        alarmReceiver = new AlarmReceiver();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'> Appointment Details </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        oldEditText =new ArrayList<>();
        newEditText = new ArrayList<>();
        findAllEditTexts();
        try {
            setDateAndTime();
            setTimeAndDate();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("TAG", "onCreate: "+e);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        selectMenu(menu);
        return true;
    }
    private void selectMenu(Menu menu){
        menu.clear();
        binding.timeButton.setClickable(isEditMode);
        binding.dateButton.setClickable(isEditMode);
        MenuInflater inflater = getMenuInflater();
        if(isEditMode){
            inflater.inflate(R.menu.save_menu, menu);
        }
        else {
            inflater.inflate(R.menu.edit_menu, menu);
        }
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
    private void uploadDataToDatabase() {
        Appointment appointment=new Appointment();
        appointment.setId(this.appointment.getId());
        appointment.setDoctorName(binding.doctorNameEt.getText().toString());
        appointment.setHospitalName(binding.hospitalNameEt.getText().toString());
        appointment.setPurposeOfAppointment(binding.typeOfAppointmentEt.getText().toString());
        appointment.setPhoneNumber(binding.phoneNumberEt.getText().toString());
        appointment.setAddressOfHospital(binding.addressEt.getText().toString());
        appointment.setTime(binding.timeSelected.getText().toString());
        appointment.setDate(binding.startDateSelected.getText().toString());

        Log.d("TAG", "save: "+appointment.getId());
        ref.child("Appointment").child(mAuth.getUid()).child(String.valueOf(appointment.getId())).setValue(appointment).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(AppointmentDetailActivity.this,"Appointment created",Toast.LENGTH_LONG).show();
                Thread thread=new Thread(() -> appDatabase.appointmentDao().update(appointment));
                thread.start();
                alarmReceiver.setAlarm(AppointmentDetailActivity.this,myCalendar, (int) appointment.getId(), ReminderType.APPOINTMENT_REMINDER);
                finish();
            }
            Log.d("TAG", "onComplete: "+task.toString());
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
    }
    private void setTimeAndDate() {
        binding.dateButton.setOnClickListener(v -> {
                    DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        setDate = String.format("%02d", month + 1) + "/" +
                                String.format("%02d", day) + "/" + String.format("%04d", year);
                        binding.startDateSelected.setText(setDate);
                        isUpdated  = true;
                    };
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentDetailActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
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
    private void setDateAndTime() throws ParseException {
        setTime();
        setDate();
    }
    private void setTime() throws ParseException {
        Calendar temp = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ROOT);
        Date time = sdf.parse(appointment.getTime());
        temp.setTime(time);
        myCalendar.set(Calendar.MINUTE,temp.get(Calendar.MINUTE));
        myCalendar.set(Calendar.HOUR_OF_DAY,temp.get(Calendar.HOUR_OF_DAY));
    }

    @Override
    protected void onStart() {
        super.onStart();
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, Constants.AD_INT, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }

    private void setDate() throws ParseException {
        Calendar temp = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ROOT);
        Date time = sdf.parse(appointment.getDate());
        temp.setTime(time);
        myCalendar.set(Calendar.DAY_OF_MONTH,temp.get(Calendar.DAY_OF_MONTH));
        myCalendar.set(Calendar.MONTH,temp.get(Calendar.MONTH));
        myCalendar.set(Calendar.YEAR,temp.get(Calendar.YEAR));
    }
}


