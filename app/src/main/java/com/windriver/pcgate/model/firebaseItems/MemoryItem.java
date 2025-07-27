package com.windriver.pcgate.model.firebaseItems;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoryItem
    {
    private String name;
    private double price;
    private String imageUrl;
    private String color;
    private String ddrType;
    private int casLatency;
    private int firstWordLatency;
    private List<Integer> modules;
    private List<Integer> speed;
    }

/*
public class MemoryItem
    {
    public String name;
    public double price;
    public String imageUrl;
    public String color;
    public String ddr_type;
    public int cas_latency;
    public int first_word_latency;
    public List<Integer> modules;
    public List<Integer> speed;

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

*/
