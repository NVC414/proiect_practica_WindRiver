package com.windriver.pcgate.model;

public class MotherboardItem implements ShopItem
    {
    public String name;
    public String price;
    public String imageUrl;
    public String color;
    public String ddrType;
    public String formFactor;
    public String socket;
    public int maxMemory;
    public int memorySlots;
    private int quantity = 0;

    public MotherboardItem() {}

    public MotherboardItem(String name, String price, String imageUrl, String color, String ddrType,
                           String formFactor, String socket, int maxMemory, int memorySlots) {
        this.name = name;
    this.price = price;
        this.imageUrl = imageUrl;
        this.color = color;
        this.ddrType = ddrType;
        this.formFactor = formFactor;
        this.socket = socket;
        this.maxMemory = maxMemory;
        this.memorySlots = memorySlots;
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
