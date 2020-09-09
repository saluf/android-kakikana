package com.salab.project.kakikana.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.salab.project.kakikana.R;
import com.salab.project.kakikana.viewmodel.UserViewModel;

/**
 * Settings for app customization.
 **/
public class SettingFragment extends PreferenceFragmentCompat {

    // constants
    private static final String TAG = SettingFragment.class.getSimpleName();
    private static final int REQUEST_CODE_SIGN_IN = 101;

    // global variable
    private GoogleSignInClient mGoogleSignInClient;
    private UserViewModel mViewModel;
    private boolean isLinkingWithGoogle = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        mViewModel.getUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser != null) {
                updateUi(currentUser.isAnonymous());
            }
        });

    }

    private void updateUi(boolean isAnonymous) {
        // anonymous users -> show link with Google action
        // signed in with Google users -> show sign out action
        // setup callback for link with google account action

        Preference actionLinkGoogle = findPreference(getString(R.string.pref_key_action_link_google));
        Preference actionSignOut = findPreference(getString(R.string.pref_key_action_sign_out));

        if (isAnonymous) {

            // link with Google
            actionLinkGoogle.setVisible(true);
            actionSignOut.setVisible(false);
            actionLinkGoogle.setOnPreferenceClickListener(preference -> {
                requestGoogleAuth();
                return true;
            });

        } else {
            if (isLinkingWithGoogle) {
                // linking or signing successfully redirect to login to refresh cached data
                redirectToLogin();
            } else {
                // sign out
                actionLinkGoogle.setVisible(false);
                actionSignOut.setVisible(true);
                actionSignOut.setOnPreferenceClickListener(preference -> {
                    mViewModel.signOut();
                    redirectToLogin();
                    return true;
                });
            }
        }
    }

    private void redirectToLogin() {
        NavDirections action = SettingFragmentDirections.actionSettingDestToLoginFragment();
        NavHostFragment.findNavController(this).navigate(action);
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
                if (account != null) {
                    String idToken = account.getIdToken();
                    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                    isLinkingWithGoogle = true;
                    mViewModel.linkWithGoogle(credential);
                    Log.d(TAG, "Google authentication is successful");
                }

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(requireContext(), "Google authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Google sign in failed" + e.getMessage());
            }
        }
    }
}