package com.windriver.pcgate.ui.Cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.windriver.pcgate.databinding.FragmentCartBinding;

public class CartFragment extends Fragment
    {
    private FragmentCartBinding binding;
    private CartAdapter cartAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        com.windriver.pcgate.ui.Cart.CartViewModel cartViewModel = com.windriver.pcgate.ui.Cart.CartViewModel.getInstance();
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerCartItems;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(new java.util.ArrayList<>());
        recyclerView.setAdapter(cartAdapter);

        final TextView textTotal = binding.textTotal;
        Button buttonCheckout = binding.buttonCheckout;
        // buttonCheckout.setOnClickListener(v -> {/* TODO: implement checkout */});

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
            {
                cartAdapter.setCartItems(items);
            });
        cartViewModel.getTotalSum().observe(getViewLifecycleOwner(), sum ->
            {
                textTotal.setText(String.format("Total: $%.2f", sum));
            });
        return root;
        }

    @Override
    public void onDestroyView()
        {
        super.onDestroyView();
        binding = null;
        }
    }