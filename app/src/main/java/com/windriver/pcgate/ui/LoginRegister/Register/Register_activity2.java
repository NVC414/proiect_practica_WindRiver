package com.windriver.pcgate.ui.LoginRegister.Register;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.windriver.pcgate.R;
import com.windriver.pcgate.ui.LoginRegister.Login_activity;

import java.util.Calendar;

public class Register_activity2 extends AppCompatActivity {
    TextView textViews;
    Button button;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        radioGroup = findViewById(R.id.RadioGroup);
        datePicker = findViewById(R.id.AgePicker);

        textViews = findViewById(R.id.loginNow);
        textViews.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), Login_activity.class);
                startActivity(intent);
                finish();
            }
        });
        button = findViewById(R.id.next_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!validateGender() | !validateAge()){
                    return;
                }
                selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());
                String gender = selectedGender.getText().toString();


                Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                intent.putExtra("date", getIntent().getStringExtra("date"));
                intent.putExtra("gender", gender);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("password", getIntent().getStringExtra("password"));
                intent.putExtra("phone", getIntent().getStringExtra("phone"));
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean validateGender(){
        if(radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }

    }
    private boolean validateAge(){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;
        if(isAgeValid < 12){
            Toast.makeText(this, "You are not old enough to make an account!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }


}
