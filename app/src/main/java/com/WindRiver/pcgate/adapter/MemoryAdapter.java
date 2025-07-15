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
import com.windriver.pcgate.model.MemoryItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>
    {
    private List<MemoryItem> memoryList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_MEMORY = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    private List<MemoryItem> allMemory = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;

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

    public void setAllMemory(List<MemoryItem> allMemory)
        {
        this.allMemory = allMemory;
        }

    public List<MemoryItem> getAllMemory()
        {
        return allMemory;
        }

    public interface OnItemClickListener
        {
        void onItemClick(MemoryItem item);
        }

    public void setOnItemClickListener(OnItemClickListener listener)
        {
        this.itemClickListener = listener;
        }

    @Override
    public int getItemViewType(int position)
        {
        MemoryItem item = memoryList.get(position);
        if ("__VIEW_MORE__".equals(item.name))
        {
            return TYPE_VIEW_MORE;
        }
        return TYPE_MEMORY;
        }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        int layoutToUse = layoutResId;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutToUse, parent, false);
        if (viewType == TYPE_VIEW_MORE)
        {
            return new ViewMoreViewHolder(view);
        }
        else
        {
            return new MemoryViewHolder(view);
        }
        }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position)
        {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_VIEW_MORE)
        {
            ViewMoreViewHolder viewMoreHolder = (ViewMoreViewHolder) holder;
            viewMoreHolder.name.setText("View More");
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
            holder.name.setText(item.name);
            holder.price.setText(item.price + " RON");
            holder.addToCartButton.setVisibility(View.VISIBLE);
            holder.addToCartButton.setOnClickListener(v ->
                {
                    if (addToCartClickListener != null)
                    {
                        addToCartClickListener.onAddToCart(item);
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
                String url = item.imageUrl != null ? item.imageUrl : "";
                Glide.with(holder.itemView.getContext()).load(url).placeholder(
                        R.drawable.ic_image_placeholder).centerCrop().into(holder.memoryImage);
            }
        }
        }

    @Override
    public int getItemCount()
        {
        return memoryList.size();
        }

    public void setMemoryList(List<MemoryItem> newList)
        {
        this.memoryList = newList;
        notifyDataSetChanged();
        }

    static class MemoryViewHolder extends RecyclerView.ViewHolder
        {
        TextView name, price;
        Button addToCartButton;
        ImageView memoryImage;

        public MemoryViewHolder(@NonNull View itemView)
            {
            super(itemView);
            name = itemView.findViewById(R.id.memoryName);
            price = itemView.findViewById(R.id.memoryPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
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
