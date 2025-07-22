package com.windriver.pcgate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotherboardItem {
    private String name;
    private String price;
    private String imageUrl;
    private String color;
    private String ddrType;
    private String formFactor;
    private String socket;
    private int maxMemory;
    private int memorySlots;
}
/*
public class MotherboardItem {
    public String name;
    public String price;
    public String imageUrl;
    public String color;
    public String ddrType;
    public String formFactor;
    public String socket;
    public int maxMemory;
    public int memorySlots;

    public MotherboardItem() {}

    public MotherboardItem(String name, String price, String imageUrl, String color, String ddrType,
                           String formFactor, String socket, int maxMemory, int memorySlots) {
        this.name = name;
        this.price = "$"+price;
        this.imageUrl = imageUrl;
        this.color = color;
        this.ddrType = ddrType;
        this.formFactor = formFactor;
        this.socket = socket;
        this.maxMemory = maxMemory;
        this.memorySlots = memorySlots;
    }
}
*/

