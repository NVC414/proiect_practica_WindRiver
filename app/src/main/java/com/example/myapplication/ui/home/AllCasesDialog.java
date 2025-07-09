package com.example.myapplication.ui.home;

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

import com.example.myapplication.R;
import com.example.myapplication.adapter.CaseAdapter;
import com.example.myapplication.adapter.CaseAdapter.OnAddToCartClickListener;
import com.example.myapplication.model.CaseItem;

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
