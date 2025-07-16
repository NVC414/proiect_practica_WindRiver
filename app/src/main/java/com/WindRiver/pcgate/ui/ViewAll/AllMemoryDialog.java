package com.windriver.pcgate.ui.ViewAll;

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

import com.windriver.pcgate.R;
import com.windriver.pcgate.adapter.ShopItemAdapter;
import com.windriver.pcgate.model.MemoryItem;

import java.util.List;

public class AllMemoryDialog extends DialogFragment
    {
    private final List<MemoryItem> allMemory;
    private final ShopItemAdapter.OnAddToCartListener addToCartClickListener;

    public AllMemoryDialog(List<MemoryItem> allMemory,
                           ShopItemAdapter.OnAddToCartListener addToCartClickListener)
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
        ShopItemAdapter<MemoryItem> adapter = new ShopItemAdapter<>(allMemory);
        adapter.setOnAddToCartListener(addToCartClickListener);
        adapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                MemoryItem mem = (MemoryItem) item;
                android.content.Intent intent = new android.content.Intent(getContext(),
                        com.windriver.pcgate.ui.DetailView.MemoryDetailsActivity.class);
                intent.putExtra("name", mem.name);
                intent.putExtra("price", mem.price);
                intent.putExtra("imageUrl", mem.imageUrl);
                intent.putExtra("ddr_type", mem.ddr_type);
                intent.putExtra("color", mem.color);
                intent.putExtra("cas_latency", mem.cas_latency);
                intent.putExtra("first_word_latency", mem.first_word_latency);
                // Add other extras as needed
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
