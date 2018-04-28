package com.example.pocketdate;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportActivity extends AppCompatActivity {

    private int matchID;
    private boolean justMatched;
    private int userID;
    String userEmail;
    String profileLocation;
    String firstName;
    String lastName;
    boolean inChat;
    private String matchFirstName;

    // Session Manager Class
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Session class instance
        session = new SessionManagement(getApplicationContext());

        // grabs the intent object that contains the bundled data being passed in
        Intent thisActivity = getIntent();
        String jsonArray = thisActivity.getStringExtra("jsonArray");
        this.userID = thisActivity.getIntExtra("userID", -1);
        this.matchID = thisActivity.getIntExtra("matchID", -1);
        this.justMatched = thisActivity.getBooleanExtra("justMatched", false);
        // user is now in a chat if we reach this activity
        this.inChat = true;
        this.userEmail = thisActivity.getStringExtra("inputEmail");
        this.profileLocation = thisActivity.getStringExtra("profileLocation");
        this.firstName = thisActivity.getStringExtra("firstName");
        this.lastName = thisActivity.getStringExtra("lastName");
        this.matchFirstName = thisActivity.getStringExtra("matchFirstName");

        final Spinner staticSpinner = (Spinner) findViewById(R.id.static_spinner);

        final EditText aboutMe = (EditText) findViewById(R.id.about_text_input);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.reason_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        // listener for the static spinner
        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        // references the block user spinner that we're using
        final Spinner staticSpinnerTwo = (Spinner) findViewById(R.id.static_spinner_two);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapterTwo = ArrayAdapter
                .createFromResource(this, R.array.option_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapterTwo
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinnerTwo.setAdapter(staticAdapterTwo);

        // listener for the static spinner
        staticSpinnerTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        Button submitButton = (Button) findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reason = staticSpinner.getSelectedItem().toString();
                String comments = aboutMe.getText().toString();
                String blockFlag = staticSpinner.getSelectedItem().toString();

                Log.v("Match's id", Integer.toString(ReportActivity.this.matchID));
                // creates the server connection that will process the report

                confirmUnmatch(reason, comments, blockFlag);

        }
        });

    }

    // additional dialog box to ensure that the user wants to unmatch with the current person
    private void confirmUnmatch(final String reason, final String comments, final String blockFlag)
    {
        // setup the alert builder
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("Are you sure you want to report and unmatch with " + this.matchFirstName + "?");

        confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // Do nothing but close the dialog
                Log.v("Unmatch chosen", "User chose to unmatch with the person");
                ServerConnection reportConn = new ServerConnection("http://cop4331groupeight.com/chatapi.php");
                String error = reportConn.reportUser(ReportActivity.this.userID, ReportActivity.this.matchID, reason, comments, blockFlag);
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
            session.handleUnmatch();

            Intent myIntent = new Intent(getApplicationContext(),
                    MatchActivity.class);
            myIntent.putExtra("userID", userID);
            myIntent.putExtra("inputEmail", userEmail);
            myIntent.putExtra("profileLocation", profileLocation);
            myIntent.putExtra("firstName", firstName);
            myIntent.putExtra("lastName", lastName);
            myIntent.putExtra("inChat", inChat);
            // creates a little text bubble indicating that the login was successful
            Toast.makeText(getApplicationContext(), "Successfully unmatched and reported.", Toast.LENGTH_LONG).show();
            startActivity(myIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //go through the process of switching back to the main activity page..
    }
}
