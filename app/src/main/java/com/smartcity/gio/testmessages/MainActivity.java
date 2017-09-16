package com.smartcity.gio.testmessages;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.smartcity.gio.testmessages.AccessDataBase.FeedReaderContract;
import com.smartcity.gio.testmessages.AccessDataBase.FeedReaderDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG="GIODEBUG";
    FeedReaderDbHelper mDbHelper;
    // Define a projection that specifies which columns from the database
    String[] projection = {
            FeedReaderContract.FeedEntry._ID,
            FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
            FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new FeedReaderDbHelper(getBaseContext());

        Insert();
        Read();
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

        Cursor  cursor = db.rawQuery("select * from "+FeedReaderContract.FeedEntry.TABLE_NAME,null);

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
