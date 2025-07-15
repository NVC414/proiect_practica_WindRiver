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
import com.windriver.pcgate.adapter.CaseAdapter;
import com.windriver.pcgate.adapter.CaseAdapter.OnAddToCartClickListener;
import com.windriver.pcgate.model.CaseItem;
import com.windriver.pcgate.ui.DetailView.CaseDetailsActivity;

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
        View view = inflater.inflate(R.layout.dialog_all_cases, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.allCasesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        CaseAdapter adapter = new CaseAdapter(allCases, R.layout.item_case_grid);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.name))
                {
                    return;
                }
                Intent intent = new Intent(getContext(), CaseDetailsActivity.class);
                intent.putExtra("name", item.name);
                intent.putExtra("price", item.price);
                intent.putExtra("imageUrl", item.imageUrl);
                intent.putExtra("color", item.color != null ? item.color : "");
                intent.putExtra("type", item.type != null ? item.type : "");
                intent.putExtra("side_panel", item.side_panel != null ? item.side_panel : "");
                intent.putExtra("psu", item.psu != null ? item.psu : "");
                intent.putExtra("internal_35_bays", item.internal_35_bays);
                intent.putExtra("external_volume", item.external_volume);
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
        dialog.setTitle("All Cases");
        return dialog;
        }
    }
