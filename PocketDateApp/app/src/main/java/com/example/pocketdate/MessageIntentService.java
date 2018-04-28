package com.example.pocketdate;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MessageIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.pocketdate.action.FOO";
    private static final String ACTION_BAZ = "com.example.pocketdate.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.pocketdate.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.pocketdate.extra.PARAM2";

    public MessageIntentService() {
        super("MessageIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MessageIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MessageIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //performs the desired task
        ServerConnection checkNew = new ServerConnection("http://cop4331groupeight.com/chatapi.php");

        // ensures that there are extras to access
        if(intent != null)
        {
            int userID = intent.getIntExtra("userID", -1);

            JSONArray resultJSON;
            JSONObject resultJSONObj;

            String resultString = checkNew.checkMessages(userID);

            // attempts to convert the JSONString into a JSONArray for simple data manipulation
            try
            {
                resultJSON = new JSONArray(resultString);
                resultJSONObj = resultJSON.getJSONObject(0);
                String matchFirstName = resultJSONObj.getString("matchName");
                boolean notificationCheck = resultJSONObj.getBoolean("newNotification");

                // if we received a new message, let the user know via a push notification
                if(notificationCheck)
                {
                    android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo)
                            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                    R.drawable.logo))
                            .setContentTitle("New message!")
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setContentText("You've received a new message from " + matchFirstName + "!")
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                            new Intent(this, MatchActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(contentIntent);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(2, mBuilder.build());
                }
            }
            // handles the exception where the user loses connection to the internet
            catch(JSONException | NullPointerException e)
            {
                if(e instanceof NullPointerException)
                {
                    Toast.makeText(MessageIntentService.this, "Error Connecting to Internet. Check your connection settings.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            // kills the service if it tries activating while the intent is null
            stopSelf();
        }

        // If we get killed, after returning from here, restart
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
