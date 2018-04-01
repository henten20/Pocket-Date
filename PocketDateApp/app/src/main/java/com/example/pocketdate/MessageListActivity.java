package com.example.pocketdate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.stupidcupid.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageListActivity extends AppCompatActivity {

    //Declare the Adapter, RecyclerView and our custom ArrayList
    RecyclerView recyclerView;
    CustomAdapter adapter;
    private ArrayList<CustomPojo> listContentArr = new ArrayList<>();
    int userID;

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

        } catch (JSONException e)
        {
            e.printStackTrace();
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