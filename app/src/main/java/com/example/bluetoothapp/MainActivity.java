package com.example.bluetoothapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

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
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothapp.adapter.BlutoothDeviceAdapter;
import com.example.bluetoothapp.model.BluetoothDeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BlutoothDeviceAdapter.AdapterOnClickListener<BluetoothDeviceInfo> {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int REQUEST_ENABLE_BT = 1;
    private BlutoothDeviceAdapter adapter;
    private BluetoothAdapter bluetoothAdapter;

    private RecyclerView recyclerView;

    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothLeScanner bluetoothLeScanner;

    private TextView textNearByDevice;

    private boolean scanning;


    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;


    Handler handler = new Handler();
    Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            // Refresh your data source and notify the adapter
            adapter.refresh();

            // Schedule the next refresh after 10 seconds
            handler.postDelayed(this, 10000);

        }
    };


    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            int rssi = result.getRssi(); //
            if (!deviceList.contains(device)) {
                //     UUID uuid = uuids[0].getUuid();
                adapter.addDevice(device, rssi, "unaccesible");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        // Start the periodic refresh
        handler.postDelayed(refreshRunnable, 10000);


    }

    private void initializeVariables() {
        recyclerView = findViewById(R.id.recycle_nearby_div);
        adapter = new BlutoothDeviceAdapter(this, this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        textNearByDevice = findViewById(R.id.text_nearby_devices);
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
            startScanning();

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
        textNearByDevice.setText("you are near to the " + item.getDevice().getName() + "device");
    }
}

