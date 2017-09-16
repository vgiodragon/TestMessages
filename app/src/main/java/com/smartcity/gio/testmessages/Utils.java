package com.smartcity.gio.testmessages;

import android.content.Context;
import android.util.Log;

/**
 * Created by gio on 9/13/17.
 */

public class Utils {

    private static final String ipglobal ="181.65.191.191";
    private static final String iplocal ="181.65.191.191";
    private static String userMqtt ="Alumno";
    private static String passMqtt ="Alumno";
    private static String topicMqtt ="Alumno";

    public static String getIpglobal() {
        return ipglobal;
    }

    public static String getIplocal() {
        return iplocal;
    }

    public static String getUserMqtt() {
        return userMqtt;
    }

    public static String getPassMqtt() {
        return passMqtt;
    }

    public static String getTopicMqtt() {
        return topicMqtt;
    }

    public static void StartSubcriptiontoAlarms(Context ctx){
        /*
        ///Cargo las alarmas de la DB xq sino cuando llame al service leera un NULL!
        InventeAlarms();
        if(mFogLan==null)
            mFogLan = new FogLan();
        ////
        suscriberAlarms = new SuscriberAlarm[alarms.length];
        for(int i=0;i<alarms.length;i++){
            suscriberAlarms[i] = new SuscriberAlarm(ctx,alarms[i]);
            suscriberAlarms[i].creoClienteMQTT();
        }

        Log.d(TAG,"StartSubcriptiontoAlarms_ suscrito!");
        */
    }
}
