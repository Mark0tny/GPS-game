package com.example.kotu9.gpsgame.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.kotu9.gpsgame.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String IDENTIFIER = "GeofenceIntentService";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.e(IDENTIFIER, "" + getErrorString(geofencingEvent.getErrorCode()));
            return;
        }
        Log.i(IDENTIFIER, geofencingEvent.toString());

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String transitionDetails = getGeofenceTransitionInfo(triggeringGeofences);
            String transitionType = getTransitionString(geofenceTransition);
            Log.d("IntentService Noti", "On handle" + "GEOFENCE ID:" + geofenceTransition);

            createNotification(context, transitionType, transitionDetails);
        }
    }

    private String getGeofenceTransitionInfo(List<Geofence> triggeringGeofences) {
        String triggeringLocationsString = "";
        for (Geofence geofence : triggeringGeofences) {
            triggeringLocationsString = geofence.getRequestId();
        }
        return triggeringLocationsString + "dupa";
    }


    private String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "geofence too many_geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "geofence too many pending_intents";
            default:
                return "geofence error";
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "location entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "location exited";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "dwell at location";
            default:
                return "location transition";
        }
    }

    private void createNotification(Context context, String locTransitionType, String locationDetails) {
        String CHANNEL_ID = "GPSgame";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.fui_ic_mail_white_24dp)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                        .setContentTitle(locTransitionType)
                        .setContentText(locationDetails)
                        .setAutoCancel(true).setCategory("Message")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());
        Log.d("IntentServiceNoti", builder.build().toString());

    }
}
