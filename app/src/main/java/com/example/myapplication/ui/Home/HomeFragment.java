package com.example.myapplication.ui.Home;

import android.content.Intent;
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

import com.example.myapplication.R;
import com.example.myapplication.adapter.CaseAdapter;
import com.example.myapplication.adapter.ImageAdapter;
import com.example.myapplication.model.CaseItem;
import com.example.myapplication.ui.Cart.CartItem;
import com.example.myapplication.ui.Cart.CartViewModel;
import com.example.myapplication.ui.DetailView.CaseDetailsActivity;
import com.example.myapplication.ui.DetailView.CpuDetailsActivity;
import com.example.myapplication.ui.ViewAll.AllCasesDialog;
import com.example.myapplication.ui.ViewAll.AllCpusDialog;
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

private static List<CaseItem> persistentRandomCases = null;
private static List<com.example.myapplication.model.CpuItem> persistentRandomCpus = null;
private static List<com.example.myapplication.model.MemoryItem> persistentRandomMemory = null;

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
            String color = "";
            String type = "";
            String side_panel = "";
            String psu = "";
            int internal_35_bays = 0;
            double external_volume = 0;
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
            if (child.child("color").getValue() != null)
            {
                color = Objects.requireNonNull(child.child("color").getValue()).toString();
            }
            if (child.child("type").getValue() != null)
            {
                type = Objects.requireNonNull(child.child("type").getValue()).toString();
            }
            if (child.child("side_panel").getValue() != null)
            {
                side_panel = Objects.requireNonNull(
                        child.child("side_panel").getValue()).toString();
            }
            if (child.child("psu").getValue() != null)
            {
                psu = Objects.requireNonNull(child.child("psu").getValue()).toString();
            }
            if (child.child("internal_35_bays").getValue() != null)
            {
                try
                {
                    internal_35_bays = Integer.parseInt(Objects.requireNonNull(
                            child.child("internal_35_bays").getValue()).toString());
                }
                catch (Exception ignored)
                {
                }
            }
            if (child.child("external_volume").getValue() != null)
            {
                try
                {
                    external_volume = Double.parseDouble(Objects.requireNonNull(
                            child.child("external_volume").getValue()).toString());
                }
                catch (Exception ignored)
                {
                }
            }
            allCases.add(new CaseItem(name, price, imageUrl, color, type, side_panel, psu,
                    internal_35_bays, external_volume));
        }
        if (persistentRandomCases == null)
        {
            java.util.Collections.shuffle(allCases);
            persistentRandomCases = new ArrayList<>();
            for (int i = 0; i < Math.min(5, allCases.size()); i++)
            {
                persistentRandomCases.add(allCases.get(i));
            }
            CaseItem viewMoreItem = new CaseItem("__VIEW_MORE__", "", "", "", "", "", "", 0, 0);
            persistentRandomCases.add(viewMoreItem);
        }
        caseAdapter.setCaseList(persistentRandomCases);
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

caseAdapter.setOnItemClickListener(item ->
    {
        if ("__VIEW_MORE__".equals(item.name))
        {
            return;
        }
        Intent intent = new Intent(getContext(), CaseDetailsActivity.class);
        intent.putExtra("name", item.name);
        intent.putExtra("price", item.price);
        intent.putExtra("imageUrl", item.imageUrl);
        // If you have more fields, fetch them from Firebase and add here
        // For demo, try to get extra fields from allCases
        for (CaseItem c : caseAdapter.getAllCases())
        {
            if (c.name.equals(item.name))
            {
                intent.putExtra("color", c.color != null ? c.color : "");
                intent.putExtra("type", c.type != null ? c.type : "");
                intent.putExtra("side_panel", c.side_panel != null ? c.side_panel : "");
                intent.putExtra("psu", c.psu != null ? c.psu : "");
                intent.putExtra("internal_35_bays", c.internal_35_bays);
                intent.putExtra("external_volume", c.external_volume);
                break;
            }
        }
        startActivity(intent);
    });

// Setup RecyclerView for CPUs
RecyclerView cpuRecyclerView = view.findViewById(R.id.cpuRecyclerView);
cpuRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
List<com.example.myapplication.model.CpuItem> cpuList = new ArrayList<>();
com.example.myapplication.adapter.CpuAdapter cpuAdapter = new com.example.myapplication.adapter.CpuAdapter(
        cpuList);
cpuRecyclerView.setAdapter(cpuAdapter);

