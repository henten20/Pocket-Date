package com.example.pocketdate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stupidcupid.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;

public class MatchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    boolean isClicked = false;
    int userID;
    String userEmail;
    String profileLocation;
    String firstName;
    String lastName;
    boolean inChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // grabs the intent object that contains the bundled data being passed in
        Intent thisActivity = getIntent();

        // this is a bad way to do this (use bundles and checks instead of directly accessing data from intent)
        this.userID = thisActivity.getIntExtra("userID", -1);
        this.userEmail = thisActivity.getStringExtra("inputEmail");
        this.profileLocation = thisActivity.getStringExtra("profileLocation");
        this.firstName = thisActivity.getStringExtra("firstName");
        this.lastName = thisActivity.getStringExtra("lastName");
        this.inChat = thisActivity.getBooleanExtra("inChat", true);

        Log.v("Another Test", profileLocation);
        NavigationView myNav = (NavigationView) findViewById(R.id.nav_view);
        //Menu navMenu = myNav.getMenu();
        //MenuItem cameraItem = navMenu.findItem(R.id.nav_camera);
        //cameraItem.setTitle(this.profileLocation);
        // starts the asynchronous task of downloading the image
        View headerView = myNav.getHeaderView(0);

        // image is being updated
        ImageView img = (ImageView) headerView.findViewById(R.id.imageView);
        new DownloadImageTask(img).execute(this.profileLocation);

        TextView nameText = (TextView) headerView.findViewById(R.id.nameField);
        nameText.setText(this.firstName + " " + this.lastName);

        TextView emailText = (TextView) headerView.findViewById(R.id.emailField);
        emailText.setText(this.userEmail);

        myNav.setNavigationItemSelectedListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button matchButton = (Button) findViewById(R.id.match_button);

        // if the user is currently in a convrsation, change the button from start matching to open convo
        if(inChat)
        {
            matchButton.setText("Open Conversation");
        }

        // event listener that will open the new activity
        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logic goes here that will pull down information from person they are in a chat with
                // create a connection to the php, download and pass them to the MessageList activity
                JSONArray resultJSON = getChatInfo();

                Intent myIntent = new Intent(MatchActivity.this,
                        MessageListActivity.class);

                // checks to see if the jsonobject is null, which it will be if no messages have been sent
                if(resultJSON == null)
                {
                    myIntent.putExtra("jsonArray", "empty");
                }
                else
                {
                    // passes jsonarray as a string to the next activity
                    myIntent.putExtra("jsonArray", resultJSON.toString());
                }
                myIntent.putExtra("userID", getUserID());
                startActivity(myIntent);
            }
        });

    }

    // returns the user's id to the onclick event
    private int getUserID()
    {
        return this.userID;
    }
    // method that will obtain the current chat information for a user when open the messages screen
    private JSONArray getChatInfo()
    {
        JSONArray resultJSON = null;

        // creates a ServerConnectionObject that handles establishing a connection with the remote server
        ServerConnection messageConn = new ServerConnection("http://cop4331groupeight.com/chatapi.php");
        String resultString = messageConn.loadMessages(this.userID, this.userEmail);

        // attempts to convert the JSONString into a JSONArray for simple data manipulation
        try
        {
            resultJSON = new JSONArray(resultString);
        }
        catch(JSONException e)
        {
            Log.v("Failed", e.toString());
        }

        // returns the JSON array with all of our JSON Objects that contains the message details
        return resultJSON;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.match, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // asynchronous task that handles setting the user's profile image in the navigation bar
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}