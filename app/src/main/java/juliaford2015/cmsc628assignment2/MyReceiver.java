package juliaford2015.cmsc628assignment2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR))
        {
            // TODO: handle if error
        }
        else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED))
        {
            // TODO: handle if deleted/missing
        }
        else
        {
            // okay, try getting key-values in received message
            String msg = extras.getString("message");
            String title = extras.getString("title");
            Log.i("debug receive", title + ": " + msg);
            //TODO: handle messages if okay
        }
    }
}
