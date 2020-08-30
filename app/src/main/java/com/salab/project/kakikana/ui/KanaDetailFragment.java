package com.salab.project.kakikana.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.salab.project.kakikana.databinding.FragmentKanaDetailBinding;

/**
 * Fragment to show detail of each kana, including sequence of writing, statistics, and common usage.
 */
public class KanaDetailFragment extends Fragment {

    //constants
    private static final String TAG = KanaDetailFragment.class.getSimpleName();

    // global variables
    private FragmentKanaDetailBinding mBinding;

    public KanaDetailFragment() {
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
        mBinding = FragmentKanaDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get passed kana Id
        int kanaId = KanaDetailFragmentArgs.fromBundle(getArguments()).getKanaId();
        Toast.makeText(requireContext(), kanaId + "", Toast.LENGTH_SHORT).show();
    }
}