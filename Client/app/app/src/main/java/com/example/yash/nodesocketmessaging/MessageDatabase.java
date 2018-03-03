package com.example.yash.nodesocketmessaging;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yash on 03-03-2018.
 */

public class MessageDatabase extends SQLiteOpenHelper {

    // All private Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "KlydoMessage";

    // table name
    private static final String TABLE_USER_LIST = "UserList";
    private static final String TABLE_CHAT_THREAD = "ChatThread";

    // Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FROM = "user_from";
    private static final String KEY_TO = "user_to";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "time";

    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    public MessageDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create table if not exists
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER_LIST + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_EMAIL + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);

        //Create table if not exists
        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_CHAT_THREAD + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FROM + " TEXT,"
                + KEY_TO + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_TIMESTAMP + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_LIST);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_THREAD);

        // Create tables again
        onCreate(sqLiteDatabase);
    }


    //Add new message to database
    public void addMessage(UserUtils UserUtils) {

        //gets the instance in built(SQL lite) writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //Insert New Recored
        String query = "INSERT INTO " + TABLE_CHAT_THREAD + "(" + KEY_FROM + "," + KEY_TO + "," + KEY_MESSAGE + "," + KEY_TIMESTAMP + ") VALUES('" + UserUtils.getFrom() + "','" + UserUtils.getTo() + "','" + UserUtils.getMessage() + "','" + UserUtils.getTime() + "')";
        db.execSQL(query);

    }

    //Add chats to in built sql lite for instant access to messages
    // this function is called only once to fill the chat threads
    public void addUser(UserUtils user) {
        SQLiteDatabase db = this.getWritableDatabase();

        //to check whether record exists or not
        boolean exist = false;
        String query = "SELECT * FROM " + TABLE_USER_LIST + " WHERE " + KEY_USER_EMAIL + " = '" + user.getEmail() + "'";
        Cursor cursor = db.rawQuery(query, null);

        //checks whether table contains record
        if (cursor.moveToFirst()) {
            //loop through all records
            do {
                exist = true;
            } while (cursor.moveToNext());
        }
        //if not exist then insert new one
        if (!exist) {
            query = "INSERT INTO " + TABLE_USER_LIST + "(" + KEY_USER_NAME + "," + KEY_USER_EMAIL + ") values('" + user.getName() + "','" + user.getEmail() + "')";
            db.execSQL(query);
        }
        cursor.close();
    }

    //Get all user list to show on message activity
    public List<UserUtils> getAllUserList() {

        //create structure array
        List<UserUtils> UserUtilsList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER_LIST;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //checks whether table contains record
        if (cursor.moveToFirst()) {
            //loop through all records
            do {
                UserUtils UserUtils = new UserUtils();

                //create single structure
                UserUtils.setName(cursor.getString(1));
                UserUtils.setEmail(cursor.getString(2));

                //push to structure array
                UserUtilsList.add(UserUtils);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return UserUtilsList;
    }

    // Getting all message for single chat thread
    public List<UserUtils> getAllMessage(String to, String from) {
        List<UserUtils> UserUtilsList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CHAT_THREAD + " WHERE (" + KEY_FROM + " = '" + from + "' AND " + KEY_TO + " = '" + to + "') OR (" + KEY_FROM + " = '" + to + "' AND " + KEY_TO + " = '" + from + "') ORDER BY " + KEY_ID + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //checks whether table contains record
        if (cursor.moveToFirst()) {
            //loop through all records
            do {
                UserUtils UserUtils1 = new UserUtils();

                //creating structure of user info
                UserUtils1.setTo(cursor.getString(1));
                UserUtils1.setMessage(cursor.getString(2));

                //push structure to array
                UserUtilsList.add(UserUtils1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return UserUtilsList;
    }

    //Deletes User From List
    public void deleteUser(UserUtils UserUtils) {

        //gets the instance in built(SQL lite) writable database
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_USER_LIST + " WHERE " + KEY_USER_EMAIL + " = '" + UserUtils.getTo() + "'";

        //executes a query
        db.execSQL(query);
    }

    public Boolean getUserCount() {
        SQLiteDatabase db = this.getWritableDatabase();

        //to check whether the given record exists
        boolean exist = false;
        String query = "SELECT * FROM " + TABLE_USER_LIST;

        //Stores all record from given query
        Cursor cursor = db.rawQuery(query, null);

        //checks whether table contains record
        if (cursor.moveToFirst()) {
            //loop through all records
            do {
                exist = true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return exist;
    }
}
