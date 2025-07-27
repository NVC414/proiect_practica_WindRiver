package com.windriver.pcgate.model.firebaseItems;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GpuItem
    {
    private String name;
    private String price;
    private String imageUrl;
    private String color;
    private String chipset;
    private String coreClock;
    private String boostClock;
    private int memory;
    private int length;
    }

/*
public class GpuItem {
    public String name;
    public String price;
    public String imageUrl;
    public String color;
    public String chipset;
    public String core_clock;
    public String boost_clock;
    public int memory;
    public int length;

    public GpuItem() {}

    public GpuItem(String name, String price, String imageUrl, String color, String chipset,
                   String core_clock, String boost_clock, int memory, int length) {
        this.name = name;
        this.price = "$"+price;
        this.imageUrl = imageUrl;
        this.color = color;
        this.chipset = chipset;
        this.core_clock = core_clock;
        this.boost_clock = boost_clock;
        this.memory = memory;
        this.length = length;
    }
}
*/

