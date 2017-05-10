package com.broadcast.metricell;

import android.support.v7.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

        intent = new Intent(this, BroadcastService.class);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void updateUI(Intent intent) {
        String location = intent.getStringExtra("locString");
        String strength = intent.getStringExtra("signalStrength");
        String state=intent.getStringExtra("sigState");

        TextView txtStrength = (TextView) findViewById(R.id.txtStrength);
        TextView txtLocation = (TextView) findViewById(R.id.txtLocation);
        TextView txtState = (TextView) findViewById(R.id.txtState);

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