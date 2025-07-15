package com.windriver.pcgate.model;

public class CaseItem
    {
    public String name;
    public String price;
    public String imageUrl;
    public String color;
    public String type;
    public String side_panel;
    public String psu;
    public int internal_35_bays;
    public double external_volume;

    public CaseItem()
        {
        }

    public CaseItem(String name, String price, String imageUrl, String color, String type,
                    String side_panel, String psu, int internal_35_bays, double external_volume)
        {
        this.name = name;
        this.price = price + " RON";
        this.imageUrl = imageUrl;
        this.color = color;
        this.type = type;
        this.side_panel = side_panel;
        this.psu = psu;
        this.internal_35_bays = internal_35_bays;
        this.external_volume = external_volume;
        }
    }
