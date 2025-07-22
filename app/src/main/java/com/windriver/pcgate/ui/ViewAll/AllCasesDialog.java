package com.windriver.pcgate.ui.ViewAll;

import android.app.Dialog;
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
import com.windriver.pcgate.adapter.CaseAdapter;
import com.windriver.pcgate.adapter.CaseAdapter.OnAddToCartClickListener;
import com.windriver.pcgate.model.CaseItem;
import com.windriver.pcgate.ui.Cart.CartItem;
import com.windriver.pcgate.ui.Cart.CartViewModel;

import java.util.List;

public class AllCasesDialog extends DialogFragment
    {
    private final List<CaseItem> allCases;
    private final OnAddToCartClickListener addToCartClickListener;

    public AllCasesDialog(List<CaseItem> allCases, OnAddToCartClickListener addToCartClickListener)
        {
        this.allCases = allCases;
        this.addToCartClickListener = addToCartClickListener;
        }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
        {
        return inflater.inflate(R.layout.dialog_all_cases, container, false);
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.allCasesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        CartViewModel cartViewModel = CartViewModel.getInstance();
        CaseAdapter adapter = new CaseAdapter(allCases, R.layout.item_case_grid);

        adapter.setOnAddToCartClickListener(addToCartClickListener);
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
        adapter.setOnItemClickListener(item -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), com.windriver.pcgate.ui.DetailView.CaseDetailsActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("type", item.getType());
            intent.putExtra("color", item.getColor());
            intent.putExtra("side_panel", item.getSidePanel());
            intent.putExtra("psu", item.getPsu());
            intent.putExtra("internal_35_bays", item.getInternal35Bays());
            intent.putExtra("external_volume", item.getExternalVolume());
            startActivity(intent);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
        {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("All Cases");
        return dialog;
        }
    }
