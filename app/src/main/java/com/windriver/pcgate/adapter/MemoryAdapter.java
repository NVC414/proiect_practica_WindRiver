package com.windriver.pcgate.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.windriver.pcgate.R;
import com.windriver.pcgate.model.MemoryItem;
import com.bumptech.glide.Glide;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>
    {
    private final List<MemoryItem> memoryList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_MEMORY = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    @Getter
    @Setter
    private List<MemoryItem> allMemory = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;
    private java.util.Map<String, Integer> cartQuantities = new java.util.HashMap<>();
    private OnRemoveFromCartClickListener removeFromCartClickListener;

    public MemoryAdapter(List<MemoryItem> memoryList)
        {
        this(memoryList, R.layout.item_memory);
        }

    public MemoryAdapter(List<MemoryItem> memoryList, int layoutResId)
        {
        this.memoryList = memoryList;
        this.layoutResId = layoutResId;
        }

    public interface OnAddToCartClickListener
        {
        void onAddToCart(MemoryItem item);
        }

    public void setOnAddToCartClickListener(OnAddToCartClickListener listener)
        {
        this.addToCartClickListener = listener;
        }

    public interface OnViewMoreClickListener
        {
        void onViewMore();
        }

    public void setOnViewMoreClickListener(OnViewMoreClickListener listener)
        {
        this.viewMoreClickListener = listener;
        }

        public interface OnItemClickListener
        {
        void onItemClick(MemoryItem item);
        }

    public void setOnItemClickListener(OnItemClickListener listener)
        {
        this.itemClickListener = listener;
        }

    public interface OnRemoveFromCartClickListener {
        void onRemoveFromCart(MemoryItem item);
    }

    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener) {
        this.removeFromCartClickListener = listener;
    }

    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities)
    {
        for (int i = 0; i < memoryList.size(); i++) {
            MemoryItem item = memoryList.get(i);
            String key = item.getName();
            Integer oldQty = this.cartQuantities.get(key);
            Integer newQty = cartQuantities.get(key);
            if ((oldQty == null && newQty != null) || (oldQty != null && !oldQty.equals(newQty))) {
                notifyItemChanged(i);
            }
        }
        this.cartQuantities = cartQuantities;
    }

    public void setMemoryList(List<MemoryItem> newList)
    {
        androidx.recyclerview.widget.DiffUtil.DiffResult diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(new androidx.recyclerview.widget.DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return memoryList.size();
            }
            @Override
            public int getNewListSize() {
                return newList.size();
            }
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                MemoryItem oldItem = memoryList.get(oldItemPosition);
                MemoryItem newItem = newList.get(newItemPosition);
                return oldItem.getName().equals(newItem.getName());
            }
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                MemoryItem oldItem = memoryList.get(oldItemPosition);
                MemoryItem newItem = newList.get(newItemPosition);
                return oldItem.equals(newItem);
            }
        });
        memoryList.clear();
        memoryList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position)
        {
        MemoryItem item = memoryList.get(position);
        if ("__VIEW_MORE__".equals(item.getName()))
        {
            return TYPE_VIEW_MORE;
        }
        return TYPE_MEMORY;
        }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        if (viewType == TYPE_VIEW_MORE)
        {
            return new ViewMoreViewHolder(view);
        }
        else
        {
            return new MemoryViewHolder(view);
        }
        }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position)
        {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_VIEW_MORE)
        {
            ViewMoreViewHolder viewMoreHolder = (ViewMoreViewHolder) holder;
            viewMoreHolder.name.setText("@string/view_more");
            viewMoreHolder.price.setText("");
            viewMoreHolder.addToCartButton.setVisibility(View.GONE);
            if (viewMoreHolder.memoryImage != null)
            {
                viewMoreHolder.memoryImage.setVisibility(View.VISIBLE);
                viewMoreHolder.memoryImage.setImageResource(R.drawable.ic_memory_placeholder);
            }
            viewMoreHolder.itemView.setOnClickListener(v ->
                {
                    if (viewMoreClickListener != null)
                    {
                        viewMoreClickListener.onViewMore();
                    }
                });
        }
        else
        {
            MemoryItem item = memoryList.get(position);
            holder.name.setText(item.getName());
            holder.price.setText("$%s".formatted(item.getPrice()));
            Integer quantityObj = cartQuantities.get(item.getName());
            int quantity = (quantityObj != null) ? quantityObj : 0;            if (quantity > 0) {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.layoutCartActions.setVisibility(View.VISIBLE);
                holder.textQuantity.setText(String.valueOf(quantity));
            } else {
                holder.addToCartButton.setVisibility(View.VISIBLE);
                holder.layoutCartActions.setVisibility(View.GONE);
            }
            holder.addToCartButton.setOnClickListener(v -> {
                if (addToCartClickListener != null) {
                    addToCartClickListener.onAddToCart(item);
                }
            });
            holder.buttonAddMoreToCart.setOnClickListener(v -> {
                if (addToCartClickListener != null) {
                    addToCartClickListener.onAddToCart(item);
                }
            });
            holder.buttonRemoveFromCart.setOnClickListener(v -> {
                if (removeFromCartClickListener != null) {
                    removeFromCartClickListener.onRemoveFromCart(item);
                }
            });
            holder.itemView.setOnClickListener(v ->
                {
                    if (itemClickListener != null)
                    {
                        itemClickListener.onItemClick(item);
                    }
                });
            if (holder.memoryImage != null)
            {
                String url = item.getImageUrl() != null ? item.getImageUrl() : "";
                Glide.with(holder.itemView.getContext()).load(url).placeholder(
                        R.drawable.ic_memory_placeholder).centerCrop().into(holder.memoryImage);
            }
        }
        }

    @Override
    public int getItemCount()
        {
        return memoryList.size();
        }

    public static class MemoryViewHolder extends RecyclerView.ViewHolder
        {
        TextView name, price;
        Button addToCartButton;
        ImageButton buttonRemoveFromCart;
        ImageButton buttonAddMoreToCart;
        View layoutCartActions;
        TextView textQuantity;
        ImageView memoryImage;

        public MemoryViewHolder(@NonNull View itemView)
            {
            super(itemView);
            name = itemView.findViewById(R.id.memoryName);
            price = itemView.findViewById(R.id.memoryPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            buttonRemoveFromCart = itemView.findViewById(R.id.buttonRemoveFromCartMemory);
            buttonAddMoreToCart = itemView.findViewById(R.id.buttonAddMoreToCartMemory);
            layoutCartActions = itemView.findViewById(R.id.layoutCartActionsMemory);
            textQuantity = itemView.findViewById(R.id.textQuantityMemory);
            memoryImage = itemView.findViewById(R.id.memoryImage);
            }
        }

    static class ViewMoreViewHolder extends MemoryViewHolder
        {
        public ViewMoreViewHolder(@NonNull View itemView)
            {
            super(itemView);
            }
        }
    }
