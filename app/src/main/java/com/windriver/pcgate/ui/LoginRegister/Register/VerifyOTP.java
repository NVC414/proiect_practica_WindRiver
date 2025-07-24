package com.windriver.pcgate.ui.LoginRegister.Register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.windriver.pcgate.Databases.UserHelperClass;
import com.windriver.pcgate.MainActivity;
import com.windriver.pcgate.R;

public class VerifyOTP extends AppCompatActivity {
    // Class member variables
    String fullName, phoneNumber, username, password, email, date, gender,UserUID;
    FirebaseAuth mAuth;
    Button buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        // --- Assign directly to class member variables ---
        // 'name' from intent should likely go to 'fullName' class member
        this.fullName = getIntent().getStringExtra("name"); // Corrected to assign to class member 'fullName'

        // For the rest, ensure you assign to the class members, not new local variables
        this.username = getIntent().getStringExtra("username");
        this.email = getIntent().getStringExtra("email");
        this.password = getIntent().getStringExtra("password"); // Still advise against storing raw password
        this.phoneNumber = getIntent().getStringExtra("phone");
        this.gender = getIntent().getStringExtra("gender");
        this.date = getIntent().getStringExtra("date");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            UserUID = mAuth.getCurrentUser().getUid();
                        }
                    }
                });




        buttons = findViewById(R.id.Verify_button);
        buttons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Now storeNewUsersData() will use the correctly populated class member variables
                storeNewUsersData();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // Finish VerifyOTP after starting MainActivity
            }
        });
    }

    private void storeNewUsersData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            // Cannot proceed without a logged-in user to get the UID
            Toast.makeText(this, "Error: No authenticated user found to save data.", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = firebaseUser.getUid();

        // --- Database Operation ---
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users"); // Root node for all users

        // Create user data object (ensure UserHelperClass does NOT take/store password)
        UserHelperClass userProfileData = new UserHelperClass(
                this.fullName,
                this.phoneNumber, // Storing phone number as a field
                this.username,
                this.email,
                this.date,
                this.gender
        );

        // Store data under the User's UID
        reference.child(UserUID).setValue(userProfileData)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully written
                    // The navigation to MainActivity is already handled in the onClickListener
                    // You could show a success Toast here if desired, e.g.:
                    // Toast.makeText(VerifyOTP.this, "Profile data saved.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to write data
                    Toast.makeText(this, "Failed to save profile data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Consider if you still want to navigate to MainActivity if saving fails.
                });
    }

}