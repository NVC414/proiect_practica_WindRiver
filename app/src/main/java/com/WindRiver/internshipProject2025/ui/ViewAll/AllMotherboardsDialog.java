package com.WindRiver.internshipProject2025.ui.ViewAll;

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

import com.WindRiver.internshipProject2025.R;
import com.WindRiver.internshipProject2025.adapter.MotherboardAdapter;
import com.WindRiver.internshipProject2025.adapter.MotherboardAdapter.OnAddToCartClickListener;
import com.WindRiver.internshipProject2025.model.MotherboardItem;
import com.WindRiver.internshipProject2025.ui.DetailView.MotherboardDetailsActivity;

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
        MotherboardAdapter adapter = new MotherboardAdapter(allMotherboards, R.layout.item_case_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item -> {
            if ("__VIEW_MORE__".equals(item.name)) {
                return;
            }
            Intent intent = new Intent(getContext(), MotherboardDetailsActivity.class);
            intent.putExtra("name", item.name);
            intent.putExtra("price", item.price);
            intent.putExtra("imageUrl", item.imageUrl);
            intent.putExtra("color", item.color != null ? item.color : "");
            intent.putExtra("ddr_type", item.ddrType != null ? item.ddrType : "");
            intent.putExtra("form_factor", item.formFactor != null ? item.formFactor : "");
            intent.putExtra("socket", item.socket != null ? item.socket : "");
            intent.putExtra("max_memory", item.maxMemory);
            intent.putExtra("memory_slots", item.memorySlots);
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
