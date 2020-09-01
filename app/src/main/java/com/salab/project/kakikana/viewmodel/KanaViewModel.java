package com.salab.project.kakikana.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.salab.project.kakikana.model.Kana;
import com.salab.project.kakikana.repository.Repository;

import java.util.List;

/**
 * A Shared ViewModel holds furigana data for KanaList and KanaDetail Fragments.
 */
public class KanaViewModel extends AndroidViewModel {

    // constants
    private static final String TAG = KanaViewModel.class.getSimpleName();

    private Repository repository;
    private LiveData<List<Kana>> kanaList;
    private MutableLiveData<Kana> selectedKana;

    public KanaViewModel(Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
        kanaList = repository.getKanaList();
        selectedKana = new MutableLiveData<>();
    }

    public LiveData<List<Kana>> getKanaList() {
        return kanaList;
    }

    public LiveData<Kana> getSelectedKana() {
        return selectedKana;
    }

    public void setSelectedKanaById(int kanaId) {
        selectedKana.setValue(findKanaById(kanaId));
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

}
