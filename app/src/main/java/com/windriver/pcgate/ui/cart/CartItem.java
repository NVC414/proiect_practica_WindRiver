package com.windriver.pcgate.ui.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CartItem
    {
    private String name;
    private double price;
    private int quantity;

/*
    public CartItem(String name, double price, int quantity)
        {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        }

    public String getName()
        {
        return name;
        }

    public double getPrice()
        {
        return price;
        }

    public int getQuantity()
        {
        return quantity;
        }

    public void setQuantity(int quantity)
        {
        this.quantity = quantity;
        }
*/
    }

