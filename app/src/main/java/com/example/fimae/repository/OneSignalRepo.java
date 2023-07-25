package com.example.fimae.repository;

import android.content.Context;

import com.onesignal.OneSignal;

public class OneSignalRepo {
    private static final String ONESIGNAL_APP_ID = "e4027807-c701-4d05-8712-0246cdbbc0d8";

    public static void init(Context context) {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(context);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

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
