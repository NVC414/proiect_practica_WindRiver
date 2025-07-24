package com.windriver.pcgate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseItem {
    private String name;
    private String price;
    private String imageUrl;
    private String color;
    private String type;
    private String sidePanel;
    private String psu;
    private int internal35Bays;
    private double externalVolume;
}
