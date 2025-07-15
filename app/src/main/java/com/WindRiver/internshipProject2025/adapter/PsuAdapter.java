package com.WindRiver.internshipProject2025.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.WindRiver.internshipProject2025.R;
import com.WindRiver.internshipProject2025.model.PsuItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class PsuAdapter extends RecyclerView.Adapter<PsuAdapter.PsuViewHolder> {
    private List<PsuItem> psuList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_PSU = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    private List<PsuItem> allPsus = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;

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

    public void setAllPsus(List<PsuItem> allPsus) {
        this.allPsus = allPsus;
    }

    public List<PsuItem> getAllPsus() {
        return allPsus;
    }

    public interface OnItemClickListener {
        void onItemClick(PsuItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        PsuItem item = psuList.get(position);
        if ("__VIEW_MORE__".equals(item.name)) {
            return TYPE_VIEW_MORE;
        }
        return TYPE_PSU;
    }

    @NonNull
    @Override
    public PsuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutToUse = layoutResId;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutToUse, parent, false);
        if (viewType == TYPE_VIEW_MORE) {
            return new ViewMoreViewHolder(view);
        } else {
            return new PsuViewHolder(view);
        }
    }

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
            if (holder.psuImage != null) {
                String url = item.imageUrl != null ? item.imageUrl : "";
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
        ImageView psuImage;

        public PsuViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.psuName);
            price = itemView.findViewById(R.id.psuPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            psuImage = itemView.findViewById(R.id.psuImage);
        }
    }

    static class ViewMoreViewHolder extends PsuViewHolder {
        public ViewMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

