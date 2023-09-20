package com.example.bluetoothapp;

import android.annotation.SuppressLint;

import com.example.bluetoothapp.model.BluetoothDeviceInfo;

public class Utils {

    public static String getNameArea(BluetoothDeviceInfo deviceInfo) {
        @SuppressLint("MissingPermission")
        String deviceName = deviceInfo.getDevice().getName();
        String area;
        switch (deviceName) {
            case "00000534":
                area = "Office room";
                break;
            case "00000523":
                area = "Reception";
                break;
            case "00000525":
                area = "Lunch room";
                break;
            default:
                area = "Not mentioned"; // Change this to your desired default text
                break;
        }

        return area;


    }


}
