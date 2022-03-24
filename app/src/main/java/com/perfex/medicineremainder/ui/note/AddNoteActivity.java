package com.perfex.medicineremainder.ui.note;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perfex.medicineremainder.AlarmReceiver;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.ReminderType;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.databinding.ActivityAddNoteBinding;
import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.model.Note;
import com.perfex.medicineremainder.ui.add.AddCheckUpActivity;
import com.perfex.medicineremainder.utils.Validator;

public class AddNoteActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = database.getReference( );
    private AppDatabase appDatabase;
    AlarmReceiver alarmReceiver=new AlarmReceiver();
    private FirebaseAuth mAuth;
    ActivityAddNoteBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteBinding.inflate(LayoutInflater.from(this));
        mAuth = FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Add Note </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
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
        if (!Validator.isEditTextValid(binding.titleEt)){
            return;
        }
        if (!Validator.isEditTextValid(binding.prescriptionEt)){
            return;
        }
        if (!Validator.isEditTextValid(binding.addressEt)){
            return;
        }
        Note note=new Note();
        note.setId(Integer.valueOf(Math.abs((int) System.currentTimeMillis())));
        note.setAddress(binding.addressEt.getText().toString());
        note.setTitle(binding.titleEt.getText().toString());
        note.setContent(binding.prescriptionEt.getText().toString());
        Log.d("TAG", "save: "+ref.toString());
        ref.child("Note").child(mAuth.getUid()).child(String.valueOf(note.getId())).setValue(note).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(AddNoteActivity.this,"Note created",Toast.LENGTH_LONG).show();
                Thread thread=new Thread(() -> appDatabase.noteDao().insert(note));
                thread.start();
                finish();
            }
            Log.d("TAG", "onComplete: "+ task);
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: "+e));
    }
}