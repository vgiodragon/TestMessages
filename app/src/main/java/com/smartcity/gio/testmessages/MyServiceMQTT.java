package com.smartcity.gio.testmessages;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.smartcity.gio.testmessages.AccessDataBase.FeedReaderContract;
import com.smartcity.gio.testmessages.AccessDataBase.FeedReaderDbHelper;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyServiceMQTT extends Service {
    private final String TAG="GIODEBUG";
    FeedReaderDbHelper mDbHelper;
    public MyServiceMQTT() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        //Utils.StartSubcriptiontoAlarms(this);
        Subcriptor subcriptorGlobal= new Subcriptor(getBaseContext(),Utils.getIpglobal());
        Subcriptor subcriptorLocal= new Subcriptor(getBaseContext(),Utils.getIplocal());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Utils.DisconnectandUnsubcribe();
        super.onDestroy();
    }

    public class Subcriptor{
        private Context ctx;
        private MqttAndroidClient mqttAndroidClient;
        private String ip;

        public Subcriptor(Context ctx, String ip) {
            this.ctx = ctx;
            this.ip = ip;
        }

        public void creoClienteMQTT(){
            int random= (int) (Math.random()*123456);
            mqttAndroidClient
                    = new MqttAndroidClient(ctx,  "tcp://"+
                    ip+":1883", "GioMovil"+random);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            options.setUserName(Utils.getUserMqtt());
            options.setPassword(Utils.getPassMqtt().toCharArray());

            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d("GIODEBUG_MQTT_SA","creoClienteMQTT_Llego del topic " + topic + ": " + new String(message.getPayload()));

                    // Store!!
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            try {
                IMqttToken token = mqttAndroidClient.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        //Log.d("GIODEBUG_MQTT_SA", "IMqttActionListener_onSuccess_"+alarm.getTopic()+" on");
                        try {
                            mqttAndroidClient.subscribe(Utils.getTopicMqtt(), 0);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("GIODEBUG_MQTT_SA", "IMqttActionListener_onFailure_"+asyncActionToken.toString());
                    }

                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void Insert(){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss.SSS");
        String currentDateandTime = sdf.format(new Date());
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, String.valueOf(currentDateandTime));
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "{Esto es un JSON}");

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        Log.d(TAG,"insert: "+newRowId);
    }

    public void Read(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " DESC";

        Cursor cursor = db.rawQuery("select * from "+FeedReaderContract.FeedEntry.TABLE_NAME,null);

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            itemIds.add(itemId);
        }
        Log.d(TAG,"item: "+itemIds.toString());
        cursor.close();

    }
}
