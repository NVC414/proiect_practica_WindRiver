package com.windriver.pcgate.model;

public class CaseItem implements ShopItem
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
    private int quantity = 0;

    public CaseItem()
        {
        }

    public CaseItem(String name, String price, String imageUrl, String color, String type,
                    String side_panel, String psu, int internal_35_bays, double external_volume)
        {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.color = color;
        this.type = type;
        this.side_panel = side_panel;
        this.psu = psu;
        this.internal_35_bays = internal_35_bays;
        this.external_volume = external_volume;
        }

    // ShopItem interface methods
    @Override
    public String getName()
        {
        return name;
        }

    @Override
    public double getPrice()
        {
        try
        {
            return Double.parseDouble(price.replace("$", ""));
        }
        catch (Exception e)
        {
            return 0.0;
        }
        }

    @Override
    public String getImageUrl()
        {
        return imageUrl;
        }

    @Override
    public int getQuantity()
        {
        return quantity;
        }

    @Override
    public void setQuantity(int quantity)
        {
        this.quantity = quantity;
        }
    }
