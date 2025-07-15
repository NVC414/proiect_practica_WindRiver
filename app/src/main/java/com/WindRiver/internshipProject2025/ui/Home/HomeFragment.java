package com.WindRiver.internshipProject2025.ui.Home;

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

import com.WindRiver.internshipProject2025.R;
import com.WindRiver.internshipProject2025.adapter.CaseAdapter;
import com.WindRiver.internshipProject2025.adapter.ImageAdapter;
import com.WindRiver.internshipProject2025.model.CaseItem;
import com.WindRiver.internshipProject2025.ui.Cart.CartItem;
import com.WindRiver.internshipProject2025.ui.Cart.CartViewModel;
import com.WindRiver.internshipProject2025.ui.DetailView.CaseDetailsActivity;
import com.WindRiver.internshipProject2025.ui.DetailView.CpuDetailsActivity;
import com.WindRiver.internshipProject2025.ui.ViewAll.AllCasesDialog;
import com.WindRiver.internshipProject2025.ui.ViewAll.AllCpusDialog;
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

public class HomeFragment extends Fragment
    {

    private static List<CaseItem> persistentRandomCases = null;
    private static List<com.WindRiver.internshipProject2025.model.CpuItem> persistentRandomCpus = null;
    private static List<com.WindRiver.internshipProject2025.model.MemoryItem> persistentRandomMemory = null;
    private static List<com.WindRiver.internshipProject2025.model.MotherboardItem> persistentRandomMotherboards = null;
    private static List<com.WindRiver.internshipProject2025.model.GpuItem> persistentRandomGpus = null;

    private final int[] images = {R.drawable.banner_image1, R.drawable.banner_image2, R.drawable.banner_image3};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
        {
        return inflater.inflate(R.layout.fragment_home, container, false);
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
        {
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        ImageAdapter adapter = new ImageAdapter(requireContext(), images);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
            { // this works, i don't know quite why, but it does
                tab.setCustomView(R.layout.custom_tab);
            }).attach();

        // Setup RecyclerView for cases
        RecyclerView caseRecyclerView = view.findViewById(R.id.caseRecyclerView);
        caseRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<CaseItem> caseList = new ArrayList<>();
        CaseAdapter caseAdapter = new CaseAdapter(caseList);
        caseRecyclerView.setAdapter(caseAdapter);

        // CartViewModel for cart operations
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(
                CartViewModel.class);

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
                        imageUrl = Objects.requireNonNull(
                                child.child("image-url").getValue()).toString();
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
                    CaseItem viewMoreItem = new CaseItem("__VIEW_MORE__", "", "", "", "", "", "", 0,
                            0);
                    persistentRandomCases.add(viewMoreItem);
                }
                caseAdapter.setCaseList(persistentRandomCases);
                caseAdapter.setAllCases(allCases); // For grid dialog
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
                android.widget.Toast.makeText(getContext(),
                        "Failed to load cases: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            });

        // Handle 'View More' click
        caseAdapter.setOnViewMoreClickListener(() ->
            {
                // Show dialog/fragment with all cases in a grid (3 per row)
                AllCasesDialog dialog = new AllCasesDialog(caseAdapter.getAllCases(),
                        addToCartClickListener
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
        List<com.WindRiver.internshipProject2025.model.CpuItem> cpuList = new ArrayList<>();
        com.WindRiver.internshipProject2025.adapter.CpuAdapter cpuAdapter = new com.WindRiver.internshipProject2025.adapter.CpuAdapter(
                cpuList);
        cpuRecyclerView.setAdapter(cpuAdapter);

        // Add to Cart button logic for CPUs
        com.WindRiver.internshipProject2025.adapter.CpuAdapter.OnAddToCartClickListener addToCartClickListenerCpu = item ->
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
                List<com.WindRiver.internshipProject2025.model.CpuItem> allCpus = new ArrayList<>();
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
                    allCpus.add(new com.WindRiver.internshipProject2025.model.CpuItem(name, price,
                            imageUrl, boost_clock, core_clock, core_count, graphics, smt, socket,
                            tdp));
                }
                if (persistentRandomCpus == null)
                {
                    java.util.Collections.shuffle(allCpus);
                    persistentRandomCpus = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allCpus.size()); i++)
                    {
                        persistentRandomCpus.add(allCpus.get(i));
                    }
                    com.WindRiver.internshipProject2025.model.CpuItem viewMoreItem = new com.WindRiver.internshipProject2025.model.CpuItem(
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
                android.widget.Toast.makeText(getContext(),
                        "Failed to load CPUs: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            });

        // Handle 'View More' click for CPUs
        cpuAdapter.setOnViewMoreClickListener(() ->
            {
                // Show dialog/fragment with all CPUs in a grid (3 per row)
                AllCpusDialog dialog = new AllCpusDialog(cpuAdapter.getAllCpus(),
                        addToCartClickListenerCpu
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
        List<com.WindRiver.internshipProject2025.model.MemoryItem> memoryList = new ArrayList<>();
        com.WindRiver.internshipProject2025.adapter.MemoryAdapter memoryAdapter = new com.WindRiver.internshipProject2025.adapter.MemoryAdapter(
                memoryList);
        memoryRecyclerView.setAdapter(memoryAdapter);

        // Add to Cart button logic for Memory
        com.WindRiver.internshipProject2025.adapter.MemoryAdapter.OnAddToCartClickListener addToCartClickListenerMemory = item ->
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
                List<com.WindRiver.internshipProject2025.model.MemoryItem> allMemory = new ArrayList<>();
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
                    Integer firstWordLatencyObj = child.child("first_word_latency").getValue(
                            Integer.class);
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
                            new com.WindRiver.internshipProject2025.model.MemoryItem(name, price,
                                    imageUrl, color, ddr_type, cas_latency, first_word_latency,
                                    modules, speed));
                }
                if (persistentRandomMemory == null)
                {
                    java.util.Collections.shuffle(allMemory);
                    persistentRandomMemory = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allMemory.size()); i++)
                    {
                        persistentRandomMemory.add(allMemory.get(i));
                    }
                    com.WindRiver.internshipProject2025.model.MemoryItem viewMoreItem = new com.WindRiver.internshipProject2025.model.MemoryItem(
                            "__VIEW_MORE__", 0, "", "", "", 0, 0, new ArrayList<>(),
                            new ArrayList<>());
                    persistentRandomMemory.add(viewMoreItem);
                }
                memoryAdapter.setMemoryList(persistentRandomMemory);
                memoryAdapter.setAllMemory(allMemory);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
                android.widget.Toast.makeText(getContext(),
                        "Failed to load Memory: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            });

        // Handle 'View More' click for Memory
        memoryAdapter.setOnViewMoreClickListener(() ->
            {
                com.WindRiver.internshipProject2025.ui.ViewAll.AllMemoryDialog dialog = new com.WindRiver.internshipProject2025.ui.ViewAll.AllMemoryDialog(
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
                        com.WindRiver.internshipProject2025.ui.DetailView.MemoryDetailsActivity.class);
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

        // Setup RecyclerView for Motherboards
        RecyclerView motherboardRecyclerView = view.findViewById(R.id.motherboardRecyclerView);
        motherboardRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<com.WindRiver.internshipProject2025.model.MotherboardItem> motherboardList = new ArrayList<>();
        com.WindRiver.internshipProject2025.adapter.MotherboardAdapter motherboardAdapter = new com.WindRiver.internshipProject2025.adapter.MotherboardAdapter(motherboardList);
        motherboardRecyclerView.setAdapter(motherboardAdapter);

        // Add to Cart button logic for Motherboards
        com.WindRiver.internshipProject2025.adapter.MotherboardAdapter.OnAddToCartClickListener addToCartClickListenerMotherboard = item -> {
            double price = 0.0;
            try {
                price = Double.parseDouble(item.price.replaceAll("[^0-9.]", ""));
            } catch (Exception ignored) {}
            CartItem cartItem = new CartItem(item.name, price, 1);
            cartViewModel.addItem(cartItem);
            android.widget.Toast.makeText(getContext(), "Added to cart", android.widget.Toast.LENGTH_SHORT).show();
        };
        motherboardAdapter.setOnAddToCartClickListener(addToCartClickListenerMotherboard);

        // Fetch all motherboards from Firebase, shuffle, pick 5 random, add 'View More' item
        DatabaseReference motherboardRef = FirebaseDatabase.getInstance().getReference().child("motherboard");
        motherboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<com.WindRiver.internshipProject2025.model.MotherboardItem> allMotherboards = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = "";
                    String price = "";
                    String imageUrl = "";
                    String color = "";
                    String ddrType = "";
                    String formFactor = "";
                    String socket = "";
                    int maxMemory = 0;
                    int memorySlots = 0;
                    if (child.child("name").getValue() != null) {
                        name = Objects.requireNonNull(child.child("name").getValue()).toString();
                    }
                    if (child.child("price").getValue() != null) {
                        price = Objects.requireNonNull(child.child("price").getValue()).toString();
                    }
                    if (child.child("image-url").getValue() != null) {
                        imageUrl = Objects.requireNonNull(child.child("image-url").getValue()).toString();
                    }
                    if (child.child("color").getValue() != null) {
                        color = Objects.requireNonNull(child.child("color").getValue()).toString();
                    }
                    if (child.child("ddr_type").getValue() != null) {
                        ddrType = Objects.requireNonNull(child.child("ddr_type").getValue()).toString();
                    }
                    if (child.child("form_factor").getValue() != null) {
                        formFactor = Objects.requireNonNull(child.child("form_factor").getValue()).toString();
                    }
                    if (child.child("socket").getValue() != null) {
                        socket = Objects.requireNonNull(child.child("socket").getValue()).toString();
                    }
                    if (child.child("max_memory").getValue() != null) {
                        try {
                            maxMemory = Integer.parseInt(Objects.requireNonNull(child.child("max_memory").getValue()).toString());
                        } catch (Exception ignored) {}
                    }
                    if (child.child("memory_slots").getValue() != null) {
                        try {
                            memorySlots = Integer.parseInt(Objects.requireNonNull(child.child("memory_slots").getValue()).toString());
                        } catch (Exception ignored) {}
                    }
                    allMotherboards.add(new com.WindRiver.internshipProject2025.model.MotherboardItem(name, price, imageUrl, color, ddrType, formFactor, socket, maxMemory, memorySlots));
                }
                if (persistentRandomMotherboards == null) {
                    java.util.Collections.shuffle(allMotherboards);
                    persistentRandomMotherboards = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allMotherboards.size()); i++) {
                        persistentRandomMotherboards.add(allMotherboards.get(i));
                    }
                    com.WindRiver.internshipProject2025.model.MotherboardItem viewMoreItem = new com.WindRiver.internshipProject2025.model.MotherboardItem("__VIEW_MORE__", "", "", "", "", "", "", 0, 0);
                    persistentRandomMotherboards.add(viewMoreItem);
                }
                motherboardAdapter.setMotherboardList(persistentRandomMotherboards);
                motherboardAdapter.setAllMotherboards(allMotherboards);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
                android.widget.Toast.makeText(getContext(), "Failed to load motherboards: " + error.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Handle 'View More' click for Motherboards
        motherboardAdapter.setOnViewMoreClickListener(() -> {
            com.WindRiver.internshipProject2025.ui.ViewAll.AllMotherboardsDialog dialog = new com.WindRiver.internshipProject2025.ui.ViewAll.AllMotherboardsDialog(
                motherboardAdapter.getAllMotherboards(), addToCartClickListenerMotherboard);
            dialog.show(getParentFragmentManager(), "AllMotherboardsDialog");
        });

        motherboardAdapter.setOnItemClickListener(item -> {
            if ("__VIEW_MORE__".equals(item.name)) {
                return;
            }
            Intent intent = new Intent(getContext(), com.WindRiver.internshipProject2025.ui.DetailView.MotherboardDetailsActivity.class);
            intent.putExtra("name", item.name);
            intent.putExtra("price", item.price);
            intent.putExtra("imageUrl", item.imageUrl);
            intent.putExtra("color", item.color);
            intent.putExtra("ddr_type", item.ddrType);
            intent.putExtra("form_factor", item.formFactor);
            intent.putExtra("socket", item.socket);
            intent.putExtra("max_memory", item.maxMemory);
            intent.putExtra("memory_slots", item.memorySlots);
            startActivity(intent);
        });

        // Setup RecyclerView for GPUs
        RecyclerView gpuRecyclerView = view.findViewById(R.id.gpuRecyclerView);
        gpuRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<com.WindRiver.internshipProject2025.model.GpuItem> gpuList = new ArrayList<>();
        com.WindRiver.internshipProject2025.adapter.GpuAdapter gpuAdapter = new com.WindRiver.internshipProject2025.adapter.GpuAdapter(gpuList);
        gpuRecyclerView.setAdapter(gpuAdapter);

        // Add to Cart button logic for GPUs
        com.WindRiver.internshipProject2025.adapter.GpuAdapter.OnAddToCartClickListener addToCartClickListenerGpu = item -> {
            double price = 0.0;
            try {
                price = Double.parseDouble(item.price.replaceAll("[^0-9.]", ""));
            } catch (Exception ignored) {}
            CartItem cartItem = new CartItem(item.name, price, 1);
            cartViewModel.addItem(cartItem);
            android.widget.Toast.makeText(getContext(), "Added to cart", android.widget.Toast.LENGTH_SHORT).show();
        };
        gpuAdapter.setOnAddToCartClickListener(addToCartClickListenerGpu);

        // Fetch all GPUs from Firebase, shuffle, pick 5 random, add 'View More' item
        DatabaseReference gpuRef = FirebaseDatabase.getInstance().getReference().child("video-card");
        gpuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<com.WindRiver.internshipProject2025.model.GpuItem> allGpus = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("name").getValue(String.class);
                    String price = "";
                    Object priceObj = child.child("price").getValue();
                    if (priceObj != null) {
                        if (priceObj instanceof Double) {
                            price = String.format("%.2f", (Double) priceObj);
                        } else if (priceObj instanceof Long) {
                            price = String.format("%d", (Long) priceObj);
                        } else if (priceObj instanceof String) {
                            price = (String) priceObj;
                        } else {
                            price = String.valueOf(priceObj);
                        }
                    }
                    String imageUrl = child.child("image-url").getValue(String.class);
                    String color = child.child("color").getValue(String.class);
                    String chipset = child.child("chipset").getValue(String.class);
                    String core_clock = "";
                    Object coreClockObj = child.child("core_clock").getValue();
                    if (coreClockObj != null) {
                        if (coreClockObj instanceof Double) {
                            core_clock = String.format("%.0f", (Double) coreClockObj);
                        } else if (coreClockObj instanceof Long) {
                            core_clock = String.format("%d", (Long) coreClockObj);
                        } else if (coreClockObj instanceof String) {
                            core_clock = (String) coreClockObj;
                        } else {
                            core_clock = String.valueOf(coreClockObj);
                        }
                    }
                    String boost_clock = "";
                    Object boostClockObj = child.child("boost_clock").getValue();
                    if (boostClockObj != null) {
                        if (boostClockObj instanceof Double) {
                            boost_clock = String.format("%.0f", (Double) boostClockObj);
                        } else if (boostClockObj instanceof Long) {
                            boost_clock = String.format("%d", (Long) boostClockObj);
                        } else if (boostClockObj instanceof String) {
                            boost_clock = (String) boostClockObj;
                        } else {
                            boost_clock = String.valueOf(boostClockObj);
                        }
                    }
                    int memory = 0;
                    Object memoryObj = child.child("memory").getValue();
                    if (memoryObj != null) {
                        if (memoryObj instanceof Long) {
                            memory = ((Long) memoryObj).intValue();
                        } else if (memoryObj instanceof Integer) {
                            memory = (Integer) memoryObj;
                        } else if (memoryObj instanceof String) {
                            try {
                                memory = Integer.parseInt((String) memoryObj);
                            } catch (Exception ignored) {}
                        }
                    }
                    int length = 0;
                    Object lengthObj = child.child("length").getValue();
                    if (lengthObj != null) {
                        if (lengthObj instanceof Long) {
                            length = ((Long) lengthObj).intValue();
                        } else if (lengthObj instanceof Integer) {
                            length = (Integer) lengthObj;
                        } else if (lengthObj instanceof String) {
                            try {
                                length = Integer.parseInt((String) lengthObj);
                            } catch (Exception ignored) {}
                        }
                    }
                    allGpus.add(new com.WindRiver.internshipProject2025.model.GpuItem(name, price, imageUrl, color, chipset, core_clock, boost_clock, memory, length));
                }
                if (persistentRandomGpus == null) {
                    java.util.Collections.shuffle(allGpus);
                    persistentRandomGpus = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allGpus.size()); i++) {
                        persistentRandomGpus.add(allGpus.get(i));
                    }
                    com.WindRiver.internshipProject2025.model.GpuItem viewMoreItem = new com.WindRiver.internshipProject2025.model.GpuItem("__VIEW_MORE__", "", "", "", "", "", "", 0, 0);
                    persistentRandomGpus.add(viewMoreItem);
                }
                gpuAdapter.setGpuList(persistentRandomGpus);
                gpuAdapter.setAllGpus(allGpus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
                android.widget.Toast.makeText(getContext(), "Failed to load GPUs: " + error.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Handle 'View More' click for GPUs
        gpuAdapter.setOnViewMoreClickListener(() -> {
            com.WindRiver.internshipProject2025.ui.ViewAll.AllGpuDialog dialog = new com.WindRiver.internshipProject2025.ui.ViewAll.AllGpuDialog(
                gpuAdapter.getAllGpus(), addToCartClickListenerGpu);
            dialog.show(getParentFragmentManager(), "AllGpuDialog");
        });

        gpuAdapter.setOnItemClickListener(item -> {
            if ("__VIEW_MORE__".equals(item.name)) {
                return;
            }
            Intent intent = new Intent(getContext(), com.WindRiver.internshipProject2025.activity.GpuDetailsActivity.class);
            intent.putExtra("name", item.name);
            intent.putExtra("price", item.price);
            intent.putExtra("imageUrl", item.imageUrl);
            intent.putExtra("color", item.color);
            intent.putExtra("chipset", item.chipset);
            intent.putExtra("core_clock", item.core_clock);
            intent.putExtra("boost_clock", item.boost_clock);
            intent.putExtra("memory", item.memory);
            intent.putExtra("length", item.length);
            startActivity(intent);
        });
        }
    }