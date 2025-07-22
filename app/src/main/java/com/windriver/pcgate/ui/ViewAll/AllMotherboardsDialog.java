package com.windriver.pcgate.ui.ViewAll;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.windriver.pcgate.R;
import com.windriver.pcgate.adapter.MotherboardAdapter;
import com.windriver.pcgate.adapter.MotherboardAdapter.OnAddToCartClickListener;
import com.windriver.pcgate.model.MotherboardItem;
import com.windriver.pcgate.ui.Cart.CartItem;
import com.windriver.pcgate.ui.Cart.CartViewModel;
import com.windriver.pcgate.ui.DetailView.MotherboardDetailsActivity;

import java.util.List;

public class AllMotherboardsDialog extends DialogFragment {
    private final List<MotherboardItem> allMotherboards;
    private final OnAddToCartClickListener addToCartClickListener;

    public AllMotherboardsDialog(List<MotherboardItem> allMotherboards, OnAddToCartClickListener addToCartClickListener) {
        this.allMotherboards = allMotherboards;
        this.addToCartClickListener = addToCartClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_all_cases, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.allCasesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        CartViewModel cartViewModel = CartViewModel.getInstance();
        MotherboardAdapter adapter = new MotherboardAdapter(allMotherboards, R.layout.item_motherboard_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item -> {
            if ("__VIEW_MORE__".equals(item.getName())) {
                return;
            }
            Intent intent = new Intent(getContext(), MotherboardDetailsActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("color", item.getColor() != null ? item.getColor() : "");
            intent.putExtra("ddr_type", item.getDdrType() != null ? item.getDdrType() : "");
            intent.putExtra("form_factor", item.getFormFactor() != null ? item.getFormFactor() : "");
            intent.putExtra("socket", item.getSocket() != null ? item.getSocket() : "");
            intent.putExtra("max_memory", item.getMaxMemory());
            intent.putExtra("memory_slots", item.getMemorySlots());
            startActivity(intent);
        });
        adapter.setOnRemoveFromCartClickListener(item -> {
            double priceValue = 0.0;
            try { priceValue = Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", "")); } catch (Exception ignored) {}
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
        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.white);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("All Motherboards");
        return dialog;
    }
}
