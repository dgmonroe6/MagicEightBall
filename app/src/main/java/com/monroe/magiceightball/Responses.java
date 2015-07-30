package com.monroe.magiceightball;

/**
 * Created by Monroe on 7/24/2015.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.sql.SQLException;
import java.util.HashMap;

public class Responses extends ContentProvider
{
    static final String SUBJECT_NAME = "com.monroe.provider.MagicEightBall";
    static final String URL = "content://" + SUBJECT_NAME + "/friends";
    static final Uri CONTENT_URI = Uri.parse(URL);

    //fields for the database
    static final String ID = "id";
    static final String NAME = "name";
    static final String RESPONSE = "response";

    DBHelper dbHelper;

    //projection map for a query
    private static HashMap<String, String> ResponseMap;

    //integer values used in content URI
    static final int FRIENDS = 1;
    static final int FRIENDS_ID = 2;

    //maps content URI "pattern" to the integer values that were set above
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher((UriMatcher.NO_MATCH));
        uriMatcher.addURI(SUBJECT_NAME, "friends", FRIENDS);
        uriMatcher.addURI(SUBJECT_NAME, "friends/#", FRIENDS_ID);
    }

    //database declarations
    private SQLiteDatabase database;
    static final String DATABASE_NAME = "ProphecyResponses";
    static final String TABLE_NAME = "responseTable";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " response TEXT NOT NULL);";

    public Responses() {}

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.v(DBHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ". Old data will be destroyed");
            db.execSQL("DROP_TABLE_IF_EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch(uriMatcher.match(uri)){
            case FRIENDS:
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case FRIENDS_ID:
                String id = uri.getLastPathSegment();
                count = database.delete(TABLE_NAME, ID +
                        " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        //at the givwen URI
        switch(uriMatcher.match(uri)){
            case FRIENDS:
                return "vnd.android.cursor.dir/vnd.monroe.friends";
            case FRIENDS_ID:
                return "vnd.android.cursor.item/vnd.monroe.friends";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = database.insert(TABLE_NAME, "", values);

        //if record is added successfully
        if (row > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        try {
            throw new SQLException("Fail to add a new record into " + uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        dbHelper = new DBHelper(context);

        //permissions to the writable
        database = dbHelper.getWritableDatabase();
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        switch(uriMatcher.match(uri)){
            //maps all database column names
            case FRIENDS:
                queryBuilder.setProjectionMap(ResponseMap);
                break;

            case FRIENDS_ID:
                queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        if(sortOrder == null || sortOrder == ""){
            sortOrder = NAME;
        }

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);

        //register to watch a content URI for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int count = 0;

        switch(uriMatcher.match(uri)){
            case FRIENDS:
                count = database.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case FRIENDS_ID:
                count = database.update(TABLE_NAME, values, ID +
                        " = " + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}