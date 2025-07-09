package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.CaseItem;
import java.util.List;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseViewHolder> {
    private List<CaseItem> caseList;

    public CaseAdapter(List<CaseItem> caseList) {
        this.caseList = caseList;
    }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_case, parent, false);
        return new CaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
        CaseItem item = caseList.get(position);
        holder.name.setText(item.name);
        holder.price.setText(item.price);
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
        public CaseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.caseName);
            price = itemView.findViewById(R.id.casePrice);
        }
    }
}

