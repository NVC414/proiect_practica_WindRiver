package com.windriver.pcgate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.windriver.pcgate.R;
import com.windriver.pcgate.model.CpuItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class CpuAdapter extends RecyclerView.Adapter<CpuAdapter.CpuViewHolder>
    {
    private List<CpuItem> cpuList;
    private OnAddToCartClickListener addToCartClickListener;
    private OnItemClickListener itemClickListener;
    private OnViewMoreClickListener viewMoreClickListener;
    private final int layoutResId;
    private List<CpuItem> allCpus = new java.util.ArrayList<>();
    // Add a map to track cart quantities
    private java.util.Map<String, Integer> cartQuantities = new java.util.HashMap<>();
    private OnRemoveFromCartClickListener removeFromCartClickListener;
    private OnAddMoreToCartClickListener addMoreToCartClickListener;

    public CpuAdapter(List<CpuItem> cpuList)
        {
        this(cpuList, R.layout.item_cpu);
        }

    public CpuAdapter(List<CpuItem> cpuList, int layoutResId)
        {
        this.cpuList = cpuList;
        this.layoutResId = layoutResId;
        }

    public interface OnAddToCartClickListener
        {
        void onAddToCart(CpuItem item);
        }

    public void setOnAddToCartClickListener(OnAddToCartClickListener listener)
        {
        this.addToCartClickListener = listener;
        }

    public interface OnItemClickListener
        {
        void onItemClick(CpuItem item);
        }

    public void setOnItemClickListener(OnItemClickListener listener)
        {
        this.itemClickListener = listener;
        }

    public interface OnViewMoreClickListener
        {
        void onViewMore();
        }

    public void setOnViewMoreClickListener(OnViewMoreClickListener listener)
        {
        this.viewMoreClickListener = listener;
        }

    public interface OnRemoveFromCartClickListener {
        void onRemoveFromCart(CpuItem item);
    }
    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener) {
        this.removeFromCartClickListener = listener;
    }
    public interface OnAddMoreToCartClickListener {
        void onAddMoreToCart(CpuItem item);
    }
    public void setOnAddMoreToCartClickListener(OnAddMoreToCartClickListener listener) {
        this.addMoreToCartClickListener = listener;
    }

    @NonNull
    @Override
    public CpuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new CpuViewHolder(view);
        }

    // Call this when cart changes
    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities) {
        this.cartQuantities = cartQuantities;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CpuViewHolder holder, int position)
        {
        CpuItem item = cpuList.get(position);
        if ("__VIEW_MORE__".equals(item.name))
        {
            holder.name.setText("View More");
            holder.price.setText("");
            holder.addToCartButton.setVisibility(View.GONE);
            holder.layoutCartActionsCpu.setVisibility(View.GONE);
            holder.image.setImageResource(R.drawable.ic_cpu_placeholder);
            holder.itemView.setOnClickListener(v ->
                {
                    if (viewMoreClickListener != null)
                    {
                        viewMoreClickListener.onViewMore();
                    }
                });
        }
        else
        {
            holder.name.setText(item.name);
            holder.price.setText(String.format("$%.2f", item.price));
            Glide.with(holder.itemView.getContext()).load(item.imageUrl).placeholder(
                    R.drawable.ic_cpu_placeholder).centerCrop().into(holder.image);
            int quantity = cartQuantities.containsKey(item.name) ? cartQuantities.get(item.name) : 0;
            if (quantity > 0) {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.layoutCartActionsCpu.setVisibility(View.VISIBLE);
                holder.textQuantityCpu.setText(String.valueOf(quantity));
            } else {
                holder.addToCartButton.setVisibility(View.VISIBLE);
                holder.layoutCartActionsCpu.setVisibility(View.GONE);
            }
            holder.addToCartButton.setOnClickListener(v ->
                {
                    if (addToCartClickListener != null)
                    {
                        addToCartClickListener.onAddToCart(item);
                    }
                });
            holder.buttonAddMoreToCartCpu.setOnClickListener(v -> {
                if (addMoreToCartClickListener != null) {
                    addMoreToCartClickListener.onAddMoreToCart(item);
                }
            });
            holder.buttonRemoveFromCartCpu.setOnClickListener(v -> {
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
        }
        }

    @Override
    public int getItemCount()
        {
        return cpuList.size();
        }

    public void setCpuList(List<CpuItem> newList)
        {
        this.cpuList = newList;
        notifyDataSetChanged();
        }

    public void setAllCpus(List<CpuItem> allCpus)
        {
        this.allCpus = allCpus;
        }

    public List<CpuItem> getAllCpus()
        {
        return allCpus;
        }

    static class CpuViewHolder extends RecyclerView.ViewHolder
        {
        TextView name, price, textQuantityCpu;
        ImageView image;
        Button addToCartButton;
        LinearLayout layoutCartActionsCpu;
        ImageButton buttonRemoveFromCartCpu, buttonAddMoreToCartCpu;
        public CpuViewHolder(@NonNull View itemView)
            {
            super(itemView);
            name = itemView.findViewById(R.id.cpuName);
            price = itemView.findViewById(R.id.cpuPrice);
            image = itemView.findViewById(R.id.cpuImage);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCartCpu);
            layoutCartActionsCpu = itemView.findViewById(R.id.layoutCartActionsCpu);
            textQuantityCpu = itemView.findViewById(R.id.textQuantityCpu);
            buttonRemoveFromCartCpu = itemView.findViewById(R.id.buttonRemoveFromCartCpu);
            buttonAddMoreToCartCpu = itemView.findViewById(R.id.buttonAddMoreToCartCpu);
            }
        }
    }
