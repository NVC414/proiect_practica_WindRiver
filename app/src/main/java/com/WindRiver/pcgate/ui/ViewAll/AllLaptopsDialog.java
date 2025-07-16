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
import com.windriver.pcgate.adapter.ShopItemAdapter;
import com.windriver.pcgate.model.ShopItem;

import java.util.List;

public class AllLaptopsDialog extends DialogFragment
    {
    private final List<ShopItem> allLaptops;
    private final ShopItemAdapter.OnAddToCartListener addToCartClickListener;

    public AllLaptopsDialog(List<ShopItem> allLaptops,
                            ShopItemAdapter.OnAddToCartListener addToCartClickListener)
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
        ShopItemAdapter<ShopItem> adapter = new ShopItemAdapter<>(allLaptops);
        adapter.setOnAddToCartListener(addToCartClickListener);
        adapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                android.content.Intent intent = new android.content.Intent(getContext(),
                        com.windriver.pcgate.ui.DetailView.LaptopDetailsActivity.class);
                // Cast to LaptopItem for laptop-specific fields
                if (item instanceof com.windriver.pcgate.model.LaptopItem)
                {
                    com.windriver.pcgate.model.LaptopItem laptop = (com.windriver.pcgate.model.LaptopItem) item;
                    intent.putExtra("brand", laptop.brand);
                    intent.putExtra("model", laptop.model);
                    intent.putExtra("price", laptop.price);
                    intent.putExtra("imageUrl", laptop.imageUrl);
                    intent.putExtra("processor", laptop.processor);
                    intent.putExtra("ram_gb", laptop.ram_gb);
                    intent.putExtra("ram_type", laptop.ram_type);
                    intent.putExtra("graphic_card_gb", laptop.graphic_card_gb);
                    intent.putExtra("hdd", laptop.hdd);
                    intent.putExtra("ssd", laptop.ssd);
                }
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
        {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.all_laptops));
        return dialog;
        }
    }
