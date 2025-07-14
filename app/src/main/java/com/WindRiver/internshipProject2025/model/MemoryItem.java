package com.WindRiver.internshipProject2025.model;

import java.util.List;

public class MemoryItem
    {
    public String name;
    public double price;
    public String imageUrl;
    public String color;
    public String ddr_type;
    public int cas_latency;
    public int first_word_latency;
    public List<Integer> modules; // [count, size]
    public List<Integer> speed;   // [type, value]

    public MemoryItem()
        {
        }

    public MemoryItem(String name, double price, String imageUrl, String color, String ddr_type,
                      int cas_latency, int first_word_latency, List<Integer> modules,
                      List<Integer> speed)
        {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.color = color;
        this.ddr_type = ddr_type;
        this.cas_latency = cas_latency;
        this.first_word_latency = first_word_latency;
        this.modules = modules;
        this.speed = speed;
        }
    }

