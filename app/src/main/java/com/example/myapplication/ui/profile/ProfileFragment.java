package com.example.myapplication.ui.profile;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment
    {

    private FragmentProfileBinding binding;    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ImageButton settingsButton = root.findViewById(R.id.myOrdersButton);
        ImageButton callButton = root.findViewById(R.id.callSupportButton);

        setupButtonFeedback(settingsButton);
        setupButtonFeedback(callButton);

                return root;
    }
    private void setupButtonFeedback(ImageButton button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animateButton(v, 0.9f);
                    break;
                case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL:
                    animateButton(v, 1.0f);
                    break;
            }
            return false;
        });
    }

    private void animateButton(View view, float scale) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        scaleX.setDuration(100);
        scaleY.setDuration(100);
        scaleX.start();
        scaleY.start();
    }@Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
    }