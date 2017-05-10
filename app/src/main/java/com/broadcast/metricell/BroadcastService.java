package com.broadcast.metricell;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.*;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class BroadcastService  extends Service {
    myPhoneStateListener psListener;
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.broadcast.metricell";
    private final Handler handler = new Handler();
    Intent intent;
    public int signalStrengthValue = 0;
    public String locString=null;
    public String sigState=null;
    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        psListener = new myPhoneStateListener();
        //retrieve a reference to an instance of TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(psListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_SERVICE_STATE
                | PhoneStateListener.LISTEN_CELL_LOCATION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 1 second
        }
    };

    private void DisplayLoggingInfo() {

        intent.putExtra("signalStrength",String.valueOf(signalStrengthValue) );
        intent.putExtra("locString", locString);
        intent.putExtra("sigState",sigState);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
    }

    public class myPhoneStateListener extends PhoneStateListener {


        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.isGsm()) {
                if (signalStrength.getGsmSignalStrength() != 99)
                    signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
                else
                    signalStrengthValue = signalStrength.getGsmSignalStrength();
            } else {
                signalStrengthValue = signalStrength.getCdmaDbm();
            }
            appendLog(String.valueOf(signalStrengthValue));

        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);

            switch (serviceState.getState()) {
                case ServiceState.STATE_IN_SERVICE:

                    Log.d(TAG,"STATE_IN_SERVICE");
                    sigState="STATE_IN_SERVICE";
                    appendLog(sigState);
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:

                    Log.d(TAG,"STATE_OUT_OF_SERVICE");
                    sigState="STATE_OUT_OF_SERVICE";
                    appendLog(sigState);
                    break;
                case ServiceState.STATE_EMERGENCY_ONLY:

                    Log.d(TAG,"STATE_EMERGENCY_ONLY");
                    sigState="STATE_EMERGENCY_ONLY";
                    appendLog(sigState);
                    break;
                case ServiceState.STATE_POWER_OFF:

                    Log.d(TAG,"STATE_POWER_OFF");
                    sigState="STATE_POWER_OFF";
                    appendLog(sigState);
                    break;
            }
        }


        @Override
        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);
            if (location instanceof GsmCellLocation) {
                GsmCellLocation gcLoc = (GsmCellLocation) location;
                Log.d(TAG,gcLoc.toString());
                locString=gcLoc.toString();
                appendLog(locString);

            } else if (location instanceof CdmaCellLocation) {
                CdmaCellLocation ccLoc = (CdmaCellLocation) location;
                Log.d(TAG,ccLoc.toString());
                locString=ccLoc.toString();
                appendLog(locString);

            } else {

                locString=location.toString();
                appendLog(locString);
            }
        }

    }


    public void appendLog(String text)
    {
        File logFile = new File("sdcard/log.file");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {

                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }
    }



}
