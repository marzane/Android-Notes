package com.marzane.notepad.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.marzane.notepad.Utils.DateUtil;
import com.marzane.notepad.models.NoteModel;
import com.marzane.notepad.repository.database.DatabaseContrat;

import java.util.ArrayList;

public class RecentNotesRepository extends DatabaseContrat.DataBaseDbHelper implements ICRUD<Integer, NoteModel> {

    private static DatabaseContrat.RecentNote recentNoteTable;
    private static DatabaseContrat.DataBaseDbHelper dbHelper;
    private SQLiteDatabase db;

    public RecentNotesRepository(Context context) {
        super(context);
        dbHelper = new DatabaseContrat.DataBaseDbHelper(context);
    }

    // return NodeModel object or NULL
    @Override
    public NoteModel listById(Integer id) {
        db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                recentNoteTable._ID,
                recentNoteTable.COLUMN_NAME_TITLE,
                recentNoteTable.COLUMN_NAME_LASTEDIT,
                recentNoteTable.COLUMN_NAME_PATH,
                recentNoteTable.COLUMN_NAME_REAL_PATH
        };

        // Filter results WHERE "id" = my id
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {id.toString()};

        // How you want the results sorted in the resulting Cursor
        /*
        String sortOrder =
                recentNoteTable.COLUMN_NAME_LASTEDIT + " DESC";
         */

