package com.example.restaurantfinder;

import android.app.Activity;
import android.media.Image;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.restaurantfinder.R.id.imageView;

/**
 * Created by YD on 2017-11-24.
 */

public class TypesAdapter extends BaseAdapter {
    Activity context;
    int images [];
    String types[];

    public  TypesAdapter(Activity context,int images[], String types[])
    {
        super();
        this.context = context;
        this.images = images;
        this.types = types;

    }
    @Override
    public int getCount() {
        return types.length;
    }

    @Override
    public Object getItem(int i) {
        return types[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.gird_item, null);
        }
        ImageView imageView = (ImageView)view.findViewById(R.id.itemImage);
        TextView textView = (TextView)view.findViewById(R.id.itemText);


        imageView.setImageResource(images[i]);
        textView.setText(types[i]);

        return view;
    }
}
