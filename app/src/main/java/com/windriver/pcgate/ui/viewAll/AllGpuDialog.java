package com.windriver.pcgate.ui.viewAll;

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
import com.windriver.pcgate.adapter.GpuAdapter;
import com.windriver.pcgate.model.GpuItem;
import com.windriver.pcgate.ui.cart.CartItem;
import com.windriver.pcgate.ui.cart.CartViewModel;
import com.windriver.pcgate.ui.detailView.GpuDetailsActivity;

import java.util.List;

public class AllGpuDialog extends DialogFragment {
    private final List<GpuItem> allGpus;
    private final GpuAdapter.OnAddToCartClickListener addToCartClickListener;

    public AllGpuDialog(List<GpuItem> allGpus, GpuAdapter.OnAddToCartClickListener addToCartClickListener) {
        this.allGpus = allGpus;
        this.addToCartClickListener = addToCartClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_all_gpu, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.allGpuRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        CartViewModel cartViewModel = CartViewModel.getInstance();
        GpuAdapter adapter = new GpuAdapter(allGpus, R.layout.item_gpu_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item -> {
            if ("__VIEW_MORE__".equals(item.getName())) {
                return;
            }
            Intent intent = new Intent(getContext(), GpuDetailsActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("color", item.getColor() != null ? item.getColor() : "");
            intent.putExtra("chipset", item.getChipset() != null ? item.getChipset() : "");
            intent.putExtra("core_clock", item.getCoreClock() != null ? item.getCoreClock() : "");
            intent.putExtra("boost_clock", item.getBoostClock() != null ? item.getBoostClock() : "");
            intent.putExtra("memory", item.getMemory());
            intent.putExtra("length", item.getLength());
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
        dialog.setTitle("All GPUs");
        return dialog;
    }
}
