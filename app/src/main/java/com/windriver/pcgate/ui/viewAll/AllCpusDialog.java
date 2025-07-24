package com.windriver.pcgate.ui.viewAll;

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
import com.windriver.pcgate.adapter.CpuAdapter;
import com.windriver.pcgate.model.CpuItem;
import com.windriver.pcgate.ui.cart.CartItem;
import com.windriver.pcgate.ui.cart.CartViewModel;

import java.util.List;

public class AllCpusDialog extends DialogFragment
    {
    private final List<CpuItem> allCpus;
    private final CpuAdapter.OnAddToCartClickListener addToCartClickListener;

    public AllCpusDialog(List<CpuItem> allCpus,
                         CpuAdapter.OnAddToCartClickListener addToCartClickListener)
        {
        this.allCpus = allCpus;
        this.addToCartClickListener = addToCartClickListener;
        }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
        {
        View view = inflater.inflate(R.layout.dialog_all_cpus, container, false);

        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> dismiss());
        return view;
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.allCpusRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        CartViewModel cartViewModel = CartViewModel.getInstance();
        CpuAdapter adapter = new CpuAdapter(allCpus, R.layout.item_cpu_grid);

        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnRemoveFromCartClickListener(item -> {
            java.util.List<CartItem> current = cartViewModel.getCartItems().getValue();
            if (current != null) {
                for (CartItem ci : current) {
                    if (ci.getName().equals(item.getName())) {
                        int newQty = ci.getQuantity() - 1;
                        if (newQty > 0) {
                            cartViewModel.addItem(new CartItem(item.getName(), item.getPrice(), -1));
                        } else {
                            cartViewModel.addItem(new CartItem(item.getName(), item.getPrice(), -ci.getQuantity()));
                        }
                        break;
                    }
                }
            }
        });
        adapter.setOnAddMoreToCartClickListener(item -> cartViewModel.addItem(new CartItem(item.getName(), item.getPrice(), 1)));
        adapter.setOnItemClickListener(item -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), com.windriver.pcgate.ui.detailView.CpuDetailsActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("boostClock", item.getBoostClock());
            intent.putExtra("coreClock", item.getCoreClock());
            intent.putExtra("coreCount", item.getCoreCount());
            intent.putExtra("graphics", item.getGraphics());
            intent.putExtra("smt", item.isSmt());
            intent.putExtra("socket", item.getSocket());
            intent.putExtra("tdp", item.getTdp());
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
        dialog.setTitle("All CPUs");
        return dialog;
        }
    }
