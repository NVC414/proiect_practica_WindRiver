package com.example.myapplication.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel
    {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(
            new ArrayList<>());
    private final MutableLiveData<Double> totalSum = new MutableLiveData<>(0.0);

    public LiveData<List<CartItem>> getCartItems()
        {
        return cartItems;
        }

    public LiveData<Double> getTotalSum()
    {
    return totalSum;
    }

    public void addItem(CartItem item)
        {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null)
        {
            currentItems = new ArrayList<>();
        }
        boolean found = false;
        for (CartItem cartItem : currentItems)
        {
            if (cartItem.getName().equals(item.getName()))
            {
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                found = true;
                break;
            }
        }
        if (!found)
        {
            currentItems.add(item);
        }
        cartItems.setValue(new ArrayList<>(currentItems));
        recalculateTotal();
    }

    private void recalculateTotal()
        {
        List<CartItem> items = cartItems.getValue();
        double sum = 0.0;
        if (items != null)
        {
            for (CartItem item : items)
            {
                sum += item.getPrice() * item.getQuantity();
            }
        }
        totalSum.setValue(sum);
    }
    }
