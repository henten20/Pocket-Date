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
    private String matchProfileLocation;
    private String matchFirstName;
    private String matchLastName;

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
        this.creepTextView = (TextView)findViewById(R.id.creep_textView);
        this.creepView = (ImageView)findViewById(R.id.creep_image);

        // fills in all of the appropriate profile text and image fields
        fillFields();

    }

    // fills in all of the fields for the profile
    private void fillFields()
    {
        TextView nameView = (TextView)findViewById(R.id.match_name_textView);
        nameView.setText(this.matchFirstName + " " + this.matchLastName);

        ImageView profileView = (ImageView)findViewById(R.id.profileImageView);
        new DownloadImageTask(profileView).execute(this.matchProfileLocation);

        adjustCreepImage();
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

    // adjust the textview and the imageview depending on the creep level of the match
    private void adjustCreepImage()
    {
        // switch statement that will set the appropriate textview/imageview depending on the creep level of the match
        switch(this.matchCreepLevel)
        {
            case 0:
                this.creepView.setImageResource(R.drawable.progress0);
                this.creepTextView.setText("Not a creep!");
                break;
            case 1:
                this.creepView.setImageResource(R.drawable.progress1);
                this.creepTextView.setText("Might be a creep.");
                break;
            case 2:
                this.creepView.setImageResource(R.drawable.progress2);
                this.creepTextView.setText("Creepy");
                break;
            case 3:
                this.creepView.setImageResource(R.drawable.progress3);
                this.creepTextView.setText("Creepier than average");
                break;
            case 4:
                this.creepView.setImageResource(R.drawable.progress4);
                this.creepTextView.setText("Very creepy. Be careful.");
                break;
            default:
                this.creepView.setImageResource(R.drawable.progress0);
                this.creepTextView.setText("Not a creep!");
                break;
        }

    }
}
