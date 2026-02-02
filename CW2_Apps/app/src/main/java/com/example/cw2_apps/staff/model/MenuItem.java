package com.example.cw2_apps.staff.model;

public class MenuItem {
    public long id;
    public String name;
    public double price;
    public String imageUri;

    public MenuItem(long id, String name, double price, String imageUri) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUri = imageUri;
    }

}