// Add to Cart button logic for CPUs
com.example.myapplication.adapter.CpuAdapter.OnAddToCartClickListener addToCartClickListenerCpu = item ->
    {
        double price = item.price;
        CartItem cartItem = new CartItem(item.name, price, 1);
        cartViewModel.addItem(cartItem);
        android.widget.Toast.makeText(getContext(), "Added to cart",
                android.widget.Toast.LENGTH_SHORT).show();
    };
cpuAdapter.setOnAddToCartClickListener(addToCartClickListenerCpu);

// Fetch all CPUs from Firebase, shuffle, pick 5 random, add 'View More' item
DatabaseReference cpuRef = FirebaseDatabase.getInstance().getReference().child("cpu");
cpuRef.addListenerForSingleValueEvent(new ValueEventListener()
    {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot)
        {
        List<com.example.myapplication.model.CpuItem> allCpus = new ArrayList<>();
        for (DataSnapshot child : snapshot.getChildren())
        {
            Double priceObj = child.child("price").getValue(Double.class);
            double price = priceObj != null ? priceObj : 0.0;
            Double boostClockObj = child.child("boost_clock").getValue(Double.class);
            double boost_clock = boostClockObj != null ? boostClockObj : 0.0;
            Double coreClockObj = child.child("core_clock").getValue(Double.class);
            double core_clock = coreClockObj != null ? coreClockObj : 0.0;
            Integer coreCountObj = child.child("core_count").getValue(Integer.class);
            int core_count = coreCountObj != null ? coreCountObj : 0;
            Boolean smtObj = child.child("smt").getValue(Boolean.class);
            boolean smt = smtObj != null ? smtObj : false;
            Integer tdpObj = child.child("tdp").getValue(Integer.class);
            int tdp = tdpObj != null ? tdpObj : 0;
            String name = child.child("name").getValue(String.class);
            String imageUrl = child.child("image-url").getValue(String.class);
            String graphics = child.child("graphics").getValue(String.class);
            String socket = child.child("socket").getValue(String.class);
            allCpus.add(
                    new com.example.myapplication.model.CpuItem(name, price, imageUrl, boost_clock,
                            core_clock, core_count, graphics, smt, socket, tdp));
        }
        if (persistentRandomCpus == null)
        {
            java.util.Collections.shuffle(allCpus);
            persistentRandomCpus = new ArrayList<>();
            for (int i = 0; i < Math.min(5, allCpus.size()); i++)
            {
                persistentRandomCpus.add(allCpus.get(i));
            }
            com.example.myapplication.model.CpuItem viewMoreItem = new com.example.myapplication.model.CpuItem(
                    "__VIEW_MORE__", 0, "", 0, 0, 0, "", false, "", 0);
            persistentRandomCpus.add(viewMoreItem);
        }
        cpuAdapter.setCpuList(persistentRandomCpus);
        cpuAdapter.setAllCpus(allCpus);
        }

    @Override
    public void onCancelled(@NonNull DatabaseError error)
        {
        android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
        android.widget.Toast.makeText(getContext(), "Failed to load CPUs: " + error.getMessage(),
                android.widget.Toast.LENGTH_SHORT).show();
        }
    });

// Handle 'View More' click for CPUs
cpuAdapter.setOnViewMoreClickListener(() ->
    {
        // Show dialog/fragment with all CPUs in a grid (3 per row)
        AllCpusDialog dialog = new AllCpusDialog(cpuAdapter.getAllCpus(), addToCartClickListenerCpu
                // pass the same listener
        );
        dialog.show(getParentFragmentManager(), "AllCpusDialog");
    });

