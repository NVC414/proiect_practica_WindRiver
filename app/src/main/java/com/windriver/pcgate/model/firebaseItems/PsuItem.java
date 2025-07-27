package com.windriver.pcgate.model.firebaseItems;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PsuItem
    {
    private String name;
    private String price;
    private String imageUrl;
    private String color;
    private String efficiency;
    private String modular;
    private String type;
    private int wattage;
    }

/*
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
        this.price = "$"+price;
        this.imageUrl = imageUrl;
        this.color = color;
        this.efficiency = efficiency;
        this.modular = modular;
        this.type = type;
        this.wattage = wattage;
    }
}
*/

