package com.windriver.pcgate.ui.DetailView;

import android.annotation.SuppressLint;
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

public class PsuDetailsActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psu_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String imageUrl = intent.getStringExtra("imageUrl");
        String type = intent.getStringExtra("type");
        String color = intent.getStringExtra("color");
        String efficiency = intent.getStringExtra("efficiency");
        String modular = intent.getStringExtra("modular");
        int wattage = intent.getIntExtra("wattage", 0);

        ImageView psuImage = findViewById(R.id.psuImage);
        TextView psuName = findViewById(R.id.psuName);
        TextView psuPrice = findViewById(R.id.psuPrice);
        TextView psuType = findViewById(R.id.psuType);
        TextView psuColor = findViewById(R.id.psuColor);
        TextView psuEfficiency = findViewById(R.id.psuEfficiency);
        TextView psuModular = findViewById(R.id.psuModular);
        TextView psuWattage = findViewById(R.id.psuWattage);
        Button addToCartButton = findViewById(R.id.buttonAddToCart);
        ImageButton backButton = findViewById(R.id.buttonBack);

        Glide.with(this).load(imageUrl).placeholder(
                R.drawable.ic_psu_placeholder).centerCrop().into(psuImage);

        psuName.setText(name);
        psuPrice.setText(price);
        psuType.setText("Type: " + type);
        psuColor.setText("Color: " + color);
        psuEfficiency.setText("Efficiency: " + efficiency);
        psuModular.setText("Modular: " + modular);
        psuWattage.setText("Wattage: " + wattage + " W");

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
