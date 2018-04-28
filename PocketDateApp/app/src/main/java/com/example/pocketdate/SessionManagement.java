package com.example.pocketdate;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MyPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_FIRST = "firstName";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "userEmail";

    public static final String KEY_LAST = "lastName";

    public static final String KEY_CHATSTATUS = "inChat";

    public static final String KEY_PASS = "pass";

    public static final String KEY_USER = "userID";

    public static final String KEY_PROFILE = "profileLocation";

    public static final String KEY_PREFERENCE = "preference";

    public static final String KEY_ABOUT = "about";

    // Constructor
    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(int userID, String userEmail, String pass, String profileLocation, String firstName, String lastName, boolean inChat, boolean isLoggedIn, String preference, String about){
        // Storing login value as TRUE

        // adds fields to the editor
        editor.putString(KEY_PASS, pass);
        editor.putBoolean(KEY_CHATSTATUS, inChat);
        editor.putString(KEY_EMAIL, userEmail);
        editor.putString(KEY_PROFILE, profileLocation);
        editor.putString(KEY_FIRST, firstName);
        editor.putString(KEY_LAST, lastName);
        editor.putInt(KEY_USER, userID);
        editor.putBoolean(IS_LOGIN, isLoggedIn);
        editor.putString(KEY_PREFERENCE, preference);
        editor.putString(KEY_ABOUT, about);

        // commit changes
        editor.commit();
    }

    public void updateInfo(String about, String preference)
    {
        editor.putString(KEY_ABOUT, about);
        editor.putString(KEY_PREFERENCE, preference);
        editor.commit();
    }

    public void handleUnmatch()
    {
        editor.putBoolean(KEY_CHATSTATUS, false);
        editor.commit();
    }

    public void handleMatch()
    {
        editor.putBoolean(KEY_CHATSTATUS, true);
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_FIRST, pref.getString(KEY_FIRST, null));
        user.put(KEY_LAST, pref.getString(KEY_LAST, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PROFILE, pref.getString(KEY_PROFILE, null));
        user.put(KEY_CHATSTATUS, Boolean.toString(pref.getBoolean(KEY_CHATSTATUS, false)));
        user.put(KEY_USER, Integer.toString(pref.getInt(KEY_USER, -1)));
        user.put(KEY_PREFERENCE, pref.getString(KEY_PREFERENCE, null));
        user.put(KEY_ABOUT, pref.getString(KEY_ABOUT, null));
        // user email id

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }


}
