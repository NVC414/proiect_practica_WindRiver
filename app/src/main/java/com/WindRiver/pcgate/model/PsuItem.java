package com.windriver.pcgate.model;

public class PsuItem implements ShopItem
    {
    public String name;
    public String price;
    public String imageUrl;
    public String color;
    public String efficiency;
    public String modular;
    public String type;
    public int wattage;
    private int quantity = 0;

    public PsuItem() {}

    public PsuItem(String name, String price, String imageUrl, String color, String efficiency, String modular, String type, int wattage) {
        this.name = name;
    this.price = price;
        this.imageUrl = imageUrl;
        this.color = color;
        this.efficiency = efficiency;
        this.modular = modular;
        this.type = type;
        this.wattage = wattage;
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
