package com.WindRiver.internshipProject2025.ui.DetailView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.WindRiver.internshipProject2025.R;
import com.WindRiver.internshipProject2025.ui.Cart.CartItem;
import com.WindRiver.internshipProject2025.ui.Cart.CartViewModel;
import com.bumptech.glide.Glide;

public class CpuDetailsActivity extends AppCompatActivity
    {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        double price = intent.getDoubleExtra("price", 0.0);
        String imageUrl = intent.getStringExtra("imageUrl");
        double boostClock = intent.getDoubleExtra("boost_clock", 0.0);
        double coreClock = intent.getDoubleExtra("core_clock", 0.0);
        int coreCount = intent.getIntExtra("core_count", 0);
        String graphics = intent.getStringExtra("graphics");
        boolean smt = intent.getBooleanExtra("smt", false);
        String socket = intent.getStringExtra("socket");
        int tdp = intent.getIntExtra("tdp", 0);

        ImageView cpuImage = findViewById(R.id.cpuImage);
        TextView cpuName = findViewById(R.id.cpuName);
        TextView cpuPrice = findViewById(R.id.cpuPrice);
        TextView cpuBoostClock = findViewById(R.id.cpuBoostClock);
        TextView cpuCoreClock = findViewById(R.id.cpuCoreClock);
        TextView cpuCoreCount = findViewById(R.id.cpuCoreCount);
        TextView cpuGraphics = findViewById(R.id.cpuGraphics);
        TextView cpuSMT = findViewById(R.id.cpuSMT);
        TextView cpuSocket = findViewById(R.id.cpuSocket);
        TextView cpuTDP = findViewById(R.id.cpuTDP);
        Button addToCartButton = findViewById(R.id.buttonAddToCartCpu);
        ImageButton backButton = findViewById(R.id.buttonBack);

        Glide.with(this).load(imageUrl).placeholder(
                R.drawable.ic_image_placeholder).centerCrop().into(cpuImage);

        cpuName.setText(name);
        cpuPrice.setText(String.format("%.2f RON", price));
        cpuBoostClock.setText("Boost Clock: " + boostClock + " GHz");
        cpuCoreClock.setText("Core Clock: " + coreClock + " GHz");
        cpuCoreCount.setText("Core Count: " + coreCount);
        cpuGraphics.setText("Graphics: " + (graphics != null ? graphics : "None"));
        cpuSMT.setText("SMT: " + (smt ? "Yes" : "No"));
        cpuSocket.setText("Socket: " + (socket != null ? socket : ""));
        cpuTDP.setText("TDP: " + tdp + " W");

        CartViewModel cartViewModel = CartViewModel.getInstance();
        addToCartButton.setOnClickListener(v ->
            {
                CartItem cartItem = new CartItem(name, price, 1);
                cartViewModel.addItem(cartItem);
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            });

        backButton.setOnClickListener(v -> finish());
        }
    }

