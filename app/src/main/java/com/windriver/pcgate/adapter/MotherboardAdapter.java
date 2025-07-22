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
import com.windriver.pcgate.model.MotherboardItem;
import com.bumptech.glide.Glide;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MotherboardAdapter extends RecyclerView.Adapter<MotherboardAdapter.MotherboardViewHolder> {
    private List<MotherboardItem> motherboardList;
    private OnAddToCartClickListener addToCartClickListener;
    private OnItemClickListener itemClickListener;
    private final int layoutResId;
    @Getter
    @Setter
    private List<MotherboardItem> allMotherboards;

    public MotherboardAdapter(List<MotherboardItem> motherboardList) {
        this(motherboardList, R.layout.item_case);
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

    public interface OnItemClickListener {
        void onItemClick(MotherboardItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnViewMoreClickListener {
        void onViewMore();
    }

    private OnViewMoreClickListener viewMoreClickListener;

    public void setOnViewMoreClickListener(OnViewMoreClickListener listener) {
        this.viewMoreClickListener = listener;
    }

    @NonNull
    @Override
    public MotherboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new MotherboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MotherboardViewHolder holder, int position) {
        if (position == motherboardList.size() - 1 && viewMoreClickListener != null) {

            holder.name = holder.itemView.findViewById(R.id.caseName);
            holder.name.setText(R.string.view_more);
            holder.price = holder.itemView.findViewById(R.id.casePrice);
            holder.price.setVisibility(View.GONE);
            holder.addToCartButton = holder.itemView.findViewById(R.id.buttonAddToCart);
            holder.addToCartButton.setVisibility(View.GONE);
            holder.motherboardImage = holder.itemView.findViewById(R.id.caseImage);
            if (holder.motherboardImage != null) {
                holder.motherboardImage.setVisibility(View.VISIBLE);
                holder.motherboardImage.setImageResource(R.drawable.ic_motherboard_placeholder);
            }
            holder.itemView.setOnClickListener(v -> viewMoreClickListener.onViewMore());
        } else {
            MotherboardItem item = motherboardList.get(position);

            holder.name = holder.itemView.findViewById(R.id.caseName);
            holder.price = holder.itemView.findViewById(R.id.casePrice);
            holder.addToCartButton = holder.itemView.findViewById(R.id.buttonAddToCart);
            holder.motherboardImage = holder.itemView.findViewById(R.id.caseImage);
            holder.name.setText(item.getName());
            holder.price.setText(item.getPrice());
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
        ImageView motherboardImage;

        public MotherboardViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
