package com.example.bluetoothapp;

import com.example.bluetoothapp.model.DeviceInfoModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contants {

    public List<DeviceInfoModel> getMainDevies() {
        return new ArrayList<>(Arrays.asList(new DeviceInfoModel(534, 00000534, "00000534-91D8-4115-BB09-F76BAF6A0E7F", "Office room"), new DeviceInfoModel(523, 00000523, "00000523-91D8-4115-BB09-F76BAF6A0E7F", "reception"), new DeviceInfoModel(525, 00000525, "00000525-91D8-4115-BB09-F76BAF6A0E7F", "Lunch room")));
    }
}


