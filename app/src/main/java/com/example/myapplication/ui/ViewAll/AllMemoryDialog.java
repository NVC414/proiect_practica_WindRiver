package com.example.myapplication.ui.ViewAll;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MemoryAdapter;
import com.example.myapplication.adapter.MemoryAdapter.OnAddToCartClickListener;
import com.example.myapplication.model.MemoryItem;

import java.util.List;

public class AllMemoryDialog extends DialogFragment
    {
    private final List<MemoryItem> allMemory;
    private final OnAddToCartClickListener addToCartClickListener;

    public AllMemoryDialog(List<MemoryItem> allMemory,
                           OnAddToCartClickListener addToCartClickListener)
        {
        this.allMemory = allMemory;
        this.addToCartClickListener = addToCartClickListener;
        }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
        {
        View view = inflater.inflate(R.layout.dialog_all_memory, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.allMemoryRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        MemoryAdapter adapter = new MemoryAdapter(allMemory, R.layout.item_memory_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.name))
                {
                    return;
                }
                android.content.Intent intent = new android.content.Intent(getContext(),
                        com.example.myapplication.ui.DetailView.MemoryDetailsActivity.class);
                intent.putExtra("name", item.name);
                intent.putExtra("price", item.price);
                intent.putExtra("imageUrl", item.imageUrl);
                intent.putExtra("ddr_type", item.ddr_type);
                intent.putExtra("color", item.color);
                intent.putExtra("cas_latency", item.cas_latency);
                intent.putExtra("first_word_latency", item.first_word_latency);
                intent.putIntegerArrayListExtra("modules", new java.util.ArrayList<>(item.modules));
                intent.putIntegerArrayListExtra("speed", new java.util.ArrayList<>(item.speed));
                startActivity(intent);
            });
        recyclerView.setAdapter(adapter);
        // Add back button
        View backButton = view.findViewById(R.id.buttonBack);
        if (backButton != null)
        {
            backButton.setOnClickListener(v -> dismiss());
        }
        return view;
        }

    @Override
    public void onStart()
        {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null)
        {
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.white);
        }
        }
    }
