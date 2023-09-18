package com.example.bluetoothapp.model;

public class BlutoothDevice {

    private String name;
    private String address;
    private int rssi;
    private byte[] scanRecord;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRssi() {
        return String.valueOf(rssi);
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public BlutoothDevice(String name, int rssi, byte[] scanRecord, String address) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

}
