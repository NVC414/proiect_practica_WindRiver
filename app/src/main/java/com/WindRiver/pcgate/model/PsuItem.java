package com.windriver.pcgate.model;

public class PsuItem {
    public String name;
    public String price;
    public String imageUrl;
    public String color;
    public String efficiency;
    public String modular;
    public String type;
    public int wattage;

    public PsuItem() {}

    public PsuItem(String name, String price, String imageUrl, String color, String efficiency, String modular, String type, int wattage) {
        this.name = name;
        this.price = price + " RON";
        this.imageUrl = imageUrl;
        this.color = color;
        this.efficiency = efficiency;
        this.modular = modular;
        this.type = type;
        this.wattage = wattage;
    }
}

