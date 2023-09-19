package com.example.bluetoothapp.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetoothapp.R;
import com.example.bluetoothapp.model.BluetoothDeviceInfo;
import com.example.bluetoothapp.model.DeviceInfoModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlutoothDeviceAdapter extends RecyclerView.Adapter<BlutoothDeviceAdapter.BlutoothDeviceViewHolder> {


    private List<BluetoothDevice> dataList;
    private Context context;

    public interface AdapterOnClickListener<T> {
        void onItemSelected(T item);}
    private AdapterOnClickListener<BluetoothDeviceInfo> adapterItemTypeOnClickListener;
    private List<BluetoothDeviceInfo> deviceList = new ArrayList<>();


    public BlutoothDeviceAdapter(Context context,AdapterOnClickListener<BluetoothDeviceInfo> adapterItemTypeOnClickListener) {
        this.context = context;
        dataList = new ArrayList<>();
        this.adapterItemTypeOnClickListener = adapterItemTypeOnClickListener;

    }

    public void setDataList(List<BluetoothDevice> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void refresh() {
        this.dataList.clear();
        notifyDataSetChanged();
    }

    class BlutoothDeviceViewHolder extends RecyclerView.ViewHolder {


        TextView textName;
        TextView textRssi;
        TextView textArea;

        TextView textUUID;

        BlutoothDeviceViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.text_name);
            textRssi = itemView.findViewById(R.id.text_rssi);
            textArea = itemView.findViewById(R.id.text_area);
            textUUID = itemView.findViewById(R.id.text_uuid);
        }
    }


    public void addDevice(BluetoothDevice device, int rssi, String uuid) {

        @SuppressLint("MissingPermission") String deviceName = device.getName();
        if (deviceName != null && (deviceName.equals("00000534") || deviceName.equals("00000523") || deviceName.equals("00000525"))) {
            for (BluetoothDeviceInfo existingDevice : deviceList) {
                if (existingDevice.getDevice().getAddress().equals(device.getAddress())) {
                    // Update the existing device's RSSI value and notify the change
                    existingDevice.setRssi(rssi);
                    notifyDataSetChanged();
                    return;
                }
            }
            List<DeviceInfoModel> listdata = new ArrayList<>(Arrays.asList(new DeviceInfoModel(534, 00000534, "00000534-91D8-4115-BB09-F76BAF6A0E7F", "Office room"), new DeviceInfoModel(523, 00000523, "00000523-91D8-4115-BB09-F76BAF6A0E7F", "reception"), new DeviceInfoModel(525, 00000525, "00000525-91D8-4115-BB09-F76BAF6A0E7F", "Lunch room")));
            // If the device is not in the list, add it as a new entry
            BluetoothDeviceInfo deviceInfo = new BluetoothDeviceInfo(device, rssi, uuid);
            deviceList.add(deviceInfo);
            notifyDataSetChanged();
        }

        adapterItemTypeOnClickListener.onItemSelected(getDeviceWithHighestRssi(deviceList));
    }


    @Override
    public BlutoothDeviceAdapter.BlutoothDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_blutooth_device, parent, false);
        return new BlutoothDeviceAdapter.BlutoothDeviceViewHolder(view);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull BlutoothDeviceViewHolder holder, int position) {

        final BluetoothDeviceInfo currentItem = (BluetoothDeviceInfo) deviceList.get(position);


        if (currentItem.getDevice().getName() == null) {
            holder.textName.setText("Unknown");
        } else {
            holder.textName.setText(currentItem.getDevice().getName());
        }

        if (currentItem.getUuid() == null) {
            holder.textUUID.setText("Unknown");
        } else {
            holder.textUUID.setText(currentItem.getUuid());
        }


        holder.textRssi.setText(Integer.toString(currentItem.getRssi()));
        holder.textArea.setText("Rooms");

    }


    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public BluetoothDeviceInfo getDeviceWithHighestRssi(List<BluetoothDeviceInfo> deviceList) {
        if (deviceList.isEmpty()) {
            return null; // Handle the case when the list is empty
        }

        BluetoothDeviceInfo highestRssiDevice = deviceList.get(0); // Initialize with the first device in the list

        for (BluetoothDeviceInfo deviceInfo : deviceList) {
            if (deviceInfo.getRssi() > highestRssiDevice.getRssi()) {
                highestRssiDevice = deviceInfo; // Update the device with the highest RSSI value
            }
        }

        return highestRssiDevice;
    }


}
