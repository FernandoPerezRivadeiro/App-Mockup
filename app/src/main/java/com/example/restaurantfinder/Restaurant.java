package com.example.restaurantfinder;

import static android.support.v7.widget.AppCompatDrawableManager.get;

/**
 * Created by YD on 2017-11-22.
 */

public class Restaurant {
     String id;
    private String name;
    private String address;
    private double price;
    private String openHours;
    private String closeHours;
    private String type;
    private int img;


    public Restaurant(int img,String id , String name, String address,double price,String openHours,
                             String closeHours, String type)
    {
        this.img = img;
        this.id =id;
        this.name =name;
        this.address = address;
        this.price = price;
        this.openHours = openHours;
        this.closeHours = closeHours;
        this.type = type;
    }
    public String getName()
    {
        return this.name;
    }
    public String getAddress()
    {
        return this.address;
    }
    public double getPrice()
    {
        return this.price;
    }
    public String getOpenHours()
    {
        return this.openHours;
    }
    public String getCloseHours()
    {
        return this.closeHours;
    }
    public String getType()
    {
        return this.type;
    }
    public String getId()
    {
        return this.id;
    }
    public int getImg()
    {
        return this.img;
    }
    public String toString()
    {
        return getId();
    }


}
