package com.windriver.pcgate.ui.Cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.windriver.pcgate.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>
    {
    private List<com.windriver.pcgate.ui.Cart.CartItem> cartItems;

    public CartAdapter(List<com.windriver.pcgate.ui.Cart.CartItem> cartItems)
        {
        this.cartItems = cartItems;
        }

    public void setCartItems(List<com.windriver.pcgate.ui.Cart.CartItem> cartItems)
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
        com.windriver.pcgate.ui.Cart.CartItem item = cartItems.get(position);
        holder.nameView.setText(item.getName());
        holder.priceView.setText(String.format("$%.2f", item.getPrice() * item.getQuantity()));
        holder.quantityView.setText(String.valueOf(item.getQuantity()));

        holder.addButton.setOnClickListener(v -> {

            CartViewModel.getInstance().addItem(new CartItem(item.getName(), item.getPrice(), 1));
        });
        holder.removeButton.setOnClickListener(v -> {

            CartViewModel.getInstance().addItem(new CartItem(item.getName(), item.getPrice(), -1));
        });
        }

    @Override
    public int getItemCount()
        {
        return cartItems != null ? cartItems.size() : 0;
        }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView priceView;
        TextView quantityView;
        android.widget.ImageButton addButton;
        android.widget.ImageButton removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.text_cart_item_name);
            priceView = itemView.findViewById(R.id.text_cart_item_price);
            quantityView = itemView.findViewById(R.id.text_cart_quantity);
            addButton = itemView.findViewById(R.id.button_cart_add);
            removeButton = itemView.findViewById(R.id.button_cart_remove);
        }
    }
    }
