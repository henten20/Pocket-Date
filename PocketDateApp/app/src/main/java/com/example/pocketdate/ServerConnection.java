package com.example.pocketdate;

import android.net.Uri;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Austin on 3/31/2018.
 */

public class ServerConnection {

    // url that the connection will be accessing
    private HttpURLConnection conn;
    private URL url;
    private String query;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    public ServerConnection(String url)
    {
        // attempts to create a connection
        try
        {
            // Enter URL address where your php file resides
            this.url = new URL(url);

        } catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // private method to prevent premature access - if the query isn't set before this method is called, the app will crash
    private String initiateConnection()
    {
        // attempts to establish a connection with the remote server
        try {

            // permits all network connections through
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Open connection for sending data
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(this.query);
            writer.flush();
            writer.close();
            os.close();

            // establishes the connection to the webserver
            conn.connect();

            // grabs the POST response back from the webserver
            int response_code = conn.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK)
            {
                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                // adds the result to the stringbuilder object
                while ((line = reader.readLine()) != null)
                {
                    result.append(line);
                }

                String resultString = result.toString();

                // must close here as well as outside the conditional
                conn.disconnect();

                return resultString;
            }

            // closes the connection to the remote server.
            conn.disconnect();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    // method that will establish the connection for an initial login attempt
    public String initialLogin(String email, String password)
    {
        // Append parameters to URL - we can pass our parameters to the php code here
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("email", email)
                .appendQueryParameter("password", password);
        // constructs the query that will be posted to the webserver
        String query = builder.build().getEncodedQuery();

        // sets the object's query with the appropriate parameters
        this.query = query;

        return initiateConnection();
    }

    // method that handles setting the parameters for a connection to the remote server that will load all of the messages
    public String loadMessages(int userID, String userEmail)
    {
        // Append parameters to URL - we can pass our parameters to the php code here
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("action", "load")
                .appendQueryParameter("userID", Integer.toString(userID))
                .appendQueryParameter("userEmail", userEmail);

        // constructs the query that will be posted to the webserver
        String query = builder.build().getEncodedQuery();

        // sets object's query with the correct parameters for loading messages
        this.query = query;

        // returns the string that is obtained from the "chatapi.php" file
        return initiateConnection();
    }

    // returns our match's profile information
    public String getProfileInfo(int userID)
    {
        // Append parameters to URL - we can pass our parameters to the php code here
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("action", "profile")
                .appendQueryParameter("userID", Integer.toString(userID));

        // constructs the query that will be posted to the webserver
        String query = builder.build().getEncodedQuery();

        // sets object's query with the correct parameters for loading messages
        this.query = query;

        // returns the string that is obtained from the "chatapi.php" file
        return initiateConnection();
    }

    public String sendMessage(String contents, int userID)
    {
        // Append parameters to URL - we can pass our parameters to the php code here
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("action", "send")
                .appendQueryParameter("userID", Integer.toString(userID))
                .appendQueryParameter("messageContents", contents);

        // constructs the query that will be posted to the webserver
        String query = builder.build().getEncodedQuery();

        // sets object's query with the correct parameters for loading messages
        this.query = query;

        // returns the string that is obtained from the "chatapi.php" file
        return initiateConnection();
    }

    public String unmatchPerson(int userID)
    {
        // Append parameters to URL - we can pass our parameters to the php code here
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("action", "unmatch")
                .appendQueryParameter("userID", Integer.toString(userID));

        // constructs the query that will be posted to the webserver
        String query = builder.build().getEncodedQuery();

        // sets object's query with the correct parameters for loading messages
        this.query = query;

        // returns the string that is obtained from the "chatapi.php" file
        return initiateConnection();
    }

    public String updateProfilePic(String encodedString, int userID)
    {
        // Append parameters to URL - we can pass our parameters to the php code here
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("action", "updateProfilePic")
                .appendQueryParameter("userID", Integer.toString(userID))
                .appendQueryParameter("encodedImage", encodedString);

        // constructs the query that will be posted to the webserver
        String query = builder.build().getEncodedQuery();

        // sets object's query with the correct parameters for loading messages
        this.query = query;

        // returns the string that is obtained from the "chatapi.php" file
        return initiateConnection();
    }
}
