package com.appiness.callrec.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.appiness.callrec.Database.DatabaseHandler;


public class DatabaseSingleton {


    public static SQLiteDatabase database;

    public static SQLiteDatabase getInstance(Context activity){
        if(database==null)
            database = new DatabaseHandler(activity).getWritableDatabase();
        return database;
    }
}
