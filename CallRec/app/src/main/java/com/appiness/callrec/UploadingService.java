package com.appiness.callrec;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;

public class UploadingService extends Service {

    SQLiteDatabase SQLITEDATABASE;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://callrec-be9cd.appspot.com");

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String phoneNumber = intent.getStringExtra("number");
        String time = new CommonMethods().getTIme();

        SQLITEDATABASE = openOrCreateDatabase("DBVoice", Context.MODE_PRIVATE, null);

        Cursor cursor = SQLITEDATABASE.rawQuery("SELECT * FROM TableVoice",null);
        if(cursor.moveToFirst()) {
            do {
                final String string = cursor.getString(cursor.getColumnIndex("rec"));

                Uri filepath = Uri.fromFile(new File(string));
                Log.d("======UploadService====", filepath.getPath());
                Log.d("======UploadService2===", string);

                try{
                    Log.d("======number===", phoneNumber);
                   }
                catch (Exception ee) {
                    ee.printStackTrace();
                }

                StorageReference childRef = storageRef.child(phoneNumber + "__" + time + "voice");
                UploadTask uploadTask = childRef.putFile(filepath);
                uploadTask
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(UploadingService.this, "Done", Toast.LENGTH_SHORT).show();
                                SQLITEDATABASE.delete("TableVoice", "rec" + " = ?", new String[] { string });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadingService.this, "Failed", Toast.LENGTH_SHORT).show();
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
