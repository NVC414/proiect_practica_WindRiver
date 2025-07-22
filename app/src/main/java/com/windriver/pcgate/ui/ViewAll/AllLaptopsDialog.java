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
import com.windriver.pcgate.adapter.LaptopAdapter;
import com.windriver.pcgate.adapter.LaptopAdapter.OnAddToCartClickListener;
import com.windriver.pcgate.model.LaptopItem;

import java.util.List;

public class AllLaptopsDialog extends DialogFragment
    {
    private final List<LaptopItem> allLaptops;
    private final OnAddToCartClickListener addToCartClickListener;

    public AllLaptopsDialog(List<LaptopItem> allLaptops,
                            OnAddToCartClickListener addToCartClickListener)
        {
        this.allLaptops = allLaptops;
        this.addToCartClickListener = addToCartClickListener;
        }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
        {
        View view = inflater.inflate(R.layout.dialog_all_laptops, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.allLaptopsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        LaptopAdapter adapter = new LaptopAdapter(allLaptops, R.layout.item_laptop_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getModel()))
                {
                    return;
                }
                android.content.Intent intent = new android.content.Intent(getContext(),
                        com.windriver.pcgate.ui.DetailView.LaptopDetailsActivity.class);
                intent.putExtra("brand", item.getBrand());
                intent.putExtra("model", item.getModel());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("processor", item.getProcessor());
                intent.putExtra("ram_gb", item.getRamGb());
                intent.putExtra("ram_type", item.getRamType());
                intent.putExtra("graphic_card_gb", item.getGraphicCardGb());
                intent.putExtra("hdd", item.getHdd());
                intent.putExtra("ssd", item.getSsd());
                startActivity(intent);
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
        dialog.setTitle(getString(R.string.all_laptops));
        return dialog;
        }
    }
