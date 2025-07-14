package com.example.myapplication.model;

public class CpuItem
    {
    public String name;
    public double price;
    public String imageUrl;
    public double boost_clock;
    public double core_clock;
    public int core_count;
    public String graphics;
    public boolean smt;
    public String socket;
    public int tdp;

    public CpuItem()
        {
        }

    public CpuItem(String name, double price, String imageUrl, double boost_clock,
                   double core_clock, int core_count, String graphics, boolean smt, String socket,
                   int tdp)
        {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.boost_clock = boost_clock;
        this.core_clock = core_clock;
        this.core_count = core_count;
        this.graphics = graphics;
        this.smt = smt;
        this.socket = socket;
        this.tdp = tdp;
        }
    }

