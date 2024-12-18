package com.marzane.bloc_de_notas.repository.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class DatabaseContrat {

    private static final String SQL_CREATE_TABLE_NOTE =
            "CREATE TABLE " + RecentNote.TABLE_NAME + " (" +
                    RecentNote._ID + " INTEGER PRIMARY KEY," +
                    RecentNote.COLUMN_NAME_TITLE + " TEXT," +
                    RecentNote.COLUMN_NAME_LASTEDIT + " TEXT," +
                    RecentNote.COLUMN_NAME_PATH + " TEXT," +
                    RecentNote.COLUMN_NAME_REAL_PATH + " TEXT)";

    private static final String SQL_DELETE_TABLE_NOTE =
            "DROP TABLE IF EXISTS " + RecentNote.TABLE_NAME;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContrat() {}

    /* Inner class that defines the table contents */
    public static class RecentNote implements BaseColumns {
        public static final String TABLE_NAME = "recent_notes";

        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LASTEDIT = "last_edit";
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_REAL_PATH = "real_path";
    }


    public static class DataBaseDbHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "notebook.db";

        public DataBaseDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE_NOTE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            //db.execSQL(SQL_DELETE_TABLE_NOTE);
            //onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //onUpgrade(db, oldVersion, newVersion);
        }
    }

}
