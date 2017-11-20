package com.smartcity.gio.testmessages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

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
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gio on 11/17/17.
 */
public class Subcriptor{
    private final String TAG="GIODEBUG_Sub";
    private Context ctx;
    private MqttAndroidClient mqttAndroidClient;
    private String ip;
    FeedReaderDbHelper mDbHelper;
    String TABLE_NAME;
    MqttDefaultFilePersistence filePersistence;

    public Subcriptor(Context ctx, String ip,String TABLE_NAME,MqttDefaultFilePersistence filePersistence) {
        this.ctx = ctx;
        this.ip = ip;
        this.TABLE_NAME = TABLE_NAME;
        mDbHelper = new FeedReaderDbHelper(ctx,TABLE_NAME);
        this.filePersistence = filePersistence;
    }

    public void creoClienteMQTT(){
        int random= (int) (Math.random()*123456);
        mqttAndroidClient
                = new MqttAndroidClient(ctx,  "tcp://"+
                ip+":1883", "GioMovil"+random,filePersistence);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        options.setUserName(Utils.getUserMqtt());
        options.setPassword(Utils.getPassMqtt().toCharArray());
        options.setCleanSession(true);

        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(120000);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //Log.d("GIODEBUG_MQTT_SA","creoClienteMQTT_Llego del topic " + topic + ": " + new String(message.getPayload()));
                // Store!!
                Insert(TABLE_NAME,new String(message.getPayload()));
                Read(TABLE_NAME);
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
                    Log.d("GIODEBUG_MQTT_SA", "IMqttActionListener_onSuccess_"+Utils.getTopicMqtt()+" on");
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
        Log.d("GIODEBUG_MQTT_SA","Suscribiendo");
    }
    public void Insert(String TABLE_NAME, String mnsj){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HH:mm:ss.SSS");
        String currentDateandTime = sdf.format(new Date());
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, String.valueOf(currentDateandTime));
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE,mnsj);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);
        Log.d(TAG,TABLE_NAME+"_insert: "+newRowId);
        Toast.makeText(ctx,"insertado",Toast.LENGTH_SHORT).show();
    }

    public void Read(String TABLE_NAME){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " DESC";

        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            itemIds.add(itemId);
        }
        //Log.d(TAG,"item: "+itemIds.toString());
        cursor.close();

    }

    private void Unsuscribe(){
        try {
            IMqttToken unsubToken = mqttAndroidClient.unsubscribe(Utils.getTopicMqtt());
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Log.d("GIODEBUG_MQTT", "Unsubcribe: onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("GIODEBUG_MQTT", "Unsubcribe: onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void Disconnect(){
        try {
            IMqttToken disconToken = mqttAndroidClient.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    Log.d("GIODEBUG_MQTT", "Disconnect: onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("GIODEBUG_MQTT", "Disconnect: onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void CancelarSuscripcion(){
        if(mqttAndroidClient!=null)
            if (mqttAndroidClient.isConnected()){
                Unsuscribe();
                Disconnect();
            }
    }
}