package com.windriver.pcgate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.windriver.pcgate.databinding.ActivityMainBinding;
import com.windriver.pcgate.ui.cart.CartViewModel;
import com.windriver.pcgate.ui.LoginRegister.Login_activity;

public class MainActivity extends AppCompatActivity
    {

        private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

        if (user == null)
        {
            Intent intent = new Intent(getApplicationContext(), Login_activity.class);
            startActivity(intent);
            finish();
            return;
        }




        setupNavigation();

        }

    private void setupNavigation()
        {
        BottomNavigationView navView = binding.navView;
        CartViewModel cartViewModel = CartViewModel.getInstance();
        cartViewModel.getCartItems().observe(this, items ->
            {
                int count = 0;
                for (com.windriver.pcgate.ui.cart.CartItem item : items)
                {
                    count += item.getQuantity();
                }
                if (count > 0)
                {
                    navView.getOrCreateBadge(R.id.navigation_cart).setNumber(count);
                    navView.getOrCreateBadge(R.id.navigation_cart).setVisible(true);
                }
                else
                {
                    navView.removeBadge(R.id.navigation_cart);
                }
            });

            View navHostFragment = findViewById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment == null)
        {
            Toast.makeText(this, "Error: Navigation host missing.", Toast.LENGTH_LONG).show();
            return;
        }

        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_cart).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
            {
                if (destination.getId() == R.id.chatFragment)
                {
                    binding.navView.setVisibility(View.GONE);
                }
                else
                {
                    binding.navView.setVisibility(View.VISIBLE);
                }
            });
        }




    }