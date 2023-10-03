package com.example.bluetoothapp.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.bluetoothapp.R;
import com.example.bluetoothapp.model.RssiValueHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;

public class BLEScanService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    private BluetoothLeScanner bluetoothLeScanner;
    private Map<String, RssiValueHolder> rssiValuesMap = new HashMap<>();
    private List<String> currenReadingMap = new ArrayList<>();

    BluetoothAdapter bluetoothAdapter;
    private PowerManager.WakeLock wakeLock;


    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {


            BluetoothDevice device = result.getDevice();
            if (device.getName()!=null) {
                Log.d("BackgroundService", device.getName());
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        createNotificationChannel();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BLEScanService::WakeLockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification()); // Start as a foreground service
        startScanning();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScanning();
        // Release the wake lock
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    private void startScanning() {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.startScan(scanCallback);
        } else {
            Toast.makeText(this, "BLE scanning not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void stopScanning() {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("BLE Scanning Service")
                .setContentText("Scanning for BLE devices...")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
    }
}
