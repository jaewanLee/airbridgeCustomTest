package org.airbloc.airbridgecustomtest;

import com.google.firebase.messaging.FirebaseMessaging;

import io.airbridge.AirBridge;
import io.airbridge.deeplink.DeepLink;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AirBridge.setDebugMode(true);

        AirBridge.setCustomSessionTimeOut(15);
        AirBridge.setWifiInfoEnable(true);
        DeepLink.trackAirbridgeLinkOnly();
        //TODO eaby-work
        AirBridge.setIsFirebaseCallback(true);
        AirBridge.init(this, "customerTester", "335822e4f8d542f6b56c8ca963dcc717");

        FirebaseMessaging.getInstance().subscribeToTopic("airbridge_test");


    }

}
