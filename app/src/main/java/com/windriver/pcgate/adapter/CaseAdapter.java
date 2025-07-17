package com.windriver.pcgate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.windriver.pcgate.R;
import com.windriver.pcgate.model.CaseItem;

import java.util.List;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseViewHolder>
    {
    private List<CaseItem> caseList;
    private OnAddToCartClickListener addToCartClickListener;
    private static final int TYPE_CASE = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private OnViewMoreClickListener viewMoreClickListener;
    private List<CaseItem> allCases = new java.util.ArrayList<>();
    private final int layoutResId;
    private OnItemClickListener itemClickListener;

    // Add a map to track cart quantities
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

    public void setAllCases(List<CaseItem> allCases)
        {
        this.allCases = allCases;
        }

    public List<CaseItem> getAllCases()
        {
        return allCases;
        }

    public interface OnItemClickListener
        {
        void onItemClick(CaseItem item);
        }

    public void setOnItemClickListener(OnItemClickListener listener)
        {
        this.itemClickListener = listener;
        }

    // Call this when cart changes
    public void setCartQuantities(java.util.Map<String, Integer> cartQuantities)
        {
        this.cartQuantities = cartQuantities;
        notifyDataSetChanged();
        }

    @Override
    public int getItemViewType(int position)
        {
        CaseItem item = caseList.get(position);
        if ("__VIEW_MORE__".equals(item.name))
        {
            return TYPE_VIEW_MORE;
        }
        return TYPE_CASE;
        }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        int layoutToUse = layoutResId;
        if (viewType == TYPE_VIEW_MORE)
        {
            layoutToUse = layoutResId; // Always use the same layout for 'View More'
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutToUse, parent, false);
        if (viewType == TYPE_VIEW_MORE)
        {
            return new ViewMoreViewHolder(view);
        }
        else
        {
            return new CaseViewHolder(view);
        }
        }

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
            holder.name.setText(item.name);
            holder.price.setText(item.price);
            int quantity = cartQuantities.containsKey(item.name) ? cartQuantities.get(
                    item.name) : 0;
            // Always reset visibility before animation logic
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
            // Animation logic
            boolean wasInCart = holder.layoutCartActions.getVisibility() == View.VISIBLE;
            boolean nowInCart = quantity > 0;
            if (nowInCart)
            {
                if (!wasInCart)
                {
                    holder.addToCartButton.clearAnimation();
                    holder.layoutCartActions.clearAnimation();
                    Animation splitOut = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                            R.anim.button_split_out);
                    Animation splitIn = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                            R.anim.button_split_in);
                    splitOut.setAnimationListener(
                            new android.view.animation.Animation.AnimationListener()
                                {
                                @Override
                                public void onAnimationStart(
                                        android.view.animation.Animation animation)
                                    {
                                    }

                                @Override
                                public void onAnimationRepeat(
                                        android.view.animation.Animation animation)
                                    {
                                    }

                                @Override
                                public void onAnimationEnd(
                                        android.view.animation.Animation animation)
                                    {
                                    holder.addToCartButton.setVisibility(View.GONE);
                                    holder.layoutCartActions.setVisibility(View.VISIBLE);
                                    holder.layoutCartActions.startAnimation(splitIn);
                                    }
                                });
                    holder.addToCartButton.startAnimation(splitOut);
                }
                else
                {
                    holder.addToCartButton.setVisibility(View.GONE);
                    holder.layoutCartActions.setVisibility(View.VISIBLE);
                }
                // Set quantity text if present
                TextView textQuantity = holder.itemView.findViewById(R.id.textQuantity);
                if (textQuantity != null)
                {
                    textQuantity.setText(String.valueOf(quantity));
                }
            }
            else
            {
                if (wasInCart)
                {
                    holder.addToCartButton.clearAnimation();
                    holder.layoutCartActions.clearAnimation();
                    Animation splitOut = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                            R.anim.button_split_out);
                    Animation splitIn = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                            R.anim.button_split_in);
                    splitOut.setAnimationListener(
                            new android.view.animation.Animation.AnimationListener()
                                {
                                @Override
                                public void onAnimationStart(
                                        android.view.animation.Animation animation)
                                    {
                                    }

                                @Override
                                public void onAnimationRepeat(
                                        android.view.animation.Animation animation)
                                    {
                                    }

                                @Override
                                public void onAnimationEnd(
                                        android.view.animation.Animation animation)
                                    {
                                    holder.layoutCartActions.setVisibility(View.GONE);
                                    holder.addToCartButton.setVisibility(View.VISIBLE);
                                    holder.addToCartButton.startAnimation(splitIn);
                                    }
                                });
                    holder.layoutCartActions.startAnimation(splitOut);
                }
                else
                {
                    holder.addToCartButton.setVisibility(View.VISIBLE);
                    holder.layoutCartActions.setVisibility(View.GONE);
                }
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
                String url = item.imageUrl != null ? item.imageUrl : "";
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
        this.caseList = newList;
        notifyDataSetChanged();
        }

    // Add new interface for remove action
    public interface OnRemoveFromCartClickListener
        {
        void onRemoveFromCart(CaseItem item);
        }

    private OnRemoveFromCartClickListener removeFromCartClickListener;

    public void setOnRemoveFromCartClickListener(OnRemoveFromCartClickListener listener)
        {
        this.removeFromCartClickListener = listener;
        }

    static class CaseViewHolder extends RecyclerView.ViewHolder
        {
        TextView name, price;
        Button addToCartButton;
        Button buttonRemoveFromCart;
        Button buttonAddMoreToCart;
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
