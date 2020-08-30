package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.salab.project.kakikana.NavGraphMainDirections;
import com.salab.project.kakikana.adpater.KanaListAdapter;
import com.salab.project.kakikana.databinding.FragmentKanaListBinding;
import com.salab.project.kakikana.model.Kana;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to list all kanas.
 */
public class KanaListFragment extends Fragment implements KanaListAdapter.onKanaItemClickListener{

    //constants
    private static final String TAG = KanaListFragment.class.getSimpleName();
    public static final int SPAN_COUNT = 5; // furigana has 5 different vowels, a standard way to display

    // global variables
    private FragmentKanaListBinding mBinding;

    public KanaListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentKanaListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        sample data
        final List<Kana> kanaList = new ArrayList<>();
        kanaList.add(new Kana(1001, "hiragana", "あ", "a"));
        kanaList.add(new Kana(1002, "hiragana", "い", "i"));
        kanaList.add(new Kana(1003, "hiragana", "う", "u"));
        kanaList.add(new Kana(1004, "hiragana", "え", "e"));
        kanaList.add(new Kana(1005, "hiragana", "", ""));
        kanaList.add(new Kana(1006, "hiragana", "か", "ka"));

        final List<Kana> kanaList2 = new ArrayList<>();
        kanaList2.add(new Kana(2001, "katakana", "ア", "a"));
        kanaList2.add(new Kana(2002, "katakana", "イ", "i"));
        kanaList2.add(new Kana(2003, "katakana", "ウ", "u"));
        kanaList2.add(new Kana(2004, "katakana", "エ", "e"));
        kanaList2.add(new Kana(2005, "katakana", "", ""));
        kanaList2.add(new Kana(2006, "katakana", "カ", "ka"));

        // TODO: move this logic into repository and run in background thread
//        String jsonString = IOUtil.readJsonFromRawToString(requireContext(), R.raw.kana_table);
//        Gson gson = new Gson();
//        Type kanaListType = new TypeToken<ArrayList<Kana>>(){}.getType();
//        final List<Kana> kanaList3 = gson.fromJson(jsonString, kanaListType);

        // setup recycler view
        final KanaListAdapter kanaListAdapter = new KanaListAdapter(kanaList, this);
        mBinding.rvKanaList.setAdapter(kanaListAdapter);
        mBinding.rvKanaList.setLayoutManager(new GridLayoutManager(requireContext(), SPAN_COUNT));
        mBinding.rvKanaList.setHasFixedSize(false);

        // TODO: move data slice logic into ViewModel, UI only need to notify the ViewModel
        // Provide adapter difference data set depending on the selected tab
        mBinding.tabLayoutKanaList.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    kanaListAdapter.setmKanaList(kanaList);
                } else {
                    kanaListAdapter.setmKanaList(kanaList2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onKanaItemClick(int kanaId) {
        // navigate to detail page of the clicked kana
        NavGraphMainDirections.ActionGlobalKanaDetailFragment action = NavGraphMainDirections.actionGlobalKanaDetailFragment();
        action.setKanaId(kanaId);
        NavHostFragment.findNavController(this).navigate(action);

    }
}