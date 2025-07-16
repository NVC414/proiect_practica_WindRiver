package com.windriver.pcgate.model;

public class LaptopItem implements ShopItem
    {
    public String brand;
    public String model;
    public String price;
    public String imageUrl;
    public String processor;
    public String ram_gb;
    public String ram_type;
    public String graphic_card_gb;
    public String hdd;
    public String ssd;
    private int quantity = 0;

    public LaptopItem()
        {
        }

    public LaptopItem(String brand, String model, String price, String imageUrl, String processor,
                      String ram_gb, String ram_type, String graphic_card_gb, String hdd,
                      String ssd)
        {
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
        this.processor = processor;
        this.ram_gb = ram_gb;
        this.ram_type = ram_type;
        this.graphic_card_gb = graphic_card_gb;
        this.hdd = hdd;
        this.ssd = ssd;
    }

    // ShopItem interface methods
    @Override
    public String getName()
        {
        return model;
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
