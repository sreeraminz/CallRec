package com.appiness.callrec.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.appiness.callrec.Database.DatabaseManager;
import com.appiness.callrec.utilities.CommonMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import static com.appiness.callrec.broadcast_receiver.PhoneStateReceiver.phoneNumber;


public class UploadingService extends Service {


    SQLiteDatabase SQLITEDATABASE;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://callrec-be9cd.appspot.com");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String time = new CommonMethods().getTIme();
        String n=  new DatabaseManager(this).getNumber(time);

        //Toast.makeText(this, "UpP"+n, Toast.LENGTH_SHORT).show();

        SQLITEDATABASE = openOrCreateDatabase("DBVoice", Context.MODE_PRIVATE, null);


        Cursor cursor = SQLITEDATABASE.rawQuery("SELECT * FROM TableVoice",null);
        if(cursor.moveToFirst()) {
            do {
                final String string = cursor.getString(cursor.getColumnIndex("rec"));

                Uri filepath = Uri.fromFile(new File(string));
                Log.d("======UploadService====", filepath.getPath());
                Log.d("======UploadService2===", string);


                StorageReference childRef = storageRef.child(phoneNumber+ "__" + time);


                UploadTask uploadTask = childRef.putFile(filepath);

                uploadTask
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(UploadingService.this, "done", Toast.LENGTH_SHORT).show();
                                SQLITEDATABASE.delete("TableVoice", "rec" + " = ?", new String[] { string });

                                //Toast.makeText(UploadingService.this, "N"+phoneNumber, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadingService.this, "failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }while(cursor.moveToNext());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
