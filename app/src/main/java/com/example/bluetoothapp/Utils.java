package com.example.bluetoothapp;

import android.annotation.SuppressLint;

import com.example.bluetoothapp.model.BluetoothDeviceInfo;
import com.example.bluetoothapp.model.DeviceInfoModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static String getNameArea(BluetoothDeviceInfo deviceInfo) {
        @SuppressLint("MissingPermission") String deviceName = deviceInfo.getDevice().getDeviceName();
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

    public static List<BluetoothDeviceInfo> getDevices() {


        return new ArrayList<>(Arrays.asList(new BluetoothDeviceInfo(new DeviceInfoModel(534, "00000534", "00000534-91D8-4115-BB09-F76BAF6A0E7F", "Office room"),0),new BluetoothDeviceInfo(new DeviceInfoModel(523, "00000523", "00000523-91D8-4115-BB09-F76BAF6A0E7F", "reception"),0), new BluetoothDeviceInfo(new DeviceInfoModel(525, "00000525", "00000525-91D8-4115-BB09-F76BAF6A0E7F", "Lunch room"),0)));
    }


}
