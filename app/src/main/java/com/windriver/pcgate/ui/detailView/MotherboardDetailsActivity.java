package com.windriver.pcgate.ui.detailView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.ui.cart.CartItem;
import com.windriver.pcgate.ui.cart.CartViewModel;

public class MotherboardDetailsActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
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
        Button addToCartButton = findViewById(R.id.buttonAddToCartMotherboard);
        ImageButton backButton = findViewById(R.id.buttonBack);
        View layoutCartActions = findViewById(R.id.layoutCartActionsMotherboard);
        ImageButton buttonRemoveFromCart = findViewById(R.id.buttonRemoveFromCartMotherboard);
        ImageButton buttonAddMoreToCart = findViewById(R.id.buttonAddMoreToCartMotherboard);
        TextView textQuantity = findViewById(R.id.textQuantityMotherboard);

        Glide.with(this).load(imageUrl).placeholder(
                R.drawable.ic_motherboard_placeholder).centerCrop().into(motherboardImage);

        motherboardName.setText(name);
        motherboardPrice.setText("$" + price);
        motherboardColor.setText("Color: " + color);
        motherboardDDRType.setText("DDR Type: " + ddrType);
        motherboardFormFactor.setText("Form Factor: " + formFactor);
        motherboardSocket.setText("Socket: " + socket);
        motherboardMaxMemory.setText("Max Memory: " + maxMemory + " GB");
        motherboardMemorySlots.setText("Memory Slots: " + memorySlots);

        CartViewModel cartViewModel = CartViewModel.getInstance();

        Runnable updateCartUI = () -> {
            java.util.List<CartItem> items = cartViewModel.getCartItems().getValue();
            int quantity = 0;
            try {
                if (price != null) {
                    Double.parseDouble(price.replaceAll("[^0-9.]", ""));
                }
            } catch (Exception ignored) {}
            if (items != null) {
                for (CartItem item : items) {
                    if (item.getName().equals(name)) {
                        quantity = item.getQuantity();
                        break;
                    }
                }
            }
            boolean nowInCart = quantity > 0;
            if (nowInCart) {
                addToCartButton.setVisibility(View.GONE);
                layoutCartActions.setVisibility(View.VISIBLE);
                textQuantity.setText(String.valueOf(quantity));
            } else {
                addToCartButton.setVisibility(View.VISIBLE);
                layoutCartActions.setVisibility(View.GONE);
            }
        };

        cartViewModel.getCartItems().observe(this, items -> updateCartUI.run());

        addToCartButton.setOnClickListener(v -> {
            double priceValue = 0.0;
            try {
                priceValue = price != null ? Double.parseDouble(price.replaceAll("[^0-9.]", "")) : 0.0;
            } catch (Exception ignored) {}
            CartItem cartItem = new CartItem(name, priceValue, 1);
            cartViewModel.addItem(cartItem);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });
        buttonAddMoreToCart.setOnClickListener(v -> {
            double priceValue = 0.0;
            try {
                priceValue = price != null ? Double.parseDouble(price.replaceAll("[^0-9.]", "")) : 0.0;
            } catch (Exception ignored) {}
            CartItem cartItem = new CartItem(name, priceValue, 1);
            cartViewModel.addItem(cartItem);
        });
        buttonRemoveFromCart.setOnClickListener(v -> {
            java.util.List<CartItem> items = cartViewModel.getCartItems().getValue();
            double priceValue = 0.0;
            try {
                priceValue = price != null ? Double.parseDouble(price.replaceAll("[^0-9.]", "")) : 0.0;
            } catch (Exception ignored) {}
            if (items != null) {
                for (CartItem item : items) {
                    if (item.getName().equals(name)) {
                        int newQty = item.getQuantity() - 1;
                        if (newQty > 0) {
                            cartViewModel.addItem(new CartItem(name, priceValue, -1));
                        } else {
                            cartViewModel.addItem(new CartItem(name, priceValue, -item.getQuantity()));
                        }
                        break;
                    }
                }
            }
        });
        backButton.setOnClickListener(v -> finish());
        updateCartUI.run();
    }
}
