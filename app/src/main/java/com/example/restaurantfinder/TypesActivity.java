package com.example.restaurantfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class TypesActivity extends AppCompatActivity {
    DBHelper myDb;
    Intent intent;
    int images[] = {R.drawable.burger,R.drawable.cafe,R.drawable.indian,
            R.drawable.italian,R.drawable.japanese,R.drawable.mexican,
            R.drawable.spanish,R.drawable.vegetarian};
    String typesOfRestaurants[] = {"Burguer Shop","Cafe","Indian","Italian","Japanese","Mexican",
    "Spanish","Vegetarian"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);

        myDb = new DBHelper(this);
        GridView grid = (GridView)findViewById(R.id.typesGrid);
        grid.setAdapter(new TypesAdapter(this,images,typesOfRestaurants));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_restaurant);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Types of Restaurant");

        /**
         * Sets an onCLickListener event for each of the items in the gridView.
         * Every time an item is clicked it sends an integer to the next activity indicating
         * what type of restaurant was selected from the gridView
         */
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i)
                {
                    case 0:
                    {
                     intent = new Intent(TypesActivity.this,PlacesList.class);
                        intent.putExtra("index","0");
                        startActivity(intent);
                    }
                    break;
                    case 1:
                    {
                        intent = new Intent(TypesActivity.this,PlacesList.class);
                        intent.putExtra("index","1");
                        startActivity(intent);
                    }
                    break;
                    case 2:
                    {
                        intent = new Intent(TypesActivity.this,PlacesList.class);
                        intent.putExtra("index","2");
                        startActivity(intent);
                    }
                    break;
                    case 3:
                    {
                        intent = new Intent(TypesActivity.this,PlacesList.class);
                        intent.putExtra("index","3");
                        startActivity(intent);
                    }
                    break;
                    case 4:
                    {
                        intent = new Intent(TypesActivity.this,PlacesList.class);
                        intent.putExtra("index","4");
                        startActivity(intent);
                    }
                    break;
                    case 5:
                    {
                        intent = new Intent(TypesActivity.this,PlacesList.class);
                        intent.putExtra("index","5");
                        startActivity(intent);
                    }
                    break;
                    case 6:
                    {
                        intent = new Intent(TypesActivity.this,PlacesList.class);
                        intent.putExtra("index","6");
                        startActivity(intent);
                    }
                    break;
                    case 7:
                    {
                        intent = new Intent(TypesActivity.this,PlacesList.class);
                        intent.putExtra("index","7");
                        startActivity(intent);
                    }
                    break;
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_types,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.typesAccount:
                startActivity(new Intent(TypesActivity.this,AccountActivity.class));
                break;
            case R.id.typesMenu:
                startActivity(new Intent(TypesActivity.this,SelectActivity.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
