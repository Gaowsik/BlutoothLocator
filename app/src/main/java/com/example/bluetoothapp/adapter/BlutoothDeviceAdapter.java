package com.example.bluetoothapp.adapter;

import static com.example.bluetoothapp.Utils.getNameArea;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BlutoothDeviceAdapter extends RecyclerView.Adapter<BlutoothDeviceAdapter.BlutoothDeviceViewHolder> {


    private List<BluetoothDevice> dataList;
    private Context context;

    public interface AdapterOnClickListener<T> {
        void onItemSelected(T item);
    }

    private AdapterOnClickListener<BluetoothDeviceInfo> adapterItemTypeOnClickListener;
    private List<BluetoothDeviceInfo> deviceList = new ArrayList<>();


    public BlutoothDeviceAdapter(Context context, AdapterOnClickListener<BluetoothDeviceInfo> adapterItemTypeOnClickListener) {
        this.context = context;
        dataList = new ArrayList<>();
        this.adapterItemTypeOnClickListener = adapterItemTypeOnClickListener;

    }

    public void setDataList(BluetoothDevice dataList) {
        this.dataList.add(dataList);
        notifyDataSetChanged();
    }

    public void refresh() {
        this.dataList.clear();
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
    public void updateAverageRssi(String deviceAddress, int averageRssi) {
        for (BluetoothDeviceInfo deviceInfo : deviceList) {
            if (deviceInfo.getDevice().getDeviceName().equals(deviceAddress)) {
                deviceInfo.setRssi(averageRssi);
                sortDeviceListByRssiDescendingWithZeroAtEnd();
                notifyDataSetChanged();
                break;
            }
        }

        if (!deviceList.isEmpty()) {
            adapterItemTypeOnClickListener.onItemSelected(getDeviceWithHighestRssi(deviceList));
        }
    }

    @SuppressLint("MissingPermission")
    public boolean containsDevice(String deviceAddress) {
        for (BluetoothDevice deviceInfo : dataList) {
            if (deviceInfo.getName().equals(deviceAddress)) {
                return true;
            }
        }
        return false;
    }

    public void addDevice(List<BluetoothDeviceInfo> devices) {

/*        @SuppressLint("MissingPermission") String deviceName = device.getName();
        if (deviceName != null && (deviceName.equals("00000534") || deviceName.equals("00000523") || deviceName.equals("00000525"))) {
*//*            for (BluetoothDeviceInfo existingDevice : deviceList) {
                if (existingDevice.getDevice().getAddress().equals(device.getAddress())) {
                    // Update the existing device's RSSI value and notify the change
                    existingDevice.setRssi(rssi);
                    notifyDataSetChanged();
                    return;
                }
            }*//*
            List<DeviceInfoModel> listdata = new ArrayList<>(Arrays.asList(new DeviceInfoModel(534, 00000534, "00000534-91D8-4115-BB09-F76BAF6A0E7F", "Office room"), new DeviceInfoModel(523, 00000523, "00000523-91D8-4115-BB09-F76BAF6A0E7F", "reception"), new DeviceInfoModel(525, 00000525, "00000525-91D8-4115-BB09-F76BAF6A0E7F", "Lunch room")));
            // If the device is not in the list, add it as a new entry*/

            deviceList.addAll(devices);
            notifyDataSetChanged();
        }

    // Custom method to remove an object from the data source
    public void removeDeviceByName(String deviceNameToRemove) {
        for (BluetoothDeviceInfo deviceInfo : deviceList) {
            if (deviceInfo.getDevice().getDeviceName().equals(deviceNameToRemove)) {
                deviceList.remove(deviceInfo);
                notifyDataSetChanged();
                return; // Stop iterating since we found and removed the device
            }
        }
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


        if (currentItem.getDevice().getDeviceName() == null) {
            holder.textName.setText("Unknown");
        } else {
            holder.textName.setText(currentItem.getDevice().getDeviceName());
        }


            holder.textUUID.setText("Unknown");

        holder.textRssi.setText(Integer.toString(currentItem.getRssi()));
        holder.textArea.setText(getNameArea(currentItem));


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
            if (deviceInfo.getRssi() > highestRssiDevice.getRssi() && deviceInfo.getRssi()!=0) {
                highestRssiDevice = deviceInfo; // Update the device with the highest RSSI value
            }
        }

        return highestRssiDevice;
    }

    public void sortDeviceListByRssi() {
        Collections.sort(deviceList, new Comparator<BluetoothDeviceInfo>() {
            @Override
            public int compare(BluetoothDeviceInfo deviceInfo1, BluetoothDeviceInfo deviceInfo2) {
                // Compare based on rssi values
                return Integer.compare( deviceInfo2.getRssi(),deviceInfo1.getRssi());
            }
        });

        notifyDataSetChanged();
    }

    public void sortDeviceListByRssiDescendingWithZeroAtEnd() {
        Collections.sort(deviceList, new Comparator<BluetoothDeviceInfo>() {
            @Override
            public int compare(BluetoothDeviceInfo deviceInfo1, BluetoothDeviceInfo deviceInfo2) {
                int rssi1 = deviceInfo1.getRssi();
                int rssi2 = deviceInfo2.getRssi();

                if (rssi1 == 0 && rssi2 == 0) {
                    // If both have an rssi value of 0, consider them equal
                    return 0;
                } else if (rssi1 == 0) {
                    // If deviceInfo1 has an rssi value of 0, it should appear later
                    return 1;
                } else if (rssi2 == 0) {
                    // If deviceInfo2 has an rssi value of 0, it should appear later
                    return -1;
                } else {
                    // Compare based on rssi values in descending order
                    return Integer.compare(rssi2, rssi1);
                }
            }
        });

        notifyDataSetChanged();
    }


}
