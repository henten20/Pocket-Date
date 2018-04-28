package com.example.pocketdate;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class MatchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Session Manager Class
    SessionManagement session;

    public static PendingIntent sender;
    private static final int FLIP_DURATION = 3000;
    private ViewFlipper viewFlipper;

    int userID;
    String userEmail;
    String profileLocation;
    String firstName;
    String lastName;
    String preference;
    String about;
    boolean inChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Session class instance
        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        // cancels current alarms that are set
        cancelCurrentAlarms();

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        this.firstName = user.get(SessionManagement.KEY_FIRST);
        this.lastName = user.get(SessionManagement.KEY_LAST);
        this.inChat = Boolean.parseBoolean(user.get(SessionManagement.KEY_CHATSTATUS));
        this.userID = Integer.parseInt(user.get(SessionManagement.KEY_USER));
        this.userEmail = user.get(SessionManagement.KEY_EMAIL);
        this.profileLocation = user.get(SessionManagement.KEY_PROFILE);
        this.preference = user.get(SessionManagement.KEY_PREFERENCE);
        this.about = user.get(SessionManagement.KEY_ABOUT);

        // grabs the navigation view
        NavigationView myNav = (NavigationView) findViewById(R.id.nav_view);

        // grabs the viewflipper view
        viewFlipper = (ViewFlipper)findViewById(R.id.image_view_flipper);
        // references the animations for when the slideshow flips between images
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        startSlideshow();

        // starts the asynchronous task of downloading the image
        View headerView = myNav.getHeaderView(0);

        // image is being updated
        ImageView img = (ImageView) headerView.findViewById(R.id.imageView);

        // only starts the timer if the user is logged in
        if(session.isLoggedIn() && getApplicationContext() != null && this.userID > -1 && this.profileLocation != null && this.firstName != null && this.preference != null && this.about != null)
        {
            new DownloadImageTask(img).execute(this.profileLocation);

            boolean alarmUp = (sender != null);

            // checks to see if an instance of our alarm manager is currently up.
            // if it isn't, then we should start the service that looks for new messages
            if (!alarmUp)
            {
                setOnetimeTimer(getApplicationContext());
            }

        }

        TextView nameText = (TextView) headerView.findViewById(R.id.nameField);
        nameText.setText(this.firstName + " " + this.lastName);

        TextView emailText = (TextView) headerView.findViewById(R.id.emailField);
        emailText.setText(this.userEmail);

        myNav.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button matchButton = (Button) findViewById(R.id.match_button);

        // if the user is currently in a convrsation, change the button from start matching to open convo
        if(this.inChat)
        {
            matchButton.setText("Chat");
        }

        // event listexner that will open the new activity
        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logic goes here that will pull down information from person they are in a chat with
                // create a connection to the php, download and pass them to the MessageList activity
                JSONArray resultJSON = getChatInfo();
                String matchProfileLocation;
                String matchFirstName;
                boolean justMatched = false;
                int creepLevel;

                if(!inChat)
                {
                    try {
                        JSONObject myObj = resultJSON.getJSONObject(0);
                        matchProfileLocation = myObj.getString("profileLocation");
                        matchFirstName = myObj.getString("firstName");
                        creepLevel = myObj.getInt("creepLevel");

                        // if the values returned by the json are empty and == -1, then no potential sutors are available
                        if(matchProfileLocation == null || matchFirstName == null || creepLevel == -1 || matchFirstName.isEmpty() || matchProfileLocation.isEmpty())
                        {
                            Toast.makeText(MatchActivity.this, "Unfortunately, there are no potential matches available right now. Please wait to be matched and try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            inChat = true;
                            openDialog(matchProfileLocation, matchFirstName);
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();

                        // in case the user loses their connection
                        if(e instanceof  NullPointerException)
                        {
                            Toast.makeText(MatchActivity.this, "Error Connecting to Internet. Check your connection settings.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                //otherwise,
                else
                {

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

                    // sends all of the parameters over that will be used to bring up the matchactivity again
                    myIntent.putExtra("inputEmail", MatchActivity.this.userEmail);
                    myIntent.putExtra("profileLocation", MatchActivity.this.profileLocation);
                    myIntent.putExtra("firstName", MatchActivity.this.firstName);
                    myIntent.putExtra("lastName", MatchActivity.this.lastName);
                    myIntent.putExtra("justMatched", justMatched);
                    myIntent.putExtra("inChat", MatchActivity.this.inChat);
                    myIntent.putExtra("userID", MatchActivity.this.userID);
                    startActivity(myIntent);
                }
            }
        });

    }

    public void setOnetimeTimer(Context context) {
        Log.v("code runs...", "hmmmm");
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MessageIntentService.class);
        intent.putExtra("userID", this.userID);
        sender = PendingIntent.getService(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 10), 10000, sender);
        Log.v("code finishes....", "also hmmmm");

    }

    // starts the slideshow
    private void startSlideshow(){
        if(!viewFlipper.isFlipping()){
            viewFlipper.setAutoStart(true);
            viewFlipper.setFlipInterval(FLIP_DURATION);
            viewFlipper.startFlipping();
        }
    }

    private void openDialog(String matchProfileLocation, String matchFirstName)
    {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(MatchActivity.this, R.style.CustomDialog);
        LayoutInflater factory = LayoutInflater.from(MatchActivity.this);
        final View view = factory.inflate(R.layout.matchpopup, null);
        ImageView myImage = (ImageView) view.findViewById(R.id.profileImageView);
        TextView matchText = (TextView) view.findViewById(R.id.matchText);
        matchText.setText("You've matched with " + matchFirstName + "!");
        new DownloadImageTask(myImage).execute(matchProfileLocation);

        session.handleMatch();

        Button chatButton = (Button)view.findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MatchActivity.this,
                        MessageListActivity.class);
                myIntent.putExtra("inputEmail", MatchActivity.this.userEmail);
                myIntent.putExtra("profileLocation", MatchActivity.this.profileLocation);
                myIntent.putExtra("firstName", MatchActivity.this.firstName);
                myIntent.putExtra("lastName", MatchActivity.this.lastName);
                myIntent.putExtra("justMatched", true);
                myIntent.putExtra("inChat", MatchActivity.this.inChat);
                myIntent.putExtra("jsonArray", "empty");
                myIntent.putExtra("userID", getUserID());
                startActivity(myIntent);
            }
        });

        alertadd.setView(view);
        AlertDialog alert = alertadd.create();

        alert.show();

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
        String resultString = messageConn.loadMessages(this.userID, this.userEmail, this.preference);
        Log.v("PHP RETURNED", resultString);

        // attempts to convert the JSONString into a JSONArray for simple data manipulation
        try
        {
            resultJSON = new JSONArray(resultString);
        }
        // handles the exception where the user loses connection to the internet
        catch(JSONException | NullPointerException e)
        {
            Log.v("Failed", e.toString());
            Log.v("TELL ME", "WTF");
            if(e instanceof NullPointerException)
            {
                Toast.makeText(MatchActivity.this, "Error Connecting to Internet. Check your connection settings.", Toast.LENGTH_SHORT).show();
            }
        }

        // returns the JSON array with all of our JSON Objects that contains the message details
        return resultJSON;
    }

    // if the back button is pressed and the drawer is open, close it
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // this is for the three dot settings option on the match page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void cancelCurrentAlarms()
    {
        // cancels the existing notification service when the user logs out
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent updateServiceIntent = new Intent(getApplicationContext(), MessageIntentService.class);
        //PendingIntent pendingUpdateIntent = PendingIntent.getService(getApplicationContext(), 0, updateServiceIntent, 0);

        // Cancel alarms
        try {
            alarmManager.cancel(sender);
            Log.v("AlarmManager", "CANCELED");
        } catch (Exception e) {
            Log.e("AlarmManager update", e.toString());
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // if the user decides that they want to log out
        if (id == R.id.nav_logout)
        {
            AlertDialog.Builder myNotice = new AlertDialog.Builder(MatchActivity.this);
            myNotice.setTitle("Are you sure that you want to logout?");
            myNotice.setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {

                    session.logoutUser();

                    cancelCurrentAlarms();

                    // process logout
                    Intent myIntent = new Intent(MatchActivity.this,
                            LoginActivity.class);
                    startActivity(myIntent);
                }
            });

            myNotice.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    // Do nothing but close the dialog
                }
            });

            // creates the dialog and displays it
            AlertDialog alert = myNotice.create();
            alert.show();

        }
        else if(id == R.id.nav_settings)
        {
            Intent myIntent = new Intent(MatchActivity.this,
                    SettingsActivity.class);
            startActivity(myIntent);
        }
        // if the profile option is selected, pass all of this data into new instance of ProfileActivity
        else if(id == R.id.nav_profile)
        {
            Intent myIntent = new Intent(MatchActivity.this, ProfileActivity.class);
            myIntent.putExtra("userID", getUserID());
            myIntent.putExtra("inputEmail", userEmail);
            myIntent.putExtra("profileLocation", profileLocation);
            myIntent.putExtra("firstName", firstName);
            myIntent.putExtra("lastName", lastName);
            myIntent.putExtra("inChat", inChat);
            myIntent.putExtra("preference", this.preference);
            myIntent.putExtra("about", this.about);
            startActivity(myIntent);
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
