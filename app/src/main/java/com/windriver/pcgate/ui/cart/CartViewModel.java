package com.windriver.pcgate.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.windriver.pcgate.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel
    {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(
            new ArrayList<>());
    private final MutableLiveData<Double> totalSum = new MutableLiveData<>(0.0);

    private static CartViewModel instance;

    public static CartViewModel getInstance()
        {
        if (instance == null)
        {
            instance = new CartViewModel();
        }
        return instance;
        }

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
        CartRepository.getInstance().addItem(item);
        cartItems.setValue(CartRepository.getInstance().getCartItems());
        recalculateTotal();
        }

    public void clearCart()
        {
        CartRepository.getInstance().clearCart();
        cartItems.setValue(CartRepository.getInstance().getCartItems());
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

    public CartViewModel()
        {
        CartRepository.getInstance().setOnCartChangedListener(newCartItems ->
            {
                cartItems.setValue(newCartItems);
                recalculateTotal();
            });
        cartItems.setValue(CartRepository.getInstance().getCartItems());
        recalculateTotal();
        }
    }
