package com.smartcity.gio.testmessages;

/**
 * Created by gio on 9/13/17.
 */


import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gio on 8/16/17.
 */

public class MiCallBackMQTT implements MqttCallback {
    private final String GioDBug = "GioDBug_MCBMQTT";

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(GioDBug,"Connection was lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(GioDBug,"Message llego Arrived!: " + topic + ": " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(GioDBug,"Delivery Complete!");
    }

/*
    public PosicionBus convertJson(String contentAsString){
        PosicionBus posicionBus = null;

        try {
            JSONObject jsonObject = new JSONObject(contentAsString);

            posicionBus = new PosicionBus(
                    jsonObject.getString("time") ,
                    jsonObject.getDouble("latitude"),
                    jsonObject.getDouble("longitude")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  posicionBus;

    }
    */
}