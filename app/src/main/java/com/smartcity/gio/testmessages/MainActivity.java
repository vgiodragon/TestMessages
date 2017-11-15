package com.smartcity.gio.testmessages;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.smartcity.gio.testmessages.AccessDataBase.FeedReaderContract;
import com.smartcity.gio.testmessages.AccessDataBase.FeedReaderDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG="GIODEBUG";
    FeedReaderDbHelper mDbHelper;

    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WAKE_LOCK,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 4;
    private final int code_request=1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

        //String TABLE_NAME = "beagonsglobal";
        //mDbHelper = new FeedReaderDbHelper(getApplicationContext(),TABLE_NAME);

        //Insert(TABLE_NAME);
        //Read(TABLE_NAME);

        //Intent intent = new Intent(this, MyServiceMQTT.class);
        //startService(intent);
    }

    public void startService(View view){
        Intent intent = new Intent(this, MyServiceMQTT.class);
        startService(intent);
    }

    public void stopService(View view){
        Intent intent = new Intent(this, MyServiceMQTT.class);
        stopService(intent);
    }

    public void saveFile(View view){
        //Read("beagonsglobal");

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Beagons");
        Log.d("GIODEBUG","root"+root);
        String filename = "beagonsglobal.csv";

        File file = new File (myDir, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            //out.write(string.getBytes());
            ArrayList<Publicacion>publicacions = Read("beagonsglobal");

            for (Publicacion publicacion : publicacions)
                out.write(publicacion.toString().getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Saved!!", Toast.LENGTH_SHORT)
                .show();
    }



    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case code_request:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "COARSE LOCATION permitido", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "COARSE LOCATION no permitido", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void Insert(String TABLE_NAME){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss.SSS");
        String currentDateandTime = sdf.format(new Date());
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, String.valueOf(currentDateandTime));
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "{Esto es un JSON}");

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);
        Log.d(TAG,"insert: "+newRowId);
    }

    public ArrayList<Publicacion> Read(String TABLE_NAME){
        mDbHelper = new FeedReaderDbHelper(getApplicationContext(),TABLE_NAME);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " DESC";

        Cursor  cursor = db.rawQuery("select * from "+TABLE_NAME,null);
// Define a projection that specifies which columns from the database
// you will actually use after this query.

        ArrayList <Publicacion>elemtns = new ArrayList<>();

        while(cursor.moveToNext()) {
            String name =cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE));
            String hora =cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE));

            //elemtns.add(hora+"+"+name);

                String horat[] = hora.split("_");
                try {
                    JSONObject jsonObject = new JSONObject(name);
                    String hora2[]=jsonObject.getString("date").split(" ");
                    String fecha_llegada = horat[0].substring(0,2)+"/"
                            +horat[0].substring(2,4)+"/"+horat[0].substring(4);
                    String hora_llegada = horat[1].substring(0,2)+":"
                            +horat[1].substring(2,4)+":"+horat[1].substring(4);

                    String fecha_envio2 = hora2[0].replace("-","/");
                    Publicacion mpublicacion =new Publicacion(fecha_llegada,hora_llegada,
                            fecha_envio2.substring(2),hora2[1],jsonObject.getDouble("value"));
                    elemtns.add(mpublicacion);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        //Log.d(TAG,"item: "+itemIds.toString());
        //Log.d(TAG,"elemtns: "+elemtns.toString());
        Log.d(TAG,"size: "+elemtns.size());
        cursor.close();
        return elemtns;
    }
}















