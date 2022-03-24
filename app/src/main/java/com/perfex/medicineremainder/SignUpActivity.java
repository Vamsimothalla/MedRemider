package com.perfex.medicineremainder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.perfex.medicineremainder.adapter.SliderAdapter;
import com.perfex.medicineremainder.adapter.SliderData;
import com.perfex.medicineremainder.databinding.ActivitySignUpBinding;
import com.perfex.medicineremainder.databinding.SignUpScreenBinding;
import com.perfex.medicineremainder.ui.ui.login.LoginActivity;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private SignUpScreenBinding binding;
    private FirebaseAuth mAuth;
    private final ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignUpScreenBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        sliderDataArrayList.add(new SliderData(R.drawable.slide_1));
        sliderDataArrayList.add(new SliderData(R.drawable.slide_2));
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);
        binding.slider.setSliderAdapter(adapter);
        binding.slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        binding.slider.setScrollTimeInSec(2);
        binding.slider.setAutoCycle(true);
        binding.slider.startAutoCycle();
        signUpOnClick();
        inputChange();
        loginOnClick();
    }

    private void loginOnClick() {
            binding.signUp.setOnClickListener(v -> {
                Intent intent=new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            });
    }

    @SuppressLint("ResourceType")
    private void signUpButtonStyle() {
        if (binding.password.getText().length() > 0 && binding.email.getText().length() > 0 && binding.name.getText().length()>0) {
            if (!binding.signUpButton.isFocusable()) {
                binding.signUpButton.setFocusable(true);
                binding.signUpButton.setClickable(true);
                binding.signUpButtonCardView.setCardBackgroundColor(Color.parseColor(getString(R.color.colorAccent)));
                TypedValue outValue = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                binding.signUpButton.setBackgroundResource(outValue.resourceId);
            }
        } else {
            if (binding.signUpButton.isFocusable()) {
                binding.signUpButton.setFocusable(false);
                binding.signUpButton.setClickable(false);
                binding.signUpButtonCardView.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCardViewBackground)));
                binding.signUpButton.setBackgroundResource(0);
            }
        }
    }

    private void signUpOnClick() {
        binding.signUpButton.setOnClickListener(view -> {
            if (binding.email.getText().length() > 0 && binding.password.getText().length() > 0 && binding.name.getText().length()>0) {
                mAuth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void inputChange() {
        binding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                signUpButtonStyle();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                signUpButtonStyle();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                signUpButtonStyle();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}