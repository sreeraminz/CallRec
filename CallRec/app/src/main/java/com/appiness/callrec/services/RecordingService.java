package com.appiness.callrec.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appiness.callrec.utilities.CommonMethods;
import com.google.firebase.storage.FirebaseStorage;
import java.io.IOException;


public class RecordingService extends Service {
    String SQLiteQuery;
    SQLiteDatabase SQLITEDATABASE;
    String rec;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private MediaRecorder recorder;

    @Override
    public void onDestroy() {
        super.onDestroy();
        recorder.stop();
        SubmitData2SQLiteDB();
        Intent mIntent = new Intent(this, UploadingService.class);
        startService(mIntent);
        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DBCreate();
        recorder = new MediaRecorder();
        recorder.reset();
        String phoneNumber=intent.getStringExtra("number");
        String time=new CommonMethods().getTIme();
        String path=new CommonMethods().getPath();
        rec=path+"/"+phoneNumber+"_"+time+".mp4";


        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(rec);
        Log.d("======RecService====", rec);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        return START_NOT_STICKY;
    }
    public void DBCreate(){

        SQLITEDATABASE = openOrCreateDatabase("DBVoice", Context.MODE_PRIVATE, null);
        SQLITEDATABASE.execSQL("CREATE TABLE IF NOT EXISTS TableVoice(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, rec VARCHAR);");
    }

    public void SubmitData2SQLiteDB() {

        SQLiteQuery = "INSERT INTO TableVoice (rec) VALUES('" + rec + "');";
        SQLITEDATABASE.execSQL(SQLiteQuery);
    }
}
