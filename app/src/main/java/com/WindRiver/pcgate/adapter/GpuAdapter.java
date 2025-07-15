package com.windriver.pcgate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.windriver.pcgate.R;
import com.windriver.pcgate.model.GpuItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class GpuAdapter extends RecyclerView.Adapter<GpuAdapter.GpuViewHolder> {
    private List<GpuItem> gpuList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_GPU = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    private List<GpuItem> allGpus = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;

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

    public void setAllGpus(List<GpuItem> allGpus) {
        this.allGpus = allGpus;
    }

    public List<GpuItem> getAllGpus() {
        return allGpus;
    }

    public interface OnItemClickListener {
        void onItemClick(GpuItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        GpuItem item = gpuList.get(position);
        if ("__VIEW_MORE__".equals(item.name)) {
            return TYPE_VIEW_MORE;
        }
        return TYPE_GPU;
    }

    @NonNull
    @Override
    public GpuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutToUse = layoutResId;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutToUse, parent, false);
        if (viewType == TYPE_VIEW_MORE) {
            return new ViewMoreViewHolder(view);
        } else {
            return new GpuViewHolder(view);
        }
    }

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
            holder.name.setText(item.name);
            holder.price.setText(item.price);
            holder.addToCartButton.setVisibility(View.VISIBLE);
            holder.addToCartButton.setOnClickListener(v -> {
                if (addToCartClickListener != null) {
                    addToCartClickListener.onAddToCart(item);
                }
            });
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item);
                }
            });
            if (holder.gpuImage != null) {
                String url = item.imageUrl != null ? item.imageUrl : "";
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

        public GpuViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.gpuName);
            price = itemView.findViewById(R.id.gpuPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCartGpu);
            gpuImage = itemView.findViewById(R.id.gpuImage);
        }
    }

    static class ViewMoreViewHolder extends GpuViewHolder {
        public ViewMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
