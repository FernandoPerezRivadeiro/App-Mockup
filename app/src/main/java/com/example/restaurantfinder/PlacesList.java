package com.example.restaurantfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.R.id.list;

import static com.example.restaurantfinder.R.drawable.vegetarian;
import static com.example.restaurantfinder.R.raw.restaurants;

public class PlacesList extends AppCompatActivity {
    private DBHelper myDb;
    private ArrayList<Restaurant> restaurantList;
    private Restaurant restaurant;

    private int images[] = {R.drawable.burger,R.drawable.cafe,R.drawable.indian,
            R.drawable.italian,R.drawable.japanese,R.drawable.mexican,
            R.drawable.spanish, vegetarian};
    private static final String TAG = "PlacesListActivity: ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_restaurant);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Restaurants");

        restaurantList = new ArrayList<Restaurant>();
        myDb = new DBHelper(this);

        Intent inten =getIntent();
        int index = Integer.parseInt(getIntent().getStringExtra("index"));

        switch(index)
        {
            case 0:
                getBurgerShopList();
                break;
            case 1:
                getCafesList();
                break;
            case 2:
                getIndianRestaurantList();
                break;
            case 3:
                getItalianRestaurantList();
                break;
            case 4:
                getJapaneseList();
                break;
            case 5:
                getMexicanList();
                break;
            case 6:
                getSpanishList();
                break;
            case 7:
                getVegetarianList();
                break;
            case 8:
                sortByPriceRange();
                break;
            case 9:
                getPlacesList();

        }
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
                startActivity(new Intent(PlacesList.this,AccountActivity.class));
                break;
            case R.id.listMenu:
                startActivity(new Intent(PlacesList.this,SelectActivity.class));
                break;
            case R.id.listTypes:
                startActivity(new Intent(PlacesList.this,TypesActivity.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets the price range selected by the user in the price activity and
     * sorts results by price from lowest to highest
     */
    public void sortByPriceRange()
    {
        Intent it = getIntent();
        Bundle extras = it.getExtras();
        double fromValue = extras.getDouble("fromValue");
        double toValue = extras.getDouble("toValue");

        Cursor cursor = myDb.getPriceRange(fromValue,toValue);
        if(cursor.getCount() !=0)
        {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                switch (cursor.getString(6))
                {
                    case "Burger shop":
                        restaurant = new Restaurant(images[0],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Cafe":
                        restaurant = new Restaurant(images[1],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Indian":
                        restaurant = new Restaurant(images[2],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Italian":
                        restaurant = new Restaurant(images[3],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Japanese":
                        restaurant = new Restaurant(images[4],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Mexican":
                        restaurant = new Restaurant(images[5],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Spanish":
                        restaurant = new Restaurant(images[6],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Vegetarian":
                        restaurant = new Restaurant(images[1],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                }

                restaurantList.add(restaurant);
            }

            //Creates a listView and sets the custom adapter to it
            ListView list = (ListView) findViewById(R.id.PlacesList);
            list.setAdapter(new PlacesAdapter(this, restaurantList));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String placeId = adapterView.getAdapter().getItem(i).toString();

                    Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                    intent.putExtra("id",placeId);
                    startActivity(intent);

                }
            });

        }else{
            pricesDialog();
        }
    }

    /**
     * Gets all restaurant of the type Burguer shop by calling the
     * getBurguerShops method which extracts the info from the  data base
     */
    public  void getBurgerShopList()
    {
        Cursor cursor = myDb.getBurguerShops();
        if (cursor != null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                //As the cursor moves through the selected rows resulting from the query
                //A new restaurant object is created and added to a list of restaurants
                restaurant = new Restaurant(images[0],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                restaurantList.add(restaurant);
            }
        }

        //Creates a listView and sets the custom adapter to it
        ListView list = (ListView) findViewById(R.id.PlacesList);
        list.setAdapter(new PlacesAdapter(this, restaurantList));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Calls the method toString from the Restaaurant object which returns the place Id
                String placeId = adapterView.getAdapter().getItem(i).toString();

                Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);
            }
        });
    }

    public void getCafesList()
    {
        Cursor cursor = myDb.getCafe();
        if (cursor != null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                //As the cursor moves through the selected rows resulting from the query
                //A new restaurant object is created and added to a list of restaurants
                restaurant = new Restaurant(images[1],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                restaurantList.add(restaurant);
            }
        }
        //Creates a listView and sets the custom adapter to it
        ListView list = (ListView) findViewById(R.id.PlacesList);
        list.setAdapter(new PlacesAdapter(this, restaurantList));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Calls the method toString from the Restaurant object which returns the place Id
                String placeId = adapterView.getAdapter().getItem(i).toString();

                /*An intent is created which will change the present activity
                 to the PlaceDetails activity and passes the place id*/
                Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);
            }
        });

    }

    public void getIndianRestaurantList()
    {
        Cursor cursor = myDb.getIndian();
        if (cursor != null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                //As the cursor moves through the selected rows resulting from the query
                //A new restaurant object is created and added to a list of restaurants
                restaurant = new Restaurant(images[2],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                restaurantList.add(restaurant);
            }
        }
        //Creates a listView and sets the custom adapter to it
        ListView list = (ListView) findViewById(R.id.PlacesList);
        list.setAdapter(new PlacesAdapter(this, restaurantList));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Calls the method toString from the Restaurant object which returns the place Id
                String placeId = adapterView.getAdapter().getItem(i).toString();

                /*An intent is created which will change the present activity
                 to the PlaceDetails activity and passes the place id*/
                Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);
            }
        });
    }

    public void getItalianRestaurantList()
    {
        Cursor cursor = myDb.getItalian();
        if (cursor != null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                //As the cursor moves through the selected rows resulting from the query
                //A new restaurant object is created and added to a list of restaurants
                restaurant = new Restaurant(images[3],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                restaurantList.add(restaurant);
            }
        }
        //Creates a listView and sets the custom adapter to it
        ListView list = (ListView) findViewById(R.id.PlacesList);
        list.setAdapter(new PlacesAdapter(this, restaurantList));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Calls the method toString from the Restaurant object which returns the place Id
                String placeId = adapterView.getAdapter().getItem(i).toString();

                /*An intent is created which will change the present activity
                 to the PlaceDetails activity and passes the place id*/
                Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);
            }
        });
    }

    public void getJapaneseList()
    {
        Cursor cursor = myDb.getJapanese();
        if (cursor != null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                //As the cursor moves through the selected rows resulting from the query
                //A new restaurant object is created and added to a list of restaurants
                restaurant = new Restaurant(images[4],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                restaurantList.add(restaurant);
            }
        }
        //Creates a listView and sets the custom adapter to it
        ListView list = (ListView) findViewById(R.id.PlacesList);
        list.setAdapter(new PlacesAdapter(this, restaurantList));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Calls the method toString from the Restaurant object which returns the place Id
                String placeId = adapterView.getAdapter().getItem(i).toString();

                /*An intent is created which will change the present activity
                 to the PlaceDetails activity and passes the place id*/
                Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);
            }
        });
    }
    public void getMexicanList()
    {
        Cursor cursor = myDb.getMexican();
        if (cursor != null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                //As the cursor moves through the selected rows resulting from the query
                //A new restaurant object is created and added to a list of restaurants
                restaurant = new Restaurant(images[5],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                restaurantList.add(restaurant);
            }
        }
        //Creates a listView and sets the custom adapter to it
        ListView list = (ListView) findViewById(R.id.PlacesList);
        list.setAdapter(new PlacesAdapter(this, restaurantList));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Calls the method toString from the Restaurant object which returns the place Id
                String placeId = adapterView.getAdapter().getItem(i).toString();

                /*An intent is created which will change the present activity
                 to the PlaceDetails activity and passes the place id*/
                Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);
            }
        });
    }
    public void getSpanishList()
    {
        Cursor cursor = myDb.getSpanish();
        if (cursor != null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                //As the cursor moves through the selected rows resulting from the query
                //A new restaurant object is created and added to a list of restaurants
                restaurant = new Restaurant(images[6],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                restaurantList.add(restaurant);
            }
        }
        //Creates a listView and sets the custom adapter to it
        ListView list = (ListView) findViewById(R.id.PlacesList);
        list.setAdapter(new PlacesAdapter(this, restaurantList));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Calls the method toString from the Restaurant object which returns the place Id
                String placeId = adapterView.getAdapter().getItem(i).toString();

                /*An intent is created which will change the present activity
                 to the PlaceDetails activity and passes the place id*/
                Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);
            }
        });
    }

    public void getVegetarianList()
    {
        try {
            Cursor cursor = myDb.getVegetarian();
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                while (cursor.moveToNext()) {
                    //As the cursor moves through the selected rows resulting from the query
                    //A new restaurant object is created and added to a list of restaurants
                    restaurant = new Restaurant(images[7], cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                    restaurantList.add(restaurant);
                }
            } else {
               VegetarianDialog();
            }
            //Creates a listView and sets the custom adapter to it
            ListView list = (ListView) findViewById(R.id.PlacesList);
            list.setAdapter(new PlacesAdapter(this, restaurantList));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    //Calls the method toString from the Restaurant object which returns the place Id
                    String placeId = adapterView.getAdapter().getItem(i).toString();

                /*An intent is created which will change the present activity
                 to the PlaceDetails activity and passes the place id*/
                    Intent intent = new Intent(PlacesList.this, PlaceDetails.class);
                    intent.putExtra("id", placeId);
                    startActivity(intent);
                }
            });
        }catch (Exception e)
        {
            e.getStackTrace();
        }
    }



    public void getPlacesList(){
        Log.d(TAG,"getPlacesList called");
            Cursor cursor = myDb.readPlacesTable();
            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No records found", Toast.LENGTH_SHORT).show();
                return;
            }

            while (cursor.moveToNext()) {
                switch (cursor.getString(6))
                {
                    case "Burger shop":
                        restaurant = new Restaurant(images[0],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Cafe":
                        restaurant = new Restaurant(images[1],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Indian":
                        restaurant = new Restaurant(images[2],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Italian":
                        restaurant = new Restaurant(images[3],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Japanese":
                        restaurant = new Restaurant(images[4],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Mexican":
                        restaurant = new Restaurant(images[5],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Spanish":
                        restaurant = new Restaurant(images[6],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                    case "Vegetarian":
                        restaurant = new Restaurant(images[1],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        break;
                }
                restaurantList.add(restaurant);
            }


            //Creates a listView and sets the custom adapter to it
            ListView list = (ListView) findViewById(R.id.PlacesList);
            list.setAdapter(new PlacesAdapter(this, restaurantList));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String placeId = adapterView.getAdapter().getItem(i).toString();

                Intent intent = new Intent(PlacesList.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);

            }
        });

    }

    /**
     * Creates a dialog box for the prices range warning
     */
    public void pricesDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Set dialog characteristics
        builder.setMessage("No records found. Try a different price range");

        //Set buttons for the dialog
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(PlacesList.this,SelectActivity.class));
            }
        });

        //create the dialog
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    public void VegetarianDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Set dialog characteristics
        builder.setMessage("Sorry, No vegetarian restaurants to display at the moment");

        //Set buttons for the dialog
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(PlacesList.this,TypesActivity.class));
            }
        });

        //create the dialog
        AlertDialog dialog  = builder.create();
        dialog.show();
    }
}
