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

public class MotherboardDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motherboard_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String imageUrl = intent.getStringExtra("imageUrl");
        String color = intent.getStringExtra("color");
        String ddrType = intent.getStringExtra("ddr_type");
        String formFactor = intent.getStringExtra("form_factor");
        String socket = intent.getStringExtra("socket");
        int maxMemory = intent.getIntExtra("max_memory", 0);
        int memorySlots = intent.getIntExtra("memory_slots", 0);

        ImageView motherboardImage = findViewById(R.id.motherboardImage);
        TextView motherboardName = findViewById(R.id.motherboardName);
        TextView motherboardPrice = findViewById(R.id.motherboardPrice);
        TextView motherboardColor = findViewById(R.id.motherboardColor);
        TextView motherboardDDRType = findViewById(R.id.motherboardDDRType);
        TextView motherboardFormFactor = findViewById(R.id.motherboardFormFactor);
        TextView motherboardSocket = findViewById(R.id.motherboardSocket);
        TextView motherboardMaxMemory = findViewById(R.id.motherboardMaxMemory);
        TextView motherboardMemorySlots = findViewById(R.id.motherboardMemorySlots);
        Button addToCartButton = findViewById(R.id.buttonAddToCart);
        ImageButton backButton = findViewById(R.id.buttonBack);

        Glide.with(this).load(imageUrl).placeholder(
                R.drawable.ic_motherboard_placeholder).centerCrop().into(motherboardImage);

        motherboardName.setText(name);
        motherboardPrice.setText(price);
        motherboardColor.setText("Color: " + color);
        motherboardDDRType.setText("DDR Type: " + ddrType);
        motherboardFormFactor.setText("Form Factor: " + formFactor);
        motherboardSocket.setText("Socket: " + socket);
        motherboardMaxMemory.setText("Max Memory: " + maxMemory + " GB");
        motherboardMemorySlots.setText("Memory Slots: " + memorySlots);

        CartViewModel cartViewModel = CartViewModel.getInstance();
        addToCartButton.setOnClickListener(v -> {
            double priceValue = 0.0;
            try {
                priceValue = price != null ? Double.parseDouble(
                        price.replaceAll("[^0-9.]", "")) : 0.0;
            } catch (Exception ignored) {}
            CartItem cartItem = new CartItem(name, priceValue, 1);
            cartViewModel.addItem(cartItem);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> finish());
    }
}
