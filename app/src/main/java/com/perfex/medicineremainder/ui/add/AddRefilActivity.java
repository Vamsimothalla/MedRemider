package com.perfex.medicineremainder.ui.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.perfex.medicineremainder.AlarmReceiver;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.ReminderType;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.databinding.ActivityAddRefilBinding;
import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.model.Refill;
import com.perfex.medicineremainder.utils.Validator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kotlin.jvm.Throws;

public class AddRefilActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_REQUEST = 121;
    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private AppDatabase appDatabase;
    AlarmReceiver alarmReceiver=new AlarmReceiver();
    private FirebaseAuth mAuth;
    private ActivityAddRefilBinding binding;
    private MaterialDatePicker<Pair<Long, Long>> selectedDates;
    private String firstDate;
    private String secondDate;
    private Calendar calendar;
    private String mCurrentPhotoPath;
    private File photoFile;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRefilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        checkPermission();
        mAuth = FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        binding.UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        binding.dateRange.setOnClickListener(v -> {
        selectedDates= MaterialDatePicker.Builder.dateRangePicker()
                 .setTitleText("Select dates")
                 .build();
        selectedDates.show(getSupportFragmentManager(),"Date");
        selectedDates.addOnPositiveButtonClickListener(selection -> {
             calendar=Calendar.getInstance();
            calendar.setTimeInMillis(selection.first);
            StringBuilder stringBuilder=new StringBuilder();
            firstDate = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH) ) + "/" +
                    String.format("%02d", calendar.get(Calendar.MONTH)+1) + "/" + String.format("%04d", calendar.get(Calendar.YEAR));
            calendar.setTimeInMillis(selection.second);
            secondDate = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH) ) + "/" +
                    String.format("%02d", calendar.get(Calendar.MONTH)+1) + "/" + String.format("%04d", calendar.get(Calendar.YEAR));
            stringBuilder.append(firstDate).append(" - ").append(secondDate);
            binding.selectedDateRange.setText(stringBuilder);
        });
     });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    111
            );
        }
    }


    @SuppressWarnings({"UnnecessaryBoxing", "ConstantConditions"})
    private void save() throws FileNotFoundException {
        if (!Validator.isEditTextValid(binding.medicineTypeEt)){
            return;
        }
        if (!Validator.isEditTextValid(binding.purposeEt)){
            return;
        }

        Refill refill=new Refill();
        refill.setId(Integer.valueOf(Math.abs((int) System.currentTimeMillis())));
        refill.setStartDate(firstDate);
        refill.setEndDate(secondDate);
        StorageReference uploadRef=storageRef.child("refill/"+photoFile.getName());
        UploadTask uploadTask = uploadRef.putStream(new FileInputStream(photoFile));
        uploadTask.addOnFailureListener(exception -> {
            Log.d("", "save: "+exception);
        }).addOnSuccessListener(taskSnapshot -> {
            uploadRef.getDownloadUrl().addOnCompleteListener(task -> {
                refill.setImageUrl(task.getResult().toString());
                Log.d("TAG", "save: "+ref.toString());
                ref.child("Refill").child(mAuth.getUid()).child(String.valueOf(refill.getId())).setValue(refill).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Toast.makeText(AddRefilActivity.this,"Refill created",Toast.LENGTH_LONG).show();
                        Thread thread=new Thread(() -> appDatabase.refillDao().insert(refill));
                        thread.start();
                        alarmReceiver.setAlarm(AddRefilActivity.this,calendar, (int) refill.getId(), ReminderType.REFILL_REMINDER);
                        finish();
                    }
                    Log.d("TAG", "onComplete: "+task.toString());
                }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: "+e);
                }
            });
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.imageView.setImageBitmap(myBitmap);
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
}