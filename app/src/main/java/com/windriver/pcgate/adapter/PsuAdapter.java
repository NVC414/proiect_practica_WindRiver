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

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.model.PsuItem;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class PsuAdapter extends RecyclerView.Adapter<PsuAdapter.PsuViewHolder>
{
    private List<PsuItem> psuList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_PSU = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    @Getter
    @Setter
    private List<PsuItem> allPsus = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;
    private java.util.Map<String, Integer> cartQuantities = new java.util.HashMap<>();

    public PsuAdapter(List<PsuItem> psuList) {
        this(psuList, R.layout.item_psu);
    }

    public PsuAdapter(List<PsuItem> psuList, int layoutResId) {
        this.psuList = psuList;
        this.layoutResId = layoutResId;
    }

    public interface OnAddToCartClickListener {
        void onAddToCart(PsuItem item);
    }

    public void setOnAddToCartClickListener(OnAddToCartClickListener listener) {
        this.addToCartClickListener = listener;
    }

    public interface OnViewMoreClickListener {
        void onViewMore();
    }

    public void setOnViewMoreClickListener(OnViewMoreClickListener listener) {
        this.viewMoreClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(PsuItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities) {
        this.cartQuantities = cartQuantities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        PsuItem item = psuList.get(position);
        if ("__VIEW_MORE__".equals(item.getName())) {
            return TYPE_VIEW_MORE;
        }
        return TYPE_PSU;
    }

    @NonNull
    @Override
    public PsuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        if (viewType == TYPE_VIEW_MORE) {
            return new ViewMoreViewHolder(view);
        } else {
            return new PsuViewHolder(view);
        }
    }

    public interface OnRemoveFromCartClickListener {
        void onRemoveFromCart(PsuItem item);
    }

    private OnRemoveFromCartClickListener removeFromCartClickListener;

    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener) {
        this.removeFromCartClickListener = listener;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PsuViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_VIEW_MORE) {
            ViewMoreViewHolder viewMoreHolder = (ViewMoreViewHolder) holder;
            viewMoreHolder.name.setText("View More");
            viewMoreHolder.price.setText("");
            viewMoreHolder.addToCartButton.setVisibility(View.GONE);
            if (viewMoreHolder.psuImage != null) {
                viewMoreHolder.psuImage.setVisibility(View.VISIBLE);
                viewMoreHolder.psuImage.setImageResource(R.drawable.ic_psu_placeholder);
            }
            viewMoreHolder.itemView.setOnClickListener(v -> {
                if (viewMoreClickListener != null) {
                    viewMoreClickListener.onViewMore();
                }
            });
        } else {
            PsuItem item = psuList.get(position);
            holder.name.setText(item.getName());
            holder.price.setText("$" + item.getPrice());
            int quantity = cartQuantities.getOrDefault(item.getName(), 0);

            // Use correct IDs for cart actions and quantity (for both list and grid layouts)
            View layoutCartActions = holder.itemView.findViewById(R.id.layoutCartActionsPsu) != null ?
                holder.itemView.findViewById(R.id.layoutCartActionsPsu) : holder.itemView.findViewById(R.id.layoutCartActions);
            Button addToCartButton = holder.itemView.findViewById(R.id.buttonAddToCart);
            ImageButton buttonAddMoreToCart = holder.itemView.findViewById(R.id.buttonAddMoreToCartPsu) != null ?
                holder.itemView.findViewById(R.id.buttonAddMoreToCartPsu) : holder.itemView.findViewById(R.id.buttonAddMoreToCart);
            ImageButton buttonRemoveFromCart = holder.itemView.findViewById(R.id.buttonRemoveFromCartPsu) != null ?
                holder.itemView.findViewById(R.id.buttonRemoveFromCartPsu) : holder.itemView.findViewById(R.id.buttonRemoveFromCart);
            TextView textQuantity = holder.itemView.findViewById(R.id.textQuantityPsu) != null ?
                holder.itemView.findViewById(R.id.textQuantityPsu) : holder.itemView.findViewById(R.id.textQuantity);

            if (addToCartButton != null && layoutCartActions != null) {
                if (quantity > 0) {
                    addToCartButton.setVisibility(View.GONE);
                    layoutCartActions.setVisibility(View.VISIBLE);
                } else {
                    addToCartButton.setVisibility(View.VISIBLE);
                    layoutCartActions.setVisibility(View.GONE);
                }
            }
            if (textQuantity != null) {
                textQuantity.setText(String.valueOf(quantity));
            }
            if (addToCartButton != null) {
                addToCartButton.setOnClickListener(v -> {
                    if (addToCartClickListener != null) {
                        addToCartClickListener.onAddToCart(item);
                    }
                });
            }
            if (buttonAddMoreToCart != null) {
                buttonAddMoreToCart.setOnClickListener(v -> {
                    if (addToCartClickListener != null) {
                        addToCartClickListener.onAddToCart(item);
                    }
                });
            }
            if (buttonRemoveFromCart != null) {
                buttonRemoveFromCart.setOnClickListener(v -> {
                    if (removeFromCartClickListener != null) {
                        removeFromCartClickListener.onRemoveFromCart(item);
                    }
                });
            }
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item);
                }
            });
            if (holder.psuImage != null) {
                String url = item.getImageUrl() != null ? item.getImageUrl() : "";
                Glide.with(holder.itemView.getContext()).load(url).placeholder(
                        R.drawable.ic_psu_placeholder).centerCrop().into(holder.psuImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return psuList.size();
    }

    public void setPsuList(List<PsuItem> newList) {
        this.psuList = newList;
        notifyDataSetChanged();
    }

    static class PsuViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        Button addToCartButton;
        ImageButton buttonRemoveFromCart;
        ImageButton buttonAddMoreToCart;
        View layoutCartActions;
        ImageView psuImage;

        public PsuViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.psuName);
            price = itemView.findViewById(R.id.psuPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            buttonRemoveFromCart = itemView.findViewById(R.id.buttonRemoveFromCart);
            buttonAddMoreToCart = itemView.findViewById(R.id.buttonAddMoreToCart);
            layoutCartActions = itemView.findViewById(R.id.layoutCartActions);
            psuImage = itemView.findViewById(R.id.psuImage);
        }
    }

    static class ViewMoreViewHolder extends PsuViewHolder {
        public ViewMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
