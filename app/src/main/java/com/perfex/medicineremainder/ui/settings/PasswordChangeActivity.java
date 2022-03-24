package com.perfex.medicineremainder.ui.settings;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.databinding.ActivityPasswordChangeBinding;
import com.perfex.medicineremainder.utils.Validator;

import java.util.Objects;

public class PasswordChangeActivity extends AppCompatActivity {

    ActivityPasswordChangeBinding binding;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordChangeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Change Password </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        firebaseAuth = FirebaseAuth.getInstance();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        if(item.getItemId()==R.id.save){
            Log.d("TAG", "onOptionsItemSelected: ");
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (!Validator.isPasswordValid(binding.currentPassword)) return false;
            if (!Validator.isPasswordValid(binding.newPassword)) return false;
            if (!Validator.isPasswordValid(binding.reenterPassword)) return false;
            if (binding.newPassword.getText().toString().equals(binding.reenterPassword.getText().toString())){
                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),binding.currentPassword.getText().toString());
                firebaseUser.reauthenticate(credential).addOnCompleteListener(task1 ->
                        firebaseUser.updatePassword(binding.newPassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            Log.d("TAG", "onOptionsItemSelected: "+task.isSuccessful());
                            Snackbar.make(binding.getRoot(),"Updated Successfully",Snackbar.LENGTH_LONG).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getMessage()),Snackbar.LENGTH_LONG).show()));
            }
            else {
                Snackbar.make(binding.getRoot(),"Password miss-match",Snackbar.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}