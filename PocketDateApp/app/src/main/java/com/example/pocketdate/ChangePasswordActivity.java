package com.example.pocketdate;



import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;

import android.text.TextWatcher;

import android.util.Log;
import android.view.MenuItem;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.Toast;

import java.util.HashMap;


public class ChangePasswordActivity extends AppCompatActivity {

    private int userID;
    // Session Manager Class
    SessionManagement session;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.password_dialog_layout);

        final EditText password1 = (EditText) findViewById(R.id.EditText_Pwd1);
        final EditText password2 = (EditText) findViewById(R.id.EditText_Pwd2);

        // get user data from session
        session = new SessionManagement(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();


        this.userID = Integer.parseInt(user.get(SessionManagement.KEY_USER));

        Button button = (Button) findViewById(R.id.go_back);

        button.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                if(password1.getText().toString().trim().length() == 0 || password2.getText().toString().trim().length() == 0)
                {
                    password1.setBackgroundColor(0x26FF0000);
                    password2.setBackgroundColor(0x26FF0000);
                    Toast.makeText(ChangePasswordActivity.this, "One or both of the fields is/are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(password1.getText().toString().equals(password2.getText().toString()))
                {
                    ServerConnection passConn = new ServerConnection("http://cop4331groupeight.com/chatapi.php");
                    String resultString = passConn.changePass(ChangePasswordActivity.this.userID, password1.getText().toString());
                    Toast.makeText(ChangePasswordActivity.this, "Password successfully changed!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                    startActivity(myIntent);
                }
                else
                {
                    password2.setBackgroundColor(0x26FF0000);
                    Toast.makeText(ChangePasswordActivity.this, "Passwords do not match. Please re-enter", Toast.LENGTH_SHORT).show();
                }

            }

        });



        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(password1.getText().toString().equals(password2.getText().toString()))
                {
                    password2.setBackgroundColor(0x2600FF00);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!(password1.getText().toString().equals(password2.getText().toString())))
                {
                    password2.setBackgroundColor(0x26FF0000);
                }

            }

        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(password2.getText().toString().equals(password1.getText().toString()))
                {
                    password2.setBackgroundColor(0x2600FF00);
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(!(password2.getText().toString().equals(password1.getText().toString())))
                {
                    password2.setBackgroundColor(0x26FF0000);
                }
            }

        });

    }

}