cpuAdapter.setOnItemClickListener(item ->
    {
        if ("__VIEW_MORE__".equals(item.name))
        {
            return;
        }
        Intent intent = new Intent(getContext(), CpuDetailsActivity.class);
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

// Setup RecyclerView for Memory
RecyclerView memoryRecyclerView = view.findViewById(R.id.memoryRecyclerView);
memoryRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
List<com.example.myapplication.model.MemoryItem> memoryList = new ArrayList<>();
com.example.myapplication.adapter.MemoryAdapter memoryAdapter = new com.example.myapplication.adapter.MemoryAdapter(
        memoryList);
memoryRecyclerView.setAdapter(memoryAdapter);

// Add to Cart button logic for Memory
com.example.myapplication.adapter.MemoryAdapter.OnAddToCartClickListener addToCartClickListenerMemory = item ->
    {
        double price = item.price;
        CartItem cartItem = new CartItem(item.name, price, 1);
        cartViewModel.addItem(cartItem);
        android.widget.Toast.makeText(getContext(), "Added to cart",
                android.widget.Toast.LENGTH_SHORT).show();
    };
memoryAdapter.setOnAddToCartClickListener(addToCartClickListenerMemory);

// Fetch all Memory from Firebase, shuffle, pick 5 random, add 'View More' item
DatabaseReference memoryRef = FirebaseDatabase.getInstance().getReference().child("memory");
memoryRef.addListenerForSingleValueEvent(new ValueEventListener()
    {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot)
        {
        List<com.example.myapplication.model.MemoryItem> allMemory = new ArrayList<>();
        for (DataSnapshot child : snapshot.getChildren())
        {
            String name = child.child("name").getValue(String.class);
            Double priceObj = child.child("price").getValue(Double.class);
            double price = priceObj != null ? priceObj : 0.0;
            String imageUrl = child.child("image-url").getValue(String.class);
            String color = child.child("color").getValue(String.class);
            String ddr_type = child.child("ddr_type").getValue(String.class);
            Integer casLatencyObj = child.child("cas_latency").getValue(Integer.class);
            int cas_latency = casLatencyObj != null ? casLatencyObj : 0;
            Integer firstWordLatencyObj = child.child("first_word_latency").getValue(Integer.class);
            int first_word_latency = firstWordLatencyObj != null ? firstWordLatencyObj : 0;
            List<Integer> modules = new ArrayList<>();
            for (DataSnapshot mod : child.child("modules").getChildren())
            {
                Integer val = mod.getValue(Integer.class);
                if (val != null)
                {
                    modules.add(val);
                }
            }
            List<Integer> speed = new ArrayList<>();
            for (DataSnapshot spd : child.child("speed").getChildren())
            {
                Integer val = spd.getValue(Integer.class);
                if (val != null)
                {
                    speed.add(val);
                }
            }
            allMemory.add(
                    new com.example.myapplication.model.MemoryItem(name, price, imageUrl, color,
                            ddr_type, cas_latency, first_word_latency, modules, speed));
        }
        if (persistentRandomMemory == null)
        {
            java.util.Collections.shuffle(allMemory);
            persistentRandomMemory = new ArrayList<>();
            for (int i = 0; i < Math.min(5, allMemory.size()); i++)
            {
                persistentRandomMemory.add(allMemory.get(i));
            }
            com.example.myapplication.model.MemoryItem viewMoreItem = new com.example.myapplication.model.MemoryItem(
                    "__VIEW_MORE__", 0, "", "", "", 0, 0, new ArrayList<>(), new ArrayList<>());
            persistentRandomMemory.add(viewMoreItem);
        }
        memoryAdapter.setMemoryList(persistentRandomMemory);
        memoryAdapter.setAllMemory(allMemory);
        }

    @Override
    public void onCancelled(@NonNull DatabaseError error)
        {
        android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
        android.widget.Toast.makeText(getContext(), "Failed to load Memory: " + error.getMessage(),
                android.widget.Toast.LENGTH_SHORT).show();
        }
    });

// Handle 'View More' click for Memory
memoryAdapter.setOnViewMoreClickListener(() ->
    {
        com.example.myapplication.ui.ViewAll.AllMemoryDialog dialog = new com.example.myapplication.ui.ViewAll.AllMemoryDialog(
                memoryAdapter.getAllMemory(), addToCartClickListenerMemory);
        dialog.show(getParentFragmentManager(), "AllMemoryDialog");
    });

memoryAdapter.setOnItemClickListener(item ->
    {
        if ("__VIEW_MORE__".equals(item.name))
        {
            return;
        }
        Intent intent = new Intent(getContext(),
                com.example.myapplication.ui.DetailView.MemoryDetailsActivity.class);
        intent.putExtra("name", item.name);
        intent.putExtra("price", item.price);
        intent.putExtra("imageUrl", item.imageUrl);
        intent.putExtra("ddr_type", item.ddr_type);
        intent.putExtra("color", item.color);
        intent.putExtra("cas_latency", item.cas_latency);
        intent.putExtra("first_word_latency", item.first_word_latency);
        intent.putIntegerArrayListExtra("modules", new java.util.ArrayList<>(item.modules));
        intent.putIntegerArrayListExtra("speed", new java.util.ArrayList<>(item.speed));
        startActivity(intent);
    });
}
}