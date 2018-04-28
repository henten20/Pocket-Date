package com.example.pocketdate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class MatchProfileActivity extends AppCompatActivity {

    private int matchCreepLevel;

    private ImageView creepView;

    private TextView creepTextView;
    private TextView birthdateLabel;
    private TextView genderLabel;
    private TextView aboutLabel;

    private String matchProfileLocation;
    private String matchFirstName;
    private String matchLastName;
    private String matchGender;
    private String matchAbout;
    private String matchBirthdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_profile);

        // grabs the intent object that contains the bundled data being passed in
        Intent thisActivity = getIntent();
        this.matchCreepLevel = Integer.parseInt(thisActivity.getStringExtra("creepLevel"));
        this.matchProfileLocation = thisActivity.getStringExtra("profileLocation");
        this.matchFirstName = thisActivity.getStringExtra("matchFirstName");
        this.matchLastName = thisActivity.getStringExtra("matchLastName");
        this.matchAbout = thisActivity.getStringExtra("matchAbout");
        this.matchBirthdate = thisActivity.getStringExtra("matchBirthdate");
        this.matchGender = thisActivity.getStringExtra("matchGender");

        this.birthdateLabel = (TextView)findViewById(R.id.match_birthdate_label);
        this.genderLabel = (TextView)findViewById(R.id.match_gender_label);
        this.aboutLabel = (TextView)findViewById(R.id.match_about_label);

        // fills in all of the appropriate profile text and image fields
        fillFields();

    }

    // fills in all of the fields for the profile
    private void fillFields()
    {
        TextView nameView = (TextView)findViewById(R.id.match_name_textView);
        nameView.setText(this.matchFirstName + " " + this.matchLastName);

        birthdateLabel.setText(this.matchBirthdate);
        genderLabel.setText(this.matchGender);
        aboutLabel.setText(this.matchAbout);

        ImageView profileView = (ImageView)findViewById(R.id.profileImageView);
        new DownloadImageTask(profileView).execute(this.matchProfileLocation);

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
