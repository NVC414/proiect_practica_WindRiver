package com.windriver.pcgate.ui.detailView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.ui.cart.CartItem;
import com.windriver.pcgate.ui.cart.CartViewModel;

public class CpuDetailsActivity extends AppCompatActivity
    {
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        double price = intent.getDoubleExtra("price", 0.0);
        String imageUrl = intent.getStringExtra("imageUrl");
        double boostClock = intent.getDoubleExtra("boostClock", 0.0);
        double coreClock = intent.getDoubleExtra("coreClock", 0.0);
        int coreCount = intent.getIntExtra("coreCount", 0);
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
        LinearLayout layoutCartActionsCpu = findViewById(R.id.layoutCartActionsCpu);
        ImageButton buttonRemoveFromCartCpu = findViewById(R.id.buttonRemoveFromCartCpu);
        ImageButton buttonAddMoreToCartCpu = findViewById(R.id.buttonAddMoreToCartCpu);
        TextView textQuantityCpu = findViewById(R.id.textQuantityCpu);

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
                    addToCartButton.setVisibility(View.GONE);
                    layoutCartActionsCpu.setVisibility(View.VISIBLE);
                    textQuantityCpu.setText(String.valueOf(quantity));
                }
                else
                {
                    addToCartButton.setVisibility(View.VISIBLE);
                    layoutCartActionsCpu.setVisibility(View.GONE);
                }
            };
        cartViewModel.getCartItems().observe(this, items -> updateCartUI.run());
        updateCartUI.run();

        Glide.with(this).load(imageUrl).placeholder(
                R.drawable.ic_cpu_placeholder).centerCrop().into(cpuImage);

        cpuName.setText(name);
        cpuPrice.setText(String.format("$%.2f", price));
        cpuBoostClock.setText("Boost Clock: " + boostClock + " GHz");
        cpuCoreClock.setText("Core Clock: " + coreClock + " GHz");
        cpuCoreCount.setText("Core Count: " + coreCount);
        cpuGraphics.setText("Graphics: " + (graphics != null ? graphics : "None"));
        cpuSMT.setText("SMT: " + (smt ? "Yes" : "No"));
        cpuSocket.setText("Socket: " + (socket != null ? socket : ""));
        cpuTDP.setText("TDP: " + tdp + " W");

        addToCartButton.setOnClickListener(v ->
            {
                CartItem cartItem = new CartItem(name, price, 1);
                cartViewModel.addItem(cartItem);
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            });
        buttonAddMoreToCartCpu.setOnClickListener(v ->
            {
                CartItem cartItem = new CartItem(name, price, 1);
                cartViewModel.addItem(cartItem);
            });
        buttonRemoveFromCartCpu.setOnClickListener(v ->
            {
                java.util.List<CartItem> items = cartViewModel.getCartItems().getValue();
                if (items != null)
                {
                    for (CartItem item : items)
                    {
                        if (item.getName().equals(name))
                        {
                            int newQty = item.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(new CartItem(name, price, -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new CartItem(name, price, -item.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            });
        backButton.setOnClickListener(v -> finish());
        }
    }
