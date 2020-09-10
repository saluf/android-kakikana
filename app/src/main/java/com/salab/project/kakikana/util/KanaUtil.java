package com.salab.project.kakikana.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salab.project.kakikana.model.Kana;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class KanaUtil {

    public static final int NUM_OF_KANA = 102;
    public static final int NUM_OF_HIRAGANA = 51;
    public static final int NUM_OF_KATAKANA = 51;

    public static String readJsonFromRawToString(Context context, int fileId) {
        // helper utility method to read raw json and pre-process into string
        // ref: https://bezkoder.com/java-android-read-json-file-assets-gson/#:~:text=put%20assets%20folder%20%26%20JSON%20file,JSON%20string%20to%20Java%20object
        try {
            InputStream inputStream = context.getResources().openRawResource(fileId);
            int size = inputStream.available(); // get number of bytes

            byte[] bytes = new byte[size]; // prepare buffer array

            inputStream.read(bytes); // read inputStream into buffer array
            inputStream.close();

            return new String(bytes);
        } catch (IOException e){
            return "";
        }
    }

    public static List<Kana> processJsonIntoKana(Context context, int fileId){
        String jsonString = readJsonFromRawToString(context, fileId);
        Gson gson = new Gson();
        Type kanaListType = new TypeToken<ArrayList<Kana>>(){}.getType();
        return gson.fromJson(jsonString, kanaListType);
    }
}
