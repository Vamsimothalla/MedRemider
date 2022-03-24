package com.perfex.medicineremainder.ui.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.perfex.medicineremainder.AlarmReceiver;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.database.user.medicine.Medicine;
import com.perfex.medicineremainder.databinding.ActivityAddRemiderBinding;
import com.perfex.medicineremainder.ui.add.adapter.CheckList;
import com.perfex.medicineremainder.ui.add.adapter.Timing;
import com.perfex.medicineremainder.ui.add.adapter.TimingAdapter;
import com.perfex.medicineremainder.ui.add.adapter.WeekDaysSpinnerAdapter;
import com.perfex.medicineremainder.utils.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {

    public static final String EXTRA_REMINDER_ID = "reminder_id";
    final String[] weekDays=new String[]{"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    final String[] timings=new String[]{"Early Morning","Morning","Afternoon","Evening","Night"};
    final String[][] timingsBeforeOrAfter={{"Before Food","After Food"},{"Before BreakFast","After BreakFast"},{"Before Lunch","After Lunch"},{"Before Food","After Food"},{"Before Dinner","After Dinner"}};
    private static final int CAPTURE_IMAGE_REQUEST = 121;
    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private AppDatabase appDatabase;
    AlarmReceiver alarmReceiver=new AlarmReceiver();
    private FirebaseAuth mAuth;
    private String mCurrentPhotoPath;
    private File photoFile;
    private ArrayList<String> timingSelection;
    private ArrayList<String> weekDaysSelection = new ArrayList<>();
    ActivityAddRemiderBinding binding;
    private Calendar myCalendar=Calendar.getInstance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddRemiderBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        progressDialog=new ProgressDialog(this);
        setCustomActionBar();
        mAuth = FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
        binding.weekDaysSelection.setOnClickListener(v -> {
            setBottomSheet(weekDays);
        });
        binding.timeSelection.setOnClickListener(v -> {
            setTimingBottomSheet();
        });
        setStartAndEndDates();
        binding.medicineImage.setOnClickListener(v -> {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        });
        binding.medicineImage.setOnClickListener(v -> {
            checkPermission();
            captureImage();
        });
    }

    private void setCustomActionBar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle((Html.fromHtml("<font color='#ffffff'>Add Medicine Reminder </font>")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
    }
    @SuppressLint("DefaultLocale")
    private void setStartAndEndDates() {
        binding.startDateButton.setOnClickListener(v ->{
            myCalendar=Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String setDate = String.format("%02d",month+1) + "/" + String.format("%02d",day) + "/" + String.format("%04d",year);
                binding.startDateSelected.setText(setDate);
            };
                DatePickerDialog datePickerDialog=new DatePickerDialog(AddReminderActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        );
        binding.endDateButton.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                String setDate = String.format("%02d",month+1) + "/" + String.format("%02d",day) + "/" + String.format("%04d",year);
                binding.endDateSelected.setText(setDate);
            };
            DatePickerDialog datePickerDialog=new DatePickerDialog(AddReminderActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
            datePickerDialog.show();
        });
    }
    private void setAlarmDaily(int id) throws ParseException {
        Calendar calendar=Calendar.getInstance();
        for ( String time : timingSelection ) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.ROOT);
            Date date = sdf.parse(time);
            calendar.set(Calendar.MINUTE,date.getMinutes());
            calendar.set(Calendar.HOUR,date.getHours());
            alarmReceiver.setRepeatAlarm(this,calendar,id, 24 * 60 * 60 * 1000);
            Log.d("TAG", "setAlarmDaily: "+calendar.getTime());
        }
    }
    private void setAlarmWeekly(int id) throws ParseException {
        int i = 0;
        for (String week: weekDaysSelection) {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(week.toUpperCase(Locale.ROOT));
            int day= dayOfWeek.ordinal()+1;
            Log.d("TAG", "setAlarm: "+dayOfWeek);
            Calendar calendar= nextDayOfWeek(day);
            for ( String time : timingSelection ) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.ROOT);
                Date date = sdf.parse(time);
                calendar.set(Calendar.MINUTE,date.getMinutes());
                calendar.set(Calendar.HOUR,date.getHours());
                alarmReceiver.setRepeatAlarm(this,calendar,id, 7*24 * 60 * 60 * 1000,id+i);
                ++i;
                Log.d("TAG", "setAlarm: "+calendar.getTime());
            }
        }
    }
    private void setTimingBottomSheet(){
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.timing_list_view);
        bottomSheetDialog.setCancelable(false);
        ArrayList<Timing> listV1s = new ArrayList<>();
        for (int i = 0; i < timings.length; i++) {
            Timing timing = new Timing();
            timing.setTitle(timings[i]);
            timing.setSelected(false);
            timing.setRadioList(timingsBeforeOrAfter[i]);
            listV1s.add(timing);
        }
            TimingAdapter timingAdapter = new TimingAdapter(AddReminderActivity.this,  listV1s);
            RecyclerView listView =bottomSheetDialog.findViewById(R.id.list_item);
            Button button=bottomSheetDialog.findViewById(R.id.cancel);
            Button submit=bottomSheetDialog.findViewById(R.id.submit);
            submit.setOnClickListener(v -> {
                timingSelection=new ArrayList<>();
                HashMap<Integer,String > integerStringHashMap= timingAdapter.getTimingsMap();
                timingSelection.addAll(integerStringHashMap.values());
                Log.d("TAG", "onClick: "+timingSelection);
                bottomSheetDialog.cancel();
            });
            button.setOnClickListener(v1 -> bottomSheetDialog.cancel());
            listView.setLayoutManager(new LinearLayoutManager(this));
            listView.setAdapter(timingAdapter);
            bottomSheetDialog.show();
    }
    private void setBottomSheet(String[] lists){
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.week_day_bootom_sheet);
        bottomSheetDialog.setCancelable(false);
        ArrayList<CheckList> listV1s = new ArrayList<>();
        for (String list : lists) {
            CheckList checkList = new CheckList();
            checkList.setTitle(list);
            checkList.setSelected(false);
            listV1s.add(checkList);
        }
        WeekDaysSpinnerAdapter weekDaysSpinnerAdapter = new WeekDaysSpinnerAdapter(AddReminderActivity.this, 0, listV1s);
        ListView listView =bottomSheetDialog.findViewById(R.id.list_item);
        Button button=bottomSheetDialog.findViewById(R.id.cancel);
        Button submit=bottomSheetDialog.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            weekDaysSelection=new ArrayList<>();
            StringBuilder stringBuilder=new StringBuilder();
            int i=0;
            for (CheckList c: listV1s) {
                if(c.isSelected()){
                    if(i!=0){
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(c.getTitle().substring(0,3));
                    weekDaysSelection.add(c.getTitle()) ;
                    i++;
                }
            }
            if(listV1s.size()==weekDays.length){
                if(i==0 || i==7){
                    binding.weekHint.setText("Every Day");
                }
                else {
                    binding.weekHint.setText(stringBuilder.toString());
                }
            }
            bottomSheetDialog.cancel();
        });
        button.setOnClickListener(v1 -> bottomSheetDialog.cancel());
        listView.setAdapter(weekDaysSpinnerAdapter);
        bottomSheetDialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }
    @SuppressLint("QueryPermissionsNeeded")
    private void captureImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    111
            );
        }
        else {
            Log.d("TAG", "captureImage: ");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    photoFile = createImageFile();
                    Uri photoURI = FileProvider.getUriForFile(
                            this,
                            "com.perfex.medicineremainder.fileprovider",
                            photoFile
                    );
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                } catch (Exception e) {
                    Log.d("TAG", "captureImage: "+e);
                }
            }
        }
    }
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.medicineImage.setImageBitmap(myBitmap);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
        }
        if (id==R.id.save){
            try {
                save();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("TAG", "onOptionsItemSelected: "+e.toString());
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111
            );
        }
    }
    @SuppressWarnings({"UnnecessaryBoxing", "ConstantConditions"})
    private void save() throws FileNotFoundException {
        if(photoFile == null){
            Toast.makeText(this ,"Image required please upload image",Toast.LENGTH_LONG).show();
            return;
        }
        if (!Validator.isEditTextValid(binding.medicineTypeEt)){
            return;
        }
        if (!Validator.isEditTextValid(binding.medicineDosageEt)){
            return;
        }

        Medicine medicine=new Medicine();
        medicine.setId(Integer.valueOf(Math.abs((int) System.currentTimeMillis())));
        medicine.setMedicineName(binding.medicineTypeEt.getText().toString());
        medicine.setDosage(binding.medicineDosageEt.getText().toString());
        medicine.setPurposeOfMedicine(binding.purposeOfMedicineEt.getText().toString());
        medicine.setTimingsArrayList(timingSelection);
        medicine.setWeekDaysArrayList(weekDaysSelection);
        StorageReference uploadRef=storageRef.child("medicine/"+photoFile.getName());
        UploadTask uploadTask = uploadRef.putStream(new FileInputStream(photoFile));
        uploadTask.addOnProgressListener(snapshot -> {
            progressDialog.show();
            double progress = 100.0 * (snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            System.out.println("Upload is " + progress + "% done");
            int currentprogress = (int) progress;
            progressDialog.setProgress(currentprogress);
        });
        uploadTask.addOnFailureListener(exception -> {
            Log.d("", "save: "+exception);
        }).addOnSuccessListener(taskSnapshot -> {
            uploadRef.getDownloadUrl().addOnCompleteListener(task -> {
                medicine.setImageUrl(task.getResult().toString());
                Log.d("TAG", "save: "+ref.toString());
                ref.child("MedicineReminder").child(mAuth.getUid()).child(String.valueOf(medicine.getId())).setValue(medicine).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Toast.makeText(AddReminderActivity.this,"Refill created",Toast.LENGTH_LONG).show();
                        Thread thread=new Thread(() -> appDatabase.medicineDao().insert(medicine));
                        thread.start();
                        if (weekDaysSelection.size()==0 || weekDaysSelection.size() == 7){
                            try {
                                setAlarmDaily(medicine.getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("TAG", "setTimingBottomSheet: "+e);
                            }
                        }
                        else {
                            try {
                                setAlarmWeekly(medicine.getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("TAG", "setTimingBottomSheet: "+e);
                            }
                        }
                        finish();
                    }
                    Log.d("TAG", "onComplete: "+ task);
                }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
            }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
        });
    }
    public Calendar nextDayOfWeek(int dow) {
        Calendar date = Calendar.getInstance();
        Log.d("TAG", "nextDayOfWeek: "+date.get(Calendar.DAY_OF_WEEK));
        int diff = dow - date.get(Calendar.DAY_OF_WEEK)+1;
        if (diff <= 0) {
            diff += 7;
        }
        Log.d("TAG", "nextDayOfWeek: "+diff);
        date.add(Calendar.DAY_OF_MONTH, diff);
        return date;
    }
}