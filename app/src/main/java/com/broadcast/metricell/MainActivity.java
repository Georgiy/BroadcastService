package com.broadcast.metricell;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.broadcast.service.R;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start_button = (findViewById(R.id.start_butt));
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                        runBroadcast();
                        startService(intent);
                        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    runBroadcast();
                    startService(intent);
                    registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
                    // Permission has already been granted
                }


                //runBroadcast();
                // do something when the corky2 is clicked
            }
        });



    }

    private void runBroadcast() {
        intent = new Intent(this, BroadcastService.class);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

/*    @Override
    public void onResume() {
        super.onResume();
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
    }*/

    @Override
    public void onPause() {
        super.onPause();

    }

    private void updateUI(Intent intent) {
        String location = intent.getStringExtra("locString");
        String strength = intent.getStringExtra("signalStrength");
        String state=intent.getStringExtra("sigState");

        TextView txtStrength = findViewById(R.id.txtStrength);
        TextView txtLocation = findViewById(R.id.txtLocation);
        TextView txtState = findViewById(R.id.txtState);

        txtStrength.setText(strength);
        txtLocation.setText(location);
        txtState.setText(state);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
        overridePendingTransition(0, 0);
    }

}