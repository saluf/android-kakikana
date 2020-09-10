package com.salab.project.kakikana.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.model.CommonUse;
import com.salab.project.kakikana.model.Kana;
import com.salab.project.kakikana.repository.Repository;

import java.util.List;

/**
 * A Shared ViewModel holds furigana data for KanaList and KanaDetail Fragments.
 */
public class KanaViewModel extends AndroidViewModel {

    // constants
    private static final String TAG = KanaViewModel.class.getSimpleName();
    public static final int NUM_COMMON_WORDS = 10;

    private Repository repository;
    private LiveData<List<Kana>> kanaList;
    private MutableLiveData<Kana> selectedKana;
    private LiveData<List<CommonUse>> commonWordList;
    private LiveData<DataSnapshot> userKana;

    public KanaViewModel(Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
        kanaList = repository.getKanaList();
        selectedKana = new MutableLiveData<>();
    }

    public void setSelectedKanaById(int kanaId) {
        // KanaDetail notifies that a kana is selected. Load the data into LiveData and respond to UI.

        if (selectedKana.getValue() != null && kanaId == selectedKana.getValue().getId()) {
            // if the same one is selected, skip data fetching processes
            return;
        }
        Kana selectedKanaValue = findKanaById(kanaId);
        selectedKana.setValue(selectedKanaValue);
        if (selectedKanaValue != null) {
            commonWordList = repository.getCommonUse(selectedKanaValue.getKana(), NUM_COMMON_WORDS);
            userKana = repository.getUserKana(selectedKanaValue.getId());
        }
    }

    private Kana findKanaById(int kanaId) {
        // binary search to find Kana with id == kanaId
        List<Kana> kanas = kanaList.getValue();
        if (kanas == null) {
            return null;
        }
        int l = 0;
        int r = kanas.size();
        int mid;
        Kana midKana;

        while (r >= l) {
            mid = (l + r) / 2;
            midKana = kanas.get(mid);
            if (midKana.getId() == kanaId) {
                return midKana;
            }
            if (kanaId > midKana.getId()) {
                l = mid;
            } else {
                r = mid;
            }
        }
        return null;
    }

    public LiveData<List<Kana>> getKanaList() {
        return kanaList;
    }

    public LiveData<Kana> getSelectedKana() {
        return selectedKana;
    }

    public LiveData<List<CommonUse>> getCommonWordList() {
        return commonWordList;
    }

    public LiveData<DataSnapshot> getUserKana() {
        return userKana;
    }
}
