package com.windriver.pcgate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.model.ShopItem;

import java.util.List;

public class ShopItemAdapter<T extends ShopItem> extends RecyclerView.Adapter<ShopItemAdapter.ShopItemViewHolder>
    {
    private List<T> itemList;
    private OnAddToCartListener addToCartListener;
    private OnItemClickListener itemClickListener;
    private OnViewMoreClickListener viewMoreClickListener;
    private OnRemoveFromCartListener removeFromCartListener;

    public interface OnAddToCartListener
        {
        void onAddToCart(ShopItem item);
        }

    public interface OnItemClickListener
        {
        void onItemClick(ShopItem item);
        }

    public interface OnViewMoreClickListener
        {
        void onViewMore();
        }

    public interface OnRemoveFromCartListener
        {
        void onRemoveFromCart(ShopItem item);
        }

    public ShopItemAdapter(List<T> itemList)
        {
        this.itemList = itemList;
        }

    public void setOnAddToCartListener(OnAddToCartListener listener)
        {
        this.addToCartListener = listener;
        }

    public void setOnItemClickListener(OnItemClickListener listener)
        {
        this.itemClickListener = listener;
        }

    public void setOnViewMoreClickListener(OnViewMoreClickListener listener)
        {
        this.viewMoreClickListener = listener;
        }

    public void setOnRemoveFromCartListener(OnRemoveFromCartListener listener)
        {
        this.removeFromCartListener = listener;
        }

    public void setItemList(List<T> newList)
        {
        this.itemList = newList;
        notifyDataSetChanged();
        }

    public List<T> getItemList()
        {
        return itemList;
        }

    @NonNull
    @Override
    public ShopItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent,
                false);
        return new ShopItemViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull ShopItemViewHolder holder, int position)
        {
        T item = itemList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("$" + item.getPrice());
        Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).placeholder(
                R.drawable.ic_laptop_placeholder).centerCrop().into(holder.itemImage);

        int quantity = item.getQuantity();
        if (quantity > 0)
        {
            holder.cartControlsLayout.setVisibility(View.VISIBLE);
            holder.buttonAddToCart.setVisibility(View.GONE);
            holder.textCartCount.setText(String.valueOf(quantity));
        }
        else
        {
            holder.cartControlsLayout.setVisibility(View.GONE);
            holder.buttonAddToCart.setVisibility(View.VISIBLE);
        }

        // Handle View More click
        if ("__VIEW_MORE__".equals(item.getName()) && viewMoreClickListener != null)
        {
            holder.itemView.setOnClickListener(v -> viewMoreClickListener.onViewMore());
            holder.cartControlsLayout.setVisibility(View.GONE);
            holder.buttonAddToCart.setVisibility(View.GONE);
        }
        else
        {
            holder.buttonAddToCart.setOnClickListener(v ->
                {
                    item.setQuantity(1);
                    notifyItemChanged(position);
                    if (addToCartListener != null)
                    {
                        addToCartListener.onAddToCart(item);
                    }
                });
            holder.buttonPlus.setOnClickListener(v ->
                {
                    item.setQuantity(item.getQuantity() + 1);
                    holder.textCartCount.setText(String.valueOf(item.getQuantity()));
                    notifyItemChanged(position);
                    if (addToCartListener != null)
                    {
                        addToCartListener.onAddToCart(item);
                    }
                });
            holder.buttonMinus.setOnClickListener(v ->
                {
                    int q = item.getQuantity() - 1;
                    item.setQuantity(q);
                    if (q <= 0)
                    {
                        holder.cartControlsLayout.setVisibility(View.GONE);
                        holder.buttonAddToCart.setVisibility(View.VISIBLE);
                        if (removeFromCartListener != null)
                        {
                            removeFromCartListener.onRemoveFromCart(item);
                        }
                    }
                    else
                    {
                        holder.textCartCount.setText(String.valueOf(q));
                    }
                    notifyItemChanged(position);
                    if (addToCartListener != null)
                    {
                        addToCartListener.onAddToCart(item);
                    }
                });
            holder.itemView.setOnClickListener(v ->
                {
                    if (itemClickListener != null)
                    {
                        itemClickListener.onItemClick(item);
                    }
                });
        }
        }

    @Override
    public int getItemCount()
        {
        return itemList.size();
        }

    public static class ShopItemViewHolder extends RecyclerView.ViewHolder
        {
        ImageView itemImage;
        TextView itemName, itemPrice, textCartCount;
        Button buttonAddToCart, buttonMinus, buttonPlus;
        LinearLayout cartControlsLayout;

        public ShopItemViewHolder(@NonNull View itemView)
            {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            textCartCount = itemView.findViewById(R.id.textCartCount);
            cartControlsLayout = itemView.findViewById(R.id.cartControlsLayout);
            }
        }
    }
