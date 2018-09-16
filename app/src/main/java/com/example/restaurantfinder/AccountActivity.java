package com.example.restaurantfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.O;

public class AccountActivity extends AppCompatActivity {
    private TextView name;
    private Button logOut;
    private Button displayFav;
    private DBHelper myDb;
    private int images[] = {R.drawable.burger,R.drawable.cafe,R.drawable.indian,
            R.drawable.italian,R.drawable.japanese,R.drawable.mexican,
            R.drawable.spanish,R.drawable.vegetarian};
    private ArrayList<Restaurant> restaurantList;
    private Restaurant restaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_restaurant);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Account");

        myDb=new DBHelper(this);
        restaurantList = new ArrayList<Restaurant>();


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPref.getString("userName","nothing to show");
        name = (TextView)findViewById(R.id.name_tv);
        name.setText(userName);

        logOut = (Button)findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                goToLoginActivity();
            }
        });

        displayFav = (Button)findViewById(R.id.displayFavbtn);
        displayFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean tableEmpty = myDb.checkFavoritesTable();
                if(tableEmpty == false)
                {
                    Toast.makeText(AccountActivity.this, "You haven't added any favorites yet",
                            Toast.LENGTH_LONG).show();
                }else
                {

                    //int records = myDb.countRows();
                    getPlacesList();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.itemMenu:
                startActivity(new Intent(AccountActivity.this,SelectActivity.class));
                break;
            case R.id.itemTypes:
                startActivity(new Intent(AccountActivity.this,TypesActivity.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void getPlacesList() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = sharedPref.getString("userId","nothing to show");

        Cursor cursor = myDb.readFavoritesAndPlacesTable(userId);
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
                    restaurant = new Restaurant(images[7],cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                    break;
            }


            restaurantList.add(restaurant);

        }


        //Creates a listView and sets the custom adapter to it
        ListView list = (ListView) findViewById(R.id.favList);
        list.setAdapter(new PlacesAdapter(this, restaurantList));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String placeId = adapterView.getAdapter().getItem(i).toString();

                Intent intent = new Intent(AccountActivity.this,PlaceDetails.class);
                intent.putExtra("id",placeId);
                startActivity(intent);

                //Toast.makeText(PlacesList.this, placeName, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void goToLoginActivity()
    {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }
}
