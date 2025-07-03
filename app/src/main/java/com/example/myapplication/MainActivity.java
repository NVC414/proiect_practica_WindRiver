package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.util.Random;

import kotlin.random.URandomKt;

public class MainActivity extends AppCompatActivity
    {

    private ActivityMainBinding binding;
        private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_cart).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

        public void sendMessage(View view) {
        //retrieve from firebase database and send data to the textView2
            //retrieve a random value from 0 to 4999
//            Random r = new Random();
//            int low = 0;
//            int high = 4999;
//            int result = r.nextInt(high-low) + low;
//            System.out.println(String.valueOf(result));
            mDatabase.child("video-card").child("0").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    DataSnapshot dataSnapshot = task.getResult();
                    TextView textView2=findViewById(R.id.textView2);
                    if (dataSnapshot.exists()) {
                        StringBuilder builder = new StringBuilder();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String key = childSnapshot.getKey();
                            Object value = childSnapshot.getValue();
                            builder.append(key).append(": ").append(value).append("\n");
                        }
                        textView2.setText(builder.toString());
                    } else {
                        textView2.setText("No data found");
                    }
                }
            });

        }
    }