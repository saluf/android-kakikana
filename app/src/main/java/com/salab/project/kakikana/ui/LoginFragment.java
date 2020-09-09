package com.salab.project.kakikana.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.salab.project.kakikana.R;
import com.salab.project.kakikana.databinding.FragmentLoginBinding;
import com.salab.project.kakikana.viewmodel.UserViewModel;

/**
 * This application applies single-activity architecture. The Activity are responsible to
 * host NavHost (container of destinations) and global components like appbar and bottom navigation.
 */

public class LoginFragment extends Fragment {

    // constants
    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final int REQUEST_CODE_SIGN_IN = 100;

    // global variables
    private FragmentLoginBinding mBinding;
    private UserViewModel mViewModel;

    public LoginFragment() {
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
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup Activity-scoped UserViewModel
        ViewModelProvider.Factory factory = new ViewModelProvider.NewInstanceFactory();
        mViewModel = new ViewModelProvider(requireActivity(), factory).get(UserViewModel.class);

        mViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                // not logged in
                setupButtonOnClickResponse();
                Log.d(TAG, "User has not logged in yet.");
            } else {
                // logged in
                mViewModel.loadUserData();
                redirectToHome();
                Log.d(TAG, "Logged in as " + user.getUid());
            }
        });

    }

    private void setupButtonOnClickResponse() {
        mBinding.btnSignInAnonymous.setOnClickListener(v -> mViewModel.signIn());

        mBinding.btnSignInGoogle.setOnClickListener(v -> requestGoogleAuth());
    }

    private void requestGoogleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_client_id)) //Firebase client ID
                .requestEmail() //request email
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null){
                    String idToken = account.getIdToken();
                    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                    mViewModel.signIn(credential);
                    Log.d(TAG, "Google sign in is successful");
                }

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(requireContext(), "Google sign in failed. Please try again.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Google sign in failed" + e.getMessage());
            }
        }
    }

    private void redirectToHome() {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToQuizListDest();
        NavHostFragment.findNavController(this).navigate(action);
    }
}