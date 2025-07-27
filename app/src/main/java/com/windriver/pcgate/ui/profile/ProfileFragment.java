package com.windriver.pcgate.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.windriver.pcgate.R;
import com.windriver.pcgate.databinding.FragmentProfileBinding;
import com.windriver.pcgate.model.UserHelperClass;
import com.windriver.pcgate.ui.loginRegister.Login_activity;

import java.io.File;

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


        //        setupButtonFeedback(binding.myOrdersButton);
        //        setupButtonFeedback(binding.callSupportButton);

        return root;
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
        {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null)
        {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setDisplayShowCustomEnabled(true);
                View customView = LayoutInflater.from(activity).inflate(
                        R.layout.actionbar_profile_support, null);
                ImageButton callSupportButton = customView.findViewById(R.id.callSupportButton);
                callSupportButton.setOnClickListener(v1 ->
                    {
                        String phoneNumber = "0748227667";
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(intent);
                    });
                actionBar.setCustomView(customView);
            }
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(
                    uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                    UserHelperClass user = snapshot.getValue(UserHelperClass.class);
                    if (user != null)
                    {
                        String fullName = user.getFullName();
                        String firstName = fullName != null && fullName.contains(
                                " ") ? fullName.substring(0, fullName.indexOf(" ")) : fullName;
                        binding.userNameTextView.setText("Welcome, " + firstName);
                        binding.textViewFullName.setText("Full Name: " + fullName);
                        binding.textViewUsername.setText("Username: " + user.getUsername());
                        binding.textViewEmail.setText("Email: " + user.getEmail());
                        binding.textViewPhone.setText("Phone: " + user.getPhoneNumber());
                        binding.textViewDate.setText("Date of Birth: " + user.getDate());
                        binding.textViewGender.setText("Gender: " + user.getGender());
                        binding.textViewOccupation.setText("Occupation: " + user.getOccupation());
                    }
                    else
                    {
                        Toast.makeText(getContext(), "User data not found.",
                                Toast.LENGTH_SHORT).show();
                    }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                    {
                    Toast.makeText(getContext(), "Failed to load user data.",
                            Toast.LENGTH_SHORT).show();
                    }
                });
        }

        binding.logout.setOnClickListener(v ->
            {
                if (getActivity() != null)
                {
                    try
                    {
                        File cacheDir = getActivity().getCacheDir();
                        if (cacheDir != null && cacheDir.isDirectory())
                        {
                            deleteDir(cacheDir);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), Login_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null)
                {
                    getActivity().finish();
                }
            });
        }

    //    private void setupButtonFeedback(ImageButton button)
    //        {
    //        button.setOnTouchListener((v, event) ->
    //            {
    //                switch (event.getAction())
    //                {
    //                    case MotionEvent.ACTION_DOWN:
    //                        animateButton(v, 0.9f);
    //                        break;
    //                    case MotionEvent.ACTION_UP:
    //                    case MotionEvent.ACTION_CANCEL:
    //                        animateButton(v, 1.0f);
    //                        break;
    //                }
    //                return false;
    //            });
    //        }

    //    private void animateButton(View view, float scale)
    //        {
    //        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
    //        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);
    //        scaleX.setDuration(100);
    //        scaleY.setDuration(100);
    //        scaleX.start();
    //        scaleY.start();
    //        }

    private boolean deleteDir(File dir)
        {
        if (dir != null && dir.isDirectory())
        {
            String[] children = dir.list();
            assert children != null;
            for (String child : children)
            {
                boolean success = deleteDir(new File(dir, child));
                if (!success)
                {
                    return false;
                }
            }
        }
        assert dir != null;
        return dir.delete();
        }

    @Override
    public void onDestroyView()
        {
        // Remove custom ActionBar when leaving fragment
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null)
        {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setCustomView(null);
            }
        }
        super.onDestroyView();
        binding = null;
        }
    }