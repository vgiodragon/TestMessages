package com.smartcity.gio.testmessages;

import android.content.Context;
import android.util.Log;

/**
 * Created by gio on 9/13/17.
 */

public class Utils {



    private static String userMqtt ="C001";
    private static String passMqtt ="100C";
    private static String topicMqtt ="C001/Temperatura";
    //private static String TABLE_NAME="beagonslocal";
    //private static final String ip ="192.168.1.102";
    private static String TABLE_NAME="beagonsglobal";
    private static final String ip ="beagons.uni.edu.pe";


    public static String getIp() {
        return ip;
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

    public static String getTableName() {
        return TABLE_NAME;
    }
}
