package com.WindRiver.internshipProject2025.ui.Cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.WindRiver.internshipProject2025.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>
    {
    private List<com.WindRiver.internshipProject2025.ui.Cart.CartItem> cartItems;

    public CartAdapter(List<com.WindRiver.internshipProject2025.ui.Cart.CartItem> cartItems)
        {
        this.cartItems = cartItems;
        }

    public void setCartItems(List<com.WindRiver.internshipProject2025.ui.Cart.CartItem> cartItems)
        {
        this.cartItems = cartItems;
        notifyDataSetChanged();
        }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent,
                false);
        return new CartViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position)
        {
        com.WindRiver.internshipProject2025.ui.Cart.CartItem item = cartItems.get(position);
        holder.nameView.setText(item.getName() + " x" + item.getQuantity());
        holder.priceView.setText(String.format("$%.2f", item.getPrice() * item.getQuantity()));
        }

    @Override
    public int getItemCount()
        {
        return cartItems != null ? cartItems.size() : 0;
        }

    static class CartViewHolder extends RecyclerView.ViewHolder
        {
        TextView nameView;
        TextView priceView;

        public CartViewHolder(@NonNull View itemView)
            {
            super(itemView);
            nameView = itemView.findViewById(R.id.text_cart_item_name);
            priceView = itemView.findViewById(R.id.text_cart_item_price);
            }
        }
    }
