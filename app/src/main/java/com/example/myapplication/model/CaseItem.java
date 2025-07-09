package com.example.myapplication.model;

public class CaseItem {
    public String name;
    public String price;

    public CaseItem() {}
    public CaseItem(String name, String price) {
        this.name = name;
        this.price = price+" RON";
    }
}

