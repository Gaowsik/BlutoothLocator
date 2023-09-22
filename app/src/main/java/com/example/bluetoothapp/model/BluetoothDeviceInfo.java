package com.example.bluetoothapp.model;

public class BluetoothDeviceInfo {
    private DeviceInfoModel device;
    private int rssi;

    public BluetoothDeviceInfo(DeviceInfoModel device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public DeviceInfoModel getDevice() {
        return device;
    }
    public int getRssi() {
        return rssi;
    }

}