package com.perfex.medicineremainder.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.database.user.medicine.Medicine;
import com.perfex.medicineremainder.databinding.ActivityMedicineReminderBinding;
import com.perfex.medicineremainder.ui.add.AddReminderActivity;
import com.perfex.medicineremainder.utils.ViewGroupUtil;

import java.util.ArrayList;

public class MedicineReminderActivity extends AppCompatActivity {
    public static final String EXTRA_REMINDER_ID = "remid";
    ActivityMedicineReminderBinding  binding;
    private boolean isEditMode = false;
    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    private AppDatabase appDatabase;
    private FirebaseAuth mAuth;
    ArrayList<EditText> oldEditText,newEditText;
    private Medicine medicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicineReminderBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'> Medicine Details </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        medicine = (Medicine) getIntent().getSerializableExtra("reminder");
        binding.setRemider(medicine);
        mAuth=FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
        oldEditText =new ArrayList<>();
        newEditText = new ArrayList<>();
        findAllEditTexts();
    }
    private void findAllEditTexts() {
        oldEditText.add(binding.medicineTypeEt);
        oldEditText.add(binding.medicineDosageEt);
        oldEditText.add(binding.purposeOfMedicineTv);
        for (EditText e : oldEditText) {
            EditText newEt = new EditText(this);
            newEditText.add(newEt);
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

    private void uploadDataToDatabase() {
        Medicine medicine=new Medicine();
        medicine.setId(this.medicine.getId());
        medicine.setMedicineName(binding.medicineTypeEt.getText().toString());
        medicine.setDosage(binding.medicineDosageEt.getText().toString());
        medicine.setPurposeOfMedicine(binding.purposeOfMedicineTv.getText().toString());
        medicine.setTimingsArrayList(this.medicine.getTimingsArrayList());
        medicine.setWeekDaysArrayList(this.medicine.getWeekDaysArrayList());
        ref.child("MedicineReminder").child(mAuth.getUid()).child(String.valueOf(medicine.getId())).setValue(medicine).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                Toast.makeText(MedicineReminderActivity.this,"reminder created",Toast.LENGTH_LONG).show();
                Thread thread=new Thread(() -> appDatabase.medicineDao().update(medicine));
                thread.start();
            }
        });
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
}