package com.windriver.pcgate.ui.viewAll;

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
import com.windriver.pcgate.adapter.MemoryAdapter;
import com.windriver.pcgate.adapter.MemoryAdapter.OnAddToCartClickListener;
import com.windriver.pcgate.model.MemoryItem;
import com.windriver.pcgate.ui.cart.CartItem;
import com.windriver.pcgate.ui.cart.CartViewModel;

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
        CartViewModel cartViewModel = CartViewModel.getInstance();
        MemoryAdapter adapter = new MemoryAdapter(allMemory, R.layout.item_memory_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                android.content.Intent intent = new android.content.Intent(getContext(),
                        com.windriver.pcgate.ui.detailView.MemoryDetailsActivity.class);
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("ddr_type", item.getDdrType());
                intent.putExtra("color", item.getColor());
                intent.putExtra("cas_latency", item.getCasLatency());
                intent.putExtra("first_word_latency", item.getFirstWordLatency());
                intent.putIntegerArrayListExtra("modules", new java.util.ArrayList<>(item.getModules()));
                intent.putIntegerArrayListExtra("speed", new java.util.ArrayList<>(item.getSpeed()));
                startActivity(intent);
            });
        adapter.setOnRemoveFromCartClickListener(item -> {
            double priceValue = 0.0;
            try { priceValue = Double.parseDouble(String.valueOf(item.getPrice())); } catch (Exception ignored) {}
            java.util.List<CartItem> current = cartViewModel.getCartItems().getValue();
            if (current != null) {
                for (CartItem ci : current) {
                    if (ci.getName().equals(item.getName())) {
                        int newQty = ci.getQuantity() - 1;
                        if (newQty > 0) {
                            cartViewModel.addItem(new CartItem(item.getName(), priceValue, -1));
                        } else {
                            cartViewModel.addItem(new CartItem(item.getName(), priceValue, -ci.getQuantity()));
                        }
                        break;
                    }
                }
            }
        });
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            java.util.Map<String, Integer> qtys = new java.util.HashMap<>();
            if (items != null) {
                for (CartItem ci : items) {
                    qtys.put(ci.getName(), ci.getQuantity());
                }
            }
            adapter.setCartQuantities(qtys);
        });
        recyclerView.setAdapter(adapter);
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
