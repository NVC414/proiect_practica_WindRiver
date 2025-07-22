package com.windriver.pcgate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaptopItem {
    private String brand;
    private String model;
    private String price;
    private String imageUrl;
    private String processor;
    private String ramGb;
    private String ramType;
    private String graphicCardGb;
    private String hdd;
    private String ssd;
}


/*
public class LaptopItem
    {
    public String brand;
    public String model;
    public String price;
    public String imageUrl;
    public String processor;
    public String ramGb;
    public String ramType;
    public String graphicCardGb;
    public String hdd;
    public String ssd;

    public LaptopItem()
        {
        }

    public LaptopItem(String brand, String model, String price, String imageUrl, String processor,
                      String ramGb, String ramType, String graphicCardGb, String hdd,
                      String ssd)
        {
        this.brand = brand;
        this.model = model;
        this.price = "$" + price;
        this.imageUrl = imageUrl;
        this.processor = processor;
        this.ramGb = ramGb;
        this.ramType = ramType;
        this.graphicCardGb = graphicCardGb;
        this.hdd = hdd;
        this.ssd = ssd;
        }
    }
*/