        Cursor cursor = db.query(
                recentNoteTable.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        NoteModel nota = new NoteModel();

        if(cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(recentNoteTable._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_TITLE));
            String lastEdit = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_LASTEDIT));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_PATH));
            String realPath = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_REAL_PATH));

            nota = new NoteModel(itemId, title, DateUtil.StringToLocalDateTime(lastEdit), path, realPath);
        } else {
            nota = null;
        }

        cursor.close();

        return nota;
    }


    // return NodeModel object or NULL
    public NoteModel listByPath(String pathArg) {
        db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                recentNoteTable._ID,
                recentNoteTable.COLUMN_NAME_TITLE,
                recentNoteTable.COLUMN_NAME_LASTEDIT,
                recentNoteTable.COLUMN_NAME_PATH,
                recentNoteTable.COLUMN_NAME_REAL_PATH
        };

        // Filter results WHERE "path" = my path
        String selection = recentNoteTable.COLUMN_NAME_PATH + " = ?";
        String[] selectionArgs = {pathArg};

        // How you want the results sorted in the resulting Cursor
        /*
        String sortOrder =
                recentNoteTable.COLUMN_NAME_LASTEDIT + " DESC";
         */

        Cursor cursor = db.query(
                recentNoteTable.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null                    // The sort order
        );

        NoteModel nota;

        if(cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(recentNoteTable._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_TITLE));
            String lastEdit = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_LASTEDIT));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_PATH));
            String realPath = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_REAL_PATH));

            nota = new NoteModel(itemId, title, DateUtil.StringToLocalDateTime(lastEdit), path, realPath);
        } else {
            nota = null;
        }

        cursor.close();

        return nota;
    }


    @Override
    public ArrayList<NoteModel> listAll() {
        db = dbHelper.getReadableDatabase();
        ArrayList<NoteModel> noteList = new ArrayList<>();

        if (db != null) {

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    recentNoteTable._ID,
                    recentNoteTable.COLUMN_NAME_TITLE,
                    recentNoteTable.COLUMN_NAME_LASTEDIT,
                    recentNoteTable.COLUMN_NAME_PATH,
                    recentNoteTable.COLUMN_NAME_REAL_PATH
            };

            // How you want the results sorted in the resulting Cursor

        String sortOrder =
                recentNoteTable.COLUMN_NAME_LASTEDIT + " DESC";


            Cursor cursor = db.query(
                    recentNoteTable.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    null,                   // The columns for the WHERE clause
                    null,                   // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            while (cursor.moveToNext()) {
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(recentNoteTable._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_TITLE));
                String lastEdit = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_LASTEDIT));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_PATH));
                String realPath = cursor.getString(cursor.getColumnIndexOrThrow(recentNoteTable.COLUMN_NAME_REAL_PATH));

                noteList.add(new NoteModel(itemId, title, DateUtil.StringToLocalDateTime(lastEdit), path, realPath));
            }
            cursor.close();
        }

        return noteList;
    }

    @Override
    public long insert(NoteModel modelo) {
        // Gets the data repository in write mode
        db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(recentNoteTable.COLUMN_NAME_TITLE, modelo.getTitle());
        values.put(recentNoteTable.COLUMN_NAME_LASTEDIT, DateUtil.LocalDateTimeToString(modelo.getLastOpened()));
        values.put(recentNoteTable.COLUMN_NAME_PATH, modelo.getPath());
        values.put(recentNoteTable.COLUMN_NAME_REAL_PATH, modelo.getRealPath());

        // Insert the new row, returning the primary key value of the new row or -1
        long newRowId = db.insert(recentNoteTable.TABLE_NAME, null, values);

        return newRowId;
    }


    @Override
    public int update(NoteModel modelo) {

        db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(recentNoteTable.COLUMN_NAME_TITLE, modelo.getTitle());
        values.put(recentNoteTable.COLUMN_NAME_LASTEDIT, DateUtil.LocalDateTimeToString(modelo.getLastOpened()));
        values.put(recentNoteTable.COLUMN_NAME_PATH, modelo.getPath());
        values.put(recentNoteTable.COLUMN_NAME_REAL_PATH, modelo.getRealPath());

        // Which row to update, based on the id
        String selection = recentNoteTable._ID + " LIKE ?";
        String[] selectionArgs = { modelo.getId() + "" };

        int count = db.update(
                recentNoteTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }


    public int updateNoteNoId(NoteModel model, NoteModel newModel) {

        db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(recentNoteTable.COLUMN_NAME_TITLE, newModel.getTitle());
        values.put(recentNoteTable.COLUMN_NAME_LASTEDIT, DateUtil.LocalDateTimeToString(newModel.getLastOpened()));
        values.put(recentNoteTable.COLUMN_NAME_PATH, newModel.getPath());
        values.put(recentNoteTable.COLUMN_NAME_REAL_PATH, newModel.getRealPath());

        // Which row to update, based on the path
        String selection = recentNoteTable.COLUMN_NAME_PATH + " LIKE ?";
        String[] selectionArgs = { model.getPath() };

        int count = db.update(
                recentNoteTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }


    public int updateByPath(NoteModel modelo) {

        db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(recentNoteTable.COLUMN_NAME_TITLE, modelo.getTitle());
        values.put(recentNoteTable.COLUMN_NAME_LASTEDIT, DateUtil.LocalDateTimeToString(modelo.getLastOpened()));

        // Which row to update, based on the path column
        String selection = recentNoteTable.COLUMN_NAME_PATH + " LIKE ?";
        String[] selectionArgs = { modelo.getPath() };

        int count = db.update(
                recentNoteTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }

    @Override
    public int delete(Integer id) {
        db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = recentNoteTable._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id.toString() };
        // Issue SQL statement.
        int deletedRows = db.delete(recentNoteTable.TABLE_NAME, selection, selectionArgs);

        return deletedRows;
    }


    public int deleteByPath(String uri) {
        db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = recentNoteTable.COLUMN_NAME_PATH + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { uri };
        // Issue SQL statement.
        int deletedRows = db.delete(recentNoteTable.TABLE_NAME, selection, selectionArgs);

        return deletedRows;
    }

    @Override
    public int deleteAll() {
        db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = "";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {};
        // Issue SQL statement.
        int deletedRows = db.delete(recentNoteTable.TABLE_NAME, selection, selectionArgs);

        return deletedRows;
    }
}
