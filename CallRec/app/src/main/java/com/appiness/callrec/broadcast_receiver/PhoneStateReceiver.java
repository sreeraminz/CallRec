package com.appiness.callrec.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.appiness.callrec.Database.DatabaseManager;
import com.appiness.callrec.services.RecordingService;
import com.appiness.callrec.services.UploadingService;
import com.appiness.callrec.utilities.CallDetails;
import com.appiness.callrec.utilities.CommonMethods;

import java.time.Duration;
import java.util.List;

public class PhoneStateReceiver  extends BroadcastReceiver {

    static Boolean recordStarted;
    public static String phoneNumber;
    public static String name;
    static long start_time, end_time;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        Boolean switchCheckOn = pref.getBoolean("switchOn", true);
        if (switchCheckOn) {
            try {
                System.out.println("Receiver Start");
                Bundle extras = intent.getExtras();
                String state = extras.getString(TelephonyManager.EXTRA_STATE);
                Toast.makeText(context, "Call detected(Incoming/Outgoing) " + state, Toast.LENGTH_SHORT).show();
                if (extras != null) {
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);


                        }
                        else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                        start_time = System.currentTimeMillis();

                        int j = pref.getInt("numOfCalls", 0);
                        pref.edit().putInt("numOfCalls", ++j).apply();

                        if (pref.getInt("numOfCalls", 1) == 1) {
                            Intent reivToServ = new Intent(context, RecordingService.class);
                            reivToServ.putExtra("number", phoneNumber);
                            context.startService(reivToServ);

                            Intent intentnumber = new Intent(context,UploadingService.class);
                            intentnumber.putExtra("datanumber", phoneNumber);
                            context.startService(intentnumber);

                            int serialNumber = pref.getInt("serialNumData", 1);
                            new DatabaseManager(context).addCallDetails(new CallDetails(serialNumber, phoneNumber, new CommonMethods().getTIme(), new CommonMethods().getDate()));

                            List<CallDetails> list = new DatabaseManager(context).getAllDetails();
                            for (CallDetails cd : list) {
                                String log = "Serial Number : " + cd.getSerial() + " | Phone num : " + cd.getNum() + " | Time : " + cd.getTime1() + " | Date : " + cd.getDate1();
                            }

                            pref.edit().putInt("serialNumData", ++serialNumber).apply();
                            pref.edit().putBoolean("recordStarted", true).apply();
                        }

                    } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        int k = pref.getInt("numOfCalls", 1);
                        pref.edit().putInt("numOfCalls", --k).apply();
                        int l = pref.getInt("numOfCalls", 0);

                        end_time = System.currentTimeMillis();
                        long total_time = end_time - start_time;


                        String totalTime = Long.toString(total_time);
                        pref.edit().putLong("duration", total_time);

                        Log.d("Tag","DURATION REC==" +end_time);

                        recordStarted = pref.getBoolean("recordStarted", false);
                        if (recordStarted && l == 0) {
                            context.stopService(new Intent(context, RecordingService.class));
                            pref.edit().putBoolean("recordStarted", false).apply();
                        }
                        }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        }

}
