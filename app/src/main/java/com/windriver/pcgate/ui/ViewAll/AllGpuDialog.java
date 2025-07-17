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
import com.windriver.pcgate.adapter.GpuAdapter;
import com.windriver.pcgate.model.GpuItem;
import com.windriver.pcgate.ui.DetailView.GpuDetailsActivity;

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
        GpuAdapter adapter = new GpuAdapter(allGpus, R.layout.item_gpu_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item -> {
            if ("__VIEW_MORE__".equals(item.name)) {
                return;
            }
            Intent intent = new Intent(getContext(), GpuDetailsActivity.class);
            intent.putExtra("name", item.name);
            intent.putExtra("price", item.price);
            intent.putExtra("imageUrl", item.imageUrl);
            intent.putExtra("color", item.color != null ? item.color : "");
            intent.putExtra("chipset", item.chipset != null ? item.chipset : "");
            intent.putExtra("core_clock", item.core_clock != null ? item.core_clock : "");
            intent.putExtra("boost_clock", item.boost_clock != null ? item.boost_clock : "");
            intent.putExtra("memory", item.memory);
            intent.putExtra("length", item.length);
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
        dialog.setTitle("All GPUs");
        return dialog;
    }
}
