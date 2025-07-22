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

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.ui.Cart.CartItem;
import com.windriver.pcgate.ui.Cart.CartViewModel;

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
        Button addToCartButton = findViewById(R.id.buttonAddToCart);
        ImageButton backButton = findViewById(R.id.buttonBack);

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
                CartItem cartItem = new CartItem(model, priceValue, 1);
                cartViewModel.addItem(cartItem);
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            });

        backButton.setOnClickListener(v -> finish());
        }
    }
