package com.example.bluetoothapp.model;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceInfo {
    private BluetoothDevice device;
    private int rssi;

    public BluetoothDeviceInfo(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public int getRssi() {
        return rssi;
    }

}