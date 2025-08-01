package com.windriver.pcgate.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.windriver.pcgate.R;
import com.windriver.pcgate.adapter.CaseAdapter;
import com.windriver.pcgate.adapter.ImageAdapter;
import com.windriver.pcgate.model.firebaseItems.CaseItem;
import com.windriver.pcgate.model.firebaseItems.CpuItem;
import com.windriver.pcgate.model.firebaseItems.GpuItem;
import com.windriver.pcgate.model.firebaseItems.LaptopItem;
import com.windriver.pcgate.model.firebaseItems.MemoryItem;
import com.windriver.pcgate.model.firebaseItems.MotherboardItem;
import com.windriver.pcgate.model.firebaseItems.PsuItem;
import com.windriver.pcgate.ui.cart.CartItem;
import com.windriver.pcgate.ui.cart.CartViewModel;
import com.windriver.pcgate.ui.detailView.CaseDetailsActivity;
import com.windriver.pcgate.ui.detailView.CpuDetailsActivity;
import com.windriver.pcgate.ui.viewAll.AllCasesDialog;
import com.windriver.pcgate.ui.viewAll.AllCpusDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment
    {

    private static List<CaseItem> persistentRandomCases = null;
    private static List<CpuItem> persistentRandomCpus = null;
    private static List<MemoryItem> persistentRandomMemory = null;
    private static List<MotherboardItem> persistentRandomMotherboards = null;
    private static List<GpuItem> persistentRandomGpus = null;
    private static List<PsuItem> persistentRandomPsus = null;
    private static List<LaptopItem> persistentRandomLaptops = null;

    private final int[] images = {R.drawable.banner_image1, R.drawable.banner_image2, R.drawable.banner_image3};
    private SegmentedLoadingView loadingView;
    private android.widget.TextView loadingTitle;
    private final List<View> categoryViews = new ArrayList<>();
    private final AtomicInteger loadedCategories = new AtomicInteger(0);
    private static boolean allDataLoaded = false;
    private final int TOTAL_CATEGORIES = 7;
    private final boolean showcase = true; // animatia va lua mai mult ptr afisare
    private boolean animationFinished = false;


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
        super.onViewCreated(view, savedInstanceState);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        ImageAdapter adapter = new ImageAdapter(requireContext(), images);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setCustomView(R.layout.custom_tab)).attach();


        loadingView = view.findViewById(R.id.loadingView);
        loadingTitle = view.findViewById(R.id.loadingTitle);
        categoryViews.addAll(Arrays.asList(view.findViewById(R.id.carcaseTitle),
                view.findViewById(R.id.caseRecyclerView), view.findViewById(R.id.cpuTitle),
                view.findViewById(R.id.cpuRecyclerView), view.findViewById(R.id.laptopTitle),
                view.findViewById(R.id.laptopRecyclerView), view.findViewById(R.id.memoryTitle),
                view.findViewById(R.id.memoryRecyclerView),
                view.findViewById(R.id.motherboardTitle),
                view.findViewById(R.id.motherboardRecyclerView), view.findViewById(R.id.gpuTitle),
                view.findViewById(R.id.gpuRecyclerView), view.findViewById(R.id.psuTitle),
                view.findViewById(R.id.psuRecyclerView)));

        if (!allDataLoaded)
        {
            loadingView.setVisibility(View.VISIBLE);
            loadingTitle.setVisibility(View.VISIBLE);
            for (View v : categoryViews)
            {
                v.setVisibility(View.GONE);
            }
            if (showcase)
            {
                // Showcase mode: animate with a delay
                animationFinished = false;
                android.os.Handler handler = new android.os.Handler(
                        android.os.Looper.getMainLooper());
                // ms
                int ANIMATION_DELAY_PER_SEGMENT = 300;
                for (int i = 1; i <= TOTAL_CATEGORIES; i++)
                {
                    final int progress = i;
                    handler.postDelayed(() -> loadingView.setProgress(progress),
                            (long) i * ANIMATION_DELAY_PER_SEGMENT);
                }
                handler.postDelayed(() ->
                    {
                        animationFinished = true;
                        tryFinishLoading();
                    }, (long) TOTAL_CATEGORIES * ANIMATION_DELAY_PER_SEGMENT);
            }
        }
        else
        {
            loadingView.setVisibility(View.GONE);
            loadingTitle.setVisibility(View.GONE);
            for (View v : categoryViews)
            {
                v.setVisibility(View.VISIBLE);
            }
        }


        RecyclerView caseRecyclerView = view.findViewById(R.id.caseRecyclerView);
        caseRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<CaseItem> caseList = new ArrayList<>();
        CaseAdapter caseAdapter = new CaseAdapter(caseList);
        caseRecyclerView.setAdapter(caseAdapter);


        CartViewModel cartViewModel = CartViewModel.getInstance();


        CaseAdapter.OnAddToCartClickListener addToCartClickListener = item ->
            {
                double price = 0.0;
                try
                {
                    price = Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", ""));
                }
                catch (Exception ignored)
                {
                }
                CartItem cartItem = new CartItem(item.getName(), price, 1);
                cartViewModel.addItem(cartItem);
                android.widget.Toast.makeText(getContext(), "Added to cart",
                        android.widget.Toast.LENGTH_SHORT).show();
            };
        caseAdapter.setOnAddToCartClickListener(addToCartClickListener);


        CaseAdapter.OnRemoveFromCartClickListener removeFromCartClickListener = item ->
            {
                List<CartItem> currentCart = cartViewModel.getCartItems().getValue();
                if (currentCart != null)
                {
                    for (CartItem cartItem : currentCart)
                    {
                        if (cartItem.getName().equals(item.getName()))
                        {
                            int newQty = cartItem.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(), -1));
                            }
                            else
                            {

                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(),
                                                -cartItem.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            };
        caseAdapter.setOnRemoveFromCartClickListener(removeFromCartClickListener);


        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
            {
                java.util.Map<String, Integer> quantities = new java.util.HashMap<>();
                if (items != null)
                {
                    for (CartItem cartItem : items)
                    {
                        quantities.put(cartItem.getName(), cartItem.getQuantity());
                    }
                }
                caseAdapter.setCartQuantities(quantities);
            });

        if (persistentRandomCases != null)
        {
            caseAdapter.setCaseList(persistentRandomCases);
        }


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
                caseAdapter.setAllCases(allCases);
                onCategoryDataLoaded();
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


        caseAdapter.setOnViewMoreClickListener(() ->
            {

                AllCasesDialog dialog = new AllCasesDialog(caseAdapter.getAllCases(),
                        addToCartClickListener

                );
                dialog.show(getParentFragmentManager(), "AllCasesDialog");
            });

        caseAdapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                Intent intent = new Intent(getContext(), CaseDetailsActivity.class);
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageUrl", item.getImageUrl());


                for (CaseItem c : caseAdapter.getAllCases())
                {
                    if (c.getName().equals(item.getName()))
                    {
                        intent.putExtra("color", c.getColor() != null ? c.getColor() : "");
                        intent.putExtra("type", c.getType() != null ? c.getType() : "");
                        intent.putExtra("side_panel",
                                c.getSidePanel() != null ? c.getSidePanel() : "");
                        intent.putExtra("psu", c.getPsu() != null ? c.getPsu() : "");
                        intent.putExtra("internal_35_bays", c.getInternal35Bays());
                        intent.putExtra("external_volume", c.getExternalVolume());
                        break;
                    }
                }
                startActivity(intent);
            });


        RecyclerView cpuRecyclerView = view.findViewById(R.id.cpuRecyclerView);
        cpuRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<CpuItem> cpuList = new ArrayList<>();
        com.windriver.pcgate.adapter.CpuAdapter cpuAdapter = new com.windriver.pcgate.adapter.CpuAdapter(
                cpuList);
        cpuRecyclerView.setAdapter(cpuAdapter);


        com.windriver.pcgate.adapter.CpuAdapter.OnAddToCartClickListener addToCartClickListenerCpu = item ->
            {
                double price = item.getPrice();
                CartItem cartItem = new CartItem(item.getName(), price, 1);
                cartViewModel.addItem(cartItem);
                android.widget.Toast.makeText(getContext(), "Added to cart",
                        android.widget.Toast.LENGTH_SHORT).show();
            };
        cpuAdapter.setOnAddToCartClickListener(addToCartClickListenerCpu);


        com.windriver.pcgate.adapter.CpuAdapter.OnRemoveFromCartClickListener removeFromCartClickListenerCpu = item ->
            {
                List<CartItem> currentCart = cartViewModel.getCartItems().getValue();
                if (currentCart != null)
                {
                    for (CartItem cartItem : currentCart)
                    {
                        if (cartItem.getName().equals(item.getName()))
                        {
                            int newQty = cartItem.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(), -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(),
                                                -cartItem.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            };
        cpuAdapter.setOnRemoveFromCartClickListener(removeFromCartClickListenerCpu);
        com.windriver.pcgate.adapter.CpuAdapter.OnAddMoreToCartClickListener addMoreToCartClickListenerCpu = item -> cartViewModel.addItem(
                new CartItem(item.getName(), item.getPrice(), 1));
        cpuAdapter.setOnAddMoreToCartClickListener(addMoreToCartClickListenerCpu);


        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
            {
                java.util.Map<String, Integer> quantities = new java.util.HashMap<>();
                if (items != null)
                {
                    for (CartItem cartItem : items)
                    {
                        quantities.put(cartItem.getName(), cartItem.getQuantity());
                    }
                }
                cpuAdapter.setCartQuantities(quantities);
            });

        if (persistentRandomCpus != null)
        {
            cpuAdapter.setCpuList(persistentRandomCpus);
        }


        DatabaseReference cpuRef = FirebaseDatabase.getInstance().getReference().child("cpu");
        cpuRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                List<CpuItem> allCpus = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren())
                {
                    Double priceObj = child.child("price").getValue(Double.class);
                    double price = priceObj != null ? priceObj : 0.0;
                    Double boostClockObj = child.child("boost_clock").getValue(Double.class);
                    double boost_clock = boostClockObj != null ? boostClockObj : 0.0;
                    Double coreClockObj = child.child("core_clock").getValue(Double.class);
                    double core_clock = coreClockObj != null ? coreClockObj : 0;
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
                            new CpuItem(name, price, imageUrl, boost_clock, core_clock, core_count,
                                    graphics, smt, socket, tdp));
                }
                if (persistentRandomCpus == null)
                {
                    java.util.Collections.shuffle(allCpus);
                    persistentRandomCpus = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allCpus.size()); i++)
                    {
                        persistentRandomCpus.add(allCpus.get(i));
                    }
                    CpuItem viewMoreItem = new CpuItem("__VIEW_MORE__", 0, "", 0, 0, 0, "", false,
                            "", 0);
                    persistentRandomCpus.add(viewMoreItem);
                }
                cpuAdapter.setCpuList(persistentRandomCpus);
                cpuAdapter.setAllCpus(allCpus);
                onCategoryDataLoaded();
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


        cpuAdapter.setOnViewMoreClickListener(() ->
            {

                AllCpusDialog dialog = new AllCpusDialog(cpuAdapter.getAllCpus(),
                        addToCartClickListenerCpu

                );
                dialog.show(getParentFragmentManager(), "AllCpusDialog");
            });

        cpuAdapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                Intent intent = new Intent(getContext(), CpuDetailsActivity.class);
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("boost_clock", item.getBoostClock());
                intent.putExtra("core_clock", item.getCoreClock());
                intent.putExtra("core_count", item.getCoreCount());
                intent.putExtra("graphics", item.getGraphics());
                intent.putExtra("smt", item.isSmt());
                intent.putExtra("socket", item.getSocket());
                intent.putExtra("tdp", item.getTdp());
                startActivity(intent);
            });


        RecyclerView laptopRecyclerView = view.findViewById(R.id.laptopRecyclerView);
        laptopRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<LaptopItem> laptopList = new ArrayList<>();
        com.windriver.pcgate.adapter.LaptopAdapter laptopAdapter = new com.windriver.pcgate.adapter.LaptopAdapter(
                laptopList);
        laptopRecyclerView.setAdapter(laptopAdapter);


        com.windriver.pcgate.adapter.LaptopAdapter.OnAddToCartClickListener addToCartClickListenerLaptop = item ->
            {
                double price = 0.0;
                try
                {
                    price = Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", ""));
                }
                catch (Exception ignored)
                {
                }
                String cartKey = item.getBrand() + "|" + item.getModel();
                CartItem cartItem = new CartItem(cartKey, price, 1);
                cartViewModel.addItem(cartItem);
                android.widget.Toast.makeText(getContext(), "Added to cart",
                        android.widget.Toast.LENGTH_SHORT).show();
            };
        laptopAdapter.setOnAddToCartClickListener(addToCartClickListenerLaptop);


        com.windriver.pcgate.adapter.LaptopAdapter.OnRemoveFromCartClickListener removeFromCartClickListenerLaptop = item ->
            {
                List<CartItem> currentCart = cartViewModel.getCartItems().getValue();
                if (currentCart != null)
                {
                    String cartKey = item.getBrand() + "|" + item.getModel();
                    for (CartItem cartItem : currentCart)
                    {
                        if (cartItem.getName().equals(cartKey))
                        {
                            int newQty = cartItem.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(
                                        new CartItem(cartKey, cartItem.getPrice(), -1));
                            }
                            else
                            {
                                cartViewModel.addItem(new CartItem(cartKey, cartItem.getPrice(),
                                        -cartItem.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            };
        laptopAdapter.setOnRemoveFromCartClickListener(removeFromCartClickListenerLaptop);


        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
            {
                java.util.Map<String, Integer> quantities = new java.util.HashMap<>();
                if (items != null)
                {
                    for (CartItem cartItem : items)
                    {
                        quantities.put(cartItem.getName(), cartItem.getQuantity());
                    }
                }
                laptopAdapter.setCartQuantities(quantities);
            });

        if (persistentRandomLaptops != null)
        {
            laptopAdapter.setLaptopList(persistentRandomLaptops);
        }

        DatabaseReference laptopRef = FirebaseDatabase.getInstance().getReference().child("laptop");
        laptopRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                List<LaptopItem> allLaptops = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren())
                {
                    String brand = "";
                    String model = "";
                    String price = "";
                    String imageUrl = "";
                    String processor = "";
                    String ram_gb = "";
                    String ram_type = "";
                    String graphic_card_gb = "";
                    String hdd = "";
                    String ssd = "";
                    if (child.child("brand").getValue() != null)
                    {
                        brand = Objects.requireNonNull(child.child("brand").getValue()).toString();
                    }
                    if (child.child("model").getValue() != null)
                    {
                        model = Objects.requireNonNull(child.child("model").getValue()).toString();
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
                    if (child.child("processor").getValue() != null)
                    {
                        processor = Objects.requireNonNull(
                                child.child("processor").getValue()).toString();
                    }
                    if (child.child("ram_gb").getValue() != null)
                    {
                        ram_gb = Objects.requireNonNull(
                                child.child("ram_gb").getValue()).toString();
                    }
                    if (child.child("ram_type").getValue() != null)
                    {
                        ram_type = Objects.requireNonNull(
                                child.child("ram_type").getValue()).toString();
                    }
                    if (child.child("graphic_card_gb").getValue() != null)
                    {
                        graphic_card_gb = Objects.requireNonNull(
                                child.child("graphic_card_gb").getValue()).toString();
                    }
                    if (child.child("hdd").getValue() != null)
                    {
                        hdd = Objects.requireNonNull(child.child("hdd").getValue()).toString();
                    }
                    if (child.child("ssd").getValue() != null)
                    {
                        ssd = Objects.requireNonNull(child.child("ssd").getValue()).toString();
                    }
                    allLaptops.add(new LaptopItem(brand, model, price, imageUrl, processor, ram_gb,
                            ram_type, graphic_card_gb, hdd, ssd));
                }
                if (persistentRandomLaptops == null)
                {
                    java.util.Collections.shuffle(allLaptops);
                    persistentRandomLaptops = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allLaptops.size()); i++)
                    {
                        persistentRandomLaptops.add(allLaptops.get(i));
                    }
                    LaptopItem viewMoreItem = new LaptopItem("", "__VIEW_MORE__", "", "", "", "",
                            "", "", "", "");
                    persistentRandomLaptops.add(viewMoreItem);
                }
                laptopAdapter.setLaptopList(persistentRandomLaptops);
                laptopAdapter.setAllLaptops(allLaptops);
                onCategoryDataLoaded();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
                android.widget.Toast.makeText(getContext(),
                        "Failed to load laptops: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            });


        laptopAdapter.setOnViewMoreClickListener(() ->
            {
                if (laptopAdapter.getAllLaptops() == null || laptopAdapter.getAllLaptops().isEmpty())
                {
                    android.widget.Toast.makeText(getContext(),
                            "Laptops are still loading. Please wait.",
                            android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                com.windriver.pcgate.ui.viewAll.AllLaptopsDialog dialog = new com.windriver.pcgate.ui.viewAll.AllLaptopsDialog(
                        laptopAdapter.getAllLaptops(), addToCartClickListenerLaptop);
                dialog.show(getParentFragmentManager(), "AllLaptopsDialog");
            });

        laptopAdapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getModel()))
                {
                    return;
                }
                Intent intent = new Intent(getContext(),
                        com.windriver.pcgate.ui.detailView.LaptopDetailsActivity.class);
                intent.putExtra("brand", item.getBrand());
                intent.putExtra("model", item.getModel());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("processor", item.getProcessor());
                intent.putExtra("ram_gb", item.getRamGb());
                intent.putExtra("ram_type", item.getRamType());
                intent.putExtra("graphic_card_gb", item.getGraphicCardGb());
                intent.putExtra("hdd", item.getHdd());
                intent.putExtra("ssd", item.getSsd());
                startActivity(intent);
            });


        RecyclerView memoryRecyclerView = view.findViewById(R.id.memoryRecyclerView);
        memoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<MemoryItem> memoryList = new ArrayList<>();
        com.windriver.pcgate.adapter.MemoryAdapter memoryAdapter = new com.windriver.pcgate.adapter.MemoryAdapter(
                memoryList);
        memoryRecyclerView.setAdapter(memoryAdapter);

        com.windriver.pcgate.adapter.MemoryAdapter.OnAddToCartClickListener addToCartClickListenerMemory = item ->
            {
                double price = 0.0;
                try
                {
                    price = item.getPrice();
                }
                catch (Exception ignored)
                {
                }
                CartItem cartItem = new CartItem(item.getName(), price, 1);
                cartViewModel.addItem(cartItem);
                android.widget.Toast.makeText(getContext(), "Added to cart",
                        android.widget.Toast.LENGTH_SHORT).show();
            };
        memoryAdapter.setOnAddToCartClickListener(addToCartClickListenerMemory);


        com.windriver.pcgate.adapter.MemoryAdapter.OnRemoveFromCartClickListener removeFromCartClickListenerMemory = item ->
            {
                List<CartItem> currentCart = cartViewModel.getCartItems().getValue();
                if (currentCart != null)
                {
                    for (CartItem cartItem : currentCart)
                    {
                        if (cartItem.getName().equals(item.getName()))
                        {
                            int newQty = cartItem.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(), -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(),
                                                -cartItem.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            };
        memoryAdapter.setOnRemoveFromCartClickListener(removeFromCartClickListenerMemory);


        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
            {
                java.util.Map<String, Integer> quantities = new java.util.HashMap<>();
                if (items != null)
                {
                    for (CartItem cartItem : items)
                    {
                        quantities.put(cartItem.getName(), cartItem.getQuantity());
                    }
                }
                memoryAdapter.setCartQuantities(quantities);
            });


        // Show cached memory instantly if available
        if (persistentRandomMemory != null)
        {
            memoryAdapter.setMemoryList(persistentRandomMemory);
        }

        DatabaseReference memoryRef = FirebaseDatabase.getInstance().getReference().child("memory");
        memoryRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                List<MemoryItem> allMemory = new ArrayList<>();
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
                            new MemoryItem(name, price, imageUrl, color, ddr_type, cas_latency,
                                    first_word_latency, modules, speed));
                }
                if (persistentRandomMemory == null)
                {
                    java.util.Collections.shuffle(allMemory);
                    persistentRandomMemory = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allMemory.size()); i++)
                    {
                        persistentRandomMemory.add(allMemory.get(i));
                    }
                    MemoryItem viewMoreItem = new MemoryItem("__VIEW_MORE__", 0, "", "", "", 0, 0,
                            new ArrayList<>(), new ArrayList<>());
                    persistentRandomMemory.add(viewMoreItem);
                }
                memoryAdapter.setMemoryList(persistentRandomMemory);
                memoryAdapter.setAllMemory(allMemory);
                onCategoryDataLoaded();
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


        memoryAdapter.setOnViewMoreClickListener(() ->
            {
                com.windriver.pcgate.ui.viewAll.AllMemoryDialog dialog = new com.windriver.pcgate.ui.viewAll.AllMemoryDialog(
                        memoryAdapter.getAllMemory(), addToCartClickListenerMemory);
                dialog.show(getParentFragmentManager(), "AllMemoryDialog");
            });

        memoryAdapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                Intent intent = getIntent(item);
                intent.putIntegerArrayListExtra("modules",
                        new java.util.ArrayList<>(item.getModules()));
                intent.putIntegerArrayListExtra("speed",
                        new java.util.ArrayList<>(item.getSpeed()));
                startActivity(intent);
            });


        RecyclerView motherboardRecyclerView = view.findViewById(R.id.motherboardRecyclerView);
        motherboardRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<MotherboardItem> motherboardList = new ArrayList<>();
        com.windriver.pcgate.adapter.MotherboardAdapter motherboardAdapter = new com.windriver.pcgate.adapter.MotherboardAdapter(
                motherboardList);
        motherboardRecyclerView.setAdapter(motherboardAdapter);


        com.windriver.pcgate.adapter.MotherboardAdapter.OnAddToCartClickListener addToCartClickListenerMotherboard = item ->
            {
                double price = 0.0;
                try
                {
                    price = Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", ""));
                }
                catch (Exception ignored)
                {
                }
                CartItem cartItem = new CartItem(item.getName(), price, 1);
                cartViewModel.addItem(cartItem);
                android.widget.Toast.makeText(getContext(), "Added to cart",
                        android.widget.Toast.LENGTH_SHORT).show();
            };
        motherboardAdapter.setOnAddToCartClickListener(addToCartClickListenerMotherboard);

        com.windriver.pcgate.adapter.MotherboardAdapter.OnRemoveFromCartClickListener removeFromCartClickListenerMotherboard = item ->
            {
                List<CartItem> currentCart = cartViewModel.getCartItems().getValue();
                if (currentCart != null)
                {
                    for (CartItem cartItem : currentCart)
                    {
                        if (cartItem.getName().equals(item.getName()))
                        {
                            int newQty = cartItem.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(), -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(),
                                                -cartItem.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            };
        motherboardAdapter.setOnRemoveFromCartClickListener(removeFromCartClickListenerMotherboard);

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
            {
                java.util.Map<String, Integer> quantities = new java.util.HashMap<>();
                if (items != null)
                {
                    for (CartItem cartItem : items)
                    {
                        quantities.put(cartItem.getName(), cartItem.getQuantity());
                    }
                }
                motherboardAdapter.setCartQuantities(quantities);
            });

        if (persistentRandomMotherboards != null)
        {
            motherboardAdapter.setMotherboardList(persistentRandomMotherboards);
        }

        DatabaseReference motherboardRef = FirebaseDatabase.getInstance().getReference().child(
                "motherboard");
        motherboardRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                List<MotherboardItem> allMotherboards = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren())
                {
                    String name = "";
                    String price = "";
                    String imageUrl = "";
                    String color = "";
                    String ddrType = "";
                    String formFactor = "";
                    String socket = "";
                    int maxMemory = 0;
                    int memorySlots = 0;
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
                    if (child.child("ddr_type").getValue() != null)
                    {
                        ddrType = Objects.requireNonNull(
                                child.child("ddr_type").getValue()).toString();
                    }
                    if (child.child("form_factor").getValue() != null)
                    {
                        formFactor = Objects.requireNonNull(
                                child.child("form_factor").getValue()).toString();
                    }
                    if (child.child("socket").getValue() != null)
                    {
                        socket = Objects.requireNonNull(
                                child.child("socket").getValue()).toString();
                    }
                    if (child.child("max_memory").getValue() != null)
                    {
                        try
                        {
                            maxMemory = Integer.parseInt(Objects.requireNonNull(
                                    child.child("max_memory").getValue()).toString());
                        }
                        catch (Exception ignored)
                        {
                        }
                    }
                    if (child.child("memory_slots").getValue() != null)
                    {
                        try
                        {
                            memorySlots = Integer.parseInt(Objects.requireNonNull(
                                    child.child("memory_slots").getValue()).toString());
                        }
                        catch (Exception ignored)
                        {
                        }
                    }
                    allMotherboards.add(
                            new MotherboardItem(name, price, imageUrl, color, ddrType, formFactor,
                                    socket, maxMemory, memorySlots));
                }
                if (persistentRandomMotherboards == null)
                {
                    java.util.Collections.shuffle(allMotherboards);
                    persistentRandomMotherboards = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allMotherboards.size()); i++)
                    {
                        persistentRandomMotherboards.add(allMotherboards.get(i));
                    }
                    MotherboardItem viewMoreItem = new MotherboardItem("__VIEW_MORE__", "", "", "",
                            "", "", "", 0, 0);
                    persistentRandomMotherboards.add(viewMoreItem);
                }
                motherboardAdapter.setMotherboardList(persistentRandomMotherboards);
                motherboardAdapter.setAllMotherboards(allMotherboards);
                onCategoryDataLoaded();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
                android.widget.Toast.makeText(getContext(),
                        "Failed to load motherboards: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            });


        motherboardAdapter.setOnViewMoreClickListener(() ->
            {
                com.windriver.pcgate.ui.viewAll.AllMotherboardsDialog dialog = new com.windriver.pcgate.ui.viewAll.AllMotherboardsDialog(
                        motherboardAdapter.getAllMotherboards(), addToCartClickListenerMotherboard);
                dialog.show(getParentFragmentManager(), "AllMotherboardsDialog");
            });

        motherboardAdapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                Intent intent = new Intent(getContext(),
                        com.windriver.pcgate.ui.detailView.MotherboardDetailsActivity.class);
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("color", item.getColor());
                intent.putExtra("ddr_type", item.getDdrType());
                intent.putExtra("form_factor", item.getFormFactor());
                intent.putExtra("socket", item.getSocket());
                intent.putExtra("max_memory", item.getMaxMemory());
                intent.putExtra("memory_slots", item.getMemorySlots());
                startActivity(intent);
            });


        RecyclerView gpuRecyclerView = view.findViewById(R.id.gpuRecyclerView);
        gpuRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<GpuItem> gpuList = new ArrayList<>();
        com.windriver.pcgate.adapter.GpuAdapter gpuAdapter = new com.windriver.pcgate.adapter.GpuAdapter(
                gpuList);
        gpuRecyclerView.setAdapter(gpuAdapter);


        com.windriver.pcgate.adapter.GpuAdapter.OnAddToCartClickListener addToCartClickListenerGpu = item ->
            {
                double price = 0.0;
                try
                {
                    price = Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", ""));
                }
                catch (Exception ignored)
                {
                }
                CartItem cartItem = new CartItem(item.getName(), price, 1);
                cartViewModel.addItem(cartItem);
                android.widget.Toast.makeText(getContext(), "Added to cart",
                        android.widget.Toast.LENGTH_SHORT).show();
            };
        gpuAdapter.setOnAddToCartClickListener(addToCartClickListenerGpu);


        com.windriver.pcgate.adapter.GpuAdapter.OnRemoveFromCartClickListener removeFromCartClickListenerGpu = item ->
            {
                List<CartItem> currentCart = cartViewModel.getCartItems().getValue();
                if (currentCart != null)
                {
                    for (CartItem cartItem : currentCart)
                    {
                        if (cartItem.getName().equals(item.getName()))
                        {
                            int newQty = cartItem.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(), -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(),
                                                -cartItem.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            };
        gpuAdapter.setOnRemoveFromCartClickListener(removeFromCartClickListenerGpu);

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
            {
                java.util.Map<String, Integer> quantities = new java.util.HashMap<>();
                if (items != null)
                {
                    for (CartItem cartItem : items)
                    {
                        quantities.put(cartItem.getName(), cartItem.getQuantity());
                    }
                }
                gpuAdapter.setCartQuantities(quantities);
            });


        if (persistentRandomGpus != null)
        {
            gpuAdapter.setGpuList(persistentRandomGpus);
        }

        DatabaseReference gpuRef = FirebaseDatabase.getInstance().getReference("video-card");
        gpuRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                List<GpuItem> allGpus = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren())
                {
                    String name = child.child("name").getValue(String.class);
                    String price = "";
                    Object priceObj = child.child("price").getValue();
                    if (priceObj != null)
                    {
                        if (priceObj instanceof Double)
                        {
                            price = String.format("%.2f", (Double) priceObj);
                        }
                        else if (priceObj instanceof Long)
                        {
                            price = String.format("%d", (Long) priceObj);
                        }
                        else if (priceObj instanceof String)
                        {
                            price = (String) priceObj;
                        }
                        else
                        {
                            price = String.valueOf(priceObj);
                        }
                    }
                    String imageUrl = child.child("image-url").getValue(String.class);
                    String color = child.child("color").getValue(String.class);
                    String chipset = child.child("chipset").getValue(String.class);
                    String core_clock = "";
                    Object coreClockObj = child.child("core_clock").getValue();
                    if (coreClockObj != null)
                    {
                        if (coreClockObj instanceof Double)
                        {
                            core_clock = String.format("%.0f", (Double) coreClockObj);
                        }
                        else if (coreClockObj instanceof Long)
                        {
                            core_clock = String.format("%d", (Long) coreClockObj);
                        }
                        else if (coreClockObj instanceof String)
                        {
                            core_clock = (String) coreClockObj;
                        }
                        else
                        {
                            core_clock = String.valueOf(coreClockObj);
                        }
                    }
                    String boost_clock = "";
                    Object boostClockObj = child.child("boost_clock").getValue();
                    if (boostClockObj != null)
                    {
                        if (boostClockObj instanceof Double)
                        {
                            boost_clock = String.format("%.0f", (Double) boostClockObj);
                        }
                        else if (boostClockObj instanceof Long)
                        {
                            boost_clock = String.format("%d", (Long) boostClockObj);
                        }
                        else if (boostClockObj instanceof String)
                        {
                            boost_clock = (String) boostClockObj;
                        }
                        else
                        {
                            boost_clock = String.valueOf(boostClockObj);
                        }
                    }
                    int memory = 0;
                    Object memoryObj = child.child("memory").getValue();
                    if (memoryObj != null)
                    {
                        if (memoryObj instanceof Long)
                        {
                            memory = ((Long) memoryObj).intValue();
                        }
                        else if (memoryObj instanceof Integer)
                        {
                            memory = (Integer) memoryObj;
                        }
                        else if (memoryObj instanceof String)
                        {
                            try
                            {
                                memory = Integer.parseInt((String) memoryObj);
                            }
                            catch (Exception ignored)
                            {
                            }
                        }
                    }
                    int length = 0;
                    Object lengthObj = child.child("length").getValue();
                    if (lengthObj != null)
                    {
                        if (lengthObj instanceof Long)
                        {
                            length = ((Long) lengthObj).intValue();
                        }
                        else if (lengthObj instanceof Integer)
                        {
                            length = (Integer) lengthObj;
                        }
                        else if (lengthObj instanceof String)
                        {
                            try
                            {
                                length = Integer.parseInt((String) lengthObj);
                            }
                            catch (Exception ignored)
                            {
                            }
                        }
                    }
                    allGpus.add(new GpuItem(name, price, imageUrl, color, chipset, core_clock,
                            boost_clock, memory, length));
                }
                if (persistentRandomGpus == null)
                {
                    java.util.Collections.shuffle(allGpus);
                    persistentRandomGpus = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allGpus.size()); i++)
                    {
                        persistentRandomGpus.add(allGpus.get(i));
                    }
                    GpuItem viewMoreItem = new GpuItem("__VIEW_MORE__", "", "", "", "", "", "", 0,
                            0);
                    persistentRandomGpus.add(viewMoreItem);
                }
                gpuAdapter.setGpuList(persistentRandomGpus);
                gpuAdapter.setAllGpus(allGpus);
                onCategoryDataLoaded();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
                android.widget.Toast.makeText(getContext(),
                        "Failed to load GPUs: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            });


        gpuAdapter.setOnViewMoreClickListener(() ->
            {
                com.windriver.pcgate.ui.viewAll.AllGpuDialog dialog = new com.windriver.pcgate.ui.viewAll.AllGpuDialog(
                        gpuAdapter.getAllGpus(), addToCartClickListenerGpu);
                dialog.show(getParentFragmentManager(), "AllGpuDialog");
            });

        gpuAdapter.setOnItemClickListener(item ->
            {
                if ("__VIEW_MORE__".equals(item.getName()))
                {
                    return;
                }
                Intent intent = new Intent(getContext(),
                        com.windriver.pcgate.ui.detailView.GpuDetailsActivity.class);
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("color", item.getColor());
                intent.putExtra("chipset", item.getChipset());
                intent.putExtra("core_clock", item.getCoreClock());
                intent.putExtra("boost_clock", item.getBoostClock());
                intent.putExtra("memory", item.getMemory());
                intent.putExtra("length", item.getLength());
                startActivity(intent);
            });


        RecyclerView psuRecyclerView = view.findViewById(R.id.psuRecyclerView);
        psuRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<PsuItem> psuList = new ArrayList<>();
        final com.windriver.pcgate.adapter.PsuAdapter psuAdapter = new com.windriver.pcgate.adapter.PsuAdapter(
                psuList);
        psuRecyclerView.setAdapter(psuAdapter);

        com.windriver.pcgate.adapter.PsuAdapter.OnAddToCartClickListener addToCartClickListenerPsu = item ->
            {
                double price = 0.0;
                try
                {
                    price = Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", ""));
                }
                catch (Exception ignored)
                {
                }
                CartItem cartItem = new CartItem(item.getName(), price, 1);
                cartViewModel.addItem(cartItem);
                android.widget.Toast.makeText(getContext(), "Added to cart",
                        android.widget.Toast.LENGTH_SHORT).show();
            };
        psuAdapter.setOnAddToCartClickListener(addToCartClickListenerPsu);


        com.windriver.pcgate.adapter.PsuAdapter.OnRemoveFromCartClickListener removeFromCartClickListenerPsu = item ->
            {
                List<CartItem> currentCart = cartViewModel.getCartItems().getValue();
                if (currentCart != null)
                {
                    for (CartItem cartItem : currentCart)
                    {
                        if (cartItem.getName().equals(item.getName()))
                        {
                            int newQty = cartItem.getQuantity() - 1;
                            if (newQty > 0)
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(), -1));
                            }
                            else
                            {
                                cartViewModel.addItem(
                                        new CartItem(item.getName(), cartItem.getPrice(),
                                                -cartItem.getQuantity()));
                            }
                            break;
                        }
                    }
                }
            };
        psuAdapter.setOnRemoveFromCartClickListener(removeFromCartClickListenerPsu);


        DatabaseReference psuRef = FirebaseDatabase.getInstance().getReference().child(
                "power-supply");
        psuRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                List<PsuItem> allPsus = new ArrayList<>();
                if (persistentRandomPsus == null)
                {
                    for (DataSnapshot child : snapshot.getChildren())
                    {
                        String name = "";
                        String price = "";
                        String imageUrl = "";
                        String color = "";
                        String efficiency = "";
                        String modular = "";
                        String type = "";
                        int wattage = 0;
                        if (child.child("name").getValue() != null)
                        {
                            name = Objects.requireNonNull(
                                    child.child("name").getValue()).toString();
                        }
                        Object priceObj = child.child("price").getValue();
                        if (priceObj != null)
                        {
                            if (priceObj instanceof Double)
                            {
                                price = String.format("%.2f", (Double) priceObj);
                            }
                            else if (priceObj instanceof Long)
                            {
                                price = String.format("%d", (Long) priceObj);
                            }
                            else if (priceObj instanceof String)
                            {
                                price = (String) priceObj;
                            }
                            else
                            {
                                price = String.valueOf(priceObj);
                            }
                        }
                        if (child.child("image-url").getValue() != null)
                        {
                            imageUrl = Objects.requireNonNull(
                                    child.child("image-url").getValue()).toString();
                        }
                        if (child.child("color").getValue() != null)
                        {
                            color = Objects.requireNonNull(
                                    child.child("color").getValue()).toString();
                        }
                        if (child.child("efficiency").getValue() != null)
                        {
                            efficiency = Objects.requireNonNull(
                                    child.child("efficiency").getValue()).toString();
                        }
                        if (child.child("modular").getValue() != null)
                        {
                            modular = Objects.requireNonNull(
                                    child.child("modular").getValue()).toString();
                        }
                        if (child.child("type").getValue() != null)
                        {
                            type = Objects.requireNonNull(
                                    child.child("type").getValue()).toString();
                        }
                        Object wattageObj = child.child("wattage").getValue();
                        if (wattageObj != null)
                        {
                            if (wattageObj instanceof Long)
                            {
                                wattage = ((Long) wattageObj).intValue();
                            }
                            else if (wattageObj instanceof Integer)
                            {
                                wattage = (Integer) wattageObj;
                            }
                            else if (wattageObj instanceof String)
                            {
                                try
                                {
                                    wattage = Integer.parseInt((String) wattageObj);
                                }
                                catch (Exception ignored)
                                {
                                }
                            }
                        }
                        allPsus.add(
                                new PsuItem(name, price, imageUrl, color, efficiency, modular, type,
                                        wattage));
                    }

                    java.util.Collections.shuffle(allPsus);
                    persistentRandomPsus = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, allPsus.size()); i++)
                    {
                        persistentRandomPsus.add(allPsus.get(i));
                    }
                    PsuItem viewMoreItem = new PsuItem("__VIEW_MORE__", "", "", "", "", "", "", 0);
                    persistentRandomPsus.add(viewMoreItem);
                }

                psuAdapter.setPsuList(persistentRandomPsus);
                psuAdapter.setAllPsus(allPsus);

                psuAdapter.setOnItemClickListener(item ->
                    {
                        Intent intent = new Intent(getActivity(),
                                com.windriver.pcgate.ui.detailView.PsuDetailsActivity.class);
                        intent.putExtra("name", item.getName());
                        intent.putExtra("price", item.getPrice());
                        intent.putExtra("imageUrl", item.getImageUrl());
                        intent.putExtra("color", item.getColor());
                        intent.putExtra("efficiency", item.getEfficiency());
                        intent.putExtra("modular", item.getModular());
                        intent.putExtra("type", item.getType());
                        intent.putExtra("wattage", item.getWattage());
                        startActivity(intent);
                    });

                cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items ->
                    {
                        java.util.Map<String, Integer> quantities = new java.util.HashMap<>();
                        if (items != null)
                        {
                            for (CartItem cartItem : items)
                            {
                                quantities.put(cartItem.getName(), cartItem.getQuantity());
                            }
                        }
                        psuAdapter.setCartQuantities(quantities);
                    });

                psuAdapter.setOnViewMoreClickListener(() ->
                    {
                        com.windriver.pcgate.ui.viewAll.AllPsuDialog dialog = new com.windriver.pcgate.ui.viewAll.AllPsuDialog(
                                allPsus, addToCartClickListenerPsu);
                        dialog.show(getParentFragmentManager(), "AllPsuDialog");
                    });
                onCategoryDataLoaded();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                android.util.Log.e("HomeFragment", "Database error: " + error.getMessage());
                android.widget.Toast.makeText(getContext(),
                        "Failed to load PSUs: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            });

        com.google.android.material.floatingactionbutton.FloatingActionButton fabChat = view.findViewById(
                R.id.fabChat);
        fabChat.setOnClickListener(
                v -> Navigation.findNavController(view).navigate(R.id.chatFragment));
        }

    private void onCategoryDataLoaded()
        {
        int loadedCount = loadedCategories.incrementAndGet();
        if (!showcase)
        {
            loadingView.setProgress(loadedCount);
        }

        if (loadedCount >= TOTAL_CATEGORIES)
        {
            tryFinishLoading();
        }
        }

    private synchronized void tryFinishLoading()
        {
        if (allDataLoaded)
        {
            return; // Already finished
        }

        boolean dataIsReady = loadedCategories.get() >= TOTAL_CATEGORIES;
        boolean animationIsReady = !showcase || animationFinished;

        if (dataIsReady && animationIsReady)
        {
            allDataLoaded = true;
            if (getView() != null)
            { // Ensure fragment is still alive
                getView().post(() ->
                    {
                        loadingView.setVisibility(View.GONE);
                        loadingTitle.setVisibility(View.GONE);
                        showCategoryViewsWithAnimation();
                    });
            }
        }
        }

    private void showCategoryViewsWithAnimation()
        {
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        for (int i = 0; i < categoryViews.size(); i++)
        {
            final View view = categoryViews.get(i);
            handler.postDelayed(() -> view.setVisibility(View.VISIBLE), i * 100);
        }
        }

    @NonNull
    private Intent getIntent(MemoryItem item)
        {
        Intent intent = new Intent(getContext(),
                com.windriver.pcgate.ui.detailView.MemoryDetailsActivity.class);
        intent.putExtra("name", item.getName());
        intent.putExtra("price", item.getPrice());
        intent.putExtra("imageUrl", item.getImageUrl());
        intent.putExtra("ddr_type", item.getDdrType());
        intent.putExtra("color", item.getColor());
        intent.putExtra("cas_latency", item.getCasLatency());
        intent.putExtra("first_word_latency", item.getFirstWordLatency());
        return intent;
        }
    }
