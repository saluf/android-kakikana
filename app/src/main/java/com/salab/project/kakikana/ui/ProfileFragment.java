package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.databinding.FragmentProfileBinding;
import com.salab.project.kakikana.model.User;
import com.salab.project.kakikana.viewmodel.UserViewModel;

import java.text.DateFormat;
import java.util.Date;

/**
 * Fragment display user-level info, including account info, statistics.
 **/
public class ProfileFragment extends Fragment {

    // constants
    private static final String TAG = ProfileFragment.class.getSimpleName();

    // global variables
    private FragmentProfileBinding mBinding;

    public ProfileFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUserData().observe(getViewLifecycleOwner(), dataSnapshot -> {
            if (dataSnapshot != null){
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {setupUI(user);}
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
    }

    private void setupUI(User user) {
        mBinding.contentProfile.tvProfileName.setText(user.getName());

        // ref: https://stackoverflow.com/questions/5058880/date-and-time-formatting-depending-on-locale
        Date registerDate = new Date(user.getRegisterTime());
        DateFormat simpleFormat = android.text.format.DateFormat.getMediumDateFormat(requireContext());
        mBinding.contentProfile.tvProfileRegisterDate.setText(simpleFormat.format(registerDate));

        float corrRate = Math.round((float) user.getTotalCorrect() / user.getTotalTested() * 100);
        mBinding.contentProfile.tvProfileStatCorrRate.setText(getResources().getString(R.string.format_corr_rate_in_percent_rounded, corrRate));

        mBinding.contentProfile.tvProfileStatNumCorrect.setText(String.valueOf(user.getTotalCorrect()));
    }
}