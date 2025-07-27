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

public class LaptopDetailsActivity extends AppCompatActivity
    {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laptop_details);

        Intent intent = getIntent();
        String brand = intent.getStringExtra("brand");
        String model = intent.getStringExtra("model");
        String price = intent.getStringExtra("price");
        String imageUrl = intent.getStringExtra("imageUrl");
        String processor = intent.getStringExtra("processor");
        String ram_gb = intent.getStringExtra("ram_gb");
        String ram_type = intent.getStringExtra("ram_type");
        String graphic_card_gb = intent.getStringExtra("graphic_card_gb");
        String hdd = intent.getStringExtra("hdd");
        String ssd = intent.getStringExtra("ssd");

        ImageView laptopImage = findViewById(R.id.laptopImage);
        TextView laptopBrand = findViewById(R.id.laptopBrand);
        TextView laptopModel = findViewById(R.id.laptopModel);
        TextView laptopPrice = findViewById(R.id.laptopPrice);
        TextView laptopProcessor = findViewById(R.id.laptopProcessor);
        TextView laptopRam = findViewById(R.id.laptopRam);
        TextView laptopGraphics = findViewById(R.id.laptopGraphics);
        TextView laptopHdd = findViewById(R.id.laptopHdd);
        TextView laptopSsd = findViewById(R.id.laptopSsd);
        Button addToCartButton = findViewById(R.id.buttonAddToCartLaptop);
        ImageButton backButton = findViewById(R.id.buttonBack);
        android.view.View layoutCartActions = findViewById(R.id.layoutCartActionsLaptop);
        ImageButton buttonRemoveFromCart = findViewById(R.id.buttonRemoveFromCartLaptop);
        ImageButton buttonAddMoreToCart = findViewById(R.id.buttonAddMoreToCartLaptop);
        TextView textQuantity = findViewById(R.id.textQuantityLaptop);

        Glide.with(this).load(imageUrl).placeholder(
                R.drawable.ic_laptop_placeholder).centerCrop().into(laptopImage);

        laptopBrand.setText("Brand: " + brand);
        laptopModel.setText("Model: " + model);
        laptopPrice.setText(price);
        laptopProcessor.setText("Processor: " + processor);
        laptopRam.setText("RAM: " + ram_gb + " GB " + ram_type);
        laptopGraphics.setText("Graphics: " + graphic_card_gb + " GB");
        laptopHdd.setText("HDD: " + hdd);
        laptopSsd.setText("SSD: " + ssd);

        CartViewModel cartViewModel = CartViewModel.getInstance();

        Runnable updateCartUI = () ->
            {
                java.util.List<CartItem> items = cartViewModel.getCartItems().getValue();
                int quantity = 0;
                String cartKey = brand + "|" + model;
                try
                {
                    if (price != null)
                    {
                        Double.parseDouble(price.replaceAll("[^0-9.]", ""));
                    }
                }
                catch (Exception ignored)
                {
                }
                if (items != null)
                {
                    for (CartItem item : items)
                    {
                        if (item.getName().equals(cartKey))
                        {
                            quantity = item.getQuantity();
                            break;
                        }
                    }
                }
                boolean nowInCart = quantity > 0;
                if (nowInCart)
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
                String cartKey = brand + "|" + model;
                CartItem cartItem = new CartItem(cartKey, priceValue, 1);
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
                String cartKey = brand + "|" + model;
                CartItem cartItem = new CartItem(cartKey, priceValue, 1);
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
                String cartKey = brand + "|" + model;
                if (items != null)
                {
                    for (CartItem item : items)
                    {
                        if (item.getName().equals(cartKey))
                        {
                            int newQty = item.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(new CartItem(cartKey, priceValue, -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new CartItem(cartKey, priceValue, -item.getQuantity()));
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
