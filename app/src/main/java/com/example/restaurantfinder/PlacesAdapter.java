package com.example.restaurantfinder;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.type;
import static com.example.restaurantfinder.R.raw.restaurants;

/**
 * Created by YD on 2017-11-22.
 */

public class PlacesAdapter extends BaseAdapter {
    Activity context;
    ArrayList<Restaurant> restaurants;
    private static final String TAG = "PlacesAdapter";


    public PlacesAdapter(Activity context,ArrayList<Restaurant>rest)
    {

        super();
        this.context = context;
        this.restaurants = rest;
        Log.d(TAG,"Adapter constructor called");
    }

    @Override
    public int getCount() {
        Log.d(TAG,"getCount called returning: "+restaurants.size());
        return restaurants.size();
    }

    @Override
    public Object getItem(int i) {
        Log.d(TAG,"Adapter getItem called");
        return restaurants.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.list_layout,null);
        }

        TextView name = (TextView)view.findViewById(R.id.name_tv);
        name.setText(restaurants.get(i).getName());

        TextView price = (TextView)view.findViewById(R.id.pricetext);
        price.setText("$ "+restaurants.get(i).getPrice()); //change to restaurant price

        TextView type = (TextView)view.findViewById(R.id.typeText);
        type.setText(restaurants.get(i).getType()); //change to restaurant type

        ImageView img = (ImageView)view.findViewById(R.id.theimageView);
        img.setImageResource(restaurants.get(i).getImg());


        Log.d(TAG,"View method called, returning the customized view");

        return view;
    }

}
