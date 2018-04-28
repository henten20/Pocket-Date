package com.example.pocketdate;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManagement session;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManagement(getApplicationContext());

        ImageView instaView = (ImageView) findViewById(R.id.instagram_image);
        ImageView facebookView = (ImageView) findViewById(R.id.facebook_image);
        ImageView twitterView = (ImageView) findViewById(R.id.twitter_image);

        // opens up pocketdate's twitter page
        twitterView.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View view) {

               try {
                   Intent intent = new Intent(Intent.ACTION_VIEW,
                           Uri.parse("twitter://user?screen_name=[PocketDate]"));
                   startActivity(intent);
               } catch (Exception e) {
                   startActivity(new Intent(Intent.ACTION_VIEW,
                           Uri.parse("https://mobile.twitter.com/PocketDate")));
               }

           }
       });

        // loads up pocketdate's instagram profile
        instaView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/_u/therealpocketdate");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/therealpocketdate")));
                }
            }
        });

        // loads up the pocketdate fb page
        facebookView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("fb://page/520160165052879");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.facebook.katana");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.facebook.com/Pocket-Date-520160165052879/")));
                }

            }
        });
        ActionBar bar = getSupportActionBar();

        //sets color of the action bar to black
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        bar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // creates bottomnav view where we will reference the website buttons from
        View bottomNav = findViewById(R.id.bottom_navigation);
        View websiteButton = bottomNav.findViewById(R.id.action_website);
        View registerButton = bottomNav.findViewById(R.id.action_register);

        // goes to the website
        websiteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://cop4331groupeight.com")));
            }
        });

        // goes to the website
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://cop4331groupeight.com/createAccount.html")));
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {

            // checks to see if the login was successful or not
            try
            {
                // server connection class will handle initiating the connection to the remote server
                ServerConnection loginConn = new ServerConnection("http://cop4331groupeight.com/androidlogin.php");
                String resultString = loginConn.initialLogin(email, password);

                JSONArray resultJSON = new JSONArray(resultString);
                JSONObject jsonObj = resultJSON.getJSONObject(0);

                int userID = jsonObj.getInt("userID");
                String userEmail = jsonObj.getString("email");
                String pass = jsonObj.getString("pass");
                String error = jsonObj.getString("error");
                String profileLocation = jsonObj.getString("profileLocation");
                String firstName = jsonObj.getString("firstName");
                String lastName = jsonObj.getString("lastName");
                boolean inChat = jsonObj.getBoolean("inChat");
                String preference = jsonObj.getString("preference");
                String about = jsonObj.getString("about");

                // login attempt was successful and we should proceed to the next activity
                if(error.equals("None"))
                {

                    session.createLoginSession(userID, userEmail, pass, profileLocation, firstName, lastName, inChat, true, preference, about);

                    // Staring MainActivity
                    Intent i = new Intent(getApplicationContext(), MatchActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Log.v("Error", "Unexpected error occurred when parsing data.");
                }
            }
            // failed login attempt
            catch(JSONException | NullPointerException e)
            {
                // if the user isn't currently connected to the internet, this exception will be thrown
                if (e instanceof NullPointerException)
                {
                    Toast.makeText(LoginActivity.this, "Error Connecting to Internet. Check your connection settings.", Toast.LENGTH_SHORT).show();
                }
                // otherwise, the user's credentials were invalid and they need to try again
                else
                {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }

            }
        }
    }
}

