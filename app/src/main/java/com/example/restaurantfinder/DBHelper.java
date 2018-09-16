package com.example.restaurantfinder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;
import android.widget.Toast;

import static android.R.attr.id;
import static android.R.attr.name;
import static android.R.attr.type;
import static android.R.attr.version;
import static android.os.Build.ID;
import static android.provider.Contacts.SettingsColumns.KEY;
import static com.example.restaurantfinder.R.drawable.cafe;
import static com.example.restaurantfinder.R.drawable.italian;
import static com.example.restaurantfinder.R.drawable.japanese;
import static com.example.restaurantfinder.R.drawable.mexican;
import static com.example.restaurantfinder.R.drawable.spanish;

/**
 * Created by YD on 2017-11-19.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";
    public static final String DATABASE_NAME = "restaurant.db";
    private static final int DB_VERSION = 1;
    public static final String COL_NAME = "user_name";
    public static final String COL_ID = "user_id";
    public static final String COL_PLACE_ID = "place_id";
    public static final String COL_PLACE_NAME = "place_name";
    public static final String COL_PLACE_ADDRESS = "place_address";
    public static final String COL_PLACE_OPEN_HOURS = "place_open_hours";
    public static final String COL_PLACE_CLOSE_HOURS = "place_close_hours";
    public static final String COL_PLACE_PRICE = "place_price";
    public static final String COL_PLACE_TYPE = "place_TYPE";
    public static final String COL_FAV_USER_ID = "favUserID";
    public static final String COL_FAV_PLACE_ID = "favPlaceID";



    String usersTable = "users";
    String placesTable = "places";
    String favoritesTable = "favorites";

    Context context;


    public DBHelper(Context context) {


        super(context,DATABASE_NAME, null, DB_VERSION);
        Log.d(TAG,"DBHelper constructor called");
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            //Creating users table
            String createUserTable = "CREATE TABLE IF NOT EXISTS " + usersTable + "("+COL_ID+ " TEXT PRIMARY KEY, "
                    + COL_NAME+" TEXT NOT NULL);";

            //creating places table
            String createPlacesTable = "CREATE TABLE IF NOT EXISTS " + placesTable +
                    "("+COL_PLACE_ID+" TEXT PRIMARY KEY, "
                    + COL_PLACE_NAME+" TEXT NOT NULL, "+COL_PLACE_ADDRESS+" TEXT NOT NULL, "
                    +COL_PLACE_PRICE+" REAL NOT NULL, "+COL_PLACE_OPEN_HOURS+" TEXT NOT NULL, "
                    +COL_PLACE_CLOSE_HOURS+" TEXT NOT NULL, "+COL_PLACE_TYPE+" TEXT NOT NULL);";

            //creating favorites table
            String createFavoritesTable = "CREATE TABLE IF NOT EXISTS " + favoritesTable +
                    "("+COL_FAV_USER_ID+ " TEXT NOT NULL, "
                    + COL_FAV_PLACE_ID+" TEXT NOT NULL, FOREIGN KEY ("+COL_FAV_USER_ID
                    +") REFERENCES "+usersTable+"("+COL_ID+"), FOREIGN KEY ("+COL_FAV_PLACE_ID
                    +") REFERENCES "+placesTable+"("+COL_PLACE_ID+"));";


            sqLiteDatabase.execSQL(createUserTable);
            sqLiteDatabase.execSQL(createFavoritesTable);
            sqLiteDatabase.execSQL(createPlacesTable);
            Log.d(TAG, "onCreate: tables created");
            Toast.makeText(context, "Tables created", Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            e.getStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + usersTable+";");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + favoritesTable+";");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + placesTable+";");

            onCreate(sqLiteDatabase);

            Log.d(TAG, "onUpgrade: called");
        }catch(Exception e)
        {
            e.getStackTrace();
        }
    }

    /**
     * Insert places records into the places table
     * @param id The id of the place
     * @param name The name of the place
     * @param address The address of the palce
     * @param price The average price of the place
     * @param openHours The opening time of the restaurant
     * @param closeHours The closing time of the restaurant
     * @param type  The type of restaurant
     * @return Return true if the data was successfully inserted, if not return false
     */
    public boolean insertPlacesData(String id,String name,String address,double price,
                                    String openHours,String closeHours,String type)
    {
        Log.d(TAG,"insertData: Data inserted into places table");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PLACE_ID,id);
        contentValues.put(COL_PLACE_NAME,name);
        contentValues.put(COL_PLACE_ADDRESS,address);
        contentValues.put(COL_PLACE_PRICE,price);
        contentValues.put(COL_PLACE_OPEN_HOURS,openHours);
        contentValues.put(COL_PLACE_CLOSE_HOURS,closeHours);
        contentValues.put(COL_PLACE_TYPE,type);

        long result = db.insert(placesTable,null,contentValues);
        if(result == -1) {return false;}else{return true;}

    }

    /**
     * Insert data into the users table
     * @param id the user id
     * @param name the name of the user
     * @return return a flag if the data was inserted or not
     */
    public boolean insertUsersData(String id,String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long result;

        Cursor cursor = db.rawQuery("SELECT user_id FROM users;",null);
        if(cursor.getCount() ==0) {
            contentValues.put(COL_ID, id);
            contentValues.put(COL_NAME, name);

            result = db.insert(usersTable, null, contentValues);
            Log.d(TAG, "insertUsersData: Data inserted into users table");
            if(result != -1) {return true;}
        }else{
            cursor.moveToFirst();
            while (cursor.moveToNext())
            {
                if(cursor.getString(0) == id)
                {
                    break;
                }else{
                    contentValues.put(COL_ID, id);
                    contentValues.put(COL_NAME, name);

                    result = db.insert(usersTable, null, contentValues);
                    Log.d(TAG, "insertUsersData: Data inserted into users table");
                    if(result != -1) {return true;}
                }
            }
        }

        return false;

    }

    /**
     * Read the table places using a raw query
     * @return return a cursor that allows to red the table
     */
    public Cursor readPlacesTable()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + placesTable, null);
        Log.d(TAG,"getAllData: Data selected from places table");

        return res;
    }

    /**
     * Read the places table using a query that searchs by place id
     * @param p_id the place id
     * @return return a cursor that allows to red the table
     */
    public Cursor getPlaceDetails(String p_id){
        String query = "SELECT * FROM "+placesTable+ " WHERE "+COL_PLACE_ID+" = '"+p_id+"';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);

        return c;

    }

    //Method called on the placesList activity
    // read the places table and return the records that match what the selected establishment
    public Cursor getBurguerShops()
    {
        String burguerQuery= "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_TYPE+" = 'Burger shop' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(burguerQuery,null);
        return cr;
    }
    //method to get the cafes stored in the db (called on the placesList activity)
    public Cursor getCafe()
    {
        String cafeQuery= "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_TYPE+" = 'Cafe' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(cafeQuery,null);
        return cr;
    }
    //method to get the indian restaurants stored in the db (called on the placesList activity)
    public Cursor getIndian()
    {
        String indianQuery= "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_TYPE+" = 'Indian' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(indianQuery,null);
        return cr;
    }

    //method to get the italian restaurants stored in the db (called on the placesList activity)
    public Cursor getItalian()
    {
        String italianQuery= "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_TYPE+" = 'Italian' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(italianQuery,null);
        return cr;
    }
    //method to get the Japanese restaurants stored in the db (called on the placesList activity)
    public Cursor getJapanese()
    {
        String japaneseQuery= "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_TYPE+" = 'Japanese' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(japaneseQuery,null);
        return cr;
    }
    //method to get the mexican restaurants stored in the db (called on the placesList activity)
    public Cursor getMexican()
    {
        String mexicanQuery= "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_TYPE+" = 'Mexican' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(mexicanQuery,null);
        return cr;
    }
    //method to get the Spanish restaurants stored in the db (called on the placesList activity)
    public Cursor getSpanish()
    {
        String spanishQuery= "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_TYPE+" = 'Spanish' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(spanishQuery,null);
        return cr;
    }
    //method to get the Vegetarian restaurants stored in the db (called on the placesList activity)
    public Cursor getVegetarian()
    {
        String vegQuery= "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_TYPE+" = 'Vegetarian' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(vegQuery,null);
        return cr;
    }

    /**
     * read the table places and search according to price range
     * @param minPrice minimum price
     * @param maxPrice max price
     * @return price within range
     */
    public Cursor getPriceRange(double minPrice,double maxPrice)
    {
        String priceQuery = "SELECT * FROM "+placesTable+" WHERE "+COL_PLACE_PRICE+
                " BETWEEN "+minPrice+" AND "+maxPrice+" ORDER BY "+COL_PLACE_PRICE+" ;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(priceQuery,null);
        return cursor;

    }


    //count rows in table favorites
    public int countRows ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT "+COL_FAV_USER_ID+" FROM "+favoritesTable,null).getCount();
    }



    //check if favorites table is empty or not (called on PlaceDetails and Account Activity
    public boolean checkFavoritesTable()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + favoritesTable, null);
        Log.d(TAG,"getAllData: Data selected from favorites table");

        if(cursor.getCount() ==0)
        {
            return false;
        }else
        {
            return true;
        }
    }

    //check if there's an existing place in favorites table
    public Cursor checkExistingPlaceInTable(String placeID, String userId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+favoritesTable+";",null);
        return cursor;
    }

    //insert data into favorite table
    public boolean insertIntoFavoritesTable(String placeId, String userId)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_FAV_USER_ID,userId);
        cv.put(COL_FAV_PLACE_ID,placeId);

        long result = db.insert(favoritesTable,null,cv);
        if(result == -1) {return false;}else{return true;}


    }

    //delete data from fav table
    public int deleteDataFromFavoritesTable (String placeId,String userId)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(favoritesTable,"favUserID = ? AND favPlaceID = ?",new String[]{userId,placeId});

    }

    //read from both favorite and places table
    public Cursor readFavoritesAndPlacesTable(String userId)
    {
        String query = "SELECT * FROM "+placesTable+ " INNER JOIN " +favoritesTable+
                " ON places.place_id = favorites.favPlaceID  AND " +
                "favorites.favUserID = '"+userId+"';";;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }


}
