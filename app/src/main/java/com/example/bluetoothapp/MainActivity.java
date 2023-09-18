package com.example.bluetoothapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_SCAN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.le.BluetoothLeScanner;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.bluetoothapp.adapter.BlutoothDeviceAdapter;
import com.example.bluetoothapp.model.BlutoothDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;

    private BluetoothLeScanner bluetoothLeScanner;

    private BlutoothDeviceAdapter adapter;

    private RecyclerView recyclerView;
    private boolean scanning;
    private Handler handler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void initializeVariables() {

        handler = new Handler();
        recyclerView = findViewById(R.id.recycle_nearby_div);
        adapter = new BlutoothDeviceAdapter(this);
    }

    private void init() {
        initializeVariables();
        checkPermission();
        setUpRecyclerView();
        setUpData(bleDevices);


    }


    List<BlutoothDevice> bleDevices = new ArrayList<>(Arrays.asList(
            new BlutoothDevice("Device 1", -50, new byte[]{0x01, 0x02, 0x03},"Room"),
            new BlutoothDevice("Device 2", -60,new byte[]{0x01, 0x02, 0x03}, "Room"),
            new BlutoothDevice("Device 3",-70,new byte[]{0x01, 0x02, 0x03}, "Room")
    ));

    private void setUpRecyclerView() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setUpData(List<BlutoothDevice> articles) {

        adapter.setDataList(articles);
    }


    private void checkPermission() {

        // Check if you have already been granted these permissions.
        if (checkPermissions()) {
            // Permissions are already granted. You can proceed with your Bluetooth operations.
        } else {
            // Permissions are not granted. Request them.
            requestPermissions();
        }


    }


    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH_SCAN, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, BLUETOOTH_ADMIN}, PERMISSION_REQUEST_CODE);
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
}

