package com.example.bluetoothapp.model;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceInfo {
    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    private BluetoothDevice device;
    private int rssi;

    private String uuid;

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