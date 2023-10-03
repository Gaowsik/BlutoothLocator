package com.example.bluetoothapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import static com.example.bluetoothapp.Utils.getDevices;
import static com.example.bluetoothapp.Utils.getNameArea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothapp.adapter.BlutoothDeviceAdapter;
import com.example.bluetoothapp.model.BluetoothDeviceInfo;
import com.example.bluetoothapp.model.RssiValueHolder;
import com.example.bluetoothapp.service.BLEScanService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements BlutoothDeviceAdapter.AdapterOnClickListener<BluetoothDeviceInfo> {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int REQUEST_ENABLE_BT = 1;
    private BlutoothDeviceAdapter adapter;
    private BluetoothAdapter bluetoothAdapter;

    private RecyclerView recyclerView;

    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothLeScanner bluetoothLeScanner;

    private TextView textNearByDevice;

    private Map<String, RssiValueHolder> rssiValuesMap;

    private List<String> currenReadingMap;

    private Map<String, Integer> missedValuesMap;

    private final long SCAN_INTERVAL = 5000; // 5 seconds
    private final long AVERAGE_INTERVAL = 30000; // 30 second


    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;


    Handler handler = new Handler();
    Handler averageResultHandler = new Handler();
    Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            // Refresh your data source and notify the adapter
            adapter.refresh();

            for (BluetoothDeviceInfo deviceInfo : getDevices()) {
                String deviceName = deviceInfo.getDevice().getDeviceName();
                // Check if the device name exists as a key in rssiValuesMap
                if (currenReadingMap.contains(deviceName)) {
                    // Device name exists, reset missCount to 0
                    RssiValueHolder rssiValueHolder = rssiValuesMap.get(deviceName);
                    //    rssiValueHolder.setMissCount(0);
                    missedValuesMap.put(deviceName, 0);

                } else {

                    Integer currentValue = missedValuesMap.get(deviceName);
                    if (currentValue != null) {
                        missedValuesMap.put(deviceName, currentValue + 1);
                    }
                    Log.d("MainActi", "miss"+deviceName+" count "+missedValuesMap.get(deviceName));
                    if (currentValue ==4)
                    {
                        adapter.removeDeviceByName(deviceName);
                        if (deviceInfo!=null){

                        }
                    }


                }
            }

            currenReadingMap.clear();
            Log.d("MainActi", "refresh list");

            // Schedule the next refresh after 10 seconds
            handler.postDelayed(this, SCAN_INTERVAL);

        }
    };

    Runnable averageResultRunnable = new Runnable() {
        @Override
        public void run() {
            // Refresh your data source and notify the adapter
            //calculateAndDisplayAverages();

            // Schedule the next refresh after 10 seconds
            averageResultHandler.postDelayed(this, AVERAGE_INTERVAL);

        }
    };


    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getName();
            currenReadingMap.add(device.getName());
            int rssi = result.getRssi(); //
            // Add the RSSI value to the list associated with this device
            RssiValueHolder rssiValueHolder = rssiValuesMap.get(deviceAddress);
            List<Integer> rssiValues = null;
            if (rssiValueHolder != null) {
                rssiValues = rssiValueHolder.getRssiValues();
            }
            if (rssiValues == null) {
                rssiValues = new ArrayList<>();
                // rssiValuesMap.put(deviceAddress, rssiValues);

            }

            if (!adapter.containsDevice(deviceAddress) && (device.getName() != null)) {
                if (device.getName().equals("00000534") || device.getName().equals("00000523") || device.getName().equals("00000525")) {
                    rssiValues.add(rssi);
                    // Check if the list size is at its maximum (6)
                    if (rssiValues.size() >
                            6) {
                        // Remove the last value (the sixth in the list)
                        rssiValues.remove(rssiValues.size() - 1);
                    }

                    // Move all existing values one position to the right
                    for (int i = rssiValues.size() - 1; i >= 0; i--) {
                        if (i == 0) {
                            // Set the first position with the new RSSI value
                            rssiValues.set(i, rssi);
                        } else {
                            // Move the previous value to the next position
                            rssiValues.set(i, rssiValues.get(i - 1));
                        }
                    }
                    adapter.setDataList(device);
                    RssiValueHolder rssiValueViewholder = rssiValuesMap.get(deviceAddress);

                    if (rssiValueViewholder == null) {
                        rssiValueViewholder = new RssiValueHolder();
                        rssiValuesMap.put(deviceAddress, rssiValueViewholder);
                    }

                    rssiValueViewholder.setRssiValues(rssiValues);
                    //  rssiValuesMap.put(deviceAddress, rssiValueViewholder);
                    calculateAndDisplayAverages();
                    Log.d("MainActi", "value is added to the map " + deviceAddress + ": " + rssiValues);
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        // Start the periodic refresh


    }

    private void initializeVariables() {
        recyclerView = findViewById(R.id.recycle_nearby_div);
        currenReadingMap = new ArrayList<>();
        adapter = new BlutoothDeviceAdapter(this, this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        textNearByDevice = findViewById(R.id.text_nearby_devices);
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        rssiValuesMap = new HashMap<>();
        missedValuesMap = new HashMap<>();
        missedValuesMap.put("00000534", 0);
        missedValuesMap.put("00000523", 0);
        missedValuesMap.put("00000525", 0);
        missedValuesMap.put("00000545", 0);
    }

    private void init() {
        initializeVariables();
        checkPermission();
        setUpRecyclerView();
        setUpDataToRecyclerview();
        startScanning();
        startBLEScanService();


    }


    // List<BlutoothDevice> bleDevices = new ArrayList<>(Arrays.asList(new BlutoothDevice("Device 1", -50, new byte[]{0x01, 0x02, 0x03}, "Room"), new BlutoothDevice("Device 2", -60, new byte[]{0x01, 0x02, 0x03}, "Room"), new BlutoothDevice("Device 3", -70, new byte[]{0x01, 0x02, 0x03}, "Room")));

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setUpDataToRecyclerview() {

        adapter.addDevice(getDevices());
    }


    private void checkPermission() {

        // Check if you have already been granted these permissions.
        if (checkPermissions()) {
            //startScanning();

            // Start scanning for Bluetooth devices periodically
            // Start calculating average RSSI values periodically
            startScanning();
            handler.postDelayed(refreshRunnable, SCAN_INTERVAL);
            handler.postDelayed(averageResultRunnable, AVERAGE_INTERVAL);
            // startAveragingPeriodically();
        } else {
            // Permissions are not granted. Request them.
            requestPermissions();
        }


    }


    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH_SCAN, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, BLUETOOTH_ADMIN, BLUETOOTH_CONNECT,POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
    }

    // Handle the result of the permission request.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    // The permission was denied for permission[i].
                    allPermissionsGranted = false;
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(this, "Permissions are granted succussfully", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(this, "All the permissions should be granted in order to use this app", Toast.LENGTH_SHORT).show();
            }
        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
        }

        Intent serviceIntent = new Intent(this, BLEScanService.class);
        stopService(serviceIntent);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onItemSelected(BluetoothDeviceInfo item) {
        textNearByDevice.setText("you are at the " + getNameArea(item));

    }

    private void calculateAndDisplayAverages() {
        for (Map.Entry<String, RssiValueHolder> entry : rssiValuesMap.entrySet()) {
            String deviceAddress = entry.getKey();
            RssiValueHolder rssiValueHolderValue = entry.getValue();
            List<Integer> rssiValues = rssiValueHolderValue.getRssiValues();
            Log.d("MainActi", "Average values" + deviceAddress + " countRssi " + rssiValueHolderValue.getCount());
            if (rssiValueHolderValue.getCount() == 6) {
                int sum = 0;
                for (int rssi : rssiValues) {
                    sum += rssi;
                }

                int averageRssi = sum / rssiValues.size();
                adapter.updateAverageRssi(deviceAddress, averageRssi);
                Log.d("MainActi", "Average values" + deviceAddress + " avgRssi" + averageRssi);
                rssiValueHolderValue.setCount(0);
            }
        }
    }


    private void startBLEScanService() {
        Intent serviceIntent = new Intent(this, BLEScanService.class);
        startService(serviceIntent);
    }


}

