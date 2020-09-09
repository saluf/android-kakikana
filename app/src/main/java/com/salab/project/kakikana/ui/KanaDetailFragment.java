package com.salab.project.kakikana.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.salab.project.kakikana.R;
import com.salab.project.kakikana.adpater.CommonUseAdapter;
import com.salab.project.kakikana.databinding.FragmentKanaDetailBinding;
import com.salab.project.kakikana.model.Kana;
import com.salab.project.kakikana.model.UserKana;
import com.salab.project.kakikana.viewmodel.KanaViewModel;

/**
 * Fragment to show detail of each kana, including sequence of writing, statistics, and common usage.
 */
public class KanaDetailFragment extends Fragment {

    //constants
    private static final String TAG = KanaDetailFragment.class.getSimpleName();
    private static final String KEY_SELECTED_KANA_ID = "key_selected_kana_id";

    // global variables
    private FragmentKanaDetailBinding mBinding;
    private CommonUseAdapter mAdapter;
    private int selectedKanaId;

    public KanaDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // preserve selected kana id to survive system kill
            if (savedInstanceState.containsKey(KEY_SELECTED_KANA_ID)) {
                selectedKanaId = savedInstanceState.getInt(KEY_SELECTED_KANA_ID);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentKanaDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get passed kana Id
        selectedKanaId = KanaDetailFragmentArgs.fromBundle(getArguments()).getKanaId();

        // setup commonly used words RecyclerView
        mAdapter = new CommonUseAdapter();
        mBinding.rvDetailCommonUse.setAdapter(mAdapter);
        mBinding.rvDetailCommonUse.setLayoutManager(new LinearLayoutManager(requireContext()));

        // setup ViewModel (shared with KanaDetail) by scoping to BackStackEntry
        NavBackStackEntry kanaListBackStackEntry =
                NavHostFragment.findNavController(this).getBackStackEntry(R.id.kana_list_dest);
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        KanaViewModel viewModel = new ViewModelProvider(kanaListBackStackEntry, factory).get(KanaViewModel.class);
        viewModel.getSelectedKana().observe(getViewLifecycleOwner(), this::populateHeaderUI);
        viewModel.setSelectedKanaById(selectedKanaId);

        // update commonly used words RecyclerView
        viewModel.getCommonWordList().observe(getViewLifecycleOwner(), commonWords -> {
            if (commonWords != null) {
                mAdapter.setCommonUseList(commonWords);
            }
        });

        // update user kana statistics
        viewModel.getUserKana().observe(getViewLifecycleOwner(), dataSnapshot -> {
            if (dataSnapshot != null) {
                UserKana userKana = dataSnapshot.getValue(UserKana.class);
                populateStatUI(userKana);
            }
        });
    }

    private void populateHeaderUI(Kana selectedKana) {
        mBinding.tvDetailRomaji.setText(selectedKana.getRomaji());
        mBinding.tvDetailType.setText(selectedKana.getType());

        // get resource id from filename. ex: R.drawable.stroke_hiragana_a
        int strokeImageId = getResources().getIdentifier("stroke_" + selectedKana.getType() + "_" + selectedKana.getRomaji(), "drawable", requireContext().getPackageName());
        // ImageView by default does not support GIF -> load with glide
        // ref: https://stackoverflow.com/questions/31082330/show-gif-file-with-glide-image-loading-and-caching-library

        Glide.with(this)
                .load(strokeImageId)
                .error(R.drawable.ic_image_placeholder)
                .into(mBinding.ivDetailStrokeOrder);

    }

    private void populateStatUI(UserKana userKana) {
        // statistics per kana comes from different json tree node, and therefore needs to be treat separately
        if (userKana == null) {
            // the kana is never tested before
            mBinding.tvDetailStatCorrRate.setText(getResources().getString(R.string.format_corr_rate_in_percent_rounded, 0f));
            mBinding.tvDetailStatNumCorrect.setText("0");
        } else {
            float corrRate = Math.round((float) userKana.getTotalCorrect() / userKana.getTotalTested() * 100);
            mBinding.tvDetailStatCorrRate.setText(getResources().getString(R.string.format_corr_rate_in_percent_rounded, corrRate));
            mBinding.tvDetailStatNumCorrect.setText(String.valueOf(userKana.getTotalCorrect()));
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_KANA_ID, selectedKanaId);
    }
}