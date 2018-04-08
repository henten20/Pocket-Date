package com.example.pocketdate;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    final Context context = this;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ActionBar bar = getSupportActionBar();
        //sets color of the action bar to black
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        bar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        //TextView tv = (TextView) findViewById(R.id.textView2);
        //tv.setText(Html.fromHtml("<a href=http://www.cop4331groupeight.com> Need an account?"));
        //tv.setMovementMethod(LinkMovementMethod.getInstance());

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        // initially deactivate loading view
        //findViewById(R.id.loadingPanel).setVisibility(View.GONE);

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
                //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                attemptLogin();
                //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });

        //mLoginFormView = findViewById(R.id.login_form);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        HttpURLConnection conn;
        URL url = null;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
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

                //String resultString = result.toString();
                Log.v("JSON", resultString);
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

                Log.v("Profile", profileLocation);
                // login attempt was successful and we should proceed to the next activity
                if(error.equals("None"))
                {
                    // Start NewActivity.class
                    Intent myIntent = new Intent(LoginActivity.this,
                            MatchActivity.class);
                    myIntent.putExtra("userID", userID);
                    myIntent.putExtra("inputEmail", email);
                    myIntent.putExtra("profileLocation", profileLocation);
                    myIntent.putExtra("firstName", firstName);
                    myIntent.putExtra("lastName", lastName);
                    myIntent.putExtra("inChat", inChat);
                    // creates a little text bubble indicating that the login was successful
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                    startActivity(myIntent);
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

    // sample method that will check if an email is valid or not.
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    // sample method that will check if a password is valid or not
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}

