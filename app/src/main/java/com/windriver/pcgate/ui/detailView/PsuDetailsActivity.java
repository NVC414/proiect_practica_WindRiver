package com.windriver.pcgate.ui.detailView;

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

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.ui.cart.CartItem;
import com.windriver.pcgate.ui.cart.CartViewModel;

public class PsuDetailsActivity extends AppCompatActivity
    {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
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
        Button addToCartButton = findViewById(R.id.buttonAddToCartPsu);
        ImageButton backButton = findViewById(R.id.buttonBack);
        android.view.View layoutCartActions = findViewById(R.id.layoutCartActionsPsu);
        ImageButton buttonRemoveFromCart = findViewById(R.id.buttonRemoveFromCartPsu);
        ImageButton buttonAddMoreToCart = findViewById(R.id.buttonAddMoreToCartPsu);
        TextView textQuantity = findViewById(R.id.textQuantityPsu);

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

        Runnable updateCartUI = () ->
            {
                java.util.List<CartItem> items = cartViewModel.getCartItems().getValue();
                int quantity = 0;
                if (items != null)
                {
                    for (CartItem item : items)
                    {
                        if (item.getName().equals(name))
                        {
                            quantity = item.getQuantity();
                            break;
                        }
                    }
                }
                if (quantity > 0)
                {
                    addToCartButton.setVisibility(android.view.View.GONE);
                    layoutCartActions.setVisibility(android.view.View.VISIBLE);
                    textQuantity.setText(String.valueOf(quantity));
                }
                else
                {
                    addToCartButton.setVisibility(android.view.View.VISIBLE);
                    layoutCartActions.setVisibility(android.view.View.GONE);
                }
            };
        cartViewModel.getCartItems().observe(this, items -> updateCartUI.run());
        updateCartUI.run();

        addToCartButton.setOnClickListener(v ->
            {
                double priceValue = 0.0;
                try
                {
                    priceValue = price != null ? Double.parseDouble(
                            price.replaceAll("[^0-9.]", "")) : 0.0;
                }
                catch (Exception ignored)
                {
                }
                CartItem cartItem = new CartItem(name, priceValue, 1);
                cartViewModel.addItem(cartItem);
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            });
        buttonAddMoreToCart.setOnClickListener(v ->
            {
                double priceValue = 0.0;
                try
                {
                    priceValue = price != null ? Double.parseDouble(
                            price.replaceAll("[^0-9.]", "")) : 0.0;
                }
                catch (Exception ignored)
                {
                }
                CartItem cartItem = new CartItem(name, priceValue, 1);
                cartViewModel.addItem(cartItem);
            });
        buttonRemoveFromCart.setOnClickListener(v ->
            {
                java.util.List<CartItem> items = cartViewModel.getCartItems().getValue();
                double priceValue = 0.0;
                try
                {
                    priceValue = price != null ? Double.parseDouble(
                            price.replaceAll("[^0-9.]", "")) : 0.0;
                }
                catch (Exception ignored)
                {
                }
                if (items != null)
                {
                    for (CartItem item : items)
                    {
                        if (item.getName().equals(name))
                        {
                            int newQty = item.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(new CartItem(name, priceValue, -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new CartItem(name, priceValue, -item.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            });
        backButton.setOnClickListener(v -> finish());
        }
    }
