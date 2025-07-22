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

public class CaseDetailsActivity extends AppCompatActivity
    {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String imageUrl = intent.getStringExtra("imageUrl");
        String type = intent.getStringExtra("type");
        String color = intent.getStringExtra("color");
        String sidePanel = intent.getStringExtra("side_panel");
        String psu = intent.getStringExtra("psu");
        int internal35Bays = intent.getIntExtra("internal_35_bays", 0);
        double externalVolume = intent.getDoubleExtra("external_volume", 0);

        ImageView caseImage = findViewById(R.id.caseImage);
        TextView caseName = findViewById(R.id.caseName);
        TextView casePrice = findViewById(R.id.casePrice);
        TextView caseType = findViewById(R.id.caseType);
        TextView caseColor = findViewById(R.id.caseColor);
        TextView caseSidePanel = findViewById(R.id.caseSidePanel);
        TextView casePSU = findViewById(R.id.casePSU);
        TextView caseInternal35Bays = findViewById(R.id.caseInternal35Bays);
        TextView caseExternalVolume = findViewById(R.id.caseExternalVolume);
        Button addToCartButton = findViewById(R.id.buttonAddToCart);
            ImageButton backButton = findViewById(R.id.buttonBack);
        android.view.View layoutCartActions = findViewById(R.id.layoutCartActions);
        ImageButton buttonRemoveFromCart = findViewById(R.id.buttonRemoveFromCart);
        ImageButton buttonAddMoreToCart = findViewById(R.id.buttonAddMoreToCart);
        TextView textQuantity = findViewById(R.id.textQuantity);

        Glide.with(this).load(imageUrl).placeholder(
                R.drawable.ic_case_placeholder).centerCrop().into(caseImage);

        caseName.setText(name);
        casePrice.setText("$" + price);
        caseType.setText("Type: " + type);
        caseColor.setText("Color: " + color);
        caseSidePanel.setText("Side Panel: " + sidePanel);
        casePSU.setText("PSU: " + psu);
        caseInternal35Bays.setText("Internal 3.5" + " Bays: " + internal35Bays);
        caseExternalVolume.setText("External Volume: " + externalVolume + " L");

        CartViewModel cartViewModel = CartViewModel.getInstance();


        Runnable updateCartUI = () ->
            {
                java.util.List<CartItem> items = cartViewModel.getCartItems().getValue();
                int quantity = 0;
                try
                {
                    if (price != null) {
                        Double.parseDouble(
                                price.replaceAll("[^0-9.]", ""));
                    }
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
        updateCartUI.run();
        }
    }
