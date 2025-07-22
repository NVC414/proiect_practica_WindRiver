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

public class GpuDetailsActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpu_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String imageUrl = intent.getStringExtra("imageUrl");
        String color = intent.getStringExtra("color");
        String chipset = intent.getStringExtra("chipset");
        String coreClock = intent.getStringExtra("core_clock");
        String boostClock = intent.getStringExtra("boost_clock");
        int memory = intent.getIntExtra("memory", 0);
        int length = intent.getIntExtra("length", 0);

        ImageView gpuImage = findViewById(R.id.gpuImage);
        TextView gpuName = findViewById(R.id.gpuName);
        TextView gpuPrice = findViewById(R.id.gpuPrice);
        TextView gpuChipset = findViewById(R.id.gpuChipset);
        TextView gpuColor = findViewById(R.id.gpuColor);
        TextView gpuCoreClock = findViewById(R.id.gpuCoreClock);
        TextView gpuBoostClock = findViewById(R.id.gpuBoostClock);
        TextView gpuMemory = findViewById(R.id.gpuMemory);
        TextView gpuLength = findViewById(R.id.gpuLength);
        Button addToCartButton = findViewById(R.id.buttonAddToCartGpu);
        ImageButton backButton = findViewById(R.id.buttonBack);
        android.view.View layoutCartActions = findViewById(R.id.layoutCartActionsGpu);
        ImageButton buttonRemoveFromCart = findViewById(R.id.buttonRemoveFromCartGpu);
        ImageButton buttonAddMoreToCart = findViewById(R.id.buttonAddMoreToCartGpu);
        TextView textQuantity = findViewById(R.id.textQuantityGpu);

        Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_gpu_placeholder).centerInside().into(gpuImage);

        gpuName.setText(name);
        gpuPrice.setText(price);
        gpuChipset.setText("Chipset: " + chipset);
        gpuColor.setText("Color: " + color);
        gpuCoreClock.setText("Core Clock: " + coreClock + " MHz");
        gpuBoostClock.setText("Boost Clock: " + boostClock + " MHz");
        gpuMemory.setText("Memory: " + memory + " GB");
        gpuLength.setText("Length: " + length + " mm");

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
                addToCartButton.setVisibility(android.view.View.GONE);
                layoutCartActions.setVisibility(android.view.View.VISIBLE);
                textQuantity.setText(String.valueOf(quantity));
            } else {
                addToCartButton.setVisibility(android.view.View.VISIBLE);
                layoutCartActions.setVisibility(android.view.View.GONE);
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
