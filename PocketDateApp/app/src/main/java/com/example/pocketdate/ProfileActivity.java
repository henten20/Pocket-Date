package com.example.pocketdate;


import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class ProfileActivity extends AppCompatPreferenceActivity  {
    private static final String TAG = ProfileActivity.class.getSimpleName();

    int userID;
    String userEmail;
    String profileLocation;
    String firstName;
    String lastName;
    boolean inChat;
    private boolean changeHappened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates a back button on the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent thisActivity = getIntent();

        // change happened represents whether or not the user changed any personal information while on this page
        // if they did, then we need to refresh the mainactivity page when we return. otherwise, onBackPressed() will suffice
        this.changeHappened = false;
        // this is a bad way to do this (use bundles and checks instead of directly accessing data from intent)
        this.userID = thisActivity.getIntExtra("userID", -1);
        this.userEmail = thisActivity.getStringExtra("inputEmail");
        this.profileLocation = thisActivity.getStringExtra("profileLocation");
        this.firstName = thisActivity.getStringExtra("firstName");
        this.lastName = thisActivity.getStringExtra("lastName");
        this.inChat = thisActivity.getBooleanExtra("inChat", true);
        //load profile fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ProfilePreferenceFragment()).commit();

    }

    // this is called after the file manager activity ends
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        // must call the super method here because we stated the activity from the preference fragment
        // and we want the data to be returned to the activity class
        super.onActivityResult(requestCode, resultCode, data);

        // if the result is okay, then go ahead and process the data
        if (resultCode != RESULT_CANCELED){
            if(data != null)
            {
                // grabs the uri from the file selection activity
                Uri targetUri = data.getData();
                String uriPath = getRealPathFromURI(targetUri);
                // create the stream variable that we will read the image in from
                final InputStream imageStream;

                // tries to get a base64 representation of the image
                try {
                    imageStream = getContentResolver().openInputStream(targetUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String encodedImage = encodeImage(selectedImage);
                    // establishes a connectino to our remote server where our php code will process the uploading of the image
                    ServerConnection myServer = new ServerConnection("http://cop4331groupeight.com/chatapi.php");
                    String resultString = myServer.updateProfilePic(encodedImage, this.userID);
                    Toast.makeText(getApplicationContext(), "Successfully updated profile picture", Toast.LENGTH_SHORT).show();
                    // logs that a change was made -- this is for the back button
                    this.changeHappened = true;
                    JSONArray resultJSON = new JSONArray(resultString);
                    JSONObject jsonObj = resultJSON.getJSONObject(0);
                    this.profileLocation = jsonObj.getString("profileLocation");
                // handles the case of the file uri being invalid or an invalid response from the php code
                } catch (FileNotFoundException | JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error loading image. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            // in case we can't load the image for some reason
            else
            {
                Toast.makeText(getApplicationContext(), "Error loading image. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // method that will take the bitmap of an image and convert it into a base64 string
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    // method that will take a uri and convert it into a valid filepath string
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    // preference fragment
    public static class ProfilePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // grabs the preference item that will activate the method for changing a user's profile image
            Preference myPref = (Preference) findPreference("change_prof_pic");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    // in onCreate or any event where your want the user to  select a file
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // call main activity's startActivityForResult function (allows us to return data to main activity)
                    // the 2 is an arbitrary request code. value just needs to be something greater than 0
                    getActivity().startActivityForResult(intent, 2);
                    return true;
                }
            });}
    }

    // if the user selects the back button, process the request depending on whether or not any data was altered (profile pic change, etc.)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            // if there was a change to the profile information, we need to refresh the main activity
            if(changeHappened)
            {
                Intent returnToMain = new Intent(this, MatchActivity.class);
                returnToMain.putExtra("userID", userID);
                returnToMain.putExtra("inputEmail", userEmail);
                returnToMain.putExtra("profileLocation", profileLocation);
                returnToMain.putExtra("firstName", firstName);
                returnToMain.putExtra("lastName", lastName);
                returnToMain.putExtra("inChat", inChat);
                // restarts the main activity
                startActivity(returnToMain);
            }
            // otherwise, just pop this activity off of the stack and go back to the previous activity, unchanged
            else
            {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}