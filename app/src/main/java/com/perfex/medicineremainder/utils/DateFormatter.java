package com.perfex.medicineremainder.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public static int[] dateInInt(String dateSt) throws Exception {
        String pattern = "MM/dd/yyyy";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(pattern);
        Date date =simpleDateFormat.parse(dateSt);
        return new int[]{date.getYear(),date.getMonth()+1,date.getDate()};

    }
}
