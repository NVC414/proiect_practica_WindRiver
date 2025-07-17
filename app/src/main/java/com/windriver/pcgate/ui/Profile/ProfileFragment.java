package com.windriver.pcgate.ui.Profile;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.windriver.pcgate.databinding.FragmentProfileBinding;
import com.windriver.pcgate.ui.LoginRegister.Login_activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment
    {
    // userEmailTextView and logoutButton will be accessed via binding
    private FirebaseAuth mAuth;
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Assuming your IDs in fragment_profile.xml (accessed via binding) are:
        // TextView: userEmailText (e.g., binding.userEmailText)
        // Button:   logoutBtn (e.g., binding.logoutBtn)
        // ImageButton: myOrdersButton (binding.myOrdersButton)
        // ImageButton: callSupportButton (binding.callSupportButton)

        // Setup for existing ImageButtons if they are part of the binding
        if (binding.myOrdersButton != null)
        { // Check if the ID exists in your layout/binding
            setupButtonFeedback(binding.myOrdersButton);
        }
        if (binding.callSupportButton != null)
        { // Check if the ID exists in your layout/binding
            setupButtonFeedback(binding.callSupportButton);
        }

        return root;
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
        {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Display User Email using ViewBinding
        if (currentUser != null)
        {
            if (binding.userDetails != null)
            { // Replace 'userEmailText' with your actual ID in the binding
                binding.userDetails.setText(currentUser.getEmail());
            }
        }
        else
        {
            if (binding.userDetails != null)
            { // Replace 'userEmailText'
                binding.userDetails.setText("Not Logged In");
            }
        }

        // Setup Logout Button using ViewBinding
        if (binding.logout != null)
        { // Replace 'logoutBtn' with your actual ID in the binding
            binding.logout.setOnClickListener(v ->
                {
                    mAuth.signOut();
                    Intent intent = new Intent(getActivity(), Login_activity.class);
                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null)
                    {
                        getActivity().finish();
                    }
                });
        }
        }

    private void setupButtonFeedback(ImageButton button)
        {
        button.setOnTouchListener((v, event) ->
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        animateButton(v, 0.9f);
                        break;
                    case MotionEvent.ACTION_UP: // Fall-through for ACTION_CANCEL
                    case MotionEvent.ACTION_CANCEL:
                        animateButton(v, 1.0f);
                        break;
                }
                return false; // Return false if you want other listeners to consume the event too
            });
        }

    private void animateButton(View view, float scale)
        {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        scaleX.setDuration(100);
        scaleY.setDuration(100);
        scaleX.start();
        scaleY.start();
        }

    @Override
    public void onDestroyView()
        {
        super.onDestroyView();
        binding = null; // Important to prevent memory leaks with ViewBinding
        }
    }