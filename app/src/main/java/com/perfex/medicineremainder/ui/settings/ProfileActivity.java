package com.perfex.medicineremainder.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.databinding.ActivityProfileBinding;
import com.perfex.medicineremainder.model.User;
import com.perfex.medicineremainder.utils.ViewGroupUtil;

import java.util.ArrayList;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    private DatabaseReference ref = database.getReference();
    private ActivityProfileBinding binding;
    private boolean isEditMode=false;
    private ArrayList<EditText> oldEditText,newEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        oldEditText = new ArrayList<>();
        newEditText = new ArrayList<>();
        findAllEditTexts();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        email = firebaseUser.getEmail();
        binding.email.setText(email);
        ref.child("User").child(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user=task.getResult().getValue(User.class);
                if (user != null) {
                    binding.phoneNumber.setText(user.getPhoneNumber());
                    binding.name.setText(user.getName());
                }

            }
        });
    }
    private void findAllEditTexts() {
        oldEditText.add(binding.name);
        oldEditText.add(binding.email);
        oldEditText.add(binding.phoneNumber);
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
        if (!email.toLowerCase(Locale.ROOT).equals(binding.email.getText().toString().toLowerCase(Locale.ROOT))){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(this);
            alert.setMessage("Your about to change email");
            alert.setTitle("Enter password");
            alert.setView(edittext);
            alert.setPositiveButton("Save", (dialog, whichButton) -> {
                String password = edittext.getText().toString();
                AuthCredential authCredential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),password);
                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        firebaseUser.updateEmail(binding.email.getText().toString());
                    }
                });
            });
            alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            });
            alert.show();
        }
        User user=new User();
        user.setUid(firebaseUser.getUid());
        user.setEmail(binding.email.getText().toString());
        user.setPhoneNumber(binding.phoneNumber.getText().toString());
        user.setName(binding.name.getText().toString());
        ref.child("User").child(firebaseAuth.getUid()).setValue(user).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Snackbar.make(binding.getRoot(),"Profile Updated",Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(binding.getRoot(),"Failed to Update",Snackbar.LENGTH_LONG).show();
            }
        });

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