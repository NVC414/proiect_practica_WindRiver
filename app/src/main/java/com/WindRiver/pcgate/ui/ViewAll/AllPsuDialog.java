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
import com.windriver.pcgate.model.PsuItem;
import com.windriver.pcgate.ui.DetailView.PsuDetailsActivity;

import java.util.List;

public class AllPsuDialog extends DialogFragment {
    private final List<PsuItem> allPsus;
private final ShopItemAdapter.OnAddToCartListener addToCartClickListener;

public AllPsuDialog(List<PsuItem> allPsus,
                    ShopItemAdapter.OnAddToCartListener addToCartClickListener)
    {
        this.allPsus = allPsus;
        this.addToCartClickListener = addToCartClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_all_psu, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.allPsuRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    ShopItemAdapter<PsuItem> adapter = new ShopItemAdapter<>(allPsus);
    adapter.setOnAddToCartListener(addToCartClickListener);
        adapter.setOnItemClickListener(item -> {
            if ("__VIEW_MORE__".equals(item.getName()))
            {
                return;
            }
            PsuItem psu = (PsuItem) item;
            Intent intent = new Intent(getContext(), PsuDetailsActivity.class);
            intent.putExtra("name", psu.name);
            intent.putExtra("price", psu.price);
            intent.putExtra("imageUrl", psu.imageUrl);
            intent.putExtra("color", psu.color != null ? psu.color : "");
            intent.putExtra("efficiency", psu.efficiency != null ? psu.efficiency : "");
            intent.putExtra("modular", psu.modular != null ? psu.modular : "");
            intent.putExtra("type", psu.type != null ? psu.type : "");
            intent.putExtra("wattage", psu.wattage);
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
        dialog.setTitle(getString(R.string.all_psu));
        return dialog;
    }
}
