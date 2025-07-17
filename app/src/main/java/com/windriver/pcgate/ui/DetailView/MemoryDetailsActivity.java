package com.windriver.pcgate.ui.DetailView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.windriver.pcgate.R;
import com.windriver.pcgate.ui.Cart.CartItem;
import com.windriver.pcgate.ui.Cart.CartViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MemoryDetailsActivity extends AppCompatActivity
    {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        double price = intent.getDoubleExtra("price", 0.0);
        String imageUrl = intent.getStringExtra("imageUrl");
        String ddrType = intent.getStringExtra("ddr_type");
        String color = intent.getStringExtra("color");
        int casLatency = intent.getIntExtra("cas_latency", 0);
        int firstWordLatency = intent.getIntExtra("first_word_latency", 0);
        ArrayList<Integer> modules = intent.getIntegerArrayListExtra("modules");
        ArrayList<Integer> speed = intent.getIntegerArrayListExtra("speed");

        ImageView memoryImage = findViewById(R.id.memoryImage);
        TextView memoryName = findViewById(R.id.memoryName);
        TextView memoryPrice = findViewById(R.id.memoryPrice);
        TextView memoryType = findViewById(R.id.memoryType);
        TextView memoryColor = findViewById(R.id.memoryColor);
        TextView memoryCasLatency = findViewById(R.id.memoryCasLatency);
        TextView memoryFirstWordLatency = findViewById(R.id.memoryFirstWordLatency);
        TextView memoryModules = findViewById(R.id.memoryModules);
        TextView memorySpeed = findViewById(R.id.memorySpeed);
        Button addToCartButton = findViewById(R.id.buttonAddToCart);
        ImageButton backButton = findViewById(R.id.buttonBack);

        Glide.with(this).load(imageUrl).placeholder(
                R.drawable.ic_memory_placeholder).centerCrop().into(memoryImage);

        memoryName.setText(name);
        memoryPrice.setText("$"+price);
        memoryType.setText("Type: " + ddrType);
        memoryColor.setText("Color: " + color);
        memoryCasLatency.setText("CAS Latency: " + casLatency);
        memoryFirstWordLatency.setText("First Word Latency: " + firstWordLatency);
        memoryModules.setText("Modules: " + (modules != null ? modules.toString() : "-"));
        memorySpeed.setText("Speed: " + (speed != null ? speed.toString() : "-"));

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

