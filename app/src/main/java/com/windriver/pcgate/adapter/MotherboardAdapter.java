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
import com.windriver.pcgate.model.MotherboardItem;
import com.bumptech.glide.Glide;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MotherboardAdapter extends RecyclerView.Adapter<MotherboardAdapter.MotherboardViewHolder> {
    private List<MotherboardItem> motherboardList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_MOTHERBOARD = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    @Getter
    @Setter
    private List<MotherboardItem> allMotherboards = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;
    private java.util.Map<String, Integer> cartQuantities = new java.util.HashMap<>();
    private OnRemoveFromCartClickListener removeFromCartClickListener;

    public MotherboardAdapter(List<MotherboardItem> motherboardList) {
        this(motherboardList, R.layout.item_motherboard);
    }

    public MotherboardAdapter(List<MotherboardItem> motherboardList, int layoutResId) {
        this.motherboardList = motherboardList;
        this.layoutResId = layoutResId;
    }

    public interface OnAddToCartClickListener {
        void onAddToCart(MotherboardItem item);
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
        void onItemClick(MotherboardItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities) {
        this.cartQuantities = cartQuantities;
        notifyDataSetChanged();
    }

    public interface OnRemoveFromCartClickListener {
        void onRemoveFromCart(MotherboardItem item);
    }

    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener) {
        this.removeFromCartClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        MotherboardItem item = motherboardList.get(position);
        if ("__VIEW_MORE__".equals(item.getName())) {
            return TYPE_VIEW_MORE;
        }
        return TYPE_MOTHERBOARD;
    }

    @NonNull
    @Override
    public MotherboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        if (viewType == TYPE_VIEW_MORE) {
            return new ViewMoreViewHolder(view);
        } else {
            return new MotherboardViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MotherboardViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_VIEW_MORE) {
            ViewMoreViewHolder viewMoreHolder = (ViewMoreViewHolder) holder;
            viewMoreHolder.name.setText("View More");
            viewMoreHolder.price.setText("");
            viewMoreHolder.addToCartButton.setVisibility(View.GONE);
            if (viewMoreHolder.motherboardImage != null) {
                viewMoreHolder.motherboardImage.setVisibility(View.VISIBLE);
                viewMoreHolder.motherboardImage.setImageResource(R.drawable.ic_motherboard_placeholder);
            }
            viewMoreHolder.itemView.setOnClickListener(v -> {
                if (viewMoreClickListener != null) {
                    viewMoreClickListener.onViewMore();
                }
            });
        } else {
            MotherboardItem item = motherboardList.get(position);
            holder.name.setText(item.getName());
            holder.price.setText("$" + item.getPrice());
            int quantity = cartQuantities.getOrDefault(item.getName(), 0);

            if (quantity > 0) {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.layoutCartActions.setVisibility(View.VISIBLE);
            } else {
                holder.addToCartButton.setVisibility(View.VISIBLE);
                holder.layoutCartActions.setVisibility(View.GONE);
            }

            if (quantity > 0) {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.layoutCartActions.setVisibility(View.VISIBLE);
                if (holder.textQuantity != null) {
                    holder.textQuantity.setText(String.valueOf(quantity));
                }
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
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item);
                }
            });
            if (holder.motherboardImage != null) {
                String url = item.getImageUrl() != null ? item.getImageUrl() : "";
                Glide.with(holder.itemView.getContext()).load(url).placeholder(
                        R.drawable.ic_motherboard_placeholder).centerCrop().into(holder.motherboardImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return motherboardList.size();
    }

    public void setMotherboardList(List<MotherboardItem> newList) {
        this.motherboardList = newList;
        notifyDataSetChanged();
    }

    static class MotherboardViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        Button addToCartButton;
        ImageButton buttonRemoveFromCart;
        ImageButton buttonAddMoreToCart;
        View layoutCartActions;
        ImageView motherboardImage;
        TextView textQuantity;

        public MotherboardViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.motherboardName);
            price = itemView.findViewById(R.id.motherboardPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCartMotherboard);
            buttonRemoveFromCart = itemView.findViewById(R.id.buttonRemoveFromCartMotherboard);
            buttonAddMoreToCart = itemView.findViewById(R.id.buttonAddMoreToCartMotherboard);
            layoutCartActions = itemView.findViewById(R.id.layoutCartActionsMotherboard);
            motherboardImage = itemView.findViewById(R.id.motherboardImage);
            textQuantity = itemView.findViewById(R.id.textQuantityMotherboard);
        }
    }

    static class ViewMoreViewHolder extends MotherboardViewHolder {
        public ViewMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
