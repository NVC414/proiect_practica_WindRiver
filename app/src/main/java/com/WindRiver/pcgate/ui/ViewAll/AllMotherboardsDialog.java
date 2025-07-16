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
import com.windriver.pcgate.adapter.ShopItemAdapter;
import com.windriver.pcgate.model.MotherboardItem;
import com.windriver.pcgate.ui.DetailView.MotherboardDetailsActivity;

import java.util.List;

public class AllMotherboardsDialog extends DialogFragment {
    private final List<MotherboardItem> allMotherboards;
private final ShopItemAdapter.OnAddToCartListener addToCartClickListener;

public AllMotherboardsDialog(List<MotherboardItem> allMotherboards,
                             ShopItemAdapter.OnAddToCartListener addToCartClickListener)
    {
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
    ShopItemAdapter<MotherboardItem> adapter = new ShopItemAdapter<>(allMotherboards);
    adapter.setOnAddToCartListener(addToCartClickListener);
        adapter.setOnItemClickListener(item -> {
            if ("__VIEW_MORE__".equals(item.getName()))
            {
                return;
            }
            MotherboardItem mb = (MotherboardItem) item;
            Intent intent = new Intent(getContext(), MotherboardDetailsActivity.class);
            intent.putExtra("name", mb.name);
            intent.putExtra("price", mb.price);
            intent.putExtra("imageUrl", mb.imageUrl);
            intent.putExtra("color", mb.color != null ? mb.color : "");
            intent.putExtra("ddr_type", mb.ddrType != null ? mb.ddrType : "");
            intent.putExtra("form_factor", mb.formFactor != null ? mb.formFactor : "");
            intent.putExtra("socket", mb.socket != null ? mb.socket : "");
            intent.putExtra("max_memory", mb.maxMemory);
            intent.putExtra("memory_slots", mb.memorySlots);
            startActivity(intent);
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("All Motherboards");
        return dialog;
    }
}
