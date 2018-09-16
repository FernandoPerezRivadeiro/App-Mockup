package com.example.restaurantfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class  LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    StringBuilder outputText = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        TextView title = (TextView)findViewById(R.id.login_tv);
        //Adds a new font to the text view from the assets
        Typeface myfont = Typeface.createFromAsset(getAssets(),"font/RemachineScript_Personal_Use.ttf");
        title.setTypeface(myfont);

        //Instantiating the login button object
        final LoginButton loginBtn = (LoginButton)findViewById(R.id.login_button);
        loginBtn.setReadPermissions("email", "public_profile");

        //An object of the callbackManager class that detects the actions or event of the login button
        callbackManager = CallbackManager.Factory.create();
        loginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //creates an intent that switches to the select activity if the login succeds
                goToSelectActivity();

                String userId = loginResult.getAccessToken().getUserId();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                displayUserInfo(object);

                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields","first_name,last_name,email,id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
                //prompts the user with a message if the login session is cancelled
                Toast.makeText(LoginActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });


    }

    /**
     * Display the user login information
     * @param object a JSONobject that contains the login details  of the user
     */
    public void displayUserInfo(JSONObject object){
        String first_name, last_name, email, id;
        first_name = "";
        last_name = "";
        email = "";
        id = "";

        try{
            first_name = object.getString("first_name");
            last_name = object.getString("last_name");
            email = object.getString("email");
            id = object.getString("id");

            storeInfoSharedPref(first_name,id);

        }catch(JSONException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Store shared preferences that can be accesed in any activity(user name and user id
     * @param output parameter that takes the user name
     * @param id parameter that takes user ID
     */
    public void storeInfoSharedPref(String output,String id){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //get the editor object
        SharedPreferences.Editor editor = sharedPref.edit();
        //remove already existing values
        editor.remove("userName");
        editor.remove("userId");

        //add new result string to shared preference
        editor.putString("userName",output);
        editor.putString("userId",id);
        //comit needs to be issued to save the preferences
        editor.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void goToSelectActivity()
    {
        Intent intent = new Intent(LoginActivity.this,SelectActivity.class);
        //Add flags to the intent to prevent the select activity from coming back to the login
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
