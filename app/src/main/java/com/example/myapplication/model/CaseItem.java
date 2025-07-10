package com.example.myapplication.model;

public class CaseItem {
    public String name;
    public String price;
    public String imageUrl;

    public CaseItem() {}
    public CaseItem(String name, String price, String imageUrl) {
        this.name = name;
        this.price = price+" RON";
        this.imageUrl = imageUrl;
    }
}
