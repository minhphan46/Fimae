package com.example.fimae.repository;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fimae.activities.OnChatActivity;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OneSignalRepo {
    private static final String ONESIGNAL_APP_ID = "e4027807-c701-4d05-8712-0246cdbbc0d8";

    public static void init(Context context) {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(context);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.setNotificationWillShowInForegroundHandler(new OneSignal.OSNotificationWillShowInForegroundHandler() {
            @Override
            public void notificationWillShowInForeground(OSNotificationReceivedEvent osNotificationReceivedEvent) {
                OSNotification notification = osNotificationReceivedEvent.getNotification();
                // Get custom additional data you sent with the notification
                JSONObject data = notification.getAdditionalData();
                try {
                    String converId = data.getString("conversationId");
                    Log.i("TAG", "notificationWillShowInForeground: " + converId + " " +OnChatActivity.currentConversationId );
                    if(OnChatActivity.currentConversationId == null)
                    {
                        osNotificationReceivedEvent.complete(notification);
                    }
                    else if (!converId.equals(OnChatActivity.currentConversationId)) {
                        osNotificationReceivedEvent.complete(notification);
                    }
                    else {
                        osNotificationReceivedEvent.complete(null);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenedResult result) {
                OSNotification notification = result.getNotification();
                // Get custom additional data you sent with the notification
                JSONObject data = notification.getAdditionalData();
                try {
                    String converId = data.getString("conversationId");
                    Log.i("TAG", "notificationWillShowInForeground: " + converId + " " +OnChatActivity.currentConversationId );
                    if(converId != null)
                    {
                        Intent intent = new Intent(context, OnChatActivity.class);
                        intent.putExtra("conversationID", converId);
                        context.startActivity(intent);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // promptForPushNotifications will show the native Android notification permission prompt.
        OneSignal.promptForPushNotifications();

    }

    public static void setExternalId(String externalId) {
        OneSignal.setExternalUserId(externalId);
    }

    public static void removeExternalId()
    {
        OneSignal.removeExternalUserId();
    }
}
