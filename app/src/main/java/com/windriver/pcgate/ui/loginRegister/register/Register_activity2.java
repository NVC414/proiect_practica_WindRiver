package com.windriver.pcgate.ui.loginRegister.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.windriver.pcgate.R;

import java.util.Calendar;
import java.util.Locale;

public class Register_activity2 extends AppCompatActivity
    {
    // If Occupation is an EditText, use:
    // EditText occupationEditText;
    TextView textViewsOccupation; // Renamed for clarity if it's a TextView
    Button buttonNext; // Renamed for clarity
    RadioGroup radioGroupGender; // Renamed for clarity
    // selectedGender RadioButton is only needed locally in onClick
    DatePicker datePickerAge; // Renamed for clarity
    CheckBox checkboxTerms; // Reference to the terms checkbox

    // Variables to hold data passed from Register_activity1
    String emailFromReg1, nameFromReg1, phoneFromReg1, usernameFromReg1, uidFromReg1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        // Retrieve data passed from Register_activity1
        Intent incomingIntent = getIntent();
        emailFromReg1 = incomingIntent.getStringExtra("email");
        nameFromReg1 = incomingIntent.getStringExtra("name");
        phoneFromReg1 = incomingIntent.getStringExtra("phone");
        usernameFromReg1 = incomingIntent.getStringExtra("username");
        uidFromReg1 = incomingIntent.getStringExtra("uid"); // Get UID

        // Initialize UI elements
        radioGroupGender = findViewById(R.id.RadioGroup);
        datePickerAge = findViewById(R.id.AgePicker);
        // Assuming R.id.Occupation is an EditText for user input
        // If it's a TextView where you display something, this is fine,
        // but if user types into it, it should be an EditText.
        // For this example, I'll assume it's an EditText and you want its text.
        // If it's a TextView and you get its text, ensure the text is what you expect.
        textViewsOccupation = findViewById(R.id.Occupation); // Or use EditText if it's an EditText

        buttonNext = findViewById(R.id.next_button);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        // Initially disable the Next button and set transparency
        buttonNext.setEnabled(false);
        buttonNext.setAlpha(0.5f);

        checkboxTerms.setOnCheckedChangeListener((buttonView, isChecked) ->
            {
                buttonNext.setEnabled(isChecked);
                buttonNext.setAlpha(isChecked ? 1.0f : 0.5f);
            });

        buttonNext.setOnClickListener(view ->
            {
                if (!validateGender() || !validateAge())
                { // Use || for logical OR
                    return;
                }

                // Get Gender
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
                String gender = selectedGenderRadioButton.getText().toString();

                // Get Date from DatePicker
                int day = datePickerAge.getDayOfMonth();
                int month = datePickerAge.getMonth(); // Month is 0-indexed (0 for January)
                int year = datePickerAge.getYear();
                // Format the date as a string (e.g., "YYYY-MM-DD")
                // Adding 1 to month because it's 0-indexed
                String dateOfBirth = String.format(Locale.getDefault(), "%04d-%02d-%02d", year,
                        month + 1, day);

                // Get Occupation
                // If textViewsOccupation is an EditText:
                // String occupation = ((EditText) findViewById(R.id.Occupation)).getText().toString();
                // If textViewsOccupation is indeed a TextView and its text is what you want:
                String occupation = textViewsOccupation.getText().toString();


                // --- Create Intent for VerifyOTP ---
                Intent intentToVerifyOTP = getIntentToVerifyOTP(gender, dateOfBirth, occupation);

                startActivity(intentToVerifyOTP);
                finish();
            });
        }

    @NonNull
    private Intent getIntentToVerifyOTP(String gender, String dateOfBirth, String occupation)
        {
        Intent intentToVerifyOTP = new Intent(getApplicationContext(), VerifyOTP.class);

        // Pass ALL necessary data
        // Data from Register_activity1
        intentToVerifyOTP.putExtra("email", emailFromReg1);
        intentToVerifyOTP.putExtra("name", nameFromReg1);
        intentToVerifyOTP.putExtra("phone", phoneFromReg1);
        intentToVerifyOTP.putExtra("username", usernameFromReg1);
        intentToVerifyOTP.putExtra("uid", uidFromReg1);
        intentToVerifyOTP.putExtra("gender", gender);
        intentToVerifyOTP.putExtra("date", dateOfBirth);
        intentToVerifyOTP.putExtra("occupation", occupation);
        return intentToVerifyOTP;
        }

    private boolean validateGender()
        {
        if (radioGroupGender.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
        }

    private boolean validateAge()
        {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userBirthYear = datePickerAge.getYear();
        // More accurate age calculation would also consider month and day
        int age = currentYear - userBirthYear;

        // You might want to get day/month from DatePicker and Calendar for a more precise age check
        // For simplicity, using year difference for now.
        if (age < 12)
        {
            Toast.makeText(this, "You are not old enough to create an account!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (age > 100)
        { // Example upper bound
            Toast.makeText(this, "Please enter a valid birth year.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
        }
    }