package com.appiness.callrec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.util.List;

public class PhoneStateReceiver  extends BroadcastReceiver {

    static final String TAG="State";
    static final String TAG1=" Inside State";
    static Boolean recordStarted;
    public static String phoneNumber;
    public static String name;

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
                        }
                        else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        int j = pref.getInt("numOfCalls", 0);
                        pref.edit().putInt("numOfCalls", ++j).apply();
                        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        if (pref.getInt("numOfCalls", 1) == 1) {
                            Intent reivToServ = new Intent(context, RecordingService.class);
                            reivToServ.putExtra("number", phoneNumber);
                            context.startService(reivToServ);

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
