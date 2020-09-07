package com.salab.project.kakikana.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.salab.project.kakikana.model.CommonUse;
import com.salab.project.kakikana.model.jisho.JishoData;
import com.salab.project.kakikana.model.jisho.JishoJapanese;
import com.salab.project.kakikana.model.jisho.JishoResponse;
import com.salab.project.kakikana.model.jisho.JishoSense;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * The class is responsible to fetch from Jisho.org api and parse into usable format.
 */
public class JishoApiUtil {

    // constants
    private static final String TAG = JishoApiUtil.class.getSimpleName();
    public static final String BASE_URL = "https://jisho.org/api/v1/";

    private static Retrofit retrofitInstance;

    interface JishoApiService {
        // format : https://jisho.org/api/v1/search/words?keyword=a
        @GET("search/words")
        Call<JishoResponse> getKeywordSearchResult(@Query("keyword") String keyword);
    }

    private static Retrofit getRetrofitInstance() {

        if (retrofitInstance == null) {
            Gson gson = new GsonBuilder()
                    .setLenient() // allow imperfect JSON parse
                    .create();

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitInstance;
    }

    public static List<CommonUse> fetchWordsFromApi(String keyword, int numCommonUse) {

        JishoApiService service = getRetrofitInstance().create(JishoApiService.class);
        Call<JishoResponse> call = service.getKeywordSearchResult(keyword);

        try {
            Response<JishoResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                List<JishoData> jishoDataList = response.body().getDataList();
                Log.d(TAG, "fetching commonly used word with keyword: " + keyword + " is successful.");
                return parseJishoDataList(numCommonUse, jishoDataList, keyword);
            }

        } catch (IOException e) {
            Log.d(TAG, "Fetch common word from network failed. " + e.getMessage());
        } catch (JsonSyntaxException e) {
            Log.d(TAG, "Failed to parse JSON into POJOs." + e.getMessage());
        }

        return null;
    }

    private static List<CommonUse> parseJishoDataList(int numCommonUse, List<JishoData> jishoDataList, String keyword) {
        // parse result and form a usable CommonWord list
        final List<CommonUse> commonUseList = new ArrayList<>();

        if (jishoDataList != null && !jishoDataList.isEmpty()){
            for (JishoData data : jishoDataList){
                if (!data.isCommon()){
                    // filter out un-common words
                    continue;
                }

                JishoJapanese japanese = null;
                JishoSense sense = null;

                if (!data.getJapanese().isEmpty()){
                    japanese = data.getJapanese().get(0);
                    if (!japanese.getReading().contains(keyword)){
                        // reject the result without keywords in its reading (related to keyword in some other parts)
                        continue;
                    }
                }

                if (!data.getSenses().isEmpty()){
                    sense = data.getSenses().get(0);
                }

                commonUseList.add(new CommonUse(japanese, sense));

                if (commonUseList.size() >= numCommonUse){
                    // stop parsing once the required number is reached
                    break;
                }
            }
        }
        return commonUseList;
    }
}
