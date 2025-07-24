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

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.model.CaseItem;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseViewHolder>
    {
    private final List<CaseItem> caseList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_CASE = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    @Getter
    @Setter
    private List<CaseItem> allCases = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;


    private java.util.Map<String, Integer> cartQuantities = new java.util.HashMap<>();

    public CaseAdapter(List<CaseItem> caseList)
        {
        this(caseList, R.layout.item_case);
        }

    public CaseAdapter(List<CaseItem> caseList, int layoutResId)
        {
        this.caseList = caseList;
        this.layoutResId = layoutResId;
        }

    public interface OnAddToCartClickListener
        {
        void onAddToCart(CaseItem item);
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
        void onItemClick(CaseItem item);
        }

    public void setOnItemClickListener(OnItemClickListener listener)
        {
        this.itemClickListener = listener;
        }


    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities)
        {
        for (int i = 0; i < caseList.size(); i++) {
            CaseItem item = caseList.get(i);
            String key = item.getName();
            Integer oldQty = this.cartQuantities.get(key);
            Integer newQty = cartQuantities.get(key);
            if ((oldQty == null && newQty != null) || (oldQty != null && !oldQty.equals(newQty))) {
                notifyItemChanged(i);
            }
        }
        this.cartQuantities = cartQuantities;
        }

    @Override
    public int getItemViewType(int position)
        {
        CaseItem item = caseList.get(position);
        if ("__VIEW_MORE__".equals(item.getName()))
        {
            return TYPE_VIEW_MORE;
        }
        return TYPE_CASE;
        }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        if (viewType == TYPE_VIEW_MORE)
        {
            return new ViewMoreViewHolder(view);
        }
        else
        {
            return new CaseViewHolder(view);
        }
        }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position)
        {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_VIEW_MORE)
        {
            ViewMoreViewHolder viewMoreHolder = (ViewMoreViewHolder) holder;
            viewMoreHolder.name.setText("View More");
            viewMoreHolder.price.setText("");
            viewMoreHolder.addToCartButton.setVisibility(View.GONE);
            if (viewMoreHolder.caseImage != null)
            {
                viewMoreHolder.caseImage.setVisibility(View.VISIBLE);
                viewMoreHolder.caseImage.setImageResource(R.drawable.ic_case_placeholder);
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
            CaseItem item = caseList.get(position);
            holder.name.setText(item.getName());
            holder.price.setText("$" + item.getPrice());
            Integer quantityObj = cartQuantities.getOrDefault(item.getName(), 0);
            int quantity = (quantityObj != null) ? quantityObj : 0;

            if (quantity > 0)
            {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.layoutCartActions.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.addToCartButton.setVisibility(View.VISIBLE);
                holder.layoutCartActions.setVisibility(View.GONE);
            }

            holder.layoutCartActions.getVisibility();
            boolean nowInCart = quantity > 0;
            if (nowInCart)
            {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.layoutCartActions.setVisibility(View.VISIBLE);

                TextView textQuantity = holder.itemView.findViewById(R.id.textQuantity);
                if (textQuantity != null)
                {
                    textQuantity.setText(String.valueOf(quantity));
                }
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
                    }
                });
            holder.buttonAddMoreToCart.setOnClickListener(v ->
                {
                    if (addToCartClickListener != null)
                    {
                        addToCartClickListener.onAddToCart(item);
                    }
                });
            holder.buttonRemoveFromCart.setOnClickListener(v ->
                {
                    if (removeFromCartClickListener != null)
                    {
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
            if (holder.caseImage != null)
            {
                String url = item.getImageUrl() != null ? item.getImageUrl() : "";
                Glide.with(holder.itemView.getContext()).load(url).placeholder(
                        R.drawable.ic_case_placeholder).centerCrop().into(holder.caseImage);
            }
        }
        }

    @Override
    public int getItemCount()
        {
        return caseList.size();
        }

    public void setCaseList(List<CaseItem> newList)
        {
        androidx.recyclerview.widget.DiffUtil.DiffResult diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(new androidx.recyclerview.widget.DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return caseList.size();
            }
            @Override
            public int getNewListSize() {
                return newList.size();
            }
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                CaseItem oldItem = caseList.get(oldItemPosition);
                CaseItem newItem = newList.get(newItemPosition);
                return oldItem.getName().equals(newItem.getName());
            }
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                CaseItem oldItem = caseList.get(oldItemPosition);
                CaseItem newItem = newList.get(newItemPosition);
                return oldItem.equals(newItem);
            }
        });
        caseList.clear();
        caseList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        }


    public interface OnRemoveFromCartClickListener
        {
        void onRemoveFromCart(CaseItem item);
        }

    private OnRemoveFromCartClickListener removeFromCartClickListener;

    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener)
        {
        this.removeFromCartClickListener = listener;
        }

    public static class CaseViewHolder extends RecyclerView.ViewHolder
        {
        TextView name, price;
        Button addToCartButton;
        ImageButton buttonRemoveFromCart;
        ImageButton buttonAddMoreToCart;
        View layoutCartActions;
        ImageView caseImage;

        public CaseViewHolder(@NonNull View itemView)
            {
            super(itemView);
            name = itemView.findViewById(R.id.caseName);
            price = itemView.findViewById(R.id.casePrice);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            buttonRemoveFromCart = itemView.findViewById(R.id.buttonRemoveFromCart);
            buttonAddMoreToCart = itemView.findViewById(R.id.buttonAddMoreToCart);
            layoutCartActions = itemView.findViewById(R.id.layoutCartActions);
            caseImage = itemView.findViewById(R.id.caseImage);
            }
        }

    static class ViewMoreViewHolder extends CaseViewHolder
        {
        public ViewMoreViewHolder(@NonNull View itemView)
            {
            super(itemView);
            }
        }
    }
