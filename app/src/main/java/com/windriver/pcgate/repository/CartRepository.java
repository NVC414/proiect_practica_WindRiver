package com.windriver.pcgate.repository;

import com.windriver.pcgate.ui.cart.CartItem;

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
        for (int i = 0; i < cartItems.size(); i++)
        {
            CartItem cartItem = cartItems.get(i);
            if (cartItem.getName().equals(item.getName()))
            {
                int newQty = cartItem.getQuantity() + item.getQuantity();
                if (newQty > 0)
                {
                    cartItem.setQuantity(newQty);
                }
                else
                {
                    cartItems.remove(i);
                }
                found = true;
                break;
            }
        }
        if (!found && item.getQuantity() > 0)
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
