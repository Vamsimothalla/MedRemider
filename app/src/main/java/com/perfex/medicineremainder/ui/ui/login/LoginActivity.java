package com.perfex.medicineremainder.ui.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.perfex.medicineremainder.MainActivity;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.SignUpActivity;
import com.perfex.medicineremainder.adapter.SliderAdapter;
import com.perfex.medicineremainder.adapter.SliderData;
import com.perfex.medicineremainder.databinding.LoginScreenBinding;
import com.perfex.medicineremainder.utils.Validator;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private LoginScreenBinding binding;
    private FirebaseAuth mAuth;
    private ArrayList<SliderData> sliderDataArrayList=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding = LoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sliderDataArrayList.add(new SliderData(R.drawable.slide_1));
        sliderDataArrayList.add(new SliderData(R.drawable.slide_2));
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);
        binding.slider.setSliderAdapter(adapter);
        binding.slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        binding.slider.setScrollTimeInSec(3);
        binding.slider.setAutoCycle(true);
        binding.slider.startAutoCycle();
        loginOnClick();
        inputChange();
        signUpOnclick();
    }
    private void signUpOnclick() {
        binding.signUp.setOnClickListener(v -> {
            Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("ResourceType")
    private void loginButtonStyle() {
        if (binding.password.getText().length() > 0 && binding.email.getText().length() > 0) {
            if (!binding.loginButton.isFocusable()) {
                binding.loginButton.setFocusable(true);
                binding.loginButton.setClickable(true);
                binding.loginButtonCardView.setCardBackgroundColor(Color.parseColor(getString(R.color.colorAccent)));
                TypedValue outValue = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                binding.loginButton.setBackgroundResource(outValue.resourceId);
            }
        } else {
            if (binding.loginButton.isFocusable()) {
                binding.loginButton.setFocusable(false);
                binding.loginButton.setClickable(false);
                binding.loginButtonCardView.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCardViewBackground)));
                binding.loginButton.setBackgroundResource(0);
            }
        }
    }

    private void forgotPasswordOnClick() {
        binding.forgotPasswordTextView.setOnClickListener(view -> Toast.makeText(LoginActivity.this, getString(R.string.forgot_password), Toast.LENGTH_LONG).show());
    }

    private void loginOnClick() {
        binding.loginButton.setOnClickListener(view -> {
            if(!Validator.isEmailValid(binding.email)) return;
            if(!Validator.isPasswordValid(binding.password)) return;
            if (binding.email.getText().length() > 0 && binding.password.getText().length() > 0) {
                mAuth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
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
                loginButtonStyle();
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
                loginButtonStyle();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent =new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }
}