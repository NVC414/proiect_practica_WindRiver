package com.windriver.pcgate.ui.LoginRegister.Register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.windriver.pcgate.Databases.UserHelperClass;
import com.windriver.pcgate.MainActivity;
import com.windriver.pcgate.R;
import com.windriver.pcgate.ui.LoginRegister.Login_activity;

public class VerifyOTP extends AppCompatActivity {
    // Class member variables
    String fullName, phoneNumber, username, password, email, date, gender, Occupation;
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
        this.Occupation = getIntent().getStringExtra("occupation");

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
                this.gender,
                this.Occupation
        );

        // Store data under the User's email
        reference.child(email).setValue(userProfileData)
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