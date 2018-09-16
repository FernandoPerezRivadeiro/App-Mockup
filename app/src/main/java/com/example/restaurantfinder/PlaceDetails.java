package com.example.restaurantfinder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlaceDetails extends AppCompatActivity {
    private DBHelper myDb;
    private Restaurant rest;
    private int images[] = {R.drawable.burger,R.drawable.cafe,R.drawable.indian,
            R.drawable.italian,R.drawable.japanese,R.drawable.mexican,
            R.drawable.spanish,R.drawable.vegetarian};
    private Button favBtn;
    private int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_restaurant);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Details");

        myDb = new DBHelper(this);  //Data base initialization

        gettingDetails();       //A call to the getting Details method


        getFavoritesTableRowsCount();
        checkRecordInFavoritesTable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.listAccount:
                startActivity(new Intent(PlaceDetails.this,AccountActivity.class));
                break;
            case R.id.listMenu:
                startActivity(new Intent(PlaceDetails.this,SelectActivity.class));
                break;
            case R.id.listTypes:
                startActivity(new Intent(PlaceDetails.this,TypesActivity.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public String gettingPlaceId()
    {
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }
    public String gettingUserId()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString("userId","not found");
    }

    /**
     *Gets the details from the restaurant selected from the restaurants list view
     * and populates the textViews with the data obtained from the data base
     */
    public void gettingDetails()
    {
        String place_id = gettingPlaceId();
        TextView name = (TextView)findViewById(R.id.tv_name);
        TextView address = (TextView)findViewById(R.id.tv_addr);
        TextView hours = (TextView)findViewById(R.id.tv_hours);
        TextView price = (TextView)findViewById(R.id.tv_price);
        TextView type = (TextView)findViewById(R.id.tv_type);
        ImageView restPic=(ImageView)findViewById(R.id.imageDetails);



        Cursor cursor = myDb.getPlaceDetails(place_id);


        if(cursor.getCount() ==0)
        {
            Toast.makeText(this, "The table is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        cursor.moveToFirst();
        switch (cursor.getString(6))
        {
            case "Burger shop":
                restPic.setImageResource(images[0]);
                break;
            case "Cafe":
                restPic.setImageResource(images[1]);
                break;
            case "Indian":
                restPic.setImageResource(images[2]);
                break;
            case "Italian":
                restPic.setImageResource(images[3]);
                break;
            case "Japanese":
                restPic.setImageResource(images[4]);
                break;
            case "Mexican":
                restPic.setImageResource(images[5]);
                break;
            case "Spanish":
                restPic.setImageResource(images[6]);
                break;
            case "Vegetarian":
                restPic.setImageResource(images[7]);
                break;
        }

        name.setText(cursor.getString(1));
        address.setText("  Address:  "+cursor.getString(2));
        price.setText("  Average Price:  $"+cursor.getString(3));
        hours.setText("  Open from:  "+cursor.getString(4)+" to "+cursor.getString(5));
        type.setText("  Restaurant type:  "+cursor.getString(6));

    }

    //gets the amount of rows in table favorites
    public void getFavoritesTableRowsCount()
    {
        boolean tableEmpty = myDb.checkFavoritesTable();
        if(tableEmpty == false)
        {
            Toast.makeText(this, "Favorites table is empty", Toast.LENGTH_SHORT).show();

        }

    }


    //check if there are any records in the table favorites
    public void checkRecordInFavoritesTable()
    {
        final String placeId = gettingPlaceId();
        final String userId = gettingUserId();
        favBtn = (Button)findViewById(R.id.favButton);

        Cursor cursor  = myDb.checkExistingPlaceInTable(gettingPlaceId(),gettingUserId());
        if(cursor.getCount() == 0)
        {
            Toast.makeText(this, "no records to read", Toast.LENGTH_SHORT).show();
        }else
        {
            while(cursor.moveToNext()) {

                if (cursor.getString(0).equals(gettingUserId()) &&
                        cursor.getString(1).equals(gettingPlaceId()))
                {
                    flag = 1;
                    favBtn.setText("Remove from favorites");
                    favBtn.setBackgroundColor(Color.rgb(255, 212, 38));
                    favBtn.setTextColor(Color.rgb(255, 255, 255));

                }
            }

        }


        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean inserted;
                int removed;
                switch(flag)
                {
                    case 0:
                    {
                        inserted = myDb.insertIntoFavoritesTable(placeId,userId);
                        if(inserted == true)
                        {
                            flag = 1;
                            favBtn.setBackgroundColor(Color.rgb(255, 212, 38));
                            favBtn.setTextColor(Color.rgb(255, 255, 255));
                            favBtn.setText("Remove from favorites");
                        }
                    }
                    break;
                    case 1:
                    {
                        removed =myDb.deleteDataFromFavoritesTable(placeId,userId);
                        if(removed >0)
                        {
                            flag = 0;
                            favBtn.setBackgroundColor(0);
                            favBtn.setText("Add to favorites");
                            favBtn.setTextColor(Color.rgb(255, 212, 38));
                        }

                    }
                }
            }
        });

    }
}
