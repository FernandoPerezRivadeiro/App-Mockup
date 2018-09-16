package com.example.restaurantfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.name;
import static android.R.attr.start;
import static java.lang.Double.parseDouble;

public class SelectActivity extends AppCompatActivity {
    private DBHelper myDb;
    private Button typeBtn;
    private Button priceBtn;
    private Button viewAllBtn;
    private TextView output;



    public static final String TAG = "RestaurantFinder";

    InputStream is;
    InputStreamReader isr;
    BufferedReader br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        //Check if the user is logged in , if not redirects to the login activity
        if(AccessToken.getCurrentAccessToken() == null)
        {
            goToLoginActivity();
        }

        //Setting up the logo in the action bar and the text corresponding to the activity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_restaurant);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Select Menu");



        myDb = new DBHelper(this); //creates the database

        /*Obtain the name and the id sent from the login activity using the shared preferences*/
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPref.getString("userName","nothing to show");
        String userId = sharedPref.getString("userId","not found");
        typeBtn = (Button)findViewById(R.id.typeBtn);
        priceBtn =(Button)findViewById(R.id.priceBtn);


        addUserData(userId,userName);
        writeToPlaces();

        goToTypesActivity();

        //Setting an onclick listener for the price button which will call the search by price Method
        priceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    searchByPrice();
            }
        });

        //Setting an onclick listener for the viewAllButton that creates an intent
        //which passes a number to identify the Activity making the call
        viewAllBtn = (Button)findViewById(R.id.viewAllBtn);
        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this,PlacesList.class);
                intent.putExtra("index","9");
                startActivity(intent);
            }
        });

    }

    //Instantiate a menu inflater to inflate the menu list on the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Determine which of the items was selected from the menu and the action that is to be taken
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.item1)
                startActivity(new Intent(SelectActivity.this,AccountActivity.class));

        return super.onOptionsItemSelected(item);
    }

    /**
     *Create a new intent that swaps to the ypes activity
     */
    public void goToTypesActivity(){
        typeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectActivity.this,TypesActivity.class));
            }
        });
    }

    /**
     * Take the values entered by the user, parse them to a double and validate data
     * Prompt the user with a message if the data entered was not valid input
     * An intent is created and the values are passed to PlaceList activity
     */
    public void searchByPrice()
    {
        EditText fromText = (EditText)findViewById(R.id.fromTv);
        EditText toText = (EditText)findViewById(R.id.toTv);
        String stringFrom = fromText.getText().toString();
        String stringTo = toText.getText().toString();

        try {
            double fromNumber = Double.parseDouble(stringFrom);
            double toNumber = Double.parseDouble(stringTo);

            //Checks if the number from the "fromTextView" are greater than the one in the "toTextView"
            //ALso checks if the number from the "toTextView" are less thant the one in the "fromTextView"
            //Prompts the user with a message
            if(fromNumber > toNumber)
            {
                Toast.makeText(this, "Please enter a valid price range ", Toast.LENGTH_SHORT).show();
            }

            //Checking if the user entered a valid number
            else if(fromNumber < 0 || toNumber <1)
            {
                Toast.makeText(this, "price can't be less than 1 or 0", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Creating and intent and bundle to send data to the PlacesList activity
                Intent intent = new Intent(SelectActivity.this,PlacesList.class);
                Bundle extras = new Bundle();
                extras.putDouble("fromValue",fromNumber);
                extras.putDouble("toValue",toNumber);
                intent.putExtras(extras);
                intent.putExtra("index","8"); //Number to identify the activity making the call
                fromText.setText("");
                toText.setText("");
                startActivity(intent);

            }
        }catch(Exception e)
        {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Add data to the users table
     * @param id The user Id obtained after the user logs into the app
     * @param name The user name obtained after the user logs into the app
     */
    public void addUserData(String id,String name){
        if(id == "" || name == "")
        {
            Toast.makeText(this, "unable to get data from the user", Toast.LENGTH_SHORT).show();
        }
        //Calling the method insertUsersData from the Data base helper to insert data into users table
        boolean inserted = myDb.insertUsersData(id,name);
        if(inserted == true)
        {
            Log.d(TAG,"User data inserted into users table");
        }

    }

    /**
     * Read the file text containing the information about the places and insert it into the
     * places table
     */
    public void addPlacesData()
    {
        //An inputStreamReader object is created which obtains the text file from the raw folder
        isr = new InputStreamReader(getResources().openRawResource(R.raw.restaurants));
        br = new BufferedReader(isr); //BufferedReader object that reads each line from the text file
        String line = "";
        String []tokens; //An array that contains the details about each line read from the text file
        int count =0; //Count the number of lines read by the stringBuffered (for testing purpose)
        boolean inserted ;
        try {
            while(br.readLine()!=null)
            {
                line = br.readLine();
                tokens = line.split(",");
                double price = parseDouble(tokens[3]);
                inserted = myDb.insertPlacesData(tokens[0],tokens[1],tokens[2],price,
                        tokens[4],tokens[5],tokens[6]);
                if(inserted == true) {
                    Log.d(TAG, "inserted row number: " + count);
                }
                count++;
            }
            isr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Read the data from the places table, if the table is empty insert the records
     * read from the text file making a call to the addPlacesData method
     */
    public void writeToPlaces(){
        Cursor res = myDb.readPlacesTable();
        if(res.getCount()==0) {
            addPlacesData();
           Log.d(TAG,"Data writen to table places succesfully");
        }else{
            Log.d(TAG,"the data is already loaded to the table, no more insertions are allowed");
        }
    }

    /**
     * Take the users back to the login activity if they haven't logged in
     */
    public void goToLoginActivity()
    {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

}
