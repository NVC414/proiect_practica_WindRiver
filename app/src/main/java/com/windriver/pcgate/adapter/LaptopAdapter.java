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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.model.firebaseItems.LaptopItem;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class LaptopAdapter extends RecyclerView.Adapter<LaptopAdapter.LaptopViewHolder>
    {
    private List<LaptopItem> laptopList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_LAPTOP = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    @Getter
    @Setter
    private List<LaptopItem> allLaptops = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;
    private java.util.Map<String, Integer> cartQuantities = new java.util.HashMap<>();
    private OnRemoveFromCartClickListener removeFromCartClickListener;

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


    public interface OnItemClickListener
        {
        void onItemClick(LaptopItem item);
        }

    public void setOnItemClickListener(OnItemClickListener listener)
        {
        this.itemClickListener = listener;
        }

    public interface OnRemoveFromCartClickListener
        {
        void onRemoveFromCart(LaptopItem item);
        }

    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener)
        {
        this.removeFromCartClickListener = listener;
        }

    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities)
        {
        Map<String, Integer> oldCartQuantities = new java.util.HashMap<>(this.cartQuantities);
        this.cartQuantities = cartQuantities;
        for (int i = 0; i < laptopList.size(); i++)
        {
            LaptopItem item = laptopList.get(i);
            String key = item.getBrand() + "|" + item.getModel();
            Integer oldQty = oldCartQuantities.get(key);
            Integer newQty = cartQuantities.get(key);
            if ((oldQty == null && newQty != null) || (oldQty != null && !oldQty.equals(newQty)))
            {
                notifyItemChanged(i);
            }
        }
        }

    public void setLaptopList(List<LaptopItem> newList)
        {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback()
            {
            @Override
            public int getOldListSize()
                {
                return laptopList != null ? laptopList.size() : 0;
                }

            @Override
            public int getNewListSize()
                {
                return newList != null ? newList.size() : 0;
                }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
                {
                LaptopItem oldItem = laptopList.get(oldItemPosition);
                LaptopItem newItem = newList.get(newItemPosition);
                // Assuming model+brand uniquely identifies a laptop
                return oldItem.getModel().equals(newItem.getModel()) && oldItem.getBrand().equals(
                        newItem.getBrand());
                }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
                {
                LaptopItem oldItem = laptopList.get(oldItemPosition);
                LaptopItem newItem = newList.get(newItemPosition);
                return oldItem.equals(newItem);
                }
            });
        this.laptopList = newList;
        diffResult.dispatchUpdatesTo(this);
        }

    @Override
    public int getItemViewType(int position)
        {
        LaptopItem item = laptopList.get(position);
        if ("__VIEW_MORE__".equals(item.getModel()))
        {
            return TYPE_VIEW_MORE;
        }
        return TYPE_LAPTOP;
        }

    @NonNull
    @Override
    public LaptopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        if (viewType == TYPE_VIEW_MORE)
        {
            return new ViewMoreViewHolder(view);
        }
        else
        {
            return new LaptopViewHolder(view);
        }
        }

    @SuppressLint("SetTextI18n")
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
            holder.model.setText(item.getModel());
            holder.price.setText("$" + item.getPrice());
            String key = item.getBrand() + "|" + item.getModel();
            Integer quantityObj = cartQuantities.get(key);
            int quantity = (quantityObj != null) ? quantityObj : 0;
            if (quantity > 0)
            {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.layoutCartActions.setVisibility(View.VISIBLE);
                holder.textQuantity.setText(String.valueOf(quantity));
            }
            else
            {
                holder.addToCartButton.setVisibility(View.VISIBLE);
                holder.layoutCartActions.setVisibility(View.GONE);
            }
            holder.addToCartButton.setOnClickListener(v ->
                {
                    if (addToCartClickListener != null)
                    {
                        addToCartClickListener.onAddToCart(item);
                        notifyItemChanged(holder.getBindingAdapterPosition());
                    }
                });
            holder.buttonAddMoreToCart.setOnClickListener(v ->
                {
                    if (addToCartClickListener != null)
                    {
                        addToCartClickListener.onAddToCart(item);
                        notifyItemChanged(holder.getBindingAdapterPosition());
                    }
                });
            holder.buttonRemoveFromCart.setOnClickListener(v ->
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
            if (holder.laptopImage != null)
            {
                String url = item.getImageUrl() != null ? item.getImageUrl() : "";
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

    public static class LaptopViewHolder extends RecyclerView.ViewHolder
        {
        TextView model, price;
        Button addToCartButton;
        ImageView laptopImage;
        ImageButton buttonRemoveFromCart;
        ImageButton buttonAddMoreToCart;
        View layoutCartActions;
        TextView textQuantity;

        public LaptopViewHolder(@NonNull View itemView)
            {
            super(itemView);
            model = itemView.findViewById(R.id.laptopModel);
            price = itemView.findViewById(R.id.laptopPrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            laptopImage = itemView.findViewById(R.id.laptopImage);
            buttonRemoveFromCart = itemView.findViewById(R.id.buttonRemoveFromCartLaptop);
            buttonAddMoreToCart = itemView.findViewById(R.id.buttonAddMoreToCartLaptop);
            layoutCartActions = itemView.findViewById(R.id.layoutCartActionsLaptop);
            textQuantity = itemView.findViewById(R.id.textQuantityLaptop);
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
