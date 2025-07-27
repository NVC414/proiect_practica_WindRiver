package com.windriver.pcgate.adapter;

import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.model.firebaseItems.CpuItem;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CpuAdapter extends RecyclerView.Adapter<CpuAdapter.CpuViewHolder>
    {
    private final List<CpuItem> cpuList;
    private OnAddToCartClickListener addToCartClickListener;
    private OnItemClickListener itemClickListener;
    private OnViewMoreClickListener viewMoreClickListener;
    private final int layoutResId;
    @Getter
    @Setter
    private List<CpuItem> allCpus = new java.util.ArrayList<>();

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

    public interface OnRemoveFromCartClickListener
        {
        void onRemoveFromCart(CpuItem item);
        }

    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener)
        {
        this.removeFromCartClickListener = listener;
        }

    public interface OnAddMoreToCartClickListener
        {
        void onAddMoreToCart(CpuItem item);
        }

    public void setOnAddMoreToCartClickListener(OnAddMoreToCartClickListener listener)
        {
        this.addMoreToCartClickListener = listener;
        }

    @NonNull
    @Override
    public CpuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new CpuViewHolder(view);
        }


    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities)
        {
        this.cartQuantities = cartQuantities;
        notifyItemRangeChanged(0, getItemCount());
        }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CpuViewHolder holder, int position)
        {
        CpuItem item = cpuList.get(position);
        if ("__VIEW_MORE__".equals(item.getName()))
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
            holder.name.setText(item.getName());
            holder.price.setText(String.format("$%.2f", item.getPrice()));
            Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).placeholder(
                    R.drawable.ic_cpu_placeholder).centerCrop().into(holder.image);
            Integer quantityObj = cartQuantities.get(item.getName());
            int quantity = (quantityObj != null) ? quantityObj : 0;
            if (quantity > 0)
            {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.layoutCartActionsCpu.setVisibility(View.VISIBLE);
                holder.textQuantityCpu.setText(String.valueOf(quantity));
            }
            else
            {
                holder.addToCartButton.setVisibility(View.VISIBLE);
                holder.layoutCartActionsCpu.setVisibility(View.GONE);
            }
            holder.addToCartButton.setOnClickListener(v ->
                {
                    if (addToCartClickListener != null)
                    {
                        addToCartClickListener.onAddToCart(item);
                        notifyItemChanged(holder.getBindingAdapterPosition());
                    }
                });
            holder.buttonAddMoreToCartCpu.setOnClickListener(v ->
                {
                    if (addMoreToCartClickListener != null)
                    {
                        addMoreToCartClickListener.onAddMoreToCart(item);
                        notifyItemChanged(holder.getBindingAdapterPosition());
                    }
                });
            holder.buttonRemoveFromCartCpu.setOnClickListener(v ->
                {
                    if (removeFromCartClickListener != null)
                    {
                        removeFromCartClickListener.onRemoveFromCart(item);
                        notifyItemChanged(holder.getBindingAdapterPosition());
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
        androidx.recyclerview.widget.DiffUtil.DiffResult diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(
                new androidx.recyclerview.widget.DiffUtil.Callback()
                    {
                    @Override
                    public int getOldListSize()
                        {
                        return cpuList.size();
                        }

                    @Override
                    public int getNewListSize()
                        {
                        return newList.size();
                        }

                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
                        {
                        CpuItem oldItem = cpuList.get(oldItemPosition);
                        CpuItem newItem = newList.get(newItemPosition);
                        return oldItem.getName().equals(newItem.getName());
                        }

                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
                        {
                        CpuItem oldItem = cpuList.get(oldItemPosition);
                        CpuItem newItem = newList.get(newItemPosition);
                        return oldItem.equals(newItem);
                        }
                    });
        cpuList.clear();
        cpuList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        }

    public static class CpuViewHolder extends RecyclerView.ViewHolder
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
