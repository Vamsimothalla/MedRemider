package com.perfex.medicineremainder.utils;

import android.widget.EditText;

import androidx.annotation.NonNull;

public class Validator {
    public final static String EMAIL_REGEX="^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public final static String NAME_REX = "^[a-zA-Z][ ]*$";

    public static boolean isEmailValid(@NonNull String email){
        return email.matches(EMAIL_REGEX);
    }
    public static boolean isNameValid(@NonNull String email){
        return email.matches(EMAIL_REGEX);
    }
    public static boolean isEmailValid(@NonNull EditText email){
        boolean b = email.getText().toString().matches(EMAIL_REGEX);
        if(!b) email.setError("Enter a valid email address");
        return b;
    }

    public static boolean isPasswordValid(@NonNull String password){

        return password.length()>6;
    }
    public static boolean isPasswordValid(@NonNull EditText password){
        boolean b = password.getText().toString().length()>=6;
        if(!b) password.setError("Password must be more than 6 character");
        return password.length()>=6;
    }
    public static boolean isEditTextValid(@NonNull EditText editText){
        boolean b = editText.getText().toString().length()>0;
        if(!b) editText.setError("This field required");
        return editText.length()>0;
    }
}
