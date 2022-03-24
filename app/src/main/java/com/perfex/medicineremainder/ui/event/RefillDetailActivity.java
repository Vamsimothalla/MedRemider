package com.perfex.medicineremainder.ui.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.ReminderType;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.databinding.ActivityRefillDetailBinding;
import com.perfex.medicineremainder.model.Refill;
import com.perfex.medicineremainder.ui.add.AddRefilActivity;
import com.perfex.medicineremainder.utils.Validator;
import com.perfex.medicineremainder.utils.ViewGroupUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class RefillDetailActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");

    private ActivityRefillDetailBinding binding;
    private boolean isEditMode = false;
    DatabaseReference ref = database.getReference();
    private AppDatabase appDatabase;
    private FirebaseAuth mAuth;
    ArrayList<EditText> oldEditText,newEditText;
    private Refill refill;
    private String firstDate,secondDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRefillDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        mAuth=FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
        refill= (Refill) getIntent().getSerializableExtra("refill");
        firstDate = refill.getStartDate();
        secondDate = refill.getEndDate();
        binding.setRefill(refill);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'> Refill Details </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        oldEditText =new ArrayList<>();
        newEditText = new ArrayList<>();
        findAllEditTexts();
        binding.dateRange.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> selectedDates = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select dates")
                    .build();
            selectedDates.show(getSupportFragmentManager(),"Date");
            selectedDates.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance();
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
    private void findAllEditTexts() {
        oldEditText.add(binding.medicineTypeEt);
        oldEditText.add(binding.purposeEt);
        for (EditText e : oldEditText) {
            EditText newEt = new EditText(this);
            newEditText.add(newEt);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        binding.dateRange.setClickable(isEditMode);
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
        if (!Validator.isEditTextValid(binding.medicineTypeEt)){
            return;
        }
        if (!Validator.isEditTextValid(binding.purposeEt)){
            return;
        }
        Refill refill=new Refill();
        refill.setId(this.refill.getId());
        refill.setStartDate(firstDate);
        refill.setEndDate(secondDate);
        ref.child("Refill").child(mAuth.getUid()).child(String.valueOf(refill.getId())).setValue(refill).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                Toast.makeText(RefillDetailActivity.this,"Refill updated",Toast.LENGTH_LONG).show();
                Thread thread=new Thread(() -> appDatabase.refillDao().update(refill));
                thread.start();
                //alarmReceiver.setAlarm(AddRefilActivity.this,calendar, (int) refill.getId(), ReminderType.REFILL_REMINDER);

            }
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        selectMenu(menu);
        return true;
    }
    private void selectMenu(Menu menu){
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        if(isEditMode){
            inflater.inflate(R.menu.save_menu, menu);
        }
        else {
            inflater.inflate(R.menu.edit_menu, menu);
        }
    }
}