package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.ImageAdapter;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CaseAdapter;
import com.example.myapplication.model.CaseItem;
import com.example.myapplication.ui.cart.CartItem;
import com.example.myapplication.ui.cart.CartViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

private final int[] images = {
        R.drawable.banner_image1,
        R.drawable.banner_image2,
        R.drawable.banner_image3
};

@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater,
                         @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home, container, false);
}

@Override
public void onViewCreated(@NonNull View view,
                          @Nullable Bundle savedInstanceState) {
    ViewPager2 viewPager = view.findViewById(R.id.viewPager);
    TabLayout tabLayout = view.findViewById(R.id.tabLayout);

    ImageAdapter adapter = new ImageAdapter(requireContext(), images);
    viewPager.setAdapter(adapter);

new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
    { // this works, i don't know quite why, but it does
                tab.setCustomView(R.layout.custom_tab);
            }
    ).attach();

    // Setup RecyclerView for cases
    RecyclerView caseRecyclerView = view.findViewById(R.id.caseRecyclerView);
    caseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    List<CaseItem> caseList = new ArrayList<>();
    CaseAdapter caseAdapter = new CaseAdapter(caseList);
    caseRecyclerView.setAdapter(caseAdapter);

// CartViewModel for cart operations
CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

// Add to Cart button logic
CaseAdapter.OnAddToCartClickListener addToCartClickListener = item ->
    {
        double price = 0.0;
        try
        {
            price = Double.parseDouble(item.price.replaceAll("[^0-9.]", ""));
        }
        catch (Exception ignored)
        {
        }
        CartItem cartItem = new CartItem(item.name, price, 1);
        cartViewModel.addItem(cartItem);
        android.widget.Toast.makeText(getContext(), "Added to cart",
                android.widget.Toast.LENGTH_SHORT).show();
    };
caseAdapter.setOnAddToCartClickListener(addToCartClickListener);

// Fetch all cases from Firebase, shuffle, pick 5 random, add 'View More' item
    DatabaseReference caseRef = FirebaseDatabase.getInstance().getReference().child("case");
caseRef.addListenerForSingleValueEvent(new ValueEventListener()
    {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot)
        {
        List<CaseItem> allCases = new ArrayList<>();
        for (DataSnapshot child : snapshot.getChildren())
        {
            String name = "";
            String price = "";
            String imageUrl = "";
            if (child.child("name").getValue() != null)
            {
                name = Objects.requireNonNull(child.child("name").getValue()).toString();
            }
            if (child.child("price").getValue() != null)
            {
                price = Objects.requireNonNull(child.child("price").getValue()).toString();
            }
            if (child.child("image-url").getValue() != null)
            {
                imageUrl = Objects.requireNonNull(child.child("image-url").getValue()).toString();
            }
            allCases.add(new CaseItem(name, price, imageUrl));
            }
        java.util.Collections.shuffle(allCases);
        List<CaseItem> randomFive = new ArrayList<>();
        for (int i = 0; i < Math.min(5, allCases.size()); i++)
        {
            randomFive.add(allCases.get(i));
        }
        // Add a special 'View More' item (use a flag or special name)
        CaseItem viewMoreItem = new CaseItem("__VIEW_MORE__", "", "");
        randomFive.add(viewMoreItem);
        caseAdapter.setCaseList(randomFive);
        caseAdapter.setAllCases(allCases); // For grid dialog
        }

    @Override
    public void onCancelled(@NonNull DatabaseError error)
        {
        android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
        android.widget.Toast.makeText(getContext(), "Failed to load cases: " + error.getMessage(),
                android.widget.Toast.LENGTH_SHORT).show();
        }
    });

// Handle 'View More' click
caseAdapter.setOnViewMoreClickListener(() ->
    {
        // Show dialog/fragment with all cases in a grid (3 per row)
        AllCasesDialog dialog = new AllCasesDialog(caseAdapter.getAllCases(), addToCartClickListener
                // pass the same listener
        );
        dialog.show(getParentFragmentManager(), "AllCasesDialog");
    });
}
}