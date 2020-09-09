package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.salab.project.kakikana.NavGraphMainDirections;
import com.salab.project.kakikana.R;
import com.salab.project.kakikana.adpater.KanaListAdapter;
import com.salab.project.kakikana.databinding.FragmentKanaListBinding;
import com.salab.project.kakikana.viewmodel.KanaViewModel;

import java.util.ArrayList;

/**
 * Fragment to list all kanas.
 */
public class KanaListFragment extends Fragment implements KanaListAdapter.onKanaItemClickListener{

    //constants
    private static final String TAG = KanaListFragment.class.getSimpleName();
    public static final int SPAN_COUNT = 5; // furigana has 5 different vowels, a standard way to display
    public static final String KEY_SELECTED_TAB_POSITION = "key_selected_tab_position";

    // global variables
    private FragmentKanaListBinding mBinding;
    private int selectedTabPosition = 0;

    public KanaListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            // save tab position to survive system kill
            if (savedInstanceState.containsKey(KEY_SELECTED_TAB_POSITION)){
                selectedTabPosition = savedInstanceState.getInt(KEY_SELECTED_TAB_POSITION);
            }
        }
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

        // setup RecyclerView
        final KanaListAdapter kanaListAdapter = new KanaListAdapter(new ArrayList<>(), this);
        mBinding.contentKanaList.rvKanaList.setAdapter(kanaListAdapter);
        mBinding.contentKanaList.rvKanaList.setLayoutManager(new GridLayoutManager(requireContext(), SPAN_COUNT));
        mBinding.contentKanaList.rvKanaList.setHasFixedSize(false);

        // setup ViewModel (shared with KanaDetail) by scoping to BackStackEntry
        // ref: https://developer.android.com/guide/navigation/navigation-programmatic#share_ui-related_data_between_destinations_with_viewmodel
        NavBackStackEntry kanaListBackStackEntry =
                NavHostFragment.findNavController(this).getBackStackEntry(R.id.kana_list_dest);
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        KanaViewModel viewModel = new ViewModelProvider(kanaListBackStackEntry, factory).get(KanaViewModel.class);

        viewModel.getKanaList().observe(getViewLifecycleOwner(), kanaListAdapter::setmKanaList);

        // Setup TabLayout
        mBinding.contentKanaList.tabLayoutKanaList.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Provide adapter difference options depending on which tab selected
                Log.d(TAG, "tab change");
                switch (tab.getPosition()) {
                    case 0:
                        selectedTabPosition = 0;
                        kanaListAdapter.setIsHiragana(true);
                        break;
                    case 1:
                        selectedTabPosition = 1;
                        kanaListAdapter.setIsHiragana(false);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // Switch tab to saved tab position
        TabLayout.Tab selectedTab = mBinding.contentKanaList.tabLayoutKanaList.getTabAt(selectedTabPosition);
        if (selectedTab != null) {selectedTab.select();}
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save selected tab position
        outState.putInt(KEY_SELECTED_TAB_POSITION, selectedTabPosition);
    }

    @Override
    public void onKanaItemClick(int kanaId) {
        // navigate to detail page of the clicked kana
        NavGraphMainDirections.ActionGlobalKanaDetailFragment action = NavGraphMainDirections.actionGlobalKanaDetailFragment();
        action.setKanaId(kanaId);
        NavHostFragment.findNavController(this).navigate(action);

    }
}