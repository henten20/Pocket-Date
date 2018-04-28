package com.example.pocketdate;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageListActivity extends AppCompatActivity {

    //Declare the Adapter, RecyclerView and our custom ArrayList
    RecyclerView recyclerView;
    CustomAdapter adapter;
    private ArrayList<CustomPojo> listContentArr = new ArrayList<>();
    private int matchCreepLevel;
    private int matchID;

    private int userID;
    String userEmail;
    String profileLocation;
    String firstName;
    String lastName;
    boolean inChat;

    private boolean justMatched;
    private String matchFirstName;
    private String matchLastName;
    private String matchProfileLocation;
    private String matchAbout;
    private String matchGender;
    private String matchBirthdate;

    // Session Manager Class
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        recyclerView=(RecyclerView)findViewById(R.id.recycleView);
        //As explained in the tutorial, LineatLayoutManager tells the RecyclerView that the view
        //must be arranged in linear fashion
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter=new CustomAdapter(this);

        // Session class instance
        session = new SessionManagement(getApplicationContext());

        // grabs the intent object that contains the bundled data being passed in
        Intent thisActivity = getIntent();
        String jsonArray = thisActivity.getStringExtra("jsonArray");
        this.userID = thisActivity.getIntExtra("userID", -1);
        //this.matchID = thisActivity.getIntExtra("matchID", -1);
        this.justMatched = thisActivity.getBooleanExtra("justMatched", true);
        // user is now in a chat if we reach this activity
        this.inChat = true;
        this.userEmail = thisActivity.getStringExtra("inputEmail");
        this.profileLocation = thisActivity.getStringExtra("profileLocation");
        this.firstName = thisActivity.getStringExtra("firstName");
        this.lastName = thisActivity.getStringExtra("lastName");

        // grabs the profile information of the user's match
        grabProfile();

        // sets the match's info in the custome adapter that populates the recyclerview
        adapter.setProfileLocation(this.matchProfileLocation);
        adapter.setMatchFirstName(this.matchFirstName);
        adapter.setMatchLastName(this.matchLastName);

        JSONArray messageArray = null;

        try {
            messageArray = new JSONArray(jsonArray);
            Log.v("Array passed correctly", messageArray.toString(2));
        } catch (JSONException e) {
            Log.v("Error passing jsonarray", e.toString());
        }
        //Method call for populating the view
        populateRecyclerViewValues(messageArray);

        Button sendButton = (Button) findViewById(R.id.button_chatbox_send);

        // pushes the user's message to the screen via recyclerview
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textEntry = (EditText) findViewById(R.id.edittext_chatbox);
                String messageContents = textEntry.getText().toString();
                // had to jump through some hoops to get the DateTime string to match mySQL's version
                java.util.Date date = new java.util.Date();
                java.util.Date stamp = new java.sql.Timestamp(date.getTime());
                int lastIndxDot = stamp.toString().lastIndexOf('.');
                String timeString = stamp.toString().substring(0, lastIndxDot);

                pushMessage(messageContents, timeString);

                // cool, message is created, but it needs to be pushed to the server.
                CustomPojo newMessage = new CustomPojo();
                newMessage.setName("Sender");
                newMessage.setContent(messageContents);
                newMessage.setTime(timeString);
                newMessage.setType(1);

                textEntry.setText("");
                listContentArr.add(newMessage);
                adapter.setListContent(listContentArr);
                recyclerView.setAdapter(adapter);
            }
        });

        getSupportActionBar().setTitle("Chat with " + this.matchFirstName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.match, menu);
        // changes the title of the message activity
        setTitle("Chat with " + this.matchFirstName);
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
            openDialog();
            return true;
        }
        else if(id == android.R.id.home)
        {
            // if the user's just matched, then we need to skip over the match pop-up when the back button is pressed
            if(justMatched)
            {
                Intent myIntent = new Intent(this, MatchActivity.class);
                myIntent.putExtra("userID", this.userID);
                myIntent.putExtra("inputEmail", this.userEmail);
                myIntent.putExtra("profileLocation", this.profileLocation);
                myIntent.putExtra("firstName", this.firstName);
                myIntent.putExtra("lastName", this.lastName);
                myIntent.putExtra("inChat", this.inChat);
                // starts match activity with fresh info
                startActivity(myIntent);
            }
            else
            {
                onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // opens the dialog for viewing match options
    private void openDialog()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Match Options");

        // add a list
        String[] animals = {"View Match Profile", "View Creep Level", "Unmatch", "Report"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // view match profile
                        viewMatchProfile();
                        break;
                    case 1:
                        viewCreepLevel();
                        break;
                    case 2: // unmatch
                        confirmUnmatch();
                        break;
                    case 3: // report
                        processReport();
                        break;
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void viewCreepLevel()
    {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(MessageListActivity.this, R.style.CustomDialog);
        LayoutInflater factory = getLayoutInflater();
        final View view = factory.inflate(R.layout.creeplayout, null);
        ImageView creepView = (ImageView) view.findViewById(R.id.creepView);
        TextView creepTextView = (TextView) view.findViewById(R.id.creepText);

        alertadd.setView(view);
        final AlertDialog alert = alertadd.create();

        // switch statement that will set the appropriate textview/imageview depending on the creep level of the match
        switch(this.matchCreepLevel)
        {
            case 0:
                creepView.setImageResource(R.drawable.progress0);
                creepTextView.setText("Not a creep!");
                break;
            case 1:
                creepView.setImageResource(R.drawable.progress1);
                creepTextView.setText("Might be a creep.");
                break;
            case 2:
                creepView.setImageResource(R.drawable.progress2);
                creepTextView.setText("Creepy");
                break;
            case 3:
                creepView.setImageResource(R.drawable.progress3);
                creepTextView.setText("Creepier than average");
                break;
            case 4:
                creepView.setImageResource(R.drawable.progress4);
                creepTextView.setText("Very creepy. Be careful.");
                break;
            default:
                creepView.setImageResource(R.drawable.progress4);
                creepTextView.setText("Very creepy. Be careful.");
                break;
        }


        alert.show();

        Button closeButton = (Button)view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    private void viewMatchProfile()
    {
        Intent myIntent = new Intent(MessageListActivity.this,
                MatchProfileActivity.class);
        myIntent.putExtra("creepLevel", Integer.toString(this.matchCreepLevel));
        myIntent.putExtra("profileLocation", this.matchProfileLocation);
        myIntent.putExtra("matchFirstName", this.matchFirstName);
        myIntent.putExtra("matchLastName", this.matchLastName);
        myIntent.putExtra("matchAbout", this.matchAbout);
        myIntent.putExtra("matchGender", this.matchGender);
        myIntent.putExtra("matchBirthdate", this.matchBirthdate);
        startActivity(myIntent);
    }

    private void processReport()
    {
        // placeholder for the reporting functionality
        Intent myIntent = new Intent(this, ReportActivity.class);
        myIntent.putExtra("userID", userID);
        myIntent.putExtra("inputEmail", userEmail);
        myIntent.putExtra("profileLocation", profileLocation);
        myIntent.putExtra("firstName", firstName);
        myIntent.putExtra("lastName", lastName);
        myIntent.putExtra("inChat", inChat);
        myIntent.putExtra("matchID", matchID);
        myIntent.putExtra("matchFirstName", matchFirstName);
        myIntent.putExtra("matchID", matchID);
        startActivity(myIntent);
    }
    // will be interesting to see if this works well
    private void processUnmatch()
    {
        ServerConnection unMatchConn = new ServerConnection("http://cop4331groupeight.com/chatapi.php");
        Log.v("user's id", Integer.toString(this.userID));
        String resultString = unMatchConn.unmatchPerson(this.userID);
        Log.v("Returned JSON", resultString);

        JSONArray resultJSON = null;
        JSONObject jsonObj = null;

        try {

            // modifies the shared preference variables to match the new chat status of the user
            session.handleUnmatch();

            resultJSON = new JSONArray(resultString);
            jsonObj = resultJSON.getJSONObject(0);
            int userID = jsonObj.getInt("userID");
            String userEmail = jsonObj.getString("email");
            String profileLocation = jsonObj.getString("profileLocation");
            String firstName = jsonObj.getString("firstName");
            String lastName = jsonObj.getString("lastName");
            boolean inChat = jsonObj.getBoolean("inChat");

            Log.v("Profile", profileLocation);
            // login attempt was successful and we should proceed to the next activity
                // Start NewActivity.class
            Intent myIntent = new Intent(MessageListActivity.this,
                    MatchActivity.class);
            myIntent.putExtra("userID", userID);
            myIntent.putExtra("inputEmail", userEmail);
            myIntent.putExtra("profileLocation", profileLocation);
            myIntent.putExtra("firstName", firstName);
            myIntent.putExtra("lastName", lastName);
            myIntent.putExtra("inChat", inChat);
            // creates a little text bubble indicating that the login was successful
            Toast.makeText(MessageListActivity.this, "Successfully Unmatched!", Toast.LENGTH_LONG).show();
            startActivity(myIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //go through the process of switching back to the main activity page..
    }
    // additional dialog box to ensure that the user wants to unmatch with the current person
    private void confirmUnmatch()
    {
        // setup the alert builder
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("Are you sure you want to unmatch with " + this.matchFirstName + "?");

        confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // Do nothing but close the dialog
                Log.v("Unmatch chosen", "User chose to unmatch with the person");
                processUnmatch();
            }
        });

        confirmBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Log.v("Cancel Chosen", "User chose to stay with match");
            }
        });

        AlertDialog myDialog = confirmBuilder.create();
        myDialog.show();

    }

    private void grabProfile()
    {
        ServerConnection profileConn = new ServerConnection("http://cop4331groupeight.com/chatapi.php");
        String resultString = profileConn.getProfileInfo(this.userID);
        Log.v("MatchProfile", resultString);
        JSONArray resultJSON = null;
        try
        {
            resultJSON = new JSONArray(resultString);
            JSONObject jsonObj = resultJSON.getJSONObject(0);
            Log.v("test", resultString);
            this.matchFirstName = jsonObj.getString("firstName");
            this.matchLastName = jsonObj.getString("lastName");
            this.matchProfileLocation = jsonObj.getString("profileLocation");
            this.matchCreepLevel = Integer.parseInt(jsonObj.getString("creepLevel"));
            this.matchID = Integer.parseInt(jsonObj.getString("matchID"));
            this.matchAbout = jsonObj.getString("about");
            this.matchGender = jsonObj.getString("gender");
            this.matchBirthdate = jsonObj.getString("birthdate");

            Log.v("test", this.matchAbout);
            Log.v("test", this.matchGender);
            Log.v("test", this.matchBirthdate);

        } catch (JSONException | NullPointerException e)
        {
            e.printStackTrace();

            // in case the user loses connection
            if(e instanceof NullPointerException)
            {
                Toast.makeText(MessageListActivity.this, "Error Connecting to Internet. Check your connection settings.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // method that will push a message up to our remote server
    private void pushMessage(String contents, String timeString)
    {
        ServerConnection pushConn = new ServerConnection("http://cop4331groupeight.com/chatapi.php");
        String resultString = pushConn.sendMessage(contents, this.userID, timeString);
    }
    // this is where the recyclerview is populated with all of the message data
    private void populateRecyclerViewValues(JSONArray messageArray)
    {
        JSONObject currentMessage = null;

        if(messageArray != null)
        {
            int arrayLength = messageArray.length();

            for(int iter=0;iter<arrayLength;iter++)
            {
                //Creating POJO class object
                CustomPojo pojoObject = new CustomPojo();

                // sets a default value just in case
                int senderID = -1;

                // tries to grab the current JSONObject from the array - throws an exception if invalid
                try
                {
                    currentMessage = messageArray.getJSONObject(iter);

                    //Values are binded using set method of the POJO class
                    pojoObject.setName("OtherDude");
                    pojoObject.setContent(currentMessage.getString("messageContents"));
                    pojoObject.setTime(currentMessage.getString("timeStamp"));
                    senderID = Integer.parseInt(currentMessage.getString("senderID"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // sets the xml depending on who sent the message
                Log.v("ids", Integer.toString(senderID) + " " + Integer.toString(this.userID));

                // checks to make sure a valid message was sent
                if(senderID != -1)
                {
                    if(senderID == this.userID)
                        pojoObject.setType(1);
                    else
                        pojoObject.setType(2);

                    //After setting the values, we add all the Objects to the array
                    //Hence, listConentArr is a collection of Array of POJO objects
                    listContentArr.add(pojoObject);
                }
            }

            //We set the array to the adapter if we have received a message
            adapter.setListContent(listContentArr);

            //We in turn set the adapter to the RecyclerView
            recyclerView.setAdapter(adapter);
        }
    }
}