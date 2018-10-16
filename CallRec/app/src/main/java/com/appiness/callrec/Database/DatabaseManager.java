package com.appiness.callrec.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appiness.callrec.utilities.CallDetails;

import java.util.ArrayList;
import java.util.List;

import static com.appiness.callrec.Database.DatabaseHandler.PHONE_NUMBER;
import static com.appiness.callrec.Database.DatabaseHandler.SERIAL_NUMBER;
import static com.appiness.callrec.Database.DatabaseHandler.TABLE_RECORD;
import static com.appiness.callrec.Database.DatabaseHandler.TIME;

public class DatabaseManager {

    SQLiteDatabase sqLiteDatabase;

    public DatabaseManager(Context activity) {
        sqLiteDatabase = DatabaseSingleton.getInstance(activity);
    }

    public void addCallDetails(CallDetails callDetails) {
        ContentValues values = new ContentValues();
        values.put(SERIAL_NUMBER, callDetails.getSerial());
        values.put(DatabaseHandler.PHONE_NUMBER, callDetails.getNum());
        values.put(DatabaseHandler.TIME, callDetails.getTime1());
        values.put(DatabaseHandler.DATE, callDetails.getDate1());

        sqLiteDatabase.insert(TABLE_RECORD, null, values);
    }


        public List<CallDetails> getAllDetails() {
            List<CallDetails> recordList = new ArrayList<>();
            String selectQuery = "SELECT * FROM " + TABLE_RECORD;
            Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    CallDetails callDetails = new CallDetails();
                    callDetails.setSerial(cursor.getInt(0));
                    callDetails.setNum(cursor.getString(1));
                    callDetails.setTime1(cursor.getString(2));
                    callDetails.setDate1(cursor.getString(3));

                    recordList.add(callDetails);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return recordList;
        }


        public List<CallDetails> getFiveItems(int startLimit, int endLimit){
            List<CallDetails> recordList = new ArrayList<>();
            String selectQuery = "SELECT * FROM " + TABLE_RECORD+ " where ("+SERIAL_NUMBER+" BETWEEN "+startLimit+ " AND "+endLimit+" )";
            Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    CallDetails callDetails = new CallDetails();
                    callDetails.setSerial(cursor.getInt(0));
                    callDetails.setNum(cursor.getString(1));
                    callDetails.setTime1(cursor.getString(2));
                    callDetails.setDate1(cursor.getString(3));

                    recordList.add(callDetails);
                } while (cursor.moveToNext());
            }
          cursor.close();
            return recordList;
        }

        public int getCount(){
            String selectQuery = "SELECT * FROM " + TABLE_RECORD;
            int count =0;
            Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
            count = cursor.getCount();
            cursor.close();
            return count;
        }

        public String getNumber(String time){

            String number= new String();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT "+PHONE_NUMBER+" FROM "+TABLE_RECORD+" WHERE "+ TIME+"=?", new String[] {time });
            if (cursor.moveToFirst()) {
                do {
                    number = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return number;
        }

}
