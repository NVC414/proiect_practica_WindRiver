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
import com.windriver.pcgate.model.GpuItem;
import com.bumptech.glide.Glide;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class GpuAdapter extends RecyclerView.Adapter<GpuAdapter.GpuViewHolder> {
    private List<GpuItem> gpuList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_GPU = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    @Getter
    @Setter
    private List<GpuItem> allGpus = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;
    private java.util.Map<String, Integer> cartQuantities = new java.util.HashMap<>();
    private OnRemoveFromCartClickListener removeFromCartClickListener;

    public GpuAdapter(List<GpuItem> gpuList) {
        this(gpuList, R.layout.item_gpu);
    }

    public GpuAdapter(List<GpuItem> gpuList, int layoutResId) {
        this.gpuList = gpuList;
        this.layoutResId = layoutResId;
    }

    public interface OnAddToCartClickListener {
        void onAddToCart(GpuItem item);
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
        void onItemClick(GpuItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities) {
        this.cartQuantities = cartQuantities;
        notifyDataSetChanged();
    }

    public interface OnRemoveFromCartClickListener {
        void onRemoveFromCart(GpuItem item);
    }

    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener) {
        this.removeFromCartClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        GpuItem item = gpuList.get(position);
        if ("__VIEW_MORE__".equals(item.getName())) {
            return TYPE_VIEW_MORE;
        }
        return TYPE_GPU;
    }

    @NonNull
    @Override
    public GpuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        if (viewType == TYPE_VIEW_MORE) {
            return new ViewMoreViewHolder(view);
        } else {
            return new GpuViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GpuViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_VIEW_MORE) {
            ViewMoreViewHolder viewMoreHolder = (ViewMoreViewHolder) holder;
            viewMoreHolder.name.setText("View More");
            viewMoreHolder.price.setText("");
            viewMoreHolder.addToCartButton.setVisibility(View.GONE);
            if (viewMoreHolder.gpuImage != null) {
                viewMoreHolder.gpuImage.setVisibility(View.VISIBLE);
                viewMoreHolder.gpuImage.setImageResource(R.drawable.ic_gpu_placeholder);
                viewMoreHolder.gpuImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                viewMoreHolder.gpuImage.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                viewMoreHolder.gpuImage.setPadding(0, 0, 0, 0);
            }
            viewMoreHolder.itemView.setOnClickListener(v -> {
                if (viewMoreClickListener != null) {
                    viewMoreClickListener.onViewMore();
                }
            });
        } else {
            GpuItem item = gpuList.get(position);
            holder.name.setText(item.getName());
            holder.price.setText("$"+item.getPrice());
            int quantity = cartQuantities.getOrDefault(item.getName(), 0);

            if (quantity > 0) {
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
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item);
                }
            });
            if (holder.gpuImage != null) {
                String url = item.getImageUrl() != null ? item.getImageUrl() : "";
                holder.gpuImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                holder.gpuImage.setBackgroundColor(android.graphics.Color.WHITE);
                holder.gpuImage.setPadding(8, 8, 8, 8);
                Glide.with(holder.itemView.getContext()).load(url).placeholder(
                        R.drawable.ic_gpu_placeholder).into(holder.gpuImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return gpuList.size();
    }

    public void setGpuList(List<GpuItem> newList) {
        this.gpuList = newList;
        notifyDataSetChanged();
    }

    static class GpuViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        Button addToCartButton;
        ImageView gpuImage;
        ImageButton buttonRemoveFromCart;
        ImageButton buttonAddMoreToCart;
        View layoutCartActions;
        TextView textQuantity;

        public GpuViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.gpuName);
            price = itemView.findViewById(R.id.gpuPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCartGpu);
            gpuImage = itemView.findViewById(R.id.gpuImage);
            buttonRemoveFromCart = itemView.findViewById(R.id.buttonRemoveFromCartGpu);
            buttonAddMoreToCart = itemView.findViewById(R.id.buttonAddMoreToCartGpu);
            layoutCartActions = itemView.findViewById(R.id.layoutCartActionsGpu);
            textQuantity = itemView.findViewById(R.id.textQuantityGpu);
        }
    }

    static class ViewMoreViewHolder extends GpuViewHolder {
        public ViewMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
