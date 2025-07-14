package com.example.myapplication.repository;

import com.example.myapplication.ui.Cart.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartRepository
    {
    private static CartRepository instance;
    private final List<CartItem> cartItems = new ArrayList<>();

    private CartRepository()
        {
        }

    public static CartRepository getInstance()
        {
        if (instance == null)
        {
            instance = new CartRepository();
        }
        return instance;
        }

    public List<CartItem> getCartItems()
        {
        return new ArrayList<>(cartItems);
        }

    public interface OnCartChangedListener
        {
        void onCartChanged(List<CartItem> newCartItems);
        }

    private OnCartChangedListener cartChangedListener;

    public void setOnCartChangedListener(OnCartChangedListener listener)
        {
        this.cartChangedListener = listener;
        }

    private void notifyCartChanged()
        {
        if (cartChangedListener != null)
        {
            cartChangedListener.onCartChanged(getCartItems());
        }
        }

    public void addItem(CartItem item)
        {
        boolean found = false;
        for (CartItem cartItem : cartItems)
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
            cartItems.add(item);
        }
        notifyCartChanged();
        }

    public void clearCart()
        {
        cartItems.clear();
        notifyCartChanged();
        }
    }
