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

    private FirebaseAuth mAuth;
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        mAuth = FirebaseAuth.getInstance();








        if (binding.myOrdersButton != null)
        {
            setupButtonFeedback(binding.myOrdersButton);
        }
        if (binding.callSupportButton != null)
        {
            setupButtonFeedback(binding.callSupportButton);
        }

        return root;
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
        {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null)
        {
            if (binding.userDetails != null)
            {
                binding.userDetails.setText(currentUser.getEmail());
            }
        }
        else
        {
            if (binding.userDetails != null)
            {
                binding.userDetails.setText("Not Logged In");
            }
        }


        if (binding.logout != null)
        {
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
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        animateButton(v, 1.0f);
                        break;
                }
                return false;
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
        binding = null;
        }
    }