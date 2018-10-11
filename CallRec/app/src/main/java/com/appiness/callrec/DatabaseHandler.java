package com.appiness.callrec;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "callRecords";
    public static final String TABLE_RECORD = "callRecord";
    public static final String SERIAL_NUMBER= "serialNumber";
    public static final String PHONE_NUMBER ="phoneNumber";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String TABLE_REC_VOICE = "table_voice_clip";
    public static final String REC_VOICE = "voice_clip";

    public DatabaseHandler(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOG_TABLE="CREATE TABLE " + TABLE_RECORD + "("
                + SERIAL_NUMBER + " INTEGER PRIMARY KEY,"+ PHONE_NUMBER + " TEXT," + TIME + " TEXT,"
                + DATE + " TEXT" + ")";
        db.execSQL(CREATE_LOG_TABLE);

        String CREATE_TABLE_REC_VOICE="CREATE TABLE " + TABLE_REC_VOICE + "("
                + REC_VOICE +  ")";
        db.execSQL(CREATE_TABLE_REC_VOICE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
        onCreate(db);

    }

}
