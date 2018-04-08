package com.example.pocketdate;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private int userID;

    private String matchFirstName;
    private String matchLastName;
    private String matchProfileLocation;

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

        // grabs the intent object that contains the bundled data being passed in
        Intent thisActivity = getIntent();
        String jsonArray = thisActivity.getStringExtra("jsonArray");
        this.userID = thisActivity.getIntExtra("userID", -1);

        // grabs the profile information of the user's match
        grabProfile();

        Log.v("Match's creep level", Integer.toString(this.matchCreepLevel));
        // changes the title of the toolbar to be a little more friendly
        //setTitle("Conversation with " + this.matchFirstName);

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
                pushMessage(messageContents);
                // cool, message is created, but it needs to be pushed to the server.
                CustomPojo newMessage = new CustomPojo();
                newMessage.setName("Sender");
                newMessage.setContent(messageContents);
                newMessage.setTime("6:22");
                newMessage.setType(1);

                textEntry.setText("");
                listContentArr.add(newMessage);
                adapter.setListContent(listContentArr);
                recyclerView.setAdapter(adapter);
            }
        });
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

        return super.onOptionsItemSelected(item);
    }

    // opens the dialog for viewing match options
    private void openDialog()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Match Options");

        // add a list
        String[] animals = {"View Match Profile", "Unmatch", "Report"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // view match profile
                        viewMatchProfile();
                        break;
                    case 1: // unmatch
                        confirmUnmatch();
                        break;
                    case 2: // report
                        processReport();
                        break;
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void viewMatchProfile()
    {
        Intent myIntent = new Intent(MessageListActivity.this,
                MatchProfileActivity.class);
        myIntent.putExtra("creepLevel", Integer.toString(this.matchCreepLevel));
        myIntent.putExtra("profileLocation", this.matchProfileLocation);
        myIntent.putExtra("matchFirstName", this.matchFirstName);
        myIntent.putExtra("matchLastName", this.matchLastName);
        startActivity(myIntent);
    }

    private void processReport()
    {

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
            this.matchFirstName = jsonObj.getString("firstName");
            this.matchLastName = jsonObj.getString("lastName");
            this.matchProfileLocation = jsonObj.getString("profileLocation");
            this.matchCreepLevel = jsonObj.getInt("creepLevel");

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
    private void pushMessage(String contents)
    {
        ServerConnection pushConn = new ServerConnection("http://cop4331groupeight.com/chatapi.php");
        String resultString = pushConn.sendMessage(contents, this.userID);
    }
    private void populateRecyclerViewValues(JSONArray messageArray)
    {
        /** This is where we pass the data to the adpater using POJO class.
         *  The for loop here is optional. I've just populated same data for 50 times.
         *  You can use a JSON object request to gather the required values and populate in the
         *  RecyclerView.
         * */
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
                if(senderID == this.userID)
                    pojoObject.setType(1);
                else
                    pojoObject.setType(2);

                //After setting the values, we add all the Objects to the array
                //Hence, listConentArr is a collection of Array of POJO objects
                listContentArr.add(pojoObject);
            }
            //We set the array to the adapter
            adapter.setListContent(listContentArr);
            //We in turn set the adapter to the RecyclerView
            recyclerView.setAdapter(adapter);
        }
    }
}