package com.example.bluetoothapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothapp.adapter.BlutoothDeviceAdapter;
import com.example.bluetoothapp.model.BluetoothDeviceInfo;

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

    private Map<String, List<Integer>> rssiValuesMap;

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
            Log.d("MainActi", "refresh list");

            // Schedule the next refresh after 10 seconds
            handler.postDelayed(this, SCAN_INTERVAL);

        }
    };

    Runnable averageResultRunnable = new Runnable() {
        @Override
        public void run() {
            // Refresh your data source and notify the adapter
            calculateAndDisplayAverages();

            // Schedule the next refresh after 10 seconds
            averageResultHandler.postDelayed(this, AVERAGE_INTERVAL);

        }
    };


    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            int rssi = result.getRssi(); //
            // Add the RSSI value to the list associated with this device
            List<Integer> rssiValues = rssiValuesMap.get(deviceAddress);
            if (rssiValues == null) {
                rssiValues = new ArrayList<>();
                // rssiValuesMap.put(deviceAddress, rssiValues);

            }

            if (!adapter.containsDevice(deviceAddress) && (device.getName() != null)) {
                if (device.getName().equals("00000534") || device.getName().equals("00000523") || device.getName().equals("00000525")) {
                    rssiValues.add(rssi);
                    adapter.addDevice(device, rssi, "unaccessible");
                    rssiValuesMap.put(deviceAddress, rssiValues);
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
        adapter = new BlutoothDeviceAdapter(this, this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        textNearByDevice = findViewById(R.id.text_nearby_devices);
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        rssiValuesMap = new HashMap<>();
    }

    private void init() {
        initializeVariables();
        checkPermission();
        setUpRecyclerView();
        //  setUpData(deviceList);


    }


    // List<BlutoothDevice> bleDevices = new ArrayList<>(Arrays.asList(new BlutoothDevice("Device 1", -50, new byte[]{0x01, 0x02, 0x03}, "Room"), new BlutoothDevice("Device 2", -60, new byte[]{0x01, 0x02, 0x03}, "Room"), new BlutoothDevice("Device 3", -70, new byte[]{0x01, 0x02, 0x03}, "Room")));

    private void setUpRecyclerView() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setUpData(List<BluetoothDevice> articles, int rssi) {


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
        ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH_SCAN, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, BLUETOOTH_ADMIN, BLUETOOTH_CONNECT}, PERMISSION_REQUEST_CODE);
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

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onItemSelected(BluetoothDeviceInfo item) {
        textNearByDevice.setText("you are at the " + getNameArea(item));

    }

    private void calculateAndDisplayAverages() {
        for (Map.Entry<String, List<Integer>> entry : rssiValuesMap.entrySet()) {
            String deviceAddress = entry.getKey();
            List<Integer> rssiValues = entry.getValue();
            Log.d("MainActi", "Average values" + deviceAddress + " countRssi" + rssiValues.size());
            if (!rssiValues.isEmpty()) {
                int sum = 0;
                for (int rssi : rssiValues) {
                    sum += rssi;
                }

                int averageRssi = sum / rssiValues.size();
                adapter.updateAverageRssi(deviceAddress, averageRssi);
                Log.d("MainActi", "Average values" + deviceAddress + " avgRssi" + averageRssi);
            }
        }
        rssiValuesMap.clear();
    }


}

