package com.windriver.pcgate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpuItem{
    private String name;
    private double price;
    private String imageUrl;
    private double boostClock;
    private double coreClock;
    private int coreCount;
    private String graphics;
    private boolean smt;
    private String socket;
    private int tdp;
}


/*
public class CpuItem
    {
    public String name;
    public double price;
    public String imageUrl;
    public double boostClock;
    public double core_clock;
    public int core_count;
    public String graphics;
    public boolean smt;
    public String socket;
    public int tdp;

    public CpuItem()
        {
        }

    public CpuItem(String name, double price, String imageUrl, double boostClock,
                   double core_clock, int core_count, String graphics, boolean smt, String socket,
                   int tdp)
        {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.boostClock = boostClock;
        this.core_clock = core_clock;
        this.core_count = core_count;
        this.graphics = graphics;
        this.smt = smt;
        this.socket = socket;
        this.tdp = tdp;
        }
    }
*/

