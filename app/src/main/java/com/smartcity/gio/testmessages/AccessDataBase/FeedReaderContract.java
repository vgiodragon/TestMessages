package com.smartcity.gio.testmessages.AccessDataBase;

import android.provider.BaseColumns;

/**
 * Created by gio on 9/11/17.
 */

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {

        public static final String COLUMN_NAME_TITLE = "hora_llegada";
        public static final String COLUMN_NAME_SUBTITLE = "mensaje";
    }
}
