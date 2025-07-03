package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
    {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //this is a pull request test
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_cart).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Button b = findViewById(R.id.button);
    }
    public void disable(View v) {
        v.setEnabled(false);
        Button b = (Button) v;
        b.setText("New Disabled");
    }


    public void handleText(View v){
        TextView t =findViewById(R.id.Inserter);
        String input= t.getText().toString();
        TextView l =findViewById(R.id.List);
        Toast.makeText(this, "Text inserted", Toast.LENGTH_SHORT).show();
        l.setText(l.getText().toString()+input+"\n");
    }
       /* int c=1;
        public void disable(View v) {
            v.setEnabled(false);
            Button b = (Button) v;
            if (c%2==1)
                b.setText("X");
                else
                b.setText("O");
            c++;

            }
*/


        }
