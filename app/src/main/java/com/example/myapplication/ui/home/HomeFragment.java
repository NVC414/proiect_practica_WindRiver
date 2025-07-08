package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.ImageAdapter;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CaseAdapter;
import com.example.myapplication.model.CaseItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> { // this works, i dont know quite why, but it does
                    tab.setCustomView(R.layout.custom_tab);
                }
        ).attach();

        // Setup RecyclerView for cases
        RecyclerView caseRecyclerView = view.findViewById(R.id.caseRecyclerView);
        caseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<CaseItem> caseList = new ArrayList<>();
        CaseAdapter caseAdapter = new CaseAdapter(caseList);
        caseRecyclerView.setAdapter(caseAdapter);

        // Fetch first 5 cases from Firebase
        DatabaseReference caseRef = FirebaseDatabase.getInstance().getReference().child("case");
        caseRef.limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CaseItem> newList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = "";
                    String price = "";
                    if (child.child("name").getValue() != null) {
                        name = child.child("name").getValue().toString();
                    }
                    if (child.child("price").getValue() != null) {
                        price = child.child("price").getValue().toString();
                    }
                    newList.add(new CaseItem(name, price));
                }
                caseAdapter.setCaseList(newList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}