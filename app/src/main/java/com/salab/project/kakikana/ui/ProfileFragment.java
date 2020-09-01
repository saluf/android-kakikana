package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.util.Log;
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
        userViewModel.getUserData().observe(requireActivity(), dataSnapshot -> {
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
        mBinding.tvProfileName.setText(user.getName());
    }
}