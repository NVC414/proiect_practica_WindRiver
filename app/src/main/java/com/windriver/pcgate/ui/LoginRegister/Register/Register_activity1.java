package com.windriver.pcgate.ui.LoginRegister.Register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.windriver.pcgate.MainActivity;
import com.windriver.pcgate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.windriver.pcgate.ui.LoginRegister.Login_activity;

public class Register_activity1 extends AppCompatActivity {
    Button signup_next_button;
    TextInputEditText editTextEmail, editTextPassword;
    TextInputLayout textInputLayoutEmail, textInputLayoutPassword, textInputLayoutName, textInputLayoutPhone, textInputLayoutUsername;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    ImageView ButtonBack;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        textInputLayoutEmail = findViewById(R.id.emailLayout);
        textInputLayoutPassword = findViewById(R.id.passwordLayout);
        textInputLayoutName = findViewById(R.id.Name_Layout);
        textInputLayoutPhone = findViewById(R.id.PhoneNumberLayout);
        textInputLayoutUsername = findViewById(R.id.Username_Layout);
        ButtonBack = findViewById(R.id.ButtonBack);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login_activity.class);
                startActivity(intent);
                finish();
            }
        });


        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login_activity.class);
                startActivity(intent);
                finish();
            }
        });
        signup_next_button = findViewById(R.id.signup_next_button);
        signup_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Step 1: Validate all fields from this activity
                // Use && for short-circuiting: if one fails, don't check others unnecessarily.
                // Also, call all validation methods to ensure all errors are shown at once if multiple fields are wrong.
                boolean isPhoneValid = validatePhoneNumber();
                boolean isNameValid = validateFullName();
                boolean isUsernameValid = validateUsername();
                boolean isEmailValid = validateEmail();
                boolean isPasswordValid = validatePassword();

                if (!isPhoneValid || !isNameValid || !isUsernameValid || !isEmailValid || !isPasswordValid) {
                    // If any validation fails, Toast a general message or rely on individual field errors
                    Toast.makeText(Register_activity1.this, "Please correct the errors.", Toast.LENGTH_SHORT).show();
                    return; // Exit onClick
                }

                // If all validations pass, proceed to create user
                progressBar.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String name = textInputLayoutName.getEditText().getText().toString();
                String phone = textInputLayoutPhone.getEditText().getText().toString();
                String username = textInputLayoutUsername.getEditText().getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // User created successfully
                                    Toast.makeText(Register_activity1.this, "Account created. Please verify your email.", Toast.LENGTH_LONG).show();
                                    sendEmailVerifiation(); // This will sign the user out.

                                    // Get user details from the successful task for passing
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    String userEmail = firebaseUser.getEmail(); // This is the confirmed email from Auth

                                    Intent intent = new Intent(getApplicationContext(), Register_activity2.class);
                                    // Pass data collected in THIS activity and from Auth
                                    intent.putExtra("email", userEmail);    // Pass the email used for auth
                                    intent.putExtra("name", name);
                                    intent.putExtra("phone", phone);
                                    intent.putExtra("username", username);
                                    // DO NOT pass the raw password further unless absolutely necessary for a non-DB purpose
                                    // and even then, handle with extreme care. VerifyOTP does not seem to need it for UserHelperClass.

                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Authentication failed
                                    Toast.makeText(Register_activity1.this, "Authentication failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void sendEmailVerifiation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Register_activity1.this, "Verification email sent to "+user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
    }

    private boolean validateFullName() {
        String val = textInputLayoutName.getEditText().getText().toString();
        if (val.isEmpty()) {
            textInputLayoutName.setError("Field cannot be empty");
            return false;
        } else {
            textInputLayoutName.setError(null);
            textInputLayoutName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUsername() {
        String val = textInputLayoutUsername.getEditText().getText().toString();
        String checkspaces = "([a-zA-Z]*(\\s)*[\\.\\,]*)*";
        if (val.isEmpty()) {
            textInputLayoutUsername.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 20) {
            textInputLayoutUsername.setError("Username invalid!");
            return false;
        } else if (!val.matches(checkspaces)) {
            textInputLayoutUsername.setError("No white spaces are allowed");
            return false;
        } else {
            textInputLayoutUsername.setError(null);
            textInputLayoutUsername.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = textInputLayoutEmail.getEditText().getText().toString();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            textInputLayoutEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            textInputLayoutEmail.setError("Invalid Email!");
            return false;
        } else {
            textInputLayoutEmail.setError(null);
            textInputLayoutEmail.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePhoneNumber() {
        String val = textInputLayoutPhone.getEditText().getText().toString();
        String checkPhone = "[0]+[0-9]+[0-9]+[0-9]+[0-9]+[0-9]+[0-9]+[0-9]+[0-9]+[0-9]";
        if (val.isEmpty()) {
            textInputLayoutPhone.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(checkPhone)) {
            textInputLayoutPhone.setError("Invalid Phone Number!");
            return false;
        } else {
            textInputLayoutPhone.setError(null);
            textInputLayoutPhone.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePassword() {
        String val = textInputLayoutPassword.getEditText().getText().toString();
        String checkPassword = "^" +
                "(?=.*[0-9])" +    //at least 1 digit
                "(?=.*[a-z])" +    //at least 1 lower case letter
                "(?=.*[A-Z])" +    //at least 1 upper case letter
                "(?=.*[@!#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +    //no white spaces
                ".{3,}" +    //at least 4 characters
                "$";
        if (val.isEmpty()) {
            textInputLayoutPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(checkPassword)) {
            textInputLayoutPassword.setError("Password should contain at least 4 characters,\n including uppercase, lowercase, digit and special character.");
            return false;
        } else {
            textInputLayoutPassword.setError(null);
            textInputLayoutPassword.setErrorEnabled(false);
            return true;
        }
    }
}
