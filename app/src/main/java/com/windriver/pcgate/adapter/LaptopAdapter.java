package com.windriver.pcgate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.model.LaptopItem;

import java.util.List;

public class LaptopAdapter extends RecyclerView.Adapter<LaptopAdapter.LaptopViewHolder>
    {
    private List<LaptopItem> laptopList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_LAPTOP = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    private List<LaptopItem> allLaptops = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;

    public LaptopAdapter(List<LaptopItem> laptopList)
        {
        this(laptopList, R.layout.item_laptop);
        }

    public LaptopAdapter(List<LaptopItem> laptopList, int layoutResId)
        {
        this.laptopList = laptopList;
        this.layoutResId = layoutResId;
        }

    public interface OnAddToCartClickListener
        {
        void onAddToCart(LaptopItem item);
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

    public void setAllLaptops(List<LaptopItem> allLaptops)
        {
        this.allLaptops = allLaptops;
        }

    public List<LaptopItem> getAllLaptops()
        {
        return allLaptops;
        }

    public interface OnItemClickListener
        {
        void onItemClick(LaptopItem item);
        }

    public void setOnItemClickListener(OnItemClickListener listener)
        {
        this.itemClickListener = listener;
        }

    @Override
    public int getItemViewType(int position)
        {
        LaptopItem item = laptopList.get(position);
        if ("__VIEW_MORE__".equals(item.model))
        {
            return TYPE_VIEW_MORE;
        }
        return TYPE_LAPTOP;
        }

    @NonNull
    @Override
    public LaptopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        int layoutToUse = layoutResId;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutToUse, parent, false);
        if (viewType == TYPE_VIEW_MORE)
        {
            return new ViewMoreViewHolder(view);
        }
        else
        {
            return new LaptopViewHolder(view);
        }
        }

    @Override
    public void onBindViewHolder(@NonNull LaptopViewHolder holder, int position)
        {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_VIEW_MORE)
        {
            ViewMoreViewHolder viewMoreHolder = (ViewMoreViewHolder) holder;
            viewMoreHolder.model.setText("View More");
            viewMoreHolder.price.setText("");
            viewMoreHolder.addToCartButton.setVisibility(View.GONE);
            if (viewMoreHolder.laptopImage != null)
            {
                viewMoreHolder.laptopImage.setVisibility(View.VISIBLE);
                viewMoreHolder.laptopImage.setImageResource(R.drawable.ic_laptop_placeholder);
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
            LaptopItem item = laptopList.get(position);
            holder.model.setText(item.model);
            holder.price.setText(item.price);
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
            if (holder.laptopImage != null)
            {
                String url = item.imageUrl != null ? item.imageUrl : "";
                Glide.with(holder.itemView.getContext()).load(url).placeholder(
                        R.drawable.ic_laptop_placeholder).centerCrop().into(holder.laptopImage);
            }
        }
        }

    @Override
    public int getItemCount()
        {
        return laptopList.size();
        }

    public void setLaptopList(List<LaptopItem> newList)
        {
        this.laptopList = newList;
        notifyDataSetChanged();
        }

    static class LaptopViewHolder extends RecyclerView.ViewHolder
        {
        TextView model, price;
        Button addToCartButton;
        ImageView laptopImage;

        public LaptopViewHolder(@NonNull View itemView)
            {
            super(itemView);
            model = itemView.findViewById(R.id.laptopModel);
            price = itemView.findViewById(R.id.laptopPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            laptopImage = itemView.findViewById(R.id.laptopImage);
            }
        }

    static class ViewMoreViewHolder extends LaptopViewHolder
        {
        public ViewMoreViewHolder(@NonNull View itemView)
            {
            super(itemView);
            }
        }
    }
