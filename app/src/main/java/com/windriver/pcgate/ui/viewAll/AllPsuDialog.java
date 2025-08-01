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
import com.windriver.pcgate.adapter.PsuAdapter;
import com.windriver.pcgate.adapter.PsuAdapter.OnAddToCartClickListener;
import com.windriver.pcgate.model.firebaseItems.PsuItem;
import com.windriver.pcgate.ui.cart.CartViewModel;
import com.windriver.pcgate.ui.detailView.PsuDetailsActivity;

import java.util.List;

public class AllPsuDialog extends DialogFragment
    {
    private final List<PsuItem> allPsus;
    private final OnAddToCartClickListener addToCartClickListener;

    public AllPsuDialog(List<PsuItem> allPsus, OnAddToCartClickListener addToCartClickListener)
        {
        this.allPsus = allPsus;
        this.addToCartClickListener = addToCartClickListener;
        }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
        {
        View view = inflater.inflate(R.layout.dialog_all_psu, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.allPsuRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        PsuAdapter adapter = new PsuAdapter(allPsus, R.layout.item_psu_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        CartViewModel cartViewModel = CartViewModel.getInstance();
        adapter.setOnRemoveFromCartClickListener(item ->
            {
                double priceValue = 0.0;
                try
                {
                    priceValue = Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", ""));
                }
                catch (Exception ignored)
                {
                }
                java.util.List<com.windriver.pcgate.ui.cart.CartItem> current = cartViewModel.getCartItems().getValue();
                if (current != null)
                {
                    for (com.windriver.pcgate.ui.cart.CartItem ci : current)
                    {
                        if (ci.getName().equals(item.getName()))
                        {
                            int newQty = ci.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(
                                        new com.windriver.pcgate.ui.cart.CartItem(item.getName(),
                                                priceValue, -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new com.windriver.pcgate.ui.cart.CartItem(item.getName(),
                                                priceValue, -ci.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            });
        adapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                Intent intent = new Intent(getContext(), PsuDetailsActivity.class);
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("color", item.getColor() != null ? item.getColor() : "");
                intent.putExtra("efficiency",
                        item.getEfficiency() != null ? item.getEfficiency() : "");
                intent.putExtra("modular", item.getModular() != null ? item.getModular() : "");
                intent.putExtra("type", item.getType() != null ? item.getType() : "");
                intent.putExtra("wattage", item.getWattage());
                startActivity(intent);
            });
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
            {
                java.util.Map<String, Integer> qtys = new java.util.HashMap<>();
                if (items != null)
                {
                    for (com.windriver.pcgate.ui.cart.CartItem ci : items)
                    {
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
        dialog.setTitle(getString(R.string.all_psu));
        return dialog;
        }
    }
