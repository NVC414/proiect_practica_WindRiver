package com.windriver.pcgate.ui.loginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.windriver.pcgate.MainActivity;
import com.windriver.pcgate.R;
import com.windriver.pcgate.ui.loginRegister.register.Register_activity1;

public class Login_activity extends AppCompatActivity
    {
    TextInputEditText editTextEmail, editTextPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart()
        {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            Intent intent = new Intent(Login_activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);
        textView.setOnClickListener(view ->
            {
                Intent intent = new Intent(getApplicationContext(), Register_activity1.class);
                startActivity(intent);
                finish();
            });


        btnLogin.setOnClickListener(view ->
            {
                progressBar.setVisibility(View.VISIBLE);
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(Login_activity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(Login_activity.this, "Enter password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task ->
                    {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful())
                        {
                            if (mAuth.getCurrentUser().isEmailVerified())
                            {
                                Toast.makeText(getApplicationContext(), "Login Successful",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),
                                        MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),
                                        "Verify your email before proceeding",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(Login_activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

            });
        }
    }
