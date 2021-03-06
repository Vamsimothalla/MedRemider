package com.perfex.medicineremainder.database.user;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converter {

        @TypeConverter
        public static ArrayList<ArrayList<String>> fromStrings(String value) {
            Type listType = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public static String fromArrayLists(ArrayList<ArrayList<String>> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType =new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
