package com.example.bluetoothapp.model;

public class DeviceInfoModel {
    int kfo;
    int deviceName;
    String uuid;
    String area;

    public DeviceInfoModel(int kfo, int deviceName, String uuid, String area) {
        this.kfo = kfo;
        this.deviceName = deviceName;
        this.uuid = uuid;
        this.area = area;
    }

    public int getKfo() {
        return kfo;
    }

    public void setKfo(int kfo) {
        this.kfo = kfo;
    }

    public int getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(int deviceName) {
        this.deviceName = deviceName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
