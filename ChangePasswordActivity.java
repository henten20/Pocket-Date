package com.example.pocketdate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_dialog_layout);

        final EditText password1 = (EditText) findViewById(R.id.EditText_Pwd1);
        final EditText password2 = (EditText) findViewById(R.id.EditText_Pwd2);

        Button button = (Button) findViewById(R.id.go_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password1.getText().toString().trim().length() == 0 || password2.getText().toString().trim().length() == 0)
                {
                    Toast.makeText(ChangePasswordActivity.this, "One or both of the fields is/are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(password1.getText().toString().equals(password2.getText().toString()))
                {
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
