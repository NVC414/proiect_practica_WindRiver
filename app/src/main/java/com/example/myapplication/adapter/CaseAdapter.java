package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.CaseItem;

import java.util.List;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseViewHolder> {
    private List<CaseItem> caseList;
private OnAddToCartClickListener addToCartClickListener;
private static final int TYPE_CASE = 0;
private static final int TYPE_VIEW_MORE = 1;
private OnViewMoreClickListener viewMoreClickListener;
private List<CaseItem> allCases = new java.util.ArrayList<>();
private final int layoutResId;

    public CaseAdapter(List<CaseItem> caseList) {
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
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
    int viewType = getItemViewType(position);
    if (viewType == TYPE_VIEW_MORE)
    {
        ViewMoreViewHolder viewMoreHolder = (ViewMoreViewHolder) holder;
        viewMoreHolder.name.setText("View More");
        viewMoreHolder.price.setText("");
        viewMoreHolder.addToCartButton.setVisibility(View.GONE);
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
        holder.addToCartButton.setVisibility(View.VISIBLE);
        holder.addToCartButton.setOnClickListener(v ->
            {
                if (addToCartClickListener != null)
                {
                    addToCartClickListener.onAddToCart(item);
                }
            });
    }
    }

    @Override
    public int getItemCount() {
        return caseList.size();
    }

    public void setCaseList(List<CaseItem> newList) {
        this.caseList = newList;
        notifyDataSetChanged();
    }

    static class CaseViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
    Button addToCartButton;
        public CaseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.caseName);
            price = itemView.findViewById(R.id.casePrice);
        addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
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
