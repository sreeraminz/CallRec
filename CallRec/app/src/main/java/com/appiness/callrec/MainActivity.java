package com.appiness.callrec;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.appiness.callrec.PhoneStateReceiver.phoneNumber;

public class MainActivity extends AppCompatActivity {


    DatabaseHandler db=new DatabaseHandler(this);
    final static String TAGMA="Main Activity";
    RecordAdapter rAdapter;
    RecyclerView recycler;
    List<CallDetails> callDetailsList;
    boolean checkResume=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putInt("numOfCalls",0).apply();



    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Check", "onResume: ");
        if(checkPermission()) {
            if(checkResume==false) {
                setUi();
                rAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void onPause()
    {
        super.onPause();
        SharedPreferences pref3=PreferenceManager.getDefaultSharedPreferences(this);
        if(pref3.getBoolean("pauseStateVLC",false)) {
            checkResume = true;
            pref3.edit().putBoolean("pauseStateVLC",false).apply();
        }
        else
            checkResume=false;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        MenuItem item=menu.findItem(R.id.mySwitch);

        View view = getLayoutInflater().inflate(R.layout.switch_layout,null,false) ;

        final SharedPreferences pref1= PreferenceManager.getDefaultSharedPreferences(this);

        SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.switchCheck);
        switchCompat.setChecked(pref1.getBoolean("switchOn",true));
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d("Switch", "onCheckedChanged: " +isChecked);
                    Toast.makeText(getApplicationContext(), "Call Recorder ON", Toast.LENGTH_LONG).show();
                    pref1.edit().putBoolean("switchOn",isChecked).apply();
                }else{
                    Log.d("Switch", "onCheckedChanged: " +isChecked);
                    Toast.makeText(getApplicationContext(), "Call Recorder OFF", Toast.LENGTH_LONG).show();
                    pref1.edit().putBoolean("switchOn",isChecked).apply();
                }
            }
        });
        item.setActionView(view);
        return true;
    }

    public void setUi()
    {
        recycler= findViewById(R.id.recyclerView);
        callDetailsList=new DatabaseManager(this).getAllDetails();

        for(CallDetails cd:callDetailsList)
        {
            String log="Phone num : "+cd.getNum()+" | Time : "+cd.getTime1()+" | Date : "+cd.getDate1();
        }

        Collections.reverse(callDetailsList);
        rAdapter=new RecordAdapter(callDetailsList,this);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(rAdapter);

    }


    private boolean checkPermission()
    {
        int i=0;
        String[] perm={Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS};
        List<String> reqPerm=new ArrayList<>();
        for(String permis:perm) {
            int resultPhone = ContextCompat.checkSelfPermission(MainActivity.this,permis);
            if(resultPhone== PackageManager.PERMISSION_GRANTED)
                i++;
            else {
                reqPerm.add(permis);
            }
        }

        if(i==5)
            return true;
        else
            return requestPermission(reqPerm);
    }



    private boolean requestPermission(List<String> perm)
    {

        String[] listReq=new String[perm.size()];
        listReq=perm.toArray(listReq);
        for(String permissions:listReq) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissions)) {
                Toast.makeText(getApplicationContext(), "Phone Permissions needed for " + permissions, Toast.LENGTH_LONG);
            }
        }
        ActivityCompat.requestPermissions(MainActivity.this, listReq, 1);
        return false;
    }


    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults)
    {
        switch(requestCode)
        {
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getApplicationContext(),"Permission Granted to access Phone calls",Toast.LENGTH_LONG);
                else
                    Toast.makeText(getApplicationContext(),"You can't access Phone calls",Toast.LENGTH_LONG);
                break;
        }

    }
}
