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
import com.windriver.pcgate.adapter.CpuAdapter;
import com.windriver.pcgate.model.CpuItem;
import com.windriver.pcgate.ui.DetailView.CpuDetailsActivity;

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
        RecyclerView recyclerView = view.findViewById(R.id.allCpusRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        CpuAdapter adapter = new CpuAdapter(allCpus, R.layout.item_cpu);
        adapter.setOnAddToCartClickListener(addToCartClickListener);
        adapter.setOnItemClickListener(item ->
            {
                android.content.Intent intent = new android.content.Intent(getContext(),
                        CpuDetailsActivity.class);
                intent.putExtra("name", item.name);
                intent.putExtra("price", item.price);
                intent.putExtra("imageUrl", item.imageUrl);
                intent.putExtra("boost_clock", item.boost_clock);
                intent.putExtra("core_clock", item.core_clock);
                intent.putExtra("core_count", item.core_count);
                intent.putExtra("graphics", item.graphics);
                intent.putExtra("smt", item.smt);
                intent.putExtra("socket", item.socket);
                intent.putExtra("tdp", item.tdp);
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
        dialog.setTitle("All CPUs");
        return dialog;
        }
    }